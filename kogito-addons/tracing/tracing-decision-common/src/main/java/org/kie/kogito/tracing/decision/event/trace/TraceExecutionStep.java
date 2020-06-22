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
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.kie.kogito.tracing.decision.event.common.Message;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

public class TraceExecutionStep {

    @JsonInclude(NON_NULL)
    private final TraceExecutionStepType type;
    @JsonInclude(NON_DEFAULT)
    private final long duration;
    @JsonInclude(NON_NULL)
    private final String name;
    @JsonInclude(NON_NULL)
    private final Object result;
    @JsonInclude(NON_EMPTY)
    private final List<Message> messages;
    @JsonInclude(NON_EMPTY)
    private final Map<String, Object> additionalData;
    @JsonInclude(NON_EMPTY)
    private final List<TraceExecutionStep> children;

    public TraceExecutionStep(TraceExecutionStepType type, long duration, String name, Object result, List<Message> messages, Map<String, Object> additionalData, List<TraceExecutionStep> children) {
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

    public Object getResult() {
        return result;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public Map<String, Object> getAdditionalData() {
        return additionalData;
    }

    public List<TraceExecutionStep> getChildren() {
        return children;
    }
}
