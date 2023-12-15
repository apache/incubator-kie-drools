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
package org.kie.kogito.runtime.tools.quarkus.extension.runtime.forms;

import java.io.FileNotFoundException;

import org.kie.kogito.runtime.tools.quarkus.extension.runtime.forms.model.FormContent;
import org.kie.kogito.runtime.tools.quarkus.extension.runtime.forms.model.FormFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

@Path("/forms")
public class FormsService {

    private FormsStorage storage;

    private static final Logger LOGGER = LoggerFactory.getLogger(FormsService.class);

    @Inject
    public FormsService(FormsStorage storage) {
        this.storage = storage;
    }

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFormsList(@QueryParam("names") FormFilter filter) {
        try {
            return Response.ok(storage.getFormInfoList(filter)).build();
        } catch (Exception e) {
            LOGGER.warn("Error while getting forms list: ", e);
            return Response.status(INTERNAL_SERVER_ERROR.getStatusCode(), "Unexpected error while getting forms list: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/count")
    @Produces(MediaType.TEXT_PLAIN)
    public Response formsCount() {
        try {
            return Response.ok(storage.getFormsCount()).build();
        } catch (Exception e) {
            LOGGER.error("Error while getting forms count: ", e);
            return Response.status(INTERNAL_SERVER_ERROR.getStatusCode(), "Unexpected error while getting forms count: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/{formName:\\S+}/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFormContent(@PathParam("formName") String formName) {
        try {
            Response.ResponseBuilder responseBuilder = Response.ok(storage.getFormContent(formName));
            return responseBuilder.build();
        } catch (FileNotFoundException fe) {
            return Response.status(INTERNAL_SERVER_ERROR.getStatusCode(), fe.getMessage()).build();
        } catch (Exception e) {
            LOGGER.warn("Coudln't find form '" + formName + "'");
            return Response.status(INTERNAL_SERVER_ERROR.getStatusCode(), "Unexpected error while getting form content: " + e.getMessage()).build();
        }
    }

    @POST
    @Path("/{formName:\\S+}/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateFormContent(@PathParam("formName") String formName, FormContent formContent) {
        try {
            storage.updateFormContent(formName, formContent);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.status(INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage()).build();
        }
    }
}
