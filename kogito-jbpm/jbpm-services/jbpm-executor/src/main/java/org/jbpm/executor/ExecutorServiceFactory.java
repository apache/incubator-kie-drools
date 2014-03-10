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


import javax.persistence.EntityManagerFactory;

import org.jbpm.executor.impl.ClassCacheManager;
import org.jbpm.executor.impl.ExecutorImpl;
import org.jbpm.executor.impl.ExecutorQueryServiceImpl;
import org.jbpm.executor.impl.ExecutorRequestAdminServiceImpl;
import org.jbpm.executor.impl.ExecutorRunnable;
import org.jbpm.executor.impl.ExecutorServiceImpl;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.kie.internal.executor.api.Executor;
import org.kie.internal.executor.api.ExecutorAdminService;
import org.kie.internal.executor.api.ExecutorQueryService;
import org.kie.internal.executor.api.ExecutorService;

/**
 * Creates singleton instance of <code>ExecutorService</code> that shall be used outside of CDI 
 * environment.
 */
public class ExecutorServiceFactory {
	
	private final static String mode = System.getProperty( "org.jbpm.cdi.executor.mode", "singleton" );
   
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
    
    public static synchronized void resetExecutorService(ExecutorService executorService) {
    	if (executorService.equals(serviceInstance)) {
    		serviceInstance = null;
    	}
    }

    private static ExecutorService configure(EntityManagerFactory emf) {
    	// create instances of executor services
    	
    	ExecutorQueryService queryService = new ExecutorQueryServiceImpl();
    	Executor executor = new ExecutorImpl();    	
        ExecutorRunnable runnable = new ExecutorRunnable();
    	ExecutorAdminService adminService = new ExecutorRequestAdminServiceImpl();
    	
    	ClassCacheManager classCacheManager = new ClassCacheManager();
    	
    	// create executor for persistence handling
        TransactionalCommandService commandService = new TransactionalCommandService(emf);
        
        // set executor on all instances that requires it
        ((ExecutorQueryServiceImpl) queryService).setCommandService(commandService);
        ((ExecutorImpl) executor).setCommandService(commandService);
        ((ExecutorRequestAdminServiceImpl) adminService).setCommandService(commandService);
        ((ExecutorRunnable) runnable).setCommandService(commandService);
        
        // configure services
        ExecutorService service = new ExecutorServiceImpl(executor);
    	((ExecutorServiceImpl)service).setQueryService(queryService);
    	((ExecutorServiceImpl)service).setExecutor(executor);               
        ((ExecutorServiceImpl)service).setAdminService(adminService);
        
        runnable.setClassCacheManager(classCacheManager);
        runnable.setQueryService(queryService);

        ((ExecutorImpl)executor).setExecutorRunnable(runnable);        

        return service;
    }
}
