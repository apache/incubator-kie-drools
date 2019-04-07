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

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.test.util.AbstractKieServicesBaseTest;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.ejb.api.DefinitionServiceEJBRemote;
import org.jbpm.services.ejb.api.DeploymentServiceEJBRemote;
import org.jbpm.services.ejb.api.ProcessServiceEJBRemote;
import org.jbpm.services.ejb.api.RuntimeDataServiceEJBRemote;
import org.jbpm.services.ejb.api.UserTaskServiceEJBRemote;
import org.jbpm.services.ejb.client.helper.DeploymentServiceWrapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.query.QueryFilter;
import org.kie.scanner.KieMavenRepository;

import static org.junit.Assert.*;
import static org.kie.scanner.KieMavenRepository.getKieMavenRepository;

public class ClientEjbTimerServiceTest extends AbstractKieServicesBaseTest {
	
	private static final String application = "sample-war-ejb-app";
	
	private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();
    
    private static final String ARTIFACT_ID = "custom-data-project";
    private static final String GROUP_ID = "org.jbpm.test";
    private static final String VERSION = "1.0";
    
    private Long processInstanceId;
    
    @Before
    public void prepare() throws MalformedURLException {
    	
    	configureServices();
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        File kjar = new File("src/test/resources/kjar/custom-data-project-1.0.jar");
        File pom = new File("src/test/resources/kjar/pom.xml");
        KieMavenRepository repository = getKieMavenRepository();
        repository.installArtifact(releaseId, kjar, pom);
       
    }
    
    @After
    public void cleanup() {
    	if (processInstanceId != null) {
	    	// let's abort process instance to leave the system in clear state
	    	processService.abortProcessInstance(processInstanceId);
    	}
        cleanupSingletonSessionId();
        if (units != null && !units.isEmpty()) {
            for (DeploymentUnit unit : units) {
                deploymentService.undeploy(unit);
            }
            units.clear();
        }
        close();
    }

	@Override
	protected void close() {
		// do nothing
	}
	
	@Before
	public void setup() {
		configureServices();
	}

	@Override
	protected void configureServices() {
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
			throw new RuntimeException("Unable to configure services");
		}
	}
	
	@Test
    public void testStartProcess() throws InterruptedException {
    	assertNotNull(deploymentService);
        
        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
    	assertNotNull(processService);
    	
    	Map<String, Object> params = new HashMap<String, Object>();
    	params.put("expiresAt", "2s");
    	
    	long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "timer-test.timer-process", params);
    	assertNotNull(processInstanceId);
    	
    	ProcessInstance pi = processService.getProcessInstance(processInstanceId);    	
    	assertNotNull(pi);
    	
    	Thread.sleep(3000);
    	
    	pi = processService.getProcessInstance(processInstanceId);    	
    	assertNull(pi);

    }
	
	@Test
    public void testStartProcessWithHTDeadline() throws InterruptedException {
    	assertNotNull(deploymentService);
        
        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        
        deploymentService.deploy(deploymentUnit);	
        units.add(deploymentUnit);
    	assertNotNull(processService);
    	
    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "htdeadlinetest");
        
        List<TaskSummary> krisTasks = runtimeDataService.getTasksAssignedAsPotentialOwner("krisv", new QueryFilter());
        assertEquals(1, krisTasks.size());
        List<TaskSummary> johnTasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
        assertEquals(0, johnTasks.size());
        List<TaskSummary> maryTasks = runtimeDataService.getTasksAssignedAsPotentialOwner("mary", new QueryFilter());
        assertEquals(0, maryTasks.size());
        
        // now wait for 2 seconds for first reassignment
        Thread.sleep(3000);
        
        krisTasks = runtimeDataService.getTasksAssignedAsPotentialOwner("krisv", new QueryFilter());
        assertEquals(0, krisTasks.size());
        johnTasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
        assertEquals(1, johnTasks.size());
        maryTasks = runtimeDataService.getTasksAssignedAsPotentialOwner("mary", new QueryFilter());
        assertEquals(0, maryTasks.size());
        
        userTaskService.start(johnTasks.get(0).getId(), "john");
        
        // now wait for 2 more seconds for second reassignment
        Thread.sleep(2000);
        krisTasks = runtimeDataService.getTasksAssignedAsPotentialOwner("krisv", new QueryFilter());
        assertEquals(0, krisTasks.size());
        johnTasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
        assertEquals(1, johnTasks.size());
        maryTasks = runtimeDataService.getTasksAssignedAsPotentialOwner("mary", new QueryFilter());
        assertEquals(0, maryTasks.size());
        
        // now wait for 1 seconds to make sure that reassignment did not happen any more since task was already started
        Thread.sleep(3000);
        
        krisTasks = runtimeDataService.getTasksAssignedAsPotentialOwner("krisv", new QueryFilter());
        assertEquals(0, krisTasks.size());
        johnTasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
        assertEquals(0, johnTasks.size());
        maryTasks = runtimeDataService.getTasksAssignedAsPotentialOwner("mary", new QueryFilter());
        assertEquals(1, maryTasks.size());
        userTaskService.start(maryTasks.get(0).getId(), "mary");
        userTaskService.complete(maryTasks.get(0).getId(), "mary", null);
        
        // now wait for 2 seconds to make sure that reassignment did not happen any more since task was completed
        Thread.sleep(2000);
        
        ProcessInstance processInstance = processService.getProcessInstance(processInstanceId);        
        assertNull(processInstance);
        processInstanceId = null;

    }
}
