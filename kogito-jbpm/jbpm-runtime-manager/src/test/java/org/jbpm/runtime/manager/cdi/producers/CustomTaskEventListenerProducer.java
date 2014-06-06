package org.jbpm.runtime.manager.cdi.producers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jbpm.runtime.manager.api.qualifiers.Task;
import org.jbpm.services.task.audit.JPATaskLifeCycleEventListener;
import org.kie.api.task.TaskLifeCycleEventListener;
import org.kie.internal.runtime.manager.EventListenerProducer;

@Task
public class CustomTaskEventListenerProducer implements EventListenerProducer<TaskLifeCycleEventListener> {

	@Override
	public List<TaskLifeCycleEventListener> getEventListeners(String identifier, Map<String, Object> params) {
		
		List<TaskLifeCycleEventListener> taskEventListeners = new ArrayList<TaskLifeCycleEventListener>();
		taskEventListeners.add(new JPATaskLifeCycleEventListener());

		
		return taskEventListeners;
	}

}
