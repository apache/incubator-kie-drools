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

package org.kie.kogito.trusty.storage.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.kie.kogito.tracing.decision.event.common.MessageLevel;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Message {

    public static final String CATEGORY_FIELD = "category";
    public static final String EXCEPTION_FIELD = "exception";
    public static final String LEVEL_FIELD = "level";
    public static final String SOURCE_ID_FIELD = "sourceId";
    public static final String TEXT_FIELD = "text";
    public static final String TYPE_FIELD = "type";

    @JsonProperty(LEVEL_FIELD)
    private MessageLevel level;

    @JsonProperty(CATEGORY_FIELD)
    private String category;

    @JsonProperty(TYPE_FIELD)
    private String type;

    @JsonProperty(SOURCE_ID_FIELD)
    private String sourceId;

    @JsonProperty(TEXT_FIELD)
    private String text;

    @JsonProperty(EXCEPTION_FIELD)
    private MessageExceptionField exception;

    public Message() {
    }

    public Message(MessageLevel level, String category, String type, String sourceId, String text, MessageExceptionField exception) {
        this.level = level;
        this.category = category;
        this.type = type;
        this.sourceId = sourceId;
        this.text = text;
        this.exception = exception;
    }

    public MessageLevel getLevel() {
        return level;
    }

    public void setLevel(MessageLevel level) {
        this.level = level;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public MessageExceptionField getException() {
        return exception;
    }

    public void setException(MessageExceptionField exception) {
        this.exception = exception;
    }
}
