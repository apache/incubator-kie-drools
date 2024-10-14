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
package org.kie.dmn.core.ast;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.EvaluatorResult;
import org.kie.dmn.api.core.event.AfterConditionalEvaluationEvent;
import org.kie.dmn.api.core.event.AfterEvaluateConditionalEvent;
import org.kie.dmn.api.core.event.DMNRuntimeEventListener;
import org.kie.dmn.api.core.event.DMNRuntimeEventManager;
import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.core.impl.DMNResultImpl;
import org.kie.dmn.model.api.ChildExpression;
import org.kie.dmn.model.api.Conditional;
import org.kie.dmn.model.api.DMNElement;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.Expression;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DMNConditionalEvaluatorTest {

    private static final String ID_ELEMENT_ID = "ID_ELEMENT_ID";
    private static final String THEN_ELEMENT_ID = "THEN_ELEMENT_ID";
    private static final String ELSE_ELEMENT_ID = "ELSE_ELEMENT_ID";
    private static DMNRuntimeEventManager eventManagerMock;
    private static DMNRuntimeEventListener spiedListener;
    private static EvaluatorResult ifEvaluationMock;
    private static EvaluatorResult thenEvaluationMock;
    private static EvaluatorResult elseEvaluationMock;
    private static DMNResultImpl dmnResultMock;
    private static DMNConditionalEvaluator dmnConditionalEvaluator;

    @BeforeAll
    static void setUp() {
        spiedListener = spy(DMNRuntimeEventListener.class);
        Set<DMNRuntimeEventListener> listeners = Collections.singleton(spiedListener);
        eventManagerMock = mock(DMNRuntimeEventManager.class);
        when(eventManagerMock.hasListeners()).thenReturn(true);
        when(eventManagerMock.getListeners()).thenReturn(listeners);
        ifEvaluationMock = mock(EvaluatorResult.class);
        thenEvaluationMock = mock(EvaluatorResult.class);
        elseEvaluationMock = mock(EvaluatorResult.class);

        dmnResultMock = mock(DMNResultImpl.class);
        DMNExpressionEvaluator ifEvaluatorMock = mock(DMNExpressionEvaluator.class);
        DMNExpressionEvaluator thenEvaluatorMock = mock(DMNExpressionEvaluator.class);
        DMNExpressionEvaluator elseEvaluatorMock = mock(DMNExpressionEvaluator.class);

        when(ifEvaluatorMock.evaluate(eventManagerMock, dmnResultMock)).thenReturn(ifEvaluationMock);
        when(thenEvaluatorMock.evaluate(eventManagerMock, dmnResultMock)).thenReturn(thenEvaluationMock);
        when(elseEvaluatorMock.evaluate(eventManagerMock, dmnResultMock)).thenReturn(elseEvaluationMock);

        ChildExpression ifMock = mock(ChildExpression.class);
        when(ifMock.getId()).thenReturn(ID_ELEMENT_ID);

        ChildExpression thenMock = mock(ChildExpression.class);
        when(thenMock.getId()).thenReturn(THEN_ELEMENT_ID);

        ChildExpression elseMock = mock(ChildExpression.class);
        when(elseMock.getId()).thenReturn(ELSE_ELEMENT_ID);

        Conditional conditionalMock = mock(Conditional.class);
        when(conditionalMock.getIf()).thenReturn(ifMock);
        when(conditionalMock.getThen()).thenReturn(thenMock);
        when(conditionalMock.getElse()).thenReturn(elseMock);

        List<DMNModelInstrumentedBase> nodeChildren = Collections.singletonList(conditionalMock);
        DMNElement nodeMock = mock(DMNElement.class);
        when(nodeMock.getChildren()).thenReturn(nodeChildren);

        dmnConditionalEvaluator = new DMNConditionalEvaluator("name",
                                                              nodeMock,
                                                              ifEvaluatorMock,
                                                              thenEvaluatorMock,
                                                              elseEvaluatorMock);
    }

    @BeforeEach
    void setup() {
        reset(spiedListener);
    }

    @Test
    void evaluateListenerInvocation() {
        when(ifEvaluationMock.getResultType()).thenReturn(EvaluatorResult.ResultType.FAILURE); // not interested in
        // nested execution

        dmnConditionalEvaluator.evaluate(eventManagerMock, dmnResultMock);
        ArgumentCaptor<AfterEvaluateConditionalEvent> evaluateConditionalEventArgumentCaptor =
                ArgumentCaptor.forClass(AfterEvaluateConditionalEvent.class);
        verify(spiedListener).afterEvaluateConditional(evaluateConditionalEventArgumentCaptor.capture());
        AfterEvaluateConditionalEvent evaluateConditionalEvent = evaluateConditionalEventArgumentCaptor.getValue();
        assertThat(evaluateConditionalEvent).isNotNull();
        assertThat(evaluateConditionalEvent.getEvaluatorResultResult()).isEqualTo(ifEvaluationMock);
        assertThat(evaluateConditionalEvent.getExecutedId()).isEqualTo(ID_ELEMENT_ID);
    }

    @Test
    void evaluateManageBooleanOrNullIfResultInvocation() {
        when(ifEvaluationMock.getResultType()).thenReturn(EvaluatorResult.ResultType.SUCCESS);
        when(ifEvaluationMock.getResult()).thenReturn(true);

        DMNConditionalEvaluator spiedDmnConditionalEvaluator = spy(dmnConditionalEvaluator);
        spiedDmnConditionalEvaluator.evaluate(eventManagerMock, dmnResultMock);
        verify(spiedDmnConditionalEvaluator).manageBooleanOrNullIfResult(true, eventManagerMock, dmnResultMock);
    }

    @Test
    void manageBooleanOrNullIfResultWithTrue() {
        dmnConditionalEvaluator.manageBooleanOrNullIfResult(true, eventManagerMock, dmnResultMock);
        ArgumentCaptor<AfterConditionalEvaluationEvent> afterConditionalEvaluationEventArgumentCaptor =
                ArgumentCaptor.forClass(AfterConditionalEvaluationEvent.class);
        verify(spiedListener).afterConditionalEvaluation(afterConditionalEvaluationEventArgumentCaptor.capture());
        AfterConditionalEvaluationEvent conditionalEvaluationEvent =
                afterConditionalEvaluationEventArgumentCaptor.getValue();
        assertThat(conditionalEvaluationEvent).isNotNull();
        assertThat(conditionalEvaluationEvent.getEvaluatorResultResult()).isEqualTo(thenEvaluationMock);
        assertThat(conditionalEvaluationEvent.getExecutedId()).isEqualTo(THEN_ELEMENT_ID);
    }

    @Test
    void manageBooleanOrNullIfResultWithFalse() {
        dmnConditionalEvaluator.manageBooleanOrNullIfResult(false, eventManagerMock, dmnResultMock);
        ArgumentCaptor<AfterConditionalEvaluationEvent> afterConditionalEvaluationEventArgumentCaptor =
                ArgumentCaptor.forClass(AfterConditionalEvaluationEvent.class);
        verify(spiedListener).afterConditionalEvaluation(afterConditionalEvaluationEventArgumentCaptor.capture());
        AfterConditionalEvaluationEvent conditionalEvaluationEvent =
                afterConditionalEvaluationEventArgumentCaptor.getValue();
        assertThat(conditionalEvaluationEvent).isNotNull();
        assertThat(conditionalEvaluationEvent.getEvaluatorResultResult()).isEqualTo(elseEvaluationMock);
        assertThat(conditionalEvaluationEvent.getExecutedId()).isEqualTo(ELSE_ELEMENT_ID);
    }

    @Test
    void manageBooleanOrNullIfResultWithNull() {
        dmnConditionalEvaluator.manageBooleanOrNullIfResult(null, eventManagerMock, dmnResultMock);
        ArgumentCaptor<AfterConditionalEvaluationEvent> afterConditionalEvaluationEventArgumentCaptor =
                ArgumentCaptor.forClass(AfterConditionalEvaluationEvent.class);
        verify(spiedListener).afterConditionalEvaluation(afterConditionalEvaluationEventArgumentCaptor.capture());
        AfterConditionalEvaluationEvent conditionalEvaluationEvent =
                afterConditionalEvaluationEventArgumentCaptor.getValue();
        assertThat(conditionalEvaluationEvent).isNotNull();
        assertThat(conditionalEvaluationEvent.getEvaluatorResultResult()).isEqualTo(elseEvaluationMock);
        assertThat(conditionalEvaluationEvent.getExecutedId()).isEqualTo(ELSE_ELEMENT_ID);
    }
}