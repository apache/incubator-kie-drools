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
package org.kie.kogito.infinispan;

import java.util.Collections;
import java.util.List;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.jbpm.process.instance.impl.Action;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.WorkflowProcess;
import org.jbpm.workflow.core.node.ActionNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.api.definition.process.Node;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.auth.SecurityPolicy;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceReadMode;
import org.kie.kogito.process.ProcessInstances;
import org.kie.kogito.process.SignalFactory;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.process.bpmn2.BpmnProcess;
import org.kie.kogito.process.bpmn2.BpmnVariables;
import org.kie.kogito.process.bpmn2.StaticApplicationAssembler;
import org.kie.kogito.process.impl.StaticProcessConfig;
import org.kie.kogito.process.workitems.impl.DefaultKogitoWorkItemHandler;
import org.kie.kogito.testcontainers.KogitoInfinispanContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.entry;
import static org.kie.kogito.internal.process.runtime.KogitoProcessInstance.STATE_ACTIVE;
import static org.kie.kogito.internal.process.runtime.KogitoProcessInstance.STATE_COMPLETED;
import static org.kie.kogito.internal.process.runtime.KogitoProcessInstance.STATE_ERROR;
import static org.kie.kogito.test.utils.ProcessInstancesTestUtils.abort;
import static org.kie.kogito.test.utils.ProcessInstancesTestUtils.abortFirst;
import static org.kie.kogito.test.utils.ProcessInstancesTestUtils.assertEmpty;
import static org.kie.kogito.test.utils.ProcessInstancesTestUtils.assertOne;
import static org.kie.kogito.test.utils.ProcessInstancesTestUtils.getFirst;

@Testcontainers
class CacheProcessInstancesIT {

    @Container
    public KogitoInfinispanContainer container = new KogitoInfinispanContainer();
    private RemoteCacheManager cacheManager;

    @BeforeEach
    void setup() {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder
                .addServer()
                .host("127.0.0.1")
                .port(container.getMappedPort());

        cacheManager = new RemoteCacheManager(builder.build());
    }

    @AfterEach
    void close() {
        if (cacheManager != null) {
            cacheManager.close();
        }
    }

    private BpmnProcess createProcess(String fileName) {
        StaticProcessConfig processConfig = StaticProcessConfig.newStaticProcessConfigBuilder()
                .withWorkItemHandler("Human Task", new DefaultKogitoWorkItemHandler())
                .build();

        Application application = StaticApplicationAssembler.instance().newStaticApplication(new CacheProcessInstancesFactory(cacheManager), processConfig, fileName);

        org.kie.kogito.process.Processes container = application.get(org.kie.kogito.process.Processes.class);
        String processId = container.processIds().stream().findFirst().get();
        org.kie.kogito.process.Process<? extends Model> process = container.processById(processId);

        abort(process.instances());
        BpmnProcess compiledProcess = (BpmnProcess) process;
        // workaround as BpmnProcess does not compile the scripts but just reads the xml
        for (Node node : ((WorkflowProcess) ((BpmnProcess) process).process()).getNodes()) {
            if (node instanceof ActionNode) {
                DroolsAction a = ((ActionNode) node).getAction();
                a.setMetaData("Action", (Action) kcontext -> {
                    System.out.println("The variable value is " + kcontext.getVariable("s") + " about to call toString on it");
                    kcontext.getVariable("s").toString();
                });
            }
        }
        return compiledProcess;
    }

    @Test
    void testFindByIdReadMode() {
        BpmnProcess process = createProcess("BPMN2-UserTask-Script.bpmn2");

        ProcessInstance<BpmnVariables> mutablePi = process.createInstance(BpmnVariables.create(Collections.singletonMap("var", "value")));

        mutablePi.start();
        assertThat(mutablePi.status()).isEqualTo(STATE_ERROR);
        assertThat(mutablePi.error()).hasValueSatisfying(error -> {
            assertThat(error.errorMessage()).contains("java.lang.NullPointerException");
            assertThat(error.failedNodeId()).isEqualTo("ScriptTask_1");
        });
        assertThat(mutablePi.variables().toMap()).containsExactly(entry("var", "value"));

        ProcessInstances<BpmnVariables> instances = process.instances();
        assertOne(instances);
        ProcessInstance<BpmnVariables> pi = instances.findById(mutablePi.id(), ProcessInstanceReadMode.READ_ONLY).get();
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> pi.abort());

        ProcessInstance<BpmnVariables> readOnlyPi = instances.findById(mutablePi.id(), ProcessInstanceReadMode.READ_ONLY).get();
        assertThat(readOnlyPi.status()).isEqualTo(STATE_ERROR);
        assertThat(readOnlyPi.error()).hasValueSatisfying(error -> {
            assertThat(error.errorMessage()).contains("java.lang.NullPointerException");
            assertThat(error.failedNodeId()).isEqualTo("ScriptTask_1");
        });
        assertThat(readOnlyPi.variables().toMap()).containsExactly(entry("var", "value"));
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> readOnlyPi.abort());

        instances.findById(mutablePi.id()).get().abort();
        assertEmpty(instances);
    }

    @Test
    void testValuesReadMode() {
        BpmnProcess process = createProcess("BPMN2-UserTask-Script.bpmn2");

        ProcessInstance<BpmnVariables> processInstance = process.createInstance(BpmnVariables.create(Collections.singletonMap("test", "test")));

        processInstance.start();

        ProcessInstances<BpmnVariables> instances = process.instances();
        ProcessInstance<BpmnVariables> pi = instances.stream(ProcessInstanceReadMode.READ_ONLY).findAny().get();
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> pi.abort());
        abortFirst(instances);
        assertEmpty(instances);
    }

    @Test
    void testBasicFlow() {
        BpmnProcess process = createProcess("BPMN2-UserTask-Script.bpmn2");

        ProcessInstance<BpmnVariables> processInstance = process.createInstance(BpmnVariables.create(Collections.singletonMap("s", "test")));

        processInstance.start();
        assertThat(processInstance.status()).isEqualTo(STATE_ACTIVE);

        assertOne(process.instances());

        SecurityPolicy asJohn = SecurityPolicy.of("john", emptyList());

        assertThat(getFirst(process.instances()).workItems(asJohn)).hasSize(1);

        List<WorkItem> workItems = processInstance.workItems(asJohn);
        assertThat(workItems).hasSize(1);
        WorkItem workItem = workItems.get(0);
        assertThat(workItem.getParameters()).containsEntry("ActorId", "john");
        processInstance.completeWorkItem(workItem.getId(), null, asJohn);
        assertThat(processInstance.status()).isEqualTo(STATE_COMPLETED);
        assertEmpty(process.instances());
    }

    @Test
    public void testSignalStorage() {
        BpmnProcess process = createProcess("BPMN2-IntermediateCatchEventSignal.bpmn2");
        CacheProcessInstances fsInstances = (CacheProcessInstances) process.instances();
        ProcessInstance<BpmnVariables> pi1 = process.createInstance(BpmnVariables.create(Collections.singletonMap("name", "sig1")));
        ProcessInstance<BpmnVariables> pi2 = process.createInstance(BpmnVariables.create(Collections.singletonMap("name", "sig2")));
        pi1.start();
        pi2.start();

        assertThat(process.instances().stream().count()).isEqualTo(2);

        pi1.workItems().forEach(wi -> pi1.completeWorkItem(wi.getId(), Collections.emptyMap()));
        pi2.workItems().forEach(wi -> pi2.completeWorkItem(wi.getId(), Collections.emptyMap()));

        process.send(SignalFactory.of("sig1", "SomeValue"));
        assertThat(process.instances().stream().count()).isEqualTo(1);
        process.send(SignalFactory.of("sig2", "SomeValue"));
        assertThat(process.instances().stream().count()).isEqualTo(0);
    }

    private class CacheProcessInstancesFactory extends AbstractProcessInstancesFactory {

        CacheProcessInstancesFactory(RemoteCacheManager cacheManager) {
            super(cacheManager, false, null);
        }

    }
}
