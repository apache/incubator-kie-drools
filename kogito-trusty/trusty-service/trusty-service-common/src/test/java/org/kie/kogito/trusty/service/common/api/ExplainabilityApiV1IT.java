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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;
import org.kie.kogito.tracing.typedvalue.TypedValue;
import org.kie.kogito.trusty.service.common.TrustyService;
import org.kie.kogito.trusty.service.common.responses.CounterfactualRequestResponse;
import org.kie.kogito.trusty.service.common.responses.SalienciesResponse;
import org.kie.kogito.trusty.storage.api.model.BaseExplainabilityResult;
import org.kie.kogito.trusty.storage.api.model.CounterfactualDomainCategorical;
import org.kie.kogito.trusty.storage.api.model.CounterfactualDomainRange;
import org.kie.kogito.trusty.storage.api.model.CounterfactualExplainabilityRequest;
import org.kie.kogito.trusty.storage.api.model.CounterfactualSearchDomain;
import org.kie.kogito.trusty.storage.api.model.ExplainabilityStatus;
import org.kie.kogito.trusty.storage.api.model.FeatureImportanceModel;
import org.kie.kogito.trusty.storage.api.model.LIMEExplainabilityResult;
import org.kie.kogito.trusty.storage.api.model.SaliencyModel;
import org.kie.kogito.trusty.storage.api.model.TypedVariableWithValue;
import org.mockito.ArgumentCaptor;
import org.testcontainers.shaded.org.apache.commons.lang.builder.CompareToBuilder;

import com.fasterxml.jackson.databind.JsonNode;

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

    private static final String TEST_COUNTERFACTUAL_ID = "counterfactualId";

    @InjectMock
    TrustyService executionService;

    private static BaseExplainabilityResult buildValidExplainabilityResult() {
        return new LIMEExplainabilityResult(
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

    private static CounterfactualExplainabilityRequest buildValidCounterfactual() {
        return new CounterfactualExplainabilityRequest(TEST_EXECUTION_ID, TEST_COUNTERFACTUAL_ID);
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
        ArgumentCaptor<List<TypedVariableWithValue>> goalsCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List<CounterfactualSearchDomain>> searchDomainsCaptor = ArgumentCaptor.forClass(List.class);

        mockServiceWithCounterfactualRequest();

        CounterfactualRequestResponse response = given()
                .filter(new RequestLoggingFilter())
                .filter(new ResponseLoggingFilter())
                .contentType(MediaType.APPLICATION_JSON)
                .body(getCounterfactualJsonRequest())
                .when().post("/executions/decisions/" + TEST_EXECUTION_ID + "/explanations/counterfactuals")
                .as(CounterfactualRequestResponse.class);

        assertNotNull(response);
        assertNotNull(response.getExecutionId());
        assertNotNull(response.getCounterfactualId());
        assertEquals(response.getExecutionId(), TEST_EXECUTION_ID);
        assertEquals(response.getCounterfactualId(), TEST_COUNTERFACTUAL_ID);

        verify(executionService).requestCounterfactuals(eq(TEST_EXECUTION_ID), goalsCaptor.capture(), searchDomainsCaptor.capture());
        List<TypedVariableWithValue> goalsParameter = goalsCaptor.getValue();
        assertNotNull(goalsParameter);
        assertEquals(2, goalsParameter.size());

        TypedVariableWithValue goal1 = goalsParameter.get(0);
        assertEquals(TypedValue.Kind.UNIT, goal1.getKind());
        assertEquals("deposit", goal1.getName());
        assertEquals("number", goal1.getTypeRef());
        assertEquals(5000, goal1.getValue().asInt());

        TypedVariableWithValue goal2 = goalsParameter.get(1);
        assertEquals(TypedValue.Kind.UNIT, goal2.getKind());
        assertEquals("approved", goal2.getName());
        assertEquals("boolean", goal2.getTypeRef());
        assertEquals(Boolean.TRUE, goal2.getValue().asBoolean());

        List<CounterfactualSearchDomain> searchDomainsParameter = searchDomainsCaptor.getValue();
        assertNotNull(searchDomainsParameter);
        assertEquals(3, searchDomainsParameter.size());

        CounterfactualSearchDomain domain1 = searchDomainsParameter.get(0);
        assertTrue(domain1.isFixed());
        assertEquals(TypedValue.Kind.UNIT, domain1.getKind());
        assertEquals("age", domain1.getName());
        assertEquals("number", domain1.getTypeRef());
        assertNull(domain1.getDomain());

        CounterfactualSearchDomain domain2 = searchDomainsParameter.get(1);
        assertFalse(domain2.isFixed());
        assertEquals(TypedValue.Kind.UNIT, domain2.getKind());
        assertEquals("income", domain2.getName());
        assertEquals("number", domain2.getTypeRef());
        assertNotNull(domain2.getDomain());
        assertTrue(domain2.getDomain() instanceof CounterfactualDomainRange);
        CounterfactualDomainRange domain2Def = (CounterfactualDomainRange) domain2.getDomain();
        assertEquals(0, domain2Def.getLowerBound().asInt());
        assertEquals(1000, domain2Def.getUpperBound().asInt());

        CounterfactualSearchDomain domain3 = searchDomainsParameter.get(2);
        assertFalse(domain3.isFixed());
        assertEquals(TypedValue.Kind.UNIT, domain3.getKind());
        assertEquals("taxCode", domain3.getName());
        assertEquals("string", domain3.getTypeRef());
        assertNotNull(domain3.getDomain());
        assertTrue(domain3.getDomain() instanceof CounterfactualDomainCategorical);
        CounterfactualDomainCategorical domain3Def = (CounterfactualDomainCategorical) domain3.getDomain();
        assertEquals(3, domain3Def.getCategories().size());
        assertTrue(domain3Def.getCategories().stream().map(JsonNode::asText).collect(Collectors.toList()).containsAll(Arrays.asList("A", "B", "C")));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testCounterfactualRequestWithStructuredModel() {
        ArgumentCaptor<List<TypedVariableWithValue>> goalsCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List<CounterfactualSearchDomain>> searchDomainsCaptor = ArgumentCaptor.forClass(List.class);

        mockServiceWithCounterfactualRequest();

        CounterfactualRequestResponse response = given()
                .filter(new RequestLoggingFilter())
                .filter(new ResponseLoggingFilter())
                .contentType(MediaType.APPLICATION_JSON)
                .body(getCounterfactualWithStructuredModelJsonRequest())
                .when().post("/executions/decisions/" + TEST_EXECUTION_ID + "/explanations/counterfactuals")
                .as(CounterfactualRequestResponse.class);

        assertNotNull(response);
        assertNotNull(response.getExecutionId());
        assertNotNull(response.getCounterfactualId());
        assertEquals(response.getExecutionId(), TEST_EXECUTION_ID);
        assertEquals(response.getCounterfactualId(), TEST_COUNTERFACTUAL_ID);

        verify(executionService).requestCounterfactuals(eq(TEST_EXECUTION_ID), goalsCaptor.capture(), searchDomainsCaptor.capture());
        List<TypedVariableWithValue> goalsParameter = goalsCaptor.getValue();
        assertNotNull(goalsParameter);
        assertEquals(1, goalsParameter.size());

        TypedVariableWithValue goal1 = goalsParameter.get(0);
        assertEquals(TypedValue.Kind.STRUCTURE, goal1.getKind());
        assertEquals("Fine", goal1.getName());
        assertEquals("tFine", goal1.getTypeRef());
        assertEquals(2, goal1.getComponents().size());

        Iterator<TypedVariableWithValue> goal1ChildIterator = goal1.getComponents().iterator();
        TypedVariableWithValue goal1Child1 = goal1ChildIterator.next();
        TypedVariableWithValue goal1Child2 = goal1ChildIterator.next();

        assertEquals(TypedValue.Kind.UNIT, goal1Child1.getKind());
        assertEquals("Amount", goal1Child1.getName());
        assertEquals("number", goal1Child1.getTypeRef());
        assertEquals(100, goal1Child1.getValue().asInt());
        assertNull(goal1Child1.getComponents());

        assertEquals(TypedValue.Kind.UNIT, goal1Child2.getKind());
        assertEquals("Points", goal1Child2.getName());
        assertEquals("number", goal1Child2.getTypeRef());
        assertEquals(0, goal1Child2.getValue().asInt());
        assertNull(goal1Child2.getComponents());

        List<CounterfactualSearchDomain> searchDomainsParameter = searchDomainsCaptor.getValue();
        assertNotNull(searchDomainsParameter);
        assertEquals(1, searchDomainsParameter.size());

        CounterfactualSearchDomain domain1 = searchDomainsParameter.get(0);
        assertFalse(domain1.isFixed());
        assertEquals(TypedValue.Kind.STRUCTURE, domain1.getKind());
        assertEquals("Violation", domain1.getName());
        assertEquals("tViolation", domain1.getTypeRef());
        assertNull(domain1.getDomain());
        assertEquals(3, domain1.getComponents().size());

        Iterator<CounterfactualSearchDomain> domain1ChildIterator = domain1.getComponents().iterator();
        CounterfactualSearchDomain domain1Child1 = domain1ChildIterator.next();
        CounterfactualSearchDomain domain1Child2 = domain1ChildIterator.next();
        CounterfactualSearchDomain domain1Child3 = domain1ChildIterator.next();

        assertFalse(domain1Child1.isFixed());
        assertEquals(TypedValue.Kind.UNIT, domain1Child1.getKind());
        assertEquals("Type", domain1Child1.getName());
        assertEquals("string", domain1Child1.getTypeRef());
        assertNotNull(domain1Child1.getDomain());
        assertTrue(domain1Child1.getDomain() instanceof CounterfactualDomainCategorical);
        CounterfactualDomainCategorical domain1Child1Def = (CounterfactualDomainCategorical) domain1Child1.getDomain();
        assertEquals(2, domain1Child1Def.getCategories().size());
        assertTrue(domain1Child1Def.getCategories().stream().map(JsonNode::asText).collect(Collectors.toList()).containsAll(Arrays.asList("speed", "driving under the influence")));

        assertFalse(domain1Child2.isFixed());
        assertEquals(TypedValue.Kind.UNIT, domain1Child2.getKind());
        assertEquals("Actual Speed", domain1Child2.getName());
        assertEquals("number", domain1Child2.getTypeRef());
        assertNotNull(domain1Child2.getDomain());
        assertTrue(domain1Child2.getDomain() instanceof CounterfactualDomainRange);
        CounterfactualDomainRange domain1Child2Def = (CounterfactualDomainRange) domain1Child2.getDomain();
        assertEquals(0, domain1Child2Def.getLowerBound().asInt());
        assertEquals(100, domain1Child2Def.getUpperBound().asInt());

        assertFalse(domain1Child3.isFixed());
        assertEquals(TypedValue.Kind.UNIT, domain1Child3.getKind());
        assertEquals("Speed Limit", domain1Child3.getName());
        assertEquals("number", domain1Child3.getTypeRef());
        assertNotNull(domain1Child3.getDomain());
        assertTrue(domain1Child3.getDomain() instanceof CounterfactualDomainRange);
        CounterfactualDomainRange domain1Child3Def = (CounterfactualDomainRange) domain1Child3.getDomain();
        assertEquals(0, domain1Child3Def.getLowerBound().asInt());
        assertEquals(100, domain1Child3Def.getUpperBound().asInt());
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
    }
}
