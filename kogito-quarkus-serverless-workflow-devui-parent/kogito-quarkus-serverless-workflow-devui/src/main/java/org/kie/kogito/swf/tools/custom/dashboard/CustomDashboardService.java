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
package org.kie.kogito.swf.tools.custom.dashboard;

import org.kie.kogito.swf.tools.custom.dashboard.model.CustomDashboardFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

@Path("/customDashboard")
public class CustomDashboardService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomDashboardService.class);

    private CustomDashboardStorage storage;

    @Inject
    public void setStorage(CustomDashboardStorage storage) {
        this.storage = storage;
    }

    @GET
    @Path("/count")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getCustomDashboardFilesCount() {
        try {
            return Response.ok(storage.getCustomDashboardFilesCount()).build();
        } catch (Exception e) {
            LOGGER.warn("Error while getting CustomDashboard file count: ", e);
            return Response.status(INTERNAL_SERVER_ERROR.getStatusCode(), "Unexpected error while getting CustomDashboard files count: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomDashboardFiles(@QueryParam("names") CustomDashboardFilter filter) {
        try {
            return Response.ok(storage.getCustomDashboardFiles(filter)).build();
        } catch (Exception e) {
            LOGGER.warn("Error while getting CustomDashboard list: ", e);
            return Response.status(INTERNAL_SERVER_ERROR.getStatusCode(), "Unexpected error while getting CustomDashboard files list: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/{name:\\S+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomDashboardFileContent(@PathParam("name") String name) {
        try {
            return Response.ok(storage.getCustomDashboardFileContent(name)).build();
        } catch (Exception e) {
            LOGGER.warn("Error while getting CustomDashboard file content: ", e);
            return Response.status(INTERNAL_SERVER_ERROR.getStatusCode(), "Unexpected error while getting CustomDashboard file content: " + e.getMessage()).build();
        }
    }
}
