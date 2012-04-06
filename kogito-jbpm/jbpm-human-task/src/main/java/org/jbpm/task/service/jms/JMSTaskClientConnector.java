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

import org.jbpm.task.event.TaskEventKey;
import org.jbpm.task.service.BaseClientHandler;
import org.jbpm.task.service.BaseHandler;
import org.jbpm.task.service.TaskClientConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jbpm.task.service.Command;

public class JMSTaskClientConnector implements TaskClientConnector {
	
	private static final Logger logger = LoggerFactory.getLogger(JMSTaskClientConnector.class);
	
	protected final BaseClientHandler handler;
	protected final String name;
	protected AtomicInteger counter;
	
	private MessageProducer producer;
	private Properties connectionProperties;
	private Context context;
    private boolean transactedQueue = false;
    private boolean enableLog = false;
	
	private String selector;
	protected QueueConnection connection;
	protected QueueSession producerSession;
	protected QueueSession consumerSession;
	protected Queue taskServerQueue;
	protected Queue responseQueue;

	public JMSTaskClientConnector(String name, BaseClientHandler handler, Properties connectionProperties, Context context) {
		if (name == null) {
			throw new IllegalArgumentException("Name can not be null");
		}
		this.name = name;
		this.handler = handler;
		this.connectionProperties = connectionProperties;
		this.context = context;
		this.counter = new AtomicInteger();
		if(System.getProperty("enableLog") != null)
			this.enableLog = Boolean.parseBoolean(System.getProperty("enableLog"));
	}

	public boolean connect(String address, int port) {
		return connect();
	}

	public boolean connect() {
		if (this.producerSession != null) { 
			return true;
		}
		try {
			
			String connFactoryName = connectionProperties.getProperty(TaskServiceConstants.TASK_CLIENT_CONNECTION_FACTORY_NAME);
			String transactedQueueString = connectionProperties.getProperty(TaskServiceConstants.TASK_CLIENT_TRANSACTED_QUEUE_NAME);
			String acknowledgeModeString = connectionProperties.getProperty(TaskServiceConstants.TASK_CLIENT_ACKNOWLEDGE_MODE_NAME);
			String taskServerQueueName = connectionProperties.getProperty(TaskServiceConstants.TASK_CLIENT_QUEUE_NAME_NAME);
			String responseQueueName = connectionProperties.getProperty(TaskServiceConstants.TASK_CLIENT_RESPONSE_QUEUE_NAME_NAME);
			transactedQueue = Boolean.valueOf(transactedQueueString);
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
			this.producerSession = this.connection.createQueueSession(transactedQueue, acknowledgeMode);
			this.consumerSession = this.connection.createQueueSession(transactedQueue, acknowledgeMode);
			this.taskServerQueue = this.producerSession.createQueue(taskServerQueueName);
			this.responseQueue = this.consumerSession.createQueue(responseQueueName);
			this.producer = this.producerSession.createProducer(this.taskServerQueue);
			this.connection.start();
			return true;
		} catch (Exception e) {
            logger.error(e.getMessage(), e);
		}
		return false;
	}
	
	private Object readMessage(ObjectMessage serverMessage) throws JMSException {
		return serverMessage.getObject();
	}

	public void disconnect() {
		try {
			if (this.producer != null) 
				this.producer.close();
			if(this.producerSession != null) {
				this.producerSession.close();
			    this.producerSession = null;
			}
			if(this.consumerSession != null)
				this.consumerSession.close();
			if (this.connection != null)
				this.connection.close();
		} catch(Exception e){
            logger.error(e.getMessage(), e);
		}
	}

	public void write(Object object) {
		try {
			ObjectMessage message = this.producerSession.createObjectMessage();
			this.selector = UUID.randomUUID().toString();
			
			//JA Bride :  now making aware of TaskKeyEvent handling
			Command cObj = (Command)object;
			java.util.List<Object> args = cObj.getArguments();
			boolean removeEvent = true;
			if(args.get(0) instanceof TaskEventKey){
				TaskEventKey eventKey = (TaskEventKey)args.get(0);
				removeEvent = (Boolean)args.get(1);
				logger.info("write() registering following taskEventKey with Human Task Server :\n\t"+eventKey.getEvent()+ "\n\tremoveEvent = "+removeEvent+"\n\tselector = "+selector);
			}else if(enableLog){
				logger.info("write() selector = "+selector+" : command = "+args.get(0));
			}
			Thread responseThread = new Thread(new Responder(selector, removeEvent));
			responseThread.start();
			message.setStringProperty(TaskServiceConstants.SELECTOR_NAME, this.selector);
			message.setObject((Serializable)object);
			
			synchronized(producer){
				this.producer.send(message);
				if(transactedQueue)
					this.producerSession.commit();
			}
		} catch (Throwable e) {
            logger.error("write() exception when attempting to write to : "+this.taskServerQueue);
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
		private final boolean removeEvent;
		
		protected Responder(String selector, boolean removeEvent) {
			this.selector = selector;
			this.removeEvent = removeEvent;
		}
		
		private void handleMessage(ObjectMessage serverMessage) throws Exception{
			if (serverMessage != null) {
				((JMSTaskClientHandler) handler).messageReceived(consumerSession, readMessage(serverMessage), responseQueue, selector);
			}else
				throw new RuntimeException("handleMessage() message received from Human Task Server is null!");
		}
		
		public void run() {
			MessageConsumer consumer = null;
			try {
				consumer = consumerSession.createConsumer(responseQueue, " " + TaskServiceConstants.SELECTOR_NAME + " like '" + selector + "%' ");
				ObjectMessage serverMessage = null;
				if(removeEvent) {
					serverMessage = (ObjectMessage) consumer.receive();
					handleMessage(serverMessage);
					if(transactedQueue)
						consumerSession.commit();
				}else {
					while(true){
						serverMessage = (ObjectMessage) consumer.receive();
						handleMessage(serverMessage);
						if(transactedQueue)
							consumerSession.commit();
					}
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			} finally {
				if (consumer != null) {
					try {
						consumer.close();
					} catch (Exception e) {
		                logger.error(e.getMessage(), e);
					}
				}
			}
		}
	}
}