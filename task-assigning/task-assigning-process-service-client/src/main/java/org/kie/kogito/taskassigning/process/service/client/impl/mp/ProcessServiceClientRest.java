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
package org.kie.kogito.taskassigning.process.service.client.impl.mp;

import java.io.Closeable;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

public interface ProcessServiceClientRest extends Closeable {

    @POST
    @Path("/{processId}/{processInstanceId}/{taskId}/{workitemId}")
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    ObjectNode transitionTask(@PathParam String processId, @PathParam String processInstanceId, @PathParam String taskId, @PathParam String workitemId,
                              @QueryParam("phase") String phase,
                              @QueryParam("user") String user, @QueryParam("group") List<String> group,
                              String payload);

    @GET
    @Path("/{processId}/{processInstanceId}/{taskId}/{workitemId}/schema")
    @Produces(APPLICATION_JSON)
    TaskSchema getTaskSchema(@PathParam String processId, @PathParam String processInstanceId, @PathParam String taskId, @PathParam String workitemId,
                             @QueryParam("user") String user, @QueryParam("group") List<String> group);
}
