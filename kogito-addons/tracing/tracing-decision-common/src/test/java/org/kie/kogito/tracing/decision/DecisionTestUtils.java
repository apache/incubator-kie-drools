/*
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.kie.kogito.tracing.decision;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.kogito.dmn.DMNKogito;
import org.kie.kogito.tracing.decision.event.evaluate.EvaluateEvent;

public class DecisionTestUtils {

    public static final ObjectMapper MAPPER = new ObjectMapper();

    public static final String MODEL_RESOURCE = "/Traffic Violation.dmn";
    public static final String MODEL_NAMESPACE = "https://github.com/kiegroup/drools/kie-dmn/_A4BCA8B8-CF08-433F-93B2-A2598F19ECFF";
    public static final String MODEL_NAME = "Traffic Violation";

    public static final String FIRST_DECISION_NODE_ID = "_4055D956-1C47-479C-B3F4-BAEB61F1C929";
    public static final String LAST_DECISION_NODE_ID = "_8A408366-D8E9-4626-ABF3-5F69AA01F880";

    public static final String DECISION_SERVICE_NODE_ID = "_073E3815-F30F-4835-A5CF-A9B354444E09";
    public static final String DECISION_SERVICE_NODE_NAME = "FineService";
    public static final String DECISION_SERVICE_DECISION_ID = "_4055D956-1C47-479C-B3F4-BAEB61F1C929";

    public static final String EVALUATE_ALL_EXECUTION_ID = "4ac4c69f-4925-4221-b67e-4b14ce47bef8";
    public static final String EVALUATE_ALL_JSON_RESOURCE = "/Traffic Violation_EvaluateEvents_evaluateAll.json";
    public static final String EVALUATE_DECISION_SERVICE_EXECUTION_ID = "77408667-f218-40b0-a355-1bab047a3e9e";
    public static final String EVALUATE_DECISION_SERVICE_JSON_RESOURCE = "/Traffic Violation_EvaluateEvents_evaluateDecisionService.json";

    private static final TypeReference<List<EvaluateEvent>> EVALUATE_EVENT_LIST_TYPE = new TypeReference<List<EvaluateEvent>>() {
    };

    public static DMNRuntime createDMNRuntime() {
        return DMNKogito.createGenericDMNRuntime(new java.io.InputStreamReader(
                DecisionTestUtils.class.getResourceAsStream(MODEL_RESOURCE)
        ));
    }

    public static DMNModel createDMNModel() {
        return createDMNRuntime().getModel(MODEL_NAMESPACE, MODEL_NAME);
    }

    public static Map<String, Object> getEvaluateAllContext() {
        return new HashMap<String, Object>() {{
            put("Driver", getDriver(25, 10));
            put("Violation", getViolation("speed", 115, 100));
        }};
    }

    public static Map<String, Object> getEvaluateAllContextForWarning() {
        return new HashMap<String, Object>() {{
            put("Driver", getDriver(25, 10));
            put("Violation", getViolation("speed", 95, 100));
        }};
    }

    public static Map<String, Object> getEvaluateAllContextForError() {
        return new HashMap<String, Object>() {{
            put("Violation", getViolation("speed", 115, 100));
        }};
    }

    public static Map<String, Object> getEvaluateDecisionServiceContext() {
        return new HashMap<String, Object>() {{
            put("Violation", getViolation("speed", 115, 100));
        }};
    }

    public static Map<String, Object> getEvaluateDecisionServiceContextForWarning() {
        return new HashMap<String, Object>() {{
            put("Violation", getViolation("speed", 95, 100));
        }};
    }

    public static Map<String, Object> getDriver(int age, int points) {
        return new HashMap<String, Object>() {{
            put("Age", age);
            put("Points", points);
        }};
    }

    public static Map<String, Object> getViolation(String type, int actualSpeed, int speedLimit) {
        return new HashMap<String, Object>() {{
            put("Type", type);
            put("Actual Speed", actualSpeed);
            put("Speed Limit", speedLimit);
        }};
    }

    public static List<EvaluateEvent> readEvaluateEventsFromJsonResource(String resourceName) throws IOException {
        return MAPPER.readValue(DecisionTestUtils.class.getResourceAsStream(resourceName),
                EVALUATE_EVENT_LIST_TYPE
        );
    }
}
