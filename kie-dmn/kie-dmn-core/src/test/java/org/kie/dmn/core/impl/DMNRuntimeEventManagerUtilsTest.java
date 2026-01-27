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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.api.io.Resource;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.EvaluatorResult;
import org.kie.dmn.api.core.event.AfterConditionalEvaluationEvent;
import org.kie.dmn.api.core.event.AfterEvaluateConditionalEvent;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionTableEvent;
import org.kie.dmn.api.core.event.DMNRuntimeEventListener;
import org.kie.dmn.api.core.event.DMNRuntimeEventManager;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.internal.utils.DMNRuntimeBuilder;
import org.kie.internal.io.ResourceFactory;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
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

    @BeforeEach
    void setup() {
        reset(spiedListener);
    }

    @Test
    void fireAfterEvaluateConditional() {
        EvaluatorResult evaluatorResult = mock(EvaluatorResult.class);
        String executedId = "EXECUTED_ID";
        DMNRuntimeEventManagerUtils.fireAfterEvaluateConditional(eventManagerMock, evaluatorResult, executedId);
        ArgumentCaptor<AfterEvaluateConditionalEvent> evaluateConditionalEventArgumentCaptor = ArgumentCaptor.forClass(AfterEvaluateConditionalEvent.class);
        verify(spiedListener).afterEvaluateConditional(evaluateConditionalEventArgumentCaptor.capture());
        AfterEvaluateConditionalEvent evaluateConditionalEvent = evaluateConditionalEventArgumentCaptor.getValue();
        assertThat(evaluateConditionalEvent).isNotNull();
        assertThat(evaluateConditionalEvent.getEvaluatorResultResult()).isEqualTo(evaluatorResult);
        assertThat(evaluateConditionalEvent.getExecutedId()).isEqualTo(executedId);
    }

    @Test
    void fireAfterConditionalEvaluation() {
        EvaluatorResult evaluatorResult = mock(EvaluatorResult.class);
        String conditionalName = "NAME";
        String decisionName = "DECISION";
        String executedId = "EXECUTED_ID";
        DMNRuntimeEventManagerUtils.fireAfterConditionalEvaluation(eventManagerMock, conditionalName, decisionName, evaluatorResult, executedId);
        ArgumentCaptor<AfterConditionalEvaluationEvent> conditionalEvaluationEventArgumentCaptor = ArgumentCaptor.forClass(AfterConditionalEvaluationEvent.class);
        verify(spiedListener).afterConditionalEvaluation(conditionalEvaluationEventArgumentCaptor.capture());
        AfterConditionalEvaluationEvent evaluateConditionalEvent = conditionalEvaluationEventArgumentCaptor.getValue();
        assertThat(evaluateConditionalEvent).isNotNull();
        assertThat(evaluateConditionalEvent.getNodeName()).isEqualTo(conditionalName);
        assertThat(evaluateConditionalEvent.getDecisionName()).isEqualTo(decisionName);
        assertThat(evaluateConditionalEvent.getEvaluatorResultResult()).isEqualTo(evaluatorResult);
        assertThat(evaluateConditionalEvent.getExecutedId()).isEqualTo(executedId);
    }

    @Test
    void testConditionalEvent() {
        String decisionName = "B";
        String executedId = "_F9D2FA33-4604-4AAA-8FF1-5A4AC5055385";
        Resource resource = ResourceFactory.newClassPathResource("valid_models/DMNv1_5/ConditionalEvent.dmn");
        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults()
                .buildConfiguration()
                .fromResources(Collections.singletonList(resource))
                .getOrElseThrow(RuntimeException::new);
        dmnRuntime.addListener(spiedListener);
        assertThat(dmnRuntime).isNotNull();
        String nameSpace = "https://kie.org/dmn/_5B448C78-0DBF-4554-92A4-8C0247EB01FD";

        final DMNModel dmnModel = dmnRuntime.getModel(nameSpace, "DMN_00DF4B93-0243-4813-BA70-A1894AC723BE");
        assertThat(dmnModel).isNotNull();
        DMNContext context = DMNFactory.newContext();
        context.set("A", List.of(3));
        DMNResult dmnResult = dmnRuntime.evaluateAll(dmnModel, context);
        assertThat(dmnResult.getDecisionResultByName(decisionName).getResult()).isEqualTo(List.of("pos"));

        ArgumentCaptor<AfterConditionalEvaluationEvent> conditionalEvaluationEventArgumentCaptor = ArgumentCaptor.forClass(AfterConditionalEvaluationEvent.class);
        verify(spiedListener).afterConditionalEvaluation(conditionalEvaluationEventArgumentCaptor.capture());
        AfterConditionalEvaluationEvent evaluateConditionalEvent = conditionalEvaluationEventArgumentCaptor.getValue();
        assertThat(evaluateConditionalEvent).isNotNull();
        assertThat(evaluateConditionalEvent.getDecisionName()).isEqualTo(decisionName);
        EvaluatorResult retrieved = evaluateConditionalEvent.getEvaluatorResultResult();
        assertThat(retrieved).isNotNull();
        assertThat(evaluateConditionalEvent.getExecutedId()).isEqualTo(executedId);
    }

    @Test
    void testEvaluateDecisionTableEvent() {
        String decisionName = "New Decision";
        String bkmName = "New BKM";
        String dtId = "_46B46F91-5810-452F-B1D4-A0B0304737B1";
        Resource resource = ResourceFactory.newClassPathResource("valid_models/DMNv1_6/decisionsInBKMWithNameInput.dmn");
        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults()
                .buildConfiguration()
                .fromResources(Collections.singletonList(resource))
                .getOrElseThrow(RuntimeException::new);
        dmnRuntime.addListener(spiedListener);
        assertThat(dmnRuntime).isNotNull();
        String nameSpace = "https://kie.org/dmn/_8010864B-CC05-4DB2-A6CB-B19968FD56BC";

        final DMNModel dmnModel = dmnRuntime.getModel(nameSpace, "DMN_DE9C9FE9-DF27-43B7-917C-96765C61467F");
        assertThat(dmnModel).isNotNull();
        DMNContext context = DMNFactory.newContext();
        context.set("name", "2");
        DMNResult dmnResult = dmnRuntime.evaluateAll(dmnModel, context);
        assertThat(dmnResult.getDecisionResultByName(decisionName)).isNotNull();
        assertThat(dmnResult.getDecisionResultByName(decisionName).getResult()).isEqualTo("bb");

        ArgumentCaptor<AfterEvaluateDecisionTableEvent> evaluateDecisionTableEventCaptor = ArgumentCaptor.forClass(AfterEvaluateDecisionTableEvent.class);
        verify(spiedListener, times(5)).afterEvaluateDecisionTable(evaluateDecisionTableEventCaptor.capture());
        
        AfterEvaluateDecisionTableEvent evaluateDecisionTableEvent = evaluateDecisionTableEventCaptor.getAllValues().stream()
                .filter(event -> decisionName.equals(event.getDecisionName()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No event found for decision: " + decisionName));

        assertThat(evaluateDecisionTableEvent).isNotNull();
        assertThat(evaluateDecisionTableEvent.getDecisionName()).isEqualTo(decisionName);
        assertThat(evaluateDecisionTableEvent.getNodeName()).isEqualTo(bkmName);
        assertThat(evaluateDecisionTableEvent.getDecisionTableName()).isEqualTo(bkmName);
        assertThat(evaluateDecisionTableEvent.getDecisionTableId()).isEqualTo(dtId);
        assertThat(evaluateDecisionTableEvent.getSelected()).isNotEmpty();
        assertThat(evaluateDecisionTableEvent.getSelectedIds()).contains("_4FCA6937-8E97-4513-8D43-460E6B7D5686");
    }

    @Test
    void verifyDependentDecisionEvaluationEvents() {
        String decisionName = "Loan Pre-Qualification";
        String nodeName = "Loan Pre-Qualification";
        String dtId = "_EF7F404A-939E-4889-95D8-E4053DD1EED9";
        Resource resource = ResourceFactory.newClassPathResource("valid_models/DMNv1_5/Sample.dmn");
        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults()
                .buildConfiguration()
                .fromResources(Collections.singletonList(resource))
                .getOrElseThrow(RuntimeException::new);
        dmnRuntime.addListener(spiedListener);
        assertThat(dmnRuntime).isNotNull();
        String nameSpace = "https://kie.apache.org/dmn/_857FE424-BEDA-4772-AB8E-2F4CDDB864AB";

        final DMNModel dmnModel = dmnRuntime.getModel(nameSpace, "loan_pre_qualification");
        assertThat(dmnModel).isNotNull();
        DMNContext context = DMNFactory.newContext();
        context.set("Credit Score", Map.of("FICO", 700));

        Map<String, Object> monthly = new HashMap<>();
        monthly.put("Income", 121233);
        monthly.put("Repayments", 33);
        monthly.put("Expenses", 123);
        monthly.put("Tax", 32);
        monthly.put("Insurance", 55);
        Map<String, Object> applicantData = new HashMap<>();
        applicantData.put("Age", 32);
        applicantData.put("Marital Status", "S");
        applicantData.put("Employment Status", "Employed");
        applicantData.put("Existing Customer", false);
        applicantData.put("Monthly", monthly);
        context.set("Applicant Data", applicantData);

        Map<String, Object> requestedProduct = new HashMap<>();
        requestedProduct.put("Type", "Special Loan");
        requestedProduct.put("Rate", 1);
        requestedProduct.put("Term", 2);
        requestedProduct.put("Amount", 333);
        context.set("Requested Product", requestedProduct);

        context.set("id", "_0A185BAC-7692-45FA-B722-7C86C626BD51");

        DMNResult dmnResult = dmnRuntime.evaluateAll(dmnModel, context);
        assertThat(dmnResult.getDecisionResultByName(decisionName)).isNotNull();

        ArgumentCaptor<AfterEvaluateDecisionTableEvent> evaluateDecisionTableEventCaptor = ArgumentCaptor.forClass(AfterEvaluateDecisionTableEvent.class);
        verify(spiedListener, times(2)).afterEvaluateDecisionTable(evaluateDecisionTableEventCaptor.capture());

        AfterEvaluateDecisionTableEvent evaluateDecisionTableEvent = evaluateDecisionTableEventCaptor.getAllValues().stream()
                .filter(event -> decisionName.equals(event.getDecisionName()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No event found for decision: " + decisionName));

        assertThat(evaluateDecisionTableEvent).isNotNull();
        assertThat(evaluateDecisionTableEvent.getDecisionName()).isEqualTo(decisionName);
        assertThat(evaluateDecisionTableEvent.getNodeName()).isEqualTo(nodeName);
        assertThat(evaluateDecisionTableEvent.getDecisionTableName()).isEqualTo(nodeName);
        assertThat(evaluateDecisionTableEvent.getDecisionTableId()).isEqualTo(dtId);
        assertThat(evaluateDecisionTableEvent.getSelected()).isNotEmpty();
        assertThat(evaluateDecisionTableEvent.getSelectedIds()).contains("_C8FA33B1-AF6E-4A59-B7B9-6FDF1F495C44");
    }

    @Test
    void testThreadLocalValue() throws Exception {
        DMNRuntimeEventManagerImpl eventManager = new DMNRuntimeEventManagerImpl();
        int elements = 6;
        Set<Thread> threads = new HashSet<>();
        Map<Integer, AtomicReference<String>> mappedThreadValues = new HashMap<>();
        CountDownLatch latch = new CountDownLatch(elements);

        IntStream.range(0, elements).forEach(i -> {
            AtomicReference<String> threadValue = new AtomicReference<>();
            Thread thread = new Thread(() -> {
                eventManager.setCurrentEvaluatingDecisionName("New Decision " + i);
                threadValue.set(eventManager.getCurrentEvaluatingDecisionName());
                eventManager.clearCurrentEvaluatingDecisionName();
                assertThat(eventManager.getCurrentEvaluatingDecisionName()).isNull();
                latch.countDown();
            });
            mappedThreadValues.put(i, threadValue);
            threads.add(thread);
        });

        threads.forEach(Thread::start);
        latch.await();

        mappedThreadValues.forEach((i, threadValue) -> assertThat(threadValue.get()).isEqualTo("New Decision " + i));
    }

    @Test
    void testEvaluateConditionalEvent() {
        String decisionName = "D1";
        Resource resource = ResourceFactory.newClassPathResource("valid_models/DMNv1_6/ConditionalIf.dmn");
        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults()
                .buildConfiguration()
                .fromResources(Collections.singletonList(resource))
                .getOrElseThrow(RuntimeException::new);
        dmnRuntime.addListener(spiedListener);
        assertThat(dmnRuntime).isNotNull();
        String nameSpace = "https://kie.org/dmn/_2693E3CA-A0F2-4861-9726-EBD251F2F549";

        final DMNModel dmnModel = dmnRuntime.getModel(nameSpace, "DMN_1D502349-17ED-4CF7-9B6C-C107AC85FC2D");
        assertThat(dmnModel).isNotNull();
        DMNContext context = DMNFactory.newContext();
        context.set("Input", 2);
        DMNResult dmnResult = dmnRuntime.evaluateAll(dmnModel, context);
        assertThat(dmnResult.getDecisionResultByName(decisionName)).isNotNull();
        assertThat(dmnResult.getDecisionResultByName(decisionName).getResult()).isEqualTo(BigDecimal.valueOf(6));

        ArgumentCaptor<AfterEvaluateConditionalEvent> evaluateConditionalEventCaptor = ArgumentCaptor.forClass(AfterEvaluateConditionalEvent.class);
        verify(spiedListener, times(3)).afterEvaluateConditional(evaluateConditionalEventCaptor.capture());

        AfterEvaluateConditionalEvent evaluateConditionalEvent = evaluateConditionalEventCaptor.getAllValues().get(0);

        assertThat(evaluateConditionalEvent).isNotNull();
        assertThat(evaluateConditionalEvent.getExecutedId()).isEqualTo("_63C5E174-D55D-4C52-95A1-8ED3FACF44FA");
        assertThat(evaluateConditionalEvent.getEvaluatorResultResult().getResult()).isEqualTo(true);

        ArgumentCaptor<AfterConditionalEvaluationEvent> conditionalEvaluationEventCaptor = ArgumentCaptor.forClass(AfterConditionalEvaluationEvent.class);
        verify(spiedListener, times(3)).afterConditionalEvaluation(conditionalEvaluationEventCaptor.capture());

        AfterConditionalEvaluationEvent conditionalEvaluationEvent = conditionalEvaluationEventCaptor.getAllValues().stream()
                .filter(event -> decisionName.equals(event.getDecisionName()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No conditional evaluation event found for decision: " + decisionName));

        assertThat(conditionalEvaluationEvent).isNotNull();
        assertThat(conditionalEvaluationEvent.getDecisionName()).isEqualTo(decisionName);
        assertThat(conditionalEvaluationEvent.getNodeName()).isEqualTo("Logic IF");
        assertThat(conditionalEvaluationEvent.getExecutedId()).isEqualTo("_1B74D6B2-C69A-4A50-A94A-DD55CC188EC4");
        assertThat(conditionalEvaluationEvent.getEvaluatorResultResult().getResult()).isEqualTo(BigDecimal.valueOf(2));
    }

}