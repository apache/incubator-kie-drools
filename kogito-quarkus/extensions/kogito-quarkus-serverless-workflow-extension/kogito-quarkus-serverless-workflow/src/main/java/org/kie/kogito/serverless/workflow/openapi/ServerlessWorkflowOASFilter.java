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

import org.eclipse.microprofile.openapi.OASFactory;
import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.PathItem;
import org.eclipse.microprofile.openapi.models.media.MediaType;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.kie.kogito.serverless.workflow.SWFConstants;

import io.smallrye.openapi.api.util.MergeUtil;

public final class ServerlessWorkflowOASFilter implements OASFilter {

    private final Collection<OpenAPI> inputModelSchemas;

    public ServerlessWorkflowOASFilter(Collection<OpenAPI> inputModelSchemas) {
        this.inputModelSchemas = inputModelSchemas;
    }

    @Override
    public void filterOpenAPI(OpenAPI openAPI) {
        if (!inputModelSchemas.isEmpty()) {
            inputModelSchemas.forEach(modelSchema -> MergeUtil.merge(openAPI, modelSchema));
            addWorkflowdataSchemaReferences(openAPI);
        } else {
            removeJsonModelInfoSchemaReferences(openAPI);
        }
    }

    private static void removeJsonModelInfoSchemaReferences(OpenAPI openAPI) {
        openAPI.getPaths()
                .getPathItems()
                .values()
                .forEach(pathItem -> getMediaTypes(pathItem).stream()
                        .filter(mediaType -> mediaType.getSchema() != null)
                        .filter(mediaType -> "#/components/schemas/JsonNodeModelInput".equals(mediaType.getSchema().getRef()))
                        .forEach(mediaType -> mediaType.setSchema(OASFactory.createSchema().type(Schema.SchemaType.OBJECT))));
    }

    private static void addWorkflowdataSchemaReferences(OpenAPI openAPI) {
        Schema schema = OASFactory.createSchema().ref(SWFConstants.INPUT_MODEL_REF);

        openAPI.getPaths()
                .getPathItems()
                .values()
                .forEach(pathItem -> getMediaTypes(pathItem)
                        .forEach(mediaType -> mediaType.setSchema(schema)));
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
}
