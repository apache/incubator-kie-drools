package org.jbpm.task.service.jms;

import java.io.Serializable;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.task.service.BaseHandler;
import org.jbpm.task.service.TaskClientConnector;

public class JMSTaskClientConnector implements TaskClientConnector {
	
	private static final Log log = LogFactory.getLog(JMSTaskClientConnector.class);
	protected QueueConnection connection;
	protected QueueSession session;
	protected Queue queue;
	protected Queue responseQueue;
	protected final BaseJMSHandler handler;
	protected final String name;
	protected AtomicInteger counter;
	private MessageProducer producer;
	private Properties connectionProperties;
	private Context context;
	
	private String selector;
	

	public JMSTaskClientConnector(String name, BaseJMSHandler handler, Properties connectionProperties, Context context) {
		if (name == null) {
			throw new IllegalArgumentException("Name can not be null");
		}
		this.name = name;
		this.handler = handler;
		this.connectionProperties = connectionProperties;
		this.context = context;
		this.counter = new AtomicInteger();
	}

	public boolean connect(String address, int port) {
		return connect();
	}

	public boolean connect() {
		if (this.session != null) { 
			//throw new IllegalStateException("Already connected. Disconnect first.");
			return true;
		}
		try {
			
			String connFactoryName = connectionProperties.getProperty(TaskServiceConstants.TASK_CLIENT_CONNECTION_FACTORY_NAME);
			String transactedQueueString = connectionProperties.getProperty(TaskServiceConstants.TASK_CLIENT_TRANSACTED_QUEUE_NAME);
			String acknowledgeModeString = connectionProperties.getProperty(TaskServiceConstants.TASK_CLIENT_ACKNOWLEDGE_MODE_NAME);
			String queueName = connectionProperties.getProperty(TaskServiceConstants.TASK_CLIENT_QUEUE_NAME_NAME);
			String responseQueueName = connectionProperties.getProperty(TaskServiceConstants.TASK_CLIENT_RESPONSE_QUEUE_NAME_NAME);
			boolean transactedQueue = Boolean.valueOf(transactedQueueString);
			int acknowledgeMode = Session.DUPS_OK_ACKNOWLEDGE; //default
			if ("AUTO_ACKNOWLEDGE".equals(acknowledgeModeString)) {
				acknowledgeMode = Session.AUTO_ACKNOWLEDGE;
			} else if ("CLIENT_ACKNOWLEDGE".equals(acknowledgeModeString)) {
				acknowledgeMode = Session.CLIENT_ACKNOWLEDGE;
			}
			Context ctx = this.context;
			if (ctx == null) {
				ctx = new InitialContext();
			}
			QueueConnectionFactory factory = (QueueConnectionFactory) ctx.lookup(connFactoryName);
			this.connection = factory.createQueueConnection();
			this.session = this.connection.createQueueSession(transactedQueue, acknowledgeMode);
			this.queue = this.session.createQueue(queueName);
			this.responseQueue = this.session.createQueue(responseQueueName);
			this.producer = this.session.createProducer(this.queue);
			this.connection.start();
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return false;
	}
	
	private Object readMessage(ObjectMessage serverMessage) throws JMSException {
		return serverMessage.getObject();
	}

	public void disconnect() {
		if (this.producer != null) {
			try {
				this.producer.close();
			} catch (Exception e) { 
			} finally {
				this.producer = null;
			}
		}
		if (this.session != null) {
			try {
				this.session.close();
			} catch (Exception e) {
			} finally {
				this.session = null;
			}
		}
		if (this.connection != null) {
			try {
				this.connection.close();
			} catch (Exception e) {
			} finally {
				this.connection = null;
			}
		}
	}

	public void write(Object object) {
		try {
			ObjectMessage message = this.session.createObjectMessage();
			this.selector = UUID.randomUUID().toString();
			Thread responseThread = new Thread(new Responder(selector));
			responseThread.start();
			message.setStringProperty(TaskServiceConstants.SELECTOR_NAME, this.selector);
			message.setObject((Serializable)object);
			this.producer.send(message);
			this.session.commit();
		} catch (Throwable e) {
			throw new RuntimeException("Error creating message", e);
		}
	}

	public AtomicInteger getCounter() {
		return this.counter;
	}

	public BaseHandler getHandler() {
		return this.handler;
	}

	public String getName() {
		return this.name;
	}
	
	protected class Responder implements Runnable {
		
		private final String selector;
		
		protected Responder(String selector) {
			this.selector = selector;
		}
		
		public void run() {
			MessageConsumer consumer = null;
			try {
				consumer = session.createConsumer(responseQueue, " " + TaskServiceConstants.SELECTOR_NAME + " like '" + selector + "%' ");
				ObjectMessage serverMessage = (ObjectMessage) consumer.receive();
				if (serverMessage != null) {
					((JMSTaskClientHandler) handler).messageReceived(session, readMessage(serverMessage), responseQueue, selector);
				}
			} catch (JMSException e) {
				if (!"102".equals(e.getErrorCode())) {
					throw new RuntimeException("No se pudo recibir respuesta JMS", e);
				}
				log.info(e.getMessage());
				return;
			} catch (Exception e) {
				throw new RuntimeException("Error inesperado recibiendo respuesta JMS", e);
			} finally {
				if (consumer != null) {
					try {
						consumer.close();
					} catch (Exception e) {
						log.info("No se pudo cerrar el consumer: " + e.getMessage());
					}
				}
			}
		}
	}
}
