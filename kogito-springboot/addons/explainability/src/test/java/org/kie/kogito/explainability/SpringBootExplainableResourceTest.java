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
package org.kie.kogito.explainability;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.model.ModelIdentifier;
import org.kie.kogito.explainability.model.PredictInput;
import org.kie.kogito.explainability.model.PredictOutput;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kie.kogito.explainability.Constants.MODEL_NAME;
import static org.kie.kogito.explainability.Constants.MODEL_NAMESPACE;

class SpringBootExplainableResourceTest {

    SpringBootExplainableResource resource = new SpringBootExplainableResource(new ApplicationMock());

    @Test
    @SuppressWarnings("unchecked")
    void explainServiceTest() {
        List<PredictInput> inputs = singletonList(createInput(40));

        List<PredictOutput> outputs = (List<PredictOutput>) resource.predict(inputs).getBody();

        assertNotNull(outputs);
        assertEquals(1, outputs.size());

        PredictOutput output = outputs.get(0);

        assertNotNull(output.getResult());
        assertNotNull(output.getModelIdentifier());
        Map<String, Object> result = output.getResult();

        assertTrue(result.containsKey("Should the driver be suspended?"));
        assertEquals("Yes", result.get("Should the driver be suspended?"));
        assertTrue(result.containsKey("Fine"));
        Map<String, Object> expectedFine = new HashMap<>();
        expectedFine.put("Points", BigDecimal.valueOf(7));
        expectedFine.put("Amount", BigDecimal.valueOf(1000));
        assertEquals(expectedFine.get("Points"), ((Map<String, Object>) result.get("Fine")).get("Points"));
        assertEquals(expectedFine.get("Amount"), ((Map<String, Object>) result.get("Fine")).get("Amount"));
    }

    @Test
    void explainServiceTestMultipleInputs() {
        List<PredictInput> inputs = asList(createInput(40), createInput(120));

        List<PredictOutput> outputs = (List<PredictOutput>) resource.predict(inputs).getBody();

        assertNotNull(outputs);
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
        List<PredictOutput> outputs = (List<PredictOutput>) resource.predict(emptyList()).getBody();

        assertNotNull(outputs);
        assertEquals(0, outputs.size());
    }

    @Test
    void explainServiceFail() {
        String unknownwResourceId = "unknown:model";
        PredictInput input = createInput(10);
        input.getModelIdentifier().setResourceId(unknownwResourceId);
        ResponseEntity responseEntity = resource.predict(singletonList(input));

        assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getStatusCodeValue());
        assertEquals("An Exception occurred processing the predict request. Please see the logs for more details.", responseEntity.getBody());
    }

    private PredictInput createInput(int speedLimit) {
        String resourceId = String.format("%s:%s", MODEL_NAMESPACE, MODEL_NAME);

        Map<String, Object> driver = new HashMap<>();
        driver.put("Age", 25);
        driver.put("Points", 100);

        Map<String, Object> violation = new HashMap<>();
        violation.put("Type", "speed");
        violation.put("Actual Speed", 120);
        violation.put("Speed Limit", speedLimit);

        Map<String, Object> payload = new HashMap<>();
        payload.put("Driver", driver);
        payload.put("Violation", violation);

        ModelIdentifier modelIdentifier = new ModelIdentifier("dmn", resourceId);
        return new PredictInput(modelIdentifier, payload);
    }
}
