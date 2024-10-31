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

    public static boolean checkArgs(ParserContext context, FunctionRef functionRef, String... requiredArgs) {
        JsonNode args = functionRef.getArguments();
        boolean isOk = true;
        if (args == null) {
            context.addValidationError("Arguments cannot be null for function " + functionRef.getRefName());
            isOk = false;
        } else {
            for (String arg : requiredArgs) {
                if (!args.has(arg)) {
                    context.addValidationError("Missing mandatory " + arg + " argument for function " + functionRef.getRefName());
                    isOk = false;
                }
            }
        }
        return isOk;
    }

    private ActionNodeUtils() {
    }
}
