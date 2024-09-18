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
package org.kie.kogito.event.impl.adapter;

import java.util.Map;

import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceCommentDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceCommentEventBody;
import org.kie.kogito.usertask.events.UserTaskCommentEvent;

public class UserTaskCommentEventDataEventAdapter extends AbstractDataEventAdapter {

    public UserTaskCommentEventDataEventAdapter() {
        super(UserTaskCommentEvent.class);
    }

    @Override
    public DataEvent<?> adapt(Object payload) {
        UserTaskCommentEvent event = (UserTaskCommentEvent) payload;
        Map<String, Object> metadata = AdapterHelper.buildUserTaskMetadata(event.getUserTaskInstance());

        int eventType = UserTaskInstanceCommentEventBody.EVENT_TYPE_ADDED;
        if (event.getOldComment() != null && event.getNewComment() == null) {
            eventType = UserTaskInstanceCommentEventBody.EVENT_TYPE_DELETED;
        } else if (event.getOldComment() != null && event.getNewComment() != null) {
            eventType = UserTaskInstanceCommentEventBody.EVENT_TYPE_CHANGE;
        }

        UserTaskInstanceCommentEventBody.Builder builder = UserTaskInstanceCommentEventBody.create()
                .eventType(eventType)
                .userTaskDefinitionId(event.getUserTask().id())
                .userTaskInstanceId(event.getUserTaskInstance().getId())
                .userTaskName(event.getUserTaskInstance().getTaskName());

        String updatedBy = null;
        switch (eventType) {
            case UserTaskInstanceCommentEventBody.EVENT_TYPE_ADDED:
            case UserTaskInstanceCommentEventBody.EVENT_TYPE_CHANGE:
                builder.commentContent(event.getNewComment().getContent())
                        .commentId(event.getNewComment().getId())
                        .eventDate(event.getNewComment().getUpdatedAt())
                        .eventUser(event.getNewComment().getUpdatedBy());
                updatedBy = event.getNewComment().getUpdatedBy();
                break;
            case UserTaskInstanceCommentEventBody.EVENT_TYPE_DELETED:
                builder.commentId(event.getOldComment().getId())
                        .eventDate(event.getOldComment().getUpdatedAt())
                        .eventUser(event.getOldComment().getUpdatedBy());

                updatedBy = event.getOldComment().getUpdatedBy();
                break;
        }

        UserTaskInstanceCommentEventBody body = builder.build();
        UserTaskInstanceCommentDataEvent utEvent = new UserTaskInstanceCommentDataEvent(AdapterHelper.buildSource(getConfig().service(), event.getUserTaskInstance().getExternalReferenceId()),
                getConfig().addons().toString(), updatedBy, metadata, body);

        return utEvent;
    }

}
