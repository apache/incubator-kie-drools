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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.serverlessworkflow.api.functions.FunctionDefinition;
import io.serverlessworkflow.api.functions.FunctionDefinition.Type;
import io.serverlessworkflow.api.mapper.BaseObjectMapper;
import io.serverlessworkflow.api.mapper.JsonObjectMapper;
import io.serverlessworkflow.api.mapper.YamlObjectMapper;

public class ServerlessWorkflowUtils {

    public static final String DEFAULT_WORKFLOW_FORMAT = "json";
    public static final String ALTERNATE_WORKFLOW_FORMAT = "yml";
    public static final String APP_PROPERTIES_BASE = "kogito.sw.";
    private static final String APP_PROPERTIES_FUNCTIONS_BASE = "functions.";
    public static final String APP_PROPERTIES_STATES_BASE = "states.";

    public static final String OPENAPI_OPERATION_SEPARATOR = "#";

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerlessWorkflowUtils.class);

    private ServerlessWorkflowUtils() {
    }

    public static BaseObjectMapper getObjectMapper(String workflowFormat) {
        return ALTERNATE_WORKFLOW_FORMAT.equals(workflowFormat) ? new YamlObjectMapper() : new JsonObjectMapper();
    }

    public static String conditionScript(String conditionStr) {
        if (conditionStr.startsWith("{{")) {
            conditionStr = conditionStr.substring(2);
        }
        if (conditionStr.endsWith("}}")) {
            conditionStr = conditionStr.substring(0, conditionStr.length() - 2);
        }

        return conditionStr.trim();
    }

    public static String resolveFunctionMetadata(FunctionDefinition function, String metadataKey, WorkflowAppContext workflowAppContext) {
        return resolveFunctionMetadata(function, metadataKey, workflowAppContext, "");
    }

    public static Integer resolveFunctionMetadataAsInt(FunctionDefinition function, String metadataKey, WorkflowAppContext workflowAppContext) {
        String value = resolveFunctionMetadata(function, metadataKey, workflowAppContext);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            LOGGER.warn("Error converting {} to number", value, ex);
            return null;
        }
    }

    public static String resolveFunctionMetadata(FunctionDefinition function, String metadataKey, WorkflowAppContext workflowAppContext, String defaultValue) {
        if (function != null && function.getMetadata() != null && function.getMetadata().containsKey(metadataKey)) {
            return function.getMetadata().get(metadataKey);
        }

        if (function != null && workflowAppContext != null &&
                workflowAppContext.getApplicationProperties().containsKey(APP_PROPERTIES_BASE + APP_PROPERTIES_FUNCTIONS_BASE + function.getName() + "." + metadataKey)) {
            return workflowAppContext.getApplicationProperty(APP_PROPERTIES_BASE + APP_PROPERTIES_FUNCTIONS_BASE + function.getName() + "." + metadataKey);
        }

        LOGGER.warn("Could not resolve function metadata: {}", metadataKey);
        return defaultValue;
    }

    /**
     * @see <a href="https://github.com/serverlessworkflow/specification/blob/main/specification.md#Using-Functions-For-RESTful-Service-Invocations">Using Functions For RESTful Service
     *      Invocations</a>
     * @param function to extract the OpenApi URI
     * @return the OpenApi URI if found, or an empty string if not
     */
    public static String getOpenApiURI(FunctionDefinition function) {
        if (isOpenApiOperation(function)) {
            return function.getOperation().substring(0, function.getOperation().indexOf(OPENAPI_OPERATION_SEPARATOR));
        }
        return "";
    }

    /**
     * @see <a href="https://github.com/serverlessworkflow/specification/blob/main/specification.md#Using-Functions-For-RESTful-Service-Invocations">Using Functions For RESTful Service
     *      Invocations</a>
     * @param function to extract the OpenApi operationId
     * @return the OpenApi operationId if found, otherwise an empty string
     */
    public static String getOpenApiOperationId(FunctionDefinition function) {
        final String uri = getOpenApiURI(function);
        if (uri.isEmpty()) {
            return "";
        }
        return function.getOperation().substring(uri.length() + 1);
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
}
