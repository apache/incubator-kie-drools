/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
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
import org.jbpm.executor.impl.ExecutorImpl;
import org.jbpm.executor.impl.ExecutorServiceImpl;
import org.jbpm.services.ejb.api.ExecutorServiceEJB;
import org.kie.api.executor.ExecutorAdminService;
import org.kie.api.executor.ExecutorQueryService;
import org.kie.api.executor.ExecutorService;
import org.kie.api.executor.ExecutorStoreService;

@Singleton
@Startup
public class ExecutorServiceEJBImpl extends ExecutorServiceImpl implements ExecutorServiceEJB, ExecutorService, RequeueAware {

	private ExecutorStoreService storeService;
	
	@PostConstruct
	@Override
	public void init() {
		ExecutorImpl executor = new ExecutorImpl();
		executor.setExecutorStoreService(storeService);
		
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

}
