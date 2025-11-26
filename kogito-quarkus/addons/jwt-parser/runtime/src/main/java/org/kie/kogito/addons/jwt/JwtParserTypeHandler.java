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
package org.kie.kogito.addons.jwt;

import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.ruleflow.core.factory.WorkItemNodeFactory;
import org.kie.kogito.serverless.workflow.parser.ParserContext;
import org.kie.kogito.serverless.workflow.parser.types.WorkItemTypeHandler;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.functions.FunctionDefinition;

import static org.kie.kogito.serverless.workflow.parser.FunctionTypeHandlerFactory.trimCustomOperation;

/**
 * Function type handler for JWT parsing operations in SonataFlow
 * Handles custom functions with type "jwt-parser"
 */
public class JwtParserTypeHandler extends WorkItemTypeHandler {

    @Override
    protected <T extends RuleFlowNodeContainerFactory<T, ?>> WorkItemNodeFactory<T> fillWorkItemHandler(
            Workflow workflow, ParserContext context, WorkItemNodeFactory<T> node, FunctionDefinition functionDef) {

        String operation = trimCustomOperation(functionDef);
        if (operation != null && !operation.isEmpty()) {
            node.workParameter(JwtParserWorkItemHandler.OPERATION_PARAM, operation);
        }

        return node.workName(JwtParserWorkItemHandler.NAME);
    }

    @Override
    public String type() {
        return JwtParserWorkItemHandler.NAME;
    }
}
