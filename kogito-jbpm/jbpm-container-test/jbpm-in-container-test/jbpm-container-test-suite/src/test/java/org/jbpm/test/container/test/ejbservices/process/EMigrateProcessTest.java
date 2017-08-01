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

package org.jbpm.test.container.test.ejbservices.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;

import org.assertj.core.api.Assertions;
import org.jbpm.services.api.admin.MigrationReport;
import org.jbpm.services.ejb.api.admin.ProcessInstanceMigrationServiceEJBLocal;
import org.jbpm.test.container.AbstractRuntimeEJBServicesTest;
import org.jbpm.test.container.groups.EAP;
import org.jbpm.test.container.groups.WAS;
import org.jbpm.test.container.groups.WLS;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.query.QueryFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Category({ EAP.class, WAS.class, WLS.class })
public class EMigrateProcessTest extends AbstractRuntimeEJBServicesTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(EMigrateProcessTest.class);

    protected static final String USER_YODA = "yoda";

    @EJB
    protected ProcessInstanceMigrationServiceEJBLocal migrationService;

    protected static String kieJar2;

    @Before
    @Override
    public void deployKieJar() {
        if (kieJar == null) {
            kieJar = archive.deployMigrationV1KieJar().getIdentifier();
        }
        if (kieJar2 == null) {
            kieJar2 = archive.deployMigrationV2KieJar().getIdentifier();
        }
    }

    @After
    @Override
    public void cleanup() {
        super.cleanup();
        kieJar2 = null;
    }

    @Test
    public void testUpgradeProcessInstance() throws Exception {
        Long processInstanceId = processService.startProcess(kieJar, EVALUATION_PROCESS_ID_V1);
        if (processInstanceId != null) {
            archive.getPids().add(processInstanceId);
        }

        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER_YODA, new QueryFilter(0,
                10));
        Assertions.assertThat(tasks.size()).isEqualTo(1);

        TaskSummary task = tasks.get(0);
        Assertions.assertThat(task.getName()).isEqualTo("Evaluate items");
        Assertions.assertThat(task.getDeploymentId()).isEqualTo(kieJar);
        Assertions.assertThat(task.getProcessId()).isEqualTo(EVALUATION_PROCESS_ID_V1);
        Assertions.assertThat(task.getProcessInstanceId()).isEqualTo(processInstanceId);

        // migrate process instance to evaluation 2 in container 2
        MigrationReport report = migrationService.migrate(kieJar, processInstanceId, kieJar2, EVALUATION_PROCESS_ID_V2);
        Assertions.assertThat(report).isNotNull();
        Assertions.assertThat(report.isSuccessful()).isTrue();

        // it stays in the same task
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER_YODA, new QueryFilter(0, 10));
        Assertions.assertThat(tasks.size()).isEqualTo(1);

        task = tasks.get(0);
        Assertions.assertThat(task.getName()).isEqualTo("Evaluate items");
        Assertions.assertThat(task.getDeploymentId()).isEqualTo(kieJar2);
        Assertions.assertThat(task.getProcessId()).isEqualTo(EVALUATION_PROCESS_ID_V2);
        Assertions.assertThat(task.getProcessInstanceId()).isEqualTo(processInstanceId);

        userTaskService.completeAutoProgress(task.getId(), USER_YODA, null);

        // but next task should be Approve user task
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER_YODA, new QueryFilter(0, 10));
        Assertions.assertThat(tasks.size()).isEqualTo(1);

        task = tasks.get(0);
        Assertions.assertThat(task.getName()).isEqualTo("Approve");
        Assertions.assertThat(task.getDeploymentId()).isEqualTo(kieJar2);
        Assertions.assertThat(task.getProcessId()).isEqualTo(EVALUATION_PROCESS_ID_V2);
        Assertions.assertThat(task.getProcessInstanceId()).isEqualTo(processInstanceId);

    }

    @Test
    public void testUpgradeProcessInstanceWithNodeMapping() throws Exception {
        Long processInstanceId = processService.startProcess(kieJar, EVALUATION_PROCESS_ID_V1);
        if (processInstanceId != null) {
            archive.getPids().add(processInstanceId);
        }

        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER_YODA, new QueryFilter(0,
                10));
        Assertions.assertThat(tasks.size()).isEqualTo(1);

        TaskSummary task = tasks.get(0);
        Assertions.assertThat(tasks.get(0).getName()).isEqualTo("Evaluate items");
        Assertions.assertThat(task.getDeploymentId()).isEqualTo(kieJar);
        Assertions.assertThat(task.getProcessId()).isEqualTo(EVALUATION_PROCESS_ID_V1);
        Assertions.assertThat(task.getProcessInstanceId()).isEqualTo(processInstanceId);

        Map<String, String> nodeMapping = new HashMap<String, String>();
        nodeMapping.put("_4E8E7545-FB70-494E-9136-2B9ABE655889", "_56FB3E50-DEDD-415B-94DD-0357C91836B9");
        // migrate process instance to evaluation 2 in container 2
        MigrationReport report = migrationService.migrate(kieJar, processInstanceId, kieJar2, EVALUATION_PROCESS_ID_V2,
                nodeMapping);
        Assertions.assertThat(report).isNotNull();
        Assertions.assertThat(report.isSuccessful()).isTrue();

        // migrated to Approve user task
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER_YODA, new QueryFilter(0, 10));
        Assertions.assertThat(tasks.size()).isEqualTo(1);

        task = tasks.get(0);
        Assertions.assertThat(task.getName()).isEqualTo("Approve");
        Assertions.assertThat(task.getDeploymentId()).isEqualTo(kieJar2);
        Assertions.assertThat(task.getProcessId()).isEqualTo(EVALUATION_PROCESS_ID_V2);
        Assertions.assertThat(task.getProcessInstanceId()).isEqualTo(processInstanceId);

    }

    @Test
    public void testUpgradeProcessInstances() throws Exception {

        List<Long> ids = new ArrayList<Long>();

        for (int i = 0; i < 5; i++) {
            Long processInstanceId = processService.startProcess(kieJar, EVALUATION_PROCESS_ID_V1);
            ids.add(processInstanceId);
            if (processInstanceId != null) {
                archive.getPids().add(processInstanceId);
            }
        }

        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER_YODA, new QueryFilter(0,
                10));
        Assertions.assertThat(tasks.size()).isEqualTo(5);

        for (TaskSummary task : tasks) {
            Assertions.assertThat(task.getName()).isEqualTo("Evaluate items");
            Assertions.assertThat(task.getDeploymentId()).isEqualTo(kieJar);
            Assertions.assertThat(task.getProcessId()).isEqualTo(EVALUATION_PROCESS_ID_V1);
        }

        // migrate process instance to evaluation 2 in container 2
        List<MigrationReport> reports = migrationService.migrate(kieJar, ids, kieJar2, EVALUATION_PROCESS_ID_V2);
        Assertions.assertThat(reports).isNotNull();

        for (MigrationReport report : reports) {
            Assertions.assertThat(report.isSuccessful()).isTrue();
        }

        // it stays in the same task
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER_YODA, new QueryFilter(0, 10));
        Assertions.assertThat(tasks.size()).isEqualTo(5);

        for (TaskSummary task : tasks) {
            Assertions.assertThat(task.getName()).isEqualTo("Evaluate items");
            Assertions.assertThat(task.getDeploymentId()).isEqualTo(kieJar2);
            Assertions.assertThat(task.getProcessId()).isEqualTo(EVALUATION_PROCESS_ID_V2);

            userTaskService.completeAutoProgress(task.getId(), USER_YODA, null);
        }
        // but next task should be Approve user task
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER_YODA, new QueryFilter(0, 10));
        Assertions.assertThat(tasks.size()).isEqualTo(5);

        for (TaskSummary task : tasks) {
            Assertions.assertThat(task.getName()).isEqualTo("Approve");
            Assertions.assertThat(task.getDeploymentId()).isEqualTo(kieJar2);
            Assertions.assertThat(task.getProcessId()).isEqualTo(EVALUATION_PROCESS_ID_V2);
        }

    }

    @Test
    public void testUpgradeProcessInstancesWithNodeMapping() throws Exception {

        List<Long> ids = new ArrayList<Long>();

        for (int i = 0; i < 5; i++) {
            Long processInstanceId = processService.startProcess(kieJar, EVALUATION_PROCESS_ID_V1);
            ids.add(processInstanceId);
            if (processInstanceId != null) {
                archive.getPids().add(processInstanceId);
            }
        }

        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER_YODA, new QueryFilter(0,
                10));
        Assertions.assertThat(tasks.size()).isEqualTo(5);

        for (TaskSummary task : tasks) {
            Assertions.assertThat(task.getName()).isEqualTo("Evaluate items");
            Assertions.assertThat(task.getDeploymentId()).isEqualTo(kieJar);
            Assertions.assertThat(task.getProcessId()).isEqualTo(EVALUATION_PROCESS_ID_V1);
        }

        Map<String, String> nodeMapping = new HashMap<String, String>();
        nodeMapping.put("_4E8E7545-FB70-494E-9136-2B9ABE655889", "_56FB3E50-DEDD-415B-94DD-0357C91836B9");
        // migrate process instance to evaluation 2 in container 2
        List<MigrationReport> reports = migrationService.migrate(kieJar, ids, kieJar2, EVALUATION_PROCESS_ID_V2,
                nodeMapping);
        Assertions.assertThat(reports).isNotNull();

        for (MigrationReport report : reports) {
            Assertions.assertThat(report.isSuccessful()).isTrue();
        }

        // but next task should be Approve user task
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER_YODA, new QueryFilter(0, 10));
        Assertions.assertThat(tasks.size()).isEqualTo(5);

        for (TaskSummary task : tasks) {
            Assertions.assertThat(task.getName()).isEqualTo("Approve");
            Assertions.assertThat(task.getDeploymentId()).isEqualTo(kieJar2);
            Assertions.assertThat(task.getProcessId()).isEqualTo(EVALUATION_PROCESS_ID_V2);
        }
    }

}
