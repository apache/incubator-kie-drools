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

package org.kie.kogito.serverless.workflow.parser.types;

import org.jbpm.compiler.canonical.descriptors.TaskDescriptor;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.ruleflow.core.factory.WorkItemNodeFactory;
import org.kie.kogito.serverless.workflow.parser.ParserContext;
import org.kie.kogito.serverless.workflow.parser.ServerlessWorkflowParser;

import com.fasterxml.jackson.databind.JsonNode;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.functions.FunctionDefinition;
import io.serverlessworkflow.api.functions.FunctionRef;

import static org.kie.kogito.serverless.workflow.parser.FunctionTypeHandlerFactory.trimCustomOperation;
import static org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils.resolveFunctionMetadata;

public class ServiceTypeHandler extends WorkItemTypeHandler {

    private static final String SERVICE_TASK_TYPE = "Service Task";
    private static final String SERVICE_IMPL_KEY = "implementation";
    private static final String WORKITEM_PARAM_TYPE = "ParameterType";
    private static final String WORKITEM_PARAM = "Parameter";
    private static final String WORKITEM_INTERFACE = "Interface";
    private static final String WORKITEM_OPERATION = "Operation";
    private static final String WORKITEM_INTERFACE_IMPL = "interfaceImplementationRef";
    private static final String WORKITEM_OPERATION_IMPL = "operationImplementationRef";
    private static final String INTFC_SEPARATOR = "::";
    private static final String LANG_SEPARATOR = ":";

    @Override
    protected WorkItemNodeFactory<?> buildWorkItem(RuleFlowNodeContainerFactory<?, ?> embeddedSubProcess,
            ParserContext context,
            String inputVar,
            String outputVar) {
        return embeddedSubProcess
                .workItemNode(context.newId())
                .metaData(TaskDescriptor.KEY_WORKITEM_TYPE, SERVICE_TASK_TYPE)
                .workName(SERVICE_TASK_TYPE)
                .inMapping(inputVar, WORKITEM_PARAM)
                .outMapping(WORKITEM_PARAM, outputVar);
    }

    @Override
    protected <T extends RuleFlowNodeContainerFactory<T, ?>> WorkItemNodeFactory<T> addFunctionArgs(Workflow workflow, WorkItemNodeFactory<T> node, FunctionRef functionRef) {
        JsonNode functionArgs = functionRef.getArguments();
        if (functionArgs == null) {
            node.workParameter(WORKITEM_PARAM_TYPE, ServerlessWorkflowParser.JSON_NODE);
        } else {
            processArgs(workflow, node, functionArgs, WORKITEM_PARAM);
        }
        return node;
    }

    @Override
    protected <T extends RuleFlowNodeContainerFactory<T, ?>> WorkItemNodeFactory<T> fillWorkItemHandler(Workflow workflow, ParserContext context, WorkItemNodeFactory<T> node,
            FunctionDefinition functionDef) {
        String intfc = null;
        String method = null;
        String lang = null;
        String operation = trimCustomOperation(functionDef);

        int indexOf = operation.indexOf(INTFC_SEPARATOR);
        if (indexOf != -1) {
            method = operation.substring(indexOf + INTFC_SEPARATOR.length());
            operation = operation.substring(0, indexOf);
            indexOf = operation.indexOf(LANG_SEPARATOR);
            if (indexOf != -1) {
                intfc = operation.substring(indexOf + LANG_SEPARATOR.length());
                lang = operation.substring(0, indexOf);
            } else {
                intfc = operation;
            }
        }
        if (lang == null) {
            lang = resolveFunctionMetadata(
                    functionDef, SERVICE_IMPL_KEY, context.getContext(), String.class, "Java");
        }
        return node.workParameter(WORKITEM_INTERFACE, intfc)
                .workParameter(WORKITEM_OPERATION, method)
                .workParameter(WORKITEM_INTERFACE_IMPL, intfc)
                .workParameter(WORKITEM_OPERATION_IMPL, method)
                .workParameter(SERVICE_IMPL_KEY, lang);
    }

    @Override
    public String type() {
        return "service";
    }

}
