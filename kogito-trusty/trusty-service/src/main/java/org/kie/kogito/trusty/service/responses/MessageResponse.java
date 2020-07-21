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

package org.kie.kogito.trusty.service.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.kie.kogito.trusty.storage.api.model.Message;

public class MessageResponse {

    @JsonProperty("level")
    private String level;

    @JsonProperty("category")
    private String category;

    @JsonProperty("type")
    private String type;

    @JsonProperty("sourceId")
    private String sourceId;

    @JsonProperty("text")
    private String text;

    @JsonProperty("exception")
    private MessageExceptionFieldResponse exception;

    private MessageResponse() {
    }

    public MessageResponse(String level, String category, String type, String sourceId, String text, MessageExceptionFieldResponse exception) {
        this.level = level;
        this.category = category;
        this.type = type;
        this.sourceId = sourceId;
        this.text = text;
        this.exception = exception;
    }

    public String getLevel() {
        return level;
    }

    public String getCategory() {
        return category;
    }

    public String getType() {
        return type;
    }

    public String getSourceId() {
        return sourceId;
    }

    public String getText() {
        return text;
    }

    public MessageExceptionFieldResponse getException() {
        return exception;
    }

    public static MessageResponse from(Message message) {
        if (message == null) {
            return null;
        }
        String level = message.getLevel() == null ? null : message.getLevel().name();
        return new MessageResponse(
                level,
                message.getCategory(),
                message.getType(),
                message.getSourceId(),
                message.getText(),
                MessageExceptionFieldResponse.from(message.getException())
        );
    }
}
