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
package org.kie.kogito.index.service;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.kie.kogito.event.process.NodeDefinition;
import org.kie.kogito.event.process.ProcessDefinitionDataEvent;
import org.kie.kogito.event.process.ProcessDefinitionEventBody;
import org.kie.kogito.index.json.JsonUtils;
import org.kie.kogito.index.model.Node;
import org.kie.kogito.index.model.ProcessDefinition;

import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.enterprise.context.ApplicationScoped;

import static java.util.stream.Collectors.toList;

@ApplicationScoped
public class ProcessDefinitionHelper {

    public static ProcessDefinition merge(ProcessDefinition instance, ProcessDefinitionDataEvent event) {
        if (event == null) {
            return instance;
        }
        ProcessDefinitionEventBody data = event.getData();
        if (data == null) {
            return instance;
        }
        if (instance == null) {
            instance = new ProcessDefinition();
        }
        instance.setId(doMerge(data.getId(), instance.getId()));
        instance.setName(doMerge(data.getName(), instance.getName()));
        instance.setVersion(doMerge(data.getVersion(), instance.getVersion()));
        instance.setAddons(doMerge(data.getAddons(), instance.getAddons()));
        instance.setRoles(doMerge(data.getRoles(), instance.getRoles()));
        instance.setType(doMerge(data.getType(), instance.getType()));
        instance.setEndpoint(doMerge(data.getEndpoint(), instance.getEndpoint()));
        instance.setDescription(doMerge(data.getDescription(), instance.getDescription()));
        instance.setAnnotations(doMerge(data.getAnnotations(), instance.getAnnotations()));
        instance.setMetadata(doMerge(toStringMap(data.getMetadata()), instance.getMetadata()));
        instance.setNodes(doMerge(nodeDefinitions(data), instance.getNodes()));
        return instance;
    }

    private static List<Node> nodeDefinitions(ProcessDefinitionEventBody data) {
        if (data.getNodes() == null && data.getNodes().isEmpty()) {
            return Collections.emptyList();
        }
        return data.getNodes().stream().map(ProcessDefinitionHelper::nodeDefinition).collect(toList());
    }

    private static Node nodeDefinition(NodeDefinition definition) {
        Node node = new Node();
        node.setId(definition.getId());
        node.setName(definition.getName());
        node.setUniqueId(definition.getUniqueId());
        node.setType(definition.getType());
        node.setMetadata(toStringMap(definition.getMetadata()));
        return node;
    }

    private static <T> T doMerge(T incoming, T current) {
        boolean notEmpty = (incoming instanceof Collection) ? !((Collection<?>) incoming).isEmpty() : incoming != null;
        boolean notEquals = !Objects.deepEquals(incoming, current);
        if (notEmpty && notEquals) {
            return incoming;
        }
        return current;
    }

    private static Map<String, String> toStringMap(Map<String, ?> input) {
        if (input == null) {
            return null;
        }
        return input.entrySet().stream()
                .map(entry -> {
                    if (String.class.isInstance(entry.getValue())) {
                        return entry;
                    }
                    String value = null;
                    try {
                        value = JsonUtils.getObjectMapper().writeValueAsString(entry.getValue());
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    return new AbstractMap.SimpleEntry<>(entry.getKey(), value);
                }).collect(Collectors.toMap(Map.Entry::getKey, e -> (String) e.getValue()));
    }
}
