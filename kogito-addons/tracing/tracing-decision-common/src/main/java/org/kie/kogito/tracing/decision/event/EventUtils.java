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

package org.kie.kogito.tracing.decision.event;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.kogito.tracing.decision.event.common.InternalMessageType;
import org.kie.kogito.tracing.decision.event.common.Message;
import org.kie.kogito.tracing.decision.event.common.MessageCategory;
import org.kie.kogito.tracing.decision.event.common.MessageExceptionField;
import org.kie.kogito.tracing.decision.event.common.MessageFEELEvent;
import org.kie.kogito.tracing.decision.event.common.MessageFEELEventSeverity;
import org.kie.kogito.tracing.decision.event.common.MessageLevel;
import org.kie.kogito.tracing.decision.event.trace.TraceResourceId;
import org.kie.kogito.tracing.decision.event.trace.TraceType;

public class EventUtils {

    public static <I, O> List<O> map(List<I> input, Function<I, O> mapper) {
        return input == null
                ? null
                : input.stream().map(mapper).collect(Collectors.toList());
    }

    public static Message messageFrom(DMNMessage message) {
        if (message == null) {
            return null;
        }
        return new Message(
                messageLevelFrom(message.getLevel()),
                MessageCategory.DMN,
                message.getMessageType().name(),
                message.getSourceId(),
                message.getText(),
                messageFEELEventFrom(message.getFeelEvent()),
                messageExceptionFieldFrom(message.getException())
        );
    }

    public static Message messageFrom(InternalMessageType message) {
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

    public static Message messageFrom(InternalMessageType message, Throwable throwable) {
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
                messageExceptionFieldFrom(throwable)
        );
    }

    public static MessageExceptionField messageExceptionFieldFrom(Throwable throwable) {
        if (throwable == null) {
            return null;
        }
        return new MessageExceptionField(throwable.getClass().getName(), throwable.getMessage(), messageExceptionFieldFrom(throwable.getCause()));
    }

    public static MessageFEELEvent messageFEELEventFrom(FEELEvent feelEvent) {
        if (feelEvent == null) {
            return null;
        }
        return new MessageFEELEvent(
                messageFEELEventSeverityFrom(feelEvent.getSeverity()),
                feelEvent.getMessage(),
                feelEvent.getLine(),
                feelEvent.getColumn(),
                messageExceptionFieldFrom(feelEvent.getSourceException())
        );
    }

    public static MessageFEELEventSeverity messageFEELEventSeverityFrom(FEELEvent.Severity severity) {
        if (severity == null) {
            return null;
        }
        switch (severity) {
            case TRACE:
                return MessageFEELEventSeverity.TRACE;
            case INFO:
                return MessageFEELEventSeverity.INFO;
            case WARN:
                return MessageFEELEventSeverity.WARN;
            case ERROR:
                return MessageFEELEventSeverity.ERROR;
            default:
                throw new IllegalArgumentException(String.format("Can't convert FEELEvent.Severity.%s to MessageFEELEventSeverity", severity.name()));
        }
    }

    public static MessageLevel messageLevelFrom(org.kie.api.builder.Message.Level level) {
        if (level == null) {
            return null;
        }
        switch (level) {
            case ERROR:
                return MessageLevel.ERROR;
            case WARNING:
                return MessageLevel.WARNING;
            case INFO:
                return MessageLevel.INFO;
            default:
                throw new IllegalArgumentException(String.format("Can't convert Message.Level.%s to MessageLevel", level.name()));
        }
    }

    public static TraceResourceId traceResourceIdFrom(DMNModel model) {
        if (model == null) {
            return null;
        }
        return new TraceResourceId(model.getNamespace(), model.getName());
    }

    public static TraceType traceTypeFrom(DMNType dmnType) {
        return new TraceType(dmnType.getId(), dmnType.getNamespace(), dmnType.getName());
    }

    private EventUtils() {}
}
