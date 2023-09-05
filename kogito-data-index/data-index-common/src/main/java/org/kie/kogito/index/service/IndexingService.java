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
package org.kie.kogito.index.service;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.kogito.index.model.Job;
import org.kie.kogito.index.model.NodeInstance;
import org.kie.kogito.index.model.ProcessDefinition;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.model.UserTaskInstance;
import org.kie.kogito.index.storage.DataIndexStorageService;
import org.kie.kogito.persistence.api.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static java.util.stream.Collectors.toList;
import static org.kie.kogito.index.json.JsonUtils.getObjectMapper;
import static org.kie.kogito.index.storage.Constants.ID;
import static org.kie.kogito.index.storage.Constants.KOGITO_DOMAIN_ATTRIBUTE;
import static org.kie.kogito.index.storage.Constants.LAST_UPDATE;
import static org.kie.kogito.index.storage.Constants.PROCESS_ID;
import static org.kie.kogito.index.storage.Constants.PROCESS_INSTANCES_DOMAIN_ATTRIBUTE;
import static org.kie.kogito.index.storage.Constants.USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE;

@ApplicationScoped
public class IndexingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexingService.class);

    @Inject
    DataIndexStorageService manager;

    public void indexProcessInstance(ProcessInstance pi) {
        ProcessInstance previousPI = manager.getProcessInstancesCache().get(pi.getId());
        if (previousPI != null) {
            List<NodeInstance> nodes = previousPI.getNodes().stream().filter(n -> !pi.getNodes().contains(n)).collect(toList());
            pi.getNodes().addAll(nodes);
        }
        ProcessDefinition definition = pi.getDefinition();
        if (!manager.getProcessDefinitionsCache().containsKey(definition.getKey())) {
            manager.getProcessDefinitionsCache().put(definition.getKey(), definition);
        }
        manager.getProcessInstancesCache().put(pi.getId(), pi);
    }

    public void indexJob(Job job) {
        manager.getJobsCache().put(job.getId(), job);
    }

    public void indexUserTaskInstance(UserTaskInstance ut) {
        manager.getUserTaskInstancesCache().put(ut.getId(), ut);
    }

    public void indexModel(ObjectNode json) {
        String processId = json.remove(PROCESS_ID).asText();
        Storage<String, ObjectNode> cache = manager.getDomainModelCache(processId);
        if (cache == null) {
            //          Unknown process type, ignore
            LOGGER.debug("Ignoring Kogito cloud event for unknown process: {}", processId);
            return;
        }

        String processInstanceId = json.get(ID).asText();
        String type = cache.getRootType();
        ObjectNode model = cache.get(processInstanceId);
        ObjectNode builder = getObjectMapper().createObjectNode();
        builder.put("_type", type);
        if (model == null) {
            builder.setAll(json);
        } else {
            copyAllEventData(json, processInstanceId, model, builder);
            ObjectNode kogito = indexKogitoDomain((ObjectNode) json.get(KOGITO_DOMAIN_ATTRIBUTE), (ObjectNode) model.get(KOGITO_DOMAIN_ATTRIBUTE));
            builder.set(KOGITO_DOMAIN_ATTRIBUTE, kogito);
        }
        cache.put(processInstanceId, builder);
    }

    private void copyAllEventData(ObjectNode json, String processInstanceId, ObjectNode model, ObjectNode builder) {
        ArrayNode indexPIArray = (ArrayNode) json.get(KOGITO_DOMAIN_ATTRIBUTE).get(PROCESS_INSTANCES_DOMAIN_ATTRIBUTE);
        if (indexPIArray == null) {
            builder.setAll(model);
        } else {
            JsonNode id = indexPIArray.get(0).get(ID);
            if (!processInstanceId.equals(id.asText())) {
                //For sub-process merge with current values
                builder.setAll(model);
            }
            builder.setAll(json);
        }
    }

    private ObjectNode indexKogitoDomain(ObjectNode kogitoEvent, ObjectNode kogitoCache) {
        ObjectNode kogitoBuilder = getObjectMapper().createObjectNode();
        kogitoBuilder.set(LAST_UPDATE, kogitoEvent.get(LAST_UPDATE));

        ArrayNode indexPIArray = (ArrayNode) kogitoEvent.get(PROCESS_INSTANCES_DOMAIN_ATTRIBUTE);
        if (indexPIArray != null) {
            kogitoBuilder.set(PROCESS_INSTANCES_DOMAIN_ATTRIBUTE, copyToArray(kogitoCache.get(PROCESS_INSTANCES_DOMAIN_ATTRIBUTE), indexPIArray));
            kogitoBuilder.set(USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE, kogitoCache.get(USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE));
        }

        ArrayNode indexTIArray = (ArrayNode) kogitoEvent.get(USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE);
        if (indexTIArray != null) {
            kogitoBuilder.set(USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE, copyToArray(kogitoCache.get(USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE), indexTIArray));
            kogitoBuilder.set(PROCESS_INSTANCES_DOMAIN_ATTRIBUTE, kogitoCache.get(PROCESS_INSTANCES_DOMAIN_ATTRIBUTE));
        }

        return kogitoBuilder;
    }

    private ArrayNode copyToArray(JsonNode arrayCache, ArrayNode arrayEvent) {
        if (arrayCache == null || arrayCache.isNull()) {
            return getObjectMapper().createArrayNode().add(arrayEvent.get(0));
        }
        ArrayNode arrayNode = (ArrayNode) arrayCache;
        String indexId = arrayEvent.get(0).get(ID).asText();
        for (int i = 0; i < arrayCache.size(); i++) {
            if (indexId.equals(arrayCache.get(i).get(ID).asText())) {
                arrayNode.set(i, arrayEvent.get(0));
                return arrayNode;
            }
        }

        arrayNode.add(arrayEvent.get(0));
        return arrayNode;
    }
}
