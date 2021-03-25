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

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceReadMode;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.process.impl.Sig;
import org.kie.kogito.services.uow.UnitOfWorkExecutor;

public class $Type$Resource {

    @POST
    @Path("/{id}/$taskName$")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response signal(@PathParam("id") final String id, @Context UriInfo uriInfo) {
        return UnitOfWorkExecutor.executeInUnitOfWork(application.unitOfWorkManager(), () -> {
            return process
                .instances()
                .findById(id)
                .map(pi -> {
                    pi.send(Sig.of("$taskNodeName$", java.util.Collections.emptyMap()));
                    java.util.Optional<WorkItem> task =
                            pi
                                .workItems()
                                .stream()
                                .filter(wi -> wi.getName().equals("$taskName$"))
                                .findFirst();
                    if (task.isPresent()) {
                        return Response
                                .created(uriInfo.getAbsolutePathBuilder().path(task.get().getId()).build())
                                .entity(pi.variables().toOutput())
                            .build();
                    }
                    return Response.status(Response.Status.NOT_FOUND).build();
                });
        }).orElseThrow(() -> new NotFoundException());
    }

    @POST
    @Path("/{id}/$taskName$/{taskId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public $Type$Output completeTask(@PathParam("id") final String id,
                                     @PathParam("taskId") final String taskId,
                                     @QueryParam("phase") @DefaultValue("complete") final String phase,
                                     @QueryParam("user") final String user,
                                     @QueryParam("group") final List<String> groups,
                                     final $TaskOutput$ model) {
        return UnitOfWorkExecutor
            .executeInUnitOfWork(
                application.unitOfWorkManager(),
                () -> process
                    .instances()
                    .findById(id)
                    .map(pi -> {
                        pi
                            .transitionWorkItem(
                                taskId,
                                HumanTaskTransition.withModel(phase, model, Policies.of(user, groups)));
                        return pi.variables().toOutput();
                    }))
                    .orElseThrow(() -> new NotFoundException());
    }
    
    
    @PUT
    @Path("/{id}/$taskName$/{taskId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public $TaskOutput$ saveTask(@PathParam("id") final String id,
                                     @PathParam("taskId") final String taskId,
                                     @QueryParam("user") final String user,
                                     @QueryParam("group") final List<String> groups,
                                     final $TaskOutput$ model) {
        return UnitOfWorkExecutor
                .executeInUnitOfWork(
                        application.unitOfWorkManager(),
                        () -> process
                                .instances()
                                .findById(id)
                                .map(pi -> $TaskOutput$.fromMap(pi.updateWorkItem(
                                        taskId,
                                        wi -> HumanTaskHelper.updateContent(wi, model),
                                        Policies.of(user,groups)))))
                .orElseThrow(() -> new NotFoundException());
    }
    
    
    @POST
    @Path("/{id}/$taskName$/{taskId}/phases/{phase}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public $Type$Output taskTransition(
                                       @PathParam("id") final String id,
                                       @PathParam("taskId") final String taskId,
                                       @PathParam("phase") final String phase,
                                       @QueryParam("user") final String user,
                                       @QueryParam("group") final List<String> groups,
                                       final $TaskOutput$ model) {
        return UnitOfWorkExecutor
                .executeInUnitOfWork(
                        application.unitOfWorkManager(),
                        () -> process
                                .instances()
                                .findById(id)
                                .map(pi -> {
                                    pi.transitionWorkItem(
                                            taskId,
                                            HumanTaskTransition.withModel(phase, model, Policies.of(user, groups)));
                                    return pi.variables().toOutput();
                                }))
                                .orElseThrow(() -> new NotFoundException());
    }
    
    

    @GET
    @Path("/{id}/$taskName$/{taskId}")
    @Produces(MediaType.APPLICATION_JSON)
    public $TaskInput$ getTask(@PathParam("id") String id,
                               @PathParam("taskId") String taskId,
                               @QueryParam("user") final String user,
                               @QueryParam("group") final List<String> groups) {
        return process.instances()
                      .findById(id, ProcessInstanceReadMode.READ_ONLY)
                      .map(pi -> $TaskInput$.from(pi.workItem(taskId, Policies.of(user, groups))))
                      .orElseThrow(() -> new NotFoundException());
    }

    @GET
    @Path("$taskName$/schema")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> getSchema() {
        return JsonSchemaUtil.load(this.getClass().getClassLoader(), process.id(), "$taskName$");
    }

    @GET
    @Path("/{id}/$taskName$/{taskId}/schema")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> getSchemaAndPhases(@PathParam("id") final String id,
                                                  @PathParam("taskId") final String taskId,
                                                  @QueryParam("user") final String user,
                                                  @QueryParam("group") final List<String> groups) {
        return JsonSchemaUtil
            .addPhases(
                process,
                application,
                id,
                taskId,
                Policies.of(user, groups),
                JsonSchemaUtil.load(this.getClass().getClassLoader(), process.id(), "$taskName$"));
    }

    @DELETE
    @Path("/{id}/$taskName$/{taskId}")
    @Produces(MediaType.APPLICATION_JSON)
    public $Type$Output abortTask(@PathParam("id") final String id,
                                  @PathParam("taskId") final String taskId,
                                  @QueryParam("phase") @DefaultValue("abort") final String phase,
                                  @QueryParam("user") final String user,
                                  @QueryParam("group") final List<String> groups) {
        return UnitOfWorkExecutor.executeInUnitOfWork(application.unitOfWorkManager(),
                                                      () -> process
                                                                   .instances()
                                                                   .findById(id)
                                                                   .map(pi -> {
                                                                       pi.transitionWorkItem(taskId,
                                                                                             HumanTaskTransition.withoutModel(phase,
                                                                                                     Policies.of(user, groups)));
                                                                       return pi.variables().toOutput();
                                                                   }))
                                                                   .orElseThrow(() -> new NotFoundException());
    }
    
    @POST
    @Path("/{id}/$taskName$/{taskId}/comments")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addComment(@PathParam("id") final String id,
                               @PathParam("taskId") final String taskId,
                               @QueryParam("user") final String user,
                               @QueryParam("group") final List<String> groups,
                               String commentInfo,
                               @Context UriInfo uriInfo) {
        return UnitOfWorkExecutor
                .executeInUnitOfWork(
                        application.unitOfWorkManager(),
                        () -> process
                                .instances()
                                .findById(id)
                                .map(pi -> {
                                    Comment comment = pi.updateWorkItem(
                                            taskId,
                                            wi -> HumanTaskHelper.addComment(wi, commentInfo, user),
                                            Policies.of(user, groups));
                                    return Response.created(uriInfo.getAbsolutePathBuilder().path(comment.getId()
                                            .toString())
                                            .build()).entity(comment).build();
                                })
                                .orElseThrow(() -> new NotFoundException()));
    }

    @PUT
    @Path("/{id}/$taskName$/{taskId}/comments/{commentId}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Comment updateComment(@PathParam("id") final String id,
                                 @PathParam("taskId") final String taskId,
                                 @PathParam("commentId") final String commentId,
                                 @QueryParam("user") final String user,
                                 @QueryParam("group") final List<String> groups,
                                 String comment) {
        return UnitOfWorkExecutor
                .executeInUnitOfWork(
                        application.unitOfWorkManager(),
                        () -> process
                                .instances()
                                .findById(id)
                                .map(pi -> pi.updateWorkItem(
                                        taskId,
                                        wi -> HumanTaskHelper.updateComment(wi, commentId, comment, user),
                                        Policies.of(user, groups)))
                                .orElseThrow(() -> new NotFoundException()));
    }

    @DELETE
    @Path("/{id}/$taskName$/{taskId}/comments/{commentId}")
    public Response deleteComment(@PathParam("id") final String id,
                                  @PathParam("taskId") final String taskId,
                                  @PathParam("commentId") final String commentId,
                                  @QueryParam("user") final String user,
                                  @QueryParam("group") final List<String> groups) {
        return UnitOfWorkExecutor
                .executeInUnitOfWork(
                        application.unitOfWorkManager(),
                        () -> process
                                .instances()
                                .findById(id)
                                .map(pi -> {
                                    boolean removed = pi.updateWorkItem(
                                            taskId,
                                            wi -> HumanTaskHelper.deleteComment(wi, commentId, user),
                                            Policies.of(user, groups));
                                    return (removed ? Response.ok() : Response.status(Status.NOT_FOUND)).build();
                                })
                                .orElseThrow(() -> new NotFoundException()));
    }

    @POST
    @Path("/{id}/$taskName$/{taskId}/attachments")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAttachment(@PathParam("id") final String id,
                                  @PathParam("taskId") final String taskId,
                                  @QueryParam("user") final String user,
                                  @QueryParam("group") final List<String> groups,
                                  AttachmentInfo attachmentInfo,
                                  @Context UriInfo uriInfo) {
        return UnitOfWorkExecutor
                .executeInUnitOfWork(
                        application.unitOfWorkManager(),
                        () -> process
                                .instances()
                                .findById(id)
                                .map(pi -> {
                                    Attachment attachment = pi.updateWorkItem(
                                            taskId,
                                            wi -> HumanTaskHelper.addAttachment(wi, attachmentInfo, user),
                                            Policies.of(user, groups));
                                    return Response.created(uriInfo.getAbsolutePathBuilder().path(attachment.getId()
                                            .toString())
                                            .build()).entity(attachment).build();
                                })
                                .orElseThrow(() -> new NotFoundException()));
    }

    @PUT
    @Path("/{id}/$taskName$/{taskId}/attachments/{attachmentId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Attachment updateAttachment(@PathParam("id") final String id,
                                       @PathParam("taskId") final String taskId,
                                       @PathParam("attachmentId") final String attachmentId,
                                       @QueryParam("user") final String user,
                                       @QueryParam("group") final List<String> groups,
                                       AttachmentInfo attachment) {
        return UnitOfWorkExecutor
                .executeInUnitOfWork(
                        application.unitOfWorkManager(),
                        () -> process
                                .instances()
                                .findById(id)
                                .map(pi -> pi.updateWorkItem(
                                        taskId,
                                        wi -> HumanTaskHelper.updateAttachment(wi, attachmentId, attachment, user),
                                        Policies.of(user, groups)))
                                .orElseThrow(() -> new NotFoundException()));
    }

    @DELETE
    @Path("/{id}/$taskName$/{taskId}/attachments/{attachmentId}")
    public Response deleteAttachment(@PathParam("id") final String id,
                                     @PathParam("taskId") final String taskId,
                                     @PathParam("attachmentId") final String attachmentId,
                                     @QueryParam("user") final String user,
                                     @QueryParam("group") final List<String> groups) {
        return UnitOfWorkExecutor
                .executeInUnitOfWork(
                        application.unitOfWorkManager(),
                        () -> process
                                .instances()
                                .findById(id)
                                .map(pi -> {
                                    boolean removed = pi.updateWorkItem(
                                            taskId,
                                            wi -> HumanTaskHelper.deleteAttachment(wi, attachmentId, user),
                                            Policies.of(user, groups));
                                    return (removed ? Response.ok() : Response.status(Status.NOT_FOUND)).build();
                                })
                                .orElseThrow(() -> new NotFoundException()));
    }

    @GET
    @Path("/{id}/$taskName$/{taskId}/attachments/{attachmentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Attachment getAttachment(@PathParam("id") final String id,
                                    @PathParam("taskId") final String taskId,
                                    @PathParam("attachmentId") final String attachmentId,
                                    @QueryParam("user") final String user,
                                    @QueryParam("group") final List<String> groups) {
        Attachment attachment = HumanTaskHelper.findTask(process.instances().findById(id).orElseThrow(
                () -> new NotFoundException()), taskId, Policies.of(user, groups))
                .getAttachments().get(attachmentId);
        if (attachment == null) {
            throw new NotFoundException("Attachment " + attachmentId + " not found");
        }
        return attachment;
    }

    @GET
    @Path("/{id}/$taskName$/{taskId}/attachments")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Attachment> getAttachments(@PathParam("id") final String id,
                                                 @PathParam("taskId") final String taskId,
                                                 @QueryParam("user") final String user,
                                                 @QueryParam("group") final List<String> groups) {
        return HumanTaskHelper.findTask(process.instances().findById(id).orElseThrow(() -> new NotFoundException()),
                taskId, Policies.of(user, groups))
                .getAttachments().values();
    }

    @GET
    @Path("/{id}/$taskName$/{taskId}/comments/{commentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Comment getComment(@PathParam("id") final String id,
                              @PathParam("taskId") final String taskId,
                              @PathParam("commentId") final String commentId,
                              @QueryParam("user") final String user,
                              @QueryParam("group") final List<String> groups) {
        Comment comment = HumanTaskHelper.findTask(process.instances().findById(id).orElseThrow(
                () -> new NotFoundException()), taskId, Policies.of(user, groups))
                .getComments().get(commentId);
        if (comment == null) {
            throw new NotFoundException("Comment " + commentId + " not found");
        }
        return comment;
    }

    @GET
    @Path("/{id}/$taskName$/{taskId}/comments")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Comment> getComments(@PathParam("id") final String id,
                                           @PathParam("taskId") final String taskId,
                                           @QueryParam("user") final String user,
                                           @QueryParam("group") final List<String> groups) {
        return HumanTaskHelper.findTask(process.instances().findById(id).orElseThrow(() -> new NotFoundException()),
                taskId, Policies.of(user, groups))
                .getComments().values();
    }
}
