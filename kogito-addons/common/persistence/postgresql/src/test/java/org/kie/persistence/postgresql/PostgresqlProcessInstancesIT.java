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

import org.drools.util.io.ClassPathResource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.auth.IdentityProviders;
import org.kie.kogito.auth.SecurityPolicy;
import org.kie.kogito.persistence.postgresql.AbstractProcessInstancesFactory;
import org.kie.kogito.persistence.postgresql.PostgresqlProcessInstances;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceReadMode;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.process.bpmn2.BpmnProcess;
import org.kie.kogito.process.bpmn2.BpmnVariables;
import org.kie.kogito.testcontainers.KogitoPostgreSqlContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import io.vertx.pgclient.PgPool;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.internal.process.runtime.KogitoProcessInstance.STATE_ACTIVE;
import static org.kie.kogito.internal.process.runtime.KogitoProcessInstance.STATE_COMPLETED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Testcontainers
class PostgresqlProcessInstancesIT {

    @Container
    final static KogitoPostgreSqlContainer container = new KogitoPostgreSqlContainer();

    private static PgPool client;

    private SecurityPolicy securityPolicy = SecurityPolicy.of(IdentityProviders.of("john"));

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
        process.instances().values(ProcessInstanceReadMode.MUTABLE).forEach(p -> p.abort());
        return process;
    }

    private static PgPool client() {
        return PgPool.pool(container.getReactiveUrl());
    }

    @Test
    void testBasicFlow() {
        BpmnProcess process = createProcess("BPMN2-UserTask.bpmn2");
        ProcessInstance<BpmnVariables> processInstance = process.createInstance(BpmnVariables.create(Collections.singletonMap("test", "test")));
        processInstance.start();

        assertThat(processInstance.status()).isEqualTo(STATE_ACTIVE);
        assertThat(processInstance.description()).isEqualTo("User Task");

        PostgresqlProcessInstances processInstances = (PostgresqlProcessInstances) process.instances();
        assertThat(processInstances.size()).isOne();
        assertThat(processInstances.exists(processInstance.id())).isTrue();

        ProcessInstance<?> readOnlyPI = process.instances().findById(processInstance.id(), ProcessInstanceReadMode.READ_ONLY).get();
        assertThat(readOnlyPI.status()).isEqualTo(STATE_ACTIVE);
        assertThat(process.instances().values(ProcessInstanceReadMode.READ_ONLY).size()).isOne();

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

        processInstances = (PostgresqlProcessInstances) process.instances();
        verify(processInstances, times(2)).remove(processInstance.id());

        assertThat(processInstances.size()).isZero();

        assertThat(process.instances().values()).isEmpty();
    }

    @Test
    void testMultipleProcesses() {
        BpmnProcess utProcess = createProcess("BPMN2-UserTask.bpmn2");
        ProcessInstance<BpmnVariables> utProcessInstance = utProcess.createInstance(BpmnVariables.create());
        utProcessInstance.start();

        BpmnProcess scriptProcess = createProcess("BPMN2-UserTask-Script.bpmn2");
        ProcessInstance<BpmnVariables> scriptProcessInstance = scriptProcess.createInstance(BpmnVariables.create());
        scriptProcessInstance.start();

        //Try to remove process instance from another process id
        ((PostgresqlProcessInstances) utProcess.instances()).remove(scriptProcessInstance.id());
        ((PostgresqlProcessInstances) scriptProcess.instances()).remove(utProcessInstance.id());

        assertThat(utProcess.instances().size()).isOne();
        assertThat(utProcess.instances().values()).hasSize(1);
        assertThat(utProcess.instances().findById(utProcessInstance.id())).isPresent();
        assertThat(utProcess.instances().findById(scriptProcessInstance.id())).isEmpty();

        assertThat(scriptProcess.instances().size()).isOne();
        assertThat(scriptProcess.instances().values()).hasSize(1);
        assertThat(scriptProcess.instances().findById(scriptProcessInstance.id())).isPresent();
        assertThat(scriptProcess.instances().findById(utProcessInstance.id())).isEmpty();

        ((PostgresqlProcessInstances) utProcess.instances()).remove(utProcessInstance.id());
        assertThat(utProcess.instances().size()).isZero();
        assertThat(utProcess.instances().values()).isEmpty();
        assertThat(utProcess.instances().findById(utProcessInstance.id())).isEmpty();
        assertThat(utProcess.instances().findById(scriptProcessInstance.id())).isEmpty();

        ((PostgresqlProcessInstances) scriptProcess.instances()).remove(scriptProcessInstance.id());
        assertThat(scriptProcess.instances().size()).isZero();
        assertThat(scriptProcess.instances().values()).isEmpty();
        assertThat(scriptProcess.instances().findById(scriptProcessInstance.id())).isEmpty();
        assertThat(scriptProcess.instances().findById(utProcessInstance.id())).isEmpty();
    }

    private class PostgreProcessInstancesFactory extends AbstractProcessInstancesFactory {

        public PostgreProcessInstancesFactory(PgPool client) {
            super(client, true, 10000l, false);
        }

        @Override
        public PostgresqlProcessInstances createProcessInstances(Process<?> process) {
            return spy(super.createProcessInstances(process));
        }

    }
}
