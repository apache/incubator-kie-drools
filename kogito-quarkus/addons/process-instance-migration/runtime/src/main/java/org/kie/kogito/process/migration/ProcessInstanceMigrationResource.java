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
package org.kie.kogito.process.migration;

import org.kie.kogito.Application;
import org.kie.kogito.process.Processes;

import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/management/processes/")
public class ProcessInstanceMigrationResource extends BaseProcessInstanceMigrationResource<Response> {

    //CDI
    public ProcessInstanceMigrationResource() {
        this(null, null);
    }

    @Inject
    public ProcessInstanceMigrationResource(Instance<Processes> processes, Application application) {
        super(processes::get, application);
    }

    @Override
    protected <R> Response buildOkResponse(R body) {
        return Response
                .status(Response.Status.OK)
                .entity(body)
                .build();
    }

    @Override
    protected Response badRequestResponse(String message) {
        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(message)
                .build();
    }

    @Override
    protected Response notFoundResponse(String message) {
        return Response
                .status(Response.Status.NOT_FOUND)
                .entity(message)
                .build();
    }

    @Override
    @POST
    @Path("{processId}/instances/{processInstanceId}/migrate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response migrateInstance(@PathParam("processId") String processId, @PathParam("processInstanceId") String processInstanceId, ProcessMigrationSpec migrationSpec) {
        return doMigrateInstance(processId, migrationSpec, processInstanceId);
    }

    @Override
    @POST
    @Path("{processId}/migrate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response migrateAllInstances(@PathParam("processId") String processId, ProcessMigrationSpec migrationSpec) {
        return doMigrateAllInstances(processId, migrationSpec);
    }
}
