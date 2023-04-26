/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.serverless.workflow.utils;

import java.util.function.Supplier;

import org.jbpm.compiler.canonical.descriptors.TaskDescriptor;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.ruleflow.core.factory.WorkItemNodeFactory;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.serverless.workflow.suppliers.ConfigWorkItemSupplier;
import org.kie.kogito.serverless.workflow.suppliers.ParamsRestBodyBuilderSupplier;
import org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils.ExpressionBuilder;
import org.kogito.workitem.rest.RestWorkItemHandler;

import com.github.javaparser.ast.expr.Expression;

import static org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils.getPropKey;
import static org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils.runtimeResolveMetadata;

public class RestWorkflowUtils {

    public static final String URL = "url";
    private static final String OPEN_API_PROPERTIES_BASE = "kogito.sw.openapi.";

    public static String getOpenApiProperty(String serviceName, String metadataKey, KogitoBuildContext context) {
        return getOpenApiProperty(serviceName, metadataKey, context, String.class, "");
    }

    public static <T> T getOpenApiProperty(String serviceName, String metadataKey, KogitoBuildContext context, Class<T> clazz, T defaultValue) {
        return context.getApplicationProperty(getPropKey(getOpenApiPrefix(serviceName), metadataKey), clazz).orElse(defaultValue);
    }

    public static Supplier<Expression> runtimeOpenApi(String serviceName, String metadataKey, KogitoBuildContext context) {
        return runtimeOpenApi(serviceName, metadataKey, context, String.class, null);
    }

    public static <T> Supplier<Expression> runtimeOpenApi(String serviceName, String metadataKey, KogitoBuildContext context, Class<T> clazz, T defaultValue) {
        return runtimeOpenApi(serviceName, metadataKey, clazz, getOpenApiProperty(serviceName, metadataKey, context, clazz, defaultValue), ConfigWorkItemSupplier::new);
    }

    public static <T> Supplier<Expression> runtimeOpenApi(String serviceName, String metadataKey, Class<T> clazz, T defaultValue, ExpressionBuilder<T> builder) {
        return runtimeResolveMetadata(getOpenApiPrefix(serviceName), metadataKey, clazz, defaultValue, builder);
    }

    public static String getOpenApiPrefix(String serviceName) {
        return OPEN_API_PROPERTIES_BASE + serviceName;
    }

    public static <T extends RuleFlowNodeContainerFactory<T, ?>> WorkItemNodeFactory<T> fillRest(WorkItemNodeFactory<T> workItemNode) {
        return workItemNode
                .metaData(TaskDescriptor.KEY_WORKITEM_TYPE, RestWorkItemHandler.REST_TASK_TYPE)
                .workParameter(RestWorkItemHandler.BODY_BUILDER, new ParamsRestBodyBuilderSupplier())
                .workName(RestWorkItemHandler.REST_TASK_TYPE);
    }

    private RestWorkflowUtils() {
    }
}
