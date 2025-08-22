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
import java.util.HashMap;

import org.kie.kogito.jobs.ExpirationTime;
import org.kie.kogito.jobs.JobDescription;
import org.kie.kogito.jobs.descriptors.ProcessInstanceJobDescription;
import org.kie.kogito.jobs.descriptors.ProcessInstanceJobDescriptionBuilder;
import org.kie.kogito.jobs.descriptors.UserTaskInstanceJobDescription;
import org.kie.kogito.jobs.descriptors.UserTaskInstanceJobDescriptionBuilder;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import static java.util.Optional.ofNullable;

public class JobDescriptionDeserializer extends StdDeserializer<JobDescription> {

    private static final long serialVersionUID = -8307549297456060422L;

    public JobDescriptionDeserializer() {
        super(ProcessInstanceJobDescription.class);
    }

    @Override
    public JobDescription deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JacksonException {
        try {
            JsonNode node = jp.getCodec().readTree(jp);
            String jobDescriptionType = node.get("@type").asText();
            switch (jobDescriptionType) {
                case "ProcessInstanceJobDescription": {
                    ProcessInstanceJobDescriptionBuilder builder = ProcessInstanceJobDescription.newProcessInstanceJobDescriptionBuilder();
                    ofNullable(node.get("id")).ifPresent(e -> builder.id(e.textValue()));
                    ofNullable(node.get("priority")).ifPresent(e -> builder.priority(e.asInt()));
                    String expirationTimeType = node.get("expirationTime").get("@type").asText();
                    builder.expirationTime((ExpirationTime) ctxt.readTreeAsValue(node.get("expirationTime"), Class.forName(expirationTimeType)));

                    ofNullable(node.get("timerId")).ifPresent(e -> builder.timerId(e.textValue()));
                    ofNullable(node.get("processInstanceId")).ifPresent(e -> builder.processInstanceId(e.textValue()));
                    ofNullable(node.get("rootProcessInstanceId")).ifPresent(e -> builder.rootProcessInstanceId(e.textValue()));
                    ofNullable(node.get("processId")).ifPresent(e -> builder.processId(e.textValue()));
                    ofNullable(node.get("rootProcessId")).ifPresent(e -> builder.rootProcessId(e.textValue()));
                    ofNullable(node.get("nodeInstanceId")).ifPresent(e -> builder.nodeInstanceId(e.textValue()));

                    return builder.build();
                }
                case "UserTaskInstanceJobDescription": {
                    UserTaskInstanceJobDescriptionBuilder builder = UserTaskInstanceJobDescription.newUserTaskInstanceJobDescriptionBuilder();
                    ofNullable(node.get("id")).ifPresent(e -> builder.id(e.textValue()));
                    ofNullable(node.get("priority")).ifPresent(e -> builder.priority(e.asInt()));
                    String expirationTimeType = node.get("expirationTime").get("@type").asText();
                    builder.expirationTime((ExpirationTime) ctxt.readTreeAsValue(node.get("expirationTime"), Class.forName(expirationTimeType)));

                    ofNullable(node.get("userTaskInstanceId")).ifPresent(e -> builder.userTaskInstanceId(e.textValue()));
                    var metadata = new HashMap<String, Object>();
                    ofNullable(node.get("processId")).ifPresent(e -> metadata.put("ProcessId", e.textValue()));
                    ofNullable(node.get("processInstanceId")).ifPresent(e -> metadata.put("ProcessInstanceId", e.textValue()));
                    ofNullable(node.get("nodeInstanceId")).ifPresent(e -> metadata.put("NodeInstanceId", e.textValue()));
                    ofNullable(node.get("rootProcessInstanceId")).ifPresent(e -> metadata.put("RootProcessInstanceId", e.textValue()));
                    ofNullable(node.get("rootProcessId")).ifPresent(e -> metadata.put("RootProcessId", e.textValue()));
                    builder.metadata(metadata);
                    return builder.build();
                }
            }
        } catch (ClassNotFoundException e1) {
            throw new IllegalArgumentException("expiration time class not found", e1);
        }
        return null;
    }

}
