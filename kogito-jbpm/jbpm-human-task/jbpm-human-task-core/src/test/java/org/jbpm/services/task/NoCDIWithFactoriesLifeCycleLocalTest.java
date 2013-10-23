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

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.services.task.identity.MvelUserGroupCallbackImpl;
import org.jbpm.shared.services.impl.JbpmLocalTransactionManager;
import org.junit.After;
import org.junit.Before;
import org.kie.internal.task.api.InternalTaskService;

/**
 *
 *
 */

public class NoCDIWithFactoriesLifeCycleLocalTest extends LifeCycleBaseTest {

    private EntityManagerFactory emf;
    @Override
    @Before
    public void setUp() {
        
        emf = Persistence.createEntityManagerFactory("org.jbpm.services.task");
        // Default Configuration for standalone environments
        taskService = (InternalTaskService) HumanTaskServiceFactory.newTaskServiceConfigurator()
        		.transactionManager(new JbpmLocalTransactionManager())
        		.userGroupCallback(new MvelUserGroupCallbackImpl())
                .entityManagerFactory(emf)
                .getTaskService();

        super.setUp();
        
    }
    
    @After
    public void tearDown(){
        emf.close();
    }
    
}
