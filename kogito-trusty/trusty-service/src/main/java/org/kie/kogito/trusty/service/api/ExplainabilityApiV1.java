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

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
import org.kie.kogito.trusty.service.TrustyService;
import org.kie.kogito.trusty.service.responses.DecisionStructuredInputsResponse;
import org.kie.kogito.trusty.service.responses.FeatureImportanceResponse;
import org.kie.kogito.trusty.service.responses.SalienciesResponse;
import org.kie.kogito.trusty.service.responses.SaliencyResponse;
import org.kie.kogito.trusty.storage.api.model.ExplainabilityResult;
import org.kie.kogito.trusty.storage.api.model.FeatureImportance;
import org.kie.kogito.trusty.storage.api.model.Saliency;

@Path("executions/decisions")
public class ExplainabilityApiV1 {

    @Inject
    TrustyService trustyService;

    @GET
    @Path("/{executionId}/explanations/saliencies")
    @APIResponses(value = {
            @APIResponse(description = "Gets the local explanation of a decision.", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(type = SchemaType.OBJECT, implementation = DecisionStructuredInputsResponse.class))),
            @APIResponse(description = "Bad Request", responseCode = "400", content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    @Operation(
            summary = "Returns the saliencies for a decision.",
            description = "Returns the saliencies for a particular decision calculated using the lime algorithm."
    )
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStructuredInputs(
            @Parameter(
                    name = "executionId",
                    description = "The execution ID.",
                    required = true,
                    schema = @Schema(implementation = String.class)
            ) @PathParam("executionId") String executionId) {
        return retrieveExplainabilityResult(executionId)
                .map(ExplainabilityApiV1::explainabilityResultModelToResponse)
                .map(Response::ok)
                .orElseGet(() -> Response.status(Response.Status.BAD_REQUEST.getStatusCode()))
                .build();
    }

    private Optional<ExplainabilityResult> retrieveExplainabilityResult(String executionId) {
        try {
            return Optional.ofNullable(trustyService.getExplainabilityResultById(executionId));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    static SalienciesResponse explainabilityResultModelToResponse(ExplainabilityResult model) {
        if (model == null) {
            return null;
        }
        return new SalienciesResponse(
                model.getStatus().name(),
                model.getStatusDetails(),
                model.getSaliencies().stream()
                        .map(ExplainabilityApiV1::saliencyModelToResponse)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
        );
    }

    static FeatureImportanceResponse featureImportanceModelToResponse(FeatureImportance model) {
        if (model == null) {
            return null;
        }
        return new FeatureImportanceResponse(model.getFeatureName(), model.getScore());
    }

    static SaliencyResponse saliencyModelToResponse(Saliency model) {
        if (model == null) {
            return null;
        }
        return new SaliencyResponse(
                model.getOutcomeId(),
                model.getOutcomeName(),
                model.getFeatureImportance().stream()
                        .map(ExplainabilityApiV1::featureImportanceModelToResponse)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
        );
    }
}
