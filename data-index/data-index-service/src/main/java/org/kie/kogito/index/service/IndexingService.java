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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.kie.kogito.index.cache.Cache;
import org.kie.kogito.index.cache.CacheService;
import org.kie.kogito.index.model.NodeInstance;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.model.UserTaskInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.toList;
import static org.kie.kogito.index.Constants.PROCESS_INSTANCES_DOMAIN_ATTRIBUTE;
import static org.kie.kogito.index.Constants.USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE;
import static org.kie.kogito.index.json.JsonUtils.getObjectMapper;

@ApplicationScoped
public class IndexingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexingService.class);
    private static final String PROCESS_ID = "processId";

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

    public void indexUserTaskInstance(UserTaskInstance ut) {
        manager.getUserTaskInstancesCache().put(ut.getId(), ut);
    }

    public void indexModel(ObjectNode json) {
        String processId = json.get(PROCESS_ID).asText();
        Cache<String, ObjectNode> cache = manager.getDomainModelCache(processId);
        if (cache == null) {
//          Unknown process type, ignore
            LOGGER.debug("Ignoring Kogito cloud event for unknown process: {}", processId);
            return;
        }

        String processInstanceId = json.get("id").asText();
        String type = cache.getRootType();
        ObjectNode model = cache.get(processInstanceId);
        if (model == null) {
            ObjectNode builder = getObjectMapper().createObjectNode();
            builder.put("_type", type);
            json.remove(PROCESS_ID);
            builder.setAll(json);
            cache.put(processInstanceId, builder);
        } else {
            ObjectNode builder = getObjectMapper().createObjectNode();
            builder.put("_type", type);
            ArrayNode indexPIArray = (ArrayNode) json.get(PROCESS_INSTANCES_DOMAIN_ATTRIBUTE);
            if (indexPIArray != null) {
                json.remove(PROCESS_ID);
                JsonNode id = indexPIArray.get(0).get("id");
                if (processInstanceId.equals(id.asText())) {
                    //For processes simply copy all values
                    builder.setAll(json);
                } else {
                    //For sub-process merge with current values
                    builder.setAll(model);
                    builder.setAll(json);
                }
                ArrayNode utArray = (ArrayNode) model.get(USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE);
                if (utArray != null) {
                    builder.set(USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE, utArray);
                }
                copyJsonArray(PROCESS_INSTANCES_DOMAIN_ATTRIBUTE, model, builder, indexPIArray);
            }
            ArrayNode indexTIArray = (ArrayNode) json.get(USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE);
            if (indexTIArray != null) {
                builder.setAll(model);
                copyJsonArray(USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE, model, builder, indexTIArray);
            }
            cache.put(processInstanceId, builder);
        }
    }

    private void copyJsonArray(String attribute, ObjectNode model, ObjectNode builder, ArrayNode arrayNode) {
        ArrayNode arrayBuilder = getObjectMapper().createArrayNode().addAll(arrayNode);
        ArrayNode jsonArray = model.withArray(attribute);
        if (jsonArray != null) {
            String indexTaskId = arrayNode.get(0).get("id").asText();
            jsonArray.forEach(ti -> {
                if (!indexTaskId.equals(ti.get("id").asText())) {
                    arrayBuilder.add(ti);
                }
            });
        }
        builder.remove(attribute);
        builder.set(attribute, arrayBuilder);
    }
}
