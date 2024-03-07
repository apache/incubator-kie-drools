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
package org.kie.kogito.event.impl;

import org.junit.jupiter.api.Test;
import org.kie.api.event.process.ErrorEvent;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessEvent;
import org.kie.api.event.process.ProcessNodeEvent;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.kogito.event.process.ProcessInstanceErrorDataEvent;
import org.kie.kogito.event.process.ProcessInstanceNodeDataEvent;
import org.kie.kogito.event.process.ProcessInstanceStateDataEvent;
import org.kie.kogito.event.process.ProcessInstanceVariableDataEvent;
import org.kie.kogito.internal.process.runtime.KogitoNode;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcess;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcessInstance;
import org.mockito.Mockito;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.event.process.ProcessInstanceEventMetadata.PROCESS_ID_META_DATA;

public class ProcessInstanceEventBatchTest {

    @Test
    public void testNoServiceDefined() {
        ProcessInstanceEventBatch batch = new ProcessInstanceEventBatch("", null);

        assertThat(batch.extractRuntimeSource(singletonMap(PROCESS_ID_META_DATA, "travels"))).isEqualTo("/travels");
        assertThat(batch.extractRuntimeSource(singletonMap(PROCESS_ID_META_DATA, "demo.orders"))).isEqualTo("/orders");
    }

    @Test
    public void testNoProcessIdDefined() {
        ProcessInstanceEventBatch batch = new ProcessInstanceEventBatch("http://localhost:8080", null);
        assertThat(batch.extractRuntimeSource(emptyMap())).isNull();
    }

    @Test
    public void testServiceDefined() {
        ProcessInstanceEventBatch batch = new ProcessInstanceEventBatch("http://localhost:8080", null);

        assertThat(batch.extractRuntimeSource(singletonMap(PROCESS_ID_META_DATA, "travels"))).isEqualTo("http://localhost:8080/travels");
        assertThat(batch.extractRuntimeSource(singletonMap(PROCESS_ID_META_DATA, "demo.orders"))).isEqualTo("http://localhost:8080/orders");
    }

    @Test
    public void testEventSorting() {
        ProcessInstanceEventBatch batch = new ProcessInstanceEventBatch("", null);
        KogitoWorkflowProcess process = Mockito.mock(KogitoWorkflowProcess.class);
        KogitoWorkflowProcessInstance processInstance = Mockito.mock(KogitoWorkflowProcessInstance.class);
        KogitoNodeInstance nodeInstance = Mockito.mock(KogitoNodeInstance.class);
        KogitoNode node = Mockito.mock(KogitoNode.class);
        Mockito.when(processInstance.getProcess()).thenReturn(process);
        Mockito.when(nodeInstance.getNode()).thenReturn(node);
        batch.append(mockEvent(ProcessVariableChangedEvent.class, processInstance));
        batch.append(mockEvent(ProcessNodeTriggeredEvent.class, processInstance, nodeInstance));
        batch.append(mockEvent(ProcessNodeLeftEvent.class, processInstance, nodeInstance));
        batch.append(mockEvent(ProcessStartedEvent.class, processInstance));
        batch.append(mockEvent(ProcessNodeTriggeredEvent.class, processInstance, nodeInstance));
        batch.append(mockEvent(ProcessNodeLeftEvent.class, processInstance, nodeInstance));
        batch.append(mockEvent(ErrorEvent.class, processInstance, nodeInstance));
        batch.append(mockEvent(ProcessCompletedEvent.class, processInstance));
        batch.append(mockEvent(ProcessNodeTriggeredEvent.class, processInstance, nodeInstance));
        batch.append(mockEvent(ProcessNodeLeftEvent.class, processInstance, nodeInstance));
        assertThat(batch.processedEvents).hasExactlyElementsOfTypes(ProcessInstanceStateDataEvent.class, ProcessInstanceVariableDataEvent.class, ProcessInstanceNodeDataEvent.class,
                ProcessInstanceNodeDataEvent.class,
                ProcessInstanceNodeDataEvent.class, ProcessInstanceNodeDataEvent.class, ProcessInstanceErrorDataEvent.class, ProcessInstanceNodeDataEvent.class, ProcessInstanceNodeDataEvent.class,
                ProcessInstanceStateDataEvent.class);
    }

    private <T extends ProcessEvent> T mockEvent(Class<T> clazz, ProcessInstance processInstance) {
        T event = Mockito.mock(clazz);
        Mockito.when(event.getProcessInstance()).thenReturn(processInstance);
        return event;
    }

    private <T extends ProcessNodeEvent> T mockEvent(Class<T> clazz, ProcessInstance processInstance, NodeInstance nodeInstance) {
        T event = Mockito.mock(clazz);
        Mockito.when(event.getProcessInstance()).thenReturn(processInstance);
        Mockito.when(event.getNodeInstance()).thenReturn(nodeInstance);
        return event;
    }
}
