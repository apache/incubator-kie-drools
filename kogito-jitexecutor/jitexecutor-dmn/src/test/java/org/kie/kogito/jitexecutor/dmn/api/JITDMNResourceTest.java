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
package org.kie.kogito.jitexecutor.dmn.api;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.jitexecutor.dmn.requests.JITDMNPayload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.kie.kogito.jitexecutor.dmn.TestingUtils.getModelFromIoUtils;

@QuarkusTest
public class JITDMNResourceTest {

    private static String invalidModel;
    private static String modelWithExtensionElements;
    private static String modelWithMultipleEvaluationHitIds;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String EVALUATION_HIT_IDS_FIELD_NAME = "evaluationHitIds";

    static {
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @BeforeAll
    public static void setup() throws IOException {
        invalidModel = getModelFromIoUtils("invalid_models/DMNv1_x/test.dmn");
        modelWithExtensionElements = getModelFromIoUtils("valid_models/DMNv1_x/testWithExtensionElements.dmn");
        modelWithMultipleEvaluationHitIds = getModelFromIoUtils("valid_models/DMNv1_5/MultipleHitRules.dmn");
    }

    @Test
    void testjitEndpoint() {
        JITDMNPayload jitdmnpayload = new JITDMNPayload(invalidModel, buildContext());
        given()
                .contentType(ContentType.JSON)
                .body(jitdmnpayload)
                .when().post("/jitdmn")
                .then()
                .statusCode(200)
                .body(containsString("Loan Approval"), containsString("Approved"));
    }

    @Test
    void testjitdmnResultEndpoint() {
        JITDMNPayload jitdmnpayload = new JITDMNPayload(modelWithMultipleEvaluationHitIds, buildMultipleHitContext());
        given()
                .contentType(ContentType.JSON)
                .body(jitdmnpayload)
                .when().post("/jitdmn/dmnresult")
                .then()
                .statusCode(200)
                .body(containsString("Statistics"));
    }

    @Test
    void testjitdmnResultEndpointWithEvaluationHitIds() throws JsonProcessingException {
        JITDMNPayload jitdmnpayload = new JITDMNPayload(modelWithMultipleEvaluationHitIds, buildMultipleHitContext());
        final String rule0 = "_E5C380DA-AF7B-4401-9804-C58296EC09DD";
        final String rule1 = "_DFD65E8B-5648-4BFD-840F-8C76B8DDBD1A";
        final String rule2 = "_E80EE7F7-1C0C-4050-B560-F33611F16B05";
        String response = given().contentType(ContentType.JSON)
                .body(jitdmnpayload)
                .when().post("/jitdmn/dmnresult")
                .then()
                .statusCode(200)
                .body(containsString("Statistics"),
                        containsString(EVALUATION_HIT_IDS_FIELD_NAME),
                        containsString(rule0),
                        containsString(rule1),
                        containsString(rule2))
                .extract()
                .asString();
        JsonNode retrieved = MAPPER.readTree(response);
        ObjectNode evaluationHitIdsNode = (ObjectNode) retrieved.get(EVALUATION_HIT_IDS_FIELD_NAME);
        Assertions.assertThat(evaluationHitIdsNode).hasSize(3);
        final Map<String, Integer> expectedEvaluationHitIds = Map.of(rule0, 3, rule1, 2, rule2, 1);
        evaluationHitIdsNode.fields().forEachRemaining(entry -> Assertions.assertThat(expectedEvaluationHitIds).containsEntry(entry.getKey(), entry.getValue().asInt()));
    }

    @Test
    void testjitExplainabilityEndpoint() {
        JITDMNPayload jitdmnpayload = new JITDMNPayload(invalidModel, buildContext());
        given()
                .contentType(ContentType.JSON)
                .body(jitdmnpayload)
                .when().post("/jitdmn/evaluateAndExplain")
                .then()
                .statusCode(200)
                .body(containsString("dmnResult"), containsString("saliencies"), containsString("xls2dmn"),
                        containsString("featureName"));
    }

    @Test
    void testjitdmnWithExtensionElements() {
        Map<String, Object> context = new HashMap<>();
        context.put("m", 1);
        context.put("n", 2);

        JITDMNPayload jitdmnpayload = new JITDMNPayload(modelWithExtensionElements, context);
        given()
                .contentType(ContentType.JSON)
                .body(jitdmnpayload)
                .when().post("/jitdmn/dmnresult")
                .then()
                .statusCode(200)
                .body(containsString("m"), containsString("n"), containsString("sum"));
    }

    private Map<String, Object> buildContext() {
        Map<String, Object> context = new HashMap<>();
        context.put("FICO Score", 800);
        context.put("DTI Ratio", .1);
        context.put("PITI Ratio", .1);
        return context;
    }

    private Map<String, Object> buildMultipleHitContext() {
        final List<BigDecimal> numbers = new ArrayList<>();
        numbers.add(BigDecimal.valueOf(10));
        numbers.add(BigDecimal.valueOf(2));
        numbers.add(BigDecimal.valueOf(1));
        final Map<String, Object> context = new HashMap<>();
        context.put("Numbers", numbers);
        return context;
    }
}
