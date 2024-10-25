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
package org.kie.kogito.event.serializer;

import java.io.IOException;

import org.kie.kogito.event.process.MultipleProcessInstanceDataEvent;
import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.event.process.ProcessInstanceErrorDataEvent;
import org.kie.kogito.event.process.ProcessInstanceNodeDataEvent;
import org.kie.kogito.event.process.ProcessInstanceSLADataEvent;
import org.kie.kogito.event.process.ProcessInstanceStateDataEvent;
import org.kie.kogito.event.process.ProcessInstanceVariableDataEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class JsonProcessInstanceDataEventDeserializer extends StdDeserializer<ProcessInstanceDataEvent<?>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonProcessInstanceDataEventDeserializer.class);

    private static final long serialVersionUID = 6152014726577574241L;

    public JsonProcessInstanceDataEventDeserializer() {
        this(JsonProcessInstanceDataEventDeserializer.class);
    }

    public JsonProcessInstanceDataEventDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public ProcessInstanceDataEvent<?> deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        LOGGER.debug("Deserialize process instance data event: {}", node);
        String type = node.get("type").asText();

        switch (type) {
            case MultipleProcessInstanceDataEvent.MULTIPLE_TYPE:
                return jp.getCodec().treeToValue(node, MultipleProcessInstanceDataEvent.class);
            case ProcessInstanceErrorDataEvent.ERROR_TYPE:
                return (ProcessInstanceDataEvent<?>) jp.getCodec().treeToValue(node, ProcessInstanceErrorDataEvent.class);
            case ProcessInstanceNodeDataEvent.NODE_TYPE:
                return (ProcessInstanceDataEvent<?>) jp.getCodec().treeToValue(node, ProcessInstanceNodeDataEvent.class);
            case ProcessInstanceSLADataEvent.SLA_TYPE:
                return (ProcessInstanceDataEvent<?>) jp.getCodec().treeToValue(node, ProcessInstanceSLADataEvent.class);
            case ProcessInstanceStateDataEvent.STATE_TYPE:
                return (ProcessInstanceDataEvent<?>) jp.getCodec().treeToValue(node, ProcessInstanceStateDataEvent.class);
            case ProcessInstanceVariableDataEvent.VAR_TYPE:
                return (ProcessInstanceDataEvent<?>) jp.getCodec().treeToValue(node, ProcessInstanceVariableDataEvent.class);
            default:
                LOGGER.warn("Unknown type {} in json data {}", type, node);
                return (ProcessInstanceDataEvent<?>) jp.getCodec().treeToValue(node, ProcessInstanceDataEvent.class);

        }
    }
}