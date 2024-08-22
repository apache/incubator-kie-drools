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
package org.kie.kogito.process.dynamic;

import java.util.Collection;
import java.util.stream.Collectors;

import org.kie.kogito.process.Process;
import org.kogito.workitem.rest.RestWorkItemHandler;

import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/_dynamic")
public class ProcessInstanceDynamicCallsResource {

    @Inject
    Vertx vertx;
    @Inject
    WebClientOptions sslOptions;
    private RestWorkItemHandler handler;
    private Collection<Process<?>> processes;

    @Inject
    ProcessInstanceDynamicCallsResource(Instance<Process<?>> processes) {
        this.processes = processes.stream().collect(Collectors.toUnmodifiableList());
    }

    @PostConstruct
    void init() {
        handler = new RestWorkItemHandler(WebClient.create(vertx), WebClient.create(vertx, sslOptions));
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{processId}/{processInstanceId}/rest")
    public Response executeRestCall(@PathParam("processId") String processId, @PathParam("processInstanceId") String processInstanceId, RestCallInfo input) {
        ProcessInstanceDynamicCallHelper.executeRestCall(handler, processes, processId, processInstanceId, input);
        return Response.status(200).build();
    }
}
