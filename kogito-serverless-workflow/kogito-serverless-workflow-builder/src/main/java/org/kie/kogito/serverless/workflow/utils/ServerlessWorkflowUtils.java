/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import java.util.function.Supplier;

import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.serverless.workflow.io.URIContentLoaderFactory;
import org.kie.kogito.serverless.workflow.parser.ParserContext;
import org.kie.kogito.serverless.workflow.suppliers.ConfigWorkItemSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.ast.expr.Expression;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.functions.FunctionDefinition;
import io.serverlessworkflow.api.functions.FunctionDefinition.Type;
import io.serverlessworkflow.api.mapper.BaseObjectMapper;
import io.serverlessworkflow.api.mapper.JsonObjectMapper;
import io.serverlessworkflow.api.mapper.YamlObjectMapper;

public class ServerlessWorkflowUtils {

    private static final Logger logger = LoggerFactory.getLogger(ServerlessWorkflowUtils.class);

    public static final String DEFAULT_WORKFLOW_FORMAT = "json";
    public static final String ALTERNATE_WORKFLOW_FORMAT = "yml";
    private static final String APP_PROPERTIES_BASE = "kogito.sw.";
    private static final String OPEN_API_PROPERTIES_BASE = "org.kogito.openapi.client.";

    private static final String APP_PROPERTIES_FUNCTIONS_BASE = APP_PROPERTIES_BASE + "functions.";
    private static final String APP_PROPERTIES_STATES_BASE = "states.";
    public static final String OPENAPI_OPERATION_SEPARATOR = "#";

    private static final String REGEX_NO_EXT = "[.][^.]+$";
    private static final String ONLY_CHARS = "[^a-z]";

    private ServerlessWorkflowUtils() {
    }

    public static BaseObjectMapper getObjectMapper(String workflowFormat) {
        return ALTERNATE_WORKFLOW_FORMAT.equals(workflowFormat) ? new YamlObjectMapper() : new JsonObjectMapper();
    }

    private static String getFunctionPrefix(FunctionDefinition function) {
        return APP_PROPERTIES_FUNCTIONS_BASE + function.getName();
    }

    private static String getOpenApiPrefix(String serviceName) {
        return OPEN_API_PROPERTIES_BASE + serviceName;
    }

    private static String getPropKey(String prefix, String key) {
        return prefix + "." + key;
    }

    public static String resolveFunctionMetadata(FunctionDefinition function, String metadataKey, KogitoBuildContext context) {
        return resolveFunctionMetadata(function, metadataKey, context, String.class, "");
    }

    public static <T> T resolveFunctionMetadata(FunctionDefinition function, String metadataKey, KogitoBuildContext context, Class<T> clazz, T defaultValue) {
        return (function.getMetadata() != null && function.getMetadata().containsKey(metadataKey)) ? clazz.cast(function.getMetadata().get(metadataKey))
                : context.getApplicationProperty(getPropKey(getFunctionPrefix(function), metadataKey), clazz).orElse(defaultValue);
    }

    public static String getOpenApiProperty(String serviceName, String metadataKey, KogitoBuildContext context) {
        return getOpenApiProperty(serviceName, metadataKey, context, String.class, "");
    }

    public static <T> T getOpenApiProperty(String serviceName, String metadataKey, KogitoBuildContext context, Class<T> clazz, T defaultValue) {
        return context.getApplicationProperty(getPropKey(getOpenApiPrefix(serviceName), metadataKey), clazz).orElse(defaultValue);
    }

    public static Supplier<Expression> runtimeRestApi(FunctionDefinition function, String metadataKey, KogitoBuildContext context) {
        return runtimeRestApi(function, metadataKey, context, String.class, null);
    }

    public static Supplier<Expression> runtimeOpenApi(String serviceName, String metadataKey, KogitoBuildContext context) {
        return runtimeOpenApi(serviceName, metadataKey, context, String.class, null);
    }

    public static <T> Supplier<Expression> runtimeRestApi(FunctionDefinition function, String metadataKey, KogitoBuildContext context, Class<T> clazz, T defaultValue) {
        return runtimeResolveMetadata(getFunctionPrefix(function), metadataKey, clazz, resolveFunctionMetadata(function, metadataKey, context, clazz, defaultValue),
                ConfigWorkItemSupplier::new);
    }

    public static <T> Supplier<Expression> runtimeOpenApi(String serviceName, String metadataKey, KogitoBuildContext context, Class<T> clazz, T defaultValue) {
        return runtimeOpenApi(serviceName, metadataKey, clazz, getOpenApiProperty(serviceName, metadataKey, context, clazz, defaultValue), ConfigWorkItemSupplier::new);
    }

    public static <T> Supplier<Expression> runtimeOpenApi(String serviceName, String metadataKey, Class<T> clazz, T defaultValue, ExpressionBuilder<T> builder) {
        return runtimeResolveMetadata(getOpenApiPrefix(serviceName), metadataKey, clazz, defaultValue, builder);
    }

    private static <T> Supplier<Expression> runtimeResolveMetadata(String prefix, String metadataKey, Class<T> clazz, T defaultValue,
            ExpressionBuilder<T> builder) {
        return builder.create(getPropKey(prefix, metadataKey), clazz, defaultValue);
    }

    public static interface ExpressionBuilder<T> {
        Supplier<Expression> create(String key, Class<T> clazz, T defaultValue);
    }

    public static String getForEachVarName(KogitoBuildContext context) {
        return context.getApplicationProperty(APP_PROPERTIES_BASE + APP_PROPERTIES_STATES_BASE + "foreach.outputVarName").orElse("_swf_eval_temp");
    }

    /**
     * @see <a href="https://github.com/serverlessworkflow/specification/blob/main/specification.md#Using-Functions-For-RESTful-Service-Invocations">Using Functions For RESTful Service
     *      Invocations</a>
     * @param function to extract the OpenApi URI
     * @return the OpenApi URI if found, or an empty string if not
     */
    public static String getOpenApiURI(FunctionDefinition function) {
        return isOpenApiOperation(function) ? function.getOperation().substring(0, function.getOperation().indexOf(OPENAPI_OPERATION_SEPARATOR)) : "";
    }

    /**
     * @see <a href="https://github.com/serverlessworkflow/specification/blob/main/specification.md#Using-Functions-For-RESTful-Service-Invocations">Using Functions For RESTful Service
     *      Invocations</a>
     * @param function to extract the OpenApi operationId
     * @return the OpenApi operationId if found, otherwise an empty string
     */
    public static String getOpenApiOperationId(FunctionDefinition function) {
        final String uri = getOpenApiURI(function);
        return uri.isEmpty() ? uri : function.getOperation().substring(uri.length() + 1);
    }

    /**
     * Checks whether or not the Function definition is an OpenApi operation
     *
     * @param function to verify
     * @return true if the given function refers to an OpenApi operation
     */
    public static boolean isOpenApiOperation(FunctionDefinition function) {
        return function.getType() == Type.REST && function.getOperation() != null && function.getOperation().contains(OPENAPI_OPERATION_SEPARATOR);
    }

    public static String getServiceName(String uri) {
        return uri.substring(uri.lastIndexOf('/') + 1).toLowerCase().replaceFirst(REGEX_NO_EXT, "").replaceAll(ONLY_CHARS, "");
    }

    public static final Optional<byte[]> processResourceFile(Workflow workflow, ParserContext parserContext, String uriStr) {
        return processResourceFile(workflow, parserContext, uriStr, null);
    }

    public static final Optional<byte[]> processResourceFile(Workflow workflow, ParserContext parserContext, String uriStr, String authRef) {
        URI uri = URI.create(uriStr);
        try {
            byte[] bytes = URIContentLoaderFactory.readAllBytes(URIContentLoaderFactory.buildLoader(uri, parserContext.getContext().getClassLoader(), workflow, authRef));
            parserContext.addGeneratedFile(new GeneratedFile(GeneratedFileType.INTERNAL_RESOURCE, uri.getPath(), bytes));
            return Optional.of(bytes);
        } catch (IOException io) {
            // if file cannot be found in build context, warn it and return the unmodified uri (it might be possible that later the resource is available at runtime) 
            logger.warn("Resource {} cannot be found at build time, ignoring", uri, io);
        }
        return Optional.empty();
    }

}
