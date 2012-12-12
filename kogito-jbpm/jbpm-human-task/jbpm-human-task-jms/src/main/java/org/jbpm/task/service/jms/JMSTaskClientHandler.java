package org.jbpm.task.service.jms;

import java.util.HashMap;
import java.util.Map;

import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;

import org.jbpm.task.service.BaseClientHandler;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.TaskClientHandler;
import org.kie.SystemEventListener;

public class JMSTaskClientHandler extends BaseClientHandler {
	private TaskClientHandler handler;
	private Map<String, MessageProducer> producers;

	public JMSTaskClientHandler(SystemEventListener systemEventListener) {
		this.handler = new TaskClientHandler(this.responseHandlers, systemEventListener);
		this.producers = new HashMap<String, MessageProducer>();
	}

	public TaskClient getClient() {
		return this.handler.getClient();
	}

	public void setClient(TaskClient client) {
		this.handler.setClient(client);
	}

	public void exceptionCaught(Session session, Throwable cause) throws Exception {
	}

	public void messageReceived(Session session, Object message, Destination destination, String selector) throws Exception {
		String name = "";
		if (destination instanceof Queue) {
			name = ((Queue) destination).getQueueName();
		} else if (destination instanceof Topic) {
			name = ((Topic) destination).getTopicName();
		}
		MessageProducer producer = (MessageProducer) this.producers.get(name);
		if (producer == null) {
			producer = session.createProducer(destination);
			this.producers.put(name, producer);
		}
		this.handler.messageReceived(new JMSSessionWriter(session, producer, selector), message);
	}
}
