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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.kie.api.runtime.process.ProcessInstance.STATE_ACTIVE;
import static org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED;

import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.core.io.impl.ClassPathResource;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.RemoteCacheManagerAdmin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.persistence.KogitoProcessInstancesFactory;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceNotFoundExteption;
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
        assertEquals(STATE_ACTIVE, processInstance.status());
        

        WorkItem workItem = processInstance.workItems().get(0);
        assertNotNull(workItem);
        assertEquals("john", workItem.getParameters().get("ActorId"));
        processInstance.completeWorkItem(workItem.getId(), null);
        assertEquals(STATE_COMPLETED, processInstance.status());
    }
    
    @Test
    public void testProcessInstanceNotFound() {
        
        BpmnProcess process = (BpmnProcess) BpmnProcess.from(new ClassPathResource("BPMN2-UserTask.bpmn2")).get(0);
        process.setProcessInstancesFactory(new CacheProcessInstancesFactory(cacheManager));
        process.configure();
                                     
        ProcessInstance<BpmnVariables> processInstance = process.createInstance(BpmnVariables.create(Collections.singletonMap("test", "test")));

        processInstance.start();
        assertEquals(STATE_ACTIVE, processInstance.status());
        mockCache.clear();

        assertThrows(ProcessInstanceNotFoundExteption.class, () -> processInstance.workItems().get(0));
        
        Optional<? extends ProcessInstance<BpmnVariables>> loaded = process.instances().findById(processInstance.id());
        assertFalse(loaded.isPresent());
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
