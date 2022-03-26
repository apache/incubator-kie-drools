/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.index.json;

import java.util.Set;
import java.util.function.Function;

import org.kie.kogito.event.process.UserTaskInstanceDataEvent;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.kie.kogito.index.Constants.ID;
import static org.kie.kogito.index.Constants.KOGITO_DOMAIN_ATTRIBUTE;
import static org.kie.kogito.index.Constants.LAST_UPDATE;
import static org.kie.kogito.index.Constants.PROCESS_ID;
import static org.kie.kogito.index.Constants.USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE;
import static org.kie.kogito.index.json.JsonUtils.getObjectMapper;

public class UserTaskInstanceMetaMapper implements Function<UserTaskInstanceDataEvent, ObjectNode> {

    @Override
    public ObjectNode apply(UserTaskInstanceDataEvent event) {
        if (event == null) {
            return null;
        }

        ObjectNode json = getObjectMapper().createObjectNode();
        json.put(ID, isNullOrEmpty(event.getData().getRootProcessInstanceId()) ? event.getData().getProcessInstanceId() : event.getData().getRootProcessInstanceId());
        json.put(PROCESS_ID, isNullOrEmpty(event.getData().getRootProcessId()) ? event.getData().getProcessId() : event.getData().getRootProcessId());
        ObjectNode kogito = getObjectMapper().createObjectNode();
        kogito.put(LAST_UPDATE, event.getTime().toInstant().toEpochMilli());
        kogito.withArray(USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE).add(getUserTaskJson(event));
        json.set(KOGITO_DOMAIN_ATTRIBUTE, kogito);
        return json;
    }

    private ObjectNode getUserTaskJson(UserTaskInstanceDataEvent event) {
        ObjectNode json = getObjectMapper().createObjectNode();
        json.put(ID, event.getData().getId());
        json.put("processInstanceId", event.getData().getProcessInstanceId());
        json.put("state", event.getData().getState());
        if (!isNullOrEmpty(event.getData().getTaskDescription())) {
            json.put("description", event.getData().getTaskDescription());
        }
        if (!isNullOrEmpty(event.getData().getTaskName())) {
            json.put("name", event.getData().getTaskName());
        }
        if (!isNullOrEmpty(event.getData().getTaskPriority())) {
            json.put("priority", event.getData().getTaskPriority());
        }
        if (!isNullOrEmpty(event.getData().getActualOwner())) {
            json.put("actualOwner", event.getData().getActualOwner());
        }
        mapArray("adminUsers", event.getData().getAdminUsers(), json);
        mapArray("adminGroups", event.getData().getAdminGroups(), json);
        mapArray("excludedUsers", event.getData().getExcludedUsers(), json);
        mapArray("potentialGroups", event.getData().getPotentialGroups(), json);
        mapArray("potentialUsers", event.getData().getPotentialUsers(), json);
        if (event.getData().getCompleteDate() != null) {
            json.put("completed", event.getData().getCompleteDate().toInstant().toEpochMilli());
        }
        if (event.getData().getStartDate() != null) {
            json.put("started", event.getData().getStartDate().toInstant().toEpochMilli());
        }
        if (event.getTime() != null) {
            json.put(LAST_UPDATE, event.getTime().toInstant().toEpochMilli());
        }
        return json;
    }

    private void mapArray(String attribute, Set<String> strings, ObjectNode json) {
        if (strings != null && !strings.isEmpty()) {
            ArrayNode array = json.withArray(attribute);
            strings.forEach(s -> array.add(s));
        }
    }
}
