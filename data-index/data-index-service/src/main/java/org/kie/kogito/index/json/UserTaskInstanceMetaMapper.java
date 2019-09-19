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

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.kie.kogito.index.event.KogitoUserTaskCloudEvent;
import org.kie.kogito.index.model.UserTaskInstance;

public class UserTaskInstanceMetaMapper implements Function<KogitoUserTaskCloudEvent, JsonObject> {

    @Override
    public JsonObject apply(KogitoUserTaskCloudEvent event) {
        if (event == null) {
            return null;
        }

        UserTaskInstance ut = event.getData();
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("id", ut.getId());
        builder.add("processInstanceId", ut.getProcessInstanceId());
        builder.add("state", ut.getState());
        if (ut.getDescription() != null) {
            builder.add("description", ut.getDescription());
        }
        if (ut.getName() != null) {
            builder.add("name", ut.getName());
        }
        if (ut.getPriority() != null) {
            builder.add("priority", ut.getPriority());
        }
        if (ut.getActualOwner() != null) {
            builder.add("actualOwner", ut.getActualOwner());
        }
        mapArray("adminUsers", ut.getAdminUsers(), builder);
        mapArray("adminGroups", ut.getAdminGroups(), builder);
        mapArray("excludedUsers", ut.getExcludedUsers(), builder);
        mapArray("potentialGroups", ut.getPotentialGroups(), builder);
        mapArray("potentialUsers", ut.getPotentialUsers(), builder);
        if (ut.getCompleted() != null) {
            builder.add("completed", ut.getCompleted().toInstant().toEpochMilli());
        }
        if (ut.getStarted() != null) {
            builder.add("started", ut.getStarted().toInstant().toEpochMilli());
        }
        return builder.build();
    }

    private void mapArray(String attribute, Set<String> strings, JsonObjectBuilder builder) {
        if (strings != null && !strings.isEmpty()) {
            builder.add(attribute, new JsonArrayMapper().apply(strings));
        }
    }
}
