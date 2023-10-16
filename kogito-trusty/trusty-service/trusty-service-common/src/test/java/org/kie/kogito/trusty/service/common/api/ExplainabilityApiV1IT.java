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
package org.kie.kogito.trusty.service.common.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.api.BaseExplainabilityResult;
import org.kie.kogito.explainability.api.CounterfactualDomainCategorical;
import org.kie.kogito.explainability.api.CounterfactualDomainRange;
import org.kie.kogito.explainability.api.CounterfactualExplainabilityRequest;
import org.kie.kogito.explainability.api.CounterfactualExplainabilityResult;
import org.kie.kogito.explainability.api.CounterfactualSearchDomain;
import org.kie.kogito.explainability.api.CounterfactualSearchDomainValue;
import org.kie.kogito.explainability.api.ExplainabilityStatus;
import org.kie.kogito.explainability.api.FeatureImportanceModel;
import org.kie.kogito.explainability.api.LIMEExplainabilityResult;
import org.kie.kogito.explainability.api.ModelIdentifier;
import org.kie.kogito.explainability.api.NamedTypedValue;
import org.kie.kogito.explainability.api.SaliencyModel;
import org.kie.kogito.tracing.typedvalue.TypedValue;
import org.kie.kogito.tracing.typedvalue.UnitValue;
import org.kie.kogito.trusty.service.common.TrustyService;
import org.kie.kogito.trusty.service.common.responses.CounterfactualRequestResponse;
import org.kie.kogito.trusty.service.common.responses.CounterfactualResultsResponse;
import org.kie.kogito.trusty.service.common.responses.SalienciesResponse;
import org.kie.kogito.trusty.storage.api.model.decision.Decision;
import org.kie.kogito.trusty.storage.api.model.decision.DecisionOutcome;
import org.mockito.ArgumentCaptor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.IntNode;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kie.kogito.trusty.service.common.TrustyServiceTestUtils.getCounterfactualJsonRequest;
import static org.kie.kogito.trusty.service.common.TrustyServiceTestUtils.getCounterfactualWithStructuredModelJsonRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
class ExplainabilityApiV1IT {

    private static final String TEST_EXECUTION_ID = "executionId";

    private static final String TEST_SERVICE_URL = "serviceUrl";

    private static final String TEST_COUNTERFACTUAL_ID = "counterfactualId";

    private static final Long TEST_MAX_RUNNING_TIME_SECONDS = 60L;

    private static final CounterfactualExplainabilityResult SOLUTION1 = new CounterfactualExplainabilityResult(TEST_EXECUTION_ID,
            TEST_COUNTERFACTUAL_ID,
            "solution1",
            0L,
            ExplainabilityStatus.SUCCEEDED,
            "",
            true,
            CounterfactualExplainabilityResult.Stage.INTERMEDIATE,
            Collections.emptyList(),
            Collections.emptyList());
    private static final CounterfactualExplainabilityResult SOLUTION2 = new CounterfactualExplainabilityResult(TEST_EXECUTION_ID,
            TEST_COUNTERFACTUAL_ID,
            "solution2",
            1L,
            ExplainabilityStatus.SUCCEEDED,
            "",
            true,
            CounterfactualExplainabilityResult.Stage.FINAL,
            Collections.emptyList(),
            Collections.emptyList());

    @InjectMock
    TrustyService executionService;

    private static BaseExplainabilityResult buildValidExplainabilityResult() {
        return new LIMEExplainabilityResult(
                TEST_EXECUTION_ID,
                ExplainabilityStatus.SUCCEEDED,
                null,
                List.of(
                        new SaliencyModel("Output1",
                                List.of(
                                        new FeatureImportanceModel("Feature1", 0.49384),
                                        new FeatureImportanceModel("Feature2", -0.1084))),
                        new SaliencyModel("Output2",
                                List.of(
                                        new FeatureImportanceModel("Feature1", 0.0),
                                        new FeatureImportanceModel("Feature2", 0.70293)))));
    }

    private static CounterfactualExplainabilityRequest buildValidCounterfactual() {
        return new CounterfactualExplainabilityRequest(TEST_EXECUTION_ID,
                TEST_SERVICE_URL,
                new ModelIdentifier("resourceType", "resourceIdentifier"),
                TEST_COUNTERFACTUAL_ID,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                TEST_MAX_RUNNING_TIME_SECONDS);
    }

    private static List<CounterfactualExplainabilityResult> buildValidCounterfactualResults() {
        return List.of(SOLUTION1, SOLUTION2);
    }

    @Test
    void testSalienciesWithExplainabilityResult() {
        mockServiceWithExplainabilityResult();

        Decision decision = new Decision(TEST_EXECUTION_ID,
                "sourceUrl",
                "serviceUrl",
                0L,
                true,
                "executorName",
                "executorModelName",
                "executorModelNamespace",
                new ArrayList<>(),
                new ArrayList<>());
        decision.getOutcomes().add(new DecisionOutcome("outcomeId1",
                "Output1",
                ExplainabilityStatus.SUCCEEDED.name(),
                new UnitValue("type", new IntNode(1)),
                Collections.emptyList(),
                Collections.emptyList()));
        decision.getOutcomes().add(new DecisionOutcome("outcomeId2",
                "Output2",
                ExplainabilityStatus.SUCCEEDED.name(),
                new UnitValue("type2", new IntNode(2)),
                Collections.emptyList(),
                Collections.emptyList()));
        when(executionService.getDecisionById(eq(TEST_EXECUTION_ID))).thenReturn(decision);

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
    @SuppressWarnings("unchecked")
    void testCounterfactualRequest() {
        ArgumentCaptor<List<NamedTypedValue>> goalsCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List<CounterfactualSearchDomain>> searchDomainsCaptor = ArgumentCaptor.forClass(List.class);

        mockServiceWithCounterfactualRequest();

        CounterfactualRequestResponse response = given()
                .filter(new RequestLoggingFilter())
                .filter(new ResponseLoggingFilter())
                .contentType(MediaType.APPLICATION_JSON)
                .body(getCounterfactualJsonRequest())
                .when()
                .post("/executions/decisions/" + TEST_EXECUTION_ID + "/explanations/counterfactuals")
                .as(CounterfactualRequestResponse.class);

        assertNotNull(response);
        assertNotNull(response.getExecutionId());
        assertNotNull(response.getCounterfactualId());
        assertEquals(response.getExecutionId(), TEST_EXECUTION_ID);
        assertEquals(response.getCounterfactualId(), TEST_COUNTERFACTUAL_ID);

        verify(executionService).requestCounterfactuals(eq(TEST_EXECUTION_ID), goalsCaptor.capture(), searchDomainsCaptor.capture());
        List<NamedTypedValue> goalsParameter = goalsCaptor.getValue();
        assertNotNull(goalsParameter);
        assertEquals(2, goalsParameter.size());

        NamedTypedValue goal1 = goalsParameter.get(0);
        assertEquals(TypedValue.Kind.UNIT, goal1.getValue().getKind());
        assertEquals("deposit", goal1.getName());
        assertEquals("number", goal1.getValue().getType());
        assertEquals(5000, goal1.getValue().toUnit().getValue().asInt());

        NamedTypedValue goal2 = goalsParameter.get(1);
        assertEquals(TypedValue.Kind.UNIT, goal2.getValue().getKind());
        assertEquals("approved", goal2.getName());
        assertEquals("boolean", goal2.getValue().getType());
        assertEquals(Boolean.TRUE, goal2.getValue().toUnit().getValue().asBoolean());

        List<CounterfactualSearchDomain> searchDomainsParameter = searchDomainsCaptor.getValue();
        assertNotNull(searchDomainsParameter);
        assertEquals(3, searchDomainsParameter.size());

        CounterfactualSearchDomain domain1 = searchDomainsParameter.get(0);
        assertEquals(TypedValue.Kind.UNIT, domain1.getValue().getKind());
        assertTrue(domain1.getValue().toUnit().isFixed());
        assertEquals("age", domain1.getName());
        assertEquals("number", domain1.getValue().getType());
        assertNull(domain1.getValue().toUnit().getDomain());

        CounterfactualSearchDomain domain2 = searchDomainsParameter.get(1);
        assertEquals(TypedValue.Kind.UNIT, domain2.getValue().getKind());
        assertFalse(domain2.getValue().toUnit().isFixed());
        assertEquals("income", domain2.getName());
        assertEquals("number", domain2.getValue().getType());
        assertNotNull(domain2.getValue().toUnit().getDomain());
        assertTrue(domain2.getValue().toUnit().getDomain() instanceof CounterfactualDomainRange);
        CounterfactualDomainRange domain2Def = (CounterfactualDomainRange) domain2.getValue().toUnit().getDomain();
        assertEquals(0, domain2Def.getLowerBound().asInt());
        assertEquals(1000, domain2Def.getUpperBound().asInt());

        CounterfactualSearchDomain domain3 = searchDomainsParameter.get(2);
        assertEquals(TypedValue.Kind.UNIT, domain3.getValue().getKind());
        assertFalse(domain3.getValue().toUnit().isFixed());
        assertEquals("taxCode", domain3.getName());
        assertEquals("string", domain3.getValue().getType());
        assertNotNull(domain3.getValue().toUnit().getDomain());
        assertTrue(domain3.getValue().toUnit().getDomain() instanceof CounterfactualDomainCategorical);
        CounterfactualDomainCategorical domain3Def = (CounterfactualDomainCategorical) domain3.getValue().toUnit().getDomain();
        assertEquals(3, domain3Def.getCategories().size());
        assertTrue(domain3Def.getCategories().stream().map(JsonNode::asText).collect(Collectors.toList()).containsAll(Arrays.asList("A", "B", "C")));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testCounterfactualRequestWithStructuredModel() {
        ArgumentCaptor<List<NamedTypedValue>> goalsCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List<CounterfactualSearchDomain>> searchDomainsCaptor = ArgumentCaptor.forClass(List.class);

        mockServiceWithCounterfactualRequest();

        CounterfactualRequestResponse response = given()
                .filter(new RequestLoggingFilter())
                .filter(new ResponseLoggingFilter())
                .contentType(MediaType.APPLICATION_JSON)
                .body(getCounterfactualWithStructuredModelJsonRequest())
                .when()
                .post("/executions/decisions/" + TEST_EXECUTION_ID + "/explanations/counterfactuals")
                .as(CounterfactualRequestResponse.class);

        assertNotNull(response);
        assertNotNull(response.getExecutionId());
        assertNotNull(response.getCounterfactualId());
        assertEquals(response.getExecutionId(), TEST_EXECUTION_ID);
        assertEquals(response.getCounterfactualId(), TEST_COUNTERFACTUAL_ID);

        verify(executionService).requestCounterfactuals(eq(TEST_EXECUTION_ID), goalsCaptor.capture(), searchDomainsCaptor.capture());
        List<NamedTypedValue> goalsParameter = goalsCaptor.getValue();
        assertNotNull(goalsParameter);
        assertEquals(1, goalsParameter.size());

        NamedTypedValue goal1 = goalsParameter.get(0);
        assertEquals(TypedValue.Kind.STRUCTURE, goal1.getValue().getKind());
        assertEquals("Fine", goal1.getName());
        assertEquals("tFine", goal1.getValue().getType());
        assertEquals(2, goal1.getValue().toStructure().getValue().size());

        Iterator<Map.Entry<String, TypedValue>> goal1ChildIterator = goal1.getValue().toStructure().getValue().entrySet().iterator();
        Map.Entry<String, TypedValue> goal1Child1 = goal1ChildIterator.next();
        Map.Entry<String, TypedValue> goal1Child2 = goal1ChildIterator.next();

        assertEquals(TypedValue.Kind.UNIT, goal1Child1.getValue().getKind());
        assertEquals("Amount", goal1Child1.getKey());
        assertEquals("number", goal1Child1.getValue().getType());
        assertEquals(100, goal1Child1.getValue().toUnit().getValue().asInt());

        assertEquals(TypedValue.Kind.UNIT, goal1Child2.getValue().getKind());
        assertEquals("Points", goal1Child2.getKey());
        assertEquals("number", goal1Child2.getValue().getType());
        assertEquals(0, goal1Child2.getValue().toUnit().getValue().asInt());

        List<CounterfactualSearchDomain> searchDomainsParameter = searchDomainsCaptor.getValue();
        assertNotNull(searchDomainsParameter);
        assertEquals(1, searchDomainsParameter.size());

        CounterfactualSearchDomain domain1 = searchDomainsParameter.get(0);
        assertEquals(TypedValue.Kind.STRUCTURE, domain1.getValue().getKind());
        assertEquals("Violation", domain1.getName());
        assertEquals("tViolation", domain1.getValue().getType());
        assertEquals(3, domain1.getValue().toStructure().getValue().size());

        Iterator<Map.Entry<String, CounterfactualSearchDomainValue>> domain1ChildIterator = domain1.getValue().toStructure().getValue().entrySet().iterator();
        Map.Entry<String, CounterfactualSearchDomainValue> domain1Child1 = domain1ChildIterator.next();
        Map.Entry<String, CounterfactualSearchDomainValue> domain1Child2 = domain1ChildIterator.next();
        Map.Entry<String, CounterfactualSearchDomainValue> domain1Child3 = domain1ChildIterator.next();

        assertEquals(TypedValue.Kind.UNIT, domain1Child1.getValue().getKind());
        assertFalse(domain1Child1.getValue().toUnit().isFixed());
        assertEquals("Type", domain1Child1.getKey());
        assertEquals("string", domain1Child1.getValue().getType());
        assertNotNull(domain1Child1.getValue().toUnit().getDomain());
        assertTrue(domain1Child1.getValue().toUnit().getDomain() instanceof CounterfactualDomainCategorical);
        CounterfactualDomainCategorical domain1Child1Def = (CounterfactualDomainCategorical) domain1Child1.getValue().toUnit().getDomain();
        assertEquals(2, domain1Child1Def.getCategories().size());
        assertTrue(domain1Child1Def.getCategories().stream().map(JsonNode::asText).collect(Collectors.toList()).containsAll(Arrays.asList("speed", "driving under the influence")));

        assertEquals(TypedValue.Kind.UNIT, domain1Child2.getValue().getKind());
        assertFalse(domain1Child2.getValue().toUnit().isFixed());
        assertEquals("Actual Speed", domain1Child2.getKey());
        assertEquals("number", domain1Child2.getValue().getType());
        assertNotNull(domain1Child2.getValue().toUnit().getDomain());
        assertTrue(domain1Child2.getValue().toUnit().getDomain() instanceof CounterfactualDomainRange);
        CounterfactualDomainRange domain1Child2Def = (CounterfactualDomainRange) domain1Child2.getValue().toUnit().getDomain();
        assertEquals(0, domain1Child2Def.getLowerBound().asInt());
        assertEquals(100, domain1Child2Def.getUpperBound().asInt());

        assertEquals(TypedValue.Kind.UNIT, domain1Child3.getValue().getKind());
        assertFalse(domain1Child3.getValue().toUnit().isFixed());
        assertEquals("Speed Limit", domain1Child3.getKey());
        assertEquals("number", domain1Child3.getValue().getType());
        assertNotNull(domain1Child3.getValue().toUnit().getDomain());
        assertTrue(domain1Child3.getValue().toUnit().getDomain() instanceof CounterfactualDomainRange);
        CounterfactualDomainRange domain1Child3Def = (CounterfactualDomainRange) domain1Child3.getValue().toUnit().getDomain();
        assertEquals(0, domain1Child3Def.getLowerBound().asInt());
        assertEquals(100, domain1Child3Def.getUpperBound().asInt());
    }

    @Test
    void testCounterfactualResultsWithRequest() {
        mockServiceWithCounterfactualRequest();
        mockServiceWithCounterfactualResults();

        given()
                .filter(new RequestLoggingFilter())
                .filter(new ResponseLoggingFilter())
                .contentType(MediaType.APPLICATION_JSON)
                .body(getCounterfactualJsonRequest())
                .when()
                .post("/executions/decisions/" + TEST_EXECUTION_ID + "/explanations/counterfactuals")
                .as(CounterfactualRequestResponse.class);

        CounterfactualResultsResponse resultsResponse = given()
                .filter(new RequestLoggingFilter())
                .filter(new ResponseLoggingFilter())
                .when()
                .get("/executions/decisions/" + TEST_EXECUTION_ID + "/explanations/counterfactuals/" + TEST_COUNTERFACTUAL_ID)
                .as(CounterfactualResultsResponse.class);

        assertNotNull(resultsResponse);
        assertNotNull(resultsResponse.getExecutionId());
        assertNotNull(resultsResponse.getCounterfactualId());
        assertEquals(resultsResponse.getExecutionId(), TEST_EXECUTION_ID);
        assertEquals(resultsResponse.getCounterfactualId(), TEST_COUNTERFACTUAL_ID);
        assertEquals(2, resultsResponse.getSolutions().size());
    }

    @Test
    void testCounterfactualResultsWithRequestWithoutResults() {
        mockServiceWithCounterfactualRequest();

        given()
                .filter(new RequestLoggingFilter())
                .filter(new ResponseLoggingFilter())
                .contentType(MediaType.APPLICATION_JSON)
                .body(getCounterfactualJsonRequest())
                .when()
                .post("/executions/decisions/" + TEST_EXECUTION_ID + "/explanations/counterfactuals")
                .as(CounterfactualRequestResponse.class);

        CounterfactualResultsResponse resultsResponse = given()
                .filter(new RequestLoggingFilter())
                .filter(new ResponseLoggingFilter())
                .when()
                .get("/executions/decisions/" + TEST_EXECUTION_ID + "/explanations/counterfactuals/" + TEST_COUNTERFACTUAL_ID)
                .as(CounterfactualResultsResponse.class);

        assertNotNull(resultsResponse);
        assertNotNull(resultsResponse.getExecutionId());
        assertNotNull(resultsResponse.getCounterfactualId());
        assertEquals(resultsResponse.getExecutionId(), TEST_EXECUTION_ID);
        assertEquals(resultsResponse.getCounterfactualId(), TEST_COUNTERFACTUAL_ID);
        assertTrue(resultsResponse.getSolutions().isEmpty());
    }

    @Test
    void testCounterfactualResultsWithoutRequest() {
        given().filter(new ResponseLoggingFilter())
                .when()
                .get("/executions/decisions/" + TEST_EXECUTION_ID + "/explanations/counterfactuals/" + TEST_COUNTERFACTUAL_ID)
                .then().statusCode(400);
    }

    private void mockServiceWithExplainabilityResult() {
        when(executionService.getExplainabilityResultById(eq(TEST_EXECUTION_ID), any()))
                .thenReturn(buildValidExplainabilityResult());
    }

    private void mockServiceWithNullExplainabilityResult() {
        when(executionService.getExplainabilityResultById(anyString(), any()))
                .thenReturn(null);
    }

    private void mockServiceWithoutExplainabilityResult() {
        when(executionService.getExplainabilityResultById(anyString(), any()))
                .thenThrow(new IllegalArgumentException("Explainability result does not exist."));
    }

    private void mockServiceWithCounterfactualRequest() {
        when(executionService.requestCounterfactuals(eq(TEST_EXECUTION_ID), any(), any()))
                .thenReturn(buildValidCounterfactual());
        when(executionService.getCounterfactualRequest(eq(TEST_EXECUTION_ID), eq(TEST_COUNTERFACTUAL_ID)))
                .thenReturn(buildValidCounterfactual());
    }

    private void mockServiceWithCounterfactualResults() {
        when(executionService.getCounterfactualResults(eq(TEST_EXECUTION_ID), eq(TEST_COUNTERFACTUAL_ID)))
                .thenReturn(buildValidCounterfactualResults());
    }
}
