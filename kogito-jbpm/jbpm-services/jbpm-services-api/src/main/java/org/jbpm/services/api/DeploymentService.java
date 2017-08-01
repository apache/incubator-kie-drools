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

package org.jbpm.services.api;

import java.util.Collection;

import org.jbpm.services.api.model.DeployedUnit;
import org.jbpm.services.api.model.DeploymentUnit;
import org.kie.api.runtime.manager.RuntimeManager;

/**
 * Deploys <code>DeploymentUnit</code>s into the runtime engine by building
 * <code>RuntimeManager</code> instance for the deployment unit. Upon successful
 * deployment <code>DeployedUnit</code> instance is created and cached for further usage.
 *
 */
public interface DeploymentService {

	/**
	 * Performs deployment operation of given <code>unit</code>.
	 * @param unit deployment unit to be deployed to runtime
	 * @throws RuntimeException in case of problems encountered while deploying unit
	 */
    void deploy(DeploymentUnit unit);
    
    /**
	 * Performs undeployment operation of given <code>unit</code>.
	 * @param unit deployment unit to be undeployed from runtime
	 * @throws RuntimeException in case of problems encountered while deploying unit
	 */
    void undeploy(DeploymentUnit unit);
    
    /**
     * Returns <code>RuntimeManager</code> instance dedicated to deployment unit identified by given id 
     * @param deploymentUnitId identifier of deployment unit
     * @return <code>RuntimeManager</code> if exists for given deployment id otherwise null
     */
    RuntimeManager getRuntimeManager(String deploymentUnitId);
    
    /**
     * Returns <code>DeployedUnit</code> instance for given deployment id if exists
     * @param deploymentUnitId identifier of deployment unit
     * @return <code>DeployedUnit</code> instance if exists for given deployment id otherwise null
     */
    DeployedUnit getDeployedUnit(String deploymentUnitId);
    
    /**
     * Returns all (currently) deployed units.
     * @return collections of all existing deployed units
     */
    Collection<DeployedUnit> getDeployedUnits();
    
    /**
     * Activates given deployment by making sure it will be available for execution.
     * @param deploymentId
     */
    void activate(String deploymentId);
    
    /**
     * Deactivates given deployment by making it only available for already running instances.
     * @param deploymentId
     */
    void deactivate(String deploymentId);
    
    /**
     * Performs a check if given deployment is already active
     * @param deploymentUnitId
     * @return
     */
    boolean isDeployed(String deploymentUnitId);
}
