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

import static org.drools.scenariosimulation.api.utils.ConstantsHolder.VALUE;

public abstract class AbstractExpressionEvaluator implements ExpressionEvaluator {

    protected boolean commonEvaluateUnaryExpression(Object rawExpression, Object resultValue, Class<?> resultClass) {
        if (isStructuredResult(resultClass)) {
            return verifyResult(rawExpression, resultValue);
        } else {
            return internalUnaryEvaluation((String) rawExpression, resultValue, resultClass, false);
        }
    }

    protected Object commonEvaluationLiteralExpression(String className, List<String> genericClasses, String raw) {
        if (isStructuredInput(className)) {
            return convertResult(raw, className, genericClasses);
        } else {
            return internalLiteralEvaluation(raw, className);
        }
    }

    /**
     * Check if resultClass represents a structured result
     * @param resultClass
     * @return
     */
    protected boolean isStructuredResult(Class<?> resultClass) {
        return resultClass != null && ScenarioSimulationSharedUtils.isCollection(resultClass.getCanonicalName());
    }

    /**
     * Check if className represents a structured input
     * @param className
     * @return
     */
    protected boolean isStructuredInput(String className) {
        return ScenarioSimulationSharedUtils.isCollection(className);
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
            if (isSimpleTypeNode(node)) {
                String generic = genericClasses.get(genericClasses.size() - 1);
                Object value = internalLiteralEvaluation(getSimpleTypeNodeTextValue(node), generic);
                toReturn.add(value);
            } else {
                String genericClassName = ScenarioSimulationSharedUtils.isMap(className) ? className : genericClasses.get(genericClasses.size() - 1);
                Object listElement = createObject(genericClassName, genericClasses);
                Object returnedObject = createAndFillObject((ObjectNode) node, listElement, genericClassName, genericClasses);
                toReturn.add(returnedObject);
            }
        }
        return toReturn;
    }

    protected Object createAndFillObject(ObjectNode json, Object toReturn, String className, List<String> genericClasses) {
        Iterator<Map.Entry<String, JsonNode>> fields = json.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> element = fields.next();
            String key = element.getKey();
            JsonNode jsonNode = element.getValue();
            // if is a simple value just return the parsed result
            if (isSimpleTypeNode(jsonNode)) {
                Map.Entry<String, List<String>> fieldDescriptor = getFieldClassNameAndGenerics(toReturn, key, className, genericClasses);
                Object value = internalLiteralEvaluation(getSimpleTypeNodeTextValue(jsonNode), fieldDescriptor.getKey());
                setField(toReturn, key, value);
                return toReturn;
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
            } else if (!isEmptyText(jsonNode)) {
                Map.Entry<String, List<String>> fieldDescriptor = getFieldClassNameAndGenerics(toReturn, key, className, genericClasses);
                setField(toReturn, key, internalLiteralEvaluation(jsonNode.textValue(), fieldDescriptor.getKey()));
            } else {
                // empty strings are skipped
            }
        }
        return toReturn;
    }

    protected boolean verifyResult(Object rawExpression, Object resultRaw) {
        if (resultRaw != null && !(resultRaw instanceof List) && !(resultRaw instanceof Map)) {
            throw new IllegalArgumentException("A list or map was expected");
        }
        if (!(rawExpression instanceof String)) {
            throw new IllegalArgumentException("Malformed raw data");
        }
        String raw = (String) rawExpression;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode jsonNode = objectMapper.readTree(raw);
            if (jsonNode.isArray()) {
                return verifyList((ArrayNode) jsonNode, (List) resultRaw);
            } else if (jsonNode.isObject()) {
                return verifyObject((ObjectNode) jsonNode, resultRaw);
            }
            throw new IllegalArgumentException("Malformed raw data");
        } catch (IOException e) {
            throw new IllegalArgumentException("Malformed raw data", e);
        }
    }

    protected boolean verifyList(ArrayNode json, List resultRaw) {
        if (resultRaw == null) {
            return isListEmpty(json);
        }
        for (JsonNode node : json) {
            boolean success = false;
            for (Object result : resultRaw) {
                boolean simpleTypeNode = isSimpleTypeNode(node);
                if (simpleTypeNode && internalUnaryEvaluation(getSimpleTypeNodeTextValue(node), result, result.getClass(), true)) {
                    success = true;
                } else if (!simpleTypeNode && verifyObject((ObjectNode) node, result)) {
                    success = true;
                }
            }
            if (!success) {
                return false;
            }
        }
        return true;
    }

    protected boolean verifyObject(ObjectNode json, Object resultRaw) {
        if (resultRaw == null) {
            return isObjectEmpty(json);
        }
        Iterator<Map.Entry<String, JsonNode>> fields = json.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> element = fields.next();
            String key = element.getKey();
            JsonNode jsonNode = element.getValue();
            Object fieldValue = extractFieldValue(resultRaw, key);
            Class<?> fieldClass = fieldValue != null ? fieldValue.getClass() : null;

            if (isSimpleTypeNode(jsonNode)) {
                if (!internalUnaryEvaluation(getSimpleTypeNodeTextValue(jsonNode), fieldValue, fieldClass, true)) {
                    return false;
                }
            } else if (jsonNode.isArray()) {
                if (!verifyList((ArrayNode) jsonNode, (List) fieldValue)) {
                    return false;
                }
            } else if (jsonNode.isObject()) {
                if (!verifyObject((ObjectNode) jsonNode, fieldValue)) {
                    return false;
                }
            } else {
                if (!internalUnaryEvaluation(jsonNode.textValue(), fieldValue, fieldClass, true)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Verify if given json node has all final values as empty strings
     * @param json
     * @return
     */
    protected boolean isNodeEmpty(JsonNode json) {
        if (json.isArray()) {
            return isListEmpty((ArrayNode) json);
        } else if (json.isObject()) {
            return isObjectEmpty((ObjectNode) json);
        } else {
            return isEmptyText(json);
        }
    }

    /**
     * Verify if all elements of given json array are empty
     * @param json
     * @return
     */
    protected boolean isListEmpty(ArrayNode json) {
        for (JsonNode node : json) {
            if (!isNodeEmpty(node)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Verify if all fields of given json object are empty
     * @param json
     * @return
     */
    protected boolean isObjectEmpty(ObjectNode json) {
        Iterator<Map.Entry<String, JsonNode>> fields = json.fields();
        while (fields.hasNext()) {
            JsonNode element = fields.next().getValue();
            if (!isNodeEmpty(element)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Verify if given json node text is empty
     * @param jsonNode
     * @return
     */
    protected boolean isEmptyText(JsonNode jsonNode) {
        return jsonNode.textValue() == null || jsonNode.textValue().isEmpty();
    }

    /**
     * A node represent a simple type if it is an object with only one field named "value"
     * @param jsonNode
     * @return
     */
    protected boolean isSimpleTypeNode(JsonNode jsonNode) {
        if (!jsonNode.isObject()) {
            return false;
        }
        ObjectNode objectNode = (ObjectNode) jsonNode;
        int numberOfFields = objectNode.size();
        return numberOfFields == 1 && objectNode.has(VALUE);
    }

    /**
     * Return text value of a simple type node
     * @param jsonNode
     * @return
     */
    protected String getSimpleTypeNodeTextValue(JsonNode jsonNode) {
        if (!isSimpleTypeNode(jsonNode)) {
            throw new IllegalArgumentException("Parameter does not contains a simple type");
        }
        return jsonNode.get(VALUE).textValue();
    }

    abstract protected boolean internalUnaryEvaluation(String rawExpression, Object resultValue, Class<?> resultClass, boolean skipEmptyString);

    abstract protected Object internalLiteralEvaluation(String raw, String className);

    abstract protected Object extractFieldValue(Object result, String fieldName);

    abstract protected Object createObject(String className, List<String> genericClasses);

    abstract protected void setField(Object toReturn, String fieldName, Object fieldValue);

    /**
     * Return a pair with field className as key and list of generics as value
     * @param element : instance to be populated
     * @param fieldName : field to analyze
     * @param className : canonical class name of instance
     * @param genericClasses : list of generics related to this field
     * @return
     */
    abstract protected Map.Entry<String, List<String>> getFieldClassNameAndGenerics(Object element, String fieldName, String className, List<String> genericClasses);
}
