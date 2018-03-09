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

package org.jbpm.kie.services.impl.admin;

import static org.jbpm.services.api.query.QueryResultMapper.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.scanner.KieMavenRepository.getKieMavenRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.services.impl.query.SqlQueryDefinition;
import org.jbpm.kie.services.impl.query.mapper.RawListQueryMapper;
import org.jbpm.kie.services.impl.query.mapper.TaskSummaryQueryMapper;
import org.jbpm.kie.services.impl.query.mapper.UserTaskInstanceQueryMapper;
import org.jbpm.kie.services.test.KModuleDeploymentServiceTest;
import org.jbpm.kie.test.util.AbstractKieServicesBaseTest;
import org.jbpm.kie.test.util.CountDownListenerFactory;
import org.jbpm.services.api.ProcessInstanceNotFoundException;
import org.jbpm.services.api.TaskNotFoundException;
import org.jbpm.services.api.admin.TaskNotification;
import org.jbpm.services.api.admin.TaskReassignment;
import org.jbpm.services.api.admin.UserTaskAdminService;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.api.model.UserTaskInstanceDesc;
import org.jbpm.services.api.query.model.QueryDefinition;
import org.jbpm.services.api.query.model.QueryParam;
import org.jbpm.services.api.query.model.QueryDefinition.Target;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.query.QueryContext;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.query.QueryFilter;
import org.kie.internal.runtime.conf.ObjectModel;
import org.kie.internal.task.api.TaskModelFactory;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.model.EmailNotification;
import org.kie.internal.task.api.model.TaskEvent;
import org.kie.scanner.KieMavenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class UserTaskAdminServiceImplTest extends AbstractKieServicesBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(KModuleDeploymentServiceTest.class);
    protected static final String ADMIN_ARTIFACT_ID = "test-admin";
    protected static final String ADMIN_GROUP_ID = "org.jbpm.test";
    protected static final String ADMIN_VERSION_V1 = "1.0.0";

    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();
    
    private KModuleDeploymentUnit deploymentUnit;
    private Long processInstanceId = null;
    
    protected UserTaskAdminService userTaskAdminService;
    
    private TaskModelFactory factory = TaskModelProvider.getFactory();

    @Before
    public void prepare() {
        configureServices();
        logger.debug("Preparing kjar");
        KieServices ks = KieServices.Factory.get();
        
        // version 1 of kjar
        ReleaseId releaseId = ks.newReleaseId(ADMIN_GROUP_ID, ADMIN_ARTIFACT_ID, ADMIN_VERSION_V1);
        List<String> processes = new ArrayList<String>();
        processes.add("repo/processes/general/humanTask.bpmn"); 

        InternalKieModule kJar1 = createKieJar(ks, releaseId, processes);
        File pom = new File("target/admin", "pom.xml");
        pom.getParentFile().mkdir();
        try {
            FileOutputStream fs = new FileOutputStream(pom);
            fs.write(getPom(releaseId).getBytes());
            fs.close();
        } catch (Exception e) {

        }
        KieMavenRepository repository = getKieMavenRepository();
        repository.installArtifact(releaseId, kJar1, pom);

        userTaskAdminService = new UserTaskAdminServiceImpl();
        ((UserTaskAdminServiceImpl) userTaskAdminService).setUserTaskService(userTaskService);
        ((UserTaskAdminServiceImpl) userTaskAdminService).setRuntimeDataService(runtimeDataService);
        ((UserTaskAdminServiceImpl) userTaskAdminService).setIdentityProvider(identityProvider);
        ((UserTaskAdminServiceImpl) userTaskAdminService).setCommandService(new TransactionalCommandService(emf));
        
        // now let's deploy to runtime both kjars
        deploymentUnit = new KModuleDeploymentUnit(ADMIN_GROUP_ID, ADMIN_ARTIFACT_ID, ADMIN_VERSION_V1);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        // set user to administrator so it will be allowed to do operations
        identityProvider.setName("Administrator");
        identityProvider.setRoles(Collections.singletonList(""));
    }

    @After
    public void cleanup() {
        cleanupSingletonSessionId();
        if (processInstanceId != null) {
            try {
                // let's abort process instance to leave the system in clear state
                processService.abortProcessInstance(processInstanceId);
                
                ProcessInstance pi = processService.getProcessInstance(processInstanceId);
                Assertions.assertThat(pi).isNull();
            } catch (ProcessInstanceNotFoundException e) {
                // ignore it as it might already be completed/aborted
            }
        }
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
        CountDownListenerFactory.clear();
    }
    
    
    public void setUserTaskAdminService(UserTaskAdminService userTaskAdminService) {
        this.userTaskAdminService = userTaskAdminService;
    }
    
    @Test
    public void testAddPotentialOwnersNotBusinessAdmin() {
        identityProvider.setName("notAdmin");
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assertions.assertThat(processInstanceId).isNotNull();
        
        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(1);
        TaskSummary task = tasks.get(0);
        
        userTaskService.release(task.getId(), "salaboy");
        Assertions.assertThatThrownBy(
                () -> userTaskAdminService.addPotentialOwners(task.getId(), false, factory.newUser("john")))
                .hasMessageContaining("User notAdmin is not business admin of task 1");
    }

    @Test
    public void testAddPotentialOwnersToNonExistentTask() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assertions.assertThat(processInstanceId).isNotNull();

        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(1);
        TaskSummary task = tasks.get(0);

        userTaskService.release(task.getId(), "salaboy");
        Assertions.assertThatThrownBy(
                () -> userTaskAdminService.addPotentialOwners(15456, false, factory.newUser("john")))
                .hasMessageContaining("Task with id 15456 was not found");
    }

    @Test
    public void testAddRemovePotentialOwnersAsGroup() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assertions.assertThat(processInstanceId).isNotNull();

        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(1);
        TaskSummary task = tasks.get(0);
        userTaskService.release(task.getId(), "salaboy");

        // Forward the task to HR group (Add HR as potential owners)
        identityProvider.setRoles(Collections.singletonList("HR"));
        userTaskAdminService.addPotentialOwners(task.getId(), true, factory.newGroup("HR"));
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("katy", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(1);

        // HR has no resources to handle so lets forward it to accounting
        userTaskAdminService.removePotentialOwners(task.getId(), factory.newGroup("HR"));
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("katy", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(0);

        identityProvider.setRoles(Collections.singletonList("Accounting"));
        userTaskAdminService.addPotentialOwners(task.getId(), false, factory.newGroup("Accounting"));
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("mary", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(1);
    }

    @Test
    public void testAddPotentialOwners() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assertions.assertThat(processInstanceId).isNotNull();
        
        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(1);
        TaskSummary task = tasks.get(0);
        
        userTaskService.release(task.getId(), "salaboy");
        
        userTaskAdminService.addPotentialOwners(task.getId(), false, factory.newUser("john"));
        
        List<TaskEvent> events = runtimeDataService.getTaskEvents(task.getId(), new QueryFilter());
        Assertions.assertThat(events).hasSize(3);
        TaskEvent updatedEvent = events.get(2);
        Assertions.assertThat(updatedEvent.getMessage()).isEqualTo("Potential owners [john] have been added");
        
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(1);
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(1);
        
        userTaskAdminService.addPotentialOwners(task.getId(), true, factory.newUser("john"));
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(0);
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(1);
    }
    
    @Test
    public void testAddPotentialOwnersWrongDeploymentId() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assertions.assertThat(processInstanceId).isNotNull();
        
        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(1);
        TaskSummary task = tasks.get(0);
        
        userTaskService.release(task.getId(), "salaboy");
        
        assertThatExceptionOfType(TaskNotFoundException.class).isThrownBy(() -> { 
            userTaskAdminService.addPotentialOwners("wrong-one", task.getId(), false, factory.newUser("john")); })
        .withMessageContaining("Task with id " + task.getId() + " is not associated with wrong-one");
        
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(0);
    }
   
    @Test
    public void testAddExcludedOwners() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assertions.assertThat(processInstanceId).isNotNull();
        
        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(1);
        TaskSummary task = tasks.get(0);
        
        userTaskService.release(task.getId(), "salaboy");
        
        userTaskAdminService.addExcludedOwners(task.getId(), false, factory.newUser("salaboy"));
        List<TaskEvent> events = runtimeDataService.getTaskEvents(task.getId(), new QueryFilter());
        Assertions.assertThat(events).hasSize(3);
        TaskEvent updatedEvent = events.get(2);
        Assertions.assertThat(updatedEvent.getMessage()).isEqualTo("Excluded owners [salaboy] have been added");
        
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(0);
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(0);
        
        userTaskAdminService.addExcludedOwners(task.getId(), true, factory.newUser("john"));
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(1);
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(0);
    }
    
    @Test
    public void testAddBusinessAdmins() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assertions.assertThat(processInstanceId).isNotNull();
        
        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(1);
        TaskSummary task = tasks.get(0);
        
        userTaskService.release(task.getId(), "salaboy");
        
        tasks = runtimeDataService.getTasksAssignedAsBusinessAdministrator("salaboy", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(0);
        
        userTaskAdminService.addBusinessAdmins(task.getId(), false, factory.newUser("salaboy"));
        List<TaskEvent> events = runtimeDataService.getTaskEvents(task.getId(), new QueryFilter());
        Assertions.assertThat(events).hasSize(3);
        TaskEvent updatedEvent = events.get(2);
        Assertions.assertThat(updatedEvent.getMessage()).isEqualTo("Business administrators [salaboy] have been added");
        
        tasks = runtimeDataService.getTasksAssignedAsBusinessAdministrator("salaboy", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(1);
        tasks = runtimeDataService.getTasksAssignedAsBusinessAdministrator("Administrator", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(1);
        
        userTaskAdminService.addBusinessAdmins(task.getId(), true, factory.newUser("salaboy"));
        tasks = runtimeDataService.getTasksAssignedAsBusinessAdministrator("salaboy", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(1);
        tasks = runtimeDataService.getTasksAssignedAsBusinessAdministrator("Administrator", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(0);
    }
    
    @Test
    public void testRemovePotentialOwners() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assertions.assertThat(processInstanceId).isNotNull();
        
        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(1);
        TaskSummary task = tasks.get(0);
        
        userTaskService.release(task.getId(), "salaboy");
        
        userTaskAdminService.removePotentialOwners(task.getId(), factory.newUser("salaboy"));
        List<TaskEvent> events = runtimeDataService.getTaskEvents(task.getId(), new QueryFilter());
        Assertions.assertThat(events).hasSize(3);
        TaskEvent updatedEvent = events.get(2);
        Assertions.assertThat(updatedEvent.getMessage()).isEqualTo("Potential owners [salaboy] have been removed");
        
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(0);
    }
    
    @Test
    public void testRemoveExcludedOwners() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assertions.assertThat(processInstanceId).isNotNull();
        
        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(1);
        TaskSummary task = tasks.get(0);
        
        userTaskService.release(task.getId(), "salaboy");
        
        userTaskAdminService.addExcludedOwners(task.getId(), false, factory.newUser("salaboy"));
        List<TaskEvent> events = runtimeDataService.getTaskEvents(task.getId(), new QueryFilter());
        Assertions.assertThat(events).hasSize(3);
        TaskEvent updatedEvent = events.get(2);
        Assertions.assertThat(updatedEvent.getMessage()).isEqualTo("Excluded owners [salaboy] have been added");
        
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(0);
        
        userTaskAdminService.removeExcludedOwners(task.getId(), factory.newUser("salaboy"));
        events = runtimeDataService.getTaskEvents(task.getId(), new QueryFilter());
        Assertions.assertThat(events).hasSize(4);
        updatedEvent = events.get(3);
        Assertions.assertThat(updatedEvent.getMessage()).isEqualTo("Excluded owners [salaboy] have been removed");
        
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(1);
    }
    
    @Test
    public void testRemoveBusinessAdmin() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assertions.assertThat(processInstanceId).isNotNull();
        
        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsBusinessAdministrator("Administrator", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(1);
        TaskSummary task = tasks.get(0);
        
        userTaskAdminService.removeBusinessAdmins(task.getId(), factory.newUser("Administrator"));
        List<TaskEvent> events = runtimeDataService.getTaskEvents(task.getId(), new QueryFilter());
        Assertions.assertThat(events).hasSize(2);
        TaskEvent updatedEvent = events.get(1);
        Assertions.assertThat(updatedEvent.getMessage()).isEqualTo("Business administrators [Administrator] have been removed");

        List<Status> readyStatuses = Arrays.asList(new Status[]{
                org.kie.api.task.model.Status.Ready
        });
        
        tasks = runtimeDataService.getTasksAssignedAsBusinessAdministratorByStatus("Administrator", readyStatuses, new QueryFilter());
        Assertions.assertThat(tasks).hasSize(0);
    }
    
    @Test
    public void testAddRemoveInputData() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assertions.assertThat(processInstanceId).isNotNull();
        
        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(1);
        TaskSummary task = tasks.get(0);
        
        Map<String, Object> inputData = userTaskService.getTaskInputContentByTaskId(task.getId());
        Assertions.assertThat(inputData).doesNotContainKey("added-input");
        
        userTaskAdminService.addTaskInput(task.getId(), "added-input", "just a test");
        inputData = userTaskService.getTaskInputContentByTaskId(task.getId());
        Assertions.assertThat(inputData).containsKey("added-input");
        Assertions.assertThat(inputData.get("added-input")).isEqualTo("just a test");

        Assertions.assertThat(inputData).doesNotContainKey("added-input2");
        Assertions.assertThat(inputData).doesNotContainKey("added-input3");
        
        Map<String, Object> extra = new HashMap<>();
        extra.put("added-input2", "1");
        extra.put("added-input3", "2");
        
        userTaskAdminService.addTaskInputs(task.getId(), extra);
        inputData = userTaskService.getTaskInputContentByTaskId(task.getId());
        Assertions.assertThat(inputData).containsKey("added-input2");
        Assertions.assertThat(inputData.get("added-input2")).isEqualTo("1");
        Assertions.assertThat(inputData).containsKey("added-input3");
        Assertions.assertThat(inputData.get("added-input3")).isEqualTo("2");
        
        userTaskAdminService.removeTaskInputs(task.getId(), "added-input2", "added-input3");
        inputData = userTaskService.getTaskInputContentByTaskId(task.getId());
        Assertions.assertThat(inputData).doesNotContainKey("added-input2");
        Assertions.assertThat(inputData).doesNotContainKey("added-input3");
    }
    
    @Test
    public void testRemoveOutputData() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assertions.assertThat(processInstanceId).isNotNull();
        
        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(1);
        TaskSummary task = tasks.get(0);
        
        Map<String, Object> output = new HashMap<>();
        output.put("added-output", "draft");
        
        userTaskService.saveContent(task.getId(), output);
        
        Map<String, Object> outputData = userTaskService.getTaskOutputContentByTaskId(task.getId());
        Assertions.assertThat(outputData).containsKey("added-output");
        Assertions.assertThat(outputData.get("added-output")).isEqualTo("draft");
        
        userTaskAdminService.removeTaskOutputs(task.getId(), "added-output");
        
        outputData = userTaskService.getTaskOutputContentByTaskId(task.getId());
        Assertions.assertThat(outputData).doesNotContainKey("added-output");
    }
    
    @Test(timeout=10000)
    public void testReassignNotStarted() throws Exception {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assertions.assertThat(processInstanceId).isNotNull();
        
        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(1);
        TaskSummary task = tasks.get(0);
        
        userTaskAdminService.reassignWhenNotStarted(task.getId(), "2s", factory.newUser("john"));
        CountDownListenerFactory.getExistingTask("userTaskAdminService").waitTillCompleted();
        
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(0);
        
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(1);
    }

    @Test(timeout=10000)
    public void testReassignNotStartedInvalidTimeExpression() throws Exception {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assertions.assertThat(processInstanceId).isNotNull();

        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(1);
        TaskSummary task = tasks.get(0);

        Assertions.assertThatThrownBy(() -> {
            userTaskAdminService.reassignWhenNotStarted(task.getId(),
                                                        "2ssssssss",
                                                        factory.newUser("john"));
        })
                .hasMessage("Error parsing time string: [ 2ssssssss ]");

        Assertions.assertThatThrownBy(() -> {
            userTaskAdminService.reassignWhenNotStarted(task.getId(),
                                                        null,
                                                        factory.newUser("john"));
        })
                .hasMessage("Invalid time expression");

        Assertions.assertThatThrownBy(() -> {
            userTaskAdminService.reassignWhenNotStarted(task.getId(),
                                                          "",
                                                          factory.newUser("john"));
        })
                .hasMessage("Invalid time expression");
    }

    @Test(timeout=10000)
    public void testReassignNotStartedInvalidOrgEntities() throws Exception {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assertions.assertThat(processInstanceId).isNotNull();

        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(1);
        TaskSummary task = tasks.get(0);

        Assertions.assertThatThrownBy(() -> {
            userTaskAdminService.reassignWhenNotStarted(task.getId(),
                                                        "2s",
                                                        null);
        })
                .hasMessage("Invalid org entity");
    }
    
    @Test(timeout=10000)
    public void testReassignNotCompleted() throws Exception {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assertions.assertThat(processInstanceId).isNotNull();
        
        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(1);
        TaskSummary task = tasks.get(0);
        
        userTaskService.start(task.getId(), "salaboy");
        
        Collection<TaskReassignment> reassignments = userTaskAdminService.getTaskReassignments(task.getId(), false);
        Assertions.assertThat(reassignments).isNotNull();
        Assertions.assertThat(reassignments).hasSize(0);
        
        userTaskAdminService.reassignWhenNotCompleted(task.getId(), "2s", factory.newUser("john"));
        reassignments = userTaskAdminService.getTaskReassignments(task.getId(), true);
        Assertions.assertThat(reassignments).isNotNull();
        Assertions.assertThat(reassignments).hasSize(1);
        
        CountDownListenerFactory.getExistingTask("userTaskAdminService").waitTillCompleted();
        
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(0);

        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(1);

        reassignments = userTaskAdminService.getTaskReassignments(task.getId(), true);
        Assertions.assertThat(reassignments).isNotNull();
        Assertions.assertThat(reassignments).hasSize(0);

        reassignments = userTaskAdminService.getTaskReassignments(task.getId(), false);
        Assertions.assertThat(reassignments).isNotNull();
        Assertions.assertThat(reassignments).hasSize(1);
    }

    @Test(timeout=10000)
    public void testReassignNotCompletedInvalidTimeExpression() throws Exception {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assertions.assertThat(processInstanceId).isNotNull();

        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(1);
        TaskSummary task = tasks.get(0);

        userTaskService.start(task.getId(), "salaboy");

        Collection<TaskReassignment> reassignments = userTaskAdminService.getTaskReassignments(task.getId(), false);
        Assertions.assertThat(reassignments).isNotNull();
        Assertions.assertThat(reassignments).hasSize(0);

        Assertions.assertThatThrownBy(() -> {
            userTaskAdminService.reassignWhenNotCompleted(task.getId(),
                                                        "2ssssssss",
                                                        factory.newUser("john"));
        })
                .hasMessage("Error parsing time string: [ 2ssssssss ]");

        Assertions.assertThatThrownBy(() -> {
            userTaskAdminService.reassignWhenNotCompleted(task.getId(),
                                                        null,
                                                        factory.newUser("john"));
        })
                .hasMessage("Invalid time expression");

        Assertions.assertThatThrownBy(() -> {
            userTaskAdminService.reassignWhenNotCompleted(task.getId(),
                                                          "",
                                                          factory.newUser("john"));
        })
                .hasMessage("Invalid time expression");
    }

    @Test(timeout=10000)
    public void testReassignNotCompletedInvalidOrgEntities() throws Exception {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assertions.assertThat(processInstanceId).isNotNull();

        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(1);
        TaskSummary task = tasks.get(0);

        userTaskService.start(task.getId(), "salaboy");

        Collection<TaskReassignment> reassignments = userTaskAdminService.getTaskReassignments(task.getId(), false);
        Assertions.assertThat(reassignments).isNotNull();
        Assertions.assertThat(reassignments).hasSize(0);

        Assertions.assertThatThrownBy(() -> {
            userTaskAdminService.reassignWhenNotCompleted(task.getId(),
                                                        "2s",
                                                        null);
        })
                .hasMessage("Invalid org entity");
    }
    
    @Test(timeout=10000)
    public void testNotifyNotStarted() throws Exception {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assertions.assertThat(processInstanceId).isNotNull();
        
        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(1);
        TaskSummary task = tasks.get(0);
        
        List<OrganizationalEntity> recipients = new ArrayList<>();
        recipients.add(factory.newUser("john"));
        
        EmailNotification emailNotification = userTaskAdminService.buildEmailNotification("test", recipients, "Simple body", "Administrator", "");
        
        userTaskAdminService.notifyWhenNotStarted(task.getId(), "2s", emailNotification);
        CountDownListenerFactory.getExistingTask("userTaskAdminService").waitTillCompleted();
        
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(1);
         
    }
    
    @Test(timeout=10000)
    public void testNotifyNotCompleted() throws Exception {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assertions.assertThat(processInstanceId).isNotNull();
        
        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(1);
        TaskSummary task = tasks.get(0);
        
        Collection<TaskNotification> notifications = userTaskAdminService.getTaskNotifications(task.getId(), false);
        Assertions.assertThat(notifications).isNotNull();
        Assertions.assertThat(notifications).hasSize(0);

        userTaskService.start(task.getId(), "salaboy");
        
        List<OrganizationalEntity> recipients = new ArrayList<>();
        recipients.add(factory.newUser("john"));
        
        EmailNotification emailNotification = userTaskAdminService.buildEmailNotification("test", recipients, "Simple body", "Administrator", "");
        
        userTaskAdminService.notifyWhenNotCompleted(task.getId(), "2s", emailNotification);
        notifications = userTaskAdminService.getTaskNotifications(task.getId(), false);
        Assertions.assertThat(notifications).isNotNull();
        Assertions.assertThat(notifications).hasSize(1);
        CountDownListenerFactory.getExistingTask("userTaskAdminService").waitTillCompleted();
        
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(1);

        notifications = userTaskAdminService.getTaskNotifications(task.getId(), true);
        Assertions.assertThat(notifications).isNotNull();
        Assertions.assertThat(notifications).hasSize(0);
        
        notifications = userTaskAdminService.getTaskNotifications(task.getId(), false);
        Assertions.assertThat(notifications).isNotNull();
        Assertions.assertThat(notifications).hasSize(1);
         
    }
    
    @Test(timeout=10000)
    public void testNotifyNotStartedAndCancel() throws Exception {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assertions.assertThat(processInstanceId).isNotNull();
        
        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(1);
        TaskSummary task = tasks.get(0);
        
        Collection<TaskNotification> notifications = userTaskAdminService.getTaskNotifications(task.getId(), false);
        Assertions.assertThat(notifications).isNotNull();
        Assertions.assertThat(notifications).hasSize(0);
        
        List<OrganizationalEntity> recipients = new ArrayList<>();
        recipients.add(factory.newUser("john"));
        
        EmailNotification emailNotification = userTaskAdminService.buildEmailNotification("test", recipients, "Simple body", "Administrator", "");
        
        long notificationId = userTaskAdminService.notifyWhenNotStarted(task.getId(), "2s", emailNotification);
        notifications = userTaskAdminService.getTaskNotifications(task.getId(), true);
        Assertions.assertThat(notifications).isNotNull();
        Assertions.assertThat(notifications).hasSize(1);
        
        userTaskAdminService.cancelNotification(task.getId(), notificationId);
        
        notifications = userTaskAdminService.getTaskNotifications(task.getId(), true);
        Assertions.assertThat(notifications).isNotNull();
        Assertions.assertThat(notifications).hasSize(0);
         
    }
    
    @Test(timeout=10000)
    public void testReassignNotStartedAndCancel() throws Exception {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        Assertions.assertThat(processInstanceId).isNotNull();
        
        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        Assertions.assertThat(tasks).hasSize(1);
        TaskSummary task = tasks.get(0);
        
        Collection<TaskReassignment> reassignments = userTaskAdminService.getTaskReassignments(task.getId(), false);
        Assertions.assertThat(reassignments).isNotNull();
        Assertions.assertThat(reassignments).hasSize(0);
        
        Long reassignmentId = userTaskAdminService.reassignWhenNotStarted(task.getId(), "2s", factory.newUser("john"));
        reassignments = userTaskAdminService.getTaskReassignments(task.getId(), true);
        Assertions.assertThat(reassignments).isNotNull();
        Assertions.assertThat(reassignments).hasSize(1);
        
        userTaskAdminService.cancelReassignment(task.getId(), reassignmentId);
        
        reassignments = userTaskAdminService.getTaskReassignments(task.getId(), true);
        Assertions.assertThat(reassignments).isNotNull();
        Assertions.assertThat(reassignments).hasSize(0);
    }
    
    @Test
    public void testGetTaskInstancesAsPotOwners() {

        String PO_TASK_QUERY = "select ti.taskId, ti.activationTime, ti.actualOwner, ti.createdBy, ti.createdOn, ti.deploymentId, " + "ti.description, ti.dueDate, ti.name, ti.parentId, ti.priority, ti.processId, ti.processInstanceId, " + "ti.processSessionId, ti.status, ti.workItemId, oe.id, eo.entity_id " + "from AuditTaskImpl ti " + "left join PeopleAssignments_PotOwners po on ti.taskId = po.task_id " + "left join OrganizationalEntity oe on po.entity_id = oe.id " + " left join PeopleAssignments_ExclOwners eo on ti.taskId = eo.task_id ";
        SqlQueryDefinition query = new SqlQueryDefinition("getMyTaskInstances", "jdbc/testDS1", Target.PO_TASK);
        query.setExpression(PO_TASK_QUERY);

        queryService.registerQuery(query);

        List<QueryDefinition> queries = queryService.getQueries(new QueryContext());
        assertNotNull(queries);
        assertEquals(1, queries.size());

        QueryDefinition registeredQuery = queries.get(0);
        assertNotNull(registeredQuery);
        assertEquals(query.getName(), registeredQuery.getName());
        assertEquals(query.getSource(), registeredQuery.getSource());
        assertEquals(query.getExpression(), registeredQuery.getExpression());
        assertEquals(query.getTarget(), registeredQuery.getTarget());

        registeredQuery = queryService.getQuery(query.getName());

        assertNotNull(registeredQuery);
        assertEquals(query.getName(), registeredQuery.getName());
        assertEquals(query.getSource(), registeredQuery.getSource());
        assertEquals(query.getExpression(), registeredQuery.getExpression());
        assertEquals(query.getTarget(), registeredQuery.getTarget());

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("approval_document", "initial content");

        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument", params);
        assertNotNull(processInstanceId);
        identityProvider.setName("notvalid");

        List<UserTaskInstanceDesc> taskInstanceLogs = queryService.query(query.getName(), UserTaskInstanceQueryMapper.get(), new QueryContext());
        assertNotNull(taskInstanceLogs);
        assertEquals(0, taskInstanceLogs.size());

        identityProvider.setName("salaboy");

        taskInstanceLogs = queryService.query(query.getName(), UserTaskInstanceQueryMapper.get(), new QueryContext());
        assertNotNull(taskInstanceLogs);
        assertEquals(1, taskInstanceLogs.size());

        List<TaskSummary> taskSummaries = queryService.query(query.getName(), TaskSummaryQueryMapper.get(), new QueryContext());
        assertNotNull(taskSummaries);
        assertEquals(1, taskSummaries.size());
        
        identityProvider.setName("Administrator");
        userTaskAdminService.addPotentialOwners(taskSummaries.get(0).getId(), false, factory.newUser("john"));
        identityProvider.setName("salaboy");
        
        taskInstanceLogs = queryService.query(query.getName(), UserTaskInstanceQueryMapper.get(), new QueryContext());
        assertNotNull(taskInstanceLogs);
        assertEquals(1, taskInstanceLogs.size());

        taskSummaries = queryService.query(query.getName(), TaskSummaryQueryMapper.get(), new QueryContext());
        assertNotNull(taskSummaries);
        assertEquals(1, taskSummaries.size());
        
        QueryParam[] parameters = QueryParam.getBuilder().append(QueryParam.groupBy(COLUMN_NAME)).append(QueryParam.count(COLUMN_TASKID)).get();

        Collection<List<Object>> instances = queryService.query(query.getName(), RawListQueryMapper.get(), new QueryContext(), parameters);
        assertNotNull(instances);
        assertEquals(1, instances.size());
        
        List<Object> result = instances.iterator().next();
        assertNotNull(result);
        assertEquals(2, result.size());
        // here we have count set to 2 because group by is on name and thus it returns duplicates
        assertTrue(result.get(1) instanceof Number);
        assertEquals(2, ((Number) result.get(1)).intValue());

        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;
    }
    
    @Test
    public void testGetTaskInstancesAsPotOwnersMultipleInstances() {

        String PO_TASK_QUERY = "select ti.taskId, ti.activationTime, ti.actualOwner, ti.createdBy, ti.createdOn, ti.deploymentId, " + "ti.description, ti.dueDate, ti.name, ti.parentId, ti.priority, ti.processId, ti.processInstanceId, " + "ti.processSessionId, ti.status, ti.workItemId, oe.id, eo.entity_id " + "from AuditTaskImpl ti " + "left join PeopleAssignments_PotOwners po on ti.taskId = po.task_id " + "left join OrganizationalEntity oe on po.entity_id = oe.id " + " left join PeopleAssignments_ExclOwners eo on ti.taskId = eo.task_id ";
        SqlQueryDefinition query = new SqlQueryDefinition("getMyTaskInstances", "jdbc/testDS1", Target.PO_TASK);
        query.setExpression(PO_TASK_QUERY);

        queryService.registerQuery(query);

        List<QueryDefinition> queries = queryService.getQueries(new QueryContext());
        assertNotNull(queries);
        assertEquals(1, queries.size());

        QueryDefinition registeredQuery = queries.get(0);
        assertNotNull(registeredQuery);
        assertEquals(query.getName(), registeredQuery.getName());
        assertEquals(query.getSource(), registeredQuery.getSource());
        assertEquals(query.getExpression(), registeredQuery.getExpression());
        assertEquals(query.getTarget(), registeredQuery.getTarget());

        registeredQuery = queryService.getQuery(query.getName());

        assertNotNull(registeredQuery);
        assertEquals(query.getName(), registeredQuery.getName());
        assertEquals(query.getSource(), registeredQuery.getSource());
        assertEquals(query.getExpression(), registeredQuery.getExpression());
        assertEquals(query.getTarget(), registeredQuery.getTarget());

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("approval_document", "initial content");

        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument", params);
        assertNotNull(processInstanceId);
        Long processInstanceId2 = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument", params);
        assertNotNull(processInstanceId);
        assertNotNull(processInstanceId2);
        identityProvider.setName("notvalid");

        List<UserTaskInstanceDesc> taskInstanceLogs = queryService.query(query.getName(), UserTaskInstanceQueryMapper.get(), new QueryContext());
        assertNotNull(taskInstanceLogs);
        assertEquals(0, taskInstanceLogs.size());

        identityProvider.setName("salaboy");

        taskInstanceLogs = queryService.query(query.getName(), UserTaskInstanceQueryMapper.get(), new QueryContext());
        assertNotNull(taskInstanceLogs);
        assertEquals(2, taskInstanceLogs.size());

        identityProvider.setName("Administrator");
        userTaskAdminService.addPotentialOwners(taskInstanceLogs.get(0).getTaskId(), false, factory.newUser("john"));
        identityProvider.setName("salaboy");
        
        taskInstanceLogs = queryService.query(query.getName(), UserTaskInstanceQueryMapper.get(), new QueryContext());
        assertNotNull(taskInstanceLogs);
        assertEquals(2, taskInstanceLogs.size());

  
        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;
        processService.abortProcessInstance(processInstanceId2);
        processInstanceId2 = null;
    }
    
    /*
     * Helper methods 
     */
    @Override
    protected List<ObjectModel> getTaskListeners() {
        List<ObjectModel> listeners = super.getTaskListeners();
        
        listeners.add(new ObjectModel("mvel", "org.jbpm.kie.test.util.CountDownListenerFactory.getTask(\"userTaskAdminService\", 1)"));
        
        return listeners;
    }
    
    protected boolean createDescriptor() {
        return true;
    }
}
