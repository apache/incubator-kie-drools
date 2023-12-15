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

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.api.CounterfactualDomainCategorical;
import org.kie.kogito.explainability.api.CounterfactualExplainabilityRequest;
import org.kie.kogito.explainability.api.CounterfactualExplainabilityResult;
import org.kie.kogito.explainability.api.CounterfactualSearchDomain;
import org.kie.kogito.explainability.api.ExplainabilityStatus;
import org.kie.kogito.explainability.api.ModelIdentifier;
import org.kie.kogito.explainability.api.NamedTypedValue;
import org.kie.kogito.trusty.service.common.TrustyService;
import org.kie.kogito.trusty.service.common.responses.CounterfactualRequestResponse;
import org.kie.kogito.trusty.service.common.responses.CounterfactualResultsResponse;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.node.TextNode;

import jakarta.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kie.kogito.trusty.service.common.TypedValueTestUtils.buildGoalUnit;
import static org.kie.kogito.trusty.service.common.TypedValueTestUtils.buildSearchDomainUnit;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExplainabilityApiV1Test {

    private static final String EXECUTION_ID = "executionId";

    private static final String SERVICE_URL = "serviceUrl";

    private static final String COUNTERFACTUAL_ID = "counterfactualId";

    private static final TrustyService trustyService = mock(TrustyService.class);

    private static final ExplainabilityApiV1 explainabilityEndpoint = new ExplainabilityApiV1();

    private static final Long MAX_RUNNING_TIME_SECONDS = 60L;

    @BeforeAll
    public static void initialise() {
        explainabilityEndpoint.trustyService = trustyService;
    }

    @BeforeEach
    public void reset() {
        Mockito.reset(trustyService);
    }

    @Test
    public void testRequestCounterfactualsWhenExecutionDoesNotExist() {
        when(trustyService.requestCounterfactuals(anyString(), any(), any())).thenThrow(new IllegalArgumentException());

        org.kie.kogito.trusty.service.common.requests.CounterfactualRequest request =
                new org.kie.kogito.trusty.service.common.requests.CounterfactualRequest(Collections.emptyList(), Collections.emptyList());

        Response response = explainabilityEndpoint.requestCounterfactuals(EXECUTION_ID, request);
        assertNotNull(response);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void testRequestCounterfactualsWhenExecutionDoesExist() {
        when(trustyService.requestCounterfactuals(anyString(), any(), any())).thenReturn(new CounterfactualExplainabilityRequest(EXECUTION_ID,
                SERVICE_URL,
                new ModelIdentifier("resourceType", "resourceIdentifier"),
                COUNTERFACTUAL_ID,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                MAX_RUNNING_TIME_SECONDS));

        org.kie.kogito.trusty.service.common.requests.CounterfactualRequest request =
                new org.kie.kogito.trusty.service.common.requests.CounterfactualRequest(Collections.emptyList(), Collections.emptyList());

        Response response = explainabilityEndpoint.requestCounterfactuals(EXECUTION_ID, request);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Object entity = response.getEntity();
        assertNotNull(entity);
        assertTrue(entity instanceof CounterfactualRequestResponse);
        CounterfactualRequestResponse counterfactualRequestResponse = (CounterfactualRequestResponse) entity;
        assertEquals(EXECUTION_ID, counterfactualRequestResponse.getExecutionId());
        assertEquals(COUNTERFACTUAL_ID, counterfactualRequestResponse.getCounterfactualId());
        assertEquals(MAX_RUNNING_TIME_SECONDS, counterfactualRequestResponse.getMaxRunningTimeSeconds());
    }

    @Test
    public void testGetAllCounterfactualsWhenExecutionDoesNotExist() {
        when(trustyService.getCounterfactualRequests(anyString())).thenThrow(new IllegalArgumentException());

        Response response = explainabilityEndpoint.getAllCounterfactualsSummary(EXECUTION_ID);
        assertNotNull(response);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testGetAllCounterfactualsWhenExecutionDoesExist() {
        when(trustyService.getCounterfactualRequests(anyString())).thenReturn(List.of(new CounterfactualExplainabilityRequest(EXECUTION_ID,
                SERVICE_URL,
                new ModelIdentifier("resourceType", "resourceIdentifier"),
                COUNTERFACTUAL_ID,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                MAX_RUNNING_TIME_SECONDS)));

        Response response = explainabilityEndpoint.getAllCounterfactualsSummary(EXECUTION_ID);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Object entity = response.getEntity();
        assertNotNull(entity);
        assertTrue(entity instanceof List);
        List<CounterfactualRequestResponse> counterfactualRequestResponse = (List) entity;
        assertEquals(1, counterfactualRequestResponse.size());

        CounterfactualRequestResponse counterfactual = counterfactualRequestResponse.get(0);
        assertEquals(EXECUTION_ID, counterfactual.getExecutionId());
        assertEquals(COUNTERFACTUAL_ID, counterfactual.getCounterfactualId());
        assertEquals(MAX_RUNNING_TIME_SECONDS, counterfactual.getMaxRunningTimeSeconds());
    }

    @Test
    public void testGetCounterfactualResultsWhenExecutionDoesNotExist() {
        when(trustyService.getCounterfactualRequest(anyString(), anyString())).thenThrow(new IllegalArgumentException());

        Response response = explainabilityEndpoint.getCounterfactualDetails(EXECUTION_ID, COUNTERFACTUAL_ID);
        assertNotNull(response);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void testGetCounterfactualResultsWhenExecutionDoesExist() {
        NamedTypedValue goal = buildGoalUnit("unit",
                "string",
                new TextNode("hello"));
        CounterfactualSearchDomain searchDomain =
                buildSearchDomainUnit("unit",
                        "string",
                        new CounterfactualDomainCategorical(List.of(new TextNode("hello"), new TextNode("goodbye"))));
        when(trustyService.getCounterfactualRequest(anyString(), anyString()))
                .thenReturn(new CounterfactualExplainabilityRequest(EXECUTION_ID,
                        SERVICE_URL,
                        new ModelIdentifier("resourceType", "resourceIdentifier"),
                        COUNTERFACTUAL_ID,
                        Collections.emptyList(),
                        List.of(goal),
                        List.of(searchDomain),
                        MAX_RUNNING_TIME_SECONDS));

        Response response = explainabilityEndpoint.getCounterfactualDetails(EXECUTION_ID, COUNTERFACTUAL_ID);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Object entity = response.getEntity();
        assertNotNull(entity);
        assertTrue(entity instanceof CounterfactualResultsResponse);
        CounterfactualResultsResponse resultsResponse = (CounterfactualResultsResponse) entity;
        assertEquals(EXECUTION_ID, resultsResponse.getExecutionId());
        assertEquals(COUNTERFACTUAL_ID, resultsResponse.getCounterfactualId());
        assertEquals(MAX_RUNNING_TIME_SECONDS, resultsResponse.getMaxRunningTimeSeconds());
        assertEquals(1, resultsResponse.getGoals().size());
        assertEquals(goal, resultsResponse.getGoals().iterator().next());
        assertEquals(1, resultsResponse.getSearchDomains().size());
        assertEquals(searchDomain, resultsResponse.getSearchDomains().iterator().next());
        assertTrue(resultsResponse.getSolutions().isEmpty());
    }

    @Test
    public void testGetCounterfactualResultsWhenExecutionDoesExistAndResultsHaveBeenCreated() {
        NamedTypedValue goal = buildGoalUnit("unit",
                "string",
                new TextNode("hello"));
        CounterfactualSearchDomain searchDomain = buildSearchDomainUnit("unit",
                "string",
                new CounterfactualDomainCategorical(List.of(new TextNode("hello"), new TextNode("goodbye"))));

        CounterfactualExplainabilityResult solution1 = new CounterfactualExplainabilityResult(EXECUTION_ID,
                COUNTERFACTUAL_ID,
                "solution1",
                0L,
                ExplainabilityStatus.SUCCEEDED,
                "",
                true,
                CounterfactualExplainabilityResult.Stage.INTERMEDIATE,
                Collections.emptyList(),
                Collections.emptyList());
        CounterfactualExplainabilityResult solution2 = new CounterfactualExplainabilityResult(EXECUTION_ID,
                COUNTERFACTUAL_ID,
                "solution2",
                1L,
                ExplainabilityStatus.SUCCEEDED,
                "",
                true,
                CounterfactualExplainabilityResult.Stage.FINAL,
                Collections.emptyList(),
                Collections.emptyList());

        when(trustyService.getCounterfactualRequest(anyString(), anyString()))
                .thenReturn(new CounterfactualExplainabilityRequest(EXECUTION_ID,
                        SERVICE_URL,
                        new ModelIdentifier("resourceType", "resourceIdentifier"),
                        COUNTERFACTUAL_ID,
                        Collections.emptyList(),
                        List.of(goal),
                        List.of(searchDomain),
                        MAX_RUNNING_TIME_SECONDS));
        when(trustyService.getCounterfactualResults(anyString(), anyString())).thenReturn(List.of(solution1, solution2));

        Response response = explainabilityEndpoint.getCounterfactualDetails(EXECUTION_ID, COUNTERFACTUAL_ID);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Object entity = response.getEntity();
        assertNotNull(entity);
        assertTrue(entity instanceof CounterfactualResultsResponse);
        CounterfactualResultsResponse resultsResponse = (CounterfactualResultsResponse) entity;
        assertEquals(EXECUTION_ID, resultsResponse.getExecutionId());
        assertEquals(COUNTERFACTUAL_ID, resultsResponse.getCounterfactualId());
        assertEquals(MAX_RUNNING_TIME_SECONDS, resultsResponse.getMaxRunningTimeSeconds());
        assertEquals(1, resultsResponse.getGoals().size());
        assertEquals(goal, resultsResponse.getGoals().iterator().next());
        assertEquals(1, resultsResponse.getSearchDomains().size());
        assertEquals(searchDomain, resultsResponse.getSearchDomains().iterator().next());
        assertEquals(2, resultsResponse.getSolutions().size());
        assertEquals(solution1, resultsResponse.getSolutions().get(0));
        assertEquals(solution2, resultsResponse.getSolutions().get(1));
    }
}
