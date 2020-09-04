package org.kie.kogito.trusty.service.api;

import java.util.List;
import java.util.stream.Collectors;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.jupiter.api.Test;
import org.kie.kogito.trusty.service.TrustyService;
import org.kie.kogito.trusty.service.responses.SalienciesResponse;
import org.kie.kogito.trusty.service.responses.SaliencyResponse;
import org.kie.kogito.trusty.storage.api.model.ExplainabilityResult;
import org.kie.kogito.trusty.storage.api.model.ExplainabilityStatus;
import org.kie.kogito.trusty.storage.api.model.FeatureImportance;
import org.kie.kogito.trusty.storage.api.model.Saliency;
import org.testcontainers.shaded.org.apache.commons.lang.builder.CompareToBuilder;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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

    @Test
    void testSalienciesWithExplainabilityResult() {
        mockServiceWithExplainabilityResult();

        SalienciesResponse response = given().filter(new ResponseLoggingFilter())
                .when().get("/executions/decisions/" + TEST_EXECUTION_ID + "/explanations/saliencies")
                .as(SalienciesResponse.class);

        assertNotNull(response);
        assertNotNull(response.getSaliencies());
        assertSame(2, response.getSaliencies().size());

        List<SaliencyResponse> sortedSaliencies = response.getSaliencies().stream()
                .sorted((s1, s2) -> new CompareToBuilder().append(s1.getOutcomeName(), s2.getOutcomeName()).toComparison())
                .collect(Collectors.toList());

        assertNotNull(sortedSaliencies.get(0));
        assertEquals("Output1", sortedSaliencies.get(0).getOutcomeName());
        assertNotNull(sortedSaliencies.get(0).getFeatureImportance());
        assertSame(2, sortedSaliencies.get(0).getFeatureImportance().size());
        assertEquals("Feature1", sortedSaliencies.get(0).getFeatureImportance().get(0).getFeatureName());
        assertEquals(0.49384, sortedSaliencies.get(0).getFeatureImportance().get(0).getFeatureScore());
        assertEquals("Feature2", sortedSaliencies.get(0).getFeatureImportance().get(1).getFeatureName());
        assertEquals(-0.1084, sortedSaliencies.get(0).getFeatureImportance().get(1).getFeatureScore());

        assertNotNull(sortedSaliencies.get(1));
        assertEquals("Output2", sortedSaliencies.get(1).getOutcomeName());
        assertNotNull(sortedSaliencies.get(1).getFeatureImportance());
        assertSame(2, sortedSaliencies.get(1).getFeatureImportance().size());
        assertEquals("Feature1", sortedSaliencies.get(1).getFeatureImportance().get(0).getFeatureName());
        assertEquals(0.0, sortedSaliencies.get(1).getFeatureImportance().get(0).getFeatureScore());
        assertEquals("Feature2", sortedSaliencies.get(1).getFeatureImportance().get(1).getFeatureName());
        assertEquals(0.70293, sortedSaliencies.get(1).getFeatureImportance().get(1).getFeatureScore());
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

    @Test
    void testConverterMethodsNotThrowingWithNullModelValues() {
        assertDoesNotThrow(() -> ExplainabilityApiV1.explainabilityResultModelToResponse(null));
        assertDoesNotThrow(() -> ExplainabilityApiV1.featureImportanceModelToResponse(null));
        assertDoesNotThrow(() -> ExplainabilityApiV1.saliencyModelToResponse(null));
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

    private static ExplainabilityResult buildValidExplainabilityResult() {
        return new ExplainabilityResult(
                TEST_EXECUTION_ID,
                ExplainabilityStatus.SUCCEEDED,
                null,
                List.of(
                        new Saliency("O1", "Output1", List.of(
                                new FeatureImportance("Feature1", 0.49384),
                                new FeatureImportance("Feature2", -0.1084)
                        )),
                        new Saliency("O2", "Output2", List.of(
                                new FeatureImportance("Feature1", 0.0),
                                new FeatureImportance("Feature2", 0.70293)
                        ))
                )
        );
    }
}
