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

@Path("v1/models")
public class ModelsApiV1 {

    @Inject
    TrustyService trustyService;

    /**
     * Gets a model definition by ID.
     * @param modelId The model ID. See {@code ModeIdCreator} for details of the identifier.
     * @return The model definition.
     */
    @GET
    @Path("/{modelId}")
    @APIResponses(value = {
            @APIResponse(description = "Gets the model definition.", responseCode = "200", content = @Content(mediaType = MediaType.TEXT_PLAIN, schema = @Schema(type = SchemaType.STRING))),
            @APIResponse(description = "Bad Request", responseCode = "400", content = @Content(mediaType = MediaType.TEXT_PLAIN))
    }
    )
    @Operation(summary = "Gets The model definition.", description = "Gets the model definition.")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getModelById(
            @Parameter(
                    name = "modelId",
                    description = "The model ID.",
                    required = true,
                    schema = @Schema(implementation = String.class)
            ) @PathParam("modelId") String modelId) {
        return handleModelRequest(modelId);
    }

    private Response handleModelRequest(String modelId) {
        return retrieveModel(modelId)
                .map(definition -> Response.ok(definition).build())
                .orElseGet(() -> Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build());
    }

    private Optional<String> retrieveModel(String modelId) {
        try {
            return Optional.of(trustyService.getModelById(modelId));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }
}