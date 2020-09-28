package com.example.gateway.gatling.wmq;

import java.util.function.Consumer;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSException;

/**
 * A decorator for {@link javax.jms.ConnectionFactory} that allows created connections to be customized by a `Consumer<Destination>`.
 *
 */
public class ConnectionFactoryDecorator<T extends ConnectionFactory> implements ConnectionFactory {

  private final T delegate;
  private final Consumer<Destination> destinationConfigurer;

  public ConnectionFactoryDecorator(T delegate, Consumer<Destination> destinationConfigurer) {
    this.delegate = delegate;
    this.destinationConfigurer = destinationConfigurer;
  }

  public Connection createConnection() throws JMSException {
    return new ConnectionDecorator(delegate.createConnection(), destinationConfigurer);
  }

  public Connection createConnection(String userName, String password) throws JMSException {
    return new ConnectionDecorator(delegate.createConnection(userName, password), destinationConfigurer);
  }

  public JMSContext createContext() {
    return delegate.createContext();
  }

  public JMSContext createContext(String userName, String password) {
    return delegate.createContext(userName, password);
  }

  public JMSContext createContext(String userName, String password, int sessionMode) {
    return delegate.createContext(userName, password, sessionMode);
  }

  public JMSContext createContext(int sessionMode) {
    return delegate.createContext(sessionMode);
  }

  public T getDelegate() {
    return delegate;
  }
}
