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

package org.jbpm.executor.cdi.impl.jpa;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.jbpm.executor.ExecutorServiceFactory;
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

@ApplicationScoped
public class JPAExecutorServiceProducer {

	@Inject
	@PersistenceUnit(unitName = "org.jbpm.domain")
	private EntityManagerFactory emf;
		
	private ExecutorEventSupportImpl eventSupport = new ExecutorEventSupportImpl();
	
	private ExecutorService service;
	
	@PostConstruct
	public void setup() {
	    service = ExecutorServiceFactory.newExecutorService(emf, eventSupport);
	}

	@Produces
	public ExecutorService produceExecutorService() {			
		
		return service;
	}
	
	@Produces
    public Executor produceExecutor() {           
        
        return ((ExecutorServiceImpl)service).getExecutor();
    }
	
	@Produces
    public ExecutorEventSupportImpl produceExecutorEventSupport() {
        
        return eventSupport;
    }

	@Produces
	public ExecutorStoreService produceStoreService() {
		ExecutorStoreService storeService = new JPAExecutorStoreService(true);
		TransactionalCommandService commandService = new TransactionalCommandService(emf);			
		((JPAExecutorStoreService) storeService).setCommandService(commandService);
		((JPAExecutorStoreService) storeService).setEmf(emf);		
		
		return storeService;
	}

	@Produces
	public ExecutorAdminService produceAdminService() {
		ExecutorAdminService adminService = new ExecutorRequestAdminServiceImpl();
		TransactionalCommandService commandService = new TransactionalCommandService(emf);				
		((ExecutorRequestAdminServiceImpl) adminService).setCommandService(commandService);
		
		return adminService;
	}

	@Produces
	public ExecutorQueryService produceQueryService() {
		ExecutorQueryService queryService = new ExecutorQueryServiceImpl(true);
		TransactionalCommandService commandService = new TransactionalCommandService(emf);		
		((ExecutorQueryServiceImpl) queryService).setCommandService(commandService);
		
		return queryService;
	}

}
