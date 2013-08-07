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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.executor.impl.ClassCacheManager;
import org.jbpm.executor.impl.ExecutorImpl;
import org.jbpm.executor.impl.ExecutorQueryServiceImpl;
import org.jbpm.executor.impl.ExecutorRequestAdminServiceImpl;
import org.jbpm.executor.impl.ExecutorRunnable;
import org.jbpm.executor.impl.ExecutorServiceImpl;
import org.jbpm.shared.services.api.JbpmServicesPersistenceManager;
import org.jbpm.shared.services.impl.JbpmLocalTransactionManager;
import org.jbpm.shared.services.impl.JbpmServicesPersistenceManagerImpl;
import org.junit.After;
import org.junit.Before;
import org.kie.internal.executor.api.Executor;
import org.kie.internal.executor.api.ExecutorQueryService;
import org.kie.internal.executor.api.ExecutorAdminService;


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
        
        executorService = new ExecutorServiceImpl();
        

        ExecutorQueryService queryService = new ExecutorQueryServiceImpl();
        ((ExecutorQueryServiceImpl)queryService).setPm(pm);
        
        ((ExecutorServiceImpl)executorService).setQueryService(queryService);

        Executor executor = new ExecutorImpl();
        ClassCacheManager classCacheManager = new ClassCacheManager();
        ExecutorRunnable runnable = new ExecutorRunnable();
        runnable.setPm(pm);
        runnable.setQueryService(queryService);
        runnable.setClassCacheManager(classCacheManager);
        ((ExecutorImpl)executor).setPm(pm);
        ((ExecutorImpl)executor).setExecutorRunnable(runnable);
        ((ExecutorImpl)executor).setQueryService(queryService);
        ((ExecutorImpl)executor).setClassCacheManager(classCacheManager);
        
        ((ExecutorServiceImpl)executorService).setExecutor(executor);
        
        ExecutorAdminService adminService = new ExecutorRequestAdminServiceImpl();
        ((ExecutorRequestAdminServiceImpl)adminService).setPm(pm);
        ((ExecutorServiceImpl)executorService).setAdminService(adminService);
        executorService.init();
        super.setUp();
    }
    
    @After
    public void tearDown() {
        super.tearDown();
        executorService.destroy();
    }
   
    
}