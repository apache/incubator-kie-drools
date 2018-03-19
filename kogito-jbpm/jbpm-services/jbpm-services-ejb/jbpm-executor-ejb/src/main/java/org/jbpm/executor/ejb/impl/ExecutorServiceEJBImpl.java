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

package org.jbpm.executor.ejb.impl;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.jbpm.executor.RequeueAware;
import org.jbpm.executor.ejb.impl.jpa.ExecutorRequestAdminServiceEJBImpl;
import org.jbpm.executor.ejb.impl.jpa.TransactionalCommandServiceExecutorEJBImpl;
import org.jbpm.executor.impl.AvailableJobsExecutor;
import org.jbpm.executor.impl.ClassCacheManager;
import org.jbpm.executor.impl.ExecutorImpl;
import org.jbpm.executor.impl.ExecutorServiceImpl;
import org.jbpm.executor.impl.event.ExecutorEventSupport;
import org.jbpm.services.ejb.api.ExecutorServiceEJB;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.kie.api.executor.ExecutorAdminService;
import org.kie.api.executor.ExecutorQueryService;
import org.kie.api.executor.ExecutorService;
import org.kie.api.executor.ExecutorStoreService;

@Singleton
@Startup
public class ExecutorServiceEJBImpl extends ExecutorServiceImpl implements ExecutorServiceEJB, ExecutorService, RequeueAware {

    private ExecutorStoreService storeService;
    private ClassCacheManager classCacheManager;
    private TransactionalCommandService transactionalCommandService;

    @EJB(beanInterface = ExecutorEventSupport.class)
    @Override
    public void setEventSupport(ExecutorEventSupport eventSupport) {
        super.setEventSupport(eventSupport);
    }

    @EJB(beanInterface=TransactionalCommandServiceExecutorEJBImpl.class)
    public void setCommandService(TransactionalCommandService commandService ) {
	    transactionalCommandService = commandService;
    }

	@PostConstruct
	@Override
	public void init() {
		ExecutorImpl executor = new ExecutorImpl();
		executor.setExecutorStoreService(this.storeService);
		executor.setEventSupport(getEventSupport());
		executor.setTransactionManager(this.transactionalCommandService.getTransactionManager());
		
		AvailableJobsExecutor jobExecutor = new AvailableJobsExecutor();             
        jobExecutor.setClassCacheManager(this.classCacheManager);
        jobExecutor.setQueryService(getQueryService());
        jobExecutor.setExecutorStoreService(this.storeService);
        jobExecutor.setEventSupport(getEventSupport());
        jobExecutor.setExecutor(executor);        
		
		executor.setJobProcessor(jobExecutor);
		
		setExecutor(executor);
		
		
		super.init();
	}

	@PreDestroy
	@Override
	public void destroy() {
		super.destroy();
	}


	@EJB
	@Override
	public void setQueryService(ExecutorQueryService queryService) {
		super.setQueryService(queryService);
	}

	@EJB(beanInterface=ExecutorRequestAdminServiceEJBImpl.class)
	@Override
	public void setAdminService(ExecutorAdminService adminService) {
		super.setAdminService(adminService);
	}
	
	@EJB
	public void setStoreService(ExecutorStoreService storeService) {
		this.storeService = storeService;
	}

	@EJB(beanInterface=ClassCacheManagerEJBImpl.class)
    public void setClassCacheManager(ClassCacheManager classCacheManager) {
        this.classCacheManager = classCacheManager;
    }
}
