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

package org.kie.kogito.trusty.service.messaging;

import java.util.List;
import java.util.stream.Collectors;

import org.kie.kogito.tracing.decision.event.trace.TraceEvent;
import org.kie.kogito.tracing.decision.event.trace.TraceInputValue;
import org.kie.kogito.tracing.decision.event.trace.TraceOutputValue;
import org.kie.kogito.trusty.storage.api.model.Decision;
import org.kie.kogito.trusty.storage.api.model.DecisionOutcome;
import org.kie.kogito.trusty.storage.api.model.Message;
import org.kie.kogito.trusty.storage.api.model.MessageExceptionField;
import org.kie.kogito.trusty.storage.api.model.TypedValue;

public class TraceEventConverter {

    public static Decision toDecision(TraceEvent event) {

        List<TypedValue> inputs = event.getInputs() == null
                ? null
                : event.getInputs().stream().map(TraceEventConverter::toInput).collect(Collectors.toList());

        List<DecisionOutcome> outcomes = event.getOutputs() == null
                ? null
                : event.getOutputs().stream().map(TraceEventConverter::toOutcome).collect(Collectors.toList());

        return new Decision(
                event.getHeader().getExecutionId(),
                event.getHeader().getStartTimestamp(),
                null,
                null,
                event.getHeader().getResourceId().getModelName(),
                inputs,
                outcomes
        );
    }

    public static TypedValue toInput(TraceInputValue eventInput) {
        return new TypedValue(
                eventInput.getName(),
                eventInput.getType().getId(),
                eventInput.getValue()
        );
    }

    public static DecisionOutcome toOutcome(TraceOutputValue eventOutput) {
        return new DecisionOutcome(
                eventOutput.getId(),
                eventOutput.getName(),
                eventOutput.getStatus(),
                new TypedValue(eventOutput.getName(), eventOutput.getType().getId(), eventOutput.getValue()),
                null,
                eventOutput.getMessages() == null ? null : eventOutput.getMessages().stream().map(TraceEventConverter::toMessage).collect(Collectors.toList())
        );
    }

    public static Message toMessage(org.kie.kogito.tracing.decision.event.common.Message eventMessage) {
        return new Message(
                eventMessage.getLevel(),
                eventMessage.getCategory() == null ? null : eventMessage.getCategory().name(),
                eventMessage.getType(),
                eventMessage.getSourceId(),
                eventMessage.getText(),
                toMessageExceptionField(eventMessage.getException())
        );
    }

    public static MessageExceptionField toMessageExceptionField(org.kie.kogito.tracing.decision.event.common.MessageExceptionField eventException) {
        return eventException == null
                ? null
                : new MessageExceptionField(eventException.getClassName(), eventException.getMessage(), toMessageExceptionField(eventException.getCause()));
    }

    private TraceEventConverter() {
    }
}
