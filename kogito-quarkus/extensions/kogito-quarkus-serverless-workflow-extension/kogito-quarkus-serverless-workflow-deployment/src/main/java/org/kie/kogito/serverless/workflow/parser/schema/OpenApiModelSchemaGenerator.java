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

package org.kie.kogito.serverless.workflow.parser.schema;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.openapi.OASFactory;
import org.eclipse.microprofile.openapi.models.Components;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.Operation;
import org.eclipse.microprofile.openapi.models.PathItem;
import org.eclipse.microprofile.openapi.models.media.Content;
import org.eclipse.microprofile.openapi.models.media.MediaType;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.eclipse.microprofile.openapi.models.media.Schema.SchemaType;
import org.eclipse.microprofile.openapi.models.responses.APIResponse;
import org.jbpm.workflow.core.WorkflowModelValidator;
import org.jbpm.workflow.core.WorkflowProcess;
import org.kie.kogito.Model;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcess;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;
import org.kie.kogito.serverless.workflow.actions.JsonSchemaValidator;
import org.kie.kogito.serverless.workflow.models.JsonNodeModelInput;
import org.kie.kogito.serverless.workflow.models.JsonNodeModelOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class OpenApiModelSchemaGenerator {

    private OpenApiModelSchemaGenerator() {
    }

    private static final Logger logger = LoggerFactory.getLogger(OpenApiModelSchemaGenerator.class);

    private static final Schema ID_SCHEMA = OASFactory.createSchema().type(SchemaType.STRING).description("Process instance id");
    private static final String INPUT_SUFFIX = "_input";
    private static final String OUTPUT_SUFFIX = "_output";

    public static void addOpenAPIModelSchema(KogitoWorkflowProcess workflow, Map<String, Schema> schemas) {
        if (workflow instanceof WorkflowProcess) {
            WorkflowProcess workflowProcess = (WorkflowProcess) workflow;
            getSchema(workflowProcess.getInputValidator()).ifPresent(v -> {
                String key = getSchemaName(workflow.getId(), INPUT_SUFFIX);
                schemas.put(key, schemaTitle(key, v));
            });
            getSchema(workflowProcess.getOutputValidator()).ifPresent(v -> {
                String key = getSchemaName(workflow.getId(), OUTPUT_SUFFIX);
                schemas.put(key, createOutputSchema(schemaTitle(key, v)));
            });
        }
    }

    private static Schema schemaTitle(String key, Schema schema) {
        if (!useTitle()) {
            logger.debug("use schema title is disabled, using {} as title", key);
            schema.title(key);
        } else if (schema.getTitle() == null) {
            logger.warn("Title for schema {} is null, using {}", schema, key);
            schema.title(key);
        }
        return schema;
    }

    private static boolean useTitle() {
        return ConfigProvider.getConfig().getOptionalValue("kogito.sw.schema.use_title", Boolean.class).orElse(true);
    }

    private static Schema createOutputSchema(Schema schema) {
        return OASFactory.createSchema().addProperty("workflowdata", schema).addProperty("id", ID_SCHEMA).title(schema.getTitle());
    }

    public static void mergeSchemas(OpenAPI targetSchema, Map<String, Schema> schemas) {
        Components components = targetSchema.getComponents();
        if (components == null) {
            components = OASFactory.createComponents();
            targetSchema.setComponents(components);
        }
        for (Schema schema : schemas.values()) {
            components.addSchema(schema.getTitle(), schema);
        }
        if (targetSchema.getPaths() != null && targetSchema.getPaths().getPathItems() != null) {
            for (PathItem pathItem : targetSchema.getPaths().getPathItems().values()) {
                processOperation(schemas, pathItem.getPOST());
                processOperation(schemas, pathItem.getPUT());
                processOperation(schemas, pathItem.getPATCH());
                processOperation(schemas, pathItem.getGET());
                processOperation(schemas, pathItem.getDELETE());
            }
        }
    }

    private static Optional<Schema> getSchema(Optional<WorkflowModelValidator> validator) {
        return validator.filter(JsonSchemaValidator.class::isInstance).map(JsonSchemaValidator.class::cast).map(OpenApiModelSchemaGenerator::getSchema);
    }

    private static Schema getSchema(JsonSchemaValidator validator) {
        try {
            return ObjectMapperFactory.get().readValue(validator.load().toString(), JsonSchemaImpl.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static String getSchemaName(String id, String suffix) {
        return id + suffix;
    }

    private static void processOperation(Map<String, Schema> schemas, Operation operation) {
        if (operation != null) {
            List<String> tags = operation.getTags();
            if (tags != null) {
                for (String tag : tags) {
                    if (operation.getRequestBody() != null) {
                        Schema schema = schemas.get(getSchemaName(tag, INPUT_SUFFIX));
                        if (schema != null) {
                            getMediaTypes(operation.getRequestBody().getContent()).stream().filter(OpenApiModelSchemaGenerator::isInput).forEach(mediaType -> mediaType.setSchema(schema));
                        }
                    }
                    if (operation.getResponses() != null && operation.getResponses().getAPIResponses() != null) {
                        Schema schema = schemas.get(getSchemaName(tag, OUTPUT_SUFFIX));
                        if (schema != null) {
                            for (APIResponse response : operation.getResponses().getAPIResponses().values()) {
                                Content content = response.getContent();
                                if (content == null) {
                                    response.setContent(OASFactory.createContent().addMediaType("application/json", OASFactory.createMediaType().schema(schema)));
                                } else {
                                    getMediaTypes(content).stream().filter(OpenApiModelSchemaGenerator::isOutput).forEach(mediaType -> mediaType.setSchema(schema));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static boolean isOutput(MediaType type) {
        return isJsonModelRef(type, JsonNodeModelOutput.class);
    }

    private static boolean isInput(MediaType type) {
        return isJsonModelRef(type, JsonNodeModelInput.class);
    }

    private static boolean isJsonModelRef(MediaType type, Class<? extends Model> clazz) {
        Schema schema = type.getSchema();
        return schema != null && type.getSchema().getRef() != null && type.getSchema().getRef().endsWith(clazz.getSimpleName());
    }

    private static Collection<MediaType> getMediaTypes(Content content) {
        return content != null && content.getMediaTypes() != null ? content.getMediaTypes().values() : List.of();
    }
}
