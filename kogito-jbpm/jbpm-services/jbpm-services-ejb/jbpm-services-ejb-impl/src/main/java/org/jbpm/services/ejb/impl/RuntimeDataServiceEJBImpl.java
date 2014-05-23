/*
 * Copyright 2014 JBoss by Red Hat.
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

package org.jbpm.services.ejb.impl;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jbpm.kie.services.api.IdentityProvider;
import org.jbpm.kie.services.impl.RuntimeDataServiceImpl;
import org.jbpm.services.api.DeploymentEvent;
import org.jbpm.services.api.DeploymentEventListener;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.ejb.TaskServiceEJBLocal;
import org.jbpm.services.ejb.api.RuntimeDataServiceEJBLocal;
import org.jbpm.services.ejb.api.RuntimeDataServiceEJBRemote;
import org.jbpm.services.ejb.impl.identity.EJBContextIdentityProvider;
import org.jbpm.services.ejb.impl.tx.TransactionalCommandServiceEJBImpl;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.kie.api.task.TaskService;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Lock(LockType.READ)
public class RuntimeDataServiceEJBImpl extends RuntimeDataServiceImpl implements DeploymentEventListener, RuntimeDataService, RuntimeDataServiceEJBLocal, RuntimeDataServiceEJBRemote {

	@Inject
	private Instance<IdentityProvider> identityProvider;
	
	@Resource
	private EJBContext context;
	// inject resources
	
	@PostConstruct
	public void configure() {
		if (identityProvider.isUnsatisfied()) {
			setIdentityProvider(new EJBContextIdentityProvider(context));
		} else {
			setIdentityProvider(identityProvider.get());
		}
	}	
	
	@EJB(beanInterface=TransactionalCommandServiceEJBImpl.class)
	@Override
	public void setCommandService(TransactionalCommandService commandService) {
		super.setCommandService(commandService);
	}


	@EJB(beanInterface=TaskServiceEJBLocal.class)
	@Override
	public void setTaskService(TaskService taskService) {
		super.setTaskService(taskService);
	}

	@Lock(LockType.WRITE)
	@Override
	public void onDeploy(DeploymentEvent event) {
		super.onDeploy(event);
	}

	@Lock(LockType.WRITE)
	@Override
	public void onUnDeploy(DeploymentEvent event) {
		super.onUnDeploy(event);
	}

}
