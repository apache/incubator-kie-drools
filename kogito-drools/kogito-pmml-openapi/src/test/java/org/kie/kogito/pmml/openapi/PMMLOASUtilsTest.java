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
package org.kie.kogito.pmml.openapi;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.enums.FIELD_USAGE_TYPE;
import org.kie.pmml.api.models.Interval;
import org.kie.pmml.api.models.MiningField;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BigIntegerNode;
import com.fasterxml.jackson.databind.node.DecimalNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.FloatNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ShortNode;
import com.fasterxml.jackson.databind.node.TextNode;

import io.smallrye.openapi.runtime.io.JsonUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kie.kogito.pmml.openapi.PMMLOASUtils.INFINITY_SYMBOL;
import static org.kie.kogito.pmml.openapi.api.PMMLOASResult.BOOLEAN;
import static org.kie.kogito.pmml.openapi.api.PMMLOASResult.DOUBLE;
import static org.kie.kogito.pmml.openapi.api.PMMLOASResult.ENUM;
import static org.kie.kogito.pmml.openapi.api.PMMLOASResult.FLOAT;
import static org.kie.kogito.pmml.openapi.api.PMMLOASResult.FORMAT;
import static org.kie.kogito.pmml.openapi.api.PMMLOASResult.INTEGER;
import static org.kie.kogito.pmml.openapi.api.PMMLOASResult.INTERVALS;
import static org.kie.kogito.pmml.openapi.api.PMMLOASResult.MAXIMUM;
import static org.kie.kogito.pmml.openapi.api.PMMLOASResult.MINIMUM;
import static org.kie.kogito.pmml.openapi.api.PMMLOASResult.NUMBER;
import static org.kie.kogito.pmml.openapi.api.PMMLOASResult.OBJECT;
import static org.kie.kogito.pmml.openapi.api.PMMLOASResult.PROPERTIES;
import static org.kie.kogito.pmml.openapi.api.PMMLOASResult.STRING;
import static org.kie.kogito.pmml.openapi.api.PMMLOASResult.TYPE;

class PMMLOASUtilsTest {

    @Test
    void isRequired() {
        MiningField toVerify = new MiningField(null,
                FIELD_USAGE_TYPE.PREDICTED,
                null,
                null,
                null,
                null,
                null);
        assertFalse(PMMLOASUtils.isRequired(toVerify));
        toVerify = new MiningField(null,
                FIELD_USAGE_TYPE.TARGET,
                null,
                null,
                null,
                null,
                null);
        assertFalse(PMMLOASUtils.isRequired(toVerify));
        toVerify = new MiningField(null,
                null,
                null,
                null,
                "MISSING_VALUE_REPLACEMENT",
                null,
                null);
        assertFalse(PMMLOASUtils.isRequired(toVerify));
        toVerify = new MiningField(null,
                null,
                null,
                null,
                null,
                null,
                null);
        assertTrue(PMMLOASUtils.isRequired(toVerify));
    }

    @Test
    void isPredicted() {
        MiningField toVerify = new MiningField(null,
                FIELD_USAGE_TYPE.PREDICTED,
                null,
                null,
                null,
                null,
                null);
        assertTrue(PMMLOASUtils.isPredicted(toVerify));
        toVerify = new MiningField(null,
                FIELD_USAGE_TYPE.TARGET,
                null,
                null,
                null,
                null,
                null);
        assertTrue(PMMLOASUtils.isPredicted(toVerify));
        toVerify = new MiningField(null,
                null,
                null,
                null,
                null,
                null,
                null);
        assertFalse(PMMLOASUtils.isPredicted(toVerify));
        Arrays.stream(FIELD_USAGE_TYPE.values())
                .filter(usageType -> usageType != FIELD_USAGE_TYPE.TARGET
                        && usageType != FIELD_USAGE_TYPE.PREDICTED)
                .forEach(usageType -> {
                    MiningField miningField = new MiningField(null,
                            usageType,
                            null,
                            null,
                            null,
                            null,
                            null);
                    assertFalse(PMMLOASUtils.isPredicted(miningField));
                });
    }

    @Test
    void getMappedType() {
        Arrays.stream(DATA_TYPE.values()).forEach(dataType -> {
            String expected;
            switch (dataType) {
                case DATE:
                case DATE_TIME:
                case STRING:
                    expected = STRING;
                    break;
                case BOOLEAN:
                    expected = BOOLEAN;
                    break;
                case INTEGER:
                    expected = INTEGER;
                    break;
                default:
                    expected = NUMBER;
            }
            assertEquals(expected, PMMLOASUtils.getMappedType(dataType));
        });
    }

    @Test
    void getMappedFormat() {
        Arrays.stream(DATA_TYPE.values()).forEach(dataType -> {
            String expected;
            switch (dataType) {
                case DOUBLE:
                    expected = DOUBLE;
                    break;
                case FLOAT:
                    expected = FLOAT;
                    break;
                default:
                    expected = null;
            }
            assertEquals(expected, PMMLOASUtils.getMappedFormat(dataType));
        });
    }

    @Test
    void addIntervals() {
        ObjectNode typeFieldNode = JsonUtil.objectNode();
        PMMLOASUtils.addIntervals(typeFieldNode, Collections.emptyList());
        assertEquals(0, typeFieldNode.size());
        //
        Interval interval = new Interval(-34.23, null);
        PMMLOASUtils.addIntervals(typeFieldNode, Collections.singletonList(interval));
        assertNotNull(typeFieldNode.get(MINIMUM));
        NumericNode numericNode = (NumericNode) typeFieldNode.get(MINIMUM);
        assertEquals(interval.getLeftMargin().doubleValue(), numericNode.asDouble());
        assertNull(typeFieldNode.get(MAXIMUM));
        assertNull(typeFieldNode.get(INTERVALS));
        //
        typeFieldNode = JsonUtil.objectNode();
        interval = new Interval(null, 35.0);
        PMMLOASUtils.addIntervals(typeFieldNode, Collections.singletonList(interval));
        assertNull(typeFieldNode.get(MINIMUM));
        assertNotNull(typeFieldNode.get(MAXIMUM));
        numericNode = (NumericNode) typeFieldNode.get(MAXIMUM);
        assertEquals(interval.getRightMargin().doubleValue(), numericNode.asDouble());
        assertNull(typeFieldNode.get(INTERVALS));
        //
        typeFieldNode = JsonUtil.objectNode();
        interval = new Interval(-34.23, 35.0);
        PMMLOASUtils.addIntervals(typeFieldNode, Collections.singletonList(interval));
        assertNotNull(typeFieldNode.get(MINIMUM));
        numericNode = (NumericNode) typeFieldNode.get(MINIMUM);
        assertEquals(interval.getLeftMargin().doubleValue(), numericNode.asDouble());
        assertNotNull(typeFieldNode.get(MAXIMUM));
        numericNode = (NumericNode) typeFieldNode.get(MAXIMUM);
        assertEquals(interval.getRightMargin().doubleValue(), numericNode.asDouble());
        assertNull(typeFieldNode.get(INTERVALS));
        //
        typeFieldNode = JsonUtil.objectNode();
        List<Interval> intervals = IntStream.range(0, 3)
                .mapToObj(i -> new Interval(i * 2 + 3, i * 3 + 4))
                .collect(Collectors.toList());
        PMMLOASUtils.addIntervals(typeFieldNode, intervals);
        assertNull(typeFieldNode.get(MINIMUM));
        assertNull(typeFieldNode.get(MAXIMUM));
        assertNotNull(typeFieldNode.get(INTERVALS));
        ArrayNode intervalsNode = (ArrayNode) typeFieldNode.get(INTERVALS);
        List<JsonNode> nodeList = StreamSupport
                .stream(intervalsNode.spliterator(), false)
                .collect(Collectors.toList());
        nodeList.forEach(intervalNode -> assertTrue(intervalNode instanceof TextNode));
        intervals.forEach(intervalValue -> {
            String leftMargin = intervalValue.getLeftMargin() != null ? intervalValue.getLeftMargin().toString() : "-" + INFINITY_SYMBOL;
            String rightMargin = intervalValue.getRightMargin() != null ? intervalValue.getRightMargin().toString() : INFINITY_SYMBOL;
            String expected = String.format("%s %s", leftMargin, rightMargin);
            assertTrue(nodeList.stream().anyMatch(node -> expected.equals(node.asText())));
        });
    }

    @Test
    void addToSetNode() {
        String fieldName = "fieldName";
        DATA_TYPE dataType = DATA_TYPE.DOUBLE;
        ObjectNode setNode = PMMLOASUtils.createSetNode();
        ObjectNode propertiesNode = (ObjectNode) setNode.get(PROPERTIES);
        assertTrue(propertiesNode.isEmpty());
        PMMLOASUtils.addToSetNode(fieldName, dataType, Collections.emptyList(), setNode);
        assertNotNull(propertiesNode.get(fieldName));
        ObjectNode fieldNameNode = (ObjectNode) propertiesNode.get(fieldName);
        assertNotNull(fieldNameNode.get(TYPE));
        assertEquals(NUMBER, fieldNameNode.get(TYPE).asText());
        assertNotNull(fieldNameNode.get(FORMAT));
        assertEquals(DOUBLE, fieldNameNode.get(FORMAT).asText());
        assertNull(fieldNameNode.get(ENUM));
        //
        List<String> allowedValues = IntStream.range(0, 3)
                .mapToObj(it -> "VALUE" + it)
                .collect(Collectors.toList());
        setNode = PMMLOASUtils.createSetNode();
        propertiesNode = (ObjectNode) setNode.get(PROPERTIES);
        assertTrue(propertiesNode.isEmpty());
        PMMLOASUtils.addToSetNode(fieldName, dataType, allowedValues, setNode);
        assertNotNull(propertiesNode.get(fieldName));
        fieldNameNode = (ObjectNode) propertiesNode.get(fieldName);
        assertNotNull(fieldNameNode.get(TYPE));
        assertEquals(NUMBER, fieldNameNode.get(TYPE).asText());
        assertNotNull(fieldNameNode.get(FORMAT));
        assertEquals(DOUBLE, fieldNameNode.get(FORMAT).asText());
        ArrayNode availableValuesNode = (ArrayNode) fieldNameNode.get(ENUM);
        assertEquals(allowedValues.size(), availableValuesNode.size());
        List<JsonNode> nodeList = StreamSupport
                .stream(availableValuesNode.spliterator(), false)
                .collect(Collectors.toList());
        nodeList.forEach(availableValueNode -> assertTrue(availableValueNode instanceof TextNode));
        allowedValues.forEach(allowedValue -> assertTrue(nodeList.stream()
                .anyMatch(availableValueNode -> availableValueNode.asText().equals(allowedValue))));
    }

    @Test
    void conditionallyCreateEnumNode() {
        ObjectNode parentNode = JsonUtil.objectNode();
        ArrayNode created = PMMLOASUtils.conditionallyCreateEnumNode(parentNode);
        assertNotNull(created);
        assertEquals(0, created.size());
        JsonNode jsonNode = parentNode.get(ENUM);
        assertNotNull(jsonNode);
        assertEquals(created, jsonNode);
        ArrayNode notCreated = PMMLOASUtils.conditionallyCreateEnumNode(parentNode);
        assertNotNull(notCreated);
        assertEquals(created, notCreated);
    }

    @Test
    void createSetNodeInParent() {
        ObjectNode parentNode = JsonUtil.objectNode();
        String nodeToCreate = "nodeToCreate";
        ObjectNode retrieved = PMMLOASUtils.createSetNodeInParent(parentNode, nodeToCreate);
        commonValidateSetNode(retrieved);
        assertEquals(retrieved, parentNode.get(nodeToCreate));
    }

    @Test
    void createSetNode() {
        ObjectNode retrieved = PMMLOASUtils.createSetNode();
        commonValidateSetNode(retrieved);
    }

    @Test
    void getNumericNode() {
        Number number = 1;
        NumericNode retrieved = PMMLOASUtils.getNumericNode(number);
        commonValidateNumericNode(retrieved, number);
        number = 1.0f;
        retrieved = PMMLOASUtils.getNumericNode(number);
        commonValidateNumericNode(retrieved, number);
        number = 0.34;
        retrieved = PMMLOASUtils.getNumericNode(number);
        commonValidateNumericNode(retrieved, number);
        number = new BigInteger("34343734734834872362532352352");
        retrieved = PMMLOASUtils.getNumericNode(number);
        commonValidateNumericNode(retrieved, number);
        number = Short.parseShort("1");
        retrieved = PMMLOASUtils.getNumericNode(number);
        commonValidateNumericNode(retrieved, number);
        number = Long.parseLong("343437347348348");
        retrieved = PMMLOASUtils.getNumericNode(number);
        commonValidateNumericNode(retrieved, number);
        number = new BigDecimal("343437347348348.2345345634634");
        retrieved = PMMLOASUtils.getNumericNode(number);
        commonValidateNumericNode(retrieved, number);
    }

    private void commonValidateSetNode(ObjectNode toValidate) {
        assertNotNull(toValidate);
        JsonNode typeNode = toValidate.get(TYPE);
        assertNotNull(typeNode);
        assertTrue(typeNode instanceof TextNode);
        assertEquals(OBJECT, ((TextNode) typeNode).asText());
        JsonNode propertiesNode = toValidate.get(PROPERTIES);
        assertNotNull(propertiesNode);
        assertTrue(propertiesNode instanceof ObjectNode);
        assertEquals(0, propertiesNode.size());
    }

    private void commonValidateNumericNode(NumericNode toValidate, Number number) {
        String className = number.getClass().getSimpleName();
        switch (className) {
            case "Integer":
                assertTrue(toValidate instanceof IntNode);
                assertEquals(number, ((IntNode) toValidate).intValue());
                break;
            case "Float":
                assertTrue(toValidate instanceof FloatNode);
                assertEquals(number, ((FloatNode) toValidate).floatValue());
                break;
            case "Double":
                assertTrue(toValidate instanceof DoubleNode);
                assertEquals(number, ((DoubleNode) toValidate).doubleValue());
                break;
            case "BigInteger":
                assertTrue(toValidate instanceof BigIntegerNode);
                assertEquals(number, ((BigIntegerNode) toValidate).bigIntegerValue());
                break;
            case "Short":
                assertTrue(toValidate instanceof ShortNode);
                assertEquals(number, ((ShortNode) toValidate).shortValue());
                break;
            case "Long":
                assertTrue(toValidate instanceof LongNode);
                assertEquals(number, ((LongNode) toValidate).longValue());
                break;
            case "BigDecimal":
                assertTrue(toValidate instanceof DecimalNode);
                assertEquals(number, ((DecimalNode) toValidate).decimalValue());
                break;
            default:
                throw new IllegalArgumentException("Failed to find a NumericNode for " + number.getClass());
        }
    }
}
