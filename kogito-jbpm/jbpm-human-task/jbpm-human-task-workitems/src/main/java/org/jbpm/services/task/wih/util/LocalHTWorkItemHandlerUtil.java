package org.jbpm.services.task.wih.util;

import javax.persistence.EntityManagerFactory;

import org.jbpm.services.task.HumanTaskServiceFactory;
import org.jbpm.services.task.wih.NonManagedLocalHTWorkItemHandler;
import org.kie.api.runtime.KieSession;
import org.kie.api.task.TaskService;
import org.kie.internal.task.api.UserGroupCallback;

public class LocalHTWorkItemHandlerUtil {
	
	public static TaskService registerLocalHTWorkItemHandler(KieSession ksession, EntityManagerFactory emf, UserGroupCallback userGroupCallback) {
        TaskService taskService = HumanTaskServiceFactory.newTaskServiceConfigurator()
	        .entityManagerFactory(emf)
	        .userGroupCallback(userGroupCallback)
	        .getTaskService();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
    		new NonManagedLocalHTWorkItemHandler(ksession, taskService));
		return taskService;
	}

}
