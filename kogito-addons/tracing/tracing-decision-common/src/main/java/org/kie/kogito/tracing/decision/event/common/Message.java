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

package org.kie.kogito.tracing.decision.event.common;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.kie.api.builder.Message.Level;
import org.kie.dmn.api.core.DMNMessage;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public class Message {

    private final Level level;
    private final MessageCategory category;
    private final String type;
    private final String sourceId;
    private final String text;
    private final MessageFEELEvent feelEvent;
    private final MessageExceptionField exception;

    public Message(Level level, MessageCategory category, String type, String sourceId, String text, MessageFEELEvent feelEvent, MessageExceptionField exception) {
        this.level = level;
        this.category = category;
        this.type = type;
        this.sourceId = sourceId;
        this.text = text;
        this.feelEvent = feelEvent;
        this.exception = exception;
    }

    public Level getLevel() {
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

    public static Message from(DMNMessage message) {
        if (message == null) {
            return null;
        }
        return new Message(
                message.getLevel(),
                MessageCategory.DMN,
                message.getMessageType().name(),
                message.getSourceId(),
                message.getText(),
                MessageFEELEvent.from(message.getFeelEvent()),
                MessageExceptionField.from(message.getException())
        );
    }

    public static List<Message> from(List<DMNMessage> messages) {
        if (messages == null) {
            return null;
        }
        return messages.stream().map(Message::from).collect(Collectors.toList());
    }

    public static Message from(InternalMessageType message) {
        if (message == null) {
            return null;
        }
        return new Message(
                message.getLevel(),
                MessageCategory.INTERNAL,
                message.name(),
                null,
                message.getText(),
                null,
                null
        );
    }

    public static Message from(InternalMessageType message, Throwable throwable) {
        if (message == null) {
            return null;
        }
        return new Message(
                message.getLevel(),
                MessageCategory.INTERNAL,
                message.name(),
                null,
                message.getText(),
                null,
                MessageExceptionField.from(throwable)
        );
    }
}
