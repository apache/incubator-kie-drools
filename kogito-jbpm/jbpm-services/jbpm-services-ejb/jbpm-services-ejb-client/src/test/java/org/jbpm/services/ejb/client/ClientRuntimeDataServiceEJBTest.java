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

package org.jbpm.services.ejb.client;

import org.jbpm.kie.services.helper.CleanUpCommand;
import org.jbpm.kie.services.test.RuntimeDataServiceImplTest;
import org.jbpm.services.ejb.api.DefinitionServiceEJBRemote;
import org.jbpm.services.ejb.api.DeploymentServiceEJBRemote;
import org.jbpm.services.ejb.api.ProcessServiceEJBRemote;
import org.jbpm.services.ejb.api.RuntimeDataServiceEJBRemote;
import org.jbpm.services.ejb.api.UserTaskServiceEJBRemote;
import org.jbpm.services.ejb.client.helper.DeploymentServiceWrapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class ClientRuntimeDataServiceEJBTest extends RuntimeDataServiceImplTest {
	
	private static final String application = "sample-war-ejb-app";
	
	@Before
    public void prepare() {
		super.prepare(); 
		userTaskService.execute(GROUP_ID +":" + ARTIFACT_ID +":" + VERSION, new CleanUpCommand());
		 
    }
	@Override
	protected void close() {
		// do nothing
		
	}

	@Override
	protected void configureServices() {
		correctUser = "anonymous";
		try {
			ClientServiceFactory factory = ServiceFactoryProvider.getProvider("JBoss");
			DeploymentServiceEJBRemote deploymentService = factory.getService(application, DeploymentServiceEJBRemote.class);
			ProcessServiceEJBRemote processService = factory.getService(application, ProcessServiceEJBRemote.class);
			RuntimeDataServiceEJBRemote runtimeDataService = factory.getService(application, RuntimeDataServiceEJBRemote.class);
			DefinitionServiceEJBRemote definitionService = factory.getService(application, DefinitionServiceEJBRemote.class);
			UserTaskServiceEJBRemote userTaskService = factory.getService(application, UserTaskServiceEJBRemote.class);
			
			setBpmn2Service(definitionService);
			setProcessService(processService);
			setRuntimeDataService(runtimeDataService);
			setUserTaskService(userTaskService);
			setDeploymentService(new DeploymentServiceWrapper(deploymentService));
			
			
		} catch (Exception e) {
			throw new RuntimeException("Unable to configure services", e);
		}
	}
	
	@Ignore("not supported for remote ejb")
	@Test
    public void testGetTasksByVariableAndValueWithTaskQueryBuilder() {
	    
	}
	
	@Ignore("not supported for remote ejb")
	@Test
    public void testGetTasksByVariableWithTaskQueryBuilder() {
	    
	}
}
