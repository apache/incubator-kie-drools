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
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.kie.kogito.trusty.service.common.TrustyService;
import org.kie.kogito.trusty.service.common.responses.CounterfactualRequestResponse;
import org.kie.kogito.trusty.service.common.responses.DecisionStructuredInputsResponse;
import org.kie.kogito.trusty.service.common.responses.SalienciesResponse;
import org.kie.kogito.trusty.storage.api.model.BaseExplainabilityResult;
import org.kie.kogito.trusty.storage.api.model.CounterfactualExplainabilityRequest;
import org.kie.kogito.trusty.storage.api.model.CounterfactualSearchDomain;
import org.kie.kogito.trusty.storage.api.model.LIMEExplainabilityResult;
import org.kie.kogito.trusty.storage.api.model.TypedVariableWithValue;

@Path("executions/decisions")
public class ExplainabilityApiV1 {

    @Inject
    TrustyService trustyService;

    @GET
    @Path("/{executionId}/explanations/saliencies")
    @APIResponses(value = {
            @APIResponse(description = "Gets the local explanation of a decision.", responseCode = "200",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(type = SchemaType.OBJECT, implementation = SalienciesResponse.class))),
            @APIResponse(description = "Bad Request", responseCode = "400", content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    @Operation(
            summary = "Returns the saliencies for a decision.",
            description = "Returns the saliencies for a particular decision calculated using the lime algorithm.")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSaliencies(
            @Parameter(
                    name = "executionId",
                    description = "The execution ID.",
                    required = true,
                    schema = @Schema(implementation = String.class)) @PathParam("executionId") String executionId) {
        return retrieveExplainabilityResult(executionId, LIMEExplainabilityResult.class)
                .map(obj -> Response.ok(new SalienciesResponse(obj)).build())
                .orElseGet(() -> Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build());
    }

    private <T extends BaseExplainabilityResult> Optional<T> retrieveExplainabilityResult(String executionId, Class<T> type) {
        try {
            return Optional.ofNullable(trustyService.getExplainabilityResultById(executionId, type));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    @POST
    @Path("/{executionId}/explanations/counterfactuals")
    @APIResponses(value = {
            @APIResponse(description = "UUID, counterfactualId, for the calculation request.", responseCode = "200",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(type = SchemaType.OBJECT, implementation = CounterfactualRequestResponse.class))),
            @APIResponse(description = "Bad Request", responseCode = "400", content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    @Operation(
            summary = "Request calculation of the counterfactuals for a decision.",
            description = "Requests calculation of the counterfactuals for a particular decision. Results of the calculation " +
                    "can be obtained by GETing /{executionId}/explanations/counterfactuals/{counterfactualId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response requestCounterfactuals(
            @Parameter(
                    name = "executionId",
                    description = "The execution ID.",
                    required = true,
                    schema = @Schema(implementation = String.class)) @PathParam("executionId") String executionId,
            @Parameter(
                    name = "Counterfactual request",
                    description = "The definition of a request to calculate a decision's Counterfactuals.",
                    required = true,
                    schema = @Schema(
                            implementation = org.kie.kogito.trusty.service.common.requests.CounterfactualRequest.class)) org.kie.kogito.trusty.service.common.requests.CounterfactualRequest request) {
        List<TypedVariableWithValue> goals = request.getGoals();
        List<CounterfactualSearchDomain> searchDomains = request.getSearchDomains();
        return requestCounterfactualsForExecution(executionId, goals, searchDomains)
                .map(obj -> new CounterfactualRequestResponse(obj.getExecutionId(), obj.getCounterfactualId()))
                .map(Response::ok)
                .orElseGet(() -> Response.status(Response.Status.BAD_REQUEST.getStatusCode()))
                .build();
    }

    private Optional<CounterfactualExplainabilityRequest> requestCounterfactualsForExecution(String executionId,
            List<TypedVariableWithValue> goals,
            List<CounterfactualSearchDomain> searchDomains) {
        try {
            return Optional.ofNullable(trustyService.requestCounterfactuals(executionId, goals, searchDomains));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    @GET
    @Path("/{executionId}/explanations/counterfactuals")
    @APIResponses(value = {
            @APIResponse(description = "All counterfactuals for a decision.", responseCode = "200",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(type = SchemaType.OBJECT, implementation = DecisionStructuredInputsResponse.class))),
            @APIResponse(description = "Bad Request", responseCode = "400", content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    @Operation(
            summary = "Returns all of the counterfactuals for a decision.",
            description = "Returns all of the counterfactuals for a particular decision.")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCounterfactuals(
            @Parameter(
                    name = "executionId",
                    description = "The execution ID.",
                    required = true,
                    schema = @Schema(implementation = String.class)) @PathParam("executionId") String executionId) {
        return getCounterfactualRequestsForExecution(executionId)
                .map(obj -> obj.stream().map(cf -> new CounterfactualRequestResponse(cf.getExecutionId(), cf.getCounterfactualId())).collect(Collectors.toList()))
                .map(Response::ok)
                .orElseGet(() -> Response.status(Response.Status.BAD_REQUEST.getStatusCode()))
                .build();
    }

    private Optional<List<CounterfactualExplainabilityRequest>> getCounterfactualRequestsForExecution(String executionId) {
        try {
            return Optional.ofNullable(trustyService.getCounterfactualRequests(executionId));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    @GET
    @Path("/{executionId}/explanations/counterfactuals/{counterfactualId}")
    @APIResponses(value = {
            @APIResponse(description = "A specific counterfactuals for a decision.", responseCode = "200",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(type = SchemaType.OBJECT, implementation = DecisionStructuredInputsResponse.class))),
            @APIResponse(description = "Bad Request", responseCode = "400", content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    @Operation(
            summary = "Returns a specific counterfactual for a decision.",
            description = "Returns a specific counterfactual for a particular decision.")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCounterfactual(
            @Parameter(
                    name = "executionId",
                    description = "The execution ID.",
                    required = true,
                    schema = @Schema(implementation = String.class)) @PathParam("executionId") String executionId,
            @Parameter(
                    name = "counterfactualId",
                    description = "The Counterfactual ID.",
                    required = true,
                    schema = @Schema(implementation = String.class)) @PathParam("counterfactualId") String counterfactualId) {
        return getCounterfactualForExecution(executionId, counterfactualId)
                .map(obj -> new CounterfactualRequestResponse(obj.getExecutionId(), obj.getCounterfactualId()))
                .map(Response::ok)
                .orElseGet(() -> Response.status(Response.Status.BAD_REQUEST.getStatusCode()))
                .build();
    }

    private Optional<CounterfactualExplainabilityRequest> getCounterfactualForExecution(String executionId, String counterfactualId) {
        try {
            return Optional.ofNullable(trustyService.getCounterfactualRequest(executionId, counterfactualId));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

}
