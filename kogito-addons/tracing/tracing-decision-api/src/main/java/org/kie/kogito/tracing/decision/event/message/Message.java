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

package org.kie.kogito.tracing.decision.event.message;

import com.fasterxml.jackson.annotation.JsonInclude;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public class Message {

    private MessageLevel level;
    private MessageCategory category;
    private String type;
    private String sourceId;
    private String text;
    private MessageFEELEvent feelEvent;
    private MessageExceptionField exception;

    private Message() {
    }

    public Message(MessageLevel level, MessageCategory category, String type, String sourceId, String text, MessageFEELEvent feelEvent, MessageExceptionField exception) {
        this.level = level;
        this.category = category;
        this.type = type;
        this.sourceId = sourceId;
        this.text = text;
        this.feelEvent = feelEvent;
        this.exception = exception;
    }

    public MessageLevel getLevel() {
        return level;
    }

    public MessageCategory getCategory() {
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

    public MessageFEELEvent getFeelEvent() {
        return feelEvent;
    }

    public MessageExceptionField getException() {
        return exception;
    }
}
