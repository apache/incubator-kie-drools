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
package org.kie.kogito.tracing.event.trace;

import java.util.List;
import java.util.Map;

import org.kie.kogito.tracing.event.message.Message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TraceExecutionStep {

    @JsonProperty("type")
    @JsonInclude(NON_NULL)
    private TraceExecutionStepType type;

    @JsonProperty("duration")
    @JsonInclude(NON_DEFAULT)
    private long duration;

    @JsonProperty("name")
    @JsonInclude(NON_NULL)
    private String name;

    @JsonProperty("result")
    @JsonInclude(NON_NULL)
    private JsonNode result;

    @JsonProperty("messages")
    @JsonInclude(NON_EMPTY)
    private List<? extends Message> messages;

    @JsonProperty("additionalData")
    @JsonInclude(NON_EMPTY)
    private Map<String, String> additionalData;

    @JsonProperty("children")
    @JsonInclude(NON_EMPTY)
    private List<TraceExecutionStep> children;

    private TraceExecutionStep() {
    }

    public TraceExecutionStep(TraceExecutionStepType type, long duration, String name, JsonNode result, List<? extends Message> messages, Map<String, String> additionalData,
            List<TraceExecutionStep> children) {
        this.type = type;
        this.duration = duration;
        this.name = name;
        this.result = result;
        this.messages = messages;
        this.additionalData = additionalData;
        this.children = children;
    }

    public TraceExecutionStepType getType() {
        return type;
    }

    public long getDuration() {
        return duration;
    }

    public String getName() {
        return name;
    }

    public JsonNode getResult() {
        return result;
    }

    public List<? extends Message> getMessages() {
        return messages;
    }

    public Map<String, String> getAdditionalData() {
        return additionalData;
    }

    public List<TraceExecutionStep> getChildren() {
        return children;
    }
}
