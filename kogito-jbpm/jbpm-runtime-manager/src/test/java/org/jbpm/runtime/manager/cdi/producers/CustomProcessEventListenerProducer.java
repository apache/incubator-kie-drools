package org.jbpm.runtime.manager.cdi.producers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.core.event.DebugProcessEventListener;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.internal.runtime.manager.EventListenerProducer;

@org.jbpm.runtime.manager.api.qualifiers.Process
public class CustomProcessEventListenerProducer implements EventListenerProducer<ProcessEventListener> {

	@Override
	public List<ProcessEventListener> getEventListeners(String identifier, Map<String, Object> params) {
		
		List<ProcessEventListener> processEventListeners = new ArrayList<ProcessEventListener>();
		processEventListeners.add(new DebugProcessEventListener());
		
		return processEventListeners;
	}

}
