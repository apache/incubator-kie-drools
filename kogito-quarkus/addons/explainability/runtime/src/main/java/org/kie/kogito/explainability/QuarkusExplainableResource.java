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
package org.kie.kogito.explainability;

import java.util.List;

import org.kie.kogito.Application;
import org.kie.kogito.explainability.model.PredictInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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
