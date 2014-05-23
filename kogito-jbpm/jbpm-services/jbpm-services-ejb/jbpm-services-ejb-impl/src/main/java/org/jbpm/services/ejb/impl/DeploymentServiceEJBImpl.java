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
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.jbpm.kie.services.api.IdentityProvider;
import org.jbpm.kie.services.impl.KModuleDeploymentService;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.runtime.manager.impl.RuntimeManagerFactoryImpl;
import org.jbpm.services.api.DefinitionService;
import org.jbpm.services.api.DeploymentEventListener;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.model.DeployedUnit;
import org.jbpm.services.ejb.api.DefinitionServiceEJBLocal;
import org.jbpm.services.ejb.api.DeploymentServiceEJBLocal;
import org.jbpm.services.ejb.api.DeploymentServiceEJBRemote;
import org.jbpm.services.ejb.api.RuntimeDataServiceEJBLocal;
import org.jbpm.services.ejb.impl.identity.EJBContextIdentityProvider;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Lock(LockType.WRITE)
public class DeploymentServiceEJBImpl extends KModuleDeploymentService implements DeploymentService, DeploymentServiceEJBLocal, DeploymentServiceEJBRemote {

	@Inject
	private Instance<IdentityProvider> identityProvider;
	
	private EJBContext context;
	// inject resources
	
	@PostConstruct
    public void onInit() {
    	
		if (identityProvider.isUnsatisfied()) {
			setIdentityProvider(new EJBContextIdentityProvider(context));
		} else {
			setIdentityProvider(identityProvider.get());
		}
		setManagerFactory(new RuntimeManagerFactoryImpl());
		super.onInit();
	}
	
	@Resource
	public void setContext(EJBContext context) {
		this.context = context;
	}
	
	@PersistenceUnit(name="org.jbpm.domain")
	@Override
	public void setEmf(EntityManagerFactory emf) {
		
		super.setEmf(emf);
	}
	
	// inject ejb beans
	@EJB(beanInterface=DefinitionServiceEJBLocal.class)
	@Override
	public void setBpmn2Service(DefinitionService bpmn2Service) {
		
		super.setBpmn2Service(bpmn2Service);
	}

	@EJB(beanInterface=RuntimeDataServiceEJBLocal.class)
	@Override
	public void setRuntimeDataService(RuntimeDataService runtimeDataService) {
		
		super.setRuntimeDataService(runtimeDataService);
		super.addListener((DeploymentEventListener) runtimeDataService);

	}

	@Override
	public void deploy(String groupId, String artifactId, String version) {
		KModuleDeploymentUnit unit = new KModuleDeploymentUnit(groupId, artifactId, version);
		
		super.deploy(unit);
	}

	@Override
	public void deploy(String groupId, String artifactId, String version,
			String kbaseName, String ksessionName) {
		KModuleDeploymentUnit unit = new KModuleDeploymentUnit(groupId, artifactId, version, kbaseName, ksessionName);
		
		super.deploy(unit);
	}

	@Override
	public void deploy(String groupId, String artifactId, String version,
			String kbaseName, String ksessionName, String strategy) {
		KModuleDeploymentUnit unit = new KModuleDeploymentUnit(groupId, artifactId, version, kbaseName, ksessionName, strategy);
		
		super.deploy(unit);
		
	}

	@Override
	public void undeploy(String deploymentId) {
		DeployedUnit deployed = getDeployedUnit(deploymentId);
		if (deployed != null) {
			super.undeploy(deployed.getDeploymentUnit());
		}
	}

}
