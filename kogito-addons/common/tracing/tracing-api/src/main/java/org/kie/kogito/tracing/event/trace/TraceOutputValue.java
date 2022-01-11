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
import org.kie.kogito.tracing.typedvalue.TypedValue;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TraceOutputValue {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("status")
    private String status;

    @JsonProperty("value")
    private TypedValue value;

    @JsonProperty("inputs")
    @JsonInclude(NON_EMPTY)
    private Map<String, TypedValue> inputs;

    @JsonProperty("messages")
    @JsonInclude(NON_EMPTY)
    private List<Message> messages;

    private TraceOutputValue() {
    }

    public TraceOutputValue(String id, String name, String status, TypedValue value, Map<String, TypedValue> inputs, List<Message> messages) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.value = value;
        this.inputs = inputs;
        this.messages = messages;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public TypedValue getValue() {
        return value;
    }

    public Map<String, TypedValue> getInputs() {
        return inputs;
    }

    public List<Message> getMessages() {
        return messages;
    }
}
