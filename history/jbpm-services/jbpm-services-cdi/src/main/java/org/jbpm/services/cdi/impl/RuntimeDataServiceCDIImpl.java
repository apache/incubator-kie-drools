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
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jbpm.kie.services.impl.RuntimeDataServiceImpl;
import org.jbpm.kie.services.impl.security.DeploymentRolesManager;
import org.jbpm.services.api.DeploymentEvent;
import org.jbpm.services.cdi.Activate;
import org.jbpm.services.cdi.Audit;
import org.jbpm.services.cdi.Deactivate;
import org.jbpm.services.cdi.Deploy;
import org.jbpm.services.cdi.RequestScopedBackupIdentityProvider;
import org.jbpm.services.cdi.Undeploy;
import org.jbpm.services.task.audit.service.TaskAuditService;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.kie.api.task.TaskService;
import org.kie.api.task.UserGroupCallback;
import org.kie.internal.identity.IdentityProvider;

@ApplicationScoped
public class RuntimeDataServiceCDIImpl extends RuntimeDataServiceImpl {
	
	@Inject
    private Instance<RequestScopedBackupIdentityProvider> backupProviders;

	@Override
    public void onDeploy(@Observes@Deploy DeploymentEvent event) {
        super.onDeploy(event);
    }
    
	@Override
    public void onUnDeploy(@Observes@Undeploy DeploymentEvent event) {
        super.onUnDeploy(event);
    }

    @Override
	public void onActivate(@Observes@Activate DeploymentEvent event) {
		super.onActivate(event);
	}

	@Override
	public void onDeactivate(@Observes@Deactivate DeploymentEvent event) {
		super.onDeactivate(event);
	}

	@Inject	
	@Override
	public void setCommandService(@Audit TransactionalCommandService commandService) {
		super.setCommandService(commandService);
	}

    @Inject
	@Override
	public void setIdentityProvider(IdentityProvider identityProvider) {
		super.setIdentityProvider(new IdentityProviderCDIWrapper(identityProvider, backupProviders));
	}

    @Inject
	@Override
	public void setTaskService(TaskService taskService) {
		super.setTaskService(taskService);
	}
	  
    @Inject
    @Override
    public void setTaskAuditService(TaskAuditService taskAuditService) {
        super.setTaskAuditService(taskAuditService);
    }
    
    @Inject
    @Override
    public void setDeploymentRolesManager(DeploymentRolesManager deploymentRolesManager) {
        super.setDeploymentRolesManager(deploymentRolesManager);
    }

    @Inject
    @Override
    public void setUserGroupCallback(UserGroupCallback userGroupCallback) {
        super.setUserGroupCallback(userGroupCallback);
    }

    @PostConstruct
    public void init() {
        taskAuditService.setTaskService(taskService);
    }
	    
}
