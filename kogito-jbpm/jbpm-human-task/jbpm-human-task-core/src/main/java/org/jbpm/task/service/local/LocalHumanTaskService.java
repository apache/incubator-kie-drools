package org.jbpm.task.service.local;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.process.workitem.wsht.LocalHTWorkItemHandler;
import org.jbpm.task.service.TaskService;
import org.kie.SystemEventListenerFactory;
import org.kie.runtime.Environment;
import org.kie.runtime.EnvironmentName;
import org.kie.runtime.StatefulKnowledgeSession;

public class LocalHumanTaskService {
	
	private static TaskService INSTANCE;
	
	public static TaskService getService(Environment environment) {
		if (INSTANCE == null) {
			EntityManagerFactory emf = (EntityManagerFactory) environment.get(EnvironmentName.ENTITY_MANAGER_FACTORY);
			if (emf == null) {
				emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
			}
	        TaskService taskService = new TaskService(emf, SystemEventListenerFactory.getSystemEventListener());
	        INSTANCE = taskService;
		}
		return INSTANCE;
	}
	
	public static org.jbpm.task.TaskService getTaskService(StatefulKnowledgeSession ksession) {
	    TaskService taskService = getService(ksession.getEnvironment());
		
		LocalHTWorkItemHandler humanTaskHandler = new LocalHTWorkItemHandler(
			new LocalTaskService(taskService), ksession);
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task", humanTaskHandler);
		
		return new LocalTaskService(taskService);
	}

}
