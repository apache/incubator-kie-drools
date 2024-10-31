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
package org.kie.kogito.serverless.workflow.parser.types;

import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.ruleflow.core.factory.ActionNodeFactory;
import org.jbpm.ruleflow.core.factory.NodeFactory;
import org.kie.kogito.serverless.workflow.parser.FunctionTypeHandlerFactory;
import org.kie.kogito.serverless.workflow.parser.ParserContext;
import org.kie.kogito.serverless.workflow.parser.VariableInfo;
import org.kie.kogito.serverless.workflow.suppliers.SysoutActionSupplier;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.functions.FunctionDefinition;
import io.serverlessworkflow.api.functions.FunctionRef;

import static org.kie.kogito.serverless.workflow.parser.handlers.ActionNodeUtils.checkArgs;

public class SysOutTypeHandler extends ActionTypeHandler {

    public static final String SYSOUT_TYPE = "sysout";
    public static final String SYSOUT_TYPE_PARAM = "message";

    @Override
    public NodeFactory<?, ?> getActionNode(Workflow workflow,
            ParserContext context,
            RuleFlowNodeContainerFactory<?, ?> embeddedSubProcess,
            FunctionDefinition functionDef,
            FunctionRef functionRef,
            VariableInfo varInfo) {
        if (!checkArgs(context, functionRef, SYSOUT_TYPE_PARAM)) {
            return embeddedSubProcess.actionNode(context.newId());
        }
        return super.getActionNode(workflow, context, embeddedSubProcess, functionDef, functionRef, varInfo);
    }

    @Override
    protected <T extends RuleFlowNodeContainerFactory<T, ?>> ActionNodeFactory<T> fillAction(Workflow workflow,
            ActionNodeFactory<T> node,
            FunctionDefinition functionDef,
            FunctionRef functionRef,
            VariableInfo varInfo) {
        return node.action(new SysoutActionSupplier(workflow.getExpressionLang(), functionRef.getArguments().get(SYSOUT_TYPE_PARAM).asText(), varInfo.getInputVar(),
                FunctionTypeHandlerFactory.trimCustomOperation(functionDef)));
    }

    @Override
    public String type() {
        return SYSOUT_TYPE;
    }
}
