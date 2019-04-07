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

package org.jbpm.executor;


import javax.persistence.EntityManagerFactory;

import org.jbpm.executor.impl.AvailableJobsExecutor;
import org.jbpm.executor.impl.ClassCacheManager;
import org.jbpm.executor.impl.ExecutorImpl;
import org.jbpm.executor.impl.ExecutorServiceImpl;
import org.jbpm.executor.impl.event.ExecutorEventSupportImpl;
import org.jbpm.executor.impl.jpa.ExecutorQueryServiceImpl;
import org.jbpm.executor.impl.jpa.ExecutorRequestAdminServiceImpl;
import org.jbpm.executor.impl.jpa.JPAExecutorStoreService;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.kie.api.executor.Executor;
import org.kie.api.executor.ExecutorAdminService;
import org.kie.api.executor.ExecutorQueryService;
import org.kie.api.executor.ExecutorService;
import org.kie.api.executor.ExecutorStoreService;

/**
 * Creates singleton instance of <code>ExecutorService</code> that shall be used outside of CDI 
 * environment.
 */
public class ExecutorServiceFactory {

	private final static String mode = System.getProperty( "org.jbpm.cdi.executor.mode", "singleton" );

	private static ExecutorService serviceInstance;
    
    public static synchronized ExecutorService newExecutorService(EntityManagerFactory emf){
    	return newExecutorService(emf, new ExecutorEventSupportImpl());
    }
    
    public static synchronized ExecutorService newExecutorService(EntityManagerFactory emf, ExecutorEventSupportImpl eventSupport){
        if ( mode.equalsIgnoreCase( "singleton" ) ) {
            if (serviceInstance == null) {
                serviceInstance = configure(emf, new TransactionalCommandService(emf), eventSupport);
            }
            return serviceInstance;
        } else {
            return configure(emf, new TransactionalCommandService(emf), eventSupport);
        }        
    }
    
    public static synchronized ExecutorService newExecutorService(EntityManagerFactory emf, TransactionalCommandService commandService, ExecutorEventSupportImpl eventSupport){
        if ( mode.equalsIgnoreCase( "singleton" ) ) {
            if (serviceInstance == null) {
                serviceInstance = configure(emf, commandService, eventSupport);
            }
            return serviceInstance;
        } else {
            return configure(emf, commandService, eventSupport);
        }        
    }

    
    public static synchronized void resetExecutorService(ExecutorService executorService) {
    	if (executorService.equals(serviceInstance)) {
    		serviceInstance = null;
    	}
    }
    
    public static synchronized void clearExecutorService() {
        
        serviceInstance = null;
        
    }

    private static ExecutorService configure(EntityManagerFactory emf, TransactionalCommandService commandService, ExecutorEventSupportImpl eventSupport) {
        // create instances of executor services

    	ExecutorQueryService queryService = new ExecutorQueryServiceImpl(true);
    	Executor executor = new ExecutorImpl();
    	ExecutorAdminService adminService = new ExecutorRequestAdminServiceImpl();

        ExecutorStoreService storeService = new JPAExecutorStoreService(true);
        ((JPAExecutorStoreService)storeService).setCommandService(commandService);
        ((JPAExecutorStoreService)storeService).setEmf(emf);
        ((JPAExecutorStoreService)storeService).setEventSupport(eventSupport);
        
        AvailableJobsExecutor jobExecutor = new AvailableJobsExecutor();
        ClassCacheManager classCacheManager = new ClassCacheManager();               
        jobExecutor.setClassCacheManager(classCacheManager);
        jobExecutor.setQueryService(queryService);
        jobExecutor.setExecutorStoreService(storeService);
        jobExecutor.setEventSupport(eventSupport);
        jobExecutor.setExecutor(executor);
        
        ((ExecutorImpl) executor).setExecutorStoreService(storeService);
        ((ExecutorImpl) executor).setEventSupport(eventSupport);
        ((ExecutorImpl) executor).setJobProcessor(jobExecutor);
        ((ExecutorImpl) executor).setTransactionManager(commandService.getTransactionManager());
        
        // set executor on all instances that requires it
        ((ExecutorQueryServiceImpl) queryService).setCommandService(commandService);        
        ((ExecutorRequestAdminServiceImpl) adminService).setCommandService(commandService);
        ((ExecutorRequestAdminServiceImpl) adminService).setExecutor(executor);
        
        // configure services
        ExecutorService service = new ExecutorServiceImpl(executor);
    	((ExecutorServiceImpl)service).setQueryService(queryService);
    	((ExecutorServiceImpl)service).setExecutor(executor);
        ((ExecutorServiceImpl)service).setAdminService(adminService);
        ((ExecutorServiceImpl)service).setEventSupport(eventSupport);

        return service;
    }

}
