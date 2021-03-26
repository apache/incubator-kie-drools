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

package org.kie.kogito.trusty.service.common.messaging.incoming;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.kie.kogito.tracing.decision.event.message.MessageLevel;
import org.kie.kogito.tracing.decision.event.trace.TraceEvent;
import org.kie.kogito.tracing.decision.event.trace.TraceInputValue;
import org.kie.kogito.tracing.decision.event.trace.TraceOutputValue;
import org.kie.kogito.tracing.typedvalue.TypedValue;
import org.kie.kogito.trusty.storage.api.model.Decision;
import org.kie.kogito.trusty.storage.api.model.DecisionInput;
import org.kie.kogito.trusty.storage.api.model.DecisionOutcome;
import org.kie.kogito.trusty.storage.api.model.Message;
import org.kie.kogito.trusty.storage.api.model.MessageExceptionField;
import org.kie.kogito.trusty.storage.api.model.TypedVariable;

public class TraceEventConverter {

    private TraceEventConverter() {
    }

    public static Decision toDecision(TraceEvent event, String sourceUrl) {

        List<DecisionInput> inputs = event.getInputs() == null
                ? null
                : event.getInputs().stream().map(TraceEventConverter::toInput).collect(Collectors.toList());

        List<DecisionOutcome> outcomes = event.getOutputs() == null
                ? null
                : event.getOutputs().stream().map(TraceEventConverter::toOutcome).collect(Collectors.toList());

        return new Decision(
                event.getHeader().getExecutionId(),
                sourceUrl,
                event.getHeader().getStartTimestamp(),
                decisionHasSucceeded(event.getOutputs()),
                null,
                event.getHeader().getResourceId().getModelName(),
                event.getHeader().getResourceId().getModelNamespace(),
                inputs,
                outcomes);
    }

    public static DecisionInput toInput(TraceInputValue eventInput) {
        return new DecisionInput(eventInput.getId(), eventInput.getName(), toTypedVariable(eventInput.getName(), eventInput.getValue()));
    }

    public static TypedVariable toTypedVariable(String name, TypedValue typedValue) {
        if (typedValue == null) {
            return TypedVariable.buildUnit(name, null, null);
        }
        switch (typedValue.getKind()) {
            case STRUCTURE:
                return TypedVariable.buildStructure(
                        name,
                        typedValue.getType(),
                        Optional.ofNullable(typedValue.toStructure().getValue())
                                .map(v -> v.entrySet().stream()
                                        .map(e -> toTypedVariable(e.getKey(), e.getValue()))
                                        .collect(Collectors.toList()))
                                .orElse(null));
            case COLLECTION:
                return TypedVariable.buildCollection(
                        name,
                        typedValue.getType(),
                        Optional.ofNullable(typedValue.toCollection().getValue())
                                .map(v -> v.stream()
                                        .map(x -> toTypedVariable(null, x))
                                        .collect(Collectors.toList()))
                                .orElse(null));
            case UNIT:
                return TypedVariable.buildUnit(
                        name,
                        typedValue.getType(),
                        typedValue.toUnit().getValue());
        }
        throw new IllegalStateException("Unsupported TypedVariable of kind " + typedValue.getKind());
    }

    public static DecisionOutcome toOutcome(TraceOutputValue eventOutput) {
        return new DecisionOutcome(
                eventOutput.getId(),
                eventOutput.getName(),
                eventOutput.getStatus(),
                toTypedVariable(eventOutput.getName(), eventOutput.getValue()),
                eventOutput.getInputs() == null ? null : eventOutput.getInputs().entrySet().stream().map(e -> toTypedVariable(e.getKey(), e.getValue())).collect(Collectors.toList()),
                eventOutput.getMessages() == null ? null : eventOutput.getMessages().stream().map(TraceEventConverter::toMessage).collect(Collectors.toList()));
    }

    public static Message toMessage(org.kie.kogito.tracing.decision.event.message.Message eventMessage) {
        return new Message(
                eventMessage.getLevel(),
                eventMessage.getCategory() == null ? null : eventMessage.getCategory().name(),
                eventMessage.getType(),
                eventMessage.getSourceId(),
                eventMessage.getText(),
                toMessageExceptionField(eventMessage.getException()));
    }

    public static MessageExceptionField toMessageExceptionField(org.kie.kogito.tracing.decision.event.message.MessageExceptionField eventException) {
        return eventException == null
                ? null
                : new MessageExceptionField(eventException.getClassName(), eventException.getMessage(), toMessageExceptionField(eventException.getCause()));
    }

    public static boolean decisionHasSucceeded(List<TraceOutputValue> outputs) {
        return outputs != null && outputs.stream().noneMatch(o -> "failed".equalsIgnoreCase(o.getStatus()) || messageListHasErrors(o.getMessages()));
    }

    private static boolean messageListHasErrors(List<org.kie.kogito.tracing.decision.event.message.Message> messages) {
        return messages != null && messages.stream().anyMatch(m -> m.getLevel() == MessageLevel.ERROR);
    }
}
