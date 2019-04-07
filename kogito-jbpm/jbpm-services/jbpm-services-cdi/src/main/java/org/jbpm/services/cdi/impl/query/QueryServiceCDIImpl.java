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

package org.jbpm.services.cdi.impl.query;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.dashbuilder.dataprovider.DataSetProviderRegistry;
import org.dashbuilder.dataset.DataSetManager;
import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.jbpm.kie.services.impl.query.QueryServiceImpl;
import org.jbpm.kie.services.impl.security.DeploymentRolesManager;
import org.jbpm.services.api.DeploymentEvent;
import org.jbpm.services.cdi.Activate;
import org.jbpm.services.cdi.Deactivate;
import org.jbpm.services.cdi.Deploy;
import org.jbpm.services.cdi.Undeploy;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.kie.api.task.UserGroupCallback;
import org.kie.internal.identity.IdentityProvider;
import org.kie.internal.runtime.cdi.BootOnLoad;

@Named("QueryServiceCDIImpl-startable")
@BootOnLoad
@ApplicationScoped
public class QueryServiceCDIImpl extends QueryServiceImpl {
    
    @Inject
    private Instance<DataSetDefRegistry> dataSetDefRegistryInstance;

    @Inject
    private Instance<DataSetManager> dataSetManagerInstance;    

    @Inject
    private Instance<DataSetProviderRegistry> providerRegistryInstance;

    @Inject
    private Instance<UserGroupCallback> userGroupCallbackInstance;

    @Inject
    @Override
    public void setIdentityProvider(IdentityProvider identityProvider) {
        super.setIdentityProvider(identityProvider);
    }

    @Inject
    @Override
    public void setCommandService(TransactionalCommandService commandService) {
        super.setCommandService(commandService);
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
    @Override
    public void init() {
        if (!dataSetManagerInstance.isUnsatisfied()) {
            setDataSetManager(dataSetManagerInstance.get());
        }
        if (!dataSetDefRegistryInstance.isUnsatisfied()) {
            setDataSetDefRegistry(dataSetDefRegistryInstance.get());
        }
        if (!providerRegistryInstance.isUnsatisfied()) {
            setProviderRegistry(providerRegistryInstance.get());
        }
        if (!userGroupCallbackInstance.isUnsatisfied()) {
            setUserGroupCallback(userGroupCallbackInstance.get());
        }
        super.init();
    }
    
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

}
