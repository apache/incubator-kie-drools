/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.persistence.jdbc;

import java.util.*;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.bpmn2.BpmnProcess;
import org.kie.kogito.process.bpmn2.BpmnVariables;
import org.kie.kogito.process.bpmn2.StaticApplicationAssembler;
import org.kie.kogito.process.impl.StaticProcessConfig;
import org.kie.kogito.process.workitems.impl.DefaultKogitoWorkItemHandler;

import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.test.utils.ProcessInstancesTestUtils.abort;

/**
 * Abstract base class for data isolation tests in JDBC persistence.
 * Tests verify that process instances are properly filtered by local process IDs
 * when Processes bean is available.
 */
public abstract class AbstractProcessInstancesDataIsolationIT {

    protected final DataSource dataSource;
    protected final org.kie.kogito.process.Processes processes;
    protected BpmnProcess process;
    protected BpmnProcess callActivityProcess;
    protected BpmnProcess remoteCallActivityProcess;
    protected GenericRepository unfilteredRepo;

    protected AbstractProcessInstancesDataIsolationIT(DataSource dataSource, org.kie.kogito.process.Processes processes) {
        this.dataSource = dataSource;
        this.processes = processes;
    }

    @BeforeEach
    public void setup() {
        // Local processes (in localProcessIds())
        process = (BpmnProcess) processes.processById("BPMN2_UserTask");

        callActivityProcess = (BpmnProcess) processes.processById("BPMN2_CallActivity");

        remoteCallActivityProcess = (BpmnProcess) processes.processById("Remote_BPMN2_CallActivity");

        // Create unfiltered repository (null Processes bean disables data isolation filtering)
        unfilteredRepo = new GenericRepository(dataSource, null);
    }

    private JDBCProcessInstances<BpmnVariables> getInstances(BpmnProcess process) {
        return (JDBCProcessInstances<BpmnVariables>) process.instances();
    }

    protected void abortInstances() {
        if (getInstances(process) != null) {
            abort(getInstances(process));
        }
        if (getInstances(callActivityProcess) != null) {
            abort(getInstances(callActivityProcess));
        }
        if (getInstances(remoteCallActivityProcess) != null) {
            abort(getInstances(remoteCallActivityProcess));
        }
    }

    @Test
    public void testDataIsolation_FindByIdAndExists() {
        // Create local standalone instance
        ProcessInstance<BpmnVariables> standaloneInstance = process.createInstance(BpmnVariables.create(singletonMap("test", "standalone")));
        standaloneInstance.start();

        // Create call activity that spawns subprocess with local root_process_id
        ProcessInstance<BpmnVariables> callActivityInstance = callActivityProcess.createInstance(BpmnVariables.create(singletonMap("test", "parent")));
        callActivityInstance.start();

        // Insert remote instances - each call creates both a remote CallActivity and its subprocess
        String remoteId1 = insertRemoteProcessInstance();
        String remoteId2 = insertRemoteProcessInstance();
        String remoteId3 = insertRemoteProcessInstance();

        // Verify remote instances ARE in database (using unfiltered repository)
        assertThat(unfilteredRepo.findByIdInternal("Remote_BPMN2_CallActivity", processes.processById("Remote_BPMN2_CallActivity").version(), UUID.fromString(remoteId1)))
                .as("Remote instance 1 should be in database").isPresent();
        assertThat(unfilteredRepo.findByIdInternal("Remote_BPMN2_CallActivity", processes.processById("Remote_BPMN2_CallActivity").version(), UUID.fromString(remoteId2)))
                .as("Remote instance 2 should be in database").isPresent();
        assertThat(unfilteredRepo.findByIdInternal("Remote_BPMN2_CallActivity", processes.processById("Remote_BPMN2_CallActivity").version(), UUID.fromString(remoteId3)))
                .as("Remote instance 3 should be in database").isPresent();

        // Verify local instances are found
        assertThat(getInstances(process).findById(standaloneInstance.id())).isPresent();
        assertThat(getInstances(process).exists(standaloneInstance.id())).isTrue();
        assertThat(getInstances(callActivityProcess).findById(callActivityInstance.id())).isPresent();
        assertThat(getInstances(callActivityProcess).exists(callActivityInstance.id())).isTrue();

        // Verify remote instances are NOT found (all three remote CallActivity instances)
        assertThat(getInstances(process).findById(remoteId1)).isEmpty();
        assertThat(getInstances(process).exists(remoteId1)).isFalse();
        assertThat(getInstances(process).findById(remoteId2)).isEmpty();
        assertThat(getInstances(process).exists(remoteId2)).isFalse();
        assertThat(getInstances(process).findById(remoteId3)).isEmpty();
        assertThat(getInstances(process).exists(remoteId3)).isFalse();
        assertThat(getInstances(callActivityProcess).findById(remoteId1)).isEmpty();
        assertThat(getInstances(callActivityProcess).exists(remoteId1)).isFalse();
    }

    @Test
    public void testDataIsolation_Stream() {
        // Create local standalone instances
        ProcessInstance<BpmnVariables> standaloneInstance1 = process.createInstance(BpmnVariables.create(singletonMap("test", "local1")));
        standaloneInstance1.start();
        ProcessInstance<BpmnVariables> standaloneInstance2 = process.createInstance(BpmnVariables.create(singletonMap("test", "local2")));
        standaloneInstance2.start();

        // Create call activity that spawns subprocess with local root_process_id
        ProcessInstance<BpmnVariables> callActivityInstance = callActivityProcess.createInstance(BpmnVariables.create(singletonMap("test", "parent")));
        callActivityInstance.start();

        // Insert remote instances - each creates both CallActivity and subprocess
        String remoteId1 = insertRemoteProcessInstance();
        String remoteId2 = insertRemoteProcessInstance();
        String remoteId3 = insertRemoteProcessInstance();
        String remoteId4 = insertRemoteProcessInstance();

        // Verify remote instances ARE in database (using unfiltered repository)
        assertThat(unfilteredRepo.findByIdInternal("Remote_BPMN2_CallActivity", processes.processById("Remote_BPMN2_CallActivity").version(), UUID.fromString(remoteId1)))
                .as("Remote instance 1 should be in database").isPresent();
        assertThat(unfilteredRepo.findByIdInternal("Remote_BPMN2_CallActivity", processes.processById("Remote_BPMN2_CallActivity").version(), UUID.fromString(remoteId2)))
                .as("Remote instance 2 should be in database").isPresent();
        assertThat(unfilteredRepo.findByIdInternal("Remote_BPMN2_CallActivity", processes.processById("Remote_BPMN2_CallActivity").version(), UUID.fromString(remoteId3)))
                .as("Remote instance 3 should be in database").isPresent();
        assertThat(unfilteredRepo.findByIdInternal("Remote_BPMN2_CallActivity", processes.processById("Remote_BPMN2_CallActivity").version(), UUID.fromString(remoteId4)))
                .as("Remote instance 4 should be in database").isPresent();

        // Verify stream includes only local instances (standalone + subprocesses from call activity)
        long count = getInstances(process).stream().count();
        assertThat(count).isEqualTo(3);

        // Verify call activity stream includes only local call activity instance
        assertThat(getInstances(callActivityProcess).stream().count()).isEqualTo(1);
    }

    @Test
    public void testDataIsolation_FindByBusinessKey() {
        final String standaloneBusinessKey = "bk-standalone-" + UUID.randomUUID();
        final String callActivityBusinessKey = "bk-callactivity-" + UUID.randomUUID();
        final String remoteBusinessKey1 = "remote-bk-" + UUID.randomUUID();
        final String remoteBusinessKey2 = "remote-bk-" + UUID.randomUUID();

        // Create local standalone instance with business key
        ProcessInstance<BpmnVariables> standaloneInstance = process.createInstance(standaloneBusinessKey, BpmnVariables.create(singletonMap("test", "standalone")));
        standaloneInstance.start();

        // Create local call activity instance with business key
        ProcessInstance<BpmnVariables> callActivityInstance = callActivityProcess.createInstance(callActivityBusinessKey, BpmnVariables.create(singletonMap("test", "callactivity")));
        callActivityInstance.start();

        // Insert remote instances with different business keys
        insertRemoteProcessInstanceWithBusinessKey(remoteBusinessKey1);
        insertRemoteProcessInstanceWithBusinessKey(remoteBusinessKey2);

        // Verify remote instances ARE in database (using unfiltered repository)
        assertThat(unfilteredRepo.findByBusinessKey("Remote_BPMN2_CallActivity", processes.processById("Remote_BPMN2_CallActivity").version(), remoteBusinessKey1))
                .as("Remote instance with business key 1 should be in database").isPresent();
        assertThat(unfilteredRepo.findByBusinessKey("Remote_BPMN2_CallActivity", processes.processById("Remote_BPMN2_CallActivity").version(), remoteBusinessKey2))
                .as("Remote instance with business key 2 should be in database").isPresent();

        // Should find only the local standalone instance
        Optional<ProcessInstance<BpmnVariables>> foundStandalone = getInstances(process).findByBusinessKey(standaloneBusinessKey);
        assertThat(foundStandalone).isPresent();
        assertThat(foundStandalone.get().id()).isEqualTo(standaloneInstance.id());

        // Should find only the local call activity instance
        Optional<ProcessInstance<BpmnVariables>> foundCallActivity = getInstances(callActivityProcess).findByBusinessKey(callActivityBusinessKey);
        assertThat(foundCallActivity).isPresent();
        assertThat(foundCallActivity.get().id()).isEqualTo(callActivityInstance.id());
    }

    @Test
    public void testDataIsolation_EmptyResultsWhenOnlyRemoteInstances() {
        String remoteId1 = insertRemoteProcessInstance();
        String remoteId2 = insertRemoteProcessInstance();
        String remoteId3 = insertRemoteProcessInstance();
        String remoteId4 = insertRemoteProcessInstance();

        // Verify remote instances ARE in database (using unfiltered repository)
        assertThat(unfilteredRepo.findByIdInternal("Remote_BPMN2_CallActivity", processes.processById("Remote_BPMN2_CallActivity").version(), UUID.fromString(remoteId1)))
                .as("Remote instance 1 should be in database").isPresent();
        assertThat(unfilteredRepo.findByIdInternal("Remote_BPMN2_CallActivity", processes.processById("Remote_BPMN2_CallActivity").version(), UUID.fromString(remoteId2)))
                .as("Remote instance 2 should be in database").isPresent();
        assertThat(unfilteredRepo.findByIdInternal("Remote_BPMN2_CallActivity", processes.processById("Remote_BPMN2_CallActivity").version(), UUID.fromString(remoteId3)))
                .as("Remote instance 3 should be in database").isPresent();
        assertThat(unfilteredRepo.findByIdInternal("Remote_BPMN2_CallActivity", processes.processById("Remote_BPMN2_CallActivity").version(), UUID.fromString(remoteId4)))
                .as("Remote instance 4 should be in database").isPresent();

        // All operations on processInstances should return empty results
        assertThat(getInstances(process).stream().count()).isZero();
        assertThat(getInstances(process).exists(remoteId1)).isFalse();
        assertThat(getInstances(process).findById(remoteId1)).isEmpty();
        assertThat(getInstances(process).findById(remoteId2)).isEmpty();

        // All operations on callActivityProcessInstances should return empty results
        assertThat(getInstances(callActivityProcess).stream().count()).isZero();
        assertThat(getInstances(callActivityProcess).exists(remoteId3)).isFalse();
        assertThat(getInstances(callActivityProcess).findById(remoteId4)).isEmpty();
    }

    @Test
    public void testDataIsolation_LocalProcessIdButRemoteRootProcessId() {
        String remoteId1 = insertRemoteProcessInstance();
        String remoteId2 = insertRemoteProcessInstance();
        String remoteId3 = insertRemoteProcessInstanceWithBusinessKey("remote-business-key");

        // Verify remote instances ARE in database (using unfiltered repository)
        assertThat(unfilteredRepo.findByIdInternal("Remote_BPMN2_CallActivity", processes.processById("Remote_BPMN2_CallActivity").version(), UUID.fromString(remoteId1)))
                .as("Remote instance 1 should be in database").isPresent();
        assertThat(unfilteredRepo.findByIdInternal("Remote_BPMN2_CallActivity", processes.processById("Remote_BPMN2_CallActivity").version(), UUID.fromString(remoteId2)))
                .as("Remote instance 2 should be in database").isPresent();
        assertThat(unfilteredRepo.findByIdInternal("Remote_BPMN2_CallActivity", processes.processById("Remote_BPMN2_CallActivity").version(), UUID.fromString(remoteId3)))
                .as("Remote instance 3 with business key should be in database").isPresent();
        assertThat(unfilteredRepo.findByBusinessKey("Remote_BPMN2_CallActivity", processes.processById("Remote_BPMN2_CallActivity").version(), "remote-business-key"))
                .as("Remote instance with business key should be in database").isPresent();

        // Verify these instances are NOT found by processInstances
        assertThat(getInstances(process).findById(remoteId1)).isEmpty();
        assertThat(getInstances(process).exists(remoteId1)).isFalse();
        assertThat(getInstances(process).findById(remoteId3)).isEmpty();
        assertThat(getInstances(process).exists(remoteId3)).isFalse();

        // Verify these instances are NOT found by callActivityProcessInstances
        assertThat(getInstances(callActivityProcess).findById(remoteId2)).isEmpty();
        assertThat(getInstances(callActivityProcess).exists(remoteId2)).isFalse();

        // Verify business key lookup returns empty (filtered out by root_process_id)
        assertThat(getInstances(process).findByBusinessKey("remote-business-key")).isEmpty();

        // Verify stream operations return zero (all instances filtered out)
        assertThat(getInstances(process).stream().count()).isZero();
        assertThat(getInstances(callActivityProcess).stream().count()).isZero();

        // Now create actual LOCAL instances to verify they ARE found
        ProcessInstance<BpmnVariables> localInstance = process.createInstance(BpmnVariables.create(singletonMap("test", "local")));
        localInstance.start();

        // Verify local instance IS found (has NULL root_process_id or local root_process_id)
        assertThat(getInstances(process).findById(localInstance.id())).isPresent();
        assertThat(getInstances(process).exists(localInstance.id())).isTrue();
        assertThat(getInstances(process).stream().count()).isGreaterThanOrEqualTo(1);

        // Verify remote instances are still NOT found
        assertThat(getInstances(process).findById(remoteId1)).isEmpty();
        assertThat(getInstances(process).findById(remoteId2)).isEmpty();
    }

    public static Set<String> localProcessIds() {
        return Set.of("BPMN2_UserTask", "BPMN2_CallActivity");
    }

    public static BpmnProcess createProcess(DataSource dataSource, String fileName, org.kie.kogito.process.Processes processes) {
        StaticProcessConfig processConfig = StaticProcessConfig.newStaticProcessConfigBuilder()
                .withWorkItemHandler("Human Task", new DefaultKogitoWorkItemHandler())
                .build();

        Application application = StaticApplicationAssembler.instance().newStaticApplication(
                new TestProcessInstancesFactory(dataSource, false, processes),
                processConfig,
                fileName);

        org.kie.kogito.process.Processes container = application.get(org.kie.kogito.process.Processes.class);
        String processId = container.processIds().stream().findFirst().get();
        org.kie.kogito.process.Process<? extends Model> process = container.processById(processId);

        return (BpmnProcess) process;
    }

    public static Map<String, BpmnProcess> createProcesses(DataSource dataSource, org.kie.kogito.process.Processes processes, String... fileNames) {
        StaticProcessConfig processConfig = StaticProcessConfig.newStaticProcessConfigBuilder()
                .withWorkItemHandler("Human Task", new DefaultKogitoWorkItemHandler())
                .build();

        Application application = StaticApplicationAssembler.instance().newStaticApplication(
                new TestProcessInstancesFactory(dataSource, false, processes),
                processConfig,
                fileNames);

        org.kie.kogito.process.Processes container = application.get(org.kie.kogito.process.Processes.class);
        Map<String, BpmnProcess> result = new HashMap<>();
        for (String processId : container.processIds()) {
            result.put(processId, (BpmnProcess) container.processById(processId));
        }
        return result;
    }

    protected String insertRemoteProcessInstance() {
        ProcessInstance<BpmnVariables> instance = remoteCallActivityProcess.createInstance(
                BpmnVariables.create(singletonMap("test", "remote")));
        instance.start();
        return instance.id();
    }

    protected String insertRemoteProcessInstanceWithBusinessKey(String businessKey) {
        ProcessInstance<BpmnVariables> instance = remoteCallActivityProcess.createInstance(
                businessKey,
                BpmnVariables.create(singletonMap("test", "remote")));
        instance.start();
        return instance.id();
    }
}
