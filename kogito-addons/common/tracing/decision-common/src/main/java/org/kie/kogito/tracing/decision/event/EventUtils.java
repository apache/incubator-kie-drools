/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.tracing.decision.event;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.util.BuiltInTypeUtils;
import org.kie.kogito.event.cloudevents.utils.CloudEventUtils;
import org.kie.kogito.tracing.decision.message.InternalMessageType;
import org.kie.kogito.tracing.event.message.Message;
import org.kie.kogito.tracing.event.message.MessageCategory;
import org.kie.kogito.tracing.event.message.MessageExceptionField;
import org.kie.kogito.tracing.event.message.MessageFEELEvent;
import org.kie.kogito.tracing.event.message.MessageFEELEventSeverity;
import org.kie.kogito.tracing.event.message.MessageLevel;
import org.kie.kogito.tracing.event.message.models.DecisionMessage;
import org.kie.kogito.tracing.event.trace.TraceResourceId;
import org.kie.kogito.tracing.typedvalue.CollectionValue;
import org.kie.kogito.tracing.typedvalue.StructureValue;
import org.kie.kogito.tracing.typedvalue.TypedValue;
import org.kie.kogito.tracing.typedvalue.UnitValue;

import com.fasterxml.jackson.databind.JsonNode;

public class EventUtils {

    public static JsonNode jsonNodeFrom(Object object) {
        return Optional.ofNullable(object).<JsonNode> map(CloudEventUtils.Mapper.mapper()::valueToTree).orElse(null);
    }

    public static <I, O> List<O> map(List<I> input, Function<I, O> mapper) {
        return input == null
                ? null
                : input.stream().map(mapper).collect(Collectors.toList());
    }

    public static Message messageFrom(DMNMessage message) {
        if (message == null) {
            return null;
        }
        return new DecisionMessage(
                messageLevelFrom(message.getLevel()),
                MessageCategory.DMN,
                message.getMessageType().name(),
                message.getSourceId(),
                message.getText(),
                messageFEELEventFrom(message.getFeelEvent()),
                messageExceptionFieldFrom(message.getException()));
    }

    public static Message messageFrom(InternalMessageType message) {
        if (message == null) {
            return null;
        }
        return new DecisionMessage(
                message.getLevel(),
                MessageCategory.INTERNAL,
                message.name(),
                null,
                message.getText(),
                null,
                null);
    }

    public static Message messageFrom(InternalMessageType message, Throwable throwable) {
        if (message == null) {
            return null;
        }
        return new DecisionMessage(
                message.getLevel(),
                MessageCategory.INTERNAL,
                message.name(),
                null,
                message.getText(),
                null,
                messageExceptionFieldFrom(throwable));
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
                messageExceptionFieldFrom(feelEvent.getSourceException()));
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

    public static <T> Stream<T> streamFrom(Iterator<T> iterator) {
        Iterable<T> iterable = () -> iterator;
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    public static TraceResourceId traceResourceIdFrom(String serviceUrl, DMNModel model) {
        if (model == null) {
            return null;
        }
        return new TraceResourceId(serviceUrl, model.getNamespace(), model.getName());
    }

    public static TypedValue typedValueFrom(Object value) {
        return typedValueFromJsonNode(jsonNodeFrom(value), BuiltInTypeUtils.determineTypeFromInstance(value));
    }

    public static TypedValue typedValueFrom(DMNType type, Object value) {
        return typedValueFromJsonNode(type, jsonNodeFrom(value), BuiltInTypeUtils.determineTypeFromInstance(value));
    }

    static TypedValue typedValueFromJsonNode(JsonNode value, Type suggestedType) {
        if (value != null && value.isObject()) {
            return new StructureValue(
                    typeOf(value).getName(),
                    streamFrom(value.fields())
                            .collect(HashMap::new, (map, entry) -> map.put(entry.getKey(), typedValueFromJsonNode(entry.getValue(), null)), HashMap::putAll));
        }
        if (value != null && value.isArray()) {
            return new CollectionValue(
                    typeOf(value).getName(),
                    streamFrom(value.elements()).map(v -> typedValueFromJsonNode(v, null)).collect(Collectors.toList()));
        }
        Type finalType = Optional.ofNullable(suggestedType).orElseGet(() -> typeOf(value));
        return new UnitValue(finalType.getName(), value);
    }

    static TypedValue typedValueFromJsonNode(DMNType type, JsonNode value, Type suggestedType) {
        if (type == null) {
            return typedValueFromJsonNode(value, suggestedType);
        }
        if (value != null && value.isObject()) {
            return new StructureValue(
                    type.getName(),
                    streamFrom(value.fields())
                            .collect(HashMap::new, (map, entry) -> map.put(entry.getKey(), typedValueFromJsonNode(type.getFields().get(entry.getKey()), entry.getValue(), null)), HashMap::putAll));
        }
        if (value != null && value.isArray()) {
            return new CollectionValue(
                    type.getName(),
                    streamFrom(value.elements()).map(element -> typedValueFromJsonNode(type, element, null)).collect(Collectors.toList()));
        }
        return new UnitValue(type.getName(), baseTypeOf(type), value);
    }

    static String baseTypeOf(DMNType type) {
        if (type == null || type.getBaseType() == null) {
            return null;
        }
        return type.getBaseType().getId() == null ? type.getBaseType().getName() : baseTypeOf(type.getBaseType());
    }

    static Type typeOf(JsonNode value) {
        if (value == null) {
            return BuiltInType.UNKNOWN;
        }
        if (value.isArray()) {
            return BuiltInType.LIST;
        }
        if (value.isBoolean()) {
            return BuiltInType.BOOLEAN;
        }
        if (value.isNumber()) {
            return BuiltInType.NUMBER;
        }
        if (value.isTextual()) {
            return BuiltInType.STRING;
        }
        return BuiltInType.UNKNOWN;
    }

    private EventUtils() {
    }
}
