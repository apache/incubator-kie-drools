/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.serverless.workflow.parser.handlers;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import io.serverlessworkflow.api.functions.FunctionDefinition;

import static org.kie.kogito.internal.utils.ConversionUtils.isEmpty;
import static org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils.OPERATION_SEPARATOR;

public enum ActionType {
    REST("rest"),
    SERVICE("service"),
    OPENAPI(ActionType::justOperation),
    EXPRESSION,
    SCRIPT("script"),
    SYSOUT("sysout"),
    RPC(ActionType::withService),
    EMPTY;

    private final String[] prefixes;
    private Optional<Function<String, ActionResource>> actionResourceFactory;

    private ActionType(String... prefixes) {
        this(null, prefixes);
    }

    private ActionType(Function<String, ActionResource> actionResourceFactory, String... prefixes) {
        this.actionResourceFactory = Optional.ofNullable(actionResourceFactory);
        this.prefixes = prefixes;
    }

    public String getOperation(FunctionDefinition function) {
        String operation = function.getOperation();
        return prefixes.length == 0 || operation == null ? operation : checkPrefix(operation);

    }

    private String checkPrefix(String operation) {
        for (String prefix : prefixes) {
            if (operation.startsWith(prefix)) {
                return operation.substring(operation.length() > prefix.length() && operation.charAt(prefix.length()) == ':' ? prefix.length() + 1 : prefix.length());
            }
        }
        return operation;
    }

    public static ActionType from(FunctionDefinition actionFunction) {
        switch (actionFunction.getType()) {
            case REST:
                return isEmpty(actionFunction.getOperation()) ? fromMetadata(actionFunction.getMetadata()) : ActionType.OPENAPI;
            case RPC:
                return ActionType.RPC;
            case EXPRESSION:
                return ActionType.EXPRESSION;
            case CUSTOM:
                return fromOperation(actionFunction.getOperation());
            default:
                throw new UnsupportedOperationException(actionFunction.getType() + " is not supported yet");
        }
    }

    private static ActionType fromOperation(String operation) {
        for (ActionType value : values()) {
            for (String prefix : value.prefixes) {
                if (operation.startsWith(prefix)) {
                    return value;
                }
            }
        }
        throw new UnsupportedOperationException("Unable to recognize custom format " + operation + ", supported custom formats are " + values());
    }

    /* This code is kept for backward compatibility with old workflow files, metadata should not be used for type resolution */
    private static ActionType fromMetadata(Map<String, String> metadata) {
        String type = metadata != null ? metadata.get("type") : null;
        if (type != null) {
            try {
                return ActionType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException ex) {
                // see return 
            }
        }
        return ActionType.EMPTY;
    }

    private static ActionResource justOperation(String operationStr) {
        String[] tokens = getTokens(operationStr, 2);
        return new ActionResource(tokens[0], tokens[1], null);
    }

    private static ActionResource withService(String operationStr) {
        String[] tokens = getTokens(operationStr, 3);
        return new ActionResource(tokens[0], tokens[2], tokens[1]);
    }

    private static String[] getTokens(String operationStr, int expectedTokens) {
        String[] tokens = operationStr.split(OPERATION_SEPARATOR);
        if (tokens.length != expectedTokens) {
            throw new IllegalArgumentException(String.format("%s should have just %d %s", operationStr, expectedTokens - 1, OPERATION_SEPARATOR));
        }
        return tokens;
    }

    public ActionResource getActionResource(FunctionDefinition function) {
        return actionResourceFactory.map(factory -> factory.apply(function.getOperation())).orElseThrow(() -> new UnsupportedOperationException(this.name() + " does not support action resources"));
    }
}
