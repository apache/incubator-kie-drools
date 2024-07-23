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

import org.jbpm.process.instance.impl.humantask.HumanTaskHelper;
import org.jbpm.util.JsonSchemaUtil;
import org.kie.kogito.auth.IdentityProviders;
import org.kie.kogito.auth.SecurityPolicy;
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
    public ResponseEntity signal(@PathVariable("id") final String id,
                                 @RequestParam("user") final String user,
                                 @RequestParam("group") final List<String> groups,
                                 final UriComponentsBuilder uriComponentsBuilder) {

        return processService.signalTask(process, id, "$taskName$", SecurityPolicy.of(user, groups))
                .map(task -> ResponseEntity
                        .created(uriComponentsBuilder
                                         .path("/$name$/{id}/$taskName$/{taskId}")
                                         .buildAndExpand(id, task.getId()).toUri())
                        .body(task.getResults()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping(value = "/{id}/$taskName$/{taskId}/phases/{phase}", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public $Type$Output completeTask(@PathVariable("id") final String id,
                                     @PathVariable("taskId") final String taskId,
                                     @PathVariable("phase") final String phase,
                                     @RequestParam("user") final String user,
                                     @RequestParam("group") final List<String> groups,
                                     @RequestBody(required = false) final $TaskOutput$ model) {
        return processService.taskTransition(process, id, taskId, phase, SecurityPolicy.of(user, groups), model)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PutMapping(value = "/{id}/$taskName$/{taskId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public $TaskOutput$ saveTask(@PathVariable("id") final String id,
                                 @PathVariable("taskId") final String taskId,
                                 @RequestParam(value = "user", required = false) final String user,
                                 @RequestParam(value = "group", required = false) final List<String> groups,
                                 @RequestBody(required = false) final $TaskOutput$ model) {
        return processService.saveTask(process, id, taskId, SecurityPolicy.of(user, groups), model, $TaskOutput$::fromMap)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping(value = "/{id}/$taskName$/{taskId}", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public $Type$Output taskTransition(@PathVariable("id") final String id,
                                       @PathVariable("taskId") final String taskId,
                                       @RequestParam(value = "phase", required = false,
                                               defaultValue = "complete") final String phase,
                                       @RequestParam(value = "user",
                                               required = false) final String user,
                                       @RequestParam(value = "group",
                                               required = false) final List<String> groups,
                                       @RequestBody(required = false) final $TaskOutput$ model) {
        return processService.taskTransition(process, id, taskId, phase, SecurityPolicy.of(user, groups), model)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "/{id}/$taskName$/{taskId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public $TaskModel$ getTask(@PathVariable("id") String id,
                               @PathVariable("taskId") String taskId,
                               @RequestParam(value = "user", required = false) final String user,
                               @RequestParam(value = "group",
                                       required = false) final List<String> groups) {
        return processService.getTask(process, id, taskId, SecurityPolicy.of(user, groups), $TaskModel$::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping(value = "/{id}/$taskName$/{taskId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public $Type$Output abortTask(@PathVariable("id") final String id,
                                  @PathVariable("taskId") final String taskId,
                                  @RequestParam(value = "phase", required = false,
                                          defaultValue = "abort") final String phase,
                                  @RequestParam(value = "user", required = false) final String user,
                                  @RequestParam(value = "group",
                                          required = false) final List<String> groups) {
        return processService.taskTransition(process, id, taskId, phase, SecurityPolicy.of(user, groups), null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
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
        return processService.getSchemaAndPhases(process, id, taskId, "$taskName$", SecurityPolicy.of(user, groups));
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
        return processService.addComment(process, id, taskId, SecurityPolicy.of(user, groups), commentInfo)
                .map(comment -> ResponseEntity
                        .created(uriComponentsBuilder.path("/$name$/{id}/$taskName$/{taskId}/comments/{commentId}")
                                         .buildAndExpand(id, taskId, comment.getId().toString()).toUri())
                        .body(comment))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PutMapping(value = "/{id}/$taskName$/{taskId}/comments/{commentId}", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.TEXT_PLAIN_VALUE)
    public Comment updateComment(@PathVariable("id") final String id,
                                 @PathVariable("taskId") final String taskId,
                                 @PathVariable("commentId") final String commentId,
                                 @RequestParam(value = "user", required = false) final String user,
                                 @RequestParam(value = "group",
                                         required = false) final List<String> groups,
                                 @RequestBody String comment) {
        return processService.updateComment(process, id, taskId, commentId, SecurityPolicy.of(user, groups), comment)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping(value = "/{id}/$taskName$/{taskId}/comments/{commentId}")
    public ResponseEntity deleteComment(@PathVariable("id") final String id,
                                        @PathVariable("taskId") final String taskId,
                                        @PathVariable("commentId") final String commentId,
                                        @RequestParam(value = "user", required = false) final String user,
                                        @RequestParam(value = "group", required = false) final List<String> groups) {
        return processService.deleteComment(process, id, taskId, commentId, SecurityPolicy.of(user, groups))
                .map(removed -> (removed ? ResponseEntity.ok().build() : ResponseEntity.notFound().build()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
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
        return processService.addAttachment(process, id, taskId, SecurityPolicy.of(user, groups), attachmentInfo)
                .map(attachment -> ResponseEntity
                        .created(uriComponentsBuilder.path(
                                "/$name$/{id}/$taskName$/{taskId}/attachments/{attachmentId}")
                                         .buildAndExpand(id,
                                                         taskId, attachment.getId()).toUri())
                        .body(attachment))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PutMapping(value = "/{id}/$taskName$/{taskId}/attachments/{attachmentId}",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Attachment updateAttachment(@PathVariable("id") final String id,
                                       @PathVariable("taskId") final String taskId,
                                       @PathVariable("attachmentId") final String attachmentId,
                                       @RequestParam(value = "user",
                                               required = false) final String user,
                                       @RequestParam(value = "group",
                                               required = false) final List<String> groups,
                                       @RequestBody AttachmentInfo attachment) {
        return processService.updateAttachment(process, id, taskId, attachmentId, SecurityPolicy.of(user, groups), attachment)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping(value = "/{id}/$taskName$/{taskId}/attachments/{attachmentId}")
    public ResponseEntity deleteAttachment(@PathVariable("id") final String id,
                                           @PathVariable("taskId") final String taskId,
                                           @PathVariable("attachmentId") final String attachmentId,
                                           @RequestParam(value = "user", required = false) final String user,
                                           @RequestParam(value = "group", required = false) final List<String> groups) {

        return processService.deleteAttachment(process, id, taskId, attachmentId, SecurityPolicy.of(user, groups))
                .map(removed -> (removed ? ResponseEntity.ok() : ResponseEntity.notFound()).build())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "/{id}/$taskName$/{taskId}/attachments/{attachmentId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Attachment getAttachment(@PathVariable("id") final String id,
                                    @PathVariable("taskId") final String taskId,
                                    @PathVariable("attachmentId") final String attachmentId,
                                    @RequestParam(value = "user", required = false) final String user,
                                    @RequestParam(value = "group",
                                            required = false) final List<String> groups) {
        return processService.getAttachment(process, id, taskId, attachmentId, SecurityPolicy.of(user, groups))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attachment " + attachmentId + " not found"));
    }

    @GetMapping(value = "/{id}/$taskName$/{taskId}/attachments", produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<Attachment> getAttachments(@PathVariable("id") final String id,
                                                 @PathVariable("taskId") final String taskId,
                                                 @RequestParam(value = "user") final String user,
                                                 @RequestParam(value = "group") final List<String> groups) {
        return processService.getAttachments(process, id, taskId, SecurityPolicy.of(user, groups))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "/{id}/$taskName$/{taskId}/comments/{commentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Comment getComment(@PathVariable("id") final String id,
                              @PathVariable("taskId") final String taskId,
                              @PathVariable("commentId") final String commentId,
                              @RequestParam(value = "user", required = false) final String user,
                              @RequestParam(value = "group",
                                      required = false) final List<String> groups) {
        return processService.getComment(process, id, taskId, commentId, SecurityPolicy.of(user, groups))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment " + commentId + " not found"));
    }

    @GetMapping(value = "/{id}/$taskName$/{taskId}/comments", produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<Comment> getComments(@PathVariable("id") final String id,
                                           @PathVariable("taskId") final String taskId,
                                           @RequestParam(value = "user",
                                                   required = false) final String user,
                                           @RequestParam(value = "group",
                                                   required = false) final List<String> groups) {
        return processService.getComments(process, id, taskId, SecurityPolicy.of(user, groups))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}