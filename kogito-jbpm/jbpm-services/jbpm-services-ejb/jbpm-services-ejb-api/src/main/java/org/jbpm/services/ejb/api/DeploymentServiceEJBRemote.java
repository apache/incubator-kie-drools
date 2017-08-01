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

package org.jbpm.services.ejb.api;

import javax.ejb.Remote;

@Remote
public interface DeploymentServiceEJBRemote  {

	/**
	 * Deploys artifact identified by given GAV (group, artifact, version) 
	 * @param groupId group id of the artifact to deploy
	 * @param artifactId artifact id of the artifact to deploy
	 * @param version version of the artifact to deploy
	 */
    void deploy(String groupId, String artifactId, String version);
    
    /**
	 * Deploys artifact identified by given GAV (group, artifact, version) with additional selection of kbase and 
	 * ksession names from kmodule.xml - these names must exists in kmodule.xml of the artifact being deployed
	 * @param groupId group id of the artifact to deploy
	 * @param artifactId artifact id of the artifact to deploy
	 * @param version version of the artifact to deploy
	 * @param kbaseName name of kie base defined in kmodule.xml
	 * @param ksessionName name of kie session defined in kmodule.xml
	 */
    void deploy(String groupId, String artifactId, String version, String kbaseName, String ksessionName);
    
    /**
	 * Deploys artifact identified by given GAV (group, artifact, version) with additional selection of kbase and 
	 * ksession names from kmodule.xml - these names must exists in kmodule.xml of the artifact being deployed and
	 * runtime strategy (singleton, per request, per process instance)
	 * @param groupId group id of the artifact to deploy
	 * @param artifactId artifact id of the artifact to deploy
	 * @param version version of the artifact to deploy
	 * @param kbaseName name of kie base defined in kmodule.xml
	 * @param ksessionName name of kie session defined in kmodule.xml
	 * @param strategy selected runtime strategy
	 */
    void deploy(String groupId, String artifactId, String version, String kbaseName, String ksessionName, String strategy);
    
    /**
     * Undeploys currently active deployment unit identified by given deploymentId
     * @param deploymentId unique identifier of the deployment
     */
    void undeploy(String deploymentId);
    
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
