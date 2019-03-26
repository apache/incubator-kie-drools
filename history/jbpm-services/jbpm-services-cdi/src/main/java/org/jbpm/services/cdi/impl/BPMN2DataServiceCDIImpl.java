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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.jbpm.kie.services.impl.bpmn2.BPMN2DataServiceImpl;
import org.jbpm.services.api.DeploymentEvent;
import org.jbpm.services.cdi.Activate;
import org.jbpm.services.cdi.Deactivate;
import org.jbpm.services.cdi.Deploy;
import org.jbpm.services.cdi.Undeploy;

@ApplicationScoped
public class BPMN2DataServiceCDIImpl extends BPMN2DataServiceImpl {

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
