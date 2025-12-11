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
import org.jbpm.ruleflow.core.factory.WorkItemNodeFactory;
import org.kie.kogito.serverless.workflow.parser.ParserContext;
import org.kie.kogito.serverless.workflow.suppliers.JsonNodeResultHandlerSupplier;
import org.kie.kogito.serverless.workflow.suppliers.ParamsRestBodyBuilderSupplier;
import org.kogito.workitem.rest.RestWorkItemHandler;
import org.kogito.workitem.rest.auth.ApiKeyAuthDecorator;
import org.kogito.workitem.rest.auth.BearerTokenAuthDecorator;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.functions.FunctionDefinition;

import static org.kie.kogito.serverless.workflow.parser.FunctionTypeHandlerFactory.trimCustomOperation;
import static org.kie.kogito.serverless.workflow.utils.RestWorkflowUtils.fillRest;
import static org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils.ACCESS_TOKEN;
import static org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils.API_KEY;
import static org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils.API_KEY_PREFIX;
import static org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils.APP_PROPERTIES_FUNCTIONS_BASE;
import static org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils.PASSWORD_PROP;
import static org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils.USER_PROP;
import static org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils.runtimeRestApi;

public class RestTypeHandler extends WorkItemTypeHandler {

    public static final String REST_TYPE = "rest";
    private static final String METHOD_SEPARATOR = ":";
    private static final String PORT = "port";

    @Override
    protected <T extends RuleFlowNodeContainerFactory<T, ?>> WorkItemNodeFactory<T> fillWorkItemHandler(Workflow workflow,
            ParserContext context,
            WorkItemNodeFactory<T> node,
            FunctionDefinition functionDef) {
        String url = null;
        String method = null;
        // try extracting from operation (format method:url)
        String operation = trimCustomOperation(functionDef);
        int indexOf = operation.indexOf(METHOD_SEPARATOR);
        if (indexOf != -1) {
            method = operation.substring(0, indexOf);
            url = operation.substring(indexOf + METHOD_SEPARATOR.length());
        } else {
            url = operation;
        }
        return fillRest(node.workParameter(RestWorkItemHandler.URL, url)
                .workParameter(RestWorkItemHandler.METHOD, method)
                .workParameter(RestWorkItemHandler.USER, runtimeRestApi(functionDef, USER_PROP, context.getContext()))
                .workParameter(RestWorkItemHandler.PASSWORD, runtimeRestApi(functionDef, PASSWORD_PROP, context.getContext()))
                .workParameter(RestWorkItemHandler.PROTOCOL, runtimeRestApi(functionDef, "protocol", context.getContext()))
                .workParameter(RestWorkItemHandler.HOST, runtimeRestApi(functionDef, "host", context.getContext()))
                .workParameter(RestWorkItemHandler.PORT, runtimeRestApi(functionDef, PORT, context.getContext(), Integer.class,
                        context.getContext().getApplicationProperty(APP_PROPERTIES_FUNCTIONS_BASE + PORT).map(Integer::parseInt).orElse(null)))
                .workParameter(RestWorkItemHandler.BODY_BUILDER, new ParamsRestBodyBuilderSupplier())
                .workParameter(BearerTokenAuthDecorator.BEARER_TOKEN, runtimeRestApi(functionDef, ACCESS_TOKEN, context.getContext()))
                .workParameter(ApiKeyAuthDecorator.KEY_PREFIX, runtimeRestApi(functionDef, API_KEY_PREFIX, context.getContext()))
                .workParameter(ApiKeyAuthDecorator.KEY, runtimeRestApi(functionDef, API_KEY, context.getContext())))
                        .workParameter(RestWorkItemHandler.RESULT_HANDLER, new JsonNodeResultHandlerSupplier());
    }

    @Override
    public String type() {
        return REST_TYPE;
    }
}
