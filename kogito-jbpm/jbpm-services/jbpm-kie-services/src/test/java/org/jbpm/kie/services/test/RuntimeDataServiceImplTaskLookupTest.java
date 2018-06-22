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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.services.impl.RuntimeDataServiceImpl;
import org.jbpm.kie.services.test.objects.TestUserGroupCallbackImpl;
import org.jbpm.kie.test.util.AbstractKieServicesBaseTest;
import org.jbpm.services.api.ProcessInstanceNotFoundException;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.query.QueryContext;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.query.QueryFilter;
import org.kie.internal.task.api.AuditTask;
import org.kie.scanner.KieMavenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;
import static org.kie.scanner.KieMavenRepository.getKieMavenRepository;


public class RuntimeDataServiceImplTaskLookupTest extends AbstractKieServicesBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(RuntimeDataServiceImplTaskLookupTest.class);

    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();

    private Long processInstanceId = null;
    private KModuleDeploymentUnit deploymentUnit = null;

    private static final List<Status> readyStatusOnly = Arrays.asList(new Status[]{
            Status.Ready
    });

    private static final List<Status> suspendedStatusOnly = Arrays.asList(new Status[]{
            Status.Suspended
    });

    private static final List<String> fakeGroupIds = Arrays.asList("groupone", "grouptwo", "groupthree");

    @Before
    public void prepare() {
        System.setProperty("org.jbpm.ht.callback", "custom");
        System.setProperty("org.jbpm.ht.custom.callback", "org.jbpm.kie.services.test.objects.TestUserGroupCallbackImpl");


        configureServices();
        logger.debug("Preparing kjar");
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("repo/processes/general/BPMN2-UserTasksAssignedToGroup.bpmn2");

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

        assertNotNull(deploymentService);

        deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        assertNotNull(processService);

    }

    @After
    public void cleanup() {
        System.clearProperty("org.jbpm.ht.callback");
        System.clearProperty("org.jbpm.ht.custom.callback");

        if (processInstanceId != null) {
            try {
                // let's abort process instance to leave the system in clear state
                processService.abortProcessInstance(processInstanceId);

                ProcessInstance pi = processService.getProcessInstance(processInstanceId);
                assertNull(pi);
            } catch (ProcessInstanceNotFoundException e) {
                // ignore it as it was already completed/aborted
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
    public void testGetTasksAssignedAsPotentialOwner() {
        asssertProcessInstance();

        List<TaskSummary> taskSummaries = runtimeDataService.getTasksAssignedAsPotentialOwner("maciej", new QueryFilter());
        assertNotNull(taskSummaries);
        assertEquals(1, taskSummaries.size());

        taskSummaries = runtimeDataService.getTasksAssignedAsPotentialOwner("kris", new QueryFilter());
        assertNotNull(taskSummaries);
        assertEquals(2, taskSummaries.size());

        taskSummaries = runtimeDataService.getTasksAssignedAsPotentialOwner("tihomir", new QueryFilter());
        assertNotNull(taskSummaries);
        assertEquals(0, taskSummaries.size());

        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;
    }

    @Test
    public void testGetTasksAssignedAsPotentialOwnerWithGroups() {
        asssertProcessInstance();

        List<TaskSummary> taskSummaries = runtimeDataService.getTasksAssignedAsPotentialOwner("maciej", fakeGroupIds, new QueryFilter());
        assertNotNull(taskSummaries);
        assertEquals(1, taskSummaries.size());

        taskSummaries = runtimeDataService.getTasksAssignedAsPotentialOwner("kris", fakeGroupIds, new QueryFilter());
        assertNotNull(taskSummaries);
        assertEquals(2, taskSummaries.size());

        taskSummaries = runtimeDataService.getTasksAssignedAsPotentialOwner("tihomir", fakeGroupIds, new QueryFilter());
        assertNotNull(taskSummaries);
        assertEquals(0, taskSummaries.size());

        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;
    }
    
    @Test
    public void testGetTasksAssignedAsPotentialOwnerWithGroupsAndWrongUserId() {
        asssertProcessInstance();
        
        List<String> managerList = Arrays.asList("managers");
        
        List<TaskSummary> taskSummaries = runtimeDataService.getTasksAssignedAsPotentialOwner("mcivantos", fakeGroupIds, new QueryFilter());
        assertNotNull(taskSummaries);
        assertEquals(0, taskSummaries.size());

        taskSummaries = runtimeDataService.getTasksAssignedAsPotentialOwner("mcivantos", managerList, new QueryFilter());
        assertNotNull(taskSummaries);
        assertEquals(1, taskSummaries.size());

        List<String> groupsList = Arrays.asList("managers", "admins");
        
        taskSummaries = runtimeDataService.getTasksAssignedAsPotentialOwner("mcivantos", groupsList, new QueryFilter());
        assertNotNull(taskSummaries);
        assertEquals(2, taskSummaries.size());

        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;
    }

    @Test
    public void testGetTasksAssignedAsPotentialOwnerWithGroupsAndStatus() {
        asssertProcessInstance();

        List<TaskSummary> taskSummaries = runtimeDataService.getTasksAssignedAsPotentialOwner("maciej", null, readyStatusOnly, new QueryFilter());
        assertNotNull(taskSummaries);
        assertEquals(1, taskSummaries.size());

        taskSummaries = runtimeDataService.getTasksAssignedAsPotentialOwner("kris", null, readyStatusOnly, new QueryFilter());
        assertNotNull(taskSummaries);
        assertEquals(2, taskSummaries.size());

        taskSummaries = runtimeDataService.getTasksAssignedAsPotentialOwner("tihomir", null, readyStatusOnly, new QueryFilter());
        assertNotNull(taskSummaries);
        assertEquals(0, taskSummaries.size());

        taskSummaries = runtimeDataService.getTasksAssignedAsPotentialOwner("maciej", fakeGroupIds, readyStatusOnly, new QueryFilter());
        assertNotNull(taskSummaries);
        assertEquals(1, taskSummaries.size());

        taskSummaries = runtimeDataService.getTasksAssignedAsPotentialOwner("maciej", null, suspendedStatusOnly, new QueryFilter());
        assertNotNull(taskSummaries);
        assertEquals(0, taskSummaries.size());

        taskSummaries = runtimeDataService.getTasksAssignedAsPotentialOwner("maciej", fakeGroupIds, suspendedStatusOnly, new QueryFilter());
        assertNotNull(taskSummaries);
        assertEquals(0, taskSummaries.size());

        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;
    }

    @Test
    public void testGetTasksAssignedAsPotentialOwnerByStatus() {
        asssertProcessInstance();

        List<TaskSummary> taskSummaries = runtimeDataService.getTasksAssignedAsPotentialOwnerByStatus("maciej", readyStatusOnly, new QueryFilter());
        assertNotNull(taskSummaries);
        assertEquals(1, taskSummaries.size());

        taskSummaries = runtimeDataService.getTasksAssignedAsPotentialOwnerByStatus("kris", readyStatusOnly, new QueryFilter());
        assertNotNull(taskSummaries);
        assertEquals(2, taskSummaries.size());

        taskSummaries = runtimeDataService.getTasksAssignedAsPotentialOwnerByStatus("tihomir", readyStatusOnly, new QueryFilter());
        assertNotNull(taskSummaries);
        assertEquals(0, taskSummaries.size());

        taskSummaries = runtimeDataService.getTasksAssignedAsPotentialOwnerByStatus("maciej", suspendedStatusOnly, new QueryFilter());
        assertNotNull(taskSummaries);
        assertEquals(0, taskSummaries.size());



        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;
    }

    @Test
    public void testGetAllGroupAuditTasks() {
        asssertProcessInstance();

        List<AuditTask> auditTasks = runtimeDataService.getAllGroupAuditTask("maciej", new QueryFilter());
        assertNotNull(auditTasks);
        assertEquals(1, auditTasks.size());
        assertEquals("AdminsTask", auditTasks.get(0).getName());

        auditTasks = runtimeDataService.getAllGroupAuditTask("kris", new QueryFilter());
        assertNotNull(auditTasks);
        assertEquals(2, auditTasks.size());
        assertAuditTasksContain("AdminsTask", auditTasks);
        assertAuditTasksContain("ManagersTask", auditTasks);

        auditTasks = runtimeDataService.getAllGroupAuditTask("tihomir", new QueryFilter());
        assertNotNull(auditTasks);
        assertEquals(0, auditTasks.size());
    }

    @Test
    public void testGetAllAdminAuditTask() {
        asssertProcessInstance();

        List<AuditTask> auditTasks = runtimeDataService.getAllAdminAuditTask("maciej", new QueryFilter());
        assertNotNull(auditTasks);
        assertEquals(0, auditTasks.size());

        auditTasks = runtimeDataService.getAllAdminAuditTask("kris", new QueryFilter());
        assertNotNull(auditTasks);
        assertEquals(0, auditTasks.size());

        auditTasks = runtimeDataService.getAllAdminAuditTask("tihomir", new QueryFilter());
        assertNotNull(auditTasks);
        assertEquals(0, auditTasks.size());
    }

    private void asssertProcessInstance() {
        assertTrue(((RuntimeDataServiceImpl) runtimeDataService).getUserGroupCallback() instanceof TestUserGroupCallbackImpl);

        Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new QueryContext());
        assertNotNull(instances);
        assertEquals(0, instances.size());

        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "UserTask");
        assertNotNull(processInstanceId);

        instances = runtimeDataService.getProcessInstancesByProcessDefinition("UserTask", new QueryContext());
        assertNotNull(instances);
        assertEquals(1, instances.size());

        ProcessInstanceDesc instance = instances.iterator().next();
        assertEquals(1, (int)instance.getState());
        assertEquals("UserTask", instance.getProcessId());
    }

    private void assertAuditTasksContain(String taskName, List<AuditTask> auditTasks) {
        for(AuditTask task : auditTasks) {
            if(task.getName().equals(taskName)) {
                return;
            }
        }
        fail("Audit tasks do not contain: " + taskName);
    }
}
