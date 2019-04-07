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

package org.jbpm.kie.services.test;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.test.util.AbstractKieServicesBaseTest;
import org.jbpm.services.api.ProcessInstanceNotFoundException;
import org.jbpm.services.api.model.DeploymentUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.scanner.KieMavenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;
import static org.kie.scanner.KieMavenRepository.getKieMavenRepository;

public class TxUserTaskServiceImplTest extends AbstractKieServicesBaseTest {

private static final Logger logger = LoggerFactory.getLogger(KModuleDeploymentServiceTest.class);   
    
    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();
    
    private Long processInstanceId = null;
    private KModuleDeploymentUnit deploymentUnit = null;
       
    @Before
    public void prepare() {
    	configureServices();
    	logger.debug("Preparing kjar");
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("repo/processes/tx/child-process-01.bpmn2");
        processes.add("repo/processes/tx/parent-process-01.bpmn2");
        InternalKieModule kJar1 = createKieJar(ks, releaseId, processes);
        File pom = new File("target/kmodule", "pom.xml");
        pom.getParentFile().mkdir();
        try {
            FileOutputStream fs = new FileOutputStream(pom);
            fs.write(getPom(releaseId).getBytes());
            fs.close();
        } catch (Exception e) {
            
        }
        KieMavenRepository repository = getKieMavenRepository();
        repository.installArtifact(releaseId, kJar1, pom);
        
        assertNotNull(deploymentService);
        
        deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
    	assertNotNull(processService);

    }
    
    @After
    public void cleanup() {
    	if (processInstanceId != null) {
    		try {
		    	// let's abort process instance to leave the system in clear state
		    	processService.abortProcessInstance(processInstanceId);
		    	
		    	ProcessInstance pi = processService.getProcessInstance(processInstanceId);    	
		    	assertNull(pi);
    		} catch (ProcessInstanceNotFoundException e) {
    			// ignore it as it might already be completed/aborted
    		}
    	}
        cleanupSingletonSessionId();
        if (units != null && !units.isEmpty()) {
            for (DeploymentUnit unit : units) {
            	try {
                deploymentService.undeploy(unit);
            	} catch (Exception e) {
            		// do nothing in case of some failed tests to avoid next test to fail as well
            	}
            }
            units.clear();
        }
        close();
    }       
    
    @Test
    public void testStartCompleteWithSignalInTransactions() throws Exception {
        UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
        ut.begin();
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "com.redhat.mprs.sim.parent-process-01");
        assertNotNull(processInstanceId);
        List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
        assertNotNull(taskIds);
        assertEquals(1, taskIds.size());
        
        Long taskId = taskIds.get(0);
        
        userTaskService.start(taskId, "john");
        ut.commit();
        
        ut.begin();
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("Result", "some document data");
        userTaskService.complete(taskId, "john", results);
   
        Long childProcessInstanceId = (Long) processService.getProcessInstanceVariable(processInstanceId, "childProcessInstanceId");
        processService.signalProcessInstance(childProcessInstanceId, "EVENT", null);
        
        ut.commit();
    }
}
