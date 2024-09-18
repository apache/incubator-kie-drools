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
import java.util.Date;

import org.drools.io.ClassPathResource;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.bpmn2.BpmnProcess;
import org.kie.kogito.process.bpmn2.BpmnVariables;
import org.kie.kogito.process.impl.AbstractProcessInstance;
import org.kie.kogito.process.impl.DefaultWorkItemHandlerConfig;
import org.kie.kogito.process.impl.StaticProcessConfig;
import org.kie.kogito.process.workitems.impl.DefaultKogitoWorkItemHandler;
import org.kie.kogito.testcontainers.KogitoInfinispanContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.kie.kogito.test.utils.ProcessInstancesTestUtils.assertOne;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Testcontainers
class CacheProcessInstancesWithLockIT {

    private static final String TEST_ID = "02ac3854-46ee-42b7-8b63-5186c9889d96";

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
        StaticProcessConfig config = new StaticProcessConfig();
        ((DefaultWorkItemHandlerConfig) config.workItemHandlers()).register("Human Task", new DefaultKogitoWorkItemHandler());
        BpmnProcess process = BpmnProcess.from(config, new ClassPathResource(fileName)).get(0);
        AbstractProcessInstancesFactory factory = mock(AbstractProcessInstancesFactory.class);
        process.setProcessInstancesFactory(factory);
        process.configure();
        return process;
    }

    @Test
    public void testBasic() {
        StaticProcessConfig config = new StaticProcessConfig();
        ((DefaultWorkItemHandlerConfig) config.workItemHandlers()).register("Human Task", new DefaultKogitoWorkItemHandler());
        BpmnProcess process = createProcess("BPMN2-UserTask.bpmn2");

        CacheProcessInstances pi = new CacheProcessInstances(process, cacheManager, null, true);
        assertThat(pi).isNotNull();

        WorkflowProcessInstance createPi = ((AbstractProcessInstance<?>) process.createInstance(BpmnVariables.create(Collections.singletonMap("test", "test")))).internalGetProcessInstance();
        createPi.setId(TEST_ID);
        createPi.setStartDate(new Date());

        AbstractProcessInstance<?> mockCreatePi = mock(AbstractProcessInstance.class);
        mockCreatePi.setVersion(1L);
        when(mockCreatePi.status()).thenReturn(ProcessInstance.STATE_ACTIVE);
        when(mockCreatePi.internalGetProcessInstance()).thenReturn(createPi);
        when(mockCreatePi.id()).thenReturn(TEST_ID);
        when(mockCreatePi.process()).thenReturn((Process) process);
        pi.create(TEST_ID, mockCreatePi);
        assertOne(pi);
        assertThat(pi.exists(TEST_ID)).isTrue();

        WorkflowProcessInstance updatePi = ((AbstractProcessInstance<?>) process.createInstance(BpmnVariables.create(Collections.singletonMap("test", "test")))).internalGetProcessInstance();
        updatePi.setId(TEST_ID);
        updatePi.setStartDate(new Date());
        AbstractProcessInstance<?> mockUpdatePi = mock(AbstractProcessInstance.class);
        when(mockUpdatePi.status()).thenReturn(ProcessInstance.STATE_ACTIVE);
        when(mockUpdatePi.internalGetProcessInstance()).thenReturn(updatePi);
        when(mockUpdatePi.id()).thenReturn(TEST_ID);
        when(mockUpdatePi.process()).thenReturn((Process) process);

        try {
            pi.update(TEST_ID, mockUpdatePi);
            fail("Updating process should have failed");
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).isEqualTo("Process instance with id '" + TEST_ID + "' updated or deleted by other request");
        }
        pi.remove(TEST_ID);
        assertThat(pi.exists(TEST_ID)).isFalse();
    }
}
