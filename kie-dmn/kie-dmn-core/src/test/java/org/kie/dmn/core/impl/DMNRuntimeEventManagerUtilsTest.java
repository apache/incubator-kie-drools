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
package org.kie.dmn.core.impl;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.api.io.Resource;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.EvaluatorResult;
import org.kie.dmn.api.core.event.AfterConditionalEvaluationEvent;
import org.kie.dmn.api.core.event.AfterEvaluateConditionalEvent;
import org.kie.dmn.api.core.event.DMNRuntimeEventListener;
import org.kie.dmn.api.core.event.DMNRuntimeEventManager;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.internal.utils.DMNRuntimeBuilder;
import org.kie.internal.io.ResourceFactory;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DMNRuntimeEventManagerUtilsTest {

    private static DMNRuntimeEventManager eventManagerMock;
    private static DMNRuntimeEventListener spiedListener;

    @BeforeAll
    static void setUp() {
        spiedListener = spy(DMNRuntimeEventListener.class);
        Set<DMNRuntimeEventListener> listeners = Collections.singleton(spiedListener);
        eventManagerMock = mock(DMNRuntimeEventManager.class);
        when(eventManagerMock.hasListeners()).thenReturn(true);
        when(eventManagerMock.getListeners()).thenReturn(listeners);
    }

    @Test
    void fireAfterEvaluateConditional() {
        EvaluatorResult evaluatorResult = mock(EvaluatorResult.class);
        String executedId = "EXECUTED_ID";
        DMNRuntimeEventManagerUtils.fireAfterEvaluateConditional(eventManagerMock, evaluatorResult, executedId);
        ArgumentCaptor<AfterEvaluateConditionalEvent> evaluateConditionalEventArgumentCaptor = ArgumentCaptor.forClass(AfterEvaluateConditionalEvent.class);
        verify(spiedListener).afterEvaluateConditional (evaluateConditionalEventArgumentCaptor.capture());
        AfterEvaluateConditionalEvent evaluateConditionalEvent = evaluateConditionalEventArgumentCaptor.getValue();
        assertThat(evaluateConditionalEvent).isNotNull();
        assertThat(evaluateConditionalEvent.getEvaluatorResultResult()).isEqualTo(evaluatorResult);
        assertThat(evaluateConditionalEvent.getExecutedId()).isEqualTo(executedId);
    }

    @Test
    void fireAfterConditionalEvaluation() {
        EvaluatorResult evaluatorResult = mock(EvaluatorResult.class);
        String name = "NAME";
        String executedId = "EXECUTED_ID";
        DMNRuntimeEventManagerUtils.fireAfterConditionalEvaluation(eventManagerMock, name, evaluatorResult, executedId);
        ArgumentCaptor<AfterConditionalEvaluationEvent> conditionalEvaluationEventArgumentCaptor = ArgumentCaptor.forClass(AfterConditionalEvaluationEvent.class);
        verify(spiedListener).afterConditionalEvaluation (conditionalEvaluationEventArgumentCaptor.capture());
        AfterConditionalEvaluationEvent evaluateConditionalEvent = conditionalEvaluationEventArgumentCaptor.getValue();
        assertThat(evaluateConditionalEvent).isNotNull();
        assertThat(evaluateConditionalEvent.getNodeName()).isEqualTo(name);
        assertThat(evaluateConditionalEvent.getEvaluatorResultResult()).isEqualTo(evaluatorResult);
        assertThat(evaluateConditionalEvent.getExecutedId()).isEqualTo(executedId);
    }

    @Test
    void testConditionalEvent() {
        EvaluatorResult evaluatorResult = mock(EvaluatorResult.class);
        String name = "B [return]";
        String executedId = "_96D34F2E-3CC0-45A6-9455-2F960361A9CC";
        Resource resource = ResourceFactory.newClassPathResource("valid_models/DMNv1_5/ConditionalEvent.dmn");
        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults().buildConfiguration()
                .fromResources(Collections.singletonList(resource)).getOrElseThrow(RuntimeException::new);
        assertThat(dmnRuntime).isNotNull();
        String nameSpace = "https://kie.org/dmn/_5B448C78-0DBF-4554-92A4-8C0247EB01FD";

        final DMNModel dmnModel = dmnRuntime.getModel(nameSpace, "DMN_00DF4B93-0243-4813-BA70-A1894AC723BE");
        assertThat(dmnModel).isNotNull();
        DMNContext context = DMNFactory.newContext();
        context.set("A", List.of(3));
        DMNResult dmnResult = dmnRuntime.evaluateAll(dmnModel, context);
        assertThat(dmnResult.getDecisionResultByName("B").getResult()).isEqualTo(List.of("pos"));

        DMNRuntimeEventManager eventManager = new DMNRuntimeEventManagerImpl();
        eventManager.addListener(spiedListener);

        DMNRuntimeEventManagerUtils.fireAfterConditionalEvaluation(eventManager, name, evaluatorResult, executedId);
        ArgumentCaptor<AfterConditionalEvaluationEvent> conditionalEvaluationEventArgumentCaptor = ArgumentCaptor.forClass(AfterConditionalEvaluationEvent.class);
        verify(spiedListener).afterConditionalEvaluation (conditionalEvaluationEventArgumentCaptor.capture());
        AfterConditionalEvaluationEvent evaluateConditionalEvent = conditionalEvaluationEventArgumentCaptor.getValue();
        assertThat(evaluateConditionalEvent).isNotNull();
        assertThat(evaluateConditionalEvent.getNodeName()).isEqualTo(name);
        assertThat(evaluateConditionalEvent.getEvaluatorResultResult()).isEqualTo(evaluatorResult);
        assertThat(evaluateConditionalEvent.getExecutedId()).isEqualTo(executedId);
    }
}