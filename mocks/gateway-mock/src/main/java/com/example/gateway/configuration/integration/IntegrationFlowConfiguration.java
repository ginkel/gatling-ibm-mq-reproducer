package com.example.gateway.configuration.integration;

import javax.jms.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.jms.JmsInboundGateway;
import org.springframework.integration.jms.dsl.Jms;

@Configuration
public class IntegrationFlowConfiguration {

  @Bean
  public JmsInboundGateway mqInboundGateway(ConnectionFactory connectionFactory, IntegrationFlow mqToWebFlow) {
    return Jms
        .inboundGateway(Jms.container(connectionFactory, "DEV.QUEUE.1")
            .concurrentConsumers(10)
            .maxConcurrentConsumers(30)
            .get())
        .defaultReplyQueueName("DEV.QUEUE.1")
        .requestChannel(mqToWebFlow.getInputChannel())
        .get();
  }

  @Bean
  public IntegrationFlow integrationFlow() {
    return flow -> flow
        .log()
        .convert(String.class)
        .transform(String.class, String::toLowerCase)
        .logAndReply();
  }
}
