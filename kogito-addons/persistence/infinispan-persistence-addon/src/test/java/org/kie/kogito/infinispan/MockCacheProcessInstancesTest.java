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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.kie.api.runtime.process.ProcessInstance.STATE_ACTIVE;
import static org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED;
import static org.kie.api.runtime.process.ProcessInstance.STATE_ERROR;

import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.drools.core.io.impl.ClassPathResource;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.RemoteCacheManagerAdmin;
import org.jbpm.process.instance.impl.Action;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.WorkflowProcess;
import org.jbpm.workflow.core.node.ActionNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.api.definition.process.Node;
import org.kie.api.runtime.process.ProcessContext;
import org.kie.kogito.persistence.KogitoProcessInstancesFactory;
import org.kie.kogito.process.ProcessError;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceNotFoundException;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.process.bpmn2.BpmnProcess;
import org.kie.kogito.process.bpmn2.BpmnVariables;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class MockCacheProcessInstancesTest {
    
    private final ConcurrentHashMap<Object, Object> mockCache = new ConcurrentHashMap<>();
    private RemoteCacheManager cacheManager;
    
    @SuppressWarnings("unchecked")
    @BeforeEach
    public void setup() {
        mockCache.clear();
        cacheManager = mock(RemoteCacheManager.class);
        RemoteCacheManagerAdmin admin = mock(RemoteCacheManagerAdmin.class);
        RemoteCache<Object, Object> cache = mock(RemoteCache.class);
        
        when(cacheManager.administration()).thenReturn(admin);
        when(admin.getOrCreateCache(any(), anyString())).thenReturn(cache);
        
        when(cache.put(any(), any())).then(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object key = invocation.getArgumentAt(0, Object.class);
                Object value = invocation.getArgumentAt(1, Object.class);
                
                return mockCache.put(key, value);
            }
        });
        
        when(cache.get(any())).then(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object key = invocation.getArgumentAt(0, Object.class);               
                
                return mockCache.get(key);
            }
        });
    }

    
    @Test
    public void testBasicFlow() {
        
        BpmnProcess process = (BpmnProcess) BpmnProcess.from(new ClassPathResource("BPMN2-UserTask.bpmn2")).get(0);
        process.setProcessInstancesFactory(new CacheProcessInstancesFactory(cacheManager));
        process.configure();
                                     
        ProcessInstance<BpmnVariables> processInstance = process.createInstance(BpmnVariables.create(Collections.singletonMap("test", "test")));

        processInstance.start();
        assertThat(processInstance.status()).isEqualTo(STATE_ACTIVE);
        

        WorkItem workItem = processInstance.workItems().get(0);
        assertThat(workItem).isNotNull();
        assertThat(workItem.getParameters().get("ActorId")).isEqualTo("john");
        processInstance.completeWorkItem(workItem.getId(), null);
        assertThat(processInstance.status()).isEqualTo(STATE_COMPLETED);
    }
    
    @Test
    public void testProcessInstanceNotFound() {
        
        BpmnProcess process = (BpmnProcess) BpmnProcess.from(new ClassPathResource("BPMN2-UserTask.bpmn2")).get(0);
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
            processInstance.error().orElseThrow(() -> new IllegalStateException("Process instance not in error"))
            .retrigger();
        }); 
    }
    
    @Test
    public void testBasicFlowWithErrorAndSkip() {
        
        testBasicFlowWithError((processInstance) -> {
            processInstance.updateVariables(BpmnVariables.create(Collections.singletonMap("s", "test")));
            processInstance.error().orElseThrow(() -> new IllegalStateException("Process instance not in error"))
            .skip();
        }); 
    }
    
    private void testBasicFlowWithError(Consumer<ProcessInstance<BpmnVariables>> op) {
        
        BpmnProcess process = (BpmnProcess) BpmnProcess.from(new ClassPathResource("BPMN2-UserTask-Script.bpmn2")).get(0);
        // workaround as BpmnProcess does not compile the scripts but just reads the xml
        for (Node node : ((WorkflowProcess)process.legacyProcess()).getNodes()) {
            if (node instanceof ActionNode) {
                DroolsAction a = ((ActionNode) node).getAction();
                
                a.setMetaData("Action", new Action() {
                    
                    @Override
                    public void execute(ProcessContext kcontext) throws Exception {
                        System.out.println("The variable value is " + kcontext.getVariable("s") + " about to call toString on it");

                        kcontext.getVariable("s").toString();
                    }
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

        WorkItem workItem = processInstance.workItems().get(0);
        assertThat(workItem).isNotNull();
        assertThat(workItem.getParameters().get("ActorId")).isEqualTo("john");
        processInstance.completeWorkItem(workItem.getId(), null);
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
        public List<?> marshallers() {
            return Collections.emptyList();
        }
    }
}
