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

import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.ruleflow.core.factory.ActionNodeFactory;
import org.kie.kogito.serverless.workflow.parser.ParserContext;

import com.fasterxml.jackson.databind.JsonNode;

import io.serverlessworkflow.api.functions.FunctionDefinition;
import io.serverlessworkflow.api.functions.FunctionRef;

public class ActionNodeUtils {

    public static <T extends RuleFlowNodeContainerFactory<T, ?>> ActionNodeFactory<T> actionNode(RuleFlowNodeContainerFactory<T, ?> embeddedSubProcess, ParserContext context,
            FunctionDefinition functionDef) {
        return embeddedSubProcess.actionNode(context.newId()).name(functionDef.getName());
    }

    public static void checkArgs(FunctionRef functionRef, String... requiredArgs) {
        JsonNode args = functionRef.getArguments();
        if (args == null) {
            throw new IllegalArgumentException("Arguments cannot be null for function " + functionRef.getRefName());
        }
        for (String arg : requiredArgs) {
            if (!args.has(arg)) {
                throw new IllegalArgumentException("Missing mandatory " + arg + " argument for function " + functionRef.getRefName());
            }
        }
    }

    private ActionNodeUtils() {
    }
}
