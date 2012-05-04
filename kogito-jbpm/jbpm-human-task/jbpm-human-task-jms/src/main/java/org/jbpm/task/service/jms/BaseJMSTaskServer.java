package org.jbpm.task.service.jms;

import java.io.IOException;
import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.jbpm.task.service.TaskServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseJMSTaskServer extends TaskServer {

	private static final Logger logger = LoggerFactory.getLogger(BaseJMSTaskServer.class);
	
	private JMSTaskServerHandler handler;
	
	private Properties connectionProperties;
	private Context context;
	private Queue queue;
	private Queue responseQueue;
	private QueueConnection connection;
	
	private boolean running;
	
	private QueueSession session;
	private MessageConsumer consumer;
	
	public BaseJMSTaskServer(JMSTaskServerHandler handler, Properties properties, Context context) {
		this.handler = handler;
		this.connectionProperties = properties;
		this.context = context;
	}

	public void run() {
		try {
			start();
			while (this.running) {
				Message clientMessage = this.consumer.receive();
				if (clientMessage != null) {
					Object object = readMessage(clientMessage);
					String selector = readSelector(clientMessage);
					this.handler.messageReceived(this.session, object, this.responseQueue, selector);
				}
			}
		} catch (JMSException e) {
			if ("102".equals(e.getErrorCode())) {
				logger.info(e.getMessage());
			} else {
				logger.error(e.getMessage());
			}
		} catch (Exception e) {
			throw new RuntimeException("Error leyendo mensaje", e);
		}
	}

	private Object readMessage(Message msgReceived) throws IOException {
		ObjectMessage strmMsgReceived = (ObjectMessage) msgReceived;
		try {
			return strmMsgReceived.getObject();
		} catch (JMSException e) {
			throw new IOException("Error reading message");
		}
	}
	
	private String readSelector(Message msgReceived) throws JMSException {
		return msgReceived.getStringProperty(TaskServiceConstants.SELECTOR_NAME);
	}

	public void start() throws Exception {
		Context ctx = this.context;
		if (this.context == null) {
			ctx = new InitialContext();
		}
		String connFactoryName = this.connectionProperties.getProperty(TaskServiceConstants.TASK_SERVER_CONNECTION_FACTORY_NAME);
		boolean transacted = Boolean.valueOf(this.connectionProperties.getProperty(TaskServiceConstants.TASK_SERVER_TRANSACTED_NAME));
		String ackModeString = this.connectionProperties.getProperty(TaskServiceConstants.TASK_SERVER_ACKNOWLEDGE_MODE_NAME);
		String queueName = this.connectionProperties.getProperty(TaskServiceConstants.TASK_SERVER_QUEUE_NAME_NAME);
		String responseQueueName = this.connectionProperties.getProperty(TaskServiceConstants.TASK_SERVER_RESPONSE_QUEUE_NAME_NAME);
		int ackMode = Session.DUPS_OK_ACKNOWLEDGE; //default
		if ("AUTO_ACKNOWLEDGE".equals(ackModeString)) {
			ackMode = Session.AUTO_ACKNOWLEDGE;
		} else if ("CLIENT_ACKNOWLEDGE".equals(ackModeString)) {
			ackMode = Session.CLIENT_ACKNOWLEDGE;
		}
		QueueConnectionFactory factory = (QueueConnectionFactory) ctx.lookup(connFactoryName);
		try {
			this.connection = factory.createQueueConnection();
			this.session = connection.createQueueSession(transacted, ackMode);
			this.queue = this.session.createQueue(queueName);
			this.responseQueue = this.session.createQueue(responseQueueName);
			this.consumer = this.session.createConsumer(this.queue);
			this.connection.start();
		} catch (JMSException e) {
			throw new RuntimeException("No se pudo levantar la cola servidora del JMSTaskServer", e);
		}
		this.running = true;
	}

	public void stop() throws Exception {
		if (this.running) {
			this.running = false;
			closeAll();
		}
	}

	private void closeAll() throws JMSException {
		this.consumer.close();
		this.session.close();
		this.connection.close();
	}

	public boolean isRunning() {
		return this.running;
	}
}
