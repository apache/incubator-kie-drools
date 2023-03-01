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

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.eclipse.microprofile.openapi.OASFactory;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.Operation;
import org.eclipse.microprofile.openapi.models.PathItem;
import org.eclipse.microprofile.openapi.models.media.Content;
import org.eclipse.microprofile.openapi.models.media.MediaType;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.eclipse.microprofile.openapi.models.media.Schema.SchemaType;
import org.jbpm.workflow.core.WorkflowModelValidator;
import org.jbpm.workflow.core.WorkflowProcess;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcess;
import org.kie.kogito.serverless.workflow.parser.SwaggerSchemaProvider;

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
            Optional<SwaggerSchemaProvider> inputSchemaSupplier = getSchemaSupplier(workflowProcess.getInputValidator());
            Optional<SwaggerSchemaProvider> outputSchemaSupplier = getSchemaSupplier(workflowProcess.getOutputValidator());
            if (inputSchemaSupplier.isPresent() || outputSchemaSupplier.isPresent()) {
                OpenAPI openAPI = OASFactory.createOpenAPI().openapi(workflow.getId() + '_' + "workflowmodelschema").components(OASFactory.createComponents());
                inputSchemaSupplier.ifPresent(v -> openAPI.getComponents().addSchema(getInputSchemaName(workflow.getId()), v.getSchema()));
                outputSchemaSupplier.ifPresent(v -> openAPI.getComponents().addSchema(getOutputSchemaName(workflow.getId()),
                        OASFactory.createSchema().addProperty("workflowdata", v.getSchema()).addProperty("id", ID_SCHEMA)));
                return Optional.of(openAPI);
            }
        }
        return Optional.empty();
    }

    public static void mergeSchemas(OpenAPI targetSchema, Collection<OpenAPI> srcSchemas) {
        srcSchemas.forEach(srcSchema -> MergeUtil.merge(targetSchema, srcSchema));
        for (PathItem pathItem : targetSchema.getPaths().getPathItems().values()) {
            processInputOperation(targetSchema, pathItem.getPOST());
            processInputOperation(targetSchema, pathItem.getPUT());
            processInputOperation(targetSchema, pathItem.getPATCH());
            processOutputOperation(targetSchema, pathItem.getGET());
        }
    }

    private static Optional<SwaggerSchemaProvider> getSchemaSupplier(Optional<WorkflowModelValidator> validator) {
        return validator.filter(SwaggerSchemaProvider.class::isInstance).map(SwaggerSchemaProvider.class::cast);
    }

    private static String getInputSchemaName(String id) {
        return id + INPUT_SUFFIX;
    }

    private static String getOutputSchemaName(String id) {
        return id + OUTPUT_SUFFIX;
    }

    private static Optional<Schema> getSchemaRef(String id, OpenAPI openAPI) {
        return Optional.ofNullable(openAPI.getComponents().getSchemas().get(id));
    }

    private static void processInputOperation(OpenAPI openAPI, Operation operation) {
        if (operation != null && operation.getRequestBody() != null) {
            List<String> tags = operation.getTags();
            if (tags != null) {
                for (String tag : tags) {
                    getSchemaRef(getInputSchemaName(tag), openAPI).ifPresent(schema -> getMediaTypes(operation.getRequestBody().getContent()).forEach(mediaType -> mediaType.setSchema(schema)));
                }
            }
        }
    }

    private static void processOutputOperation(OpenAPI openAPI, Operation operation) {
        if (operation != null && operation.getResponses() != null && operation.getResponses().getAPIResponses() != null) {
            List<String> tags = operation.getTags();
            if (tags != null) {
                for (String tag : tags) {
                    getSchemaRef(getOutputSchemaName(tag), openAPI).ifPresent(schema -> operation.getResponses().getAPIResponses().values().stream()
                            .flatMap(response -> getMediaTypes(response.getContent()).stream()).forEach(mediaType -> mediaType.setSchema(schema)));
                }
            }
        }
    }

    private static Collection<MediaType> getMediaTypes(Content content) {
        return content != null && content.getMediaTypes() != null ? content.getMediaTypes().values() : List.of();
    }
}
