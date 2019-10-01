/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.kie.kogito.index.event.KogitoUserTaskCloudEvent;
import org.kie.kogito.index.model.UserTaskInstance;

import static org.kie.kogito.index.Constants.USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE;
import static org.kie.kogito.index.json.JsonUtils.getObjectMapper;

public class UserTaskInstanceMetaMapper implements Function<KogitoUserTaskCloudEvent, ObjectNode> {

    @Override
    public ObjectNode apply(KogitoUserTaskCloudEvent event) {
        if (event == null) {
            return null;
        }

        UserTaskInstance ut = event.getData();
        ObjectNode json = getObjectMapper().createObjectNode();
        json.put("id", event.getRootProcessInstanceId() == null ? event.getProcessInstanceId() : event.getRootProcessInstanceId());
        json.put("processId", event.getRootProcessId() == null ? event.getProcessId() : event.getRootProcessId());
        json.withArray(USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE).add(getUserTaskJson(ut));
        return json;
    }

    private ObjectNode getUserTaskJson(UserTaskInstance ut) {
        ObjectNode json = getObjectMapper().createObjectNode();
        json.put("id", ut.getId());
        json.put("processInstanceId", ut.getProcessInstanceId());
        json.put("state", ut.getState());
        if (ut.getDescription() != null) {
            json.put("description", ut.getDescription());
        }
        if (ut.getName() != null) {
            json.put("name", ut.getName());
        }
        if (ut.getPriority() != null) {
            json.put("priority", ut.getPriority());
        }
        if (ut.getActualOwner() != null) {
            json.put("actualOwner", ut.getActualOwner());
        }
        mapArray("adminUsers", ut.getAdminUsers(), json);
        mapArray("adminGroups", ut.getAdminGroups(), json);
        mapArray("excludedUsers", ut.getExcludedUsers(), json);
        mapArray("potentialGroups", ut.getPotentialGroups(), json);
        mapArray("potentialUsers", ut.getPotentialUsers(), json);
        if (ut.getCompleted() != null) {
            json.put("completed", ut.getCompleted().toInstant().toEpochMilli());
        }
        if (ut.getStarted() != null) {
            json.put("started", ut.getStarted().toInstant().toEpochMilli());
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
