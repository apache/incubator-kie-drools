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
package org.kie.kogito.serverless.workflow.parser.handlers;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

import org.kie.kogito.serverless.workflow.parser.ParserContext;

import io.serverlessworkflow.api.functions.FunctionDefinition;
import io.serverlessworkflow.api.functions.FunctionDefinition.Type;

import static org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils.OPERATION_SEPARATOR;

public class ActionResourceFactory {

    private static final Map<Type, BiFunction<String, Optional<ParserContext>, ActionResource>> map =
            Map.of(FunctionDefinition.Type.REST, ActionResourceFactory::justOperation, FunctionDefinition.Type.ASYNCAPI, ActionResourceFactory::justOperation, FunctionDefinition.Type.RPC,
                    ActionResourceFactory::withService);

    private static ActionResource justOperation(String operationStr, Optional<ParserContext> context) {
        String[] tokens = getTokens(operationStr, 2, context);
        return new ActionResource(tokens[0], tokens[1], null);
    }

    private static ActionResource withService(String operationStr, Optional<ParserContext> context) {
        String[] tokens = getTokens(operationStr, 3, context);
        return new ActionResource(tokens[0], tokens[2], tokens[1]);
    }

    private static String[] getTokens(String operationStr, int expectedTokens, Optional<ParserContext> context) {
        String[] tokens = operationStr.split(OPERATION_SEPARATOR);
        if (tokens.length != expectedTokens) {
            String msg = String.format("%s should have just %d %s", operationStr, expectedTokens - 1, OPERATION_SEPARATOR);
            context.ifPresentOrElse(c -> c.addValidationError(msg), () -> {
                throw new IllegalArgumentException(msg);
            });
        }
        return tokens;
    }

    public static ActionResource getActionResource(FunctionDefinition function, Optional<ParserContext> context) {
        BiFunction<String, Optional<ParserContext>, ActionResource> factory = map.get(function.getType());
        if (factory == null) {
            String msg = function.getType() + " does not support action resources";
            context.ifPresentOrElse(c -> c.addValidationError(msg), () -> {
                throw new UnsupportedOperationException(msg);
            });
        }
        String operation = function.getOperation();
        if (operation == null) {
            String msg = "operation string must not be null for function " + function.getName();
            context.ifPresentOrElse(c -> c.addValidationError(msg), () -> {
                throw new IllegalArgumentException(msg);
            });
        }
        return factory.apply(operation, context);
    }

    private ActionResourceFactory() {
    }
}
