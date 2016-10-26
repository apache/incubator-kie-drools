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

package org.jbpm.kie.services.impl.admin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.kie.scanner.MavenRepository.getMavenRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.services.test.KModuleDeploymentServiceTest;
import org.jbpm.kie.test.util.AbstractKieServicesBaseTest;
import org.jbpm.kie.test.util.CountDownListenerFactory;
import org.jbpm.services.api.ProcessInstanceNotFoundException;
import org.jbpm.services.api.admin.TaskNotification;
import org.jbpm.services.api.admin.TaskReassignment;
import org.jbpm.services.api.admin.UserTaskAdminService;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.task.exception.PermissionDeniedException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.query.QueryFilter;
import org.kie.internal.runtime.conf.ObjectModel;
import org.kie.internal.task.api.TaskModelFactory;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.model.EmailNotification;
import org.kie.scanner.MavenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        MavenRepository repository = getMavenRepository();
        repository.installArtifact(releaseId, kJar1, pom);

        userTaskAdminService = new UserTaskAdminServiceImpl();
        ((UserTaskAdminServiceImpl) userTaskAdminService).setUserTaskService(userTaskService);
        ((UserTaskAdminServiceImpl) userTaskAdminService).setRuntimeDataService(runtimeDataService);
        ((UserTaskAdminServiceImpl) userTaskAdminService).setIdentityProvider(identityProvider);
        
        // now let's deploy to runtime both kjars
        deploymentUnit = new KModuleDeploymentUnit(ADMIN_GROUP_ID, ADMIN_ARTIFACT_ID, ADMIN_VERSION_V1);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        // set user to administrator so it will be allowed to do operations
        identityProvider.setName("Administrator");
    }

    @After
    public void cleanup() {
        cleanupSingletonSessionId();
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
    
    @Test(expected=PermissionDeniedException.class)
    public void testAddPotentialOwnersNotBusinessAdmin() {
        identityProvider.setName("notAdmin");
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        assertNotNull(processInstanceId);
        
        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        assertEquals(1, tasks.size());        
        TaskSummary task = tasks.get(0);
        
        userTaskService.release(task.getId(), "salaboy");
        
        userTaskAdminService.addPotentialOwners(task.getId(), false, factory.newUser("john"));
        
    }

    @Test
    public void testAddPotentialOwners() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        assertNotNull(processInstanceId);
        
        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        assertEquals(1, tasks.size());        
        TaskSummary task = tasks.get(0);
        
        userTaskService.release(task.getId(), "salaboy");
        
        userTaskAdminService.addPotentialOwners(task.getId(), false, factory.newUser("john"));
        
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        assertEquals(1, tasks.size());
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
        assertEquals(1, tasks.size());
        
        userTaskAdminService.addPotentialOwners(task.getId(), true, factory.newUser("john"));
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        assertEquals(0, tasks.size());
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
        assertEquals(1, tasks.size());
    }
   
    @Test
    public void testAddExcludedOwners() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        assertNotNull(processInstanceId);
        
        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        assertEquals(1, tasks.size());        
        TaskSummary task = tasks.get(0);
        
        userTaskService.release(task.getId(), "salaboy");
        
        userTaskAdminService.addExcludedOwners(task.getId(), false, factory.newUser("salaboy"));
        
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        assertEquals(0, tasks.size());
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
        assertEquals(0, tasks.size());
        
        userTaskAdminService.addExcludedOwners(task.getId(), true, factory.newUser("john"));
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        assertEquals(1, tasks.size());
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
        assertEquals(0, tasks.size());
    }
    
    @Test
    public void testAddBusinessAdmins() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        assertNotNull(processInstanceId);
        
        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        assertEquals(1, tasks.size());        
        TaskSummary task = tasks.get(0);
        
        userTaskService.release(task.getId(), "salaboy");
        
        tasks = runtimeDataService.getTasksAssignedAsBusinessAdministrator("salaboy", new QueryFilter());
        assertEquals(0, tasks.size()); 
        
        userTaskAdminService.addBusinessAdmins(task.getId(), false, factory.newUser("salaboy"));
        
        tasks = runtimeDataService.getTasksAssignedAsBusinessAdministrator("salaboy", new QueryFilter());
        assertEquals(1, tasks.size()); 
        tasks = runtimeDataService.getTasksAssignedAsBusinessAdministrator("Administrator", new QueryFilter());
        assertEquals(1, tasks.size());
        
        userTaskAdminService.addBusinessAdmins(task.getId(), true, factory.newUser("salaboy"));
        tasks = runtimeDataService.getTasksAssignedAsBusinessAdministrator("salaboy", new QueryFilter());
        assertEquals(1, tasks.size()); 
        tasks = runtimeDataService.getTasksAssignedAsBusinessAdministrator("Administrator", new QueryFilter());
        assertEquals(0, tasks.size());
    }
    
    @Test
    public void testRemovePotentialOwners() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        assertNotNull(processInstanceId);
        
        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        assertEquals(1, tasks.size());        
        TaskSummary task = tasks.get(0);
        
        userTaskService.release(task.getId(), "salaboy");
        
        userTaskAdminService.removePotentialOwners(task.getId(), factory.newUser("salaboy"));
        
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        assertEquals(0, tasks.size());
    }
    
    @Test
    public void testRemoveExcludedOwners() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        assertNotNull(processInstanceId);
        
        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        assertEquals(1, tasks.size());        
        TaskSummary task = tasks.get(0);
        
        userTaskService.release(task.getId(), "salaboy");
        
        userTaskAdminService.addExcludedOwners(task.getId(), false, factory.newUser("salaboy"));        
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        assertEquals(0, tasks.size());
        
        userTaskAdminService.removeExcludedOwners(task.getId(), factory.newUser("salaboy"));
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        assertEquals(1, tasks.size());
    }
    
    @Test
    public void testRemoveBusinessAdmin() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        assertNotNull(processInstanceId);
        
        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsBusinessAdministrator("Administrator", new QueryFilter());
        assertEquals(1, tasks.size());        
        TaskSummary task = tasks.get(0);              
        
        userTaskAdminService.removeBusinessAdmins(task.getId(), factory.newUser("Administrator"));
        
        tasks = runtimeDataService.getTasksAssignedAsBusinessAdministrator("Administrator", new QueryFilter());
        assertEquals(0, tasks.size()); 
    }
    
    @Test
    public void testAddRemoveInputData() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        assertNotNull(processInstanceId);
        
        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        assertEquals(1, tasks.size());        
        TaskSummary task = tasks.get(0);
        
        Map<String, Object> inputData = userTaskService.getTaskInputContentByTaskId(task.getId());
        assertFalse(inputData.containsKey("added-input"));
        
        userTaskAdminService.addTaskInput(task.getId(), "added-input", "just a test");
        inputData = userTaskService.getTaskInputContentByTaskId(task.getId());
        assertTrue(inputData.containsKey("added-input"));
        assertEquals("just a test", inputData.get("added-input"));
        
        assertFalse(inputData.containsKey("added-input2"));
        assertFalse(inputData.containsKey("added-input3"));
        
        Map<String, Object> extra = new HashMap<>();
        extra.put("added-input2", "1");
        extra.put("added-input3", "2");
        
        userTaskAdminService.addTaskInputs(task.getId(), extra);
        inputData = userTaskService.getTaskInputContentByTaskId(task.getId());
        assertTrue(inputData.containsKey("added-input2"));
        assertEquals("1", inputData.get("added-input2"));
        assertTrue(inputData.containsKey("added-input3"));
        assertEquals("2", inputData.get("added-input3"));
        
        userTaskAdminService.removeTaskInputs(task.getId(), "added-input2", "added-input3");
        inputData = userTaskService.getTaskInputContentByTaskId(task.getId());
        assertFalse(inputData.containsKey("added-input2"));
        assertFalse(inputData.containsKey("added-input3"));
    }
    
    @Test
    public void testRemoveOutputData() {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        assertNotNull(processInstanceId);
        
        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        assertEquals(1, tasks.size());        
        TaskSummary task = tasks.get(0);
        
        Map<String, Object> output = new HashMap<>();
        output.put("added-output", "draft");
        
        userTaskService.saveContent(task.getId(), output);
        
        Map<String, Object> outputData = userTaskService.getTaskOutputContentByTaskId(task.getId());
        assertTrue(outputData.containsKey("added-output"));
        assertEquals("draft", outputData.get("added-output"));
        
        userTaskAdminService.removeTaskOutputs(task.getId(), "added-output");
        
        outputData = userTaskService.getTaskOutputContentByTaskId(task.getId());
        assertFalse(outputData.containsKey("added-output"));
    }
    
    @Test(timeout=10000)
    public void testReassignNotStarted() throws Exception {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        assertNotNull(processInstanceId);
        
        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        assertEquals(1, tasks.size());        
        TaskSummary task = tasks.get(0);
        
        userTaskAdminService.reassignWhenNotStarted(task.getId(), "2s", factory.newUser("john"));
        CountDownListenerFactory.getExistingTask("userTaskAdminService").waitTillCompleted();
        
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        assertEquals(0, tasks.size()); 
        
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
        assertEquals(1, tasks.size()); 
    }
    
    @Test(timeout=10000)
    public void testReassignNotCompleted() throws Exception {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        assertNotNull(processInstanceId);
        
        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        assertEquals(1, tasks.size());        
        TaskSummary task = tasks.get(0);
        
        userTaskService.start(task.getId(), "salaboy");
        
        Collection<TaskReassignment> reassignments = userTaskAdminService.getTaskReassignments(task.getId(), false);
        assertNotNull(reassignments);
        assertEquals(0, reassignments.size());
        
        userTaskAdminService.reassignWhenNotCompleted(task.getId(), "2s", factory.newUser("john"));
        reassignments = userTaskAdminService.getTaskReassignments(task.getId(), true);
        assertNotNull(reassignments);
        assertEquals(1, reassignments.size());
        
        CountDownListenerFactory.getExistingTask("userTaskAdminService").waitTillCompleted();
        
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        assertEquals(0, tasks.size()); 
        
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
        assertEquals(1, tasks.size()); 
        
        reassignments = userTaskAdminService.getTaskReassignments(task.getId(), true);
        assertNotNull(reassignments);
        assertEquals(0, reassignments.size());
        
        reassignments = userTaskAdminService.getTaskReassignments(task.getId(), false);
        assertNotNull(reassignments);
        assertEquals(1, reassignments.size());
    }
    
    @Test(timeout=10000)
    public void testNotifyNotStarted() throws Exception {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        assertNotNull(processInstanceId);
        
        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        assertEquals(1, tasks.size());        
        TaskSummary task = tasks.get(0);
        
        List<OrganizationalEntity> recipients = new ArrayList<>();
        recipients.add(factory.newUser("john"));
        
        EmailNotification emailNotification = userTaskAdminService.buildEmailNotification("test", recipients, "Simple body", "Administrator", "");
        
        userTaskAdminService.notifyWhenNotStarted(task.getId(), "2s", emailNotification);
        CountDownListenerFactory.getExistingTask("userTaskAdminService").waitTillCompleted();
        
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        assertEquals(1, tasks.size()); 
         
    }
    
    @Test(timeout=10000)
    public void testNotifyNotCompleted() throws Exception {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        assertNotNull(processInstanceId);
        
        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        assertEquals(1, tasks.size());        
        TaskSummary task = tasks.get(0);
        
        Collection<TaskNotification> notifications = userTaskAdminService.getTaskNotifications(task.getId(), false);
        assertNotNull(notifications);
        assertEquals(0, notifications.size());
        
        userTaskService.start(task.getId(), "salaboy");
        
        List<OrganizationalEntity> recipients = new ArrayList<>();
        recipients.add(factory.newUser("john"));
        
        EmailNotification emailNotification = userTaskAdminService.buildEmailNotification("test", recipients, "Simple body", "Administrator", "");
        
        userTaskAdminService.notifyWhenNotCompleted(task.getId(), "2s", emailNotification);
        notifications = userTaskAdminService.getTaskNotifications(task.getId(), false);
        assertNotNull(notifications);
        assertEquals(1, notifications.size());
        CountDownListenerFactory.getExistingTask("userTaskAdminService").waitTillCompleted();
        
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        assertEquals(1, tasks.size()); 
        
        notifications = userTaskAdminService.getTaskNotifications(task.getId(), true);
        assertNotNull(notifications);
        assertEquals(0, notifications.size());
        
        notifications = userTaskAdminService.getTaskNotifications(task.getId(), false);
        assertNotNull(notifications);
        assertEquals(1, notifications.size());
         
    }
    
    @Test(timeout=10000)
    public void testNotifyNotStartedAndCancel() throws Exception {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        assertNotNull(processInstanceId);
        
        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        assertEquals(1, tasks.size());        
        TaskSummary task = tasks.get(0);
        
        Collection<TaskNotification> notifications = userTaskAdminService.getTaskNotifications(task.getId(), false);
        assertNotNull(notifications);
        assertEquals(0, notifications.size());
        
        List<OrganizationalEntity> recipients = new ArrayList<>();
        recipients.add(factory.newUser("john"));
        
        EmailNotification emailNotification = userTaskAdminService.buildEmailNotification("test", recipients, "Simple body", "Administrator", "");
        
        long notificationId = userTaskAdminService.notifyWhenNotStarted(task.getId(), "2s", emailNotification);
        notifications = userTaskAdminService.getTaskNotifications(task.getId(), true);
        assertNotNull(notifications);
        assertEquals(1, notifications.size());
        
        userTaskAdminService.cancelNotification(task.getId(), notificationId);
        
        notifications = userTaskAdminService.getTaskNotifications(task.getId(), true);
        assertNotNull(notifications);
        assertEquals(0, notifications.size());
         
    }
    
    @Test(timeout=10000)
    public void testReassignNotStartedAndCancel() throws Exception {
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        assertNotNull(processInstanceId);
        
        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter());
        assertEquals(1, tasks.size());        
        TaskSummary task = tasks.get(0);
        
        Collection<TaskReassignment> reassignments = userTaskAdminService.getTaskReassignments(task.getId(), false);
        assertNotNull(reassignments);
        assertEquals(0, reassignments.size());
        
        Long reassignmentId = userTaskAdminService.reassignWhenNotStarted(task.getId(), "2s", factory.newUser("john"));
        reassignments = userTaskAdminService.getTaskReassignments(task.getId(), true);
        assertNotNull(reassignments);
        assertEquals(1, reassignments.size());
        
        userTaskAdminService.cancelReassignment(task.getId(), reassignmentId);
        
        reassignments = userTaskAdminService.getTaskReassignments(task.getId(), true);
        assertNotNull(reassignments);
        assertEquals(0, reassignments.size());
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
