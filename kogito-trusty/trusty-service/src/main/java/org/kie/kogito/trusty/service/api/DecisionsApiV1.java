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

package org.kie.kogito.trusty.service.api;

import java.util.Optional;
import java.util.function.Function;

import javax.inject.Inject;
import javax.ws.rs.GET;
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
import org.kie.kogito.trusty.service.ITrustyService;
import org.kie.kogito.trusty.service.responses.DecisionOutcomeResponse;
import org.kie.kogito.trusty.service.responses.DecisionOutcomesResponse;
import org.kie.kogito.trusty.service.responses.DecisionStructuredInputsResponse;
import org.kie.kogito.trusty.service.responses.ExecutionHeaderResponse;
import org.kie.kogito.trusty.storage.api.model.Decision;

@Path("v1/executions/decisions")
public class DecisionsApiV1 {

    @Inject
    ITrustyService trustyService;

    /**
     * Gets an execution header by ID.
     * @param executionId The execution ID.
     * @return The execution header.
     */
    @GET
    @Path("/{executionId}")
    @APIResponses(value = {
            @APIResponse(description = "Gets the decision detail header.", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(type = SchemaType.OBJECT, implementation = ExecutionHeaderResponse.class))),
            @APIResponse(description = "Bad Request", responseCode = "400", content = @Content(mediaType = MediaType.TEXT_PLAIN))
    }
    )
    @Operation(summary = "Gets The decision header with details.", description = "Gets the decision detail header.")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getExecutionById(
            @Parameter(
                    name = "executionId",
                    description = "The execution ID.",
                    required = true,
                    schema = @Schema(implementation = String.class)
            ) @PathParam("executionId") String executionId) {
        return handleDecisionRequest(executionId, ExecutionHeaderResponse::fromExecution);
    }

    @GET
    @Path("/{executionId}/structuredInputs")
    @APIResponses(value = {
            @APIResponse(description = "Gets the decision structured inputs.", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(type = SchemaType.OBJECT, implementation = DecisionStructuredInputsResponse.class))),
            @APIResponse(description = "Bad Request", responseCode = "400", content = @Content(mediaType = MediaType.TEXT_PLAIN))
    }
    )
    @Operation(summary = "Gets the decision structured inputs.")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStructuredInputs(
            @Parameter(
                    name = "executionId",
                    description = "The execution ID.",
                    required = true,
                    schema = @Schema(implementation = String.class)
            ) @PathParam("executionId") String executionId) {
        return handleDecisionRequest(executionId, DecisionStructuredInputsResponse::from);
    }

    @GET
    @Path("/{executionId}/outcomes")
    @APIResponses(value = {
            @APIResponse(description = "Gets the decision outcomes.", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(type = SchemaType.OBJECT, implementation = DecisionOutcomesResponse.class))),
            @APIResponse(description = "Bad Request", responseCode = "400", content = @Content(mediaType = MediaType.TEXT_PLAIN))
    }
    )
    @Operation(summary = "Gets the decision outcomes.")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOutcomes(
            @Parameter(
                    name = "executionId",
                    description = "The execution ID.",
                    required = true,
                    schema = @Schema(implementation = String.class)
            ) @PathParam("executionId") String executionId) {
        return handleDecisionRequest(executionId, DecisionOutcomesResponse::from);
    }

    @GET
    @Path("/{executionId}/outcomes/{outcomeId}")
    @APIResponses(value = {
            @APIResponse(description = "Gets a specific outcome of a decision.", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(type = SchemaType.OBJECT, implementation = DecisionOutcomeResponse.class))),
            @APIResponse(description = "Bad Request", responseCode = "400", content = @Content(mediaType = MediaType.TEXT_PLAIN))
    }
    )
    @Operation(summary = "Gets a specific outcome of a decision.")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOutcomeById(
            @Parameter(
                    name = "executionId",
                    description = "The execution ID.",
                    required = true,
                    schema = @Schema(implementation = String.class)
            ) @PathParam("executionId") String executionId,
            @Parameter(
                    name = "outcomeId",
                    description = "The outcome ID.",
                    required = true,
                    schema = @Schema(implementation = String.class)
            ) @PathParam("outcomeId") String outcomeId) {
        return handleDecisionRequest(executionId, decision -> decision.getOutcomes() == null ? null : decision.getOutcomes().stream()
                .filter(outcome -> outcomeId != null && outcomeId.equals(outcome.getOutcomeId()))
                .findFirst()
                .map(DecisionOutcomeResponse::from)
                .orElse(null)
        );
    }

    private Response handleDecisionRequest(String executionId, Function<Decision, Object> transformer) {
        return retrieveDecision(executionId)
                .map(transformer)
                .map(obj -> Response.ok(obj).build())
                .orElseGet(() -> Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build());
    }

    private Optional<Decision> retrieveDecision(String executionId) {
        try {
            return Optional.of(trustyService.getDecisionById(executionId));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }
}