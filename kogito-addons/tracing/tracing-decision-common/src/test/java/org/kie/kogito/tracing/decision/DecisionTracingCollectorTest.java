/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.tracing.decision;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import io.cloudevents.json.Json;
import io.cloudevents.v1.CloudEventImpl;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.util.Pair;
import org.kie.kogito.tracing.decision.event.AfterEvaluateAllEvent;
import org.kie.kogito.tracing.decision.event.EvaluateEvent;
import org.kie.kogito.tracing.decision.mock.MockDefaultAggregator;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kie.kogito.tracing.decision.mock.MockUtils.afterEvaluateAllEvent;
import static org.kie.kogito.tracing.decision.mock.MockUtils.beforeEvaluateAllEvent;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class DecisionTracingCollectorTest {

    private static final String TEST_EXECUTION_ID_1 = "c91da8ec-05f7-4dbd-adf4-c7aa88f7888b";
    private static final String TEST_EXECUTION_ID_2 = "550e2947-0952-4225-81a0-ea6e1064efd2";

    @Test
    public void test_Collector_InterleavedEvaluations_Working() {
        MockDefaultAggregator aggregator = new MockDefaultAggregator();
        Consumer<String> payloadConsumer = mock(Consumer.class);

        DecisionTracingCollector collector = new DecisionTracingCollector(aggregator, payloadConsumer);

        collector.addEvent(beforeEvaluateAllEvent(TEST_EXECUTION_ID_1));
        collector.addEvent(beforeEvaluateAllEvent(TEST_EXECUTION_ID_2));
        collector.addEvent(afterEvaluateAllEvent(TEST_EXECUTION_ID_1));
        collector.addEvent(afterEvaluateAllEvent(TEST_EXECUTION_ID_2));

        Map<String, Pair<List<EvaluateEvent>, CloudEventImpl<AfterEvaluateAllEvent>>> aggregatorCalls = aggregator.getCalls();
        assertEquals(2, aggregatorCalls.size());
        assertTrue(aggregatorCalls.containsKey(TEST_EXECUTION_ID_1));
        assertEquals(2, aggregatorCalls.get(TEST_EXECUTION_ID_1).getLeft().size());
        assertTrue(aggregatorCalls.containsKey(TEST_EXECUTION_ID_2));
        assertEquals(2, aggregatorCalls.get(TEST_EXECUTION_ID_2).getLeft().size());

        ArgumentCaptor<String> payloadCaptor = ArgumentCaptor.forClass(String.class);
        verify(payloadConsumer, times(2)).accept(payloadCaptor.capture());

        List<String> payloads = payloadCaptor.getAllValues();
        assertEquals(Json.encode(aggregatorCalls.get(TEST_EXECUTION_ID_1).getRight()), payloads.get(0));
        assertEquals(Json.encode(aggregatorCalls.get(TEST_EXECUTION_ID_2).getRight()), payloads.get(1));
    }

}
