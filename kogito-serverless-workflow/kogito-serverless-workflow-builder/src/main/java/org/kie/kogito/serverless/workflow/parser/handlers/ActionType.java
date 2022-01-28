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

import io.serverlessworkflow.api.functions.FunctionDefinition;

import static org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils.isOpenApiOperation;

enum ActionType {
    REST("rest"),
    SERVICE("service"),
    OPENAPI,
    EXPRESSION,
    SCRIPT("script"),
    SYSOUT("sysout"),
    EMPTY;

    private String[] prefixes;

    private ActionType(String... prefixes) {
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
                return isOpenApiOperation(actionFunction) ? ActionType.OPENAPI : fromMetadata(actionFunction.getMetadata());
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
}
