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
package org.kie.kogito.index.service.json;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.kie.kogito.event.usertask.UserTaskInstanceAssignmentDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceAssignmentEventBody;
import org.kie.kogito.event.usertask.UserTaskInstanceAttachmentDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceAttachmentEventBody;
import org.kie.kogito.event.usertask.UserTaskInstanceCommentDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceCommentEventBody;
import org.kie.kogito.event.usertask.UserTaskInstanceDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceStateDataEvent;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.kie.kogito.index.json.JsonUtils.getObjectMapper;
import static org.kie.kogito.index.storage.Constants.ID;
import static org.kie.kogito.index.storage.Constants.KOGITO_DOMAIN_ATTRIBUTE;
import static org.kie.kogito.index.storage.Constants.LAST_UPDATE;
import static org.kie.kogito.index.storage.Constants.PROCESS_ID;
import static org.kie.kogito.index.storage.Constants.USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE;

public class UserTaskInstanceMetaMapper implements Function<UserTaskInstanceDataEvent<?>, ObjectNode> {

    @Override
    public ObjectNode apply(UserTaskInstanceDataEvent<?> event) {
        if (event == null) {
            return null;
        }

        ObjectNode json = getObjectMapper().createObjectNode();
        json.put(ID, isNullOrEmpty(event.getKogitoRootProcessInstanceId()) ? event.getKogitoProcessInstanceId() : event.getKogitoRootProcessInstanceId());
        json.put(PROCESS_ID, isNullOrEmpty(event.getKogitoRootProcessId()) ? event.getKogitoProcessId() : event.getKogitoRootProcessId());
        ObjectNode kogito = getObjectMapper().createObjectNode();
        kogito.put(LAST_UPDATE, event.getTime() == null ? null : event.getTime().toInstant().toEpochMilli());
        kogito.withArray(USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE).add(getUserTaskJson(event));
        json.set(KOGITO_DOMAIN_ATTRIBUTE, kogito);
        return json;
    }

    private ObjectNode getUserTaskJson(UserTaskInstanceDataEvent<?> event) {
        ObjectNode json = getObjectMapper().createObjectNode();
        json.put(ID, event.getKogitoUserTaskInstanceId());
        json.put("processInstanceId", event.getKogitoProcessInstanceId());
        json.put("state", event.getKogitoUserTaskInstanceState());
        json.put(LAST_UPDATE, event.getTime() == null ? null : event.getTime().toInstant().toEpochMilli());

        if (event instanceof UserTaskInstanceStateDataEvent) {

            UserTaskInstanceStateDataEvent data = (UserTaskInstanceStateDataEvent) event;
            json.put("actualOwner", data.getData().getActualOwner());

            if (!isNullOrEmpty(data.getData().getUserTaskDescription())) {
                json.put("description", data.getData().getUserTaskDescription());
            }
            if (!isNullOrEmpty(data.getData().getUserTaskName())) {
                json.put("name", data.getData().getUserTaskName());
            }
            if (!isNullOrEmpty(data.getData().getUserTaskPriority())) {
                json.put("priority", data.getData().getUserTaskPriority());
            }

            if (data.getData().getState() != null && data.getData().getState().equals("Completed")) {
                json.put("completed", data.getData().getEventDate().toInstant().toEpochMilli());
            }
            List<String> events = List.of("Ready", "InProgress");
            if (data.getData().getState() != null && events.contains(data.getData().getState())) {
                json.put("started", data.getData().getEventDate().toInstant().toEpochMilli());
            }
        } else if (event instanceof UserTaskInstanceAssignmentDataEvent) {
            UserTaskInstanceAssignmentDataEvent data = (UserTaskInstanceAssignmentDataEvent) event;
            UserTaskInstanceAssignmentEventBody body = data.getData();
            switch (body.getAssignmentType()) {
                case "USER_OWNERS":
                    mapArray("potentialUsers", new HashSet<>(body.getUsers()), json);
                    break;
                case "USER_GROUPS":
                    mapArray("potentialGroups", new HashSet<>(body.getUsers()), json);
                    break;
                case "USERS_EXCLUDED":
                    mapArray("excludedUsers", new HashSet<>(body.getUsers()), json);
                    break;
                case "ADMIN_GROUPS":
                    mapArray("adminUsers", new HashSet<>(body.getUsers()), json);
                    break;
                case "ADMIN_USERS":
                    mapArray("adminGroups", new HashSet<>(body.getUsers()), json);
                    break;
            }

        } else if (event instanceof UserTaskInstanceAttachmentDataEvent) {
            UserTaskInstanceAttachmentDataEvent data = (UserTaskInstanceAttachmentDataEvent) event;
            UserTaskInstanceAttachmentEventBody body = data.getData();
            Map<String, Object> attachment = new HashMap<>();
            attachment.put("id", body.getAttachmentId());
            attachment.put("name", body.getAttachmentName());
            attachment.put("content", body.getAttachmentURI() != null ? body.getAttachmentURI().toString() : "");
            attachment.put("updatedBy", body.getEventUser());
            attachment.put("updatedAt", body.getEventDate());

            if (body.getEventType() == UserTaskInstanceAttachmentEventBody.EVENT_TYPE_DELETED) {
                attachment.put("remove", true);
            }

            ArrayNode arrayNode = json.withArray("attachments");
            arrayNode.add(getObjectMapper().valueToTree(attachment));

        } else if (event instanceof UserTaskInstanceCommentDataEvent) {
            UserTaskInstanceCommentDataEvent data = (UserTaskInstanceCommentDataEvent) event;
            UserTaskInstanceCommentEventBody body = data.getData();
            Map<String, Object> comment = new HashMap<>();
            comment.put("id", body.getCommentId());
            comment.put("content", body.getCommentContent());
            comment.put("updatedBy", body.getEventUser());
            comment.put("updatedAt", body.getEventDate());
            if (body.getEventType() == UserTaskInstanceCommentEventBody.EVENT_TYPE_DELETED) {
                comment.put("remove", true);
            }
            ArrayNode arrayNode = json.withArray("comments");
            arrayNode.add(getObjectMapper().valueToTree(comment));
        }

        return json;
    }

    private void mapArray(String attribute, Set<String> strings, ObjectNode json) {
        if (strings != null && !strings.isEmpty()) {
            ArrayNode array = json.withArray(attribute);
            strings.forEach(array::add);
        }
    }
}
