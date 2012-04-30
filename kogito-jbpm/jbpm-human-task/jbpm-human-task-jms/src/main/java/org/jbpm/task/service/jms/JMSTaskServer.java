package org.jbpm.task.service.jms;

import java.util.Properties;

import javax.naming.Context;

import org.drools.SystemEventListenerFactory;
import org.jbpm.task.service.TaskService;

public class JMSTaskServer extends BaseJMSTaskServer {

	public JMSTaskServer(TaskService service, Properties connProperties, Context context) {
		super(new JMSTaskServerHandler(service, SystemEventListenerFactory.getSystemEventListener()), connProperties, context);
	}
}
