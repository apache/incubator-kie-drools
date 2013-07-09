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

import org.jbpm.executor.api.Executor;
import org.jbpm.executor.api.ExecutorQueryService;
import org.jbpm.executor.api.ExecutorRequestAdminService;
import org.jbpm.executor.impl.ExecutorImpl;
import org.jbpm.executor.impl.ExecutorQueryServiceImpl;
import org.jbpm.executor.impl.ExecutorRequestAdminServiceImpl;
import org.jbpm.executor.impl.ExecutorRunnable;
import org.jbpm.executor.impl.ExecutorServiceEntryPointImpl;
import org.jbpm.shared.services.api.JbpmServicesPersistenceManager;
import org.jbpm.shared.services.api.JbpmServicesTransactionManager;
import org.jbpm.shared.services.impl.JbpmLocalTransactionManager;
import org.jbpm.shared.services.impl.JbpmServicesPersistenceManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author salaboy
 */
public class ExecutorServiceFactory {
    
    private static final Logger logger = LoggerFactory.getLogger(ExecutorServiceFactory.class);
    
    private static ExecutorServiceEntryPoint service = new ExecutorServiceEntryPointImpl();
    
    private static JbpmServicesPersistenceManager pm = new JbpmServicesPersistenceManagerImpl();
    
    private static EntityManagerFactory emf;
    
    private static JbpmServicesTransactionManager jbpmTransactionManager = new JbpmLocalTransactionManager();
    
    private static ExecutorQueryService queryService = new ExecutorQueryServiceImpl();
   
    private static Executor executor = new ExecutorImpl();
        
    private static ExecutorRequestAdminService adminService = new ExecutorRequestAdminServiceImpl();
    
    
    public static ExecutorServiceEntryPoint newExecutorService(){
        configure();
        return service;
    }
    
    
    

    private static void configure() {
        
        configurePersistenceManager();

        ((ExecutorQueryServiceImpl)queryService).setPm(pm);
        ((ExecutorServiceEntryPointImpl)service).setQueryService(queryService);

        configureExecutorImpl();
        ((ExecutorServiceEntryPointImpl)service).setExecutor(executor);
        
        
        ((ExecutorRequestAdminServiceImpl)adminService).setPm(pm);
        ((ExecutorServiceEntryPointImpl)service).setAdminService(adminService);
       

    }

    public static void setEmf(EntityManagerFactory emf) {
        ExecutorServiceFactory.emf = emf;
    }

    public static void setJbpmTransactionManager(JbpmServicesTransactionManager jbpmTransactionManager) {
        ExecutorServiceFactory.jbpmTransactionManager = jbpmTransactionManager;
    }

    
    public static void configurePersistenceManager(){
        EntityManager em = emf.createEntityManager();
        // Persistence and Transactions
        ((JbpmServicesPersistenceManagerImpl)pm).setEm(em);
        ((JbpmServicesPersistenceManagerImpl)pm).setTransactionManager(jbpmTransactionManager);
        
    }
     
    public static void configureExecutorImpl(){
        ExecutorRunnable runnable = new ExecutorRunnable();
        runnable.setPm(pm);
        runnable.setQueryService(queryService);
        ((ExecutorImpl)executor).setPm(pm);
        ((ExecutorImpl)executor).setExecutorRunnable(runnable);
        ((ExecutorImpl)executor).setQueryService(queryService);
        ((ExecutorImpl)executor).setRequestEvents(service.getExecutorEventListeners());
    }
     
    
}
