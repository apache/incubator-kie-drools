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

package org.kie.kogito.index.service;

import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.kie.kogito.index.cache.CacheService;
import org.kie.kogito.index.model.NodeInstance;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.model.UserTaskInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.toList;
import static org.kie.kogito.index.Constants.PROCESS_INSTANCES_DOMAIN_ATTRIBUTE;
import static org.kie.kogito.index.Constants.USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE;
import static org.kie.kogito.index.json.JsonUtils.parseJson;

@ApplicationScoped
public class IndexingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexingService.class);

    @Inject
    CacheService manager;

    public void indexProcessInstance(ProcessInstance pi) {
        ProcessInstance previousPI = manager.getProcessInstancesCache().get(pi.getId());
        if (previousPI != null) {
            List<NodeInstance> nodes = previousPI.getNodes().stream().filter(n -> !pi.getNodes().contains(n)).collect(toList());
            pi.getNodes().addAll(nodes);
        }
        manager.getProcessInstancesCache().put(pi.getId(), pi);
    }

    private String getModelFromProcessId(String processId) {
        return manager.getProcessIdModelCache().get(processId);
    }

    public void indexUserTaskInstance(UserTaskInstance ut) {
        manager.getUserTaskInstancesCache().put(ut.getId(), ut);
    }

    public void indexModel(String json) {
        JsonObject jsonObject = parseJson(json);
        String processId = jsonObject.getString("processId");
        String type = getModelFromProcessId(processId);
        if (type == null) {
//          Unknown process type, ignore
            LOGGER.debug("Ignoring Kogito cloud event for unknown process: {}", processId);
            return;
        }

        String processInstanceId = jsonObject.getString("id");

        Map<String, JsonObject> cache = manager.getDomainModelCache(processId);
        JsonObject model = cache.get(processInstanceId);
        if (model == null) {
            JsonObjectBuilder builder = Json.createObjectBuilder();
            builder.add("_type", type);
            builder.addAll(Json.createObjectBuilder(jsonObject).remove("processId"));
            JsonObject build = builder.build();
            cache.put(processInstanceId, build);
        } else {
            JsonObjectBuilder builder = Json.createObjectBuilder();
            builder.add("_type", type);
            JsonArray indexPIArray = jsonObject.getJsonArray(PROCESS_INSTANCES_DOMAIN_ATTRIBUTE);
            if (indexPIArray != null) {
                builder.addAll(Json.createObjectBuilder(jsonObject).remove("processId"));
                JsonArray utArray = model.getJsonArray(USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE);
                if (utArray != null) {
                    builder.add(USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE, utArray);
                }
                copyJsonArray(PROCESS_INSTANCES_DOMAIN_ATTRIBUTE, model, builder, indexPIArray);
            }
            JsonArray indexTIArray = jsonObject.getJsonArray(USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE);
            if (indexTIArray != null) {
                builder.addAll(Json.createObjectBuilder(model));
                copyJsonArray(USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE, model, builder, indexTIArray);
            }
            cache.put(processInstanceId, builder.build());
        }
    }

    private void copyJsonArray(String attribute, JsonObject model, JsonObjectBuilder builder, JsonArray indexTIArray) {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder(indexTIArray);
        JsonArray jsonArray = model.getJsonArray(attribute);
        if (jsonArray != null) {
            String indexTaskId = indexTIArray.get(0).asJsonObject().getString("id");
            jsonArray.stream().filter(ti -> !indexTaskId.equals(ti.asJsonObject().getString("id"))).forEach(arrayBuilder::add);
        }
        builder.remove(attribute);
        builder.add(attribute, arrayBuilder.build());
    }
}
