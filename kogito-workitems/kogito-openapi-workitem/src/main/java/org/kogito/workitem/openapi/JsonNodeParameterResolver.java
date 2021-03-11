/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kogito.workitem.openapi;

import java.util.Iterator;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

/**
 * Process the Function definition parameters as {@link JsonNode}.
 * Any JsonPath expressions can be applied against a given input model, which would be converted to a JsonNode.
 * <p/>
 * This class is meant to be used only by generated <strong>Service Tasks Work Item Handler</strong> code in runtime.
 */
public class JsonNodeParameterResolver implements OpenApiParameterResolver {

    private static final Configuration jsonPathConfig = Configuration
            .builder()
            .mappingProvider(new JacksonMappingProvider())
            .jsonProvider(new JacksonJsonNodeJsonProvider())
            .build();

    private final JsonNode parameterDefinition;

    private final JsonNodeParser parser;

    public JsonNodeParameterResolver(final String parameterDefinition) {
        this.parser = new JsonNodeParser(new ObjectMapper());

        this.parameterDefinition = parser.parse(parameterDefinition);
    }

    /**
     * Used by the process engine to compute the parameter.
     * The input data is the `workflowdata` process variable, which will be computed as a {@link JsonNode}.
     * Each node will be processed and any JsonPath expression, computed.
     *
     * @param inputModel the `workflowdata` process variable
     * @return A JsonNode object with all expressions computed
     */
    @Override
    public JsonNode apply(Object inputModel) {
        return this.processInputModel(this.parser.parse(inputModel), this.parameterDefinition);
    }

    private JsonNode processInputModel(final JsonNode inputModel, final JsonNode parameterDefinition) {
        if (parameterDefinition.isArray()) {
            final JsonNode processedDefinition = parameterDefinition.deepCopy();
            for (int index = 0; index < processedDefinition.size(); index++) {
                ((ArrayNode) processedDefinition).set(index, this.processInputModel(inputModel, processedDefinition.get(index)));
            }
            return processedDefinition;
        } else if (parameterDefinition.isValueNode()) {
            final String jsonPathExpr = parameterDefinition.asText();
            if (parser.isJsonPath(jsonPathExpr)) {
                return JsonPath.using(jsonPathConfig).parse(inputModel).read(jsonPathExpr, JsonNode.class);
            }
            return parameterDefinition.deepCopy();
        }

        final JsonNode processedDefinition = parameterDefinition.deepCopy();
        final Iterator<Entry<String, JsonNode>> fields = processedDefinition.fields();
        while (fields.hasNext()) {
            final Entry<String, JsonNode> jsonField = fields.next();
            ((ObjectNode) processedDefinition).replace(jsonField.getKey(), this.processInputModel(inputModel, jsonField.getValue()));
        }
        return processedDefinition;
    }
}
