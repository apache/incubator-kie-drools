package org.jbpm.task.service.jms;
 
import java.io.IOException;
import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.jbpm.task.service.SessionWriter;

public class JMSSessionWriter implements SessionWriter {
	private final Session session;
	private final MessageProducer producer;
	private final String selector;

	public JMSSessionWriter(Session session, MessageProducer producer, String selector) {
		this.session = session;
		this.producer = producer;
		this.selector = selector;
	}

	public void write(Object message) throws IOException {
		try {
			ObjectMessage clientMessage = this.session.createObjectMessage();
			clientMessage.setObject((Serializable) message);
			
			clientMessage.setStringProperty(TaskServiceConstants.SELECTOR_NAME, this.selector);
			this.producer.send(clientMessage);
		} catch (JMSException e) {
			throw new IOException("Unable to create message: " + e.getMessage());
		} finally {
			try {
				if(this.session.getTransacted()) {
					this.session.commit();
				}
			} catch (JMSException e) {
				throw new IOException("Unable to commit message: " + e.getMessage());
			}
		}
	}
}
