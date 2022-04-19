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
package org.kie.persistence.postgresql;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import org.drools.util.io.ClassPathResource;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.persistence.postgresql.AbstractProcessInstancesFactory;
import org.kie.kogito.persistence.postgresql.PostgresqlProcessInstances;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.bpmn2.BpmnProcess;
import org.kie.kogito.process.bpmn2.BpmnProcessInstance;
import org.kie.kogito.process.bpmn2.BpmnVariables;
import org.kie.kogito.process.impl.AbstractProcessInstance;
import org.kie.kogito.testcontainers.KogitoPostgreSqlContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import io.vertx.pgclient.PgPool;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Testcontainers
class PostgresqlProcessInstancesWithLockIT {

    @Container
    final static KogitoPostgreSqlContainer container = new KogitoPostgreSqlContainer();

    private static PgPool client;
    private static final String TEST_ID = "02ac3854-46ee-42b7-8b63-5186c9889d96";

    @BeforeAll
    public static void startContainerAndPublicPortIsAvailable() {
        container.start();
        client = client();
    }

    @AfterAll
    public static void close() {
        container.stop();
    }

    private BpmnProcess createProcess(String fileName) {
        BpmnProcess process = BpmnProcess.from(new ClassPathResource(fileName)).get(0);
        process.setProcessInstancesFactory(new PostgreProcessInstancesFactory(client));
        process.configure();
        return process;
    }

    private static PgPool client() {
        return PgPool.pool(container.getReactiveUrl());
    }

    @Test
    public void testBasic() {
        BpmnProcess process = createProcess("BPMN2-UserTask.bpmn2");

        PostgresqlProcessInstances pi = new PostgresqlProcessInstances(process, client, true, 1000L, false);
        assertNotNull(pi);

        WorkflowProcessInstance createPi = ((AbstractProcessInstance<?>) process.createInstance(BpmnVariables.create(Collections.singletonMap("test", "test")))).internalGetProcessInstance();
        createPi.setId(TEST_ID);
        createPi.setStartDate(new Date());

        AbstractProcessInstance<?> mockCreatePi = mock(AbstractProcessInstance.class);
        mockCreatePi.setVersion(1L);
        when(mockCreatePi.status()).thenReturn(ProcessInstance.STATE_ACTIVE);
        when(mockCreatePi.internalGetProcessInstance()).thenReturn(createPi);
        when(mockCreatePi.id()).thenReturn(TEST_ID);
        pi.create(TEST_ID, mockCreatePi);
        assertThat(pi.size()).isOne();
        assertTrue(pi.exists(TEST_ID));

        WorkflowProcessInstance updatePi = ((AbstractProcessInstance<?>) process.createInstance(BpmnVariables.create(Collections.singletonMap("test", "test")))).internalGetProcessInstance();
        updatePi.setId(TEST_ID);
        updatePi.setStartDate(new Date());
        AbstractProcessInstance<?> mockUpdatePi = mock(AbstractProcessInstance.class);
        when(mockUpdatePi.status()).thenReturn(ProcessInstance.STATE_ACTIVE);
        when(mockUpdatePi.internalGetProcessInstance()).thenReturn(updatePi);
        when(mockUpdatePi.id()).thenReturn(TEST_ID);
        pi.update(TEST_ID, mockUpdatePi);

        pi.remove(TEST_ID);
        assertFalse(pi.exists(TEST_ID));
    }

    @Test
    public void testUpdate() {
        BpmnProcess process = createProcess("BPMN2-UserTask.bpmn2");
        ProcessInstance<BpmnVariables> processInstance = process.createInstance(BpmnVariables.create(Collections.singletonMap("test", "test")));
        processInstance.start();

        PostgresqlProcessInstances processInstances = (PostgresqlProcessInstances) process.instances();
        assertThat(processInstances.size()).isOne();
        Optional<?> foundOne = processInstances.findById(processInstance.id());
        BpmnProcessInstance instanceOne = (BpmnProcessInstance) foundOne.get();
        foundOne = processInstances.findById(processInstance.id());
        BpmnProcessInstance instanceTwo = (BpmnProcessInstance) foundOne.get();
        assertEquals(1L, instanceOne.version());
        assertEquals(1L, instanceTwo.version());
        instanceOne.updateVariables(BpmnVariables.create(Collections.singletonMap("s", "test")));
        try {
            BpmnVariables testvar = BpmnVariables.create(Collections.singletonMap("ss", "test"));
            instanceTwo.updateVariables(testvar);
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).isEqualTo("Error updating process instance " + instanceOne.id());
        }
        foundOne = processInstances.findById(processInstance.id());
        instanceOne = (BpmnProcessInstance) foundOne.get();
        assertEquals(2L, instanceOne.version());

        processInstances.remove(processInstance.id());
        assertThat(processInstances.size()).isZero();
        assertThat(process.instances().values()).isEmpty();

    }

    @Test
    public void testRemove() {
        BpmnProcess process = createProcess("BPMN2-UserTask.bpmn2");
        ProcessInstance<BpmnVariables> processInstance = process.createInstance(BpmnVariables.create(Collections.singletonMap("test", "test")));
        processInstance.start();

        PostgresqlProcessInstances processInstances = (PostgresqlProcessInstances) process.instances();
        assertThat(processInstances.size()).isOne();
        Optional<?> foundOne = processInstances.findById(processInstance.id());
        BpmnProcessInstance instanceOne = (BpmnProcessInstance) foundOne.get();
        foundOne = processInstances.findById(processInstance.id());
        BpmnProcessInstance instanceTwo = (BpmnProcessInstance) foundOne.get();
        assertEquals(1L, instanceOne.version());
        assertEquals(1L, instanceTwo.version());

        processInstances.remove(instanceOne.id());
        try {
            String id = instanceTwo.id();
            processInstances.remove(id);
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).isEqualTo("The document with ID: " + instanceOne.id() + " was updated or deleted by other request.");
        }
    }

    private class PostgreProcessInstancesFactory extends AbstractProcessInstancesFactory {

        public PostgreProcessInstancesFactory(PgPool client) {
            super(client, true, 10000l, true);
        }

        @Override
        public PostgresqlProcessInstances createProcessInstances(Process<?> process) {
            PostgresqlProcessInstances instances = super.createProcessInstances(process);
            return instances;
        }
    }
}
