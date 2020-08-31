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

package org.kie.kogito.explainability;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.kie.kogito.conf.ConfigBean;
import org.kie.kogito.conf.StaticConfigBean;
import org.kie.kogito.explainability.model.PredictOutput;

import javax.inject.Singleton;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kie.kogito.explainability.Constants.MODEL_NAME;
import static org.kie.kogito.explainability.Constants.MODEL_NAMESPACE;

@QuarkusTest
public class QuarkusExplainableResourceIT {

    @Singleton
    public static ConfigBean configBeanProducer() {
        return new StaticConfigBean("http://localhost:8081");
    }

    @Test
    void explainServiceTest() {
        String resourceId = String.format("%s:%s", MODEL_NAMESPACE, MODEL_NAME);
        String body = String.format(
                "[{\"request\" : {\"Driver\": {\"Age\": 25, \"Points\": 100}, \"Violation\": {\"Type\" : \"speed\", \"Actual Speed\": 120, \"Speed Limit\": 40}}," +
                "\"modelIdentifier\": {\"resourceType\": \"dmn\",\"resourceId\": \"%s\"}}]",
                resourceId);

        List<PredictOutput> outputs = given()
                .contentType(ContentType.JSON)
                .when()
                .body(body)
                .post("/predict")
                .as(new TypeRef<List<PredictOutput>>() { });

        assertEquals(1, outputs.size());

        PredictOutput output = outputs.get(0);

        assertNotNull(output);
        assertNotNull(output.getResult());
        assertNotNull(output.getModelIdentifier());
        Map<String, Object> result = output.getResult();

        assertTrue(result.containsKey("Should the driver be suspended?"));
        assertEquals("Yes", result.get("Should the driver be suspended?"));
        assertTrue(result.containsKey("Fine"));
        Map<String, Object> expectedFine = new HashMap<>();
        expectedFine.put("Points", 7);
        expectedFine.put("Amount", 1000);
        assertEquals(expectedFine, result.get("Fine"));
    }

    @Test
    void explainServiceTestMultipleInputs() {
        String resourceId = String.format("%s:%s", MODEL_NAMESPACE, MODEL_NAME);
        String body = String.format(
                "[{\"request\" : {\"Driver\": {\"Age\": 25, \"Points\": 100}, \"Violation\": {\"Type\" : \"speed\", \"Actual Speed\": 120, \"Speed Limit\": 40}}," +
                        "\"modelIdentifier\": {\"resourceType\": \"dmn\",\"resourceId\": \"%s\"}}, " +
                        "{\"request\" : {\"Driver\": {\"Age\": 25, \"Points\": 100}, \"Violation\": {\"Type\" : \"speed\", \"Actual Speed\": 120, \"Speed Limit\": 120}}," +
                "\"modelIdentifier\": {\"resourceType\": \"dmn\",\"resourceId\": \"%s\"}}]",
                resourceId, resourceId);

        List<PredictOutput> outputs = given()
                .contentType(ContentType.JSON)
                .when()
                .body(body)
                .post("/predict")
                .as(new TypeRef<List<PredictOutput>>() { });


        assertEquals(2, outputs.size());

        PredictOutput output = outputs.get(1);

        assertNotNull(output);
        assertNotNull(output.getResult());
        assertNotNull(output.getModelIdentifier());
        Map<String, Object> result = output.getResult();

        assertTrue(result.containsKey("Should the driver be suspended?"));
        assertEquals("No", result.get("Should the driver be suspended?"));
        assertNull(result.get("Fine"));
    }

    @Test
    void explainServiceTestNoInputs() {
        String body = "[]";

        List<PredictOutput> outputs = given()
                .contentType(ContentType.JSON)
                .when()
                .body(body)
                .post("/predict")
                .as(new TypeRef<List<PredictOutput>>() { });

        assertEquals(0, outputs.size());
    }

    @Test
    void explainServiceFail() {
        String unknownResourceId = "unknown:model";
        String body = String.format(
                "[{\"request\" : {\"Driver\": {\"Age\": 25, \"Points\": 100}, \"Violation\": {\"Type\" : \"speed\", \"Actual Speed\": 120, \"Speed Limit\": 40}}," +
                        "\"modelIdentifier\": {\"resourceType\": \"dmn\",\"resourceId\": \"%s\"}}, " +
                        "{\"request\" : {\"Driver\": {\"Age\": 25, \"Points\": 100}, \"Violation\": {\"Type\" : \"speed\", \"Actual Speed\": 120, \"Speed Limit\": 120}}," +
                        "\"modelIdentifier\": {\"resourceType\": \"dmn\",\"resourceId\": \"%s\"}}]",
                unknownResourceId, unknownResourceId);

        given()
                .contentType(ContentType.JSON)
                .when()
                .body(body)
                .post("/predict")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .body(Matchers.isA(String.class))
                .body(Matchers.containsString("Model " + unknownResourceId + " not found."));
    }
}
