package org.jbpm.task.service.jms;

import java.util.Properties;

import javax.naming.Context;

import org.kie.SystemEventListenerFactory;
import org.jbpm.task.event.TaskEventListener;
import org.jbpm.task.service.TaskService;

public class JMSTaskServer extends BaseJMSTaskServer {
    private TaskService service;
	public JMSTaskServer(TaskService service, Properties connProperties, Context context) {
		super(new JMSTaskServerHandler(service, SystemEventListenerFactory.getSystemEventListener()), connProperties, context);
                this.service = service;
	}

    @Override
    public void addEventListener(TaskEventListener listener) {
        this.service.addEventListener(listener);
    }
}
