/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package com.myspace.demo;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.jbpm.util.JsonSchemaUtil;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.process.ProcessService;
import org.kie.kogito.process.workitem.Attachment;
import org.kie.kogito.process.workitem.AttachmentInfo;
import org.kie.kogito.process.workitem.Comment;
import org.kie.kogito.process.workitem.Policies;
import org.kie.kogito.process.workitem.TaskModel;
import org.kie.kogito.auth.IdentityProvider;
import org.kie.kogito.auth.IdentityProviders;
import org.kie.kogito.auth.SecurityPolicy;

@Path("/$name$")
public class $Type$ReactiveResource {

    Process<$Type$> process;

    Application application;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public CompletionStage<$Type$Output> createResource_$name$(@Context HttpHeaders httpHeaders,
                                                               @QueryParam("businessKey") String businessKey,
                                                               $Type$Input resource) {
        return CompletableFuture
                .supplyAsync(
                        () -> {
                            ProcessInstance<$Type$> pi = processService.createProcessInstance(process,
                                                                                              businessKey,
                                                                                              Optional.ofNullable(resource).orElse(new $Type$Input()).toModel(),
                                                                                              httpHeaders.getHeaderString("X-KOGITO-StartFromNode"));
                            return Response.created(uriInfo.getAbsolutePathBuilder().path(pi.id()).build())
                                    .entity(pi.checkError().variables().toModel())
                                    .build();
                        });
    }

    @GET()
    @Produces(MediaType.APPLICATION_JSON)
    public CompletionStage<List<$Type$Output>> getResources_$name$() {
        return CompletableFuture.supplyAsync(() -> processService.getProcessInstanceOutput(process));
    }

    @GET()
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public CompletionStage<$Type$Output> getResource_$name$(@PathParam("id") String id) {
        return CompletableFuture.supplyAsync(() -> processService.findById(process, id).orElseThrow(NotFoundException::new));
    }

    @DELETE()
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public CompletionStage<$Type$Output> deleteResource_$name$(@PathParam("id") final String id) {
        return CompletableFuture.supplyAsync(() -> processService.delete(process, id).orElseThrow(NotFoundException::new));
    }

    @PUT()
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public CompletionStage<$Type$Output> updateModel_$name$(@PathParam("id") String id, $Type$ resource) {
        return CompletableFuture.supplyAsync(() -> processService.update(process, id, resource).orElseThrow(NotFoundException::new));
    }

    @GET()
    @Path("/{id}/tasks")
    @Produces(MediaType.APPLICATION_JSON)
    public CompletionStage<List<TaskModel>> getTasks_$name$(@PathParam("id") String id,
                                                           @QueryParam("user") final String user,
                                                           @QueryParam("group") final List<String> groups) {
        return CompletableFuture.supplyAsync(
                () -> processService.getTasks(process, id, user, groups)
                        .orElseThrow(NotFoundException::new)
                        .stream()
                        .map($TaskModelFactory$::from)
                        .collect(Collectors.toList()));
    }
}