/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
