/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import org.kie.kogito.tracing.decision.event.evaluate.EvaluateEvent;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DecisionTracingTestUtils {

    public static final ObjectMapper MAPPER = new ObjectMapper();

    public static final String EVALUATE_ALL_JSON_RESOURCE = "/Traffic Violation_EvaluateEvents_evaluateAll.json";
    public static final String EVALUATE_DECISION_SERVICE_JSON_RESOURCE = "/Traffic Violation_EvaluateEvents_evaluateDecisionService.json";

    private static final TypeReference<List<EvaluateEvent>> EVALUATE_EVENT_LIST_TYPE = new TypeReference<List<EvaluateEvent>>() {
    };

    public static List<EvaluateEvent> readEvaluateEventsFromJsonResource(String resourceName) throws IOException {
        return MAPPER.readValue(DecisionTracingTestUtils.class.getResourceAsStream(resourceName),
                EVALUATE_EVENT_LIST_TYPE);
    }
}
