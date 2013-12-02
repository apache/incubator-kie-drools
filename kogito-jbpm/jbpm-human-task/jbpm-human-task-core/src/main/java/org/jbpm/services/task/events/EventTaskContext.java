package org.jbpm.services.task.events;

import org.kie.api.task.UserGroupCallback;
import org.kie.internal.command.World;
import org.kie.internal.task.api.TaskContext;
import org.kie.internal.task.api.TaskPersistenceContext;

public class EventTaskContext implements TaskContext {

	private TaskPersistenceContext persistenceContext;
	
	public EventTaskContext() {
		
	}
	
	public EventTaskContext(TaskPersistenceContext persistenceContext) {
		this.persistenceContext = persistenceContext;
	}
	@Override
	public World getContextManager() {		
		return null;
	}

	@Override
	public String getName() {	
		return null;
	}

	@Override
	public Object get(String identifier) {		
		return null;
	}

	@Override
	public void set(String identifier, Object value) {

	}

	@Override
	public void remove(String identifier) {	
	}

	@Override
	public TaskPersistenceContext getPersistenceContext() {		
		return this.persistenceContext;
	}

	@Override
	public void setPersistenceContext(TaskPersistenceContext context) {
		this.persistenceContext = context;
	}

	@Override
	public UserGroupCallback getUserGroupCallback() {		
		return null;
	}

}
