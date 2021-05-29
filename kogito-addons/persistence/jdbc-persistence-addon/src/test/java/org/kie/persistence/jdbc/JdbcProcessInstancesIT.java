/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.persistence.jdbc;

import java.util.Collections;
import java.util.Optional;

import javax.sql.DataSource;

import org.drools.core.io.impl.ClassPathResource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.auth.IdentityProviders;
import org.kie.kogito.auth.SecurityPolicy;
import org.kie.kogito.persistence.KogitoProcessInstancesFactory;
import org.kie.kogito.persistence.jdbc.JDBCProcessInstances;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessConfig;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceReadMode;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.process.bpmn2.BpmnProcess;
import org.kie.kogito.process.bpmn2.BpmnProcessInstance;
import org.kie.kogito.process.bpmn2.BpmnVariables;
import org.kie.kogito.testcontainers.KogitoPostgreSqlContainer;
import org.postgresql.ds.PGSimpleDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.kie.kogito.internal.process.runtime.KogitoProcessInstance.STATE_ACTIVE;
import static org.kie.kogito.internal.process.runtime.KogitoProcessInstance.STATE_COMPLETED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Testcontainers
class JdbcProcessInstancesIT {

    @Container
    final static KogitoPostgreSqlContainer container = new KogitoPostgreSqlContainer();
    private static DataSource ds;

    private SecurityPolicy securityPolicy = SecurityPolicy.of(IdentityProviders.of("john"));

    private static final String TEST_ID = "02ac3854-46ee-42b7-8b63-5186c9889d96";

    @BeforeAll
    public static void startContainerAndPublicPortIsAvailable() {
        container.start();
        ds = getDataSource(container);
    }

    @AfterAll
    public static void close() {
        container.stop();
    }

    private BpmnProcess createProcess(ProcessConfig config, String fileName) {
        BpmnProcess process = BpmnProcess.from(config, new ClassPathResource(fileName)).get(0);
        process.setProcessInstancesFactory(new JDBCProcessInstancesFactory(ds));
        process.configure();
        process.instances().values(ProcessInstanceReadMode.MUTABLE).forEach(p -> p.abort());
        return process;
    }

    private static DataSource getDataSource(final PostgreSQLContainer postgreSQLContainer) {

        PGSimpleDataSource ds = new PGSimpleDataSource();

        // Datasource initialization
        ds.setUrl(postgreSQLContainer.getJdbcUrl());
        ds.setUser(postgreSQLContainer.getUsername());
        ds.setPassword(postgreSQLContainer.getPassword());
        return ds;
    }

    @Test
    void testBasicTaskFlow() {
        BpmnProcess process = createProcess(null, "BPMN2-UserTask.bpmn2");
        ProcessInstance<BpmnVariables> processInstance = process.createInstance(BpmnVariables.create(Collections
                .singletonMap("test",
                        "test")));
        processInstance.start();

        assertThat(processInstance.status()).isEqualTo(STATE_ACTIVE);
        assertThat(processInstance.description()).isEqualTo("User Task");

        JDBCProcessInstances processInstances = (JDBCProcessInstances) process.instances();
        assertThat(processInstances.size()).isOne();
        assertThat(processInstances.exists(processInstance.id())).isTrue();
        verify(processInstances).create(any(), any());

        String testVar = (String) processInstance.variables().get("test");
        assertThat(testVar).isEqualTo("test");

        assertThat(processInstance.description()).isEqualTo("User Task");

        assertThat(process.instances().values().iterator().next().workItems(securityPolicy)).hasSize(1);

        WorkItem workItem = processInstance.workItems(securityPolicy).get(0);
        assertThat(workItem).isNotNull();
        assertThat(workItem.getParameters()).containsEntry("ActorId", "john");
        processInstance.completeWorkItem(workItem.getId(), null, securityPolicy);
        assertThat(processInstance.status()).isEqualTo(STATE_COMPLETED);

        processInstances = (JDBCProcessInstances) process.instances();
        verify(processInstances, times(2)).remove(processInstance.id());
        assertThat(processInstances.size()).isZero();
        assertThat(process.instances().values()).isEmpty();
    }

    @Test
    void testBasicFlow() {
        BpmnProcess process = createProcess(null, "BPMN2-UserTask.bpmn2");
        ProcessInstance<BpmnVariables> processInstance = process.createInstance(BpmnVariables.create(Collections
                .singletonMap("test",
                        "test")));
        processInstance.start();

        JDBCProcessInstances processInstances = (JDBCProcessInstances) process.instances();
        assertThat(processInstances.size()).isOne();
        Optional<?> foundOne = processInstances.findById(processInstance.id());
        BpmnProcessInstance instanceOne = (BpmnProcessInstance) foundOne.get();
        processInstances.update(processInstance.id(), instanceOne);

        assertThat(processInstances.size()).isOne();
        assertThat(processInstances.exists(TEST_ID)).isFalse();
        Optional<?> foundTwo = processInstances.findById(TEST_ID);
        assertThat(foundTwo).isEmpty();

        processInstances.remove(processInstance.id());
        assertThat(processInstances.size()).isZero();
        assertThat(process.instances().values()).isEmpty();
    }

    @Test
    void testException() {
        BpmnProcess process = BpmnProcess.from(new ClassPathResource("BPMN2-UserTask-Script.bpmn2")).get(0);
        process.setProcessInstancesFactory(new JDBCProcessInstancesFactory(null));
        process.configure();

        JDBCProcessInstances processInstances = (JDBCProcessInstances) process.instances();
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> processInstances.size());
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> processInstances.values());
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> processInstances.findById(TEST_ID));
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> processInstances.remove(TEST_ID));
    }

    private class JDBCProcessInstancesFactory extends KogitoProcessInstancesFactory {

        public JDBCProcessInstancesFactory(DataSource dataSource) {
            super(dataSource, true);
        }

        @Override
        public JDBCProcessInstances createProcessInstances(Process<?> process) {
            JDBCProcessInstances instances = spy(super.createProcessInstances(process));
            return instances;
        }
    }
}
