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

import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import org.eclipse.microprofile.openapi.OASFactory;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.everit.json.schema.loader.SchemaClient;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;
import org.kie.kogito.serverless.workflow.SWFConstants;
import org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.serverlessworkflow.api.Workflow;

public class OpenApiModelSchemaGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenApiModelSchemaGenerator.class);

    private final Workflow workflow;

    public OpenApiModelSchemaGenerator(Workflow workflow) {
        this.workflow = workflow;
    }

    private Schema generateInputModel() {
        if (workflow.getDataInputSchema() == null ||
                workflow.getDataInputSchema().getSchema() == null ||
                workflow.getDataInputSchema().getSchema().isEmpty()) {
            return null;
        }
        try {
            final URI inputSchemaURI = new URI(workflow.getDataInputSchema().getSchema());
            return fromJsonSchemaToOpenApiSchema(inputSchemaURI.toString(), "");
        } catch (URISyntaxException e) {
            LOGGER.warn("Invalid Data Input Schema for workflow {}. Only valid URIs are supported at this time.", workflow.getId());
        }
        return null;
    }

    /**
     * Converts a given JSON Schema URI to OpenAPI Schema definition.
     * <p/>
     * It will try to load the file into bytes, load all the schema inheritance and provide the caller
     * with a reference to an OpenAPI Schema object.
     *
     * @param jsonSchemaURI the given JSON Schema URI
     * @param authRef the Authentication Reference information to fetch the JSON Schema URI if needed
     * @return The @{@link Schema} object
     */
    private Schema fromJsonSchemaToOpenApiSchema(String jsonSchemaURI, String authRef) {
        if (jsonSchemaURI != null) {
            final Optional<byte[]> bytes = ServerlessWorkflowUtils.loadResourceFile(workflow, Optional.empty(), jsonSchemaURI, authRef);
            if (bytes.isPresent()) {
                // SchemaLoader will load all the references from other files into the schema
                final JSONObject rawSchema = new JSONObject(new JSONTokener(new String(bytes.get())));
                final SchemaLoader schemaLoader = SchemaLoader.builder()
                        .schemaJson(rawSchema)
                        .resolutionScope(jsonSchemaURI)
                        .schemaClient(SchemaClient.classPathAwareClient())
                        .build();
                try {
                    final ObjectMapper objectMapper = ObjectMapperFactory.get();
                    // the workflowdata input model now has inherited from the given JSON Schema
                    return objectMapper.readValue(schemaLoader.load().build().toString(), JsonSchemaImpl.class);
                } catch (JsonProcessingException e) {
                    throw new UncheckedIOException("Error deserializing JSON Schema " + jsonSchemaURI + " for workflow " + workflow.getId(), e);
                }
            }
        }
        return null;
    }

    public OpenAPI generateOpenAPIModelSchema() {
        return OASFactory.createOpenAPI()
                .components(OASFactory.createComponents().addSchema(SWFConstants.DEFAULT_WORKFLOW_VAR, generateInputModel()))
                .openapi("workflowmodelschema");
    }

}
