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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.kie.api.runtime.process.ProcessInstance.STATE_ACTIVE;
import static org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED;

import java.util.Collections;
import java.util.List;

import org.drools.core.io.impl.ClassPathResource;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ClientIntelligence;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.junit.jupiter.api.Test;
import org.kie.kogito.auth.SecurityPolicy;
import org.kie.kogito.persistence.KogitoProcessInstancesFactory;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.process.bpmn2.BpmnProcess;
import org.kie.kogito.process.bpmn2.BpmnVariables;
import org.kie.kogito.services.identity.StaticIdentityProvider;
import org.kie.kogito.testcontainers.InfinispanContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class CacheProcessInstancesIT {

    @Container
    public InfinispanContainer container = new InfinispanContainer(); 

    @Test
    public void testBasicFlow() {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder
            .addServer()
                .host("127.0.0.1")
                .port(container.getMappedPort())
            .security()
                .authentication()
                .username("admin")
                .password("admin")
                .realm("default")
                .serverName("infinispan")
                .saslMechanism("DIGEST-MD5")
                .clientIntelligence(ClientIntelligence.BASIC);
        
        RemoteCacheManager cacheManager = new RemoteCacheManager(builder.build());
        
        
        BpmnProcess process = (BpmnProcess) BpmnProcess.from(new ClassPathResource("BPMN2-UserTask.bpmn2")).get(0);
        process.setProcessInstancesFactory(new CacheProcessInstancesFactory(cacheManager));
        process.configure();
                                     
        ProcessInstance<BpmnVariables> processInstance = process.createInstance(BpmnVariables.create(Collections.singletonMap("test", "test")));

        processInstance.start();
        assertEquals(STATE_ACTIVE, processInstance.status());
        

        SecurityPolicy asJohn = SecurityPolicy.of(new StaticIdentityProvider("john"));
        WorkItem workItem = processInstance.workItems(asJohn).get(0);
        assertNotNull(workItem);
        assertEquals("john", workItem.getParameters().get("ActorId"));
        processInstance.completeWorkItem(workItem.getId(), null, asJohn);
        assertEquals(STATE_COMPLETED, processInstance.status());
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
