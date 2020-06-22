/*
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.kie.kogito.tracing.decision.event.trace;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import org.kie.kogito.tracing.decision.event.common.Message;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

public class TraceOutputValue {

    private final String id;
    private final String name;
    private final String status;
    private final TraceType type;
    private final JsonNode value;
    @JsonInclude(NON_EMPTY)
    private final List<Message> messages;

    public TraceOutputValue(String id, String name, String status, TraceType type, JsonNode value, List<Message> messages) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.type = type;
        this.value = value;
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

    public TraceType getType() {
        return type;
    }

    public JsonNode getValue() {
        return value;
    }

    public List<Message> getMessages() {
        return messages;
    }
}
