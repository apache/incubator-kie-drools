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

package org.jbpm.services.ejb.impl;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.jbpm.kie.services.impl.ProcessServiceImpl;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.ejb.api.DeploymentServiceEJBLocal;
import org.jbpm.services.ejb.api.ProcessServiceEJBLocal;
import org.jbpm.services.ejb.api.ProcessServiceEJBRemote;
import org.jbpm.services.ejb.api.RuntimeDataServiceEJBLocal;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;

@Stateless
public class ProcessServiceEJBImpl extends ProcessServiceImpl implements ProcessService, ProcessServiceEJBLocal, ProcessServiceEJBRemote {

	@EJB(beanInterface=DeploymentServiceEJBLocal.class)
	@Override
	public void setDeploymentService(DeploymentService deploymentService) {
		super.setDeploymentService(deploymentService);
	}

	@EJB(beanInterface=RuntimeDataServiceEJBLocal.class)
	@Override
	public void setDataService(RuntimeDataService dataService) {
		super.setDataService(dataService);
	}

	@Override
	public <T> T process(T variables, ClassLoader cl) {
		T result = RemoteObjectProcessor.processRemoteObjects(variables, cl);
		if (result == null) {
			result = super.process(variables, cl);
		}
		
		return result;
	}

	@Override
	protected void disposeRuntimeEngine(RuntimeManager manager, RuntimeEngine engine) {
		// do nothing since this is CMT bean tx sync will dispose runtime engine
	}

	
}
