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

package org.kie.kogito.trusty.service.common.api;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.kie.kogito.trusty.service.common.TrustyService;
import org.kie.kogito.trusty.service.common.responses.SalienciesResponse;
import org.kie.kogito.trusty.storage.api.model.ExplainabilityResult;
import org.kie.kogito.trusty.storage.api.model.ExplainabilityStatus;
import org.kie.kogito.trusty.storage.api.model.FeatureImportanceModel;
import org.kie.kogito.trusty.storage.api.model.SaliencyModel;
import org.testcontainers.shaded.org.apache.commons.lang.builder.CompareToBuilder;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.filter.log.ResponseLoggingFilter;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@QuarkusTest
class ExplainabilityApiV1IT {

    private static final String TEST_EXECUTION_ID = "executionId";

    @InjectMock
    TrustyService executionService;

    private static ExplainabilityResult buildValidExplainabilityResult() {
        return new ExplainabilityResult(
                TEST_EXECUTION_ID,
                ExplainabilityStatus.SUCCEEDED,
                null,
                List.of(
                        new SaliencyModel("O1", "Output1", List.of(
                                new FeatureImportanceModel("Feature1", 0.49384),
                                new FeatureImportanceModel("Feature2", -0.1084))),
                        new SaliencyModel("O2", "Output2", List.of(
                                new FeatureImportanceModel("Feature1", 0.0),
                                new FeatureImportanceModel("Feature2", 0.70293)))));
    }

    @Test
    void testSalienciesWithExplainabilityResult() {
        mockServiceWithExplainabilityResult();

        SalienciesResponse response = given().filter(new ResponseLoggingFilter())
                .when().get("/executions/decisions/" + TEST_EXECUTION_ID + "/explanations/saliencies")
                .as(SalienciesResponse.class);

        assertNotNull(response);
        assertNotNull(response.getSaliencies());
        assertSame(2, response.getSaliencies().size());

        List<SaliencyModel> sortedSaliencies = response.getSaliencies().stream()
                .sorted((s1, s2) -> new CompareToBuilder().append(s1.getOutcomeName(), s2.getOutcomeName()).toComparison())
                .collect(Collectors.toList());

        assertNotNull(sortedSaliencies.get(0));
        assertEquals("Output1", sortedSaliencies.get(0).getOutcomeName());
        assertNotNull(sortedSaliencies.get(0).getFeatureImportance());
        assertSame(2, sortedSaliencies.get(0).getFeatureImportance().size());
        assertEquals("Feature1", sortedSaliencies.get(0).getFeatureImportance().get(0).getFeatureName());
        assertEquals(0.49384, sortedSaliencies.get(0).getFeatureImportance().get(0).getScore());
        assertEquals("Feature2", sortedSaliencies.get(0).getFeatureImportance().get(1).getFeatureName());
        assertEquals(-0.1084, sortedSaliencies.get(0).getFeatureImportance().get(1).getScore());

        assertNotNull(sortedSaliencies.get(1));
        assertEquals("Output2", sortedSaliencies.get(1).getOutcomeName());
        assertNotNull(sortedSaliencies.get(1).getFeatureImportance());
        assertSame(2, sortedSaliencies.get(1).getFeatureImportance().size());
        assertEquals("Feature1", sortedSaliencies.get(1).getFeatureImportance().get(0).getFeatureName());
        assertEquals(0.0, sortedSaliencies.get(1).getFeatureImportance().get(0).getScore());
        assertEquals("Feature2", sortedSaliencies.get(1).getFeatureImportance().get(1).getFeatureName());
        assertEquals(0.70293, sortedSaliencies.get(1).getFeatureImportance().get(1).getScore());
    }

    @Test
    void testSalienciesWithNullExplainabilityResult() {
        mockServiceWithNullExplainabilityResult();

        given().filter(new ResponseLoggingFilter())
                .when().get("/executions/decisions/" + TEST_EXECUTION_ID + "/explanations/saliencies")
                .then().statusCode(400);
    }

    @Test
    void testSalienciesWithoutExplainabilityResult() {
        mockServiceWithoutExplainabilityResult();

        given().filter(new ResponseLoggingFilter())
                .when().get("/executions/decisions/" + TEST_EXECUTION_ID + "/explanations/saliencies")
                .then().statusCode(400);
    }

    private void mockServiceWithExplainabilityResult() {
        when(executionService.getExplainabilityResultById(eq(TEST_EXECUTION_ID)))
                .thenReturn(buildValidExplainabilityResult());
    }

    private void mockServiceWithNullExplainabilityResult() {
        when(executionService.getExplainabilityResultById(anyString()))
                .thenReturn(null);
    }

    private void mockServiceWithoutExplainabilityResult() {
        when(executionService.getExplainabilityResultById(anyString()))
                .thenThrow(new IllegalArgumentException("Explainability result does not exist."));
    }
}
