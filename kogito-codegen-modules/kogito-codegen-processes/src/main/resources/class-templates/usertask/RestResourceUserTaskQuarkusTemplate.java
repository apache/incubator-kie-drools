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
package com.myspace.demo;

import java.util.Map;
import java.util.List;
import java.util.Collection;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;

import org.kie.kogito.auth.IdentityProviders;
import org.kie.kogito.auth.SecurityPolicy;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceReadMode;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.process.impl.Sig;
import org.kie.kogito.services.uow.UnitOfWorkExecutor;
import org.kie.kogito.usertask.UserTaskInstanceNotFoundException;
import org.kie.kogito.usertask.UserTaskService;
import org.kie.kogito.usertask.view.UserTaskView;
import org.kie.kogito.usertask.view.UserTaskTransitionView;

import org.kie.kogito.usertask.model.*;

import jakarta.inject.Inject;

@Path("/usertasks/instance")
public class UserTasksResource {

    @Inject
    UserTaskService userTaskService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<UserTaskView> list(@QueryParam("user") String user, @QueryParam("group") List<String> groups) {
        return userTaskService.list(IdentityProviders.of(user, groups));
    }

    @GET
    @Path("/{taskId}")
    @Produces(MediaType.APPLICATION_JSON)
    public UserTaskView find(@PathParam("taskId") String taskId, @QueryParam("user") String user, @QueryParam("group") List<String> groups) {
        return userTaskService.getUserTaskInstance(taskId, IdentityProviders.of(user, groups)).orElseThrow(UserTaskInstanceNotFoundException::new);
    }

    @POST
    @Path("/{taskId}/transition")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public UserTaskView transition(
            @PathParam("taskId") String taskId,
            @QueryParam("user") String user,
            @QueryParam("group") List<String> groups, 
            TransitionInfo transitionInfo) {
        return userTaskService.transition(taskId, transitionInfo.getTransitionId(), transitionInfo.getData(), IdentityProviders.of(user, groups)).orElseThrow(UserTaskInstanceNotFoundException::new);
    }

    @GET
    @Path("/{taskId}/transition")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<UserTaskTransitionView> transition(
            @PathParam("taskId") String taskId,
            @QueryParam("user") String user,
            @QueryParam("group") List<String> groups) {
        return userTaskService.allowedTransitions(taskId, IdentityProviders.of(user, groups));
    }

    @PUT
    @Path("/{taskId}/outputs")
    @Consumes(MediaType.APPLICATION_JSON)
    public UserTaskView setOutput(
            @PathParam("taskId") String taskId,
            @QueryParam("user") String user,
            @QueryParam("group") List<String> groups,
            Map<String, Object> data) {
        return userTaskService.setOutputs(taskId, data, IdentityProviders.of(user, groups)).orElseThrow(UserTaskInstanceNotFoundException::new);
    }

    @PUT
    @Path("/{taskId}/inputs")
    @Consumes(MediaType.APPLICATION_JSON)
    public UserTaskView setOutput(@PathParam("id") String id,
            @PathParam("taskId") String taskId,
            @QueryParam("user") String user,
            @QueryParam("group") List<String> groups,
            Map<String, Object> data) {
        return userTaskService.setInputs(taskId, data, IdentityProviders.of(user, groups)).orElseThrow(UserTaskInstanceNotFoundException::new);
    }

    @GET
    @Path("/{taskId}/comments")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Comment> getComments(
            @PathParam("taskId") String taskId,
            @QueryParam("user") String user,
            @QueryParam("group") List<String> groups) {
        return userTaskService.getComments(taskId, IdentityProviders.of(user, groups));
    }

    @POST
    @Path("/{taskId}/comments")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Comment addComment(
            @PathParam("taskId") String taskId,
            @QueryParam("user") String user,
            @QueryParam("group") List<String> groups,
            CommentInfo commentInfo) {
        Comment comment = new Comment(null, user);
        comment.setContent(commentInfo.getComment());
        return userTaskService.addComment(taskId, comment, IdentityProviders.of(user, groups)).orElseThrow(UserTaskInstanceNotFoundException::new);
    }

    @GET
    @Path("/{taskId}/comments/{commentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Comment getComment(
            @PathParam("taskId") String taskId,
            @PathParam("commentId") String commentId,
            @QueryParam("user") String user,
            @QueryParam("group") List<String> groups) {
        return userTaskService.getComment(taskId, commentId, IdentityProviders.of(user, groups))
                .orElseThrow(() -> new UserTaskInstanceNotFoundException("Comment " + commentId + " not found"));
    }

    @PUT
    @Path("/{taskId}/comments/{commentId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Comment updateComment(
            @PathParam("taskId") String taskId,
            @PathParam("commentId") String commentId,
            @QueryParam("user") String user,
            @QueryParam("group") List<String> groups,
            CommentInfo commentInfo) {
        Comment comment = new Comment(commentId, user);
        comment.setContent(commentInfo.getComment());
        return userTaskService.updateComment(taskId, comment, IdentityProviders.of(user, groups))
                .orElseThrow(UserTaskInstanceNotFoundException::new);
    }

    @DELETE
    @Path("/{taskId}/comments/{commentId}")
    public Comment deleteComment(
            @PathParam("taskId") String taskId,
            @PathParam("commentId") String commentId,
            @QueryParam("user") String user,
            @QueryParam("group") List<String> groups) {
        return userTaskService.removeComment(taskId, commentId, IdentityProviders.of(user, groups))
                .orElseThrow(UserTaskInstanceNotFoundException::new);
    }

    @GET
    @Path("/{taskId}/attachments")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Attachment> getAttachments(
            @PathParam("taskId") String taskId,
            @QueryParam("user") String user,
            @QueryParam("group") List<String> groups) {
        return userTaskService.getAttachments(taskId, IdentityProviders.of(user, groups));
    }

    @POST
    @Path("/{taskId}/attachments")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Attachment addAttachment(
            @PathParam("taskId") String taskId,
            @QueryParam("user") String user,
            @QueryParam("group") List<String> groups,
            AttachmentInfo attachmentInfo) {
        Attachment attachment = new Attachment(null, user);
        attachment.setName(attachmentInfo.getName());
        attachment.setContent(attachmentInfo.getUri());
        return userTaskService.addAttachment(taskId, attachment, IdentityProviders.of(user, groups))
                .orElseThrow(UserTaskInstanceNotFoundException::new);
    }

    @PUT
    @Path("/{taskId}/attachments/{attachmentId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Attachment updateAttachment(
            @PathParam("taskId") String taskId,
            @PathParam("attachmentId") String attachmentId,
            @QueryParam("user") String user,
            @QueryParam("group") List<String> groups,
            AttachmentInfo attachmentInfo) {
        Attachment attachment = new Attachment(attachmentId, user);
        attachment.setName(attachmentInfo.getName());
        attachment.setContent(attachmentInfo.getUri());
        return userTaskService.updateAttachment(taskId, attachment, IdentityProviders.of(user, groups))
                .orElseThrow(UserTaskInstanceNotFoundException::new);
    }

    @DELETE
    @Path("/{taskId}/attachments/{attachmentId}")
    public Attachment deleteAttachment(
            @PathParam("taskId") String taskId,
            @PathParam("attachmentId") String attachmentId,
            @QueryParam("user") String user,
            @QueryParam("group") List<String> groups) {
        return userTaskService.removeAttachment(taskId, attachmentId, IdentityProviders.of(user, groups))
                .orElseThrow(UserTaskInstanceNotFoundException::new);
    }

    @GET
    @Path("/{taskId}/attachments/{attachmentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Attachment getAttachment(
            @PathParam("taskId") String taskId,
            @PathParam("attachmentId") String attachmentId,
            @QueryParam("user") String user,
            @QueryParam("group") List<String> groups) {
        return userTaskService.getAttachment(taskId, attachmentId, IdentityProviders.of(user, groups))
                .orElseThrow(() -> new UserTaskInstanceNotFoundException("Attachment " + attachmentId + " not found"));
    }

}
