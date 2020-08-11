/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.infinispan;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.drools.core.io.impl.ClassPathResource;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.RemoteCacheManagerAdmin;
import org.infinispan.protostream.BaseMarshaller;
import org.jbpm.process.instance.impl.Action;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.WorkflowProcess;
import org.jbpm.workflow.core.node.ActionNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.api.definition.process.Node;
import org.kie.kogito.auth.SecurityPolicy;
import org.kie.kogito.persistence.KogitoProcessInstancesFactory;
import org.kie.kogito.process.ProcessError;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceNotFoundException;
import org.kie.kogito.process.ProcessInstanceReadMode;
import org.kie.kogito.process.ProcessInstances;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.process.bpmn2.BpmnProcess;
import org.kie.kogito.process.bpmn2.BpmnVariables;
import org.kie.kogito.services.identity.StaticIdentityProvider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;
import static org.kie.api.runtime.process.ProcessInstance.STATE_ACTIVE;
import static org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED;
import static org.kie.api.runtime.process.ProcessInstance.STATE_ERROR;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockCacheProcessInstancesTest {

    private final ConcurrentHashMap<Object, Object> mockCache = new ConcurrentHashMap<>();
    private RemoteCacheManager cacheManager;

    @BeforeEach
    public void setup() {
        mockCache.clear();
        cacheManager = mock(RemoteCacheManager.class);
        RemoteCacheManagerAdmin admin = mock(RemoteCacheManagerAdmin.class);
        RemoteCache<Object, Object> cache = mock(RemoteCache.class);

        when(cacheManager.administration()).thenReturn(admin);
        when(admin.getOrCreateCache(any(), (String) any())).thenReturn(cache);

        when(cache.put(any(), any())).then(invocation -> {
            Object key = invocation.getArgument(0, Object.class);
            Object value = invocation.getArgument(1, Object.class);
            return mockCache.put(key, value);
        });
        when(cache.putIfAbsent(any(), any())).then(invocation -> {
            Object key = invocation.getArgument(0, Object.class);
            Object value = invocation.getArgument(1, Object.class);
            return mockCache.put(key, value);
        });
        when(cache.get(any())).then(invocation -> {
            Object key = invocation.getArgument(0, Object.class);
            return mockCache.get(key);
        });
        when(cache.remove(any())).then(invocation -> {
            Object key = invocation.getArgument(0, Object.class);
            return mockCache.remove(key);
        });
        when(cache.size()).then(invocation -> mockCache.size());
    }

    @Test
    void testFindByIdReadMode() {
        BpmnProcess process = BpmnProcess.from(new ClassPathResource("BPMN2-UserTask-Script.bpmn2")).get(0);
        // workaround as BpmnProcess does not compile the scripts but just reads the xml
        for (Node node : ((WorkflowProcess) process.process()).getNodes()) {
            if (node instanceof ActionNode) {
                DroolsAction a = ((ActionNode) node).getAction();
                a.setMetaData("Action", (Action) kcontext -> {
                    System.out.println("The variable value is " + kcontext.getVariable("s") + " about to call toString on it");
                    kcontext.getVariable("s").toString();
                });
            }
        }
        process.setProcessInstancesFactory(new CacheProcessInstancesFactory(cacheManager));
        process.configure();

        ProcessInstance<BpmnVariables> mutablePi = process.createInstance(BpmnVariables.create(Collections.singletonMap("var", "value")));

        mutablePi.start();
        assertThat(mutablePi.status()).isEqualTo(STATE_ERROR);
        assertThat(mutablePi.error()).hasValueSatisfying(error -> {
            assertThat(error.errorMessage()).endsWith("java.lang.NullPointerException - null");
            assertThat(error.failedNodeId()).isEqualTo("ScriptTask_1");
        });
        assertThat(mutablePi.variables().toMap()).containsExactly(entry("var", "value"));

        ProcessInstances<BpmnVariables> instances = process.instances();
        assertThat(instances.size()).isOne();
        ProcessInstance<BpmnVariables> pi = instances.findById(mutablePi.id(), ProcessInstanceReadMode.READ_ONLY).get();
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> pi.abort());

        ProcessInstance<BpmnVariables> readOnlyPi = instances.findById(mutablePi.id(), ProcessInstanceReadMode.READ_ONLY).get();
        assertThat(readOnlyPi.status()).isEqualTo(STATE_ERROR);
        assertThat(readOnlyPi.error()).hasValueSatisfying(error -> {
            assertThat(error.errorMessage()).endsWith("java.lang.NullPointerException - null");
            assertThat(error.failedNodeId()).isEqualTo("ScriptTask_1");
        });
        assertThat(readOnlyPi.variables().toMap()).containsExactly(entry("var", "value"));
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> readOnlyPi.abort());

        instances.findById(mutablePi.id()).get().abort();
        assertThat(instances.size()).isZero();
    }

    @Test
    public void testBasicFlow() {
        BpmnProcess process = BpmnProcess.from(new ClassPathResource("BPMN2-UserTask.bpmn2")).get(0);
        process.setProcessInstancesFactory(new CacheProcessInstancesFactory(cacheManager));
        process.configure();

        ProcessInstance<BpmnVariables> processInstance = process.createInstance(BpmnVariables.create(Collections.singletonMap("test", "test")));

        processInstance.start();
        assertThat(processInstance.status()).isEqualTo(STATE_ACTIVE);

        WorkItem workItem = processInstance.workItems(SecurityPolicy.of(new StaticIdentityProvider("john"))).get(0);
        assertThat(workItem).isNotNull();
        assertThat(workItem.getParameters().get("ActorId")).isEqualTo("john");
        processInstance.completeWorkItem(workItem.getId(), null, SecurityPolicy.of(new StaticIdentityProvider("john")));
        assertThat(processInstance.status()).isEqualTo(STATE_COMPLETED);
    }

    @Test
    public void testBasicFlowNoActors() {
        BpmnProcess process = BpmnProcess.from(new ClassPathResource("BPMN2-UserTask-NoActors.bpmn2")).get(0);
        process.setProcessInstancesFactory(new CacheProcessInstancesFactory(cacheManager));
        process.configure();

        ProcessInstance<BpmnVariables> processInstance = process.createInstance(BpmnVariables.create(Collections.singletonMap("test", "test")));

        processInstance.start();
        assertThat(processInstance.status()).isEqualTo(STATE_ACTIVE);

        WorkItem workItem = processInstance.workItems().get(0);
        assertThat(workItem).isNotNull();
        assertThat(workItem.getParameters().get("ActorId")).isNull();

        List<WorkItem> workItems = processInstance.workItems(SecurityPolicy.of(new StaticIdentityProvider("john")));
        assertThat(workItems).hasSize(1);

        processInstance.completeWorkItem(workItem.getId(), null);
        assertThat(processInstance.status()).isEqualTo(STATE_COMPLETED);
    }

    @Test
    public void testProcessInstanceNotFound() {
        BpmnProcess process = BpmnProcess.from(new ClassPathResource("BPMN2-UserTask.bpmn2")).get(0);
        process.setProcessInstancesFactory(new CacheProcessInstancesFactory(cacheManager));
        process.configure();

        ProcessInstance<BpmnVariables> processInstance = process.createInstance(BpmnVariables.create(Collections.singletonMap("test", "test")));

        processInstance.start();
        assertThat(processInstance.status()).isEqualTo(STATE_ACTIVE);
        mockCache.clear();

        assertThatThrownBy(() -> processInstance.workItems().get(0)).isInstanceOf(ProcessInstanceNotFoundException.class);

        Optional<? extends ProcessInstance<BpmnVariables>> loaded = process.instances().findById(processInstance.id());
        assertThat(loaded).isNotPresent();
    }

    @Test
    public void testBasicFlowWithErrorAndRetry() {
        testBasicFlowWithError((processInstance) -> {
            processInstance.updateVariables(BpmnVariables.create(Collections.singletonMap("s", "test")));
            processInstance.error().orElseThrow(() -> new IllegalStateException("Process instance not in error")).retrigger();
        });
    }

    @Test
    public void testBasicFlowWithErrorAndSkip() {
        testBasicFlowWithError((processInstance) -> {
            processInstance.updateVariables(BpmnVariables.create(Collections.singletonMap("s", "test")));
            processInstance.error().orElseThrow(() -> new IllegalStateException("Process instance not in error")).skip();
        });
    }

    private void testBasicFlowWithError(Consumer<ProcessInstance<BpmnVariables>> op) {
        BpmnProcess process = BpmnProcess.from(new ClassPathResource("BPMN2-UserTask-Script.bpmn2")).get(0);
        // workaround as BpmnProcess does not compile the scripts but just reads the xml
        for (Node node : ((WorkflowProcess) process.process()).getNodes()) {
            if (node instanceof ActionNode) {
                DroolsAction a = ((ActionNode) node).getAction();

                a.setMetaData("Action", (Action) kcontext -> {
                    System.out.println("The variable value is " + kcontext.getVariable("s") + " about to call toString on it");
                    kcontext.getVariable("s").toString();
                });
            }
        }
        process.setProcessInstancesFactory(new CacheProcessInstancesFactory(cacheManager));
        process.configure();

        ProcessInstance<BpmnVariables> processInstance = process.createInstance(BpmnVariables.create());

        processInstance.start();
        assertThat(processInstance.status()).isEqualTo(STATE_ERROR);

        Optional<ProcessError> errorOp = processInstance.error();
        assertThat(errorOp).isPresent();
        assertThat(errorOp.get().failedNodeId()).isEqualTo("ScriptTask_1");
        assertThat(errorOp.get().errorMessage()).isNotNull().contains("java.lang.NullPointerException - null");

        op.accept(processInstance);

        assertThat(processInstance.error()).isNotPresent();

        WorkItem workItem = processInstance.workItems(SecurityPolicy.of(new StaticIdentityProvider("john"))).get(0);
        assertThat(workItem).isNotNull();
        assertThat(workItem.getParameters().get("ActorId")).isEqualTo("john");
        processInstance.completeWorkItem(workItem.getId(), null, SecurityPolicy.of(new StaticIdentityProvider("john")));
        assertThat(processInstance.status()).isEqualTo(STATE_COMPLETED);
    }

    private class CacheProcessInstancesFactory extends KogitoProcessInstancesFactory {

        CacheProcessInstancesFactory(RemoteCacheManager cacheManager) {
            super(cacheManager);
        }

        @Override
        public String proto() {
            return null;
        }

        @Override
        public List<BaseMarshaller<?>> marshallers() {
            return Collections.emptyList();
        }
    }
}
