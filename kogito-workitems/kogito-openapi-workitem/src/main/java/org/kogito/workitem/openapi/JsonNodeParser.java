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

import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;

/**
 * Utility class to parse anything into a valid {@link JsonNode}
 */
public final class JsonNodeParser {

    private static final String JSONPATH_REGEX = "^((\\$\\[).*|(\\$\\.).*)";
    private final ObjectMapper objectMapper;
    private final Pattern jsonPathRegexPattern;

    protected JsonNodeParser(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.jsonPathRegexPattern = Pattern.compile(JSONPATH_REGEX, Pattern.CASE_INSENSITIVE);
    }

    protected JsonNode parse(final Object input) {
        if (input instanceof JsonNode) {
            return (JsonNode) input;
        }
        if (input instanceof String) {
            if (this.isJsonPath((String) input)) {
                return TextNode.valueOf((String) input);
            }
            try {
                return objectMapper.readTree((String) input);
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException("Failed to parse input model from ordinary String to Json tree", e);
            }
        }
        return objectMapper.valueToTree(input);
    }

    boolean isJsonPath(final String expression) {
        return this.jsonPathRegexPattern.matcher(expression).matches();
    }
}
