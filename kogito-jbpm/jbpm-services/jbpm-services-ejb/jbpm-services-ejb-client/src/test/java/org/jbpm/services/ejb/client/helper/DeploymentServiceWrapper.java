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

package org.jbpm.services.ejb.client.helper;

import java.util.Collection;

import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.model.DeployedUnit;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.ejb.api.DeploymentServiceEJBRemote;
import org.kie.api.runtime.manager.RuntimeManager;

public class DeploymentServiceWrapper implements DeploymentService {

	private DeploymentServiceEJBRemote remote;
	
	public DeploymentServiceWrapper(DeploymentServiceEJBRemote remote) {
		this.remote = remote;
	}
	@Override
	public void deploy(DeploymentUnit unit) {
		
		KModuleDeploymentUnit kmoduleUnit = (KModuleDeploymentUnit) unit;
		remote.deploy(kmoduleUnit.getGroupId(), kmoduleUnit.getArtifactId(), kmoduleUnit.getVersion(),
				kmoduleUnit.getKbaseName(), kmoduleUnit.getKsessionName(), kmoduleUnit.getStrategy().toString());
	}

	@Override
	public void undeploy(DeploymentUnit unit) {
		remote.undeploy(unit.getIdentifier());
	}

	@Override
	public RuntimeManager getRuntimeManager(String deploymentUnitId) {
		throw new UnsupportedOperationException("Not supported");
	}

	@Override
	public DeployedUnit getDeployedUnit(String deploymentUnitId) {
		throw new UnsupportedOperationException("Not supported");
	}

	@Override
	public Collection<DeployedUnit> getDeployedUnits() {
		throw new UnsupportedOperationException("Not supported");
	}
	@Override
	public void activate(String deploymentId) {
		remote.activate(deploymentId);
		
	}
	@Override
	public void deactivate(String deploymentId) {
		remote.deactivate(deploymentId);
	}

    @Override
    public boolean isDeployed(String deploymentUnitId) {
        return remote.isDeployed(deploymentUnitId);
    }

}
