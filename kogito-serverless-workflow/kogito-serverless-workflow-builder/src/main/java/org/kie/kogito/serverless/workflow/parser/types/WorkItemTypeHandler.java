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
import org.jbpm.ruleflow.core.factory.NodeFactory;
import org.jbpm.ruleflow.core.factory.WorkItemNodeFactory;
import org.kie.kogito.internal.process.workitem.WorkItemRecordParameters;
import org.kie.kogito.serverless.workflow.parser.FunctionTypeHandler;
import org.kie.kogito.serverless.workflow.parser.ParserContext;
import org.kie.kogito.serverless.workflow.parser.VariableInfo;
import org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils;
import org.kie.kogito.serverless.workflow.utils.WorkItemBuilder;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.functions.FunctionDefinition;
import io.serverlessworkflow.api.functions.FunctionRef;

public abstract class WorkItemTypeHandler extends WorkItemBuilder implements FunctionTypeHandler {
    @Override
    public NodeFactory<?, ?> getActionNode(Workflow workflow, ParserContext context, RuleFlowNodeContainerFactory<?, ?> embeddedSubProcess, FunctionDefinition functionDef, FunctionRef functionRef,
            VariableInfo varInfo) {
        validateArgs(functionRef);
        WorkItemNodeFactory<?> workItemFactory =
                fillWorkItemHandler(workflow, context, buildWorkItem(embeddedSubProcess, context, varInfo.getInputVar(), varInfo.getOutputVar()).name(functionDef.getName()), functionDef);
        workItemFactory.metaData(WorkItemRecordParameters.RECORD_ARGS,
                ServerlessWorkflowUtils.resolveFunctionProperty(functionDef, WorkItemRecordParameters.RECORD_ARGS, context.getContext(), Boolean.class, true));
        return addFunctionArgs(workflow,
                workItemFactory,
                functionRef);
    }

    protected abstract <T extends RuleFlowNodeContainerFactory<T, ?>> WorkItemNodeFactory<T> fillWorkItemHandler(Workflow workflow, ParserContext context, WorkItemNodeFactory<T> node,
            FunctionDefinition functionDef);

}
