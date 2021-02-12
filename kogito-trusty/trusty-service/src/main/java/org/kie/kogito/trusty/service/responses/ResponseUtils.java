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
package org.kie.kogito.trusty.service.responses;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.cloudevents.jackson.JsonFormat;
import org.kie.kogito.trusty.storage.api.model.Decision;
import org.kie.kogito.trusty.storage.api.model.DecisionInput;
import org.kie.kogito.trusty.storage.api.model.DecisionOutcome;
import org.kie.kogito.trusty.storage.api.model.Execution;
import org.kie.kogito.trusty.storage.api.model.Message;
import org.kie.kogito.trusty.storage.api.model.MessageExceptionField;
import org.kie.kogito.trusty.storage.api.model.TypedVariable;

import static org.kie.kogito.tracing.typedvalue.TypedValue.Kind.STRUCTURE;

public class ResponseUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(JsonFormat.getCloudEventJacksonModule());

    public static DecisionOutcomeResponse decisionOutcomeResponseFrom(DecisionOutcome outcome) {
        return outcome == null ? null : new DecisionOutcomeResponse(
                outcome.getOutcomeId(),
                outcome.getOutcomeName(),
                outcome.getEvaluationStatus(),
                typedVariableResponseFrom(outcome.getOutcomeResult()),
                collectionFrom(outcome.getOutcomeInputs(), ResponseUtils::typedVariableResponseFrom),
                collectionFrom(outcome.getMessages(), ResponseUtils::messageResponseFrom),
                outcome.hasErrors()
        );
    }

    public static DecisionOutcomesResponse decisionOutcomesResponseFrom(Decision decision) {
        if (decision == null) {
            return null;
        }
        Collection<DecisionOutcomeResponse> outcomes = decision.getOutcomes() == null ? null : decision.getOutcomes().stream()
                .map(ResponseUtils::decisionOutcomeResponseFrom)
                .collect(Collectors.toList());
        return new DecisionOutcomesResponse(ResponseUtils.executionHeaderResponseFrom(decision), outcomes);
    }

    public static DecisionStructuredInputsResponse decisionStructuredInputsResponseFrom(Collection<DecisionInput> inputs) {
        return inputs == null ? null : new DecisionStructuredInputsResponse(inputs.stream().map(ResponseUtils::typedVariableResponseFrom).collect(Collectors.toList()));
    }

    public static DecisionStructuredInputsResponse decisionStructuredInputsResponseFrom(Decision decision) {
        return decisionStructuredInputsResponseFrom(decision.getInputs());
    }

    public static ExecutionHeaderResponse executionHeaderResponseFrom(Execution execution) {
        OffsetDateTime ldt = OffsetDateTime.ofInstant((Instant.ofEpochMilli(execution.getExecutionTimestamp())), ZoneOffset.UTC);
        return new ExecutionHeaderResponse(execution.getExecutionId(),
                ldt,
                execution.hasSucceeded(),
                execution.getExecutorName(),
                execution.getExecutedModelName(),
                execution.getExecutedModelNamespace(),
                executionTypeFrom(execution.getExecutionType())
        );
    }

    public static ExecutionType executionTypeFrom(org.kie.kogito.trusty.storage.api.model.ExecutionType executionType) {
        switch (executionType) {
            case DECISION:
                return ExecutionType.DECISION;
            case PROCESS:
                return ExecutionType.PROCESS;
        }
        throw new IllegalStateException();
    }

    public static MessageExceptionFieldResponse messageExceptionFieldResponseFrom(MessageExceptionField field) {
        return field == null ? null : new MessageExceptionFieldResponse(
                field.getClassName(),
                field.getMessage(),
                messageExceptionFieldResponseFrom(field.getCause())
        );
    }

    public static MessageResponse messageResponseFrom(Message message) {
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
                messageExceptionFieldResponseFrom(message.getException())
        );
    }

    public static TypedVariableResponse typedVariableResponseFrom(DecisionInput input) {
        return input != null ? typedVariableResponseFrom(input.getValue()) : null;
    }

    public static TypedVariableResponse typedVariableResponseFrom(TypedVariable value) {
        if (value == null) {
            return null;
        }

        switch (value.getKind()) {
            case COLLECTION:
                return typedVariableResponseFromCollection(value);
            case STRUCTURE:
                return typedVariableResponseFromStructure(value);
            case UNIT:
                return typedVariableResponseFromUnit(value);
        }

        throw new IllegalStateException(String.format("TypedVariable of kind %s can't be converted to TypedVariableResponse", value.getKind()));
    }

    private static TypedVariableResponse typedVariableResponseFromCollection(TypedVariable value) {
        boolean isCollectionOfStructures = value.getComponents() != null && value.getComponents().stream().anyMatch(t -> t.getKind() == STRUCTURE);

        // create array of all the values of the components
        // to be placed in the "value" field of the response
        // only if this **is not** a collection of structures
        JsonNode responseValue = (isCollectionOfStructures || value.getComponents() == null)
                ? null
                : value.getComponents().stream()
                        .map(ResponseUtils::typedVariableResponseFromUnit)
                        .map(TypedVariableResponse::getValue)
                        .collect(OBJECT_MAPPER::createArrayNode, ArrayNode::add, ArrayNode::addAll);

        // create a list of lists of variables with all the values of the sub-components
        // to be placed in the "components" field of the response
        // only if this **is** a collection of structures
        List<JsonNode> responseComponents = (!isCollectionOfStructures || value.getComponents() == null)
                ? null
                : value.getComponents().stream()
                        .map(ResponseUtils::typedVariableResponseFromStructure)
                        .map(r -> r.getComponents().stream().collect(OBJECT_MAPPER::createArrayNode, ArrayNode::add, ArrayNode::addAll))
                        .collect(Collectors.toList());

        return new TypedVariableResponse(value.getName(), value.getTypeRef(), responseValue, responseComponents);
    }

    private static TypedVariableResponse typedVariableResponseFromStructure(TypedVariable value) {
        List<JsonNode> components = value.getComponents() == null
                ? null
                : value.getComponents().stream().map(ResponseUtils::typedVariableResponseFrom).<JsonNode>map(OBJECT_MAPPER::valueToTree).collect(Collectors.toList());
        return new TypedVariableResponse(value.getName(), value.getTypeRef(), null, components);
    }

    private static TypedVariableResponse typedVariableResponseFromUnit(TypedVariable value) {
        return new TypedVariableResponse(value.getName(), value.getTypeRef(), value.getValue(), null);
    }

    private static <T, U> Collection<U> collectionFrom(Collection<T> input, Function<T, U> mapper) {
        return input == null ? null : input.stream().map(mapper).collect(Collectors.toList());
    }

    private ResponseUtils() {
    }

}
