/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.casemgmt.impl;

import static java.util.stream.Collectors.toMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.kie.scanner.MavenRepository.getMavenRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jbpm.casemgmt.api.CaseNotFoundException;
import org.jbpm.casemgmt.api.model.AdHocFragment;
import org.jbpm.casemgmt.api.model.CaseDefinition;
import org.jbpm.casemgmt.api.model.CaseStage;
import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;
import org.jbpm.casemgmt.api.model.instance.CaseInstance;
import org.jbpm.casemgmt.api.model.instance.CaseMilestoneInstance;
import org.jbpm.casemgmt.api.model.instance.CommentInstance;
import org.jbpm.casemgmt.api.model.instance.CommentSortBy;
import org.jbpm.casemgmt.impl.objects.EchoService;
import org.jbpm.casemgmt.impl.util.AbstractCaseServicesBaseTest;
import org.jbpm.document.Document;
import org.jbpm.document.service.impl.DocumentImpl;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.api.model.NodeInstanceDesc;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.jbpm.services.task.impl.model.UserImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.query.QueryContext;
import org.kie.internal.query.QueryFilter;
import org.kie.scanner.MavenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CaseServiceImplTest extends AbstractCaseServicesBaseTest {
    
    private static final Logger logger = LoggerFactory.getLogger(CaseServiceImplTest.class);
    private static final String TEST_DOC_STORAGE = "target/docs";
    
    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();
    
    @Before
    public void prepare() {
        System.setProperty("org.jbpm.document.storage", TEST_DOC_STORAGE);
        deleteFolder(TEST_DOC_STORAGE);
        configureServices();
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("cases/EmptyCase.bpmn2");
        processes.add("cases/UserTaskCase.bpmn2");
        processes.add("cases/UserTaskWithStageCase.bpmn2");
        processes.add("cases/UserTaskWithStageCaseAutoStart.bpmn2");
        processes.add("cases/UserStageAdhocCase.bpmn2");
        // add processes that can be used by cases but are not cases themselves
        processes.add("processes/DataVerificationProcess.bpmn2");
        
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
        System.clearProperty("org.jbpm.document.storage");
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
    public void testStartEmptyCase() {
        assertNotNull(deploymentService);        
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), EMPTY_CASE_P_ID);
        assertNotNull(caseId);
        assertEquals(FIRST_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId);
            assertNotNull(cInstance);
            
            caseService.cancelCase(caseId);            
            try {
                caseService.getCaseInstance(caseId);
                fail("Case was aborted so it should not be found any more");
            } catch (CaseNotFoundException e) {
                // expected as it was aborted
            }
            caseId = null;
        } catch (Exception e) {
            logger.error("Unexpected error {}", e.getMessage(), e);
            fail("Unexpected exception " + e.getMessage());
        } finally {
            if (caseId != null) {
                caseService.cancelCase(caseId);
            }
        }
    }
    
    @Test
    public void testStartEmptyCaseAndDestroy() {
        assertNotNull(deploymentService);        
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), EMPTY_CASE_P_ID);
        assertNotNull(caseId);
        assertEquals(FIRST_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId);
            assertNotNull(cInstance);
            
            caseService.destroyCase(caseId);            
            try {
                caseService.getCaseInstance(caseId);
                fail("Case was aborted so it should not be found any more");
            } catch (CaseNotFoundException e) {
                // expected as it was aborted
            }
            caseId = null;
        } catch (Exception e) {
            logger.error("Unexpected error {}", e.getMessage(), e);
            fail("Unexpected exception " + e.getMessage());
        } finally {
            if (caseId != null) {
                caseService.cancelCase(caseId);
            }
        }
    }
    
    @Test
    public void testStartEmptyCaseWithCaseFile() {
        assertNotNull(deploymentService);        
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        Map<String, Object> data = new HashMap<>();
        data.put("name", "my first case");
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), EMPTY_CASE_P_ID, data);
        
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), EMPTY_CASE_P_ID, caseFile);
        assertNotNull(caseId);
        assertEquals(FIRST_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId, true, false, false, false);
            assertNotNull(cInstance);
            assertEquals(FIRST_CASE_ID, cInstance.getCaseId());
            assertNotNull(cInstance.getCaseFile());
            assertEquals("my first case", cInstance.getCaseFile().getData("name"));
            
            caseService.cancelCase(caseId);            
            try {
                caseService.getCaseInstance(caseId);
                fail("Case was aborted so it should not be found any more");
            } catch (CaseNotFoundException e) {
                // expected as it was aborted
            }       
            caseId = null;
        } catch (Exception e) {
            logger.error("Unexpected error {}", e.getMessage(), e);
            fail("Unexpected exception " + e.getMessage());
        } finally {
            if (caseId != null) {
                caseService.cancelCase(caseId);
            }
        }
    }
    
    @Test
    public void testStartEmptyCaseWithCaseFileAndDocument() {
        assertNotNull(deploymentService);        
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        byte[] docContent = "first case document".getBytes();
        DocumentImpl document = new DocumentImpl(UUID.randomUUID().toString(), "test case doc", docContent.length, new Date());
        document.setContent(docContent);
        
        Map<String, Object> data = new HashMap<>();
        data.put("name", "my first case");
        data.put("document", document);
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), EMPTY_CASE_P_ID, data);
        
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), EMPTY_CASE_P_ID, caseFile);
        assertNotNull(caseId);
        assertEquals(FIRST_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId, true, false, false, false);
            assertNotNull(cInstance);
            assertEquals(FIRST_CASE_ID, cInstance.getCaseId());
            assertNotNull(cInstance.getCaseFile());
            assertEquals("my first case", cInstance.getCaseFile().getData("name"));
            
            Object doc = cInstance.getCaseFile().getData("document");
            assertNotNull(doc);
            assertTrue(doc instanceof Document);
            
            Document caseDoc = (Document) doc;
            assertEquals("test case doc", caseDoc.getName());
            assertEquals(docContent.length, caseDoc.getSize());
            assertEquals(new String(docContent), new String(caseDoc.getContent()));
            
            caseService.cancelCase(caseId);            
            try {
                caseService.getCaseInstance(caseId);
                fail("Case was aborted so it should not be found any more");
            } catch (CaseNotFoundException e) {
                // expected as it was aborted
            }       
            caseId = null;
        } catch (Exception e) {
            logger.error("Unexpected error {}", e.getMessage(), e);
            fail("Unexpected exception " + e.getMessage());
        } finally {
            if (caseId != null) {
                caseService.cancelCase(caseId);
            }
        }
    }
    
    @Test
    public void testAddUserTaskToEmptyCase() {
        assertNotNull(deploymentService);        
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        Map<String, Object> data = new HashMap<>();
        data.put("name", "my first case");
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), EMPTY_CASE_P_ID, data);
        
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), EMPTY_CASE_P_ID, caseFile);
        assertNotNull(caseId);
        assertEquals(FIRST_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId);
            assertNotNull(cInstance);
            assertEquals(FIRST_CASE_ID, cInstance.getCaseId());
            
            // add dynamic user task to empty case instance - first by case id
            Map<String, Object> parameters = new HashMap<>();            
            caseService.addDynamicTask(FIRST_CASE_ID, caseService.newHumanTaskSpec("First task", "test", "john", null, parameters));
            
            List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
            assertNotNull(tasks);
            assertEquals(1, tasks.size());
            
            TaskSummary task = tasks.get(0);
            assertTask(task, "john", "First task", Status.Reserved);
            assertEquals("test", task.getDescription());
            
            String nameVar = (String)processService.getProcessInstanceVariable(task.getProcessInstanceId(), "name");
            assertNotNull(nameVar);
            assertEquals("my first case", nameVar);
            
            userTaskService.start(task.getId(), "john");
            Map<String, Object> outcome = new HashMap<>();
            outcome.put("name", "updated by dynamic task");
            userTaskService.complete(task.getId(), "john", outcome);
            
            nameVar = (String)processService.getProcessInstanceVariable(task.getProcessInstanceId(), "name");
            assertNotNull(nameVar);
            assertEquals("updated by dynamic task", nameVar);
            
            // second task add by process instance id
            Collection<ProcessInstanceDesc> caseProcessInstances = caseRuntimeDataService.getProcessInstancesForCase(caseId, new QueryContext());
            assertNotNull(caseProcessInstances);
            assertEquals(1, caseProcessInstances.size());
            
            ProcessInstanceDesc casePI = caseProcessInstances.iterator().next();
            assertNotNull(casePI);
            assertEquals(FIRST_CASE_ID, casePI.getCorrelationKey());
            
            caseService.addDynamicTask(casePI.getId(), caseService.newHumanTaskSpec("Second task", "another test", "mary", null, parameters));
            tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("mary", new QueryFilter());
            assertNotNull(tasks);
            assertEquals(1, tasks.size());
            
            task = tasks.get(0);
            assertTask(task, "mary", "Second task", Status.Reserved);
            assertEquals("another test", task.getDescription());
            
            userTaskService.start(task.getId(), "mary");
            userTaskService.complete(task.getId(), "mary", null);
            
            
            Collection<NodeInstanceDesc> nodes = runtimeDataService.getProcessInstanceHistoryCompleted(casePI.getId(), new QueryContext());
            assertNotNull(nodes);
            
            assertEquals(4, nodes.size());
            
            Map<String, String> nodesByName = nodes.stream().collect(toMap(NodeInstanceDesc::getName, NodeInstanceDesc::getNodeType));
            assertTrue(nodesByName.containsKey("StartProcess"));
            assertTrue(nodesByName.containsKey("EndProcess"));
            assertTrue(nodesByName.containsKey("[Dynamic] First task"));
            assertTrue(nodesByName.containsKey("[Dynamic] Second task"));
            
            assertEquals("StartNode", nodesByName.get("StartProcess"));
            assertEquals("EndNode", nodesByName.get("EndProcess"));
            assertEquals("Human Task", nodesByName.get("[Dynamic] First task"));
            assertEquals("Human Task", nodesByName.get("[Dynamic] Second task"));
            
        } catch (Exception e) {
            logger.error("Unexpected error {}", e.getMessage(), e);
            fail("Unexpected exception " + e.getMessage());
        } finally {
            if (caseId != null) {
                caseService.cancelCase(caseId);
            }
        }
    }
    
    @Test
    public void testAddUserTaskToCaseWithStage() {
        assertNotNull(deploymentService);        
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        Map<String, Object> data = new HashMap<>();
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), USER_TASK_CASE_P_ID, data);
        
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), USER_TASK_STAGE_CASE_P_ID, caseFile);
        assertNotNull(caseId);
        assertEquals(FIRST_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId);
            assertNotNull(cInstance);
            assertEquals(FIRST_CASE_ID, cInstance.getCaseId());
            
            CaseDefinition caseDef = caseRuntimeDataService.getCase(deploymentUnit.getIdentifier(), USER_TASK_STAGE_CASE_P_ID);
            assertNotNull(caseDef);
            assertEquals(1, caseDef.getCaseStages().size());
            
            CaseStage stage = caseDef.getCaseStages().iterator().next();
            
            // add dynamic user task to empty case instance - first by case id
            Map<String, Object> parameters = new HashMap<>();            
            caseService.addDynamicTaskToStage(FIRST_CASE_ID, stage.getId(), caseService.newHumanTaskSpec("First task", "test", "john", null, parameters));
            
            List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
            assertNotNull(tasks);
            assertEquals(1, tasks.size());
            
            TaskSummary task = tasks.get(0);
            assertTask(task, "john", "First task", Status.Reserved);
            assertEquals("test", task.getDescription());         

            // second task add by process instance id
            Collection<ProcessInstanceDesc> caseProcessInstances = caseRuntimeDataService.getProcessInstancesForCase(caseId, new QueryContext());
            assertNotNull(caseProcessInstances);
            assertEquals(1, caseProcessInstances.size());
            
            ProcessInstanceDesc casePI = caseProcessInstances.iterator().next();
            assertNotNull(casePI);
            assertEquals(FIRST_CASE_ID, casePI.getCorrelationKey());
            
            caseService.addDynamicTaskToStage(casePI.getId(), stage.getId(), caseService.newHumanTaskSpec("Second task", "another test", "mary", null, parameters));
            tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("mary", new QueryFilter());
            assertNotNull(tasks);
            assertEquals(1, tasks.size());
            task = tasks.get(0);
            assertTask(task, "mary", "Second task", Status.Reserved);
            assertEquals("another test", task.getDescription());
            
        } catch (Exception e) {
            logger.error("Unexpected error {}", e.getMessage(), e);
            fail("Unexpected exception " + e.getMessage());
        } finally {
            if (caseId != null) {
                caseService.cancelCase(caseId);
            }
        }
    }
    
    @Test
    public void testAddServiceTaskToEmptyCase() {
        assertNotNull(deploymentService);        
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        Map<String, Object> data = new HashMap<>();
        data.put("name", "my first case");
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), EMPTY_CASE_P_ID, data);
        
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), EMPTY_CASE_P_ID, caseFile);
        assertNotNull(caseId);
        assertEquals(FIRST_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId);
            assertNotNull(cInstance);
            assertEquals(FIRST_CASE_ID, cInstance.getCaseId());
            
            Collection<ProcessInstanceDesc> caseProcessInstances = caseRuntimeDataService.getProcessInstancesForCase(caseId, new QueryContext());
            assertNotNull(caseProcessInstances);
            assertEquals(1, caseProcessInstances.size());
            
            ProcessInstanceDesc casePI = caseProcessInstances.iterator().next();
            assertNotNull(casePI);
            assertEquals(FIRST_CASE_ID, casePI.getCorrelationKey());
            
            String nameVar = (String)processService.getProcessInstanceVariable(casePI.getId(), "name");
            assertNotNull(nameVar);
            assertEquals("my first case", nameVar);
            
            // add dynamic service task to empty case instance - first by case id
            Map<String, Object> parameters = new HashMap<>();  
            parameters.put("Interface", EchoService.class.getName());
            parameters.put("Operation", "echo");
            parameters.put("ParameterType", String.class.getName());
            parameters.put("Parameter", "testing dynamic service task");
            caseService.addDynamicTask(FIRST_CASE_ID, caseService.newTaskSpec("Service Task", "task 1", parameters));    
            
            nameVar = (String)processService.getProcessInstanceVariable(casePI.getId(), "name");
            assertNotNull(nameVar);
            assertEquals("testing dynamic service task echoed by service", nameVar);
            
            // second dynamic service task add by process instance id    
            parameters.put("Parameter", "testing dynamic service task 2");
            caseService.addDynamicTask(casePI.getId(), caseService.newTaskSpec("Service Task", "task 2", parameters));
                        
            nameVar = (String)processService.getProcessInstanceVariable(casePI.getId(), "name");
            assertNotNull(nameVar);
            assertEquals("testing dynamic service task 2 echoed by service", nameVar);
            
            Collection<NodeInstanceDesc> nodes = runtimeDataService.getProcessInstanceHistoryCompleted(casePI.getId(), new QueryContext());
            assertNotNull(nodes);
            
            assertEquals(4, nodes.size());
            
            Map<String, String> nodesByName = nodes.stream().collect(toMap(NodeInstanceDesc::getName, NodeInstanceDesc::getNodeType));
            assertTrue(nodesByName.containsKey("StartProcess"));
            assertTrue(nodesByName.containsKey("EndProcess"));
            assertTrue(nodesByName.containsKey("[Dynamic] task 1"));
            assertTrue(nodesByName.containsKey("[Dynamic] task 2"));
            
            assertEquals("StartNode", nodesByName.get("StartProcess"));
            assertEquals("EndNode", nodesByName.get("EndProcess"));
            assertEquals("Service Task", nodesByName.get("[Dynamic] task 1"));
            assertEquals("Service Task", nodesByName.get("[Dynamic] task 2"));
            
        } catch (Exception e) {
            logger.error("Unexpected error {}", e.getMessage(), e);
            fail("Unexpected exception " + e.getMessage());
        } finally {
            if (caseId != null) {
                caseService.cancelCase(caseId);
            }
        }
    }
    
    @Test
    public void testAddSubprocessToEmptyCase() {
        assertNotNull(deploymentService);        
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        Map<String, Object> data = new HashMap<>();
        data.put("name", "my first case");
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), EMPTY_CASE_P_ID, data);
        
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), EMPTY_CASE_P_ID, caseFile);
        assertNotNull(caseId);
        assertEquals(FIRST_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId);
            assertNotNull(cInstance);
            assertEquals(FIRST_CASE_ID, cInstance.getCaseId());
            
            // add dynamic user task to empty case instance - first by case id
            Map<String, Object> parameters = new HashMap<>();            
            caseService.addDynamicSubprocess(FIRST_CASE_ID, SUBPROCESS_P_ID, parameters);                        
            
            // second task add by process instance id
            Collection<ProcessInstanceDesc> caseProcessInstances = caseRuntimeDataService.getProcessInstancesForCase(caseId, new QueryContext());
            assertNotNull(caseProcessInstances);
            assertEquals(2, caseProcessInstances.size());
            
            ProcessInstanceDesc casePI = caseProcessInstances.iterator().next();
            assertNotNull(casePI);
            assertEquals(FIRST_CASE_ID, casePI.getCorrelationKey());
            
            caseService.addDynamicSubprocess(casePI.getId(), SUBPROCESS_P_ID, parameters);
            
            // let's verify that there are three process instances related to this case
            caseProcessInstances = caseRuntimeDataService.getProcessInstancesForCase(caseId, new QueryContext());
            assertNotNull(caseProcessInstances);
            assertEquals(3, caseProcessInstances.size());
            
        } catch (Exception e) {
            logger.error("Unexpected error {}", e.getMessage(), e);
            fail("Unexpected exception " + e.getMessage());
        } finally {
            if (caseId != null) {
                caseService.cancelCase(caseId);
            }
        }
    }
    
    @Test
    public void testAddSubprocessToCaseWithStage() {
        assertNotNull(deploymentService);        
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        Map<String, Object> data = new HashMap<>();
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), USER_TASK_CASE_P_ID, data);
        
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), USER_TASK_STAGE_CASE_P_ID, caseFile);
        assertNotNull(caseId);
        assertEquals(FIRST_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId);
            assertNotNull(cInstance);
            assertEquals(FIRST_CASE_ID, cInstance.getCaseId());
            
            CaseDefinition caseDef = caseRuntimeDataService.getCase(deploymentUnit.getIdentifier(), USER_TASK_STAGE_CASE_P_ID);
            assertNotNull(caseDef);
            assertEquals(1, caseDef.getCaseStages().size());
            
            CaseStage stage = caseDef.getCaseStages().iterator().next();
            
            // add dynamic user task to empty case instance - first by case id
            Map<String, Object> parameters = new HashMap<>();            
            caseService.addDynamicSubprocessToStage(FIRST_CASE_ID, stage.getId(), SUBPROCESS_P_ID, parameters);                        
            
            // second task add by process instance id
            Collection<ProcessInstanceDesc> caseProcessInstances = caseRuntimeDataService.getProcessInstancesForCase(caseId, new QueryContext());
            assertNotNull(caseProcessInstances);
            assertEquals(2, caseProcessInstances.size());
            
            ProcessInstanceDesc casePI = caseProcessInstances.iterator().next();
            assertNotNull(casePI);
            assertEquals(FIRST_CASE_ID, casePI.getCorrelationKey());
            
            caseService.addDynamicSubprocessToStage(casePI.getId(), stage.getId(), SUBPROCESS_P_ID, parameters);
            
            // let's verify that there are three process instances related to this case
            caseProcessInstances = caseRuntimeDataService.getProcessInstancesForCase(caseId, new QueryContext());
            assertNotNull(caseProcessInstances);
            assertEquals(3, caseProcessInstances.size());
            
        } catch (Exception e) {
            logger.error("Unexpected error {}", e.getMessage(), e);
            fail("Unexpected exception " + e.getMessage());
        } finally {
            if (caseId != null) {
                caseService.cancelCase(caseId);
            }
        }
    }
    
    @Test
    public void testTriggerTaskAndMilestoneInCase() {
        assertNotNull(deploymentService);        
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        
        Map<String, OrganizationalEntity> roleAssignments = new HashMap<>();
        roleAssignments.put("owner", new UserImpl("john"));
        
        Map<String, Object> data = new HashMap<>();
        data.put("s", "description");
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), USER_TASK_CASE_P_ID, data, roleAssignments);
        
        CaseDefinition caseDef = caseRuntimeDataService.getCase(deploymentUnit.getIdentifier(), USER_TASK_CASE_P_ID);
        assertNotNull(caseDef);
        assertEquals(3, caseDef.getAdHocFragments().size());
        Map<String, AdHocFragment> mappedFragments = mapAdHocFragments(caseDef.getAdHocFragments());
        assertTrue(mappedFragments.containsKey("Hello2"));
        assertTrue(mappedFragments.containsKey("Milestone1"));
        assertTrue(mappedFragments.containsKey("Milestone2"));
        
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), USER_TASK_CASE_P_ID, caseFile);
        assertNotNull(caseId);
        assertEquals(HR_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId);
            assertNotNull(cInstance);
            assertEquals(HR_CASE_ID, cInstance.getCaseId());
            
            List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
            assertNotNull(tasks);
            assertEquals(1, tasks.size());

            TaskSummary task = tasks.get(0);
            assertNotNull(task);
            assertEquals("Hello1", task.getName());            
            assertEquals("john", task.getActualOwnerId());
            assertEquals(Status.Reserved, task.getStatus());
                        
            // now let's trigger one (human task) fragment
            Map<String, Object> taskData = new HashMap<>();
            taskData.put("test", "value");
            taskData.put("fromVar", "#{s}");
            caseService.triggerAdHocFragment(HR_CASE_ID, "Hello2", taskData);
            
            tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
            assertNotNull(tasks);
            assertEquals(2, tasks.size());
            
            task = tasks.get(0);
            assertNotNull(task);
            assertEquals("Hello2", task.getName());            
            assertEquals("john", task.getActualOwnerId());
            assertEquals(Status.Reserved, task.getStatus());
            
            Map<String, Object> taskInputs = userTaskService.getTaskInputContentByTaskId(task.getId());
            assertNotNull(taskInputs);
            assertTrue(taskInputs.containsKey("test"));
            assertTrue(taskInputs.containsKey("fromVar"));
            assertEquals("value", taskInputs.get("test"));
            assertEquals("description", taskInputs.get("fromVar"));
            
            task = tasks.get(1);
            assertNotNull(task);
            assertEquals("Hello1", task.getName());            
            assertEquals("john", task.getActualOwnerId());
            assertEquals(Status.Reserved, task.getStatus());
            
            Collection<CaseMilestoneInstance> milestones = caseRuntimeDataService.getCaseInstanceMilestones(HR_CASE_ID, true, new QueryContext());
            assertNotNull(milestones);
            assertEquals(0, milestones.size());
            
            // trigger milestone node
            caseService.triggerAdHocFragment(HR_CASE_ID, "Milestone1", null);
            
            milestones = caseRuntimeDataService.getCaseInstanceMilestones(HR_CASE_ID, true, new QueryContext());
            assertNotNull(milestones);
            assertEquals(1, milestones.size());
            CaseMilestoneInstance msInstance = milestones.iterator().next();
            assertNotNull(msInstance);
            assertEquals("Milestone1", msInstance.getName());
            assertEquals(true, msInstance.isAchieved());
            assertNotNull(msInstance.getAchievedAt());
            
            // trigger another milestone node that has condition so it should not be achieved
            caseService.triggerAdHocFragment(HR_CASE_ID, "Milestone2", null);
            
            milestones = caseRuntimeDataService.getCaseInstanceMilestones(HR_CASE_ID, true, new QueryContext());
            assertNotNull(milestones);
            assertEquals(1, milestones.size());
            
            milestones = caseRuntimeDataService.getCaseInstanceMilestones(HR_CASE_ID, false, new QueryContext());
            assertNotNull(milestones);
            assertEquals(2, milestones.size());
            
            // add dataComplete to case file to achieve milestone
            caseService.addDataToCaseFile(HR_CASE_ID, "dataComplete", true);
            
            milestones = caseRuntimeDataService.getCaseInstanceMilestones(HR_CASE_ID, true, new QueryContext());
            assertNotNull(milestones);
            assertEquals(2, milestones.size());
        } catch (Exception e) {
            logger.error("Unexpected error {}", e.getMessage(), e);
            fail("Unexpected exception " + e.getMessage());
        } finally {
            if (caseId != null) {
                caseService.cancelCase(caseId);
            }
        }
    }
    
    @Test
    public void testCaseRolesWithDynamicTask() {
        assertNotNull(deploymentService);        
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        Map<String, OrganizationalEntity> roleAssignments = new HashMap<>();
        roleAssignments.put("owner", new UserImpl("john"));
        
        Map<String, Object> data = new HashMap<>();
        data.put("s", "description");
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), USER_TASK_CASE_P_ID, data, roleAssignments);
        
        CaseDefinition caseDef = caseRuntimeDataService.getCase(deploymentUnit.getIdentifier(), USER_TASK_CASE_P_ID);
        assertNotNull(caseDef);
        assertEquals(3, caseDef.getAdHocFragments().size());
        Map<String, AdHocFragment> mappedFragments = mapAdHocFragments(caseDef.getAdHocFragments());
        assertTrue(mappedFragments.containsKey("Hello2"));
        assertEquals("HumanTaskNode", mappedFragments.get("Hello2").getType());
        assertTrue(mappedFragments.containsKey("Milestone1"));
        assertEquals("MilestoneNode", mappedFragments.get("Milestone1").getType());
        assertTrue(mappedFragments.containsKey("Milestone2"));
        assertEquals("MilestoneNode", mappedFragments.get("Milestone2").getType());
        
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), USER_TASK_CASE_P_ID, caseFile);
        assertNotNull(caseId);
        assertEquals(HR_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId);
            assertNotNull(cInstance);
            assertEquals(HR_CASE_ID, cInstance.getCaseId());
            
            caseService.assignToCaseRole(HR_CASE_ID, "contact", new UserImpl("mary"));            
            
            List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
            assertNotNull(tasks);
            assertEquals(1, tasks.size());            
            assertTask(tasks.get(0), "john", "Hello1", Status.Reserved);
            
            caseService.addDynamicTask(HR_CASE_ID, caseService.newHumanTaskSpec("Second task", "another test", "contact", null, new HashMap<>()));
            tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("mary", new QueryFilter());
            assertNotNull(tasks);
            assertEquals(1, tasks.size());
            assertTask(tasks.get(0), "mary", "Second task", Status.Reserved);
            
            // now let's another user to contact role
            caseService.assignToCaseRole(HR_CASE_ID, "contact", new UserImpl("john"));
            
            caseService.addDynamicTask(HR_CASE_ID, caseService.newHumanTaskSpec("Third task", "another test", "contact", null, new HashMap<>()));
            tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("mary", new QueryFilter());
            assertNotNull(tasks);
            assertEquals(2, tasks.size());
            assertTask(tasks.get(0), null, "Third task", Status.Ready);     
            assertTask(tasks.get(1), "mary", "Second task", Status.Reserved);
            
        } catch (Exception e) {
            logger.error("Unexpected error {}", e.getMessage(), e);
            fail("Unexpected exception " + e.getMessage());
        } finally {
            if (caseId != null) {
                caseService.cancelCase(caseId);
            }
        }
    }
    
    @Test
    public void testCaseWithStageAutoStartNodes() {
        assertNotNull(deploymentService);        
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        Map<String, Object> data = new HashMap<>();
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), USER_TASK_STAGE_AUTO_START_CASE_P_ID, data);
        
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), USER_TASK_STAGE_AUTO_START_CASE_P_ID, caseFile);
        assertNotNull(caseId);
        assertEquals(FIRST_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId);
            assertNotNull(cInstance);
            assertEquals(FIRST_CASE_ID, cInstance.getCaseId());
        
            List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
            assertNotNull(tasks);
            assertEquals(2, tasks.size());            
            assertTask(tasks.get(0), "john", "Ask for input", Status.Reserved);     
            assertTask(tasks.get(1), "john", "Missing data", Status.Reserved);
            
        } catch (Exception e) {
            logger.error("Unexpected error {}", e.getMessage(), e);
            fail("Unexpected exception " + e.getMessage());
        } finally {
            if (caseId != null) {
                caseService.cancelCase(caseId);
            }
        }
    }
    
    @Test
    public void testCaseWithComments() {
        assertNotNull(deploymentService);        
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        Map<String, Object> data = new HashMap<>();
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), USER_TASK_STAGE_AUTO_START_CASE_P_ID, data);
        
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), USER_TASK_STAGE_AUTO_START_CASE_P_ID, caseFile);
        assertNotNull(caseId);
        assertEquals(FIRST_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId);
            assertNotNull(cInstance);
            assertEquals(FIRST_CASE_ID, cInstance.getCaseId());
            
            Collection<CommentInstance> caseComments = caseService.getCaseComments(FIRST_CASE_ID, new QueryContext());
            assertNotNull(caseComments);
            assertEquals(0, caseComments.size());
        
            caseService.addCaseComment(FIRST_CASE_ID, "poul", "just a tiny comment");
            
            caseComments = caseService.getCaseComments(FIRST_CASE_ID, new QueryContext());
            assertNotNull(caseComments);
            assertEquals(1, caseComments.size());
            
            CommentInstance comment = caseComments.iterator().next();
            assertComment(comment, "poul", "just a tiny comment");
            
            caseService.updateCaseComment(FIRST_CASE_ID, comment.getId(), comment.getAuthor(), "Updated " + comment.getComment());
            caseComments = caseService.getCaseComments(FIRST_CASE_ID, new QueryContext());
            assertNotNull(caseComments);
            assertEquals(1, caseComments.size());
            
            comment = caseComments.iterator().next();
            assertComment(comment, "poul", "Updated just a tiny comment");
            
            
            caseService.addCaseComment(FIRST_CASE_ID, "mary", "another comment");
            
            caseService.addCaseComment(FIRST_CASE_ID, "john", "third comment");
            
            caseComments = caseService.getCaseComments(FIRST_CASE_ID, new QueryContext());
            assertNotNull(caseComments);
            assertEquals(3, caseComments.size());
            
            Iterator<CommentInstance> it = caseComments.iterator();            
            assertComment(it.next(), "poul", "Updated just a tiny comment");            
            assertComment(it.next(), "mary", "another comment"); 
            assertComment(it.next(), "john", "third comment"); 
            
            
            caseComments = caseService.getCaseComments(FIRST_CASE_ID, CommentSortBy.Author, new QueryContext());
            assertNotNull(caseComments);
            assertEquals(3, caseComments.size());            
            it = caseComments.iterator();
            assertComment(it.next(), "john", "third comment"); 
            assertComment(it.next(), "mary", "another comment"); 
            assertComment(it.next(), "poul", "Updated just a tiny comment"); 
                   
            caseService.removeCaseComment(FIRST_CASE_ID, comment.getId());
            
            caseComments = caseService.getCaseComments(FIRST_CASE_ID, CommentSortBy.Author, new QueryContext());
            assertEquals(2, caseComments.size());            
            it = caseComments.iterator();
            assertComment(it.next(), "john", "third comment");  
            assertComment(it.next(), "mary", "another comment");            
            
        } catch (Exception e) {
            logger.error("Unexpected error {}", e.getMessage(), e);
            fail("Unexpected exception " + e.getMessage());
        } finally {
            if (caseId != null) {
                caseService.cancelCase(caseId);
            }
        }
    }
    
    @Test
    public void testStartCaseWithStageAndAdHocFragments() {
        assertNotNull(deploymentService);        
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        Map<String, Object> data = new HashMap<>();
        data.put("name", "my first case");
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), USER_TASK_STAGE_ADHOC_CASE_P_ID, data);
        
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), USER_TASK_STAGE_ADHOC_CASE_P_ID, caseFile);
        assertNotNull(caseId);
        assertEquals(FIRST_CASE_ID, caseId);
        try {
            assertCaseInstance(caseId, "my first case");
            
            Collection<AdHocFragment> availableFragments = caseRuntimeDataService.getAdHocFragmentsForCase(caseId);
            assertEquals(2, availableFragments.size());
            
            Map<String, AdHocFragment> mapped = mapAdHocFragments(availableFragments);
            assertEquals("HumanTaskNode", mapped.get("Adhoc 1").getType());
            assertEquals("HumanTaskNode", mapped.get("Adhoc 2").getType());
            
            List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
            assertNotNull(tasks);
            assertEquals(1, tasks.size());            
            assertTask(tasks.get(0), "john", "Initial step", Status.Reserved);
            
            userTaskService.completeAutoProgress(tasks.get(0).getId(), "john", null);
            
            availableFragments = caseRuntimeDataService.getAdHocFragmentsForCase(caseId);
            assertEquals(4, availableFragments.size());
            mapped = mapAdHocFragments(availableFragments);
            assertEquals("HumanTaskNode", mapped.get("Adhoc 1").getType());
            assertEquals("HumanTaskNode", mapped.get("Adhoc 2").getType());
            assertEquals("HumanTaskNode", mapped.get("First").getType());
            assertEquals("HumanTaskNode", mapped.get("Second").getType());
        } catch (Exception e) {
            logger.error("Unexpected error {}", e.getMessage(), e);
            fail("Unexpected exception " + e.getMessage());
        } finally {
            if (caseId != null) {
                caseService.cancelCase(caseId);
            }
        }
    }
    
    
    /*
     * Helper methods
     */
    
    protected void assertComment(CommentInstance comment, String author, String content) {
        assertNotNull(comment);
        assertEquals(author, comment.getAuthor());
        assertEquals(content, comment.getComment());
    }
    
    protected void assertTask(TaskSummary task, String actor, String name, Status status) {
        assertNotNull(task);            
        assertEquals(name, task.getName());
        assertEquals(actor, task.getActualOwnerId());
        assertEquals(status, task.getStatus());
    }
    
    protected void assertCaseInstance(String caseId, String name) {
        CaseInstance cInstance = caseService.getCaseInstance(caseId, true, false, false, false);
        assertNotNull(cInstance);
        assertEquals(caseId, cInstance.getCaseId());
        assertNotNull(cInstance.getCaseFile());
        assertEquals(name, cInstance.getCaseFile().getData("name"));
    }
}
