package org.jbpm.kie.services.cdi.producer;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;

import org.jbpm.services.task.HumanTaskConfigurator;
import org.jbpm.services.task.HumanTaskServiceFactory;
import org.jbpm.services.task.impl.command.CommandBasedTaskService;
import org.jbpm.services.task.lifecycle.listeners.TaskLifeCycleEventListener;
import org.kie.api.task.UserGroupCallback;
import org.kie.internal.task.api.InternalTaskService;
import org.kie.internal.task.api.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HumanTaskServiceProducer {
	
	private static final Logger logger = LoggerFactory.getLogger(HumanTaskServiceProducer.class);
	
	@Inject
	private Instance<UserGroupCallback> userGroupCallback;
	
	@Inject
	private Instance<UserInfo> userInfo;
	
	@Inject
	@Any
	private Instance<TaskLifeCycleEventListener> taskListeners;

	// internal member to ensure only single instance of task service is produced
	private InternalTaskService taskService;
	
	@Produces
	public CommandBasedTaskService produceTaskService(EntityManagerFactory emf) {
		if (taskService == null) {
			HumanTaskConfigurator configurator = HumanTaskServiceFactory.newTaskServiceConfigurator()
					.entityManagerFactory(emf)
					.userGroupCallback(safeGet(userGroupCallback))
					.userInfo(safeGet(userInfo));
					
			try {
				for (TaskLifeCycleEventListener listener : taskListeners) {
					configurator.listener(listener);
					logger.debug("Registering listener {}", listener);
				}
			} catch (Exception e) {
				logger.warn("Cannot add listeners to task service due to {}", e.getMessage());
			}
			
			this.taskService = (CommandBasedTaskService) configurator.getTaskService();	
		}
		
		return (CommandBasedTaskService)taskService;
	}
	
	protected <T> T safeGet(Instance<T> instance) {
		try {
			T object = instance.get();
			logger.debug("About to set object {} on task service", object);
			return object;
		} catch (Throwable e) {
			logger.warn("Cannot get value of of instance {} due to {}", instance, e.getMessage());
		}
		
		return null;
	}

}
