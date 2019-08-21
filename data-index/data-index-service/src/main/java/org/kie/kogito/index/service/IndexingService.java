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

import java.io.StringReader;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

import org.kie.kogito.index.cache.CacheService;
import org.kie.kogito.index.event.KogitoCloudEvent;
import org.kie.kogito.index.json.ProcessInstanceMetaMapper;
import org.kie.kogito.index.model.NodeInstance;
import org.kie.kogito.index.model.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.toList;
import static org.kie.kogito.index.Constants.PROCESS_INSTANCES_DOMAIN_ATTRIBUTE;

@ApplicationScoped
public class IndexingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexingService.class);

    @Inject
    CacheService manager;

    public void indexProcessInstance(KogitoCloudEvent event) {
        ProcessInstance pi = event.getData();
        ProcessInstance previousPI = manager.getProcessInstancesCache().get(event.getProcessInstanceId());
        if (previousPI != null) {
            List<NodeInstance> nodes = previousPI.getNodes().stream().filter(n -> !pi.getNodes().contains(n)).collect(toList());
            pi.getNodes().addAll(nodes);
        }
        manager.getProcessInstancesCache().put(event.getProcessInstanceId(), pi);
    }

    public void indexProcessInstanceModel(KogitoCloudEvent event) {
        String processId = event.getRootProcessId() == null ? event.getProcessId() : event.getRootProcessId();
        String type = getModelFromProcessId(processId);
        if (type == null) {
//          Unknown process type, ignore
            LOGGER.debug("Ignoring Kogito cloud event for Process Id: {}", event.getProcessId());
            return;
        }

        Map<String, JsonObject> cache = manager.getDomainModelCache(processId);

        if (event.getRootProcessInstanceId() == null) {
            JsonObjectBuilder builder = Json.createObjectBuilder();
            builder.add("_type", type);
            builder.addAll(Json.createObjectBuilder(readProcessInstanceVariables(event.getData().getVariables())));
            JsonObject value = cache.get(event.getProcessInstanceId());
            updateProcessInstances(builder, event, value);
            cache.put(event.getProcessInstanceId(), builder.build());
        } else {
            JsonObject value = cache.get(event.getRootProcessInstanceId());
            if (value == null) {
                LOGGER.warn("Received event for sub-process with id {}, but cache is missing entry for root process instance with id: {}", event.getProcessInstanceId(), event.getRootProcessInstanceId());
            } else {
                JsonObjectBuilder builder = Json.createObjectBuilder(value);
                updateProcessInstances(builder, event, value);
                cache.put(event.getRootProcessInstanceId(), builder.build());
            }
        }
    }

    private JsonObject readProcessInstanceVariables(String json) {
        try (JsonReader parser = Json.createReader(new StringReader(json))) {
            return parser.readObject().asJsonObject();
        }
    }

    private void updateProcessInstances(JsonObjectBuilder builder, KogitoCloudEvent event, JsonObject value) {
        JsonObject jsonValue = new ProcessInstanceMetaMapper().apply(event);
        JsonArrayBuilder piBuilder = Json.createArrayBuilder().add(jsonValue);
        if (value != null) {
            JsonArray array = value.getJsonArray(PROCESS_INSTANCES_DOMAIN_ATTRIBUTE);
            array.stream().filter(json -> !event.getProcessInstanceId().equals(json.asJsonObject().getString("id"))).forEach(json -> piBuilder.add(json));
        }
        builder.add(PROCESS_INSTANCES_DOMAIN_ATTRIBUTE, piBuilder.build());
    }

    public String getModelFromProcessId(String processId) {
        return manager.getProcessIdModelCache().get(processId);
    }
}
