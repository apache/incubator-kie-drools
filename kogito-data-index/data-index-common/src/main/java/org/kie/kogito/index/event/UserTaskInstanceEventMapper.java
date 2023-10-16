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
package org.kie.kogito.index.event;

import java.net.URI;
import java.util.function.Function;

import org.kie.kogito.event.process.AttachmentEventBody;
import org.kie.kogito.event.process.CommentEventBody;
import org.kie.kogito.event.process.UserTaskInstanceDataEvent;
import org.kie.kogito.index.model.Attachment;
import org.kie.kogito.index.model.Comment;
import org.kie.kogito.index.model.UserTaskInstance;

import com.google.common.net.UrlEscapers;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.kie.kogito.index.DateTimeUtils.toZonedDateTime;
import static org.kie.kogito.index.json.JsonUtils.getObjectMapper;

public class UserTaskInstanceEventMapper implements Function<UserTaskInstanceDataEvent, UserTaskInstance> {

    @Override
    public UserTaskInstance apply(UserTaskInstanceDataEvent event) {
        if (event == null || event.getData() == null) {
            return null;
        }

        UserTaskInstance task = new UserTaskInstance();
        task.setId(event.getData().getId());
        task.setProcessInstanceId(event.getData().getProcessInstanceId());
        task.setProcessId(event.getData().getProcessId());
        task.setRootProcessId(event.getData().getRootProcessId());
        task.setRootProcessInstanceId(event.getData().getRootProcessInstanceId());
        task.setName(event.getData().getTaskName());
        task.setDescription(event.getData().getTaskDescription());
        task.setState(event.getData().getState());
        task.setPriority(event.getData().getTaskPriority());
        task.setStarted(toZonedDateTime(event.getData().getStartDate()));
        task.setCompleted(toZonedDateTime(event.getData().getCompleteDate()));
        task.setActualOwner(event.getData().getActualOwner());
        task.setAdminUsers(event.getData().getAdminUsers());
        task.setAdminGroups(event.getData().getAdminGroups());
        task.setExcludedUsers(event.getData().getExcludedUsers());
        task.setPotentialUsers(event.getData().getPotentialUsers());
        task.setPotentialGroups(event.getData().getPotentialGroups());
        task.setComments(event.getData().getComments().stream().map(comment()).collect(toList()));
        task.setAttachments(event.getData().getAttachments().stream().map(attachment()).collect(toList()));
        task.setInputs(getObjectMapper().valueToTree(event.getData().getInputs()));
        task.setOutputs(getObjectMapper().valueToTree(event.getData().getOutputs()));
        task.setEndpoint(event.getSource() == null ? null : getEndpoint(event.getSource(), event.getData().getProcessInstanceId(), event.getData().getTaskName(), event.getData().getId()));
        task.setLastUpdate(toZonedDateTime(event.getTime()));
        task.setReferenceName(event.getData().getReferenceName());
        return task;
    }

    private Function<CommentEventBody, Comment> comment() {
        return c -> Comment.builder()
                .id(c.getId())
                .content(c.getContent())
                .updatedBy(c.getUpdatedBy())
                .updatedAt(toZonedDateTime(c.getUpdatedAt()))
                .build();
    }

    private Function<AttachmentEventBody, Attachment> attachment() {
        return a -> Attachment.builder()
                .id(a.getId())
                .content(a.getContent() == null ? null : a.getContent().toString())
                .name(a.getName())
                .updatedBy(a.getUpdatedBy())
                .updatedAt(toZonedDateTime(a.getUpdatedAt()))
                .build();
    }

    public String getEndpoint(URI source, String pId, String taskName, String taskId) {
        String name = UrlEscapers.urlPathSegmentEscaper().escape(taskName);
        return source.toString() + format("/%s/%s/%s", pId, name, taskId);
    }
}
