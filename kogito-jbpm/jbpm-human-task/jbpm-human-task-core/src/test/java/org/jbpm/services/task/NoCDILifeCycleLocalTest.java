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
package org.jbpm.services.task;

import java.util.logging.LogManager;

import javax.enterprise.event.Event;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.services.task.deadlines.DeadlinesDecorator;
import org.jbpm.services.task.identity.MvelUserGroupCallbackImpl;
import org.jbpm.services.task.identity.UserGroupLifeCycleManagerDecorator;
import org.jbpm.services.task.identity.UserGroupTaskInstanceServiceDecorator;
import org.jbpm.services.task.identity.UserGroupTaskQueryServiceDecorator;
import org.jbpm.services.task.impl.TaskAdminServiceImpl;
import org.jbpm.services.task.impl.TaskContentServiceImpl;
import org.jbpm.services.task.impl.TaskDeadlinesServiceImpl;
import org.jbpm.services.task.impl.TaskIdentityServiceImpl;
import org.jbpm.services.task.impl.TaskInstanceServiceImpl;
import org.jbpm.services.task.impl.TaskQueryServiceImpl;
import org.jbpm.services.task.impl.TaskServiceEntryPointImpl;
import org.jbpm.services.task.internals.lifecycle.LifeCycleManager;
import org.jbpm.services.task.internals.lifecycle.MVELLifeCycleManager;
import org.jbpm.services.task.rule.RuleContextProvider;
import org.jbpm.services.task.rule.TaskRuleService;
import org.jbpm.services.task.rule.impl.RuleContextProviderImpl;
import org.jbpm.services.task.rule.impl.TaskRuleServiceImpl;
import org.jbpm.services.task.subtask.SubTaskDecorator;
import org.jbpm.shared.services.api.JbpmServicesPersistenceManager;
import org.jbpm.shared.services.impl.JbpmLocalTransactionManager;
import org.jbpm.shared.services.impl.JbpmServicesPersistenceManagerImpl;
import org.jbpm.shared.services.impl.events.JbpmServicesEventImpl;
import org.junit.Before;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.TaskAdminService;
import org.kie.internal.task.api.TaskContentService;
import org.kie.internal.task.api.TaskDeadlinesService;
import org.kie.internal.task.api.TaskIdentityService;
import org.kie.internal.task.api.TaskInstanceService;
import org.kie.internal.task.api.TaskQueryService;
import org.kie.internal.task.api.UserGroupCallback;
import org.kie.internal.task.api.model.NotificationEvent;

/**
 *
 *
 */

public class NoCDILifeCycleLocalTest extends LifeCycleBaseTest {

   
    @Override
    @Before
    public void setUp() {
        
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.jbpm.services.task");
        EntityManager em = emf.createEntityManager();
        
        
        logger = LogManager.getLogManager().getLogger("");
        
        JbpmServicesPersistenceManager pm = new JbpmServicesPersistenceManagerImpl();
        ((JbpmServicesPersistenceManagerImpl)pm).setEm(em);
        ((JbpmServicesPersistenceManagerImpl)pm).setTransactionManager(new JbpmLocalTransactionManager());
        taskService = new TaskServiceEntryPointImpl();
        
        Event<Task> taskEvents = new JbpmServicesEventImpl<Task>();
        
        Event<NotificationEvent> notificationEvents = new JbpmServicesEventImpl<NotificationEvent>();
        
        UserGroupCallback userGroupCallback = new MvelUserGroupCallbackImpl();
        
        TaskQueryService queryService = new TaskQueryServiceImpl();
        ((TaskQueryServiceImpl)queryService).setPm(pm);
        
        
        UserGroupTaskQueryServiceDecorator userGroupTaskQueryServiceDecorator = new UserGroupTaskQueryServiceDecorator();
        userGroupTaskQueryServiceDecorator.setPm(pm);
        userGroupTaskQueryServiceDecorator.setUserGroupCallback(userGroupCallback);
        userGroupTaskQueryServiceDecorator.setDelegate(queryService);
        
        ((TaskServiceEntryPointImpl)taskService).setTaskQueryService(userGroupTaskQueryServiceDecorator);
        
        TaskIdentityService identityService = new TaskIdentityServiceImpl();
        ((TaskIdentityServiceImpl)identityService).setPm(pm);
        ((TaskServiceEntryPointImpl)taskService).setTaskIdentityService(identityService);
        
        TaskAdminService adminService = new TaskAdminServiceImpl();
        ((TaskAdminServiceImpl)adminService).setPm(pm);
        ((TaskServiceEntryPointImpl)taskService).setTaskAdminService(adminService);
        
        TaskInstanceService instanceService = new TaskInstanceServiceImpl();
        ((TaskInstanceServiceImpl)instanceService).setPm(pm);
        ((TaskInstanceServiceImpl)instanceService).setTaskQueryService(userGroupTaskQueryServiceDecorator);
        ((TaskInstanceServiceImpl)instanceService).setTaskEvents(taskEvents);
        
        UserGroupTaskInstanceServiceDecorator userGroupTaskInstanceDecorator = new UserGroupTaskInstanceServiceDecorator();
        userGroupTaskInstanceDecorator.setPm(pm);
        userGroupTaskInstanceDecorator.setUserGroupCallback(userGroupCallback);
        userGroupTaskInstanceDecorator.setDelegate(instanceService);
        
        TaskContentService contentService = new TaskContentServiceImpl();
        ((TaskContentServiceImpl)contentService).setPm(pm);
        ((TaskServiceEntryPointImpl)taskService).setTaskContentService(contentService);
        
        LifeCycleManager mvelLifeCycleManager = new MVELLifeCycleManager();
        ((MVELLifeCycleManager)mvelLifeCycleManager).setPm(pm);
        ((MVELLifeCycleManager)mvelLifeCycleManager).setTaskIdentityService(identityService);
        ((MVELLifeCycleManager)mvelLifeCycleManager).setTaskQueryService(userGroupTaskQueryServiceDecorator);
        ((MVELLifeCycleManager)mvelLifeCycleManager).setTaskContentService(contentService);
        ((MVELLifeCycleManager)mvelLifeCycleManager).setTaskEvents(taskEvents);
        ((MVELLifeCycleManager)mvelLifeCycleManager).setLogger(logger);
        ((MVELLifeCycleManager)mvelLifeCycleManager).initMVELOperations();
        
        
        UserGroupLifeCycleManagerDecorator userGroupLifeCycleDecorator = new UserGroupLifeCycleManagerDecorator();
        userGroupLifeCycleDecorator.setPm(pm);
        userGroupLifeCycleDecorator.setUserGroupCallback(userGroupCallback);
        userGroupLifeCycleDecorator.setManager(mvelLifeCycleManager);
        ((TaskInstanceServiceImpl)instanceService).setLifeCycleManager(userGroupLifeCycleDecorator);
        
        
        TaskDeadlinesService deadlinesService = new TaskDeadlinesServiceImpl();
        ((TaskDeadlinesServiceImpl)deadlinesService).setPm(pm);
        ((TaskDeadlinesServiceImpl)deadlinesService).setLogger(logger);
        ((TaskDeadlinesServiceImpl)deadlinesService).setNotificationEvents(notificationEvents);
        ((TaskDeadlinesServiceImpl)deadlinesService).init();
        
        SubTaskDecorator subTaskDecorator = new SubTaskDecorator();
        subTaskDecorator.setInstanceService(userGroupTaskInstanceDecorator);
        subTaskDecorator.setPm(pm);
        subTaskDecorator.setQueryService(userGroupTaskQueryServiceDecorator);
        
        DeadlinesDecorator deadlinesDecorator = new DeadlinesDecorator();
        deadlinesDecorator.setPm(pm);
        deadlinesDecorator.setQueryService(userGroupTaskQueryServiceDecorator);
        deadlinesDecorator.setDeadlineService(deadlinesService);
        deadlinesDecorator.setQueryService(userGroupTaskQueryServiceDecorator);
        deadlinesDecorator.setInstanceService(subTaskDecorator);
        
        ((TaskServiceEntryPointImpl)taskService).setTaskInstanceService(deadlinesDecorator);
        
        RuleContextProvider ruleProvider = new RuleContextProviderImpl();
        ((RuleContextProviderImpl)ruleProvider).initialize();
        TaskRuleService taskRuleService = new TaskRuleServiceImpl();
        ((TaskRuleServiceImpl)taskRuleService).setRuleContextProvider(ruleProvider);
        ((TaskServiceEntryPointImpl)taskService).setTaskRuleService(taskRuleService);

        super.setUp();
        
    }
}
