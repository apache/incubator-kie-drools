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

import org.drools.mvel.java.JavaDialect;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.ruleflow.core.factory.NodeFactory;
import org.kie.kogito.serverless.workflow.parser.FunctionTypeHandler;
import org.kie.kogito.serverless.workflow.parser.ParserContext;
import org.kie.kogito.serverless.workflow.parser.VariableInfo;
import org.kie.kogito.serverless.workflow.utils.WorkItemBuilder;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.functions.FunctionDefinition;
import io.serverlessworkflow.api.functions.FunctionRef;

import static org.kie.kogito.serverless.workflow.SWFConstants.PYTHON;
import static org.kie.kogito.serverless.workflow.SWFConstants.PYTHON_SCRIPT;
import static org.kie.kogito.serverless.workflow.SWFConstants.SCRIPT;
import static org.kie.kogito.serverless.workflow.parser.FunctionTypeHandlerFactory.trimCustomOperation;
import static org.kie.kogito.serverless.workflow.parser.handlers.ActionNodeUtils.actionNode;
import static org.kie.kogito.serverless.workflow.parser.handlers.ActionNodeUtils.checkArgs;

public class ScriptTypeHandler extends WorkItemBuilder implements FunctionTypeHandler {

    @Override
    public String type() {
        return SCRIPT;
    }

    @Override
    public NodeFactory<?, ?> getActionNode(Workflow workflow, ParserContext context,
            RuleFlowNodeContainerFactory<?, ?> embeddedSubProcess, FunctionDefinition functionDef,
            FunctionRef functionRef, VariableInfo varInfo) {
        if (!checkArgs(context, functionRef, SCRIPT)) {
            return embeddedSubProcess.actionNode(context.newId());
        }
        String lang = trimCustomOperation(functionDef);
        if (PYTHON.equalsIgnoreCase(lang)) {
            return addFunctionArgs(workflow,
                    buildWorkItem(embeddedSubProcess, context, varInfo.getInputVar(), varInfo.getOutputVar()).name(functionDef.getName()),
                    functionRef).workName(PYTHON_SCRIPT);
        } else {
            return actionNode(embeddedSubProcess, context, functionDef).action(JavaDialect.ID,
                    functionRef.getArguments().get(SCRIPT).asText());
        }
    }
}
