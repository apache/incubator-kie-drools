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
package org.kie.kogito.explainability.rest;

import java.util.concurrent.CompletionStage;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.kie.kogito.explainability.ExplanationService;
import org.kie.kogito.explainability.api.BaseExplainabilityRequest;
import org.kie.kogito.explainability.handlers.LocalExplainerServiceHandlerRegistry;

import io.smallrye.mutiny.Uni;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/v1")
public class ExplainabilityApiV1 {

    protected ExplanationService explanationService;
    protected LocalExplainerServiceHandlerRegistry explainerServiceHandlerRegistry;

    @Inject
    public ExplainabilityApiV1(
            ExplanationService explanationService,
            LocalExplainerServiceHandlerRegistry explainerServiceHandlerRegistry) {
        this.explanationService = explanationService;
        this.explainerServiceHandlerRegistry = explainerServiceHandlerRegistry;
    }

    @POST
    @Path("/explain")
    @APIResponses(value = {
            @APIResponse(description = "Retrieve the explainability for a given decision.", responseCode = "200",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(type = SchemaType.OBJECT, implementation = BaseExplainabilityRequest.class))),
            @APIResponse(description = "Bad Request", responseCode = "400", content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    @Operation(summary = "Retrieve the explainability for a given decision.", description = "Retrieve the explainability for a given decision.")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> explain(@Valid BaseExplainabilityRequest request) {
        CompletionStage<Response> result = explanationService.explainAsync(request)
                .thenApply(x -> Response.ok(x).build());

        return Uni.createFrom().completionStage(result);
    }

}
