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
import java.util.function.Function;

import io.serverlessworkflow.api.functions.FunctionDefinition;
import io.serverlessworkflow.api.functions.FunctionDefinition.Type;

import static org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils.OPERATION_SEPARATOR;

public class ActionResourceFactory {

    private static final Map<Type, Function<String, ActionResource>> map =
            Map.of(FunctionDefinition.Type.REST, ActionResourceFactory::justOperation, FunctionDefinition.Type.ASYNCAPI, ActionResourceFactory::justOperation, FunctionDefinition.Type.RPC,
                    ActionResourceFactory::withService);

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

    public static ActionResource getActionResource(FunctionDefinition function) {
        Function<String, ActionResource> factory = map.get(function.getType());
        if (factory == null) {
            throw new UnsupportedOperationException(function.getType() + " does not support action resources");
        }
        String operation = function.getOperation();
        if (operation == null) {
            throw new IllegalArgumentException("operation string must not be null for function " + function.getName());
        }
        return factory.apply(operation);
    }

    private ActionResourceFactory() {
    }
}
