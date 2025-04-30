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

import java.util.Map.Entry;
import java.util.Optional;

import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.ruleflow.core.factory.NodeFactory;
import org.kie.kogito.serverless.workflow.asyncapi.AsyncChannelInfo;
import org.kie.kogito.serverless.workflow.asyncapi.AsyncInfo;
import org.kie.kogito.serverless.workflow.operationid.WorkflowOperationId;
import org.kie.kogito.serverless.workflow.parser.FunctionTypeHandler;
import org.kie.kogito.serverless.workflow.parser.ParserContext;
import org.kie.kogito.serverless.workflow.parser.VariableInfo;
import org.kie.kogito.serverless.workflow.parser.handlers.NodeFactoryUtils;
import org.kie.kogito.serverless.workflow.suppliers.ProduceEventActionSupplier;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.functions.FunctionDefinition;
import io.serverlessworkflow.api.functions.FunctionRef;

public class AsyncAPITypeHandler implements FunctionTypeHandler {

    @Override
    public String type() {
        return FunctionDefinition.Type.ASYNCAPI.toString();
    }

    @Override
    public boolean isCustom() {
        return false;
    }

    @Override
    public NodeFactory<?, ?> getActionNode(Workflow workflow, ParserContext context,
            RuleFlowNodeContainerFactory<?, ?> embeddedSubProcess, FunctionDefinition functionDef,
            FunctionRef functionRef, VariableInfo varInfo) {
        WorkflowOperationId operationId = context.operationIdFactory().from(workflow, functionDef, Optional.of(context));
        return context.getAsyncInfoResolver().getAsyncInfo(operationId.getFileName())
                .flatMap(asyncAPI -> buildNode(workflow, context, embeddedSubProcess, functionDef, functionRef, varInfo, asyncAPI, operationId.getOperation()))
                .orElseThrow(() -> new IllegalArgumentException("Cannot find an async api with operation " + operationId.getOperation()));
    }

    private Optional<NodeFactory<?, ?>> buildNode(Workflow workflow, ParserContext context, RuleFlowNodeContainerFactory<?, ?> embeddedSubProcess, FunctionDefinition functionDef,
            FunctionRef functionRef, VariableInfo varInfo, AsyncInfo asyncInfo, String operationId) {
        for (Entry<String, AsyncChannelInfo> entry : asyncInfo.getOperation2Channel().entrySet()) {
            if (operationId.equals(entry.getKey())) {
                AsyncChannelInfo channelInfo = entry.getValue();
                return Optional.of(channelInfo.isPublish() ? buildPublishNode(workflow, context, embeddedSubProcess, functionDef, functionRef, varInfo, channelInfo)
                        : buildSubscribeNode(context, embeddedSubProcess, functionDef, varInfo, channelInfo));
            }
        }
        return Optional.empty();
    }

    private NodeFactory<?, ?> buildSubscribeNode(ParserContext context, RuleFlowNodeContainerFactory<?, ?> factory, FunctionDefinition functionDef, VariableInfo varInfo,
            AsyncChannelInfo entry) {
        return NodeFactoryUtils.consumeMessageNode(factory.eventNode(context.newId()), functionDef.getName(), entry.getName(), varInfo.getInputVar(), varInfo.getOutputVar());
    }

    private NodeFactory<?, ?> buildPublishNode(Workflow workflow, ParserContext context, RuleFlowNodeContainerFactory<?, ?> factory, FunctionDefinition functionDef, FunctionRef functionRef,
            VariableInfo varInfo, AsyncChannelInfo entry) {
        return NodeFactoryUtils.sendEventNode(
                factory.actionNode(context.newId()).action(new ProduceEventActionSupplier(workflow, entry.getName(), varInfo.getInputVar(), functionRef.getArguments())),
                functionDef.getName(),
                entry.getName(), varInfo.getInputVar());
    }
}
