/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.scenariosimulation.backend.expression;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.drools.scenariosimulation.api.utils.ScenarioSimulationSharedUtils;

public abstract class AbstractExpressionEvaluator implements ExpressionEvaluator {

    protected boolean commonEvaluateUnaryExpression(Object rawExpression, Object resultValue, Class<?> resultClass) {
        if (resultClass != null && ScenarioSimulationSharedUtils.isCollection(resultClass.getCanonicalName())) {
            return verifyResult(rawExpression, resultValue, resultClass);
        } else {
            return internalUnaryEvaluation((String) rawExpression, resultValue, resultClass, false);
        }
    }

    protected Object commonEvaluationLiteralExpression(String className, List<String> genericClasses, String raw) {
        if (ScenarioSimulationSharedUtils.isCollection(className)) {
            return convertResult(raw, className, genericClasses);
        } else {
            return internalLiteralEvaluation(raw, className);
        }
    }

    protected Object convertResult(String rawString, String className, List<String> genericClasses) {

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(rawString);
            if (jsonNode.isArray()) {
                return createAndFillList((ArrayNode) jsonNode, new ArrayList<>(), className, genericClasses);
            } else if (jsonNode.isObject()) {
                return createAndFillObject((ObjectNode) jsonNode,
                                           createObject(className, genericClasses),
                                           className,
                                           genericClasses);
            }
            throw new IllegalArgumentException("Malformed raw data");
        } catch (IOException e) {
            throw new IllegalArgumentException("Malformed raw data", e);
        }
    }

    protected List<Object> createAndFillList(ArrayNode json, List<Object> toReturn, String className, List<String> genericClasses) {
        for (JsonNode node : json) {
            String genericClassName = ScenarioSimulationSharedUtils.isMap(className) ? className : genericClasses.get(genericClasses.size() - 1);
            Object listElement = createObject(genericClassName, genericClasses);
            Object returnedObject = createAndFillObject((ObjectNode) node, listElement, genericClassName, genericClasses);
            toReturn.add(returnedObject);
        }
        return toReturn;
    }

    protected Object createAndFillObject(ObjectNode json, Object toReturn, String className, List<String> genericClasses) {
        Iterator<Map.Entry<String, JsonNode>> fields = json.fields();
        int numberOfFields = json.size();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> element = fields.next();
            String key = element.getKey();
            JsonNode jsonNode = element.getValue();
            // if is a simple value just return the parsed result
            if (numberOfFields == 1 && "value".equals(key)) {
                return internalLiteralEvaluation(jsonNode.textValue(), genericClasses.get(0));
            }

            if (jsonNode.isArray()) {
                List<Object> nestedList = new ArrayList<>();
                Map.Entry<String, List<String>> fieldDescriptor = getFieldClassNameAndGenerics(toReturn, key, className, genericClasses);
                List<Object> returnedList = createAndFillList((ArrayNode) jsonNode, nestedList, fieldDescriptor.getKey(), fieldDescriptor.getValue());
                setField(toReturn, key, returnedList);
            } else if (jsonNode.isObject()) {
                Map.Entry<String, List<String>> fieldDescriptor = getFieldClassNameAndGenerics(toReturn, key, className, genericClasses);
                Object nestedObject = createObject(fieldDescriptor.getKey(), fieldDescriptor.getValue());
                Object returnedObject = createAndFillObject((ObjectNode) jsonNode, nestedObject, fieldDescriptor.getKey(), fieldDescriptor.getValue());
                setField(toReturn, key, returnedObject);
            } else if (jsonNode.textValue() != null && !jsonNode.textValue().isEmpty()) {
                Map.Entry<String, List<String>> fieldDescriptor = getFieldClassNameAndGenerics(toReturn, key, className, genericClasses);
                setField(toReturn, key, internalLiteralEvaluation(jsonNode.textValue(), fieldDescriptor.getKey()));
            } else {
                // empty strings are skipped
            }
        }
        return toReturn;
    }

    protected boolean verifyResult(Object rawValue, Object resultRaw, Class<?> resultClass) {
        if (!(resultRaw instanceof List) && !(resultRaw instanceof Map)) {
            throw new IllegalArgumentException("A list was expected");
        }
        if (!(rawValue instanceof String)) {
            throw new IllegalArgumentException("Malformed raw data");
        }
        String raw = (String) rawValue;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode jsonNode = objectMapper.readTree(raw);
            if (jsonNode.isArray()) {
                return verifyList((ArrayNode) jsonNode, (List) resultRaw, resultClass);
            } else if (jsonNode.isObject()) {
                return verifyObject((ObjectNode) jsonNode, resultRaw, resultClass);
            }
            throw new IllegalArgumentException("Malformed raw data");
        } catch (IOException e) {
            throw new IllegalArgumentException("Malformed raw data", e);
        }
    }

    protected boolean verifyList(ArrayNode json, List resultRaw, Class<?> resultClass) {

        for (JsonNode node : json) {
            boolean success = false;
            for (Object result : resultRaw) {
                if (verifyObject((ObjectNode) node, result, resultClass)) {
                    success = true;
                }
            }
            if (!success) {
                return false;
            }
        }
        return true;
    }

    protected boolean verifyObject(ObjectNode json, Object result, Class<?> resultClass) {
        Iterator<Map.Entry<String, JsonNode>> fields = json.fields();
        int numberOfFields = json.size();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> element = fields.next();
            String key = element.getKey();
            JsonNode jsonNode = element.getValue();
            // if is a simple value just return the parsed result
            if (numberOfFields == 1 && "value".equals(key)) {
                return internalUnaryEvaluation(jsonNode.textValue(), result, result.getClass(), true);
            }

            Object fieldValue = extractFieldValue(result, key);
            if (jsonNode.isArray()) {
                if (!verifyList((ArrayNode) jsonNode, (List) fieldValue, fieldValue.getClass())) {
                    return false;
                }
            } else if (jsonNode.isObject()) {
                if (!verifyObject((ObjectNode) jsonNode, fieldValue, fieldValue.getClass())) {
                    return false;
                }
            } else {
                if (!internalUnaryEvaluation(jsonNode.textValue(), fieldValue, fieldValue.getClass(), true)) {
                    return false;
                }
            }
        }
        return true;
    }

    abstract protected boolean internalUnaryEvaluation(String rawExpression, Object resultValue, Class<?> resultClass, boolean skipEmptyString);

    abstract protected Object internalLiteralEvaluation(String raw, String className);

    abstract protected Object extractFieldValue(Object result, String fieldName);

    abstract protected Object createObject(String className, List<String> genericClasses);

    abstract protected void setField(Object toReturn, String fieldName, Object fieldValue);

    abstract protected Map.Entry<String, List<String>> getFieldClassNameAndGenerics(Object element, String fieldName, String className, List<String> genericClasses);
}
