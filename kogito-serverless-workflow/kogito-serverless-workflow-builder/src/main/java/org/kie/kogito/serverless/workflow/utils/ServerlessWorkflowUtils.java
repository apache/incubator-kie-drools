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

import org.kie.kogito.codegen.api.GeneratedFile;
import org.kie.kogito.codegen.api.GeneratedFileType;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.serverless.workflow.io.URIContentLoader;
import org.kie.kogito.serverless.workflow.io.URIContentLoaderFactory;
import org.kie.kogito.serverless.workflow.parser.ParserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.serverlessworkflow.api.functions.FunctionDefinition;
import io.serverlessworkflow.api.functions.FunctionDefinition.Type;
import io.serverlessworkflow.api.mapper.BaseObjectMapper;
import io.serverlessworkflow.api.mapper.JsonObjectMapper;
import io.serverlessworkflow.api.mapper.YamlObjectMapper;

public class ServerlessWorkflowUtils {

    private static final Logger logger = LoggerFactory.getLogger(ServerlessWorkflowUtils.class);

    public static final String DEFAULT_WORKFLOW_FORMAT = "json";
    public static final String ALTERNATE_WORKFLOW_FORMAT = "yml";
    public static final String APP_PROPERTIES_BASE = "kogito.sw.";
    private static final String APP_PROPERTIES_FUNCTIONS_BASE = "functions.";
    public static final String APP_PROPERTIES_STATES_BASE = "states.";
    public static final String OPENAPI_OPERATION_SEPARATOR = "#";

    private ServerlessWorkflowUtils() {
    }

    public static BaseObjectMapper getObjectMapper(String workflowFormat) {
        return ALTERNATE_WORKFLOW_FORMAT.equals(workflowFormat) ? new YamlObjectMapper() : new JsonObjectMapper();
    }

    public static String resolveFunctionMetadata(FunctionDefinition function, String metadataKey, KogitoBuildContext context) {
        return resolveFunctionMetadata(function, metadataKey, context, "");
    }

    public static Integer resolveFunctionMetadataAsInt(FunctionDefinition function, String metadataKey, KogitoBuildContext context) {
        String value = resolveFunctionMetadata(function, metadataKey, context);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            logger.warn("Error converting {} to number", value, ex);
            return null;
        }
    }

    public static String resolveFunctionMetadata(FunctionDefinition function, String metadataKey, KogitoBuildContext context, String defaultValue) {
        if (function != null) {
            if (function.getMetadata() != null && function.getMetadata().containsKey(metadataKey)) {
                return function.getMetadata().get(metadataKey);
            }
            Optional<String> propValue = context.getApplicationProperty(APP_PROPERTIES_BASE + APP_PROPERTIES_FUNCTIONS_BASE + function.getName() + "." + metadataKey);
            if (propValue.isPresent()) {
                return propValue.get();
            }
        }
        logger.warn("Could not resolve function metadata: {}", metadataKey);
        return defaultValue;
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

    public static void processResourceFile(URI uri, ParserContext context) {
        URIContentLoader contentLoader = URIContentLoaderFactory.buildLoader(uri, context.getContext().getClassLoader());
        try {
            context.addGeneratedFile(
                    new GeneratedFile(GeneratedFileType.INTERNAL_RESOURCE, uri.getPath(), contentLoader.toBytes()));
        } catch (IOException io) {
            // if file cannot be found in build context, warn it and return the unmodified uri (it might be possible that later the resource is available at runtime) 
            logger.warn("Resource {} cannot be found at build time, ignoring", uri, io);
        }
    }
}
