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
import java.io.Writer;
import java.net.URI;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javaparser.ast.expr.Expression;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.deserializers.ExtensionDeserializer;
import io.serverlessworkflow.api.functions.FunctionDefinition;
import io.serverlessworkflow.api.interfaces.Extension;
import io.serverlessworkflow.api.mapper.BaseObjectMapper;
import io.serverlessworkflow.api.mapper.JsonObjectMapper;
import io.serverlessworkflow.api.mapper.YamlObjectMapper;
import io.serverlessworkflow.api.serializers.ExtensionSerializer;

public class ServerlessWorkflowUtils {

    private static final Logger logger = LoggerFactory.getLogger(ServerlessWorkflowUtils.class);

    public static final String API_KEY_PREFIX = "api_key_prefix";
    public static final String API_KEY = "api_key";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String USER_PROP = "username";
    public static final String PASSWORD_PROP = "password";
    public static final String OPERATION_SEPARATOR = "#";
    /**
     * @deprecated Replaced by WorkflowFormat enum
     */
    @Deprecated
    public static final String DEFAULT_WORKFLOW_FORMAT = "json";
    /**
     * @deprecated Replaced by WorkflowFormat enum
     */
    @Deprecated
    public static final String ALTERNATE_WORKFLOW_FORMAT = "yml";
    public static final String APP_PROPERTIES_BASE = "kogito.sw.";

    public static final String APP_PROPERTIES_FUNCTIONS_BASE = APP_PROPERTIES_BASE + "functions.";

    private static final String REGEX_NO_EXT = "[.][^.]+$";

    private static final BaseObjectMapper yamlReaderMapper = deserializer(new YamlObjectMapper());
    private static final BaseObjectMapper jsonReaderMapper = deserializer(new JsonObjectMapper());

    private static final BaseObjectMapper yamlWriterMapper = serializer(new YamlObjectMapper());
    private static final BaseObjectMapper jsonWriterMapper = serializer(new JsonObjectMapper());

    private ServerlessWorkflowUtils() {
    }

    /**
     * Read a workflow.
     * 
     * @param reader Reader instance holding workflow data
     * @param workflowFormat Format of the data, json or yaml.
     * @return Workflow instance
     * @throws IOException
     */
    public static Workflow getWorkflow(Reader reader, WorkflowFormat workflowFormat) throws IOException {
        BaseObjectMapper objectMapper = workflowFormat == WorkflowFormat.YAML ? yamlReaderMapper : jsonReaderMapper;
        return objectMapper.readValue(reader, Workflow.class);
    }

    /**
     * Kept for backward compatibility purposes
     * 
     * @deprecated Rather than the string for format use WorkflowFormat enumeration to indicate if the flow is yaml or json.
     */
    @Deprecated
    public static Workflow getWorkflow(Reader reader, String workflowFormat) throws IOException {
        return getWorkflow(reader, ALTERNATE_WORKFLOW_FORMAT.equals(workflowFormat) ? WorkflowFormat.YAML : WorkflowFormat.JSON);
    }

    /**
     * Write a workflow
     * 
     * @param workflow Workflow definition
     * @param writer Target output reader
     * @param workflowFormat Format of the data, json or yaml.
     * @throws IOException
     */
    public static void writeWorkflow(Workflow workflow, Writer writer, WorkflowFormat workflowFormat) throws IOException {
        ObjectMapper objectMapper = workflowFormat == WorkflowFormat.YAML ? yamlWriterMapper : jsonWriterMapper;
        objectMapper.writeValue(writer, workflow);
    }

    private static BaseObjectMapper deserializer(BaseObjectMapper objectMapper) {
        ExtensionDeserializer deserializer = objectMapper.getWorkflowModule().getExtensionDeserializer();
        deserializer.addExtension(URIDefinitions.URI_DEFINITIONS, URIDefinitions.class);
        deserializer.addExtension(FunctionNamespaces.FUNCTION_NAMESPACES, FunctionNamespaces.class);
        deserializer.addExtension(OutputSchema.OUTPUT_SCHEMA, OutputSchema.class);
        return objectMapper;
    }

    private static BaseObjectMapper serializer(BaseObjectMapper objectMapper) {
        ExtensionSerializer serializer = objectMapper.getWorkflowModule().getExtensionSerializer();
        serializer.addExtension(URIDefinitions.URI_DEFINITIONS, URIDefinitions.class);
        serializer.addExtension(FunctionNamespaces.FUNCTION_NAMESPACES, FunctionNamespaces.class);
        serializer.addExtension(OutputSchema.OUTPUT_SCHEMA, OutputSchema.class);
        return objectMapper;
    }

    public static String getFunctionPrefix(FunctionDefinition function) {
        return APP_PROPERTIES_FUNCTIONS_BASE + function.getName();
    }

    public static <T> Supplier<Expression> runtimeRestApi(FunctionDefinition function, String metadataKey, KogitoBuildContext context, Class<T> clazz, T defaultValue) {
        return runtimeResolveMetadata(getFunctionPrefix(function), metadataKey, clazz, resolveFunctionMetadata(function, metadataKey, context, clazz, defaultValue),
                ConfigWorkItemSupplier::new);
    }

    public static Supplier<Expression> runtimeRestApi(FunctionDefinition function, String metadataKey, KogitoBuildContext context) {
        return runtimeRestApi(function, metadataKey, context, String.class, null);
    }

    public static <T> Supplier<Expression> runtimeResolveMetadata(String prefix, String metadataKey, Class<T> clazz, T defaultValue,
            ExpressionBuilder<T> builder) {
        return builder.create(getPropKey(prefix, metadataKey), clazz, defaultValue);
    }

    public static String getPropKey(String prefix, String key) {
        return prefix + "." + key;
    }

    public static String resolveFunctionMetadata(FunctionDefinition function, String metadataKey, KogitoBuildContext context) {
        return resolveFunctionMetadata(function, metadataKey, context, String.class, "");
    }

    public static <T> T resolveFunctionMetadata(FunctionDefinition function, String metadataKey, KogitoBuildContext context, Class<T> clazz, T defaultValue) {
        return (function.getMetadata() != null && function.getMetadata().containsKey(metadataKey)) ? clazz.cast(function.getMetadata().get(metadataKey))
                : context.getApplicationProperty(getPropKey(getFunctionPrefix(function), metadataKey), clazz).orElse(defaultValue);
    }

    public interface ExpressionBuilder<T> {
        Supplier<Expression> create(String key, Class<T> clazz, T defaultValue);
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

    public static String getValidIdentifier(String name) {
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
