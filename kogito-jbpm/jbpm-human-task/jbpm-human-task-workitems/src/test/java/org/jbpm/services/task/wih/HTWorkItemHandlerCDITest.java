package org.jbpm.services.task.wih;
/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.drools.core.impl.EnvironmentFactory;
import org.jbpm.services.task.HumanTaskServiceFactory;
import org.jbpm.services.task.impl.TaskServiceEntryPointImpl;
import org.jbpm.services.task.test.TestStatefulKnowledgeSession;
import org.junit.After;
import org.junit.Before;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.internal.task.api.EventService;

public class HTWorkItemHandlerCDITest extends HTWorkItemHandlerBaseTest {

    private EntityManagerFactory emf;
    private WorkItemHandler htWorkItemHandler;
    
    @Before
    public void setUp() throws Exception {
        emf = Persistence.createEntityManagerFactory("org.jbpm.services.task");
        ksession = new TestStatefulKnowledgeSession();
        ksession.setEnvironment(EnvironmentFactory.newEnvironment());
        taskService = HumanTaskServiceFactory.newTaskServiceConfigurator()
        .entityManagerFactory(emf)
        .getTaskService();
        htWorkItemHandler = new NonManagedLocalHTWorkItemHandler(ksession, taskService);
        listenr = new AddedTaskListener();
        listenr.setThrowException(false);
        ((EventService)taskService).registerTaskLifecycleEventListener(listenr);
        setHandler(htWorkItemHandler);
    }

    @After
    public void tearDown() throws Exception {
        int removeAllTasks = ((TaskServiceEntryPointImpl)taskService).removeAllTasks();
        emf.close();

    }
}
