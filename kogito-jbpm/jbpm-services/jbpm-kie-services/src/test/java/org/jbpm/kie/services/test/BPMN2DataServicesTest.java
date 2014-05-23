/*
 * Copyright 2012 JBoss by Red Hat.
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
package org.jbpm.kie.services.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.kie.scanner.MavenRepository.getMavenRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.test.util.AbstractBaseTest;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.api.model.UserTaskDefinition;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.scanner.MavenRepository;


public class BPMN2DataServicesTest extends AbstractBaseTest {

    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();
    
    @Before
    public void prepare() {
    	configureServices();
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("repo/processes/hr/hiring.bpmn2");
        processes.add("repo/processes/general/customtask.bpmn");
        processes.add("repo/processes/general/humanTask.bpmn");
        processes.add("repo/processes/general/signal.bpmn");
        processes.add("repo/processes/general/import.bpmn");
        processes.add("repo/processes/general/callactivity.bpmn");
        processes.add("repo/processes/itemrefissue/itemrefissue.bpmn");
        
        InternalKieModule kJar1 = createKieJar(ks, releaseId, processes);
        File pom = new File("target/kmodule", "pom.xml");
        pom.getParentFile().mkdir();
        try {
            FileOutputStream fs = new FileOutputStream(pom);
            fs.write(getPom(releaseId).getBytes());
            fs.close();
        } catch (Exception e) {
            
        }
        MavenRepository repository = getMavenRepository();
        repository.deployArtifact(releaseId, kJar1, pom);
    }

    
    @After
    public void cleanup() {

    	cleanupSingletonSessionId();
        if (units != null && !units.isEmpty()) {
            for (DeploymentUnit unit : units) {
                deploymentService.undeploy(unit);
            }
            units.clear();
        }
        close();
    }

    @Test
    public void testHumanTaskProcess() throws IOException {
      
        assertNotNull(deploymentService);
        
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
      
        String processId = "org.jbpm.writedocument";
        

        Collection<UserTaskDefinition> processTasks = bpmn2Service.getTasksDefinitions(deploymentUnit.getIdentifier(), processId);
        
        assertEquals(3, processTasks.size());
        Map<String, String> processData = bpmn2Service.getProcessVariables(deploymentUnit.getIdentifier(), processId);
        
        assertEquals(3, processData.keySet().size());
        Map<String, String> taskInputMappings = bpmn2Service.getTaskInputMappings(deploymentUnit.getIdentifier(), processId, "Write a Document" );
        
        assertEquals(3, taskInputMappings.keySet().size());
        
        Map<String, String> taskOutputMappings = bpmn2Service.getTaskOutputMappings(deploymentUnit.getIdentifier(), processId, "Write a Document" );
        
        assertEquals(1, taskOutputMappings.keySet().size());
        
        Map<String, Collection<String>> associatedEntities = bpmn2Service.getAssociatedEntities(deploymentUnit.getIdentifier(), processId);
        
        assertEquals(3, associatedEntities.keySet().size());
        
        
    }
    
    @Test
    public void testHiringProcessData() throws IOException {
      
        assertNotNull(deploymentService);
        
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
      
        String processId = "hiring";
        

        Collection<UserTaskDefinition> processTasks = bpmn2Service.getTasksDefinitions(deploymentUnit.getIdentifier(), processId);
        
        assertEquals(4, processTasks.size());
        Map<String, String> processData = bpmn2Service.getProcessVariables(deploymentUnit.getIdentifier(), processId);
        
        assertEquals(9, processData.keySet().size());
        Map<String, String> taskInputMappings = bpmn2Service.getTaskInputMappings(deploymentUnit.getIdentifier(), processId, "HR Interview" );
        
        assertEquals(4, taskInputMappings.keySet().size());
        
        Map<String, String> taskOutputMappings = bpmn2Service.getTaskOutputMappings(deploymentUnit.getIdentifier(), processId, "HR Interview" );
        
        assertEquals(4, taskOutputMappings.keySet().size());
        
        Map<String, Collection<String>> associatedEntities = bpmn2Service.getAssociatedEntities(deploymentUnit.getIdentifier(), processId);
        
        assertEquals(4, associatedEntities.keySet().size());
        
        Map<String, String> allServiceTasks = bpmn2Service.getServiceTasks(deploymentUnit.getIdentifier(), processId);
        assertEquals(2, allServiceTasks.keySet().size());
        
        
    }
    
    @Test
    public void testFindReusableSubProcesses() {
      
        assertNotNull(deploymentService);
        
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        String theString = "ParentProcess";
        
        assertNotNull(theString);
        Collection<String> reusableProcesses = bpmn2Service.getReusableSubProcesses(deploymentUnit.getIdentifier(), theString);
        assertNotNull(reusableProcesses);
        assertEquals(1, reusableProcesses.size());
        
        assertEquals("signal", reusableProcesses.iterator().next());
    }
    
    @Test
    public void itemRefIssue(){
        assertNotNull(deploymentService);
        
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        
        String processId = "itemrefissue";
        

        Map<String, String> processData = bpmn2Service.getProcessVariables(deploymentUnit.getIdentifier(), processId);
        assertNotNull(processData);
        
    }
}
