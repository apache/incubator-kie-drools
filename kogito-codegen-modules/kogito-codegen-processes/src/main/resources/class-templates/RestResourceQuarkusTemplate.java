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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PATCH;
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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
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
public class $Type$Resource {

    Process<$Type$> process;

    @Inject
    ProcessService processService;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "$documentation$", description = "$processInstanceDescription$")
    public Response createResource_$name$(@Context HttpHeaders httpHeaders,
                                          @Context UriInfo uriInfo,
                                          @QueryParam("businessKey") @DefaultValue("") String businessKey,
                                          $Type$Input resource) {
        ProcessInstance<$Type$> pi = processService.createProcessInstance(process,
                                                                          businessKey,
                                                                          Optional.ofNullable(resource).orElse(new $Type$Input()).toModel(),
                                                                          httpHeaders.getRequestHeaders(),
                                                                          httpHeaders.getHeaderString("X-KOGITO-StartFromNode"));
        return Response.created(uriInfo.getAbsolutePathBuilder().path(pi.id()).build())
                .entity(pi.checkError().variables().toModel())
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "$documentation$", description = "$processInstanceDescription$")
    public List<$Type$Output> getResources_$name$() {
        return processService.getProcessInstanceOutput(process);
    }

    @GET
    @Path("schema")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "$documentation$", description = "$processInstanceDescription$")
    public Map<String, Object> getResourceSchema_$name$() {
        return JsonSchemaUtil.load(this.getClass().getClassLoader(), process.id());
    }


    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "$documentation$", description = "$processInstanceDescription$")
    public $Type$Output getResource_$name$(@PathParam("id") String id) {
        return processService.findById(process, id).orElseThrow(NotFoundException::new);
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "$documentation$", description = "$processInstanceDescription$")
    public $Type$Output deleteResource_$name$(@PathParam("id") final String id) {
        return processService.delete(process, id).orElseThrow(NotFoundException::new);
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "$documentation$", description = "$processInstanceDescription$")
    public $Type$Output updateModel_$name$(@PathParam("id") String id, $Type$Input resource) {
        return processService.update(process, id, resource.toModel()).orElseThrow(NotFoundException::new);
    }
    
    @PATCH
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "$documentation$", description = "$processInstanceDescription$")
    public $Type$Output updateModelPartial_$name$(@PathParam("id") String id, $Type$Input resource) {
        return processService.updatePartial(process, id, resource.toModel()).orElseThrow(NotFoundException::new);
    }

    @GET
    @Path("/{id}/tasks")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "$documentation$", description = "$processInstanceDescription$")
    public List<TaskModel> getTasks_$name$(@PathParam("id") String id,
                                          @QueryParam("user") final String user,
                                          @QueryParam("group") final List<String> groups) {
        return processService.getTasks(process, id, SecurityPolicy.of(IdentityProviders.of(user, groups)))
                .orElseThrow(NotFoundException::new)
                .stream()
                .map($TaskModelFactory$::from)
                .collect(Collectors.toList());
    }
}