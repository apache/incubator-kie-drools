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
package org.kie.kogito.index.json;

import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceDataEvent;
import org.kie.kogito.jackson.utils.JsonObjectUtils;
import org.kie.kogito.jackson.utils.MergeUtils;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.cloudevents.jackson.JsonFormat;

public final class JsonUtils {

    private static final ObjectMapper MAPPER = configure(new ObjectMapper());

    private JsonUtils() {
    }

    public static ObjectMapper getObjectMapper() {
        return MAPPER;
    }

    public static ObjectMapper configure(ObjectMapper objectMapper) {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(JsonFormat.getCloudEventJacksonModule());
        objectMapper.registerModule(new JavaTimeModule());

        SimpleModule module = new SimpleModule("Kogito Cloud Events");
        module.addDeserializer(ProcessInstanceDataEvent.class, new JsonProcessInstanceDataEventDeserializer());
        module.addDeserializer(UserTaskInstanceDataEvent.class, new JsonUserTaskInstanceDataEventDeserializer());
        objectMapper.registerModule(module);
        return objectMapper;
    }

    public static ObjectNode mergeVariable(String variableName, Object variableValue, ObjectNode variables) {
        return (ObjectNode) MergeUtils.merge(createObjectNode(variableName, variableValue), variables);
    }

    private static ObjectNode createObjectNode(String variableName, Object variableValue) {
        int indexOf = variableName.indexOf('.');
        ObjectNode result = ObjectMapperFactory.get().createObjectNode();
        if (indexOf == -1) {
            result.set(variableName, JsonObjectUtils.fromValue(variableValue));
        } else {
            String name = variableName.substring(0, indexOf);
            result.set(name, createObjectNode(variableName.substring(indexOf + 1), variableValue));
        }
        return result;
    }
}
