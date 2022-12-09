/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.tracing.decision;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.feel.util.Pair;
import org.kie.kogito.config.ConfigBean;
import org.kie.kogito.config.StaticConfigBean;
import org.kie.kogito.event.cloudevents.utils.CloudEventUtils;
import org.kie.kogito.tracing.decision.event.evaluate.EvaluateEvent;
import org.kie.kogito.tracing.decision.mock.MockDefaultAggregator;
import org.kie.kogito.tracing.decision.terminationdetector.BoundariesTerminationDetector;
import org.kie.kogito.tracing.decision.terminationdetector.CounterTerminationDetector;
import org.kie.kogito.tracing.decision.terminationdetector.TerminationDetector;
import org.mockito.ArgumentCaptor;

import io.cloudevents.CloudEvent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.dmn.DecisionTestUtils.EVALUATE_ALL_EXECUTION_ID;
import static org.kie.kogito.dmn.DecisionTestUtils.EVALUATE_DECISION_SERVICE_EXECUTION_ID;
import static org.kie.kogito.dmn.DecisionTestUtils.createDMNModel;
import static org.kie.kogito.tracing.decision.DecisionTracingTestUtils.EVALUATE_ALL_JSON_RESOURCE;
import static org.kie.kogito.tracing.decision.DecisionTracingTestUtils.EVALUATE_DECISION_SERVICE_JSON_RESOURCE;
import static org.kie.kogito.tracing.decision.DecisionTracingTestUtils.readEvaluateEventsFromJsonResource;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class DecisionTracingCollectorTest {

    private static DMNModel model;
    private static ConfigBean configBean;

    @BeforeAll
    static void initModel() {
        model = createDMNModel();
        configBean = new StaticConfigBean();
    }

    @Test
    void testCollectorWithInterleavedEvaluationsAndBoundariesDetectorIsWorking() throws IOException {
        testInterleavedEvaluations(BoundariesTerminationDetector::new);
    }

    @Test
    void testCollectorWithInterleavedEvaluationsAndCounterDetectorIsWorking() throws IOException {
        testInterleavedEvaluations(CounterTerminationDetector::new);
    }

    private void testInterleavedEvaluations(Supplier<TerminationDetector> terminationDetectorSupplier) throws IOException {
        MockDefaultAggregator aggregator = new MockDefaultAggregator();
        Consumer<String> payloadConsumer = mock(Consumer.class);

        DecisionTracingCollector collector = new DecisionTracingCollector(
                aggregator,
                payloadConsumer,
                (namespace, name) -> model,
                terminationDetectorSupplier,
                configBean);

        List<EvaluateEvent> evaluateAllEvents = readEvaluateEventsFromJsonResource(EVALUATE_ALL_JSON_RESOURCE);
        List<EvaluateEvent> evaluateDecisionServiceEvents = readEvaluateEventsFromJsonResource(EVALUATE_DECISION_SERVICE_JSON_RESOURCE);

        for (int i = 0; i < Math.max(evaluateAllEvents.size(), evaluateDecisionServiceEvents.size()); i++) {
            if (i < evaluateAllEvents.size()) {
                collector.addEvent(evaluateAllEvents.get(i));
            }
            if (i < evaluateDecisionServiceEvents.size()) {
                collector.addEvent(evaluateDecisionServiceEvents.get(i));
            }
        }

        Map<String, Pair<List<EvaluateEvent>, CloudEvent>> aggregatorCalls = aggregator.getCalls();
        assertThat(aggregatorCalls).hasSize(2)
                .containsKey(EVALUATE_ALL_EXECUTION_ID);
        assertThat(aggregatorCalls.get(EVALUATE_ALL_EXECUTION_ID).getLeft()).hasSameSizeAs(evaluateAllEvents);
        assertThat(aggregatorCalls).containsKey(EVALUATE_DECISION_SERVICE_EXECUTION_ID);
        assertThat(aggregatorCalls.get(EVALUATE_DECISION_SERVICE_EXECUTION_ID).getLeft()).hasSameSizeAs(evaluateDecisionServiceEvents);

        ArgumentCaptor<String> payloadCaptor = ArgumentCaptor.forClass(String.class);
        verify(payloadConsumer, times(2)).accept(payloadCaptor.capture());

        int evaluateAllIndex = evaluateAllEvents.size() > evaluateDecisionServiceEvents.size() ? 1 : 0;
        int evaluateDecisionServiceIndex = evaluateAllIndex == 1 ? 0 : 1;

        List<String> payloads = payloadCaptor.getAllValues();

        String expectedEvaluateAll = encodeFromCall(aggregatorCalls, EVALUATE_ALL_EXECUTION_ID);
        assertThat(payloads.get(evaluateAllIndex)).isEqualTo(expectedEvaluateAll);

        String expectedEvaluateDecisionService = encodeFromCall(aggregatorCalls, EVALUATE_DECISION_SERVICE_EXECUTION_ID);
        assertThat(payloads.get(evaluateDecisionServiceIndex)).isEqualTo(expectedEvaluateDecisionService);
    }

    private static String encodeFromCall(Map<String, Pair<List<EvaluateEvent>, CloudEvent>> aggregatorCalls, String key) {
        return Optional.ofNullable(aggregatorCalls.get(key))
                .map(Pair::getRight)
                .flatMap(CloudEventUtils::encode)
                .orElseThrow(IllegalStateException::new);
    }
}
