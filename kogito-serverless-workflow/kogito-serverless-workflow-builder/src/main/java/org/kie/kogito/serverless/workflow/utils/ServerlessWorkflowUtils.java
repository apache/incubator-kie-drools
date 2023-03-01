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
import java.io.Reader;
import java.io.UncheckedIOException;
import java.net.URI;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;
import org.drools.util.StringUtils;
import org.jbpm.compiler.canonical.ModelMetaData;
import org.jbpm.compiler.canonical.VariableDeclarations;
import org.kie.api.definition.process.WorkflowProcess;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcess;
import org.kie.kogito.serverless.workflow.extensions.FunctionNamespaces;
import org.kie.kogito.serverless.workflow.extensions.OutputSchema;
import org.kie.kogito.serverless.workflow.extensions.URIDefinitions;
import org.kie.kogito.serverless.workflow.io.URIContentLoaderFactory;
import org.kie.kogito.serverless.workflow.models.JsonNodeModel;
import org.kie.kogito.serverless.workflow.parser.ParserContext;
import org.kie.kogito.serverless.workflow.suppliers.ConfigWorkItemSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.ast.expr.Expression;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.deserializers.ExtensionDeserializer;
import io.serverlessworkflow.api.functions.FunctionDefinition;
import io.serverlessworkflow.api.functions.FunctionDefinition.Type;
import io.serverlessworkflow.api.interfaces.Extension;
import io.serverlessworkflow.api.mapper.BaseObjectMapper;
import io.serverlessworkflow.api.mapper.JsonObjectMapper;
import io.serverlessworkflow.api.mapper.YamlObjectMapper;

public class ServerlessWorkflowUtils {

    private static final Logger logger = LoggerFactory.getLogger(ServerlessWorkflowUtils.class);

    public static final String API_KEY_PREFIX = "api_key_prefix";
    public static final String API_KEY = "api_key";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String USER_PROP = "username";
    public static final String PASSWORD_PROP = "password";
    public static final String OPERATION_SEPARATOR = "#";

    public static final String DEFAULT_WORKFLOW_FORMAT = "json";
    public static final String ALTERNATE_WORKFLOW_FORMAT = "yml";
    public static final String APP_PROPERTIES_BASE = "kogito.sw.";
    private static final String OPEN_API_PROPERTIES_BASE = "org.kogito.openapi.client.";

    private static final String APP_PROPERTIES_FUNCTIONS_BASE = APP_PROPERTIES_BASE + "functions.";
    private static final String APP_PROPERTIES_STATES_BASE = "states.";

    private static final String REGEX_NO_EXT = "[.][^.]+$";

    private ServerlessWorkflowUtils() {
    }

    public static BaseObjectMapper getObjectMapper(String workflowFormat) {
        return ALTERNATE_WORKFLOW_FORMAT.equals(workflowFormat) ? new YamlObjectMapper() : new JsonObjectMapper();
    }

    private static String getFunctionPrefix(FunctionDefinition function) {
        return APP_PROPERTIES_FUNCTIONS_BASE + function.getName();
    }

    public static Workflow getWorkflow(Reader workflowFile, String workflowFormat) throws IOException {
        BaseObjectMapper objectMapper = getObjectMapper(workflowFormat);
        ExtensionDeserializer deserializer = objectMapper.getWorkflowModule().getExtensionDeserializer();
        deserializer.addExtension(URIDefinitions.URI_DEFINITIONS, URIDefinitions.class);
        deserializer.addExtension(FunctionNamespaces.FUNCTION_NAMESPACES, FunctionNamespaces.class);
        deserializer.addExtension(OutputSchema.OUTPUT_SCHEMA, OutputSchema.class);
        return objectMapper.readValue(workflowFile, Workflow.class);
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

    public interface ExpressionBuilder<T> {
        Supplier<Expression> create(String key, Class<T> clazz, T defaultValue);
    }

    /**
     * Checks whether or not the Function definition is an OpenApi operation
     *
     * @param function to verify
     * @return true if the given function refers to an OpenApi operation
     */
    public static boolean isOpenApiOperation(FunctionDefinition function) {
        return function.getType() == Type.REST && function.getOperation() != null && function.getOperation().contains(OPERATION_SEPARATOR);
    }

    public static String getForEachVarName(KogitoBuildContext context) {
        return context.getApplicationProperty(APP_PROPERTIES_BASE + APP_PROPERTIES_STATES_BASE + "foreach.outputVarName").orElse("_swf_eval_temp");
    }

    public static Optional<byte[]> processResourceFile(Workflow workflow, ParserContext parserContext, String uriStr) {
        return processResourceFile(workflow, parserContext, uriStr, null);
    }

    public static Optional<byte[]> processResourceFile(Workflow workflow, ParserContext parserContext, String uriStr, String authRef) {
        final URI uri = URI.create(uriStr);
        final Optional<byte[]> bytes = loadResourceFile(workflow, Optional.of(parserContext), uriStr, authRef);
        bytes.ifPresent(value -> parserContext.addGeneratedFile(new GeneratedFile(GeneratedFileType.INTERNAL_RESOURCE, uri.getPath(), value)));
        return bytes;
    }

    public static Optional<byte[]> loadResourceFile(Workflow workflow, Optional<ParserContext> parserContext, String uriStr, String authRef) {
        return loadResourceFile(uriStr, Optional.of(workflow), parserContext, authRef);
    }

    public static Optional<byte[]> loadResourceFile(String uriStr, Optional<Workflow> workflow, Optional<ParserContext> parserContext, String authRef) {
        final URI uri = URI.create(uriStr);
        try {
            final byte[] bytes =
                    URIContentLoaderFactory.readAllBytes(URIContentLoaderFactory.loader(uri, parserContext.map(p -> p.getContext().getClassLoader()), Optional.empty(), workflow, authRef));
            return Optional.of(bytes);
        } catch (UncheckedIOException io) {
            // if file cannot be found in build context, warn it and return the unmodified uri (it might be possible that later the resource is available at runtime)
            logger.warn("Resource {} cannot be found at build time, ignoring", uri, io);
        }
        return Optional.empty();
    }

    public static String getRPCClassName(String serviceName) {
        return "RPC_" + serviceName + "_WorkItemHandler";
    }

    public static String getOpenApiClassName(String fileName, String methodName) {
        return StringUtils.ucFirst(getValidIdentifier(removeExt(fileName.toLowerCase())) + '_' + methodName);
    }

    public static String getOpenApiWorkItemName(String fileName, String methodName) {
        return removeExt(fileName) + '_' + methodName;
    }

    public static String removeExt(String fileName) {
        return fileName.replaceFirst(REGEX_NO_EXT, "");
    }

    public static String onlyChars(String name) {
        return filterString(name, Character::isLetter, Optional.empty());
    }

    public static String replaceNonAlphanumeric(final String name) {
        return filterString(name, Character::isLetterOrDigit, Optional.of(() -> '_'));
    }

    public static <T extends Extension> Optional<T> getExtension(Workflow workflow, Class<T> extensionClass) {
        return workflow.getExtensions().stream().filter(extensionClass::isInstance).findFirst().map(extensionClass::cast);
    }

    protected static String getValidIdentifier(String name) {
        return filterString(name, Character::isJavaIdentifierPart, Optional.empty());
    }

    protected static String filterString(String str, Predicate<Character> p, Optional<Supplier<Character>> replacer) {
        int length = str.length();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char c = str.charAt(i);
            if (p.test(c)) {
                sb.append(c);
            } else {
                replacer.ifPresent(r -> sb.append(r.get()));
            }
        }
        return sb.toString();
    }

    public static ModelMetaData getModelMetadata(WorkflowProcess process) {
        return getModelMetadata(process, JsonNodeModel.class);
    }

    private static ModelMetaData getModelMetadata(WorkflowProcess process, Class<?> modelClass) {
        return new ModelMetaData(process.getId(), modelClass.getPackage().getName(), modelClass.getSimpleName(), KogitoWorkflowProcess.PUBLIC_VISIBILITY,
                VariableDeclarations.of(Collections.emptyMap()), false);
    }
}
