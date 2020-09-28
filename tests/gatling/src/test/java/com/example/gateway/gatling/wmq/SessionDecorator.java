package com.example.gateway.gatling.wmq;

import java.io.Serializable;
import java.util.function.Consumer;
import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;

/**
 * A decorator for {@link javax.jms.Session} that allows created connections to be customized by a `Consumer<Destination>`.
 *
 * To be used via {@link ConnectionFactoryDecorator}.
 */
class SessionDecorator implements Session {
  private final Session delegate;
  private final Consumer<Destination> destinationConfigurer;

  public SessionDecorator(Session delegate, Consumer<Destination> destinationConfigurer) {
    this.delegate = delegate;
    this.destinationConfigurer = destinationConfigurer;
  }

  public BytesMessage createBytesMessage() throws JMSException {
    return delegate.createBytesMessage();
  }

  public MapMessage createMapMessage() throws JMSException {
    return delegate.createMapMessage();
  }

  public Message createMessage() throws JMSException {
    return delegate.createMessage();
  }

  public ObjectMessage createObjectMessage() throws JMSException {
    return delegate.createObjectMessage();
  }

  public ObjectMessage createObjectMessage(Serializable object) throws JMSException {
    return delegate.createObjectMessage(object);
  }

  public StreamMessage createStreamMessage() throws JMSException {
    return delegate.createStreamMessage();
  }

  public TextMessage createTextMessage() throws JMSException {
    return delegate.createTextMessage();
  }

  public TextMessage createTextMessage(String text) throws JMSException {
    return delegate.createTextMessage(text);
  }

  public boolean getTransacted() throws JMSException {
    return delegate.getTransacted();
  }

  public int getAcknowledgeMode() throws JMSException {
    return delegate.getAcknowledgeMode();
  }

  public void commit() throws JMSException {
    delegate.commit();
  }

  public void rollback() throws JMSException {
    delegate.rollback();
  }

  public void close() throws JMSException {
    delegate.close();
  }

  public void recover() throws JMSException {
    delegate.recover();
  }

  public MessageListener getMessageListener() throws JMSException {
    return delegate.getMessageListener();
  }

  public void setMessageListener(MessageListener listener) throws JMSException {
    delegate.setMessageListener(listener);
  }

  public void run() {
    delegate.run();
  }

  public MessageProducer createProducer(Destination destination) throws JMSException {
    return delegate.createProducer(destination);
  }

  public MessageConsumer createConsumer(Destination destination) throws JMSException {
    return delegate.createConsumer(destination);
  }

  public MessageConsumer createConsumer(Destination destination, String messageSelector) throws JMSException {
    return delegate.createConsumer(destination, messageSelector);
  }

  public MessageConsumer createConsumer(Destination destination, String messageSelector, boolean noLocal) throws JMSException {
    return delegate.createConsumer(destination, messageSelector, noLocal);
  }

  public MessageConsumer createSharedConsumer(Topic topic, String sharedSubscriptionName) throws JMSException {
    return delegate.createSharedConsumer(topic, sharedSubscriptionName);
  }

  public MessageConsumer createSharedConsumer(Topic topic, String sharedSubscriptionName,
      String messageSelector) throws JMSException {
    return delegate.createSharedConsumer(topic, sharedSubscriptionName, messageSelector);
  }

  public Queue createQueue(String queueName) throws JMSException {
    final Queue queue = delegate.createQueue(queueName);
    destinationConfigurer.accept(queue);
    return queue;
  }

  public Topic createTopic(String topicName) throws JMSException {
    final Topic topic = delegate.createTopic(topicName);
    destinationConfigurer.accept(topic);
    return topic;
  }

  public TopicSubscriber createDurableSubscriber(Topic topic, String name) throws JMSException {
    return delegate.createDurableSubscriber(topic, name);
  }

  public TopicSubscriber createDurableSubscriber(Topic topic, String name, String messageSelector, boolean noLocal) throws JMSException {
    return delegate.createDurableSubscriber(topic, name, messageSelector, noLocal);
  }

  public MessageConsumer createDurableConsumer(Topic topic, String name) throws JMSException {
    return delegate.createDurableConsumer(topic, name);
  }

  public MessageConsumer createDurableConsumer(Topic topic, String name, String messageSelector, boolean noLocal) throws JMSException {
    return delegate.createDurableConsumer(topic, name, messageSelector, noLocal);
  }

  public MessageConsumer createSharedDurableConsumer(Topic topic, String name) throws JMSException {
    return delegate.createSharedDurableConsumer(topic, name);
  }

  public MessageConsumer createSharedDurableConsumer(Topic topic, String name, String messageSelector) throws JMSException {
    return delegate.createSharedDurableConsumer(topic, name, messageSelector);
  }

  public QueueBrowser createBrowser(Queue queue) throws JMSException {
    return delegate.createBrowser(queue);
  }

  public QueueBrowser createBrowser(Queue queue, String messageSelector) throws JMSException {
    return delegate.createBrowser(queue, messageSelector);
  }

  public TemporaryQueue createTemporaryQueue() throws JMSException {
    final TemporaryQueue temporaryQueue = delegate.createTemporaryQueue();
    destinationConfigurer.accept(temporaryQueue);
    return temporaryQueue;
  }

  public TemporaryTopic createTemporaryTopic() throws JMSException {
    final TemporaryTopic temporaryTopic = delegate.createTemporaryTopic();
    destinationConfigurer.accept(temporaryTopic);
    return temporaryTopic;
  }

  public void unsubscribe(String name) throws JMSException {
    delegate.unsubscribe(name);
  }
}
