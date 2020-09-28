package com.example.gateway.gatling

import java.util.{Map => JMap}

import com.example.gateway.gatling.wmq.ConnectionFactoryDecorator
import com.ibm.mq.jms.MQDestination
import com.ibm.msg.client.jms.{JmsConstants, JmsFactoryFactory}
import com.ibm.msg.client.wmq.common.CommonConstants
import io.gatling.commons.validation._
import io.gatling.core.Predef.{reachRps, _}
import io.gatling.core.check.CheckResult
import io.gatling.core.session.Session
import io.gatling.jms.JmsCheck
import io.gatling.jms.Predef._
import javax.jms._

import scala.concurrent.duration._

class MqToHttpSimulation extends Simulation {
  private val hostName = getStringConfig("MQ_QM_HOSTNAME", "localhost")
  private val hostPort = getIntConfig("MQ_QM_PORT", 1414)
  private val channelName = getStringConfig("MQ_QM_CHANNEL", "DEV.ADMIN.SVRCONN")
  private val queueManagerName = getStringConfig("MQ_QM_NAME", "GW")
  private val replyTimeoutMsecs = getIntConfig("REPLY_TIMEOUT_MSECS", 10000).toLong

  private val jmsFactoryFactory = JmsFactoryFactory.getInstance(JmsConstants.WMQ_PROVIDER)
  private val connectionFactory = createConnectionFactory

  private val jmsConfig = jms
    .connectionFactory(connectionFactory)
    .replyTimeout(replyTimeoutMsecs)
    .credentials("admin", "passw0rd")

  private val scn = scenario("MQ Simulation").repeat(1) {
    exec(
      jms("DEV.QUEUE.1").requestReply
        .queue("DEV.QUEUE.1")
        .replyQueue("DEV.QUEUE.2")
        .textMessage("Hello World!")
        .property(JmsConstants.DELIVERY_MODE, JmsConstants.DELIVERY_NOT_PERSISTENT)
        .check(MqReponsePrefixCheck)
    )
  }

  setUp(
    scn.inject(constantUsersPerSec(200) during (8 minutes))
  ).throttle(
    reachRps(25) in (30 seconds),
    holdFor(1 minute),
    reachRps(50) in (30 seconds),
    holdFor(1 minute),
    reachRps(75) in (30 seconds),
    holdFor(1 minute),
    reachRps(100) in (30 seconds),
    holdFor(1 minute),
  ).protocols(jmsConfig)

  private def createConnectionFactory = {
    val result = new ConnectionFactoryDecorator(jmsFactoryFactory.createConnectionFactory(),
      delegate => {
        val mqDestination = delegate.asInstanceOf[MQDestination]
        mqDestination.setBooleanProperty(CommonConstants.WMQ_MQMD_WRITE_ENABLED, true)
        mqDestination.setIntProperty(CommonConstants.WMQ_MQMD_MESSAGE_CONTEXT, CommonConstants.WMQ_MDCTX_SET_IDENTITY_CONTEXT)
        mqDestination.setMessageBodyStyle(CommonConstants.WMQ_MESSAGE_BODY_MQ)
      })

    result.getDelegate.setStringProperty(CommonConstants.WMQ_HOST_NAME, hostName)
    result.getDelegate.setIntProperty(CommonConstants.WMQ_PORT, hostPort)
    result.getDelegate.setStringProperty(CommonConstants.WMQ_CHANNEL, channelName)
    result.getDelegate.setIntProperty(CommonConstants.WMQ_CONNECTION_MODE, CommonConstants.WMQ_CM_CLIENT)
    result.getDelegate.setStringProperty(CommonConstants.WMQ_QUEUE_MANAGER, queueManagerName)

    result
  }

  private def getStringConfig(name: String, default: String): String = {
    val value = System.getProperty(name, System.getenv(name))
    value match {
      case s: String => s
      case _ => default
    }
  }

  private def getIntConfig(name: String, default: Int): Int = {
    val envDefault = System.getenv(name)
    val overlaidDefault = envDefault match {
      case s: String => s.toInt
      case _ => default
    }
    Integer.getInteger(name, overlaidDefault)
  }
}

object MqReponsePrefixCheck extends MqPayloadCheck((body, session) => {
  CheckResult.NoopCheckResultSuccess
})

case class MqPayloadCheck(func: (String, Session) => Validation[CheckResult]) extends JmsCheck {
  override def check(response: Message, session: Session, preparedCache: JMap[Any, Any]): Validation[CheckResult] = {
    val responseText: Option[String] = response match {
      case bm: BytesMessage => Option(messageBodyAsString(bm))
      case tm: TextMessage => Option(tm.getText)
      case _ => Option.empty
    }
    responseText.map(func(_, session)).getOrElse(s"Unexpected message type: ${response.getClass}".failure)
  }

  private def messageBodyAsString(message: BytesMessage) = {
    val bodyLength = message.getBodyLength.toInt
    val bodyBytes = new Array[Byte](bodyLength)
    message.readBytes(bodyBytes, bodyLength)
    val codePage = message.getStringProperty(JmsConstants.JMS_IBM_CHARACTER_SET)
    new String(bodyBytes, codePage)
  }
}
