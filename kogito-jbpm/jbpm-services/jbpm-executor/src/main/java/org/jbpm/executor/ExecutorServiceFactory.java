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


import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;

import org.jbpm.executor.impl.AvailableJobsExecutor;
import org.jbpm.executor.impl.ClassCacheManager;
import org.jbpm.executor.impl.ExecutorImpl;
import org.jbpm.executor.impl.ExecutorRunnable;
import org.jbpm.executor.impl.ExecutorServiceImpl;
import org.jbpm.executor.impl.jpa.ExecutorQueryServiceImpl;
import org.jbpm.executor.impl.jpa.ExecutorRequestAdminServiceImpl;
import org.jbpm.executor.impl.jpa.JPAExecutorStoreService;
import org.jbpm.executor.impl.mem.InMemoryExecutorAdminServiceImpl;
import org.jbpm.executor.impl.mem.InMemoryExecutorQueryServiceImpl;
import org.jbpm.executor.impl.mem.InMemoryExecutorStoreService;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.kie.internal.executor.api.Executor;
import org.kie.internal.executor.api.ExecutorAdminService;
import org.kie.internal.executor.api.ExecutorQueryService;
import org.kie.internal.executor.api.ExecutorService;
import org.kie.internal.executor.api.ExecutorStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates singleton instance of <code>ExecutorService</code> that shall be used outside of CDI 
 * environment.
 */
public class ExecutorServiceFactory {
	
	private final static String mode = System.getProperty( "org.jbpm.cdi.executor.mode", "singleton" );
	private static final Logger logger = LoggerFactory.getLogger(ExecutorServiceFactory.class);
   
	private static ExecutorService serviceInstance;
    
    public static synchronized ExecutorService newExecutorService(EntityManagerFactory emf){
    	if ( mode.equalsIgnoreCase( "singleton" ) ) {
            if (serviceInstance == null) {
            	serviceInstance = configure(emf);
            }
            return serviceInstance;
        } else {
            return configure(emf);
        }        
    }
    
    public static synchronized ExecutorService newExecutorService(){
    	if ( mode.equalsIgnoreCase( "singleton" ) ) {
            if (serviceInstance == null) {
            	serviceInstance = configure();
            }
            return serviceInstance;
        } else {
            return configure();
        }        
    }
    
    public static synchronized void resetExecutorService(ExecutorService executorService) {
    	if (executorService.equals(serviceInstance)) {
    		serviceInstance = null;
    	}
    }

    private static ExecutorService configure(EntityManagerFactory emf) {
    	// create instances of executor services
    	
    	ExecutorQueryService queryService = new ExecutorQueryServiceImpl(true);
    	Executor executor = new ExecutorImpl();    	
    	ExecutorAdminService adminService = new ExecutorRequestAdminServiceImpl();
    	
    	// create executor for persistence handling
        TransactionalCommandService commandService = new TransactionalCommandService(emf);
        
        ExecutorStoreService storeService = new JPAExecutorStoreService(true);
        ((JPAExecutorStoreService)storeService).setCommandService(commandService);
        ((JPAExecutorStoreService)storeService).setEmf(emf);
        
        ((ExecutorImpl) executor).setExecutorStoreService(storeService);
        
        // set executor on all instances that requires it
        ((ExecutorQueryServiceImpl) queryService).setCommandService(commandService);        
        ((ExecutorRequestAdminServiceImpl) adminService).setCommandService(commandService);
        
        
        // configure services
        ExecutorService service = new ExecutorServiceImpl(executor);
    	((ExecutorServiceImpl)service).setQueryService(queryService);
    	((ExecutorServiceImpl)service).setExecutor(executor);               
        ((ExecutorServiceImpl)service).setAdminService(adminService);
         

        return service;
    }
    
    private static ExecutorService configure() {
    	// create instances of executor services
    	
    	ExecutorQueryService queryService = new InMemoryExecutorQueryServiceImpl(true);
    	Executor executor = new ExecutorImpl();    	
    	ExecutorAdminService adminService = new InMemoryExecutorAdminServiceImpl(true);
    	        
    	InMemoryExecutorStoreService storeService = new InMemoryExecutorStoreService(true);        
        
        ((ExecutorImpl) executor).setExecutorStoreService(storeService);
        
        // set executor on all instances that requires it
        ((InMemoryExecutorQueryServiceImpl) queryService).setStoreService(storeService);        
        ((InMemoryExecutorAdminServiceImpl) adminService).setStoreService(storeService);
        
        
        // configure services
        ExecutorService service = new ExecutorServiceImpl(executor);
    	((ExecutorServiceImpl)service).setQueryService(queryService);
    	((ExecutorServiceImpl)service).setExecutor(executor);               
        ((ExecutorServiceImpl)service).setAdminService(adminService);
         

        return service;
    }
    
    public static ExecutorRunnable buildRunable(EntityManagerFactory emf) {
    	ExecutorRunnable runnable = new ExecutorRunnable();
    	AvailableJobsExecutor jobExecutor = null;
    	try {
    		jobExecutor = InitialContext.doLookup("java:module/AvailableJobsExecutor");
    	} catch (Exception e) {
    		jobExecutor = new AvailableJobsExecutor();
	    	ClassCacheManager classCacheManager = new ClassCacheManager();
	    	ExecutorQueryService queryService = new ExecutorQueryServiceImpl(true);    	    	
	        
	        
	        TransactionalCommandService cmdService = new TransactionalCommandService(emf);
	        ExecutorStoreService storeService = new JPAExecutorStoreService(true);
	        ((JPAExecutorStoreService)storeService).setCommandService(cmdService);
	        ((JPAExecutorStoreService)storeService).setEmf(emf);
	        
	        ((ExecutorQueryServiceImpl) queryService).setCommandService(cmdService);       
	        jobExecutor.setClassCacheManager(classCacheManager);
	        jobExecutor.setQueryService(queryService);
	        jobExecutor.setExecutorStoreService(storeService);
	        // provide bean manager instance as context data as it might not be available to 
	        // be looked up from JNDI in non managed threads
	        try {
				Object beanManager = InitialContext.doLookup("java:comp/BeanManager");
				jobExecutor.addContextData("BeanManager", beanManager);
			} catch (NamingException ex) {
				logger.warn("No CDI BeanManager found in JNDI");
			}
    	}

        runnable.setAvailableJobsExecutor(jobExecutor);
        return runnable;
    }
    
    public static ExecutorRunnable buildRunable() {
    	ExecutorRunnable runnable = new ExecutorRunnable();
    	AvailableJobsExecutor jobExecutor = null;
    	try {
    		jobExecutor = InitialContext.doLookup("java:module/AvailableJobsExecutor");
    	} catch (Exception e) {
    		jobExecutor = new AvailableJobsExecutor();
	    	ClassCacheManager classCacheManager = new ClassCacheManager();	    	   	    
	        
	    	InMemoryExecutorStoreService storeService = new InMemoryExecutorStoreService(true);	        
	    	InMemoryExecutorQueryServiceImpl queryService = new InMemoryExecutorQueryServiceImpl(true);
	        queryService.setStoreService(storeService);
	        
	        jobExecutor.setClassCacheManager(classCacheManager);
	        jobExecutor.setQueryService(queryService);
	        jobExecutor.setExecutorStoreService(storeService);
	        // provide bean manager instance as context data as it might not be available to 
	        // be looked up from JNDI in non managed threads
	        try {
				Object beanManager = InitialContext.doLookup("java:comp/BeanManager");
				jobExecutor.addContextData("BeanManager", beanManager);
			} catch (NamingException ex) {
				logger.warn("No CDI BeanManager found in JNDI");
			}
    	}

        runnable.setAvailableJobsExecutor(jobExecutor);
        return runnable;
    }
}
