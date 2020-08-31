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
    @Path("/{executionId}/saliencies")
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
                model.getSaliencies().entrySet().stream()
                        .map(e -> saliencyModelToResponse(e.getKey(), e.getValue()))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
        );
    }

    static FeatureImportanceResponse featureImportanceModelToResponse(FeatureImportance model) {
        if (model == null) {
            return null;
        }
        return new FeatureImportanceResponse(model.getFeatureId(), model.getScore());
    }

    static SaliencyResponse saliencyModelToResponse(String id, Saliency model) {
        if (model == null) {
            return null;
        }
        return new SaliencyResponse(
                id,
                model.getFeatureImportance().stream()
                        .map(ExplainabilityApiV1::featureImportanceModelToResponse)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
        );
    }
}
