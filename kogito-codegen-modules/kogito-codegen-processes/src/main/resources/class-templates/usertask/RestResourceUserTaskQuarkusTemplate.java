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
import java.io.IOException;
import java.util.Collection;

import jakarta.inject.Inject;

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

import org.kie.kogito.auth.IdentityProviderFactory;
import org.kie.kogito.usertask.UserTaskInstanceNotFoundException;
import org.kie.kogito.usertask.UserTaskService;
import org.kie.kogito.usertask.impl.json.SimpleDeserializationProblemHandler;
import org.kie.kogito.usertask.impl.json.SimplePolymorphicTypeValidator;
import org.kie.kogito.usertask.view.UserTaskView;
import org.kie.kogito.usertask.view.UserTaskTransitionView;

import org.kie.kogito.usertask.model.*;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator.Validity;
import com.fasterxml.jackson.databind.module.SimpleModule;

@Path("/usertasks/instance")
public class UserTasksResource {

    @Inject
    UserTaskService userTaskService;

    @Inject
    IdentityProviderFactory identityProviderFactory;

    @Inject
    ObjectMapper objectMapper;

    ObjectMapper mapper;

    @jakarta.annotation.PostConstruct
    public void init() {
        mapper = objectMapper.copy();
        SimpleModule module = new SimpleModule();
        mapper.addHandler(new SimpleDeserializationProblemHandler());
        mapper.registerModule(module);
        mapper.activateDefaultTypingAsProperty(new SimplePolymorphicTypeValidator(), DefaultTyping.NON_FINAL, "@type");
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<UserTaskView> list(@QueryParam("user") String user, @QueryParam("group") List<String> groups) {
        return userTaskService.list(identityProviderFactory.getOrImpersonateIdentity(user, groups));
    }

    @GET
    @Path("/{taskId}")
    @Produces(MediaType.APPLICATION_JSON)
    public UserTaskView find(@PathParam("taskId") String taskId, @QueryParam("user") String user, @QueryParam("group") List<String> groups) {
        return userTaskService.getUserTaskInstance(taskId, identityProviderFactory.getOrImpersonateIdentity(user, groups)).orElseThrow(UserTaskInstanceNotFoundException::new);
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
        return userTaskService.transition(taskId, transitionInfo.getTransitionId(), transitionInfo.getData(), identityProviderFactory.getOrImpersonateIdentity(user, groups)).orElseThrow(UserTaskInstanceNotFoundException::new);
    }

    @GET
    @Path("/{taskId}/transition")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<UserTaskTransitionView> transition(
            @PathParam("taskId") String taskId,
            @QueryParam("user") String user,
            @QueryParam("group") List<String> groups) {
        return userTaskService.allowedTransitions(taskId, identityProviderFactory.getOrImpersonateIdentity(user, groups));
    }

    @PUT
    @Path("/{taskId}/outputs")
    @Consumes(MediaType.APPLICATION_JSON)
    public UserTaskView setOutput(
            @PathParam("taskId") String taskId,
            @QueryParam("user") String user,
            @QueryParam("group") List<String> groups,
            String body) throws IOException {
        Map<String, Object> data = mapper.readValue(body, Map.class);
        return userTaskService.setOutputs(taskId, data, identityProviderFactory.getOrImpersonateIdentity(user, groups)).orElseThrow(UserTaskInstanceNotFoundException::new);
    }

    @PUT
    @Path("/{taskId}/inputs")
    @Consumes(MediaType.APPLICATION_JSON)
    public UserTaskView setInputs(
            @PathParam("taskId") String taskId,
            @QueryParam("user") String user,
            @QueryParam("group") List<String> groups,
            String body) throws IOException {
        Map<String, Object> data = mapper.readValue(body, Map.class);
        return userTaskService.setInputs(taskId, data, identityProviderFactory.getOrImpersonateIdentity(user, groups)).orElseThrow(UserTaskInstanceNotFoundException::new);
    }

    @GET
    @Path("/{taskId}/comments")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Comment> getComments(
            @PathParam("taskId") String taskId,
            @QueryParam("user") String user,
            @QueryParam("group") List<String> groups) {
        return userTaskService.getComments(taskId, identityProviderFactory.getOrImpersonateIdentity(user, groups));
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
        return userTaskService.addComment(taskId, comment, identityProviderFactory.getOrImpersonateIdentity(user, groups)).orElseThrow(UserTaskInstanceNotFoundException::new);
    }

    @GET
    @Path("/{taskId}/comments/{commentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Comment getComment(
            @PathParam("taskId") String taskId,
            @PathParam("commentId") String commentId,
            @QueryParam("user") String user,
            @QueryParam("group") List<String> groups) {
        return userTaskService.getComment(taskId, commentId, identityProviderFactory.getOrImpersonateIdentity(user, groups))
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
        return userTaskService.updateComment(taskId, comment, identityProviderFactory.getOrImpersonateIdentity(user, groups))
                .orElseThrow(UserTaskInstanceNotFoundException::new);
    }

    @DELETE
    @Path("/{taskId}/comments/{commentId}")
    public Comment deleteComment(
            @PathParam("taskId") String taskId,
            @PathParam("commentId") String commentId,
            @QueryParam("user") String user,
            @QueryParam("group") List<String> groups) {
        return userTaskService.removeComment(taskId, commentId, identityProviderFactory.getOrImpersonateIdentity(user, groups))
                .orElseThrow(UserTaskInstanceNotFoundException::new);
    }

    @GET
    @Path("/{taskId}/attachments")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Attachment> getAttachments(
            @PathParam("taskId") String taskId,
            @QueryParam("user") String user,
            @QueryParam("group") List<String> groups) {
        return userTaskService.getAttachments(taskId, identityProviderFactory.getOrImpersonateIdentity(user, groups));
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
        return userTaskService.addAttachment(taskId, attachment, identityProviderFactory.getOrImpersonateIdentity(user, groups))
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
        return userTaskService.updateAttachment(taskId, attachment, identityProviderFactory.getOrImpersonateIdentity(user, groups))
                .orElseThrow(UserTaskInstanceNotFoundException::new);
    }

    @DELETE
    @Path("/{taskId}/attachments/{attachmentId}")
    public Attachment deleteAttachment(
            @PathParam("taskId") String taskId,
            @PathParam("attachmentId") String attachmentId,
            @QueryParam("user") String user,
            @QueryParam("group") List<String> groups) {
        return userTaskService.removeAttachment(taskId, attachmentId, identityProviderFactory.getOrImpersonateIdentity(user, groups))
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
        return userTaskService.getAttachment(taskId, attachmentId, identityProviderFactory.getOrImpersonateIdentity(user, groups))
                .orElseThrow(() -> new UserTaskInstanceNotFoundException("Attachment " + attachmentId + " not found"));
    }

}
