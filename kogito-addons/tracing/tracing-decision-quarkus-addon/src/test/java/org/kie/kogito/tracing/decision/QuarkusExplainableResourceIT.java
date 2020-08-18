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

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kie.kogito.tracing.decision.event.explainability.PredictOutput;

import static io.restassured.RestAssured.given;
import static org.kie.kogito.tracing.decision.Constants.MODEL_NAME;
import static org.kie.kogito.tracing.decision.Constants.MODEL_NAMESPACE;

@QuarkusTest
public class QuarkusExplainableResourceIT {

    @Test
    @SuppressWarnings("unchecked")
    void testRequestIsProcessedAndCorrect() throws JsonProcessingException {
        String resourceId = String.format("%s:%s", MODEL_NAMESPACE, MODEL_NAME);
        String body = String.format(
                "{\"request\" : {\"Driver\": {\"Age\": 25, \"Points\": 100}, \"Violation\": {\"Type\" : \"speed\", \"Actual Speed\": 120, \"Speed Limit\": 40}}," +
                "\"modelIdentifier\": {\"resourceType\": \"dmn\",\"resourceId\": \"%s\"}}",
                resourceId);

        PredictOutput output = given()
                .contentType(ContentType.JSON)
                .when()
                .body(body)
                .post("/predict")
                .as(PredictOutput.class);


        Assertions.assertNotNull(output);
        Assertions.assertNotNull(output.getResult());
        Assertions.assertNotNull(output.getModelIdentifier());
        Map<String, Object> result = output.getResult();

        Assertions.assertTrue(result.containsKey("Should the driver be suspended?"));
        Assertions.assertEquals("Yes", result.get("Should the driver be suspended?"));
        Assertions.assertTrue(result.containsKey("Fine"));
        Map<String, Object> expectedFine = new HashMap<>();
        expectedFine.put("Points", 7);
        expectedFine.put("Amount", 1000);
        Assertions.assertEquals(expectedFine, result.get("Fine"));
    }
}
