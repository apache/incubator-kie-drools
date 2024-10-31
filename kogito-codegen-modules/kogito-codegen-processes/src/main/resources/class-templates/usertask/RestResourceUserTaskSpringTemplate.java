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

import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.util.Collection;

import org.jbpm.util.JsonSchemaUtil;
import org.kie.kogito.auth.IdentityProviders;
import org.kie.kogito.auth.SecurityPolicy;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.process.impl.Sig;
import org.kie.kogito.services.uow.UnitOfWorkExecutor;
import org.kie.kogito.usertask.UserTaskService;
import org.kie.kogito.usertask.impl.json.SimpleDeserializationProblemHandler;
import org.kie.kogito.usertask.impl.json.SimplePolymorphicTypeValidator;
import org.kie.kogito.usertask.view.UserTaskTransitionView;
import org.kie.kogito.usertask.view.UserTaskView;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

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

import org.springframework.beans.factory.annotation.Autowired;

import org.kie.kogito.usertask.model.*;

@RestController
@RequestMapping(value = "/usertasks/instance", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class UserTasksResource {

    @Autowired
    UserTaskService userTaskService;

    @Autowired
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

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserTaskView> list(@RequestParam("user") String user, @RequestParam("group") List<String> groups) {
        return userTaskService.list(IdentityProviders.of(user, groups));
    }

    @GetMapping(value = "/{taskId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserTaskView find(@PathVariable("taskId") String taskId, @RequestParam("user") String user, @RequestParam("group") List<String> groups) {
        return userTaskService.getUserTaskInstance(taskId, IdentityProviders.of(user, groups)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping(value = "/{taskId}/transition")
    public UserTaskView transition(
            @PathVariable("taskId") String taskId,
            @RequestParam("user") String user,
            @RequestParam("group") List<String> groups,
            @RequestBody TransitionInfo transitionInfo) {
        return userTaskService.transition(taskId, transitionInfo.getTransitionId(), transitionInfo.getData(), IdentityProviders.of(user, groups))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "/{taskId}/transition", produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<UserTaskTransitionView> transition(
            @PathVariable("taskId") String taskId,
            @RequestParam("user") String user,
            @RequestParam("group") List<String> groups) {
        return userTaskService.allowedTransitions(taskId, IdentityProviders.of(user, groups));
    }

    @PutMapping("/{taskId}/outputs")
    public UserTaskView setOutput(
            @PathVariable("taskId") String taskId,
            @RequestParam("user") String user,
            @RequestParam("group") List<String> groups,
            @RequestBody String body) throws Exception {
        Map<String, Object> data = mapper.readValue(body, Map.class);
        return userTaskService.setOutputs(taskId, data, IdentityProviders.of(user, groups)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{taskId}/inputs")
    public UserTaskView setInputs(
            @PathVariable("taskId") String taskId,
            @RequestParam("user") String user,
            @RequestParam("group") List<String> groups,
            @RequestBody String body) throws Exception {
        Map<String, Object> data = mapper.readValue(body, Map.class);
        return userTaskService.setInputs(taskId, data, IdentityProviders.of(user, groups)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/{taskId}/comments")
    public Collection<Comment> getComments(
            @PathVariable("taskId") String taskId,
            @RequestParam("user") String user,
            @RequestParam("group") List<String> groups) {
        return userTaskService.getComments(taskId, IdentityProviders.of(user, groups));
    }

    @PostMapping("/{taskId}/comments")
    public Comment addComment(
            @PathVariable("taskId") String taskId,
            @RequestParam("user") String user,
            @RequestParam("group") List<String> groups,
            @RequestBody CommentInfo commentInfo) {
        Comment comment = new Comment(null, user);
        comment.setContent(commentInfo.getComment());
        return userTaskService.addComment(taskId, comment, IdentityProviders.of(user, groups)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/{taskId}/comments/{commentId}")
    public Comment getComment(
            @PathVariable("taskId") String taskId,
            @PathVariable("commentId") String commentId,
            @RequestParam("user") String user,
            @RequestParam("group") List<String> groups) {
        return userTaskService.getComment(taskId, commentId, IdentityProviders.of(user, groups))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment " + commentId + " not found"));
    }

    @PutMapping("/{taskId}/comments/{commentId}")
    public Comment updateComment(
            @PathVariable("taskId") String taskId,
            @PathVariable("commentId") String commentId,
            @RequestParam("user") String user,
            @RequestParam("group") List<String> groups,
            @RequestBody CommentInfo commentInfo) {
        Comment comment = new Comment(commentId, user);
        comment.setContent(commentInfo.getComment());
        return userTaskService.updateComment(taskId, comment, IdentityProviders.of(user, groups)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping(value = "/{taskId}/comments/{commentId}", consumes = MediaType.ALL_VALUE)
    public Comment deleteComment(
            @PathVariable("taskId") String taskId,
            @PathVariable("commentId") String commentId,
            @RequestParam("user") String user,
            @RequestParam("group") List<String> groups) {
        return userTaskService.removeComment(taskId, commentId, IdentityProviders.of(user, groups)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/{taskId}/attachments")
    public Collection<Attachment> getAttachments(
            @PathVariable("taskId") String taskId,
            @RequestParam("user") String user,
            @RequestParam("group") List<String> groups) {
        return userTaskService.getAttachments(taskId, IdentityProviders.of(user, groups));
    }

    @PostMapping("/{taskId}/attachments")
    public Attachment addAttachment(
            @PathVariable("taskId") String taskId,
            @RequestParam("user") String user,
            @RequestParam("group") List<String> groups,
            @RequestBody AttachmentInfo attachmentInfo) {
        Attachment attachment = new Attachment(null, user);
        attachment.setName(attachmentInfo.getName());
        attachment.setContent(attachmentInfo.getUri());
        return userTaskService.addAttachment(taskId, attachment, IdentityProviders.of(user, groups)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{taskId}/attachments/{attachmentId}")
    public Attachment updateAttachment(
            @PathVariable("taskId") String taskId,
            @PathVariable("attachmentId") String attachmentId,
            @RequestParam("user") String user,
            @RequestParam("group") List<String> groups,
            @RequestBody AttachmentInfo attachmentInfo) {
        Attachment attachment = new Attachment(attachmentId, user);
        attachment.setName(attachmentInfo.getName());
        attachment.setContent(attachmentInfo.getUri());
        return userTaskService.updateAttachment(taskId, attachment, IdentityProviders.of(user, groups))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping(value = "/{taskId}/attachments/{attachmentId}", consumes = MediaType.ALL_VALUE)
    public Attachment deleteAttachment(
            @PathVariable("taskId") String taskId,
            @PathVariable("attachmentId") String attachmentId,
            @RequestParam("user") String user,
            @RequestParam("group") List<String> groups) {
        return userTaskService.removeAttachment(taskId, attachmentId, IdentityProviders.of(user, groups)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/{taskId}/attachments/{attachmentId}")
    public Attachment getAttachment(
            @PathVariable("taskId") String taskId,
            @PathVariable("attachmentId") String attachmentId,
            @RequestParam("user") String user,
            @RequestParam("group") List<String> groups) {
        return userTaskService.getAttachment(taskId, attachmentId, IdentityProviders.of(user, groups))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attachment " + attachmentId + " not found"));
    }

}