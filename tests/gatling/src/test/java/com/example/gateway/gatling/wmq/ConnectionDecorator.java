package com.example.gateway.gatling.wmq;

import java.util.function.Consumer;
import javax.jms.Connection;
import javax.jms.ConnectionConsumer;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.Topic;

/**
 * A decorator for {@link javax.jms.Connection} that allows created connections to be customized by a `Consumer<Destination>`.
 *
 * To be used via {@link ConnectionFactoryDecorator}.
  */
class ConnectionDecorator implements Connection {
  private final Connection delegate;

  private final Consumer<Destination> destinationConfigurer;

  public ConnectionDecorator(Connection delegate, Consumer<Destination> destinationConfigurer) {
    this.delegate = delegate;
    this.destinationConfigurer = destinationConfigurer;
  }

  public Session createSession(boolean transacted, int acknowledgeMode) throws JMSException {
    return new SessionDecorator(delegate.createSession(transacted, acknowledgeMode), destinationConfigurer);
  }

  public Session createSession(int sessionMode) throws JMSException {
    return new SessionDecorator(delegate.createSession(sessionMode), destinationConfigurer);
  }

  public Session createSession() throws JMSException {
    return new SessionDecorator(delegate.createSession(), destinationConfigurer);
  }

  public String getClientID() throws JMSException {
    return delegate.getClientID();
  }

  public void setClientID(String clientID) throws JMSException {
    delegate.setClientID(clientID);
  }

  public ConnectionMetaData getMetaData() throws JMSException {
    return delegate.getMetaData();
  }

  public ExceptionListener getExceptionListener() throws JMSException {
    return delegate.getExceptionListener();
  }

  public void setExceptionListener(ExceptionListener listener) throws JMSException {
    delegate.setExceptionListener(listener);
  }

  public void start() throws JMSException {
    delegate.start();
  }

  public void stop() throws JMSException {
    delegate.stop();
  }

  public void close() throws JMSException {
    delegate.close();
  }

  public ConnectionConsumer createConnectionConsumer(Destination destination, String messageSelector,
      ServerSessionPool sessionPool, int maxMessages) throws JMSException {
    return delegate.createConnectionConsumer(destination, messageSelector, sessionPool, maxMessages);
  }

  public ConnectionConsumer createSharedConnectionConsumer(Topic topic, String subscriptionName,
      String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException {
    return delegate.createSharedConnectionConsumer(topic, subscriptionName, messageSelector, sessionPool, maxMessages);
  }

  public ConnectionConsumer createDurableConnectionConsumer(Topic topic, String subscriptionName,
      String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException {
    return delegate.createDurableConnectionConsumer(topic, subscriptionName, messageSelector, sessionPool, maxMessages);
  }

  public ConnectionConsumer createSharedDurableConnectionConsumer(Topic topic, String subscriptionName,
      String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException {
    return delegate.createSharedDurableConnectionConsumer(topic, subscriptionName, messageSelector, sessionPool, maxMessages);
  }
}
