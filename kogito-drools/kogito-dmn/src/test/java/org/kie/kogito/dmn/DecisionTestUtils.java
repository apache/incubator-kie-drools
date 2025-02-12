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
package org.kie.kogito.dmn;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DecisionTestUtils {

    public static final ObjectMapper MAPPER = new ObjectMapper();

    public static final String MODEL_RESOURCE = "/TrafficViolation.dmn";
    public static final String MODEL_NAMESPACE = "https://github.com/kiegroup/drools/kie-dmn/_A4BCA8B8-CF08-433F-93B2-A2598F19ECFF";
    public static final String MODEL_NAME = "Traffic Violation";

    public static final String FIRST_DECISION_NODE_ID = "_4055D956-1C47-479C-B3F4-BAEB61F1C929";
    public static final String LAST_DECISION_NODE_ID = "_8A408366-D8E9-4626-ABF3-5F69AA01F880";

    public static final String DECISION_SERVICE_NODE_ID = "_073E3815-F30F-4835-A5CF-A9B354444E09";
    public static final String DECISION_SERVICE_NODE_NAME = "FineService";
    public static final String DECISION_SERVICE_DECISION_ID = "_4055D956-1C47-479C-B3F4-BAEB61F1C929";

    public static final String EVALUATE_ALL_EXECUTION_ID = "4ac4c69f-4925-4221-b67e-4b14ce47bef8";
    public static final String EVALUATE_DECISION_SERVICE_EXECUTION_ID = "77408667-f218-40b0-a355-1bab047a3e9e";

    private static final String DRIVER_KEY = "Driver";
    private static final String DRIVER_AGE_KEY = "Age";
    private static final int DRIVER_AGE_VALUE_25 = 25;
    private static final String DRIVER_POINTS_KEY = "Points";
    private static final int DRIVER_POINTS_VALUE_10 = 10;

    private static final String VIOLATION_KEY = "Violation";
    private static final String VIOLATION_TYPE_KEY = "Type";
    private static final String VIOLATION_TYPE_VALUE_SPEED = "speed";
    private static final String VIOLATION_ACTUAL_SPEED_KEY = "Actual Speed";
    private static final String VIOLATION_SPEED_LIMIT_KEY = "Speed Limit";
    private static final int VIOLATION_SPEED_LIMIT_VALUE_100 = 100;

    public static DMNRuntime createDMNRuntime() {
        return DMNKogito.createGenericDMNRuntime(Collections.emptySet(), new java.io.InputStreamReader(
                DecisionTestUtils.class.getResourceAsStream(MODEL_RESOURCE)));
    }

    public static DMNModel createDMNModel() {
        return createDMNRuntime().getModel(MODEL_NAMESPACE, MODEL_NAME);
    }

    public static Map<String, Object> getEvaluateAllContext() {
        HashMap<String, Object> map = new HashMap<>();
        map.put(DRIVER_KEY, getDriver(DRIVER_AGE_VALUE_25, DRIVER_POINTS_VALUE_10));
        map.put(VIOLATION_KEY, getViolation(VIOLATION_TYPE_VALUE_SPEED, 115, VIOLATION_SPEED_LIMIT_VALUE_100));
        return map;
    }

    public static Map<String, Object> getEvaluateAllContextForWarning() {
        HashMap<String, Object> map = new HashMap<>();
        map.put(DRIVER_KEY, getDriver(DRIVER_AGE_VALUE_25, DRIVER_POINTS_VALUE_10));
        map.put(VIOLATION_KEY, getViolation(VIOLATION_TYPE_VALUE_SPEED, 95, VIOLATION_SPEED_LIMIT_VALUE_100));
        return map;
    }

    public static Map<String, Object> getEvaluateAllContextForError() {
        HashMap<String, Object> map = new HashMap<>();
        map.put(VIOLATION_KEY, getViolation(VIOLATION_TYPE_VALUE_SPEED, 115, VIOLATION_SPEED_LIMIT_VALUE_100));
        return map;
    }

    public static Map<String, Object> getEvaluateDecisionServiceContext() {
        HashMap<String, Object> map = new HashMap<>();
        map.put(VIOLATION_KEY, getViolation(VIOLATION_TYPE_VALUE_SPEED, 125, VIOLATION_SPEED_LIMIT_VALUE_100));
        return map;
    }

    public static Map<String, Object> getEvaluateDecisionServiceContextForWarning() {
        HashMap<String, Object> map = new HashMap<>();
        map.put(VIOLATION_KEY, getViolation(VIOLATION_TYPE_VALUE_SPEED, 95, VIOLATION_SPEED_LIMIT_VALUE_100));
        return map;
    }

    public static Map<String, Object> getDriver(int age, int points) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(DRIVER_AGE_KEY, age);
        map.put(DRIVER_POINTS_KEY, points);
        return map;
    }

    public static Map<String, Object> getViolation(String type, int actualSpeed, int speedLimit) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(VIOLATION_TYPE_KEY, type);
        map.put(VIOLATION_ACTUAL_SPEED_KEY, actualSpeed);
        map.put(VIOLATION_SPEED_LIMIT_KEY, speedLimit);
        return map;
    }

    private DecisionTestUtils() {
        throw new IllegalStateException("Utility class");
    }
}
