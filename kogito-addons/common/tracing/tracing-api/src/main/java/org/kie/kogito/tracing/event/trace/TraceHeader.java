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

import org.kie.kogito.tracing.event.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TraceHeader {

    private static final Logger LOGGER = LoggerFactory.getLogger(TraceHeader.class);

    @JsonProperty("type")
    private TraceEventType type;

    @JsonProperty("executionId")
    private String executionId;

    @JsonProperty("startTimestamp")
    @JsonInclude(NON_NULL)
    private Long startTimestamp;

    @JsonProperty("endTimestamp")
    @JsonInclude(NON_NULL)
    private Long endTimestamp;

    @JsonProperty("duration")
    @JsonInclude(NON_NULL)
    private Long duration;

    @JsonProperty("resourceId")
    private TraceResourceId resourceId;

    @JsonProperty("messages")
    @JsonInclude(NON_EMPTY)
    private List<Message> messages;

    private TraceHeader() {
    }

    public TraceHeader(TraceEventType type, String executionId, Long startTs, Long endTs, Long duration, TraceResourceId resourceId, List<Message> messages) {
        checkAndLogInvalidTimestamp(startTs, "startTimestamp", executionId);
        checkAndLogInvalidTimestamp(endTs, "endTimestamp", executionId);
        checkAndLogInvalidTimestamp(duration, "duration", executionId);
        this.type = type;
        this.executionId = executionId;
        this.startTimestamp = startTs;
        this.endTimestamp = endTs;
        this.duration = duration;
        this.resourceId = resourceId;
        this.messages = messages;
    }

    public TraceEventType getType() {
        return type;
    }

    public String getExecutionId() {
        return executionId;
    }

    public Long getStartTimestamp() {
        return startTimestamp;
    }

    public Long getEndTimestamp() {
        return endTimestamp;
    }

    public Long getDuration() {
        return duration;
    }

    public TraceResourceId getResourceId() {
        return resourceId;
    }

    public List<Message> getMessages() {
        return messages;
    }

    private void checkAndLogInvalidTimestamp(Long timestamp, String property, String executionId) {
        if (timestamp == null || timestamp < 0) {
            LOGGER.warn(String.format("The TraceHeader timestamp property %s of the execution %s is null or negative: %d", property, executionId, timestamp));
        }
    }
}
