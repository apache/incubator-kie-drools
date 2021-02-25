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
package org.kie.kogito.explainability;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.kie.kogito.Application;
import org.kie.kogito.explainability.model.PredictInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/predict")
public class QuarkusExplainableResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuarkusExplainableResource.class);
    private static final ExplainabilityService explainabilityService = ExplainabilityService.INSTANCE;

    private final Application application;

    @Inject
    public QuarkusExplainableResource(Application application) {
        this.application = application;
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public Response predict(List<PredictInput> inputs) {
        try {
            return Response.ok(explainabilityService.processRequest(application, inputs)).build();
        } catch (Exception e) {
            LOGGER.warn("An Exception occurred processing the predict request", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}
