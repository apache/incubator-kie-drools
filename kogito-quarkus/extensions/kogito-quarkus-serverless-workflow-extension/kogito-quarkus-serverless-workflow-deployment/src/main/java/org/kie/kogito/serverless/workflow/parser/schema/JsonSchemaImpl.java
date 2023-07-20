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
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.openapi.models.media.Schema;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;
import org.kie.kogito.serverless.workflow.io.URIContentLoaderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.smallrye.openapi.api.constants.OpenApiConstants;
import io.smallrye.openapi.api.models.media.SchemaImpl;

/**
 * Holder class to map and deserialize a JSON Schema structure to OpenAPI Schema
 * Exists just to make it easy for JSON Deserializers to convert the given JSON Schema into an OpenAPI Schema.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonSchemaImpl extends SchemaImpl {

    private static final Logger logger = LoggerFactory.getLogger(JsonSchemaImpl.class);

    @JsonSetter("$id")
    public void setId(String id) {
        RefSchemas.baseURI(id);
    }

    @JsonSetter("$ref")
    @Override
    public void setRef(String ref) {
        if (ref != null && !ref.startsWith("#")) {
            try (InputStream is = URIContentLoaderFactory.builder(new URI(ref)).withBaseURI(RefSchemas.getBaseURI()).build().getInputStream()) {
                JsonSchemaImpl schema = ObjectMapperFactory.get().readValue(is.readAllBytes(), JsonSchemaImpl.class);
                String key;
                if (schema.getTitle() == null) {
                    key = RefSchemas.getKey();
                    schema.title(key);
                } else {
                    key = schema.getTitle();
                }
                if (key != null) {
                    RefSchemas.get().put(key, schema);
                }
                ref = OpenApiConstants.REF_PREFIX_SCHEMA + key;
            } catch (URISyntaxException | IOException e) {
                // if not a valid uri, let super handle it
                logger.info("Error loading ref {}", ref, e);
            }
        }
        super.setRef(ref);
    }

    @JsonDeserialize(as = JsonSchemaImpl.class)
    @Override
    public Schema getItems() {
        return super.getItems();
    }

    @JsonDeserialize(contentAs = JsonSchemaImpl.class)
    @Override
    public List<Schema> getAllOf() {
        return super.getAllOf();
    }

    @JsonDeserialize(contentAs = JsonSchemaImpl.class)
    @Override
    public List<Schema> getAnyOf() {
        return super.getAnyOf();
    }

    @JsonDeserialize(contentAs = JsonSchemaImpl.class)
    @Override
    public List<Schema> getOneOf() {
        return super.getOneOf();
    }

    @JsonDeserialize(contentAs = JsonSchemaImpl.class)
    @Override
    public Map<String, Schema> getProperties() {
        return super.getProperties();
    }

    @JsonDeserialize(as = JsonSchemaImpl.class)
    @Override
    public Schema getAdditionalPropertiesSchema() {
        return super.getAdditionalPropertiesSchema();
    }

    @JsonDeserialize(as = JsonSchemaImpl.class)
    @Override
    public Schema getNot() {
        return super.getNot();
    }
}
