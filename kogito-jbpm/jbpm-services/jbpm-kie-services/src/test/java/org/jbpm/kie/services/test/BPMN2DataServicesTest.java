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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.kie.scanner.KieMavenRepository.getKieMavenRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.test.util.AbstractKieServicesBaseTest;
import org.jbpm.services.api.DeploymentNotFoundException;
import org.jbpm.services.api.ProcessDefinitionNotFoundException;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.api.model.ProcessDefinition;
import org.jbpm.services.api.model.UserTaskDefinition;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.scanner.KieMavenRepository;


public class BPMN2DataServicesTest extends AbstractKieServicesBaseTest {

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
        processes.add("repo/processes/general/import.bpmn");
        processes.add("repo/processes/general/callactivity.bpmn");
        processes.add("repo/processes/general/BPMN2-UserTask.bpmn2");
        processes.add("repo/processes/itemrefissue/itemrefissue.bpmn");
        processes.add("repo/processes/general/ObjectVariableProcess.bpmn2");

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

        ProcessDefinition procDef = bpmn2Service.getProcessDefinition(deploymentUnit.getIdentifier(), processId);
        assertNotNull(procDef);

        assertEquals(procDef.getId(), "org.jbpm.writedocument");
        assertEquals(procDef.getName(), "humanTaskSample");
        assertEquals(procDef.getKnowledgeType(), "PROCESS");
        assertEquals(procDef.getPackageName(), "defaultPackage");
        assertEquals(procDef.getType(), "RuleFlow");
        assertEquals(procDef.getVersion(), "3");
        
        Map<String, String> processData = bpmn2Service.getProcessVariables(deploymentUnit.getIdentifier(), processId);
        assertEquals("String", processData.get("approval_document"));
        assertEquals("String", processData.get("approval_translatedDocument"));
        assertEquals("String", processData.get("approval_reviewComment"));

        assertEquals(3, processData.keySet().size());

        Collection<UserTaskDefinition> userTasks = bpmn2Service.getTasksDefinitions(deploymentUnit.getIdentifier(), processId);
        assertNotNull(userTasks);
        assertEquals(3, userTasks.size());

        Map<String, UserTaskDefinition> tasksByName = new HashMap<String, UserTaskDefinition>();
        for (UserTaskDefinition userTask : userTasks) {
            tasksByName.put(userTask.getName(), userTask);
        }

        assertTrue(tasksByName.containsKey("Write a Document"));
        assertTrue(tasksByName.containsKey("Translate Document"));
        assertTrue(tasksByName.containsKey("Review Document"));

        UserTaskDefinition task = tasksByName.get("Write a Document");
        assertEquals(true, task.isSkippable());
        assertEquals("Write a Document", task.getName());
        assertEquals(9, task.getPriority().intValue());
        assertEquals("Write a Document", task.getComment());
        assertEquals("Write a Document", task.getFormName());
        assertEquals("2", task.getId());

        task = tasksByName.get("Translate Document");
        assertEquals(true, task.isSkippable());
        assertEquals("Translate Document", task.getName());
        assertEquals(0, task.getPriority().intValue());
        assertEquals("", task.getComment());
        assertEquals("Translate Document", task.getFormName());
        assertEquals("4", task.getId());

        task = tasksByName.get("Review Document");
        assertEquals(false, task.isSkippable());
        assertEquals("Review Document", task.getName());
        assertEquals(0, task.getPriority().intValue());
        assertEquals("", task.getComment());
        assertEquals("Review Document", task.getFormName());
        assertEquals("5", task.getId());

        Map<String, String> taskInputMappings = bpmn2Service.getTaskInputMappings(deploymentUnit.getIdentifier(), processId, "Write a Document" );

        assertEquals(4, taskInputMappings.keySet().size());

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
        assertEquals("java.lang.String", taskInputMappings.get("TaskName"));
        assertEquals("Object", taskInputMappings.get("GroupId"));
        assertEquals("Object", taskInputMappings.get("Comment"));
        assertEquals("String", taskInputMappings.get("in_name"));

        Map<String, String> taskOutputMappings = bpmn2Service.getTaskOutputMappings(deploymentUnit.getIdentifier(), processId, "HR Interview" );

        assertEquals(4, taskOutputMappings.keySet().size());
        assertEquals("String", taskOutputMappings.get("out_name"));
        assertEquals("Integer", taskOutputMappings.get("out_age"));
        assertEquals("String", taskOutputMappings.get("out_mail"));
        assertEquals("Integer", taskOutputMappings.get("out_score"));

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

        assertEquals("org.jbpm.signal", reusableProcesses.iterator().next());
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

    @Test
    public void testHumanTaskProcessNoIO() throws IOException {

        assertNotNull(deploymentService);

        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);

        String processId = "UserTask";


        Collection<UserTaskDefinition> processTasks = bpmn2Service.getTasksDefinitions(deploymentUnit.getIdentifier(), processId);

        assertEquals(1, processTasks.size());
        Map<String, String> processData = bpmn2Service.getProcessVariables(deploymentUnit.getIdentifier(), processId);

        assertEquals(0, processData.keySet().size());
        Map<String, String> taskInputMappings = bpmn2Service.getTaskInputMappings(deploymentUnit.getIdentifier(), processId, "Hello" );

        assertEquals(0, taskInputMappings.keySet().size());

        Map<String, String> taskOutputMappings = bpmn2Service.getTaskOutputMappings(deploymentUnit.getIdentifier(), processId, "Hello" );

        assertEquals(0, taskOutputMappings.keySet().size());

        Map<String, Collection<String>> associatedEntities = bpmn2Service.getAssociatedEntities(deploymentUnit.getIdentifier(), processId);

        assertEquals(1, associatedEntities.keySet().size());

    }

    @Test
    public void testHumanTaskProcessBeforeAndAfterUndeploy() throws IOException {

        assertNotNull(deploymentService);

        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);

        String processId = "org.jbpm.writedocument";

        ProcessDefinition procDef = bpmn2Service.getProcessDefinition(deploymentUnit.getIdentifier(), processId);
        assertNotNull(procDef);

        assertEquals(procDef.getId(), "org.jbpm.writedocument");
        assertEquals(procDef.getName(), "humanTaskSample");
        assertEquals(procDef.getKnowledgeType(), "PROCESS");
        assertEquals(procDef.getPackageName(), "defaultPackage");
        assertEquals(procDef.getType(), "RuleFlow");
        assertEquals(procDef.getVersion(), "3");

        // now let's undeploy the unit
        deploymentService.undeploy(deploymentUnit);

        try {
            bpmn2Service.getProcessDefinition(deploymentUnit.getIdentifier(), processId);
            fail("DeploymentNotFoundException was not thrown");
        } catch(DeploymentNotFoundException e) {
            // expected
        }
    }

    @Test
    public void testObjectVariable() throws IOException {
        assertNotNull(deploymentService);

        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);

        String processId = "ObjectVariableProcess";

        ProcessDefinition procDef = bpmn2Service.getProcessDefinition(deploymentUnit.getIdentifier(), processId);
        assertNotNull(procDef);

        assertEquals(procDef.getId(), "ObjectVariableProcess");
        assertEquals(procDef.getName(), "ObjectVariableProcess");
        assertEquals(procDef.getKnowledgeType(), "PROCESS");
        assertEquals(procDef.getPackageName(), "defaultPackage");
        assertEquals(procDef.getType(), "RuleFlow");
        assertEquals(procDef.getVersion(), "1");

        Map<String, String> processData = bpmn2Service.getProcessVariables(deploymentUnit.getIdentifier(), processId);

        assertEquals("String", processData.get("type"));
        assertEquals("Object", processData.get("myobject"));
        assertEquals(2, processData.keySet().size());
    }

    @Test(expected=DeploymentNotFoundException.class)
    public void testGetProcessDefinitionUndeployedDeploymentUnit() throws IOException {

        assertNotNull(deploymentService);

        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID,
                                                                  ARTIFACT_ID,
                                                                  VERSION);

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        deploymentService.undeploy(deploymentUnit);
        bpmn2Service.getProcessDefinition(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    }

    @Test(expected=DeploymentNotFoundException.class)
    public void testGetProcessDefinitionInvalidDeploymenId() throws IOException {

        assertNotNull(deploymentService);

        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID,
                                                                  ARTIFACT_ID,
                                                                  VERSION);

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        bpmn2Service.getProcessDefinition("invalidid", "org.jbpm.writedocument");
    }

    @Test(expected=ProcessDefinitionNotFoundException.class)
    public void testGetProcessDefinitionInvalidProcessId() throws IOException {

        assertNotNull(deploymentService);

        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID,
                                                                  ARTIFACT_ID,
                                                                  VERSION);

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        bpmn2Service.getProcessDefinition(deploymentUnit.getIdentifier(), "org.jbpm.invalidId");

    }
}
