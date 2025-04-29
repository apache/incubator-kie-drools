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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.dmn.api.core.EvaluatorResult;
import org.kie.dmn.api.core.event.AfterConditionalEvaluationEvent;
import org.kie.dmn.api.core.event.AfterEvaluateConditionalEvent;
import org.kie.dmn.api.core.event.DMNRuntimeEventListener;
import org.kie.dmn.api.core.event.DMNRuntimeEventManager;
import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.core.impl.DMNResultImpl;
import org.kie.dmn.model.api.BusinessKnowledgeModel;
import org.kie.dmn.model.api.DMNElement;
import org.kie.dmn.model.api.Decision;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatRuntimeException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DMNConditionalEvaluatorTest {

    private static final String DECISION_NAME = "DECISION_NAME";
    private static final String BKM_NAME = "BKM_NAME";
    private static final String IF_ELEMENT_ID = "IF_ELEMENT_ID";
    private static final String THEN_ELEMENT_ID = "THEN_ELEMENT_ID";
    private static final String ELSE_ELEMENT_ID = "ELSE_ELEMENT_ID";
    private static final Map<DMNConditionalEvaluator.EvaluatorIdentifier, DMNExpressionEvaluator> EVALUATOR_ID_MAP = new HashMap<>();
    private static DMNConditionalEvaluator dmnConditionalEvaluatorBkmNode;
    private static DMNConditionalEvaluator dmnConditionalEvaluatorDecisionNode;
    private static DMNConditionalEvaluator.EvaluatorIdentifier ifIdentifier;
    private static DMNConditionalEvaluator.EvaluatorIdentifier thenIdentifier;
    private static DMNConditionalEvaluator.EvaluatorIdentifier elseIdentifier;
    private static DMNRuntimeEventManager eventManagerMock;
    private static DMNRuntimeEventListener spiedListener;
    private static DMNResultImpl dmnResultMock;
    private static EvaluatorResult ifEvaluationMock;
    private static EvaluatorResult thenEvaluationMock;
    private static EvaluatorResult elseEvaluationMock;

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

        Decision decisionMock = mock(Decision.class);
        when(decisionMock.getName()).thenReturn(DECISION_NAME);
        DMNElement dmnElementMocked = mock(DMNElement.class);
        when(dmnElementMocked.getParentDRDElement()).thenReturn(decisionMock);

        BusinessKnowledgeModel bkmMock = mock(BusinessKnowledgeModel.class);
        when(bkmMock.getName()).thenReturn(BKM_NAME);
        DMNElement dmnElementBkmMocked = mock(DMNElement.class);
        when(dmnElementBkmMocked.getParentDRDElement()).thenReturn(bkmMock);

        ifIdentifier = new DMNConditionalEvaluator.EvaluatorIdentifier(IF_ELEMENT_ID, DMNConditionalEvaluator.EvaluatorType.IF);
        thenIdentifier = new DMNConditionalEvaluator.EvaluatorIdentifier(THEN_ELEMENT_ID, DMNConditionalEvaluator.EvaluatorType.THEN);
        elseIdentifier = new DMNConditionalEvaluator.EvaluatorIdentifier(ELSE_ELEMENT_ID, DMNConditionalEvaluator.EvaluatorType.ELSE);

        EVALUATOR_ID_MAP.put(ifIdentifier, ifEvaluatorMock);
        EVALUATOR_ID_MAP.put(thenIdentifier, thenEvaluatorMock);
        EVALUATOR_ID_MAP.put(elseIdentifier, elseEvaluatorMock);

        dmnConditionalEvaluatorDecisionNode = new DMNConditionalEvaluator("name", dmnElementMocked, EVALUATOR_ID_MAP);
        dmnConditionalEvaluatorBkmNode = new DMNConditionalEvaluator("name", dmnElementBkmMocked, EVALUATOR_ID_MAP);
    }

    @BeforeEach
    void setup() {
        reset(spiedListener);
    }

    @ParameterizedTest
    @MethodSource("dmnConditionEvaluators")
    void evaluateListenerInvocation(DMNConditionalEvaluator dmnConditionalEvaluator) {
        when(ifEvaluationMock.getResultType()).thenReturn(EvaluatorResult.ResultType.FAILURE); // not interested in
        // nested execution

        dmnConditionalEvaluator.evaluate(eventManagerMock, dmnResultMock);
        ArgumentCaptor<AfterEvaluateConditionalEvent> evaluateConditionalEventArgumentCaptor =
                ArgumentCaptor.forClass(AfterEvaluateConditionalEvent.class);
        verify(spiedListener).afterEvaluateConditional(evaluateConditionalEventArgumentCaptor.capture());
        AfterEvaluateConditionalEvent evaluateConditionalEvent = evaluateConditionalEventArgumentCaptor.getValue();
        assertThat(evaluateConditionalEvent).isNotNull();
        assertThat(evaluateConditionalEvent.getEvaluatorResultResult()).isEqualTo(ifEvaluationMock);
        assertThat(evaluateConditionalEvent.getExecutedId()).isEqualTo(IF_ELEMENT_ID);
    }

    @ParameterizedTest
    @MethodSource("dmnConditionEvaluators")
    void evaluateManageBooleanOrNullIfResultInvocation(DMNConditionalEvaluator dmnConditionalEvaluator) {
        when(ifEvaluationMock.getResultType()).thenReturn(EvaluatorResult.ResultType.SUCCESS);
        when(ifEvaluationMock.getResult()).thenReturn(true);

        DMNConditionalEvaluator spiedDmnConditionalEvaluator = spy(dmnConditionalEvaluator);
        spiedDmnConditionalEvaluator.evaluate(eventManagerMock, dmnResultMock);
        verify(spiedDmnConditionalEvaluator).manageBooleanOrNullIfResult(true, eventManagerMock, dmnResultMock);
    }

    @ParameterizedTest
    @MethodSource("dmnConditionEvaluators")
    void manageBooleanOrNullIfResultWithTrue(DMNConditionalEvaluator dmnConditionalEvaluator) {
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

    @ParameterizedTest
    @MethodSource("dmnConditionEvaluators")
    void manageBooleanOrNullIfResultWithFalse(DMNConditionalEvaluator dmnConditionalEvaluator) {
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

    @ParameterizedTest
    @MethodSource("dmnConditionEvaluators")
    void manageBooleanOrNullIfResultWithNull(DMNConditionalEvaluator dmnConditionalEvaluator)  {
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

    @Test
    void testMapEvaluatorIdentifiers() {
        Map<DMNConditionalEvaluator.EvaluatorType, DMNConditionalEvaluator.EvaluatorIdentifier> mapEvaluatorIdentifiers = DMNConditionalEvaluator.mapEvaluatorIdentifiers(EVALUATOR_ID_MAP);

        assertThat(mapEvaluatorIdentifiers).containsEntry(DMNConditionalEvaluator.EvaluatorType.IF, ifIdentifier)
            .containsEntry(DMNConditionalEvaluator.EvaluatorType.THEN, thenIdentifier)
            .containsEntry(DMNConditionalEvaluator.EvaluatorType.ELSE, elseIdentifier);
    }

    @Test
    void testGetEvaluatorIdentifier() {
        Map<DMNConditionalEvaluator.EvaluatorType, DMNConditionalEvaluator.EvaluatorIdentifier> evaluatorIdentifierMap = Map.of(
                DMNConditionalEvaluator.EvaluatorType.IF, ifIdentifier,
                DMNConditionalEvaluator.EvaluatorType.THEN, thenIdentifier,
                DMNConditionalEvaluator.EvaluatorType.ELSE, elseIdentifier);
        for (DMNConditionalEvaluator.EvaluatorType type : DMNConditionalEvaluator.EvaluatorType.values()) {
            assertThat(DMNConditionalEvaluator.getEvaluatorIdentifier(evaluatorIdentifierMap, type)).isEqualTo(evaluatorIdentifierMap.get(type));
        }
    }

    @Test
    void testMissingEvaluatorIdentifier() {
        Map<DMNConditionalEvaluator.EvaluatorType, DMNConditionalEvaluator.EvaluatorIdentifier> evaluatorIdentifierMap = new HashMap<>();
        String errorMessage = "Missing THEN evaluator in evaluatorIdMap";
        assertThatRuntimeException().isThrownBy(() -> DMNConditionalEvaluator.getEvaluatorIdentifier(evaluatorIdentifierMap, DMNConditionalEvaluator.EvaluatorType.THEN)).withMessage(errorMessage);
    }

    private static DMNConditionalEvaluator[] dmnConditionEvaluators() {
        return new DMNConditionalEvaluator[]
                { dmnConditionalEvaluatorDecisionNode, dmnConditionalEvaluatorBkmNode };
    }
}