/*
 * Copyright 2012 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.task;

import java.util.logging.LogManager;
import java.util.logging.Logger;
import javax.enterprise.event.Event;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.jbpm.shared.services.api.JbpmServicesPersistenceManager;
import org.jbpm.shared.services.impl.JbpmLocalTransactionManager;
import org.jbpm.shared.services.impl.JbpmServicesPersistenceManagerImpl;
import org.jbpm.task.api.TaskAdminService;
import org.jbpm.task.api.TaskContentService;
import org.jbpm.task.api.TaskDeadlinesService;
import org.jbpm.task.api.TaskIdentityService;
import org.jbpm.task.api.TaskInstanceService;
import org.jbpm.task.api.TaskQueryService;
import org.jbpm.shared.services.impl.events.JbpmServicesEventImpl;
import org.jbpm.task.deadlines.DeadlinesDecorator;
import org.jbpm.task.events.NotificationEvent;
import org.jbpm.task.identity.MvelUserGroupCallbackImpl;
import org.jbpm.task.identity.UserGroupCallback;
import org.jbpm.task.identity.UserGroupLifeCycleManagerDecorator;
import org.jbpm.task.impl.TaskAdminServiceImpl;
import org.jbpm.task.impl.TaskContentServiceImpl;
import org.jbpm.task.impl.TaskDeadlinesServiceImpl;
import org.jbpm.task.impl.TaskIdentityServiceImpl;
import org.jbpm.task.impl.TaskInstanceServiceImpl;
import org.jbpm.task.impl.TaskQueryServiceImpl;
import org.jbpm.task.impl.TaskServiceEntryPointImpl;
import org.jbpm.task.internals.lifecycle.LifeCycleManager;
import org.jbpm.task.internals.lifecycle.MVELLifeCycleManager;
import org.jbpm.task.subtask.SubTaskDecorator;
import org.junit.Before;

/**
 *
 *
 */

public class NoCDILifeCycleLocalTest extends LifeCycleBaseTest {

   
    @Override
    @Before
    public void setUp() {
        
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.jbpm.task");
        EntityManager em = emf.createEntityManager();
        
        
        logger = LogManager.getLogManager().getLogger("");
        
        JbpmServicesPersistenceManager pm = new JbpmServicesPersistenceManagerImpl();
        ((JbpmServicesPersistenceManagerImpl)pm).setEm(em);
        ((JbpmServicesPersistenceManagerImpl)pm).setTransactionManager(new JbpmLocalTransactionManager());
        taskService = new TaskServiceEntryPointImpl();
        
        Event<Task> taskEvents = new JbpmServicesEventImpl<Task>();
        
        Event<NotificationEvent> notificationEvents = new JbpmServicesEventImpl<NotificationEvent>();
        
        TaskQueryService queryService = new TaskQueryServiceImpl();
        ((TaskQueryServiceImpl)queryService).setPm(pm);
        taskService.setTaskQueryService(queryService);
        
        TaskIdentityService identityService = new TaskIdentityServiceImpl();
        ((TaskIdentityServiceImpl)identityService).setPm(pm);
        taskService.setTaskIdentityService(identityService);
        
        TaskAdminService adminService = new TaskAdminServiceImpl();
        ((TaskAdminServiceImpl)adminService).setPm(pm);
        taskService.setTaskAdminService(adminService);
        
        TaskInstanceService instanceService = new TaskInstanceServiceImpl();
        ((TaskInstanceServiceImpl)instanceService).setPm(pm);
        ((TaskInstanceServiceImpl)instanceService).setTaskQueryService(queryService);
        ((TaskInstanceServiceImpl)instanceService).setTaskEvents(taskEvents);
        
        
        TaskContentService contentService = new TaskContentServiceImpl();
        ((TaskContentServiceImpl)contentService).setPm(pm);
        taskService.setTaskContentService(contentService);
        
        LifeCycleManager mvelLifeCycleManager = new MVELLifeCycleManager();
        ((MVELLifeCycleManager)mvelLifeCycleManager).setPm(pm);
        ((MVELLifeCycleManager)mvelLifeCycleManager).setTaskIdentityService(identityService);
        ((MVELLifeCycleManager)mvelLifeCycleManager).setTaskQueryService(queryService);
        ((MVELLifeCycleManager)mvelLifeCycleManager).setTaskContentService(contentService);
        ((MVELLifeCycleManager)mvelLifeCycleManager).setTaskEvents(taskEvents);
        ((MVELLifeCycleManager)mvelLifeCycleManager).setLogger(logger);
        ((MVELLifeCycleManager)mvelLifeCycleManager).initMVELOperations();
        
        UserGroupCallback userGroupCallback = new MvelUserGroupCallbackImpl();
        
        UserGroupLifeCycleManagerDecorator userGroupLifeCycleDecorator = new UserGroupLifeCycleManagerDecorator();
        userGroupLifeCycleDecorator.setPm(pm);
        userGroupLifeCycleDecorator.setManager(mvelLifeCycleManager);
        userGroupLifeCycleDecorator.setUserGroupCallback(userGroupCallback);
        
        
        ((TaskInstanceServiceImpl)instanceService).setLifeCycleManager(userGroupLifeCycleDecorator);
        
        ((TaskInstanceServiceImpl)instanceService).setUserGroupCallback(userGroupCallback);
        
        TaskDeadlinesService deadlinesService = new TaskDeadlinesServiceImpl();
        ((TaskDeadlinesServiceImpl)deadlinesService).setPm(pm);
        ((TaskDeadlinesServiceImpl)deadlinesService).setLogger(logger);
        ((TaskDeadlinesServiceImpl)deadlinesService).setNotificationEvents(notificationEvents);
        
        SubTaskDecorator subTaskDecorator = new SubTaskDecorator();
        subTaskDecorator.setInstanceService(instanceService);
        subTaskDecorator.setPm(pm);
        subTaskDecorator.setQueryService(queryService);
        
        DeadlinesDecorator deadlinesDecorator = new DeadlinesDecorator();
        deadlinesDecorator.setPm(pm);
        deadlinesDecorator.setQueryService(queryService);
        deadlinesDecorator.setDeadlineService(deadlinesService);
        deadlinesDecorator.setQueryService(queryService);
        deadlinesDecorator.setInstanceService(subTaskDecorator);
        
        
        
        
        taskService.setTaskInstanceService(deadlinesDecorator);

        super.setUp();
        
    }
}
