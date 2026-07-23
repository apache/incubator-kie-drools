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
package org.kie.kogito.index.storage.merger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.kie.kogito.event.usertask.UserTaskInstanceCommentDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceCommentEventBody;
import org.kie.kogito.event.usertask.UserTaskInstanceDataEvent;
import org.kie.kogito.index.DateTimeUtils;
import org.kie.kogito.index.model.Comment;
import org.kie.kogito.index.model.UserTaskInstance;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserTaskInstanceCommentDataEventMerger implements UserTaskInstanceEventMerger {
    @Override
    public UserTaskInstance merge(UserTaskInstance userTaskInstance, UserTaskInstanceDataEvent<?> data) {
        UserTaskInstanceCommentDataEvent event = (UserTaskInstanceCommentDataEvent) data;
        UserTaskInstanceCommentEventBody body = event.getData();
        List<Comment> comments = userTaskInstance.getComments();
        if (comments == null) {
            comments = new ArrayList<>();
            userTaskInstance.setComments(comments);
        }
        switch (body.getEventType()) {
            case UserTaskInstanceCommentEventBody.EVENT_TYPE_ADDED:
            case UserTaskInstanceCommentEventBody.EVENT_TYPE_CHANGE:
                Optional<Comment> found = comments.stream().filter(e -> e.getId().equals(body.getCommentId())).findAny();
                Comment comment;
                if (found.isEmpty()) {
                    comment = new Comment();
                    comments.add(comment);
                } else {
                    comment = found.get();
                }
                comment.setId(body.getCommentId());
                comment.setContent(body.getCommentContent());
                comment.setUpdatedBy(body.getEventUser() != null ? body.getEventUser() : "unknown");
                comment.setUpdatedAt(DateTimeUtils.toZonedDateTime(body.getEventDate()));

                break;
            case UserTaskInstanceCommentEventBody.EVENT_TYPE_DELETED:
                comments.removeIf(e -> e.getId().equals(body.getCommentId()));
                break;
        }
        return userTaskInstance;
    }
}
