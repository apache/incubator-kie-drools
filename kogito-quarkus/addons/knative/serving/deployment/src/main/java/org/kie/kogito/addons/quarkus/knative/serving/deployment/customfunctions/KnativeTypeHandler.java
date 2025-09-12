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
package org.kie.kogito.addons.quarkus.knative.serving.deployment.customfunctions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.jbpm.compiler.canonical.descriptors.TaskDescriptor;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.ruleflow.core.factory.NodeFactory;
import org.jbpm.ruleflow.core.factory.WorkItemNodeFactory;
import org.kie.kogito.addons.quarkus.knative.serving.customfunctions.CloudEventKnativeParamsDecorator;
import org.kie.kogito.addons.quarkus.knative.serving.customfunctions.GetParamsDecorator;
import org.kie.kogito.addons.quarkus.knative.serving.customfunctions.KnativeWorkItemHandler;
import org.kie.kogito.addons.quarkus.knative.serving.customfunctions.Operation;
import org.kie.kogito.addons.quarkus.knative.serving.customfunctions.PlainJsonKnativeParamsDecorator;
import org.kie.kogito.serverless.workflow.parser.ParserContext;
import org.kie.kogito.serverless.workflow.parser.VariableInfo;
import org.kie.kogito.serverless.workflow.parser.types.WorkItemTypeHandler;
import org.kie.kogito.serverless.workflow.suppliers.ParamsRestBodyBuilderSupplier;
import org.kogito.workitem.rest.RestWorkItemHandler;

import com.github.javaparser.ast.expr.Expression;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.functions.FunctionDefinition;
import io.serverlessworkflow.api.functions.FunctionRef;
import io.vertx.core.http.HttpMethod;

import static org.kie.kogito.addons.quarkus.knative.serving.customfunctions.KnativeWorkItemHandler.PAYLOAD_FIELDS_PROPERTY_NAME;
import static org.kie.kogito.serverless.workflow.parser.FunctionTypeHandlerFactory.trimCustomOperation;
import static org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils.runtimeRestApi;

public class KnativeTypeHandler extends WorkItemTypeHandler {

    private static final String DEFAULT_REQUEST_TIMEOUT_VALUE = "10000";

    @Override
    public NodeFactory<?, ?> getActionNode(Workflow workflow, ParserContext context,
            RuleFlowNodeContainerFactory<?, ?> embeddedSubProcess, FunctionDefinition functionDef,
            FunctionRef functionRef, VariableInfo varInfo) {
        validateArgs(functionRef);

        WorkItemNodeFactory<?> node = buildWorkItem(embeddedSubProcess, context, varInfo.getInputVar(), varInfo.getOutputVar())
                .name(functionDef.getName());

        List<String> payloadFields = getPayloadFields(functionRef);

        Operation operation = Operation.parse(trimCustomOperation(functionDef));

        if (!payloadFields.isEmpty()) {
            node.workParameter(PAYLOAD_FIELDS_PROPERTY_NAME, payloadFields);
        }
        if (HttpMethod.GET.equals(operation.getHttpMethod())) {
            node.workParameter(RestWorkItemHandler.PARAMS_DECORATOR, GetParamsDecorator.class.getName());
        } else {
            if (operation.isCloudEvent()) {
                node.workParameter(RestWorkItemHandler.PARAMS_DECORATOR, CloudEventKnativeParamsDecorator.class.getName());
            } else {
                node.workParameter(RestWorkItemHandler.PARAMS_DECORATOR, PlainJsonKnativeParamsDecorator.class.getName());
            }
        }

        node.workParameter(KnativeWorkItemHandler.SERVICE_PROPERTY_NAME, operation.getService())
                .workParameter(KnativeWorkItemHandler.PATH_PROPERTY_NAME, operation.getPath())
                .workParameter(RestWorkItemHandler.METHOD, operation.getHttpMethod());

        return addFunctionArgs(workflow,
                fillWorkItemHandler(workflow, context, node, functionDef),
                functionRef);
    }

    private static List<String> getPayloadFields(FunctionRef functionRef) {
        List<String> payloadFields = new ArrayList<>();

        if (functionRef.getArguments() != null && !functionRef.getArguments().isEmpty()) {
            functionRef.getArguments().fieldNames().forEachRemaining(payloadFields::add);
        }
        return payloadFields;
    }

    @Override
    protected <T extends RuleFlowNodeContainerFactory<T, ?>> WorkItemNodeFactory<T> fillWorkItemHandler(
            Workflow workflow, ParserContext context, WorkItemNodeFactory<T> node, FunctionDefinition functionDef) {
        if (functionDef.getMetadata() != null) {
            functionDef.getMetadata().forEach(node::metaData);
        }

        Supplier<Expression> requestTimeout = runtimeRestApi(functionDef, "timeout",
                context.getContext(), String.class, DEFAULT_REQUEST_TIMEOUT_VALUE);

        return node.workParameter(RestWorkItemHandler.BODY_BUILDER, new ParamsRestBodyBuilderSupplier())
                .workParameter(RestWorkItemHandler.REQUEST_TIMEOUT_IN_MILLIS, requestTimeout)
                .metaData(TaskDescriptor.KEY_WORKITEM_TYPE, RestWorkItemHandler.REST_TASK_TYPE)
                .workName(KnativeWorkItemHandler.NAME);
    }

    @Override
    public String type() {
        return KnativeWorkItemHandler.NAME;
    }
}
