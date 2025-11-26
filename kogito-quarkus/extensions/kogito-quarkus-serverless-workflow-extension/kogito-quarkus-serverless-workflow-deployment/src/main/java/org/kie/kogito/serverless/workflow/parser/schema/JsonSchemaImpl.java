/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.serverless.workflow.parser.schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.openapi.models.media.Schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.smallrye.openapi.api.models.media.SchemaImpl;

import static org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils.DEFS_PREFIX;

/**
 * Holder class to map and deserialize a JSON Schema structure to OpenAPI Schema
 * Exists just to make it easy for JSON Deserializers to convert the given JSON Schema into an OpenAPI Schema.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonSchemaImpl extends SchemaImpl {

    public static final String REF_PREFIX_SCHEMA = "#/components/schemas/";

    private Map<String, Schema> defs;

    @JsonSetter("type")
    public void setType(JsonNode typeNode) {
        List<Schema.SchemaType> types = new ArrayList<>();
        if (typeNode.isArray()) {
            for (JsonNode node : typeNode) {
                types.add(from(node));
            }
        } else if (typeNode.isTextual()) {
            types.add(from(typeNode));
        }
        super.setType(types);
    }

    private static SchemaType from(JsonNode node) {
        return Schema.SchemaType.valueOf(node.asText().toUpperCase());
    }

    @JsonSetter("$defs")
    @JsonDeserialize(contentAs = JsonSchemaImpl.class)
    public void setDefs(Map<String, Schema> defs) {
        this.defs = defs;
    }

    @JsonSetter("$ref")
    @Override
    public void setRef(String ref) {
        super.setRef(ref.replace(DEFS_PREFIX, REF_PREFIX_SCHEMA));
    }

    public Map<String, Schema> getDefs() {
        return defs;
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
