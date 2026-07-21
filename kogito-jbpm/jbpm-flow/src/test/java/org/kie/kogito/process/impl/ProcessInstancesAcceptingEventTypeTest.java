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
package org.kie.kogito.process.impl;

import java.util.Collections;
import java.util.stream.Stream;

import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.Model;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.flexible.AdHocFragment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for acceptingEventType() method in ProcessInstances implementations.
 * Tests both traditional signal events and ad hoc node triggering.
 */
class ProcessInstancesAcceptingEventTypeTest {

    private MapProcessInstances<Model> processInstances;
    private AbstractProcess<Model> process;
    private WorkflowProcessInstance workflowProcessInstance;
    private AbstractProcessInstance<Model> processInstance;

    @BeforeEach
    void setup() {
        process = mock(AbstractProcess.class);
        processInstances = new MapProcessInstances<>(process);
        workflowProcessInstance = mock(WorkflowProcessInstance.class);
        processInstance = mock(AbstractProcessInstance.class);
    }

    @Test
    void testAcceptingEventType_WaitingForSignalEvent() {
        // Given: Process instance waiting for a signal event
        String processInstanceId = "test-accepting-event-instance-1";
        String signalName = "HelloMartin";

        when(workflowProcessInstance.getStringId()).thenReturn(processInstanceId);
        when(workflowProcessInstance.getEventTypes()).thenReturn(new String[] { signalName, "OtherSignal" });
        when(processInstance.id()).thenReturn(processInstanceId);
        when(processInstance.internalGetProcessInstance()).thenReturn(workflowProcessInstance);
        when(processInstance.adHocFragments()).thenReturn(Collections.emptyList());
        when(process.createInstance(any(WorkflowProcessInstance.class))).thenAnswer(invocation -> processInstance);
        when(process.createReadOnlyInstance(any(WorkflowProcessInstance.class))).thenAnswer(invocation -> processInstance);

        // Store the instance
        processInstances.create(processInstanceId, processInstance);

        // When: Check if accepting the signal
        Stream<ProcessInstance<Model>> result = processInstances.acceptingEventType(signalName, processInstanceId);

        // Then: Should return the process instance
        assertThat(result).isNotNull();
        assertThat(result.count()).isEqualTo(1);
    }

    @Test
    void testAcceptingEventType_AdHocNode() {
        // Given: Process instance with ad hoc node
        String processInstanceId = "test-accepting-event-instance-2";
        String adHocNodeName = "AdHocTask";

        AdHocFragment adHocFragment = new AdHocFragment.Builder(adHocNodeName)
                .withName(adHocNodeName)
                .withAutoStart(false)
                .build();

        when(workflowProcessInstance.getStringId()).thenReturn(processInstanceId);
        when(workflowProcessInstance.getEventTypes()).thenReturn(new String[] {});
        when(processInstance.id()).thenReturn(processInstanceId);
        when(processInstance.internalGetProcessInstance()).thenReturn(workflowProcessInstance);
        when(processInstance.adHocFragments()).thenReturn(Collections.singletonList(adHocFragment));
        when(process.createInstance(any(WorkflowProcessInstance.class))).thenReturn(processInstance);

        // Store the instance
        processInstances.create(processInstanceId, processInstance);

        // When: Check if accepting the ad hoc node signal
        Stream<ProcessInstance<Model>> result = processInstances.acceptingEventType(adHocNodeName, processInstanceId);

        // Then: Should return the process instance
        assertThat(result).isNotNull();
        assertThat(result.count()).isEqualTo(1);
    }

    @Test
    void testAcceptingEventType_BothSignalAndAdHoc() {
        // Given: Process instance with both signal event and ad hoc node
        String processInstanceId = "test-accepting-event-instance-3";
        String signalName = "HelloMartin";
        String adHocNodeName = "AdHocTask";

        AdHocFragment adHocFragment = new AdHocFragment.Builder(adHocNodeName)
                .withName(adHocNodeName)
                .withAutoStart(false)
                .build();

        when(workflowProcessInstance.getStringId()).thenReturn(processInstanceId);
        when(workflowProcessInstance.getEventTypes()).thenReturn(new String[] { signalName });
        when(processInstance.id()).thenReturn(processInstanceId);
        when(processInstance.internalGetProcessInstance()).thenReturn(workflowProcessInstance);
        when(processInstance.adHocFragments()).thenReturn(Collections.singletonList(adHocFragment));
        // Ensure the mock always returns a valid instance, not just on first call
        when(process.createInstance(any(WorkflowProcessInstance.class))).thenAnswer(invocation -> processInstance);
        when(process.createReadOnlyInstance(any(WorkflowProcessInstance.class))).thenAnswer(invocation -> processInstance);

        // Store the instance
        processInstances.create(processInstanceId, processInstance);

        // When: Check if accepting the signal
        Stream<ProcessInstance<Model>> resultSignal = processInstances.acceptingEventType(signalName, processInstanceId);

        // Then: Should return the process instance for signal
        assertThat(resultSignal).isNotNull();
        assertThat(resultSignal.count()).isEqualTo(1);

        // When: Check if accepting the ad hoc node
        Stream<ProcessInstance<Model>> resultAdHoc = processInstances.acceptingEventType(adHocNodeName, processInstanceId);

        // Then: Should return the process instance for ad hoc node
        assertThat(resultAdHoc).isNotNull();
        assertThat(resultAdHoc.count()).isEqualTo(1);
    }

    @Test
    void testAcceptingEventType_NotAccepting() {
        // Given: Process instance not waiting for signal and no ad hoc node
        String processInstanceId = "test-accepting-event-instance-4";
        String waitingFor = "HelloMartin";
        String notWaitingFor = "ByeMartin";

        when(workflowProcessInstance.getStringId()).thenReturn(processInstanceId);
        when(workflowProcessInstance.getEventTypes()).thenReturn(new String[] { waitingFor });
        when(processInstance.id()).thenReturn(processInstanceId);
        when(processInstance.internalGetProcessInstance()).thenReturn(workflowProcessInstance);
        when(processInstance.adHocFragments()).thenReturn(Collections.emptyList());
        when(process.createInstance(any(WorkflowProcessInstance.class))).thenReturn(processInstance);

        // Store the instance
        processInstances.create(processInstanceId, processInstance);

        // When: Check if accepting a signal it's not waiting for
        Stream<ProcessInstance<Model>> result = processInstances.acceptingEventType(notWaitingFor, processInstanceId);

        // Then: Should return empty stream
        assertThat(result).isNotNull();
        assertThat(result.count()).isZero();
    }

    @Test
    void testAcceptingEventType_NonExistentInstance() {
        // Given: Non-existent process instance
        String processInstanceId = "non-existent";
        String signalName = "HelloMartin";

        // When: Check if accepting signal for non-existent instance
        Stream<ProcessInstance<Model>> result = processInstances.acceptingEventType(signalName, processInstanceId);

        // Then: Should return empty stream
        assertThat(result).isNotNull();
        assertThat(result.count()).isZero();
    }

    @Test
    void testAcceptingEventType_MultipleAdHocNodes() {
        // Given: Process instance with multiple ad hoc nodes
        String processInstanceId = "test-accepting-event-instance-5";
        String adHocNode1 = "AdHocTask1";
        String adHocNode2 = "AdHocTask2";
        String adHocNode3 = "AdHocTask3";

        AdHocFragment fragment1 = new AdHocFragment.Builder(adHocNode1)
                .withName(adHocNode1)
                .withAutoStart(false)
                .build();
        AdHocFragment fragment2 = new AdHocFragment.Builder(adHocNode2)
                .withName(adHocNode2)
                .withAutoStart(true)
                .build();
        AdHocFragment fragment3 = new AdHocFragment.Builder(adHocNode3)
                .withName(adHocNode3)
                .withAutoStart(false)
                .build();

        when(workflowProcessInstance.getStringId()).thenReturn(processInstanceId);
        when(workflowProcessInstance.getEventTypes()).thenReturn(new String[] {});
        when(processInstance.id()).thenReturn(processInstanceId);
        when(processInstance.internalGetProcessInstance()).thenReturn(workflowProcessInstance);
        when(processInstance.adHocFragments()).thenReturn(java.util.Arrays.asList(fragment1, fragment2, fragment3));
        when(process.createInstance(any(WorkflowProcessInstance.class))).thenReturn(processInstance);

        // Store the instance
        processInstances.create(processInstanceId, processInstance);

        // When/Then: Check each ad hoc node
        Stream<ProcessInstance<Model>> result1 = processInstances.acceptingEventType(adHocNode1, processInstanceId);
        assertThat(result1.count()).isEqualTo(1);

        Stream<ProcessInstance<Model>> result2 = processInstances.acceptingEventType(adHocNode2, processInstanceId);
        assertThat(result2.count()).isEqualTo(1);

        Stream<ProcessInstance<Model>> result3 = processInstances.acceptingEventType(adHocNode3, processInstanceId);
        assertThat(result3.count()).isEqualTo(1);

        // When: Check non-existent ad hoc node
        Stream<ProcessInstance<Model>> resultNone = processInstances.acceptingEventType("NonExistent", processInstanceId);
        assertThat(resultNone.count()).isZero();
    }

    @Test
    void testAcceptingEventType_NullEventTypes() {
        // Given: Process instance with null event types
        String processInstanceId = "test-accepting-event-instance-6";
        String signalName = "HelloMartin";

        when(workflowProcessInstance.getStringId()).thenReturn(processInstanceId);
        when(workflowProcessInstance.getEventTypes()).thenReturn(new String[] { null, signalName, null });
        when(processInstance.id()).thenReturn(processInstanceId);
        when(processInstance.internalGetProcessInstance()).thenReturn(workflowProcessInstance);
        when(processInstance.adHocFragments()).thenReturn(Collections.emptyList());
        when(process.createInstance(any(WorkflowProcessInstance.class))).thenAnswer(invocation -> processInstance);
        when(process.createReadOnlyInstance(any(WorkflowProcessInstance.class))).thenAnswer(invocation -> processInstance);

        // Store the instance
        processInstances.create(processInstanceId, processInstance);

        // When: Check if accepting the signal
        Stream<ProcessInstance<Model>> result = processInstances.acceptingEventType(signalName, processInstanceId);

        // Then: Should handle null values and return the instance
        assertThat(result).isNotNull();
        assertThat(result.count()).isEqualTo(1);
    }
}
