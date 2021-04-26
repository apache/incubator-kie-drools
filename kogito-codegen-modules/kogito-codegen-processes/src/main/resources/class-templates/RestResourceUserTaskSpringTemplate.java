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

import org.jbpm.process.instance.impl.humantask.HumanTaskHelper;
import org.jbpm.util.JsonSchemaUtil;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.process.impl.Sig;
import org.kie.kogito.process.workitem.Comment;
import org.kie.kogito.process.workitem.Policies;
import org.kie.kogito.process.workitem.TaskMetaInfo;
import org.kie.kogito.services.uow.UnitOfWorkExecutor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

public class $Type$Resource {

    @PostMapping(value = "/{id}/$taskName$", produces = MediaType.APPLICATION_JSON_VALUE,
                 consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<$Type$Output> signal(@PathVariable("id") final String id,
                                               final UriComponentsBuilder uriComponentsBuilder) {
        return UnitOfWorkExecutor
                .executeInUnitOfWork(
                        application.unitOfWorkManager(),
                        () -> process
                                .instances()
                                .findById(id)
                                .map(pi -> {
                                    pi.send(Sig.of("$taskNodeName$", java.util.Collections.emptyMap()));
                                    java.util.Optional<WorkItem> task = pi
                                            .workItems()
                                            .stream()
                                            .filter(wi -> wi.getName().equals("$taskName$"))
                                            .findFirst();
                                    if (task.isPresent()) {
                                        UriComponents uriComponents =
                                                uriComponentsBuilder.path("/$name$/{id}/$taskName$/{taskId}")
                                                        .buildAndExpand(id, task.get().getId());
                                        URI location = uriComponents.toUri();
                                        return ResponseEntity.created(location)
                                                .body(pi.checkError().variables().toOutput());
                                    }
                                    return new ResponseEntity<$Type$Output>(HttpStatus.NOT_FOUND);
                                })
                                .orElseGet(() -> ResponseEntity.notFound().build()));
    }

    @PostMapping(value = "/{id}/$taskName$/{taskId}", produces = MediaType.APPLICATION_JSON_VALUE,
                 consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<$Type$Output> taskTransition(@PathVariable("id") final String id,
                                                       @PathVariable("taskId") final String taskId,
                                                       @RequestParam(value = "phase", required = false,
                                                                     defaultValue = "complete") final String phase,
                                                       @RequestParam(value = "user",
                                                                     required = false) final String user,
                                                       @RequestParam(value = "group",
                                                                     required = false) final List<String> groups,
                                                       @RequestBody(required = false) final $TaskOutput$ model) {
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
                                                    HumanTaskTransition.withModel(phase, model, Policies.of(user,
                                                            groups)));
                                    return ResponseEntity.ok(pi.checkError().variables().toOutput());
                                })
                                .orElseGet(() -> ResponseEntity.notFound().build()));
    }

    @PutMapping(value = "/{id}/$taskName$/{taskId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<$TaskOutput$> saveTask(@PathVariable("id") final String id,
                                   @PathVariable("taskId") final String taskId,
                                   @RequestParam(value = "user", required = false) final String user,
                                   @RequestParam(value = "group", required = false) final List<String> groups,
                                   @RequestBody(required = false) final $TaskOutput$ model) {
        return UnitOfWorkExecutor
                .executeInUnitOfWork(
                        application.unitOfWorkManager(),
                        () -> process
                                .instances()
                                .findById(id)
                                .map(pi -> ResponseEntity.ok($TaskOutput$.fromMap(pi.updateWorkItem(
                                        taskId,
                                        wi -> HumanTaskHelper.updateContent(wi, model),
                                        Policies.of(user, groups))))).orElseGet(() -> ResponseEntity.notFound()
                                                .build()));
    }

    @PostMapping(value = "/{id}/$taskName$/{taskId}/phases/{phase}", produces = MediaType.APPLICATION_JSON_VALUE,
                 consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<$Type$Output> completeTask(@PathVariable("id") final String id,
                                                     @PathVariable("taskId") final String taskId,
                                                     @PathVariable("phase") final String phase,
                                                     @RequestParam("user") final String user,
                                                     @RequestParam("group") final List<String> groups,
                                                     @RequestBody(required = false) final $TaskOutput$ model) {
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
                                    return ResponseEntity.ok(pi.checkError().variables().toOutput());
                                })
                                .orElseGet(() -> ResponseEntity.notFound().build()));
    }

    @PostMapping(value = "/{id}/$taskName$/{taskId}/comments", produces = MediaType.APPLICATION_JSON_VALUE,
                 consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Comment> addComment(@PathVariable("id") final String id,
                                              @PathVariable("taskId") final String taskId,
                                              @RequestParam(value = "user", required = false) final String user,
                                              @RequestParam(value = "group",
                                                            required = false) final List<String> groups,
                                              @RequestBody String commentInfo,
                                              UriComponentsBuilder uriComponentsBuilder) {
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
                                    return ResponseEntity.created(uriComponentsBuilder.path(
                                            "/$name$/{id}/$taskName$/{taskId}/comments/{commentId}").buildAndExpand(id,
                                                    taskId,
                                                    comment.getId()).toUri()).body(comment);
                                })
                                .orElseGet(() -> ResponseEntity.notFound().build()));
    }

    @PutMapping(value = "/{id}/$taskName$/{taskId}/comments/{commentId}", produces = MediaType.APPLICATION_JSON_VALUE,
                consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Comment> updateComment(@PathVariable("id") final String id,
                                                 @PathVariable("taskId") final String taskId,
                                                 @PathVariable("commentId") final String commentId,
                                                 @RequestParam(value = "user", required = false) final String user,
                                                 @RequestParam(value = "group",
                                                               required = false) final List<String> groups,
                                                 @RequestBody String comment) {
        return UnitOfWorkExecutor
                .executeInUnitOfWork(
                        application.unitOfWorkManager(),
                        () -> process
                                .instances()
                                .findById(id)
                                .map(pi -> ResponseEntity.ok(pi.updateWorkItem(
                                        taskId,
                                        wi -> HumanTaskHelper.updateComment(wi, commentId, comment, user),
                                        Policies.of(user, groups))))
                                .orElseGet(() -> ResponseEntity.notFound().build()));
    }

    @DeleteMapping(value = "/{id}/$taskName$/{taskId}/comments/{commentId}")
    public ResponseEntity deleteComment(@PathVariable("id") final String id,
                                        @PathVariable("taskId") final String taskId,
                                        @PathVariable("commentId") final String commentId,
                                        @RequestParam(value = "user", required = false) final String user,
                                        @RequestParam(value = "group", required = false) final List<String> groups) {
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
                                    return removed ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
                                })
                                .orElseGet(() -> ResponseEntity.notFound().build()));
    }

    @PostMapping(value = "/{id}/$taskName$/{taskId}/attachments", produces = MediaType.APPLICATION_JSON_VALUE,
                 consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Attachment> addAttachment(@PathVariable("id") final String id,
                                                    @PathVariable("taskId") final String taskId,
                                                    @RequestParam(value = "user", required = false) final String user,
                                                    @RequestParam(value = "group",
                                                                  required = false) final List<String> groups,
                                                    @RequestBody AttachmentInfo attachmentInfo,
                                                    UriComponentsBuilder uriComponentsBuilder) {
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
                                    return ResponseEntity.created(uriComponentsBuilder.path(
                                            "/$name$/{id}/$taskName$/{taskId}/attachments/{attachmentId}")
                                            .buildAndExpand(id,
                                                    taskId, attachment.getId()).toUri()).body(attachment);
                                })
                                .orElseGet(() -> ResponseEntity.notFound().build()));
    }

    @PutMapping(value = "/{id}/$taskName$/{taskId}/attachments/{attachmentId}",
                produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Attachment> updateAttachment(@PathVariable("id") final String id,
                                                       @PathVariable("taskId") final String taskId,
                                                       @PathVariable("attachmentId") final String attachmentId,
                                                       @RequestParam(value = "user",
                                                                     required = false) final String user,
                                                       @RequestParam(value = "group",
                                                                     required = false) final List<String> groups,
                                                       @RequestBody AttachmentInfo attachment) {
        return UnitOfWorkExecutor
                .executeInUnitOfWork(
                        application.unitOfWorkManager(),
                        () -> process
                                .instances()
                                .findById(id)
                                .map(pi -> ResponseEntity.ok(pi.updateWorkItem(
                                        taskId,
                                        wi -> HumanTaskHelper.updateAttachment(wi, attachmentId, attachment, user),
                                        Policies.of(user, groups))))
                                .orElseGet(() -> ResponseEntity.notFound().build()));
    }

    @DeleteMapping(value = "/{id}/$taskName$/{taskId}/attachments/{attachmentId}")
    public ResponseEntity deleteAttachment(@PathVariable("id") final String id,
                                           @PathVariable("taskId") final String taskId,
                                           @PathVariable("attachmentId") final String attachmentId,
                                           @RequestParam(value = "user", required = false) final String user,
                                           @RequestParam(value = "group", required = false) final List<String> groups) {
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
                                    return (removed ? ResponseEntity.ok() : ResponseEntity.notFound()).build();
                                })
                                .orElseGet(() -> ResponseEntity.notFound().build()));
    }

    @GetMapping(value = "/{id}/$taskName$/{taskId}/attachments/{attachmentId}",
                produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Attachment> getAttachment(@PathVariable("id") final String id,
                                                    @PathVariable("taskId") final String taskId,
                                                    @PathVariable("attachmentId") final String attachmentId,
                                                    @RequestParam(value = "user", required = false) final String user,
                                                    @RequestParam(value = "group",
                                                                  required = false) final List<String> groups) {
        Optional<ProcessInstance<$Type$>> pi = process.instances().findById(id);
        if (pi.isPresent()) {
            Attachment attachment = HumanTaskHelper.findTask(pi.get(), taskId, Policies.of(user, groups))
                    .getAttachments().get(attachmentId);
            if (attachment != null) {
                return ResponseEntity.ok(attachment);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping(value = "/{id}/$taskName$/{taskId}/attachments", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<Attachment>> getAttachments(@PathVariable("id") final String id,
                                                                 @PathVariable("taskId") final String taskId,
                                                                 @RequestParam(value = "user") final String user,
                                                                 @RequestParam(value = "group") final List<String> groups) {
        Optional<ProcessInstance<$Type$>> pi = process.instances().findById(id);
        return pi.isPresent() ? ResponseEntity.ok(HumanTaskHelper.findTask(pi.get(), taskId, Policies.of(user, groups))
                .getAttachments().values()) : ResponseEntity.notFound().build();
    }

    @GetMapping(value = "/{id}/$taskName$/{taskId}/comments/{commentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Comment> getComment(@PathVariable("id") final String id,
                                              @PathVariable("taskId") final String taskId,
                                              @PathVariable("commentId") final String commentId,
                                              @RequestParam(value = "user", required = false) final String user,
                                              @RequestParam(value = "group",
                                                            required = false) final List<String> groups) {
        Optional<ProcessInstance<$Type$>> pi = process.instances().findById(id);
        if (pi.isPresent()) {
            Comment comment = HumanTaskHelper.findTask(pi.get(), taskId, Policies.of(user, groups)).getComments().get(
                    commentId);
            if (comment != null) {
                return ResponseEntity.ok(comment);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping(value = "/{id}/$taskName$/{taskId}/comments", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<Comment>> getComments(@PathVariable("id") final String id,
                                                           @PathVariable("taskId") final String taskId,
                                                           @RequestParam(value = "user",
                                                                         required = false) final String user,
                                                           @RequestParam(value = "group",
                                                                         required = false) final List<String> groups) {
        Optional<ProcessInstance<$Type$>> pi = process.instances().findById(id);
        return pi.isPresent() ? ResponseEntity.ok(HumanTaskHelper.findTask(pi.get(), taskId, Policies.of(user, groups))
                .getComments().values()) : ResponseEntity.notFound().build();
    }

    @GetMapping(value = "/{id}/$taskName$/{taskId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<$TaskModel$> getTask(@PathVariable("id") String id,
                                               @PathVariable("taskId") String taskId,
                                               @RequestParam(value = "user", required = false) final String user,
                                               @RequestParam(value = "group",
                                                             required = false) final List<String> groups) {
        return process
                .instances()
                .findById(id)
                .map(pi -> $TaskModel$.from(pi.workItem(taskId, Policies.of(user, groups))))
                .map(m -> ResponseEntity.ok(m))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(value = "$taskName$/schema", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getSchema() {
        return JsonSchemaUtil.load(this.getClass().getClassLoader(), process.id(), "$taskName$");
    }

    @GetMapping(value = "/{id}/$taskName$/{taskId}/schema", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getSchemaAndPhases(@PathVariable("id") final String id,
                                                  @PathVariable("taskId") final String taskId,
                                                  @RequestParam(value = "user", required = false) final String user,
                                                  @RequestParam(value = "group",
                                                                required = false) final List<String> groups) {
        return JsonSchemaUtil
                .addPhases(
                        process,
                        application,
                        id,
                        taskId,
                        Policies.of(user, groups),
                        JsonSchemaUtil.load(this.getClass().getClassLoader(), process.id(), "$taskName$"));
    }

    @DeleteMapping(value = "/{id}/$taskName$/{taskId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<$Type$Output> abortTask(@PathVariable("id") final String id,
                                                  @PathVariable("taskId") final String taskId,
                                                  @RequestParam(value = "phase", required = false,
                                                                defaultValue = "abort") final String phase,
                                                  @RequestParam(value = "user", required = false) final String user,
                                                  @RequestParam(value = "group",
                                                                required = false) final List<String> groups) {
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
                                                    HumanTaskTransition.withoutModel(phase, Policies.of(user, groups)));
                                    return ResponseEntity.ok(pi.checkError().variables().toOutput());
                                })
                                .orElseGet(() -> ResponseEntity.notFound().build()));
    }
}
