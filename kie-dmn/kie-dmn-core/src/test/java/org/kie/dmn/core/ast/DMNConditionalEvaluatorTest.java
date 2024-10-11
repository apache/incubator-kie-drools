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
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.EvaluatorResult;
import org.kie.dmn.api.core.ast.DMNNode;
import org.kie.dmn.api.core.event.AfterEvaluateConditionalEvent;
import org.kie.dmn.api.core.event.DMNRuntimeEventListener;
import org.kie.dmn.api.core.event.DMNRuntimeEventManager;
import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.core.impl.DMNResultImpl;
import org.kie.dmn.model.api.DMNElement;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DMNConditionalEvaluatorTest {

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
    void evaluateListenerInvocation() {
        EvaluatorResult ifEvaluationMock = mock(EvaluatorResult.class);
        when(ifEvaluationMock.getResultType()).thenReturn(EvaluatorResult.ResultType.FAILURE); // not interested in nested execution
        DMNExpressionEvaluator ifEvaluatorMock = mock(DMNExpressionEvaluator.class);
        DMNResultImpl dmnResultMock = mock(DMNResultImpl.class);
        when(ifEvaluatorMock.evaluate(eventManagerMock, dmnResultMock)).thenReturn(ifEvaluationMock);


        DMNConditionalEvaluator dmnConditionalEvaluator = new DMNConditionalEvaluator("name",
                                                                                      mock(DMNElement.class),
                                                                                      ifEvaluatorMock,
                                                                                      mock(DMNExpressionEvaluator.class),
                                                                                      mock(DMNExpressionEvaluator.class));
        dmnConditionalEvaluator.evaluate(eventManagerMock, dmnResultMock);
        ArgumentCaptor<AfterEvaluateConditionalEvent> evaluateConditionalEventArgumentCaptor = ArgumentCaptor.forClass(AfterEvaluateConditionalEvent.class);
        verify(spiedListener).afterEvaluateConditional (evaluateConditionalEventArgumentCaptor.capture());
        AfterEvaluateConditionalEvent evaluateConditionalEvent = evaluateConditionalEventArgumentCaptor.getValue();
        assertThat(evaluateConditionalEvent).isNotNull();
        assertThat(evaluateConditionalEvent.getEvaluatorResultResult()).isEqualTo(ifEvaluationMock);
    }
}