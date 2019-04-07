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

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.test.util.AbstractKieServicesBaseTest;
import org.jbpm.services.api.ProcessInstanceNotFoundException;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.api.query.QueryNotFoundException;
import org.jbpm.services.api.query.model.QueryDefinition;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.task.query.TaskSummaryQueryBuilder.OrderBy;
import org.kie.scanner.KieMavenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;
import static org.kie.scanner.KieMavenRepository.getKieMavenRepository;

public class TaskQueryBuilderTest extends AbstractKieServicesBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(KModuleDeploymentServiceTest.class);

    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();
    protected String correctUser = "salaboy";
    protected String wrongUser = "wrongUser";

    protected Long pids[];
    protected KModuleDeploymentUnit deploymentUnit = null;
    protected KModuleDeploymentUnit deploymentUnitJPA = null;

    protected QueryDefinition query;

    protected String dataSourceJNDIname;

    @Before
    public void prepare() {
        this.dataSourceJNDIname = getDataSourceJNDI();
        configureServices();
        logger.debug("Preparing kjar");
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("repo/processes/general/humanTask.bpmn");
        processes.add("repo/processes/hr/hiring.bpmn2");

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

        prepareJPAModule(ks, repository);

        assertNotNull(processService);
    }

    protected void prepareJPAModule(KieServices ks, KieMavenRepository repository) {
        // jpa module
        ReleaseId releaseIdJPA = ks.newReleaseId("org.jbpm.test", "persistence-test", "1.0.0");
        File kjarJPA = new File("src/test/resources/kjar-jpa/persistence-test.jar");
        File pomJPA = new File("src/test/resources/kjar-jpa/pom.xml");

        repository.installArtifact(releaseIdJPA, kjarJPA, pomJPA);

        deploymentUnitJPA = new KModuleDeploymentUnit("org.jbpm.test", "persistence-test", "1.0.0");
    }

    protected String getDataSourceJNDI() {
        return "jdbc/testDS1";
    }

    @After
    public void cleanup() {
        if (query != null) {
            try {
                queryService.unregisterQuery(query.getName());
            } catch (QueryNotFoundException e) {

            }
        }

        if (pids != null) {
            for (int i = 0; i < pids.length; ++i) {
                try {
                    // let's abort process instance to leave the system in clear
                    // state
                    long pid = pids[i];
                    processService.abortProcessInstance(pid);

                    ProcessInstance pi = processService.getProcessInstance(pid);
                    assertNull(pi);
                } catch (ProcessInstanceNotFoundException e) {
                    // ignore it as it was already completed/aborted
                }
            }
            pids = null;
        }
        cleanupSingletonSessionId();
        if (units != null && !units.isEmpty()) {
            for (DeploymentUnit unit : units) {
                try {
                    deploymentService.undeploy(unit);
                } catch (Exception e) {
                    // do nothing in case of some failed tests to avoid next
                    // test to fail as well
                }
            }
            units.clear();
        }
        close();
    }

    @Test
    public void testGetTaskInstances() {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("approval_document", "initial content");

        pids = new Long[1];
        pids[0] = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument", params);
        assertNotNull(pids[0]);

        List<TaskSummary> taskInstanceLogs = runtimeDataService.taskSummaryQuery(correctUser).build().getResultList();
        assertNotNull(taskInstanceLogs);
        assertEquals(1, taskInstanceLogs.size());
    }

    @Test
    public void testGetTaskPageInstances() {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("approval_document", "initial content");
        pids = new Long[10];

        for (int i = 0; i < 10; ++i) {
            pids[i] = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument", params);
            assertNotNull(pids[i]);
        }

        List<TaskSummary> taskInstanceLogs = runtimeDataService.taskSummaryQuery(correctUser)
                .ascending(OrderBy.processInstanceId)
                .maxResults(4)
                .offset(6)
                .build().getResultList();
        assertNotNull(taskInstanceLogs);
        assertEquals(4, taskInstanceLogs.size());

        int pid = 6;
        for (TaskSummary ts : taskInstanceLogs) {
            assertEquals((long) pids[pid], (long) ts.getProcessInstanceId());
            pid++;
        }
    }

    @Test
    public void testGetTaskIdGroupsInstances() {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("approval_document", "initial content");
        pids = new Long[5];

        for (int i = 0; i < 5; ++i) {
            pids[i] = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument", params);
            assertNotNull(pids[i]);
        }

        List<TaskSummary> allTaskInstanceLogs = runtimeDataService.taskSummaryQuery(correctUser)
                .ascending(OrderBy.taskId)
                .build().getResultList();
        assertNotNull(allTaskInstanceLogs);

        long firstTaskId = allTaskInstanceLogs.get(0).getId();

        List<TaskSummary> taskInstanceLogs = runtimeDataService.taskSummaryQuery(correctUser)
                .newGroup()
                    .taskIdRange(firstTaskId, firstTaskId + 1)
                .endGroup()
                .or()
                .newGroup()
                    .taskIdRange(firstTaskId + 3, firstTaskId + 4)
                .endGroup()
                .build().getResultList();
        assertNotNull(taskInstanceLogs);
        assertEquals(4, taskInstanceLogs.size());

        long tid = firstTaskId;
        for (TaskSummary ts : taskInstanceLogs) {
            assertEquals(tid, (long) ts.getId());
            tid++;
            if (tid == firstTaskId + 2) {
                tid++;
            }
        }
    }

    @Test
    public void testGetDescendingTaskInstances() {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("approval_document", "initial content");
        pids = new Long[3];

        for (int i = 0; i < 3; ++i) {
            pids[i] = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument", params);
            assertNotNull(pids[i]);
        }

        List<TaskSummary> taskInstanceLogs = runtimeDataService.taskSummaryQuery(correctUser)
                .descending(OrderBy.processInstanceId)
                .build().getResultList();
        assertNotNull(taskInstanceLogs);
        assertEquals(3, taskInstanceLogs.size());

        int pos = 2;
        for (TaskSummary ts : taskInstanceLogs) {
            assertEquals((long) pids[pos], (long) ts.getProcessInstanceId());
            pos--;
        }
    }

    @Test
    public void testGetTaskVariables() {

        Map<String, Object> params = new HashMap<String, Object>();
        pids = new Long[3];
        String names[] = { "Dalinar Kholin", "Shallan", "Kaladin" };

        for (int i = 0; i < 3; ++i) {
            params.put("name", names[i]);
            pids[i] = processService.startProcess(deploymentUnit.getIdentifier(), "hiring", params);
            assertNotNull(pids[i]);
        }

        List<TaskSummary> taskInstanceLogs = runtimeDataService.taskSummaryQuery("katy")
                .ascending(OrderBy.processInstanceId)
                .newGroup()
                    .variableName("name")
                    .variableValue("Shallan")
                .endGroup()
                .or()
                .newGroup()
                    .variableName("name")
                    .variableValue("Kaladin")
                .endGroup()
                .build().getResultList();
        assertNotNull(taskInstanceLogs);
        assertEquals(2, taskInstanceLogs.size());

        int pos = 1;
        for (TaskSummary ts : taskInstanceLogs) {
            assertEquals((long) pids[pos], (long) ts.getProcessInstanceId());
            pos++;
        }

        taskInstanceLogs = runtimeDataService.taskSummaryQuery("katy")
                .variableName("name")
                .variableValue("Dalinar Kholin", "Shallan")
                .build().getResultList();
        assertNotNull(taskInstanceLogs);
        assertEquals(2, taskInstanceLogs.size());

        taskInstanceLogs = runtimeDataService.taskSummaryQuery("katy")
                .variableName("name")
                .regex()
                .variableValue("*lad*") // Kaladin
                .build().getResultList();
        assertNotNull(taskInstanceLogs);
        assertEquals(1, taskInstanceLogs.size());

    }
}
