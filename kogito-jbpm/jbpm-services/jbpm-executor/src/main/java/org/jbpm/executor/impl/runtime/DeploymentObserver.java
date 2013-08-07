/*
 * Copyright 2013 JBoss by Red Hat.
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

package org.jbpm.executor.impl.runtime;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.jbpm.kie.services.impl.event.Deploy;
import org.jbpm.kie.services.impl.event.DeploymentEvent;
import org.jbpm.kie.services.impl.event.Undeploy;

/**
 * Receives notifications about deployed and undeployed units so they can be made available to 
 * executor service via <code>RuntimeManagerRegistry</code>
 *
 */
@ApplicationScoped
public class DeploymentObserver {
    
    /**
     * Fired when new deployment is placed in the runtime environment
     * @param event
     */
    public void addOnDeploy(@Observes @Deploy DeploymentEvent event) {
        RuntimeManagerRegistry.get().addRuntimeManager(event.getDeploymentId(), event.getDeployedUnit().getRuntimeManager());
  
    }
    
    /**
     * Fired when active deployment has been undeployed from runtime environment
     * @param event
     */
    public void removeOnUnDeploy(@Observes @Undeploy DeploymentEvent event) {
        RuntimeManagerRegistry.get().remove(event.getDeploymentId());

    }
}
