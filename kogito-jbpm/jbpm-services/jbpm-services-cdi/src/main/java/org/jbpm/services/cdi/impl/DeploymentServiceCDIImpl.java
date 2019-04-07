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

package org.jbpm.services.cdi.impl;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.jbpm.kie.services.impl.FormManagerService;
import org.jbpm.kie.services.impl.KModuleDeploymentService;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.process.audit.event.AuditEventBuilder;
import org.jbpm.services.api.DefinitionService;
import org.jbpm.services.api.DeploymentEvent;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.model.DeployedUnit;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.cdi.Activate;
import org.jbpm.services.cdi.Deactivate;
import org.jbpm.services.cdi.Deploy;
import org.jbpm.services.cdi.Kjar;
import org.jbpm.services.cdi.RequestScopedBackupIdentityProvider;
import org.jbpm.services.cdi.Undeploy;
import org.jbpm.services.cdi.impl.manager.InjectableRegisterableItemsFactory;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.manager.RegisterableItemsFactory;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.executor.ExecutorService;
import org.kie.internal.identity.IdentityProvider;

@ApplicationScoped
@Kjar
public class DeploymentServiceCDIImpl extends KModuleDeploymentService {

	
    @Inject
    private BeanManager beanManager; 
    @Inject
    @Deploy
    protected Event<DeploymentEvent> deploymentEvent;
    @Inject
    @Undeploy
    protected Event<DeploymentEvent> undeploymentEvent;
    @Inject
    @Activate
    protected Event<DeploymentEvent> activateEvent;
    @Inject
    @Deactivate
    protected Event<DeploymentEvent> deactivateEvent;
    
    
    @Inject
    private Instance<RequestScopedBackupIdentityProvider> backupProviders;
    
    @PostConstruct
    public void onInit() {
    	super.onInit();
    }

    @PreDestroy
	@Override
	public void shutdown() {
		super.shutdown();
	}

	@Override
	public void notifyOnDeploy(DeploymentUnit unit, DeployedUnit deployedUnit) {
		if (deploymentEvent != null) {
            deploymentEvent.fire(new DeploymentEvent(unit.getIdentifier(), deployedUnit));
        }
	}
	@Override
	public void notifyOnUnDeploy(DeploymentUnit unit, DeployedUnit deployedUnit) {
		if (undeploymentEvent != null && deployedUnit != null) {
            undeploymentEvent.fire(new DeploymentEvent(unit.getIdentifier(), deployedUnit));
        }
	}
	
	@Override
	public void notifyOnActivate(DeploymentUnit unit, DeployedUnit deployedUnit) {
		if (activateEvent != null && deployedUnit != null) {
			activateEvent.fire(new DeploymentEvent(unit.getIdentifier(), deployedUnit));
        }
	}

	@Override
	public void notifyOnDeactivate(DeploymentUnit unit, DeployedUnit deployedUnit) {
		if (deactivateEvent != null && deployedUnit != null) {
			deactivateEvent.fire(new DeploymentEvent(unit.getIdentifier(), deployedUnit));
        }
	}

	@Inject
	@Override
	public void setBpmn2Service(DefinitionService bpmn2Service) {

		super.setBpmn2Service(bpmn2Service);
	}
	
	@Inject
	@Override
	public void setManagerFactory(RuntimeManagerFactory managerFactory) {

		super.setManagerFactory(managerFactory);
	}
	
	@Inject
    @PersistenceUnit(unitName = "org.jbpm.domain")
	@Override
	public void setEmf(EntityManagerFactory emf) {

		super.setEmf(emf);
	}
	
	@Inject
	@Override
	public void setRuntimeDataService(RuntimeDataService runtimeDataService) {

		super.setRuntimeDataService(runtimeDataService);
	}
	
	@Inject
	@Override
	public void setIdentityProvider(IdentityProvider identityProvider) {

		super.setIdentityProvider(new IdentityProviderCDIWrapper(identityProvider, backupProviders));
	}
        
    @Inject
	@Override
	public void setFormManagerService(FormManagerService formManagerService) {
		super.setFormManagerService(formManagerService);
	}
	
    @Inject	
    public void setExecutorService(Instance<ExecutorService> executorService) {
        if (!executorService.isUnsatisfied()) {
            super.setExecutorService(executorService.get());
        }
    }

    @Override
	protected RegisterableItemsFactory getRegisterableItemsFactory(AuditEventBuilder auditLoggerBuilder, KieContainer kieContainer,
			KModuleDeploymentUnit unit) {
        
        return InjectableRegisterableItemsFactory.getFactory(beanManager, auditLoggerBuilder, kieContainer,
                    unit.getKsessionName());
        
	}
}
