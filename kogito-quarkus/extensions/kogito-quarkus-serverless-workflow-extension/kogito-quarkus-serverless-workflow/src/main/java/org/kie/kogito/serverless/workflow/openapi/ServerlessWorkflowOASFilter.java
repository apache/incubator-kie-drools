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
package org.kie.kogito.serverless.workflow.openapi;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.microprofile.openapi.OASFactory;
import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.PathItem;
import org.eclipse.microprofile.openapi.models.media.MediaType;
import org.eclipse.microprofile.openapi.models.media.Schema;

import io.smallrye.openapi.api.util.MergeUtil;

import static java.util.function.Predicate.not;

public final class ServerlessWorkflowOASFilter implements OASFilter {

    private final Collection<SchemaInfo> inputModelSchemaInfos;

    public ServerlessWorkflowOASFilter(Collection<SchemaInfo> inputModelSchemaInfos) {
        this.inputModelSchemaInfos = inputModelSchemaInfos;
    }

    @Override
    public void filterOpenAPI(OpenAPI openAPI) {
        for (SchemaInfo inputModelSchemaInfo : inputModelSchemaInfos) {
            if (inputModelSchemaInfo.openAPI != null) {
                MergeUtil.merge(openAPI, inputModelSchemaInfo.openAPI);
                addWorkflowdataSchemaRefs(inputModelSchemaInfo, openAPI);
            }
        }

        removeJsonModelInfoSchemaReferences(openAPI);
    }

    private void removeJsonModelInfoSchemaReferences(OpenAPI openAPI) {
        getPathItemsWithoutDefinedSchema(openAPI)
                .forEach(pathItem -> getMediaTypesThatHaveJsonNodeModelInputSchema(pathItem)
                        .forEach(this::setObjectSchema));
    }

    private void setObjectSchema(MediaType mediaType) {
        mediaType.setSchema(OASFactory.createSchema().type(Schema.SchemaType.OBJECT));
    }

    private Stream<MediaType> getMediaTypesThatHaveJsonNodeModelInputSchema(PathItem pathItem) {
        return getMediaTypes(pathItem).stream().filter(this::mediaTypeHasJsonNodeModelInputSchema);
    }

    private boolean mediaTypeHasJsonNodeModelInputSchema(MediaType mediaType) {
        return mediaType != null && "#/components/schemas/JsonNodeModelInput".equals(mediaType.getSchema().getRef());
    }

    private Stream<PathItem> getPathItemsWithoutDefinedSchema(OpenAPI openAPI) {
        return openAPI.getPaths()
                .getPathItems()
                .values().stream()
                .filter(not(this::doesExistSchemaForPathItem));
    }

    private boolean doesExistSchemaForPathItem(PathItem pathItem) {
        return inputModelSchemaInfos.stream().anyMatch(schemaInfo -> pathItemHasWorkflowId(pathItem, schemaInfo.workflowId));
    }

    private static boolean pathItemHasWorkflowId(PathItem pathItem, String workflowId) {
        return pathItem.getPOST() != null
                && pathItem.getPOST().getOperationId() != null
                && pathItem.getPOST().getOperationId().matches("createResource_" + workflowId);
    }

    private static void addWorkflowdataSchemaRefs(SchemaInfo schemaInfo, OpenAPI openAPI) {
        Schema schema = OASFactory.createSchema().ref(schemaInfo.inputModelRef);

        for (PathItem pathItem : openAPI.getPaths().getPathItems().values()) {
            if (pathItemHasWorkflowId(pathItem, schemaInfo.workflowId)) {
                getMediaTypes(pathItem).forEach(mediaType -> mediaType.setSchema(schema));
            }
        }
    }

    private static Collection<MediaType> getMediaTypes(PathItem pathItem) {
        if (pathItemHasPostMethodWithMediaTypes(pathItem)) {
            return pathItem.getPOST()
                    .getRequestBody()
                    .getContent()
                    .getMediaTypes()
                    .values();
        } else {
            return List.of();
        }
    }

    private static boolean pathItemHasPostMethodWithMediaTypes(PathItem pathItem) {
        return pathItem.getPOST() != null
                && pathItem.getPOST().getRequestBody() != null
                && pathItem.getPOST().getRequestBody().getContent() != null
                && pathItem.getPOST().getRequestBody().getContent().getMediaTypes() != null;
    }

    public static final class SchemaInfo {

        private final String workflowId;

        private final OpenAPI openAPI;

        private final String inputModelRef;

        public SchemaInfo(String workflowId, OpenAPI openAPI, String inputModelRef) {
            this.workflowId = workflowId;
            this.openAPI = openAPI;
            this.inputModelRef = inputModelRef;
        }
    }
}
