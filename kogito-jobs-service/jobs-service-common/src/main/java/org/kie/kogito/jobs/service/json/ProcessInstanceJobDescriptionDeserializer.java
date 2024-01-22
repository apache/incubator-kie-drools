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
package org.kie.kogito.jobs.service.json;

import java.io.IOException;

import org.kie.kogito.jobs.ExpirationTime;
import org.kie.kogito.jobs.ProcessInstanceJobDescription;
import org.kie.kogito.jobs.ProcessInstanceJobDescriptionBuilder;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import static java.util.Optional.ofNullable;

public class ProcessInstanceJobDescriptionDeserializer extends StdDeserializer<ProcessInstanceJobDescription> {

    private static final long serialVersionUID = -8307549297456060422L;

    public ProcessInstanceJobDescriptionDeserializer() {
        super(ProcessInstanceJobDescription.class);
    }

    @Override
    public ProcessInstanceJobDescription deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JacksonException {
        ProcessInstanceJobDescriptionBuilder builder = ProcessInstanceJobDescription.builder();

        JsonNode node = jp.getCodec().readTree(jp);
        ofNullable(node.get("id")).ifPresent(e -> builder.id(e.textValue()));
        ofNullable(node.get("timerId")).ifPresent(e -> builder.timerId(e.textValue()));
        ofNullable(node.get("priority")).ifPresent(e -> builder.priority(e.asInt()));
        ofNullable(node.get("processInstanceId")).ifPresent(e -> builder.processInstanceId(e.textValue()));
        ofNullable(node.get("rootProcessInstanceId")).ifPresent(e -> builder.rootProcessInstanceId(e.textValue()));
        ofNullable(node.get("processId")).ifPresent(e -> builder.processId(e.textValue()));
        ofNullable(node.get("rootProcessId")).ifPresent(e -> builder.rootProcessId(e.textValue()));
        ofNullable(node.get("nodeInstanceId")).ifPresent(e -> builder.nodeInstanceId(e.textValue()));

        String type = node.get("expirationTime").get("@type").asText();
        try {
            builder.expirationTime((ExpirationTime) ctxt.readTreeAsValue(node.get("expirationTime"), Class.forName(type)));
        } catch (ClassNotFoundException | IOException e1) {
            e1.printStackTrace();
        }

        return builder.build();
    }

}
