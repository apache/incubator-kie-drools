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
package org.kie.kogito.internal.process.workitem;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.api.definition.process.NodeContainer;
import org.kie.kogito.internal.process.runtime.KogitoNode;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.internal.process.workitem.WorkItemRecordParameters.RECORD_ARGS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkItemRecordParametersTest {

    @Mock
    private KogitoWorkItem workItem;

    @Mock
    private KogitoNodeInstance nodeInstance;

    @Mock
    private KogitoNode node;

    @Test
    void testRecordInputParameters_WithWorkItem_NodeMetadataTrue() {
        Map<String, Object> nodeMetadata = new HashMap<>();
        nodeMetadata.put(RECORD_ARGS, true);

        Map<String, Object> nodeInstanceMetadata = new HashMap<>();
        Map<String, Object> inputArgs = Map.of("param1", "value1");

        when(workItem.getNodeInstance()).thenReturn(nodeInstance);
        when(nodeInstance.getNode()).thenReturn(node);
        when(nodeInstance.getMetaData()).thenReturn(nodeInstanceMetadata);
        when(node.getMetaData()).thenReturn(nodeMetadata);

        assertThat(nodeInstanceMetadata).isEmpty();

        WorkItemRecordParameters.recordInputParameters(workItem, inputArgs);

        verify(node).getMetaData();
        verify(nodeInstance).getMetaData();
        assertThat(nodeInstanceMetadata).hasSize(1);
        assertThat(nodeInstanceMetadata).containsEntry("inputArgs", inputArgs);
    }

    @Test
    void testRecordInputParameters_WithWorkItem_NodeMetadataFalse() {
        Map<String, Object> nodeMetadata = new HashMap<>();
        nodeMetadata.put(RECORD_ARGS, false);
        Map<String, Object> inputArgs = Map.of("param1", "value1");

        when(workItem.getNodeInstance()).thenReturn(nodeInstance);
        when(nodeInstance.getNode()).thenReturn(node);
        when(node.getMetaData()).thenReturn(nodeMetadata);

        WorkItemRecordParameters.recordInputParameters(workItem, inputArgs);

        verify(nodeInstance, never()).getMetaData();
    }

    @Test
    void testRecordInputParameters_WithArgNames_RecordsSpecifiedArgs() {
        Map<String, Object> nodeMetadata = new HashMap<>();
        nodeMetadata.put(RECORD_ARGS, true);

        Map<String, Object> nodeInstanceMetadata = new HashMap<>();

        when(workItem.getNodeInstance()).thenReturn(nodeInstance);
        when(nodeInstance.getNode()).thenReturn(node);
        when(nodeInstance.getMetaData()).thenReturn(nodeInstanceMetadata);
        when(node.getMetaData()).thenReturn(nodeMetadata);
        when(workItem.getParameter("arg1")).thenReturn("value1");
        when(workItem.getParameter("arg2")).thenReturn(42);

        assertThat(nodeInstanceMetadata).isEmpty();

        WorkItemRecordParameters.recordInputParameters(workItem, "arg1", "arg2");

        verify(node).getMetaData();
        verify(workItem).getParameter("arg1");
        verify(workItem).getParameter("arg2");
        verify(nodeInstance).getMetaData();
        assertThat(nodeInstanceMetadata).hasSize(1);
        @SuppressWarnings("unchecked")
        Map<String, Object> recorded = (Map<String, Object>) nodeInstanceMetadata.get("inputArgs");
        assertThat(recorded).containsEntry("arg1", "value1");
        assertThat(recorded).containsEntry("arg2", 42);
    }

    @Test
    void testRecordInputParameters_WithEmptyArgNames_DoesNotRecord() {
        WorkItemRecordParameters.recordInputParameters(workItem);

        verify(workItem, never()).getNodeInstance();
    }

    @Test
    void testRecordOutputParameters_WithWorkItem_ExtractsResult() {
        Map<String, Object> nodeMetadata = new HashMap<>();
        nodeMetadata.put(RECORD_ARGS, true);

        Map<String, Object> nodeInstanceMetadata = new HashMap<>();
        Map<String, Object> outputArgs = Map.of("Result", "success", "other", "value");

        when(workItem.getNodeInstance()).thenReturn(nodeInstance);
        when(nodeInstance.getNode()).thenReturn(node);
        when(nodeInstance.getMetaData()).thenReturn(nodeInstanceMetadata);
        when(node.getMetaData()).thenReturn(nodeMetadata);

        assertThat(nodeInstanceMetadata).isEmpty();

        WorkItemRecordParameters.recordOutputParameters(workItem, outputArgs);

        verify(node).getMetaData();
        verify(nodeInstance).getMetaData();
        assertThat(nodeInstanceMetadata).hasSize(1);
        assertThat(nodeInstanceMetadata).containsEntry("outputArgs", "success");
    }

    @Test
    void testRecordOutputParameters_WithWorkItem_NoResult_UsesFullMap() {
        Map<String, Object> nodeMetadata = new HashMap<>();
        nodeMetadata.put(RECORD_ARGS, true);

        Map<String, Object> nodeInstanceMetadata = new HashMap<>();
        Map<String, Object> outputArgs = Map.of("status", "completed");

        when(workItem.getNodeInstance()).thenReturn(nodeInstance);
        when(nodeInstance.getNode()).thenReturn(node);
        when(nodeInstance.getMetaData()).thenReturn(nodeInstanceMetadata);
        when(node.getMetaData()).thenReturn(nodeMetadata);

        assertThat(nodeInstanceMetadata).isEmpty();

        WorkItemRecordParameters.recordOutputParameters(workItem, outputArgs);

        verify(node).getMetaData();
        verify(nodeInstance).getMetaData();
        assertThat(nodeInstanceMetadata).hasSize(1);
        assertThat(nodeInstanceMetadata).containsEntry("outputArgs", outputArgs);
    }

    @Test
    void testRecordInputParameters_ProcessMetadataFallback() {
        Map<String, Object> nodeMetadata = new HashMap<>();
        Map<String, Object> nodeInstanceMetadata = new HashMap<>();
        Map<String, Object> processMetadata = new HashMap<>();
        processMetadata.put(RECORD_ARGS, true);
        Map<String, Object> inputArgs = Map.of("param1", "value1");

        NodeContainer parentContainer = mock(NodeContainer.class,
                org.mockito.Mockito.withSettings().extraInterfaces(org.kie.api.definition.process.Process.class));
        when(((org.kie.api.definition.process.Process) parentContainer).getMetaData()).thenReturn(processMetadata);

        when(workItem.getNodeInstance()).thenReturn(nodeInstance);
        when(nodeInstance.getNode()).thenReturn(node);
        when(nodeInstance.getMetaData()).thenReturn(nodeInstanceMetadata);
        when(node.getMetaData()).thenReturn(nodeMetadata);
        when(((KogitoNode) node).getParentContainer()).thenReturn(parentContainer);

        assertThat(nodeInstanceMetadata).isEmpty();

        WorkItemRecordParameters.recordInputParameters(workItem, inputArgs);

        verify(node).getMetaData();
        verify((org.kie.api.definition.process.Process) parentContainer).getMetaData();
        assertThat(nodeInstanceMetadata).hasSize(1);
        assertThat(nodeInstanceMetadata).containsEntry("inputArgs", inputArgs);
    }

    @Test
    void testBooleanConversion_InvalidString() {
        Map<String, Object> nodeMetadata = new HashMap<>();
        nodeMetadata.put(RECORD_ARGS, "invalid");
        Object inputArgs = Map.of("param1", "value1");

        when(nodeInstance.getNode()).thenReturn(node);
        when(node.getMetaData()).thenReturn(nodeMetadata);

        WorkItemRecordParameters.recordInputParameters(nodeInstance, inputArgs);

        verify(nodeInstance, never()).getMetaData();
    }

    @Test
    void testBooleanConversion_NonBooleanNonString() {
        Map<String, Object> nodeMetadata = new HashMap<>();
        nodeMetadata.put(RECORD_ARGS, 123);
        Object inputArgs = Map.of("param1", "value1");

        when(nodeInstance.getNode()).thenReturn(node);
        when(node.getMetaData()).thenReturn(nodeMetadata);

        WorkItemRecordParameters.recordInputParameters(nodeInstance, inputArgs);

        verify(nodeInstance, never()).getMetaData();
    }

    @Test
    void testRecordInputParameters_NullNode() {
        Object inputArgs = Map.of("param1", "value1");

        when(nodeInstance.getNode()).thenReturn(null);

        WorkItemRecordParameters.recordInputParameters(nodeInstance, inputArgs);

        verify(nodeInstance, never()).getMetaData();
    }

    @Test
    void testRecordInputParameters_ProcessMetadataFallback_WithSubprocess() {
        // Test that process-level metadata is found even when node is inside a subprocess
        Map<String, Object> nodeMetadata = new HashMap<>();
        Map<String, Object> nodeInstanceMetadata = new HashMap<>();
        Map<String, Object> processMetadata = new HashMap<>();
        processMetadata.put(RECORD_ARGS, true);
        Map<String, Object> inputArgs = Map.of("param1", "value1");

        // Create a subprocess (NodeContainer that is also a KogitoNode)
        NodeContainer subprocess = mock(NodeContainer.class,
                org.mockito.Mockito.withSettings().extraInterfaces(KogitoNode.class));

        // Create the process
        NodeContainer process = mock(NodeContainer.class,
                org.mockito.Mockito.withSettings().extraInterfaces(org.kie.api.definition.process.Process.class));
        when(((org.kie.api.definition.process.Process) process).getMetaData()).thenReturn(processMetadata);

        // Setup hierarchy: node -> subprocess -> process
        when(workItem.getNodeInstance()).thenReturn(nodeInstance);
        when(nodeInstance.getNode()).thenReturn(node);
        when(nodeInstance.getMetaData()).thenReturn(nodeInstanceMetadata);
        when(node.getMetaData()).thenReturn(nodeMetadata);
        when(((KogitoNode) node).getParentContainer()).thenReturn(subprocess);
        when(((KogitoNode) subprocess).getParentContainer()).thenReturn(process);

        assertThat(nodeInstanceMetadata).isEmpty();

        WorkItemRecordParameters.recordInputParameters(workItem, inputArgs);

        verify(node).getMetaData();
        verify((org.kie.api.definition.process.Process) process).getMetaData();
        assertThat(nodeInstanceMetadata).hasSize(1);
        assertThat(nodeInstanceMetadata).containsEntry("inputArgs", inputArgs);
    }
}
