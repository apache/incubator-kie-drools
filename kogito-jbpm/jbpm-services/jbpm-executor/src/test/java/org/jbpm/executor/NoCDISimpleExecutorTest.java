/*
 * Copyright 2013 JBoss by Red Hat.
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
package org.jbpm.executor;

import javax.enterprise.event.Event;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.executor.api.Executor;
import org.jbpm.executor.api.ExecutorQueryService;
import org.jbpm.executor.api.ExecutorRequestAdminService;
import org.jbpm.executor.entities.RequestInfo;
import org.jbpm.executor.events.listeners.DefaultExecutorEventListener;
import org.jbpm.executor.impl.ExecutorImpl;
import org.jbpm.executor.impl.ExecutorQueryServiceImpl;
import org.jbpm.executor.impl.ExecutorRequestAdminServiceImpl;
import org.jbpm.executor.impl.ExecutorRunnable;
import org.jbpm.executor.impl.ExecutorServiceEntryPointImpl;
import org.jbpm.shared.services.api.JbpmServicesPersistenceManager;
import org.jbpm.shared.services.impl.JbpmLocalTransactionManager;
import org.jbpm.shared.services.impl.JbpmServicesPersistenceManagerImpl;
import org.jbpm.shared.services.impl.events.JbpmServicesEventImpl;
import org.junit.After;
import org.junit.Before;

/**
 *
 * @author salaboy
 */
public class NoCDISimpleExecutorTest extends BasicExecutorBaseTest{
    
    public NoCDISimpleExecutorTest() {
    }
    
    
    @Before
    public void setUp() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.jbpm.executor");
        EntityManager em = emf.createEntityManager();
        
        JbpmServicesPersistenceManager pm = new JbpmServicesPersistenceManagerImpl();
        ((JbpmServicesPersistenceManagerImpl)pm).setEm(em);
        ((JbpmServicesPersistenceManagerImpl)pm).setTransactionManager(new JbpmLocalTransactionManager());        
        
        executorService = new ExecutorServiceEntryPointImpl();
        
        Event<RequestInfo> requestEvents = new JbpmServicesEventImpl<RequestInfo>();
        
        DefaultExecutorEventListener eventListener = new DefaultExecutorEventListener();
        
        ((JbpmServicesEventImpl)requestEvents).addListener(eventListener);
        
        ExecutorQueryService queryService = new ExecutorQueryServiceImpl();
        ((ExecutorQueryServiceImpl)queryService).setPm(pm);
        
        ((ExecutorServiceEntryPointImpl)executorService).setQueryService(queryService);

        Executor executor = new ExecutorImpl();
        
        ExecutorRunnable runnable = new ExecutorRunnable();
        runnable.setPm(pm);
        runnable.setQueryService(queryService);
        ((ExecutorImpl)executor).setPm(pm);
        ((ExecutorImpl)executor).setExecutorRunnable(runnable);
        ((ExecutorImpl)executor).setQueryService(queryService);
        ((ExecutorImpl)executor).setRequestEvents(requestEvents);
        
        ((ExecutorServiceEntryPointImpl)executorService).setExecutor(executor);
        
        ExecutorRequestAdminService adminService = new ExecutorRequestAdminServiceImpl();
        ((ExecutorRequestAdminServiceImpl)adminService).setPm(pm);
        ((ExecutorServiceEntryPointImpl)executorService).setAdminService(adminService);
        
        super.setUp();
    }
    
    @After
    public void tearDown() {
        super.tearDown();
    }
   
    
}