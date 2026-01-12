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
package org.kie.kogito.quarkus.serverless.workflow.opentelemetry;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.instance.NodeInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.internal.process.event.KogitoProcessEventListener;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.quarkus.serverless.workflow.opentelemetry.config.SonataFlowOtelConfig;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NodeOtelEventListenerTest {

    @Mock
    private NodeSpanManager spanManager;

    @Mock
    private SonataFlowOtelConfig config;

    @Mock
    private SonataFlowOtelConfig.EventConfig eventConfig;

    @Mock
    private Span mockSpan;

    @Mock
    private KogitoProcessInstance processInstance;

    private NodeOtelEventListener eventListener;

    @Mock
    private HeaderContextExtractor headerExtractor;

    @Mock
    private Node node;

    private NodeInstance jbpmNodeInstance;

    @BeforeEach
    public void setUp() {
        eventListener = new NodeOtelEventListener(spanManager, config, headerExtractor);
        jbpmNodeInstance = org.mockito.Mockito.mock(NodeInstance.class,
                org.mockito.Mockito.withSettings().extraInterfaces(KogitoNodeInstance.class));
    }

    @Test
    public void shouldImplementKogitoProcessEventListener() {
        assertTrue(eventListener instanceof KogitoProcessEventListener);
    }

    @Test
    public void shouldCreateStateSpanOnBeforeNodeTriggered() {
        when(((KogitoNodeInstance) jbpmNodeInstance).getNodeName()).thenReturn("node-1");
        when(processInstance.getId()).thenReturn("process-instance-1");
        when(processInstance.getProcessId()).thenReturn("test-process");
        when(processInstance.getProcessVersion()).thenReturn("1.0.0");
        when(processInstance.getState()).thenReturn(ProcessInstance.STATE_ACTIVE);

        when(jbpmNodeInstance.getNode()).thenReturn(node);
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("state", "TestState");
        when(node.getMetaData()).thenReturn(metadata);

        when(spanManager.createStateSpanWithContext(anyString(), anyString(), anyString(), anyString(), anyString(), any(), any()))
                .thenReturn(mockSpan);

        org.kie.api.event.process.ProcessNodeTriggeredEvent event =
                org.mockito.Mockito.mock(org.kie.api.event.process.ProcessNodeTriggeredEvent.class);
        when(event.getNodeInstance()).thenReturn((KogitoNodeInstance) jbpmNodeInstance);
        when(event.getProcessInstance()).thenReturn(processInstance);

        eventListener.beforeNodeTriggered(event);

        verify(spanManager).createStateSpanWithContext("process-instance-1", "test-process", "1.0.0", "ACTIVE", "TestState", new HashMap<>(), null);
        verify(spanManager).addProcessEvent(mockSpan, "state.started", "State execution started: TestState");
    }

    @Test
    public void shouldSkipSpanCreationForSameState() {
        when(((KogitoNodeInstance) jbpmNodeInstance).getNodeName()).thenReturn("node-1");
        when(processInstance.getId()).thenReturn("process-instance-1");
        when(processInstance.getProcessId()).thenReturn("test-process");
        when(processInstance.getProcessVersion()).thenReturn("1.0.0");
        when(processInstance.getState()).thenReturn(ProcessInstance.STATE_ACTIVE);

        when(jbpmNodeInstance.getNode()).thenReturn(node);
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("state", "TestState");
        when(node.getMetaData()).thenReturn(metadata);

        OtelContextHolder.setActiveState("process-instance-1", "TestState");

        org.kie.api.event.process.ProcessNodeTriggeredEvent event =
                org.mockito.Mockito.mock(org.kie.api.event.process.ProcessNodeTriggeredEvent.class);
        when(event.getNodeInstance()).thenReturn((KogitoNodeInstance) jbpmNodeInstance);
        when(event.getProcessInstance()).thenReturn(processInstance);

        eventListener.beforeNodeTriggered(event);

        verify(spanManager, never()).createStateSpanWithContext(anyString(), anyString(), anyString(), anyString(), anyString(), any(), any());
        verify(spanManager, never()).addProcessEvent(any(Span.class), eq("state.started"), anyString());

        OtelContextHolder.clearActiveState("process-instance-1");
    }

    @Test
    public void shouldEndPreviousStateSpanOnStateTransition() {
        when(((KogitoNodeInstance) jbpmNodeInstance).getNodeName()).thenReturn("node-in-state-b");
        when(processInstance.getId()).thenReturn("process-instance-1");
        when(processInstance.getProcessId()).thenReturn("test-process");
        when(processInstance.getProcessVersion()).thenReturn("1.0.0");
        when(processInstance.getState()).thenReturn(ProcessInstance.STATE_ACTIVE);

        when(jbpmNodeInstance.getNode()).thenReturn(node);
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("state", "StateB");
        when(node.getMetaData()).thenReturn(metadata);

        OtelContextHolder.setActiveState("process-instance-1", "StateA");

        Span stateASpan = org.mockito.Mockito.mock(Span.class);
        when(spanManager.getActiveStateSpan("process-instance-1", "StateA")).thenReturn(stateASpan);

        when(spanManager.createStateSpanWithContext(anyString(), anyString(), anyString(), anyString(), anyString(), any(), any()))
                .thenReturn(mockSpan);

        org.kie.api.event.process.ProcessNodeTriggeredEvent event =
                org.mockito.Mockito.mock(org.kie.api.event.process.ProcessNodeTriggeredEvent.class);
        when(event.getNodeInstance()).thenReturn((KogitoNodeInstance) jbpmNodeInstance);
        when(event.getProcessInstance()).thenReturn(processInstance);

        eventListener.beforeNodeTriggered(event);

        verify(spanManager).getActiveStateSpan("process-instance-1", "StateA");
        verify(spanManager).addProcessEvent(stateASpan, "state.completed", "State execution completed: StateA");
        verify(spanManager).endStateSpan("process-instance-1", "StateA");

        verify(spanManager).createStateSpanWithContext("process-instance-1", "test-process", "1.0.0", "ACTIVE", "StateB", new HashMap<>(), null);
        verify(spanManager).addProcessEvent(mockSpan, "state.started", "State execution started: StateB");

        OtelContextHolder.clearActiveState("process-instance-1");
    }

    @Test
    public void shouldHandleNullStateMetadataGracefully() {
        when(((KogitoNodeInstance) jbpmNodeInstance).getNodeName()).thenReturn("node-without-state");
        when(processInstance.getId()).thenReturn("process-instance-1");

        when(jbpmNodeInstance.getNode()).thenReturn(node);
        Map<String, Object> metadata = new HashMap<>();
        when(node.getMetaData()).thenReturn(metadata);

        org.kie.api.event.process.ProcessNodeTriggeredEvent event =
                org.mockito.Mockito.mock(org.kie.api.event.process.ProcessNodeTriggeredEvent.class);
        when(event.getNodeInstance()).thenReturn((KogitoNodeInstance) jbpmNodeInstance);
        when(event.getProcessInstance()).thenReturn(processInstance);

        eventListener.beforeNodeTriggered(event);

        verify(spanManager, never()).createStateSpanWithContext(anyString(), anyString(), anyString(), anyString(), anyString(), any(), any());
        verify(spanManager, never()).addProcessEvent(any(Span.class), anyString(), anyString());
    }

    @Test
    public void shouldAddProcessStartEventOnlyForFirstState() {
        when(config.events()).thenReturn(eventConfig);
        when(eventConfig.enabled()).thenReturn(true);

        when(((KogitoNodeInstance) jbpmNodeInstance).getNodeName()).thenReturn("Start");
        when(processInstance.getId()).thenReturn("process-instance-1");
        when(processInstance.getProcessId()).thenReturn("test-process");
        when(processInstance.getProcessVersion()).thenReturn("1.0.0");
        when(processInstance.getState()).thenReturn(ProcessInstance.STATE_ACTIVE);

        when(jbpmNodeInstance.getNode()).thenReturn(node);
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("state", "FirstState");
        when(node.getMetaData()).thenReturn(metadata);

        when(spanManager.createStateSpanWithContext(anyString(), anyString(), anyString(), anyString(), anyString(), any(), any()))
                .thenReturn(mockSpan);

        org.kie.api.event.process.ProcessNodeTriggeredEvent event =
                org.mockito.Mockito.mock(org.kie.api.event.process.ProcessNodeTriggeredEvent.class);
        when(event.getNodeInstance()).thenReturn((KogitoNodeInstance) jbpmNodeInstance);
        when(event.getProcessInstance()).thenReturn(processInstance);

        eventListener.beforeNodeTriggered(event);

        verify(spanManager).createStateSpanWithContext("process-instance-1", "test-process", "1.0.0", "ACTIVE", "FirstState", new HashMap<>(), null);
        verify(spanManager).addProcessEvent(mockSpan, "state.started", "State execution started: FirstState");
        verify(spanManager).addProcessEvent(eq(mockSpan), eq("process.instance.start"), any(Attributes.class));
    }

    @Test
    public void shouldNotAddProcessStartEventForSubsequentStates() {
        when(((KogitoNodeInstance) jbpmNodeInstance).getNodeName()).thenReturn("ChooseOnLanguage");
        when(processInstance.getId()).thenReturn("process-instance-1");
        when(processInstance.getProcessId()).thenReturn("test-process");
        when(processInstance.getProcessVersion()).thenReturn("1.0.0");
        when(processInstance.getState()).thenReturn(ProcessInstance.STATE_ACTIVE);

        when(jbpmNodeInstance.getNode()).thenReturn(node);
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("state", "SecondState");
        when(node.getMetaData()).thenReturn(metadata);

        OtelContextHolder.setActiveState("process-instance-1", "FirstState");
        Span firstStateSpan = org.mockito.Mockito.mock(Span.class);
        when(spanManager.getActiveStateSpan("process-instance-1", "FirstState")).thenReturn(firstStateSpan);

        when(spanManager.createStateSpanWithContext(anyString(), anyString(), anyString(), anyString(), anyString(), any(), any()))
                .thenReturn(mockSpan);

        org.kie.api.event.process.ProcessNodeTriggeredEvent event =
                org.mockito.Mockito.mock(org.kie.api.event.process.ProcessNodeTriggeredEvent.class);
        when(event.getNodeInstance()).thenReturn((KogitoNodeInstance) jbpmNodeInstance);
        when(event.getProcessInstance()).thenReturn(processInstance);

        eventListener.beforeNodeTriggered(event);

        verify(spanManager).createStateSpanWithContext("process-instance-1", "test-process", "1.0.0", "ACTIVE", "SecondState", new HashMap<>(), null);
        verify(spanManager).addProcessEvent(mockSpan, "state.started", "State execution started: SecondState");
        verify(spanManager, never()).addProcessEvent(eq(mockSpan), eq("process.instance.start"), any(Attributes.class));

        OtelContextHolder.clearActiveState("process-instance-1");
    }

}
