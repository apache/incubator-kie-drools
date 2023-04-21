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

import org.eclipse.microprofile.openapi.OASFactory;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.Operation;
import org.eclipse.microprofile.openapi.models.PathItem;
import org.eclipse.microprofile.openapi.models.media.Content;
import org.eclipse.microprofile.openapi.models.media.MediaType;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.eclipse.microprofile.openapi.models.media.Schema.SchemaType;
import org.eclipse.microprofile.openapi.models.tags.Tag;
import org.jbpm.ruleflow.core.Metadata;
import org.jbpm.workflow.core.WorkflowModelValidator;
import org.jbpm.workflow.core.WorkflowProcess;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcess;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;
import org.kie.kogito.serverless.workflow.actions.JsonSchemaValidator;

import io.smallrye.openapi.api.util.MergeUtil;

public final class OpenApiModelSchemaGenerator {

    private OpenApiModelSchemaGenerator() {
    }

    private static final Schema ID_SCHEMA;
    private static final String INPUT_SUFFIX = "_input";
    private static final String OUTPUT_SUFFIX = "_output";

    static {
        ID_SCHEMA = OASFactory.createSchema();
        ID_SCHEMA.setType(SchemaType.STRING);
        ID_SCHEMA.setDescription("Process instance id");
    }

    public static Optional<OpenAPI> generateOpenAPIModelSchema(KogitoWorkflowProcess workflow) {
        if (workflow instanceof WorkflowProcess) {
            WorkflowProcess workflowProcess = (WorkflowProcess) workflow;
            Optional<Schema> inputSchemaSupplier = getSchema(workflowProcess.getInputValidator());
            Optional<Schema> outputSchemaSupplier = getSchema(workflowProcess.getOutputValidator());
            Map<String, Object> metadata = workflowProcess.getMetaData();
            Collection<String> tags = (Collection<String>) metadata.get(Metadata.TAGS);
            String description = (String) metadata.get(Metadata.DESCRIPTION);
            if (inputSchemaSupplier.isPresent() || outputSchemaSupplier.isPresent() || tags != null || description != null) {
                OpenAPI openAPI = OASFactory.createOpenAPI().openapi(workflow.getId() + '_' + "workflowmodelschema").components(OASFactory.createComponents());
                inputSchemaSupplier.ifPresent(v -> openAPI.getComponents().addSchema(getInputSchemaName(workflow.getId()), v));
                outputSchemaSupplier.ifPresent(v -> openAPI.getComponents().addSchema(getOutputSchemaName(workflow.getId()),
                        OASFactory.createSchema().addProperty("workflowdata", v).addProperty("id", ID_SCHEMA)));
                if (tags != null) {
                    tags.forEach(tag -> openAPI.addTag(OASFactory.createObject(Tag.class).name(tag)));
                }
                if (description != null) {
                    openAPI.addTag(OASFactory.createObject(Tag.class).name(workflow.getId()).description(description));
                }
                return Optional.of(openAPI);
            }
        }
        return Optional.empty();
    }

    public static void mergeSchemas(OpenAPI targetSchema, Collection<OpenAPI> srcSchemas) {
        srcSchemas.forEach(srcSchema -> MergeUtil.merge(targetSchema, srcSchema));
        // see https://github.com/eclipse/microprofile-open-api/issues/558
        if (targetSchema.getComponents() != null && targetSchema.getComponents().getSchemas() != null
                && targetSchema.getPaths() != null && targetSchema.getPaths().getPathItems() != null) {
            Map<String, Schema> schemas = targetSchema.getComponents().getSchemas();
            for (PathItem pathItem : targetSchema.getPaths().getPathItems().values()) {
                processInputOperation(schemas, pathItem.getPOST());
                processInputOperation(schemas, pathItem.getPUT());
                processInputOperation(schemas, pathItem.getPATCH());
                processOutputOperation(schemas, pathItem.getGET());
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

    private static String getInputSchemaName(String id) {
        return id + INPUT_SUFFIX;
    }

    private static String getOutputSchemaName(String id) {
        return id + OUTPUT_SUFFIX;
    }

    private static void processInputOperation(Map<String, Schema> schemas, Operation operation) {
        if (operation != null && operation.getRequestBody() != null) {
            List<String> tags = operation.getTags();
            if (tags != null) {
                for (String tag : tags) {
                    Schema schema = schemas.get(getInputSchemaName(tag));
                    if (schema != null) {
                        getMediaTypes(operation.getRequestBody().getContent()).forEach(mediaType -> mediaType.setSchema(schema));
                    }
                }
            }
        }
    }

    private static void processOutputOperation(Map<String, Schema> schemas, Operation operation) {
        if (operation != null && operation.getResponses() != null && operation.getResponses().getAPIResponses() != null) {
            List<String> tags = operation.getTags();
            if (tags != null) {
                for (String tag : tags) {
                    Schema schema = schemas.get(getOutputSchemaName(tag));
                    if (schema != null) {
                        operation.getResponses().getAPIResponses().values().stream()
                                .flatMap(response -> getMediaTypes(response.getContent()).stream()).forEach(mediaType -> mediaType.setSchema(schema));
                    }
                }
            }
        }
    }

    private static Collection<MediaType> getMediaTypes(Content content) {
        return content != null && content.getMediaTypes() != null ? content.getMediaTypes().values() : List.of();
    }
}
