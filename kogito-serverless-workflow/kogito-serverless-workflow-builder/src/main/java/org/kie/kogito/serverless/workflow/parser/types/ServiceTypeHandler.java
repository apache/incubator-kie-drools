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

import org.jbpm.compiler.canonical.descriptors.TaskDescriptor;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.ruleflow.core.factory.WorkItemNodeFactory;
import org.kie.kogito.serverless.workflow.SWFConstants;
import org.kie.kogito.serverless.workflow.parser.ParserContext;
import org.kie.kogito.serverless.workflow.parser.ServerlessWorkflowParser;

import com.fasterxml.jackson.databind.JsonNode;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.functions.FunctionDefinition;
import io.serverlessworkflow.api.functions.FunctionRef;

import static org.kie.kogito.serverless.workflow.SWFConstants.JAVA;
import static org.kie.kogito.serverless.workflow.SWFConstants.PYTHON;
import static org.kie.kogito.serverless.workflow.SWFConstants.PYTHON_SVC;
import static org.kie.kogito.serverless.workflow.SWFConstants.SERVICE_IMPL_KEY;
import static org.kie.kogito.serverless.workflow.SWFConstants.SERVICE_TASK_TYPE;
import static org.kie.kogito.serverless.workflow.SWFConstants.WORKITEM_INTERFACE;
import static org.kie.kogito.serverless.workflow.SWFConstants.WORKITEM_INTERFACE_IMPL;
import static org.kie.kogito.serverless.workflow.SWFConstants.WORKITEM_OPERATION;
import static org.kie.kogito.serverless.workflow.SWFConstants.WORKITEM_OPERATION_IMPL;
import static org.kie.kogito.serverless.workflow.parser.FunctionTypeHandlerFactory.trimCustomOperation;
import static org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils.resolveFunctionMetadata;

public class ServiceTypeHandler extends WorkItemTypeHandler {

    public static final String SERVICE_TYPE = "service";
    public static final String INTFC_SEPARATOR = "::";
    private static final String WORKITEM_PARAM_TYPE = "ParameterType";

    private static final String LANG_SEPARATOR = ":";

    @Override
    protected WorkItemNodeFactory<?> addFunctionArgs(Workflow workflow, WorkItemNodeFactory<?> node, FunctionRef functionRef) {
        JsonNode functionArgs = functionRef.getArguments();
        if (functionArgs == null) {
            node.workParameter(WORKITEM_PARAM_TYPE, ServerlessWorkflowParser.JSON_NODE);
        } else {
            processArgs(workflow, node, functionArgs, SWFConstants.MODEL_WORKFLOW_VAR);
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
                    functionDef, SERVICE_IMPL_KEY, context.getContext(), String.class, JAVA);
        }
        switch (lang) {
            case PYTHON:
                node.workName(PYTHON_SVC);
                break;
            case JAVA:
            default:
                node.workName(SERVICE_TASK_TYPE);
                break;
        }
        return node.workParameter(WORKITEM_INTERFACE, intfc)
                .workParameter(WORKITEM_OPERATION, method)
                .workParameter(WORKITEM_INTERFACE_IMPL, intfc)
                .workParameter(WORKITEM_OPERATION_IMPL, method)
                .workParameter(SERVICE_IMPL_KEY, lang)
                .metaData(TaskDescriptor.KEY_WORKITEM_TYPE, SERVICE_TASK_TYPE);
    }

    @Override
    public String type() {
        return SERVICE_TYPE;
    }

}
