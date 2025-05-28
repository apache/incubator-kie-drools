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

import static org.assertj.core.api.Assertions.assertThat;
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
                null,
                null,
                null,
                null);
        assertThat(PMMLOASUtils.isRequired(toVerify)).isFalse();
        toVerify = new MiningField(null,
                FIELD_USAGE_TYPE.TARGET,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
        assertThat(PMMLOASUtils.isRequired(toVerify)).isFalse();
        toVerify = new MiningField(null,
                null,
                null,
                null,
                null,
                null,
                "MISSING_VALUE_REPLACEMENT",
                null,
                null,
                null);
        assertThat(PMMLOASUtils.isRequired(toVerify)).isFalse();
        toVerify = new MiningField(null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
        assertThat(PMMLOASUtils.isRequired(toVerify)).isTrue();
    }

    @Test
    void isPredicted() {
        MiningField toVerify = new MiningField(null,
                FIELD_USAGE_TYPE.PREDICTED,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
        assertThat(PMMLOASUtils.isPredicted(toVerify)).isTrue();
        toVerify = new MiningField(null,
                FIELD_USAGE_TYPE.TARGET,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
        assertThat(PMMLOASUtils.isPredicted(toVerify)).isTrue();
        toVerify = new MiningField(null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
        assertThat(PMMLOASUtils.isPredicted(toVerify)).isFalse();
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
                            null,
                            null,
                            null,
                            null);
                    assertThat(PMMLOASUtils.isPredicted(miningField)).isFalse();
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
            assertThat(PMMLOASUtils.getMappedType(dataType)).isEqualTo(expected);
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
            assertThat(PMMLOASUtils.getMappedFormat(dataType)).isEqualTo(expected);
        });
    }

    @Test
    void addIntervals() {
        ObjectNode typeFieldNode = PMMLOASUtils.objectNode();
        PMMLOASUtils.addIntervals(typeFieldNode, Collections.emptyList());
        assertThat(typeFieldNode).isEmpty();
        //
        Interval interval = new Interval(-34.23, null);
        PMMLOASUtils.addIntervals(typeFieldNode, Collections.singletonList(interval));
        assertThat(typeFieldNode.get(MINIMUM)).isNotNull();
        NumericNode numericNode = (NumericNode) typeFieldNode.get(MINIMUM);
        assertThat(numericNode.asDouble()).isEqualTo(interval.getLeftMargin().doubleValue());
        assertThat(typeFieldNode.get(MAXIMUM)).isNull();
        assertThat(typeFieldNode.get(INTERVALS)).isNull();
        //
        typeFieldNode = PMMLOASUtils.objectNode();
        interval = new Interval(null, 35.0);
        PMMLOASUtils.addIntervals(typeFieldNode, Collections.singletonList(interval));
        assertThat(typeFieldNode.get(MINIMUM)).isNull();
        assertThat(typeFieldNode.get(MAXIMUM)).isNotNull();
        numericNode = (NumericNode) typeFieldNode.get(MAXIMUM);
        assertThat(numericNode.asDouble()).isEqualTo(interval.getRightMargin().doubleValue());
        assertThat(typeFieldNode.get(INTERVALS)).isNull();
        //
        typeFieldNode = PMMLOASUtils.objectNode();
        interval = new Interval(-34.23, 35.0);
        PMMLOASUtils.addIntervals(typeFieldNode, Collections.singletonList(interval));
        assertThat(typeFieldNode.get(MINIMUM)).isNotNull();
        numericNode = (NumericNode) typeFieldNode.get(MINIMUM);
        assertThat(numericNode.asDouble()).isEqualTo(interval.getLeftMargin().doubleValue());
        assertThat(typeFieldNode.get(MAXIMUM)).isNotNull();
        numericNode = (NumericNode) typeFieldNode.get(MAXIMUM);
        assertThat(numericNode.asDouble()).isEqualTo(interval.getRightMargin().doubleValue());
        assertThat(typeFieldNode.get(INTERVALS)).isNull();
        //
        typeFieldNode = PMMLOASUtils.objectNode();
        List<Interval> intervals = IntStream.range(0, 3)
                .mapToObj(i -> new Interval(i * 2 + 3, i * 3 + 4))
                .collect(Collectors.toList());
        PMMLOASUtils.addIntervals(typeFieldNode, intervals);
        assertThat(typeFieldNode.get(MINIMUM)).isNull();
        assertThat(typeFieldNode.get(MAXIMUM)).isNull();
        assertThat(typeFieldNode.get(INTERVALS)).isNotNull();
        ArrayNode intervalsNode = (ArrayNode) typeFieldNode.get(INTERVALS);
        List<JsonNode> nodeList = StreamSupport
                .stream(intervalsNode.spliterator(), false)
                .collect(Collectors.toList());
        assertThat(nodeList).allMatch(node -> node instanceof TextNode);
        assertThat(intervals).allSatisfy(intervalValue -> {
            String leftMargin = intervalValue.getLeftMargin() != null ? intervalValue.getLeftMargin().toString() : "-" + INFINITY_SYMBOL;
            String rightMargin = intervalValue.getRightMargin() != null ? intervalValue.getRightMargin().toString() : INFINITY_SYMBOL;
            String expected = String.format("%s %s", leftMargin, rightMargin);
            assertThat(nodeList).anySatisfy(node -> expected.equals(node.asText()));
        });
    }

    @Test
    void addToSetNode() {
        String fieldName = "fieldName";
        DATA_TYPE dataType = DATA_TYPE.DOUBLE;
        ObjectNode setNode = PMMLOASUtils.createSetNode();
        ObjectNode propertiesNode = (ObjectNode) setNode.get(PROPERTIES);
        assertThat(propertiesNode).isEmpty();

        PMMLOASUtils.addToSetNode(fieldName, dataType, Collections.emptyList(), setNode);
        assertThat(propertiesNode.get(fieldName)).isNotNull();

        ObjectNode fieldNameNode = (ObjectNode) propertiesNode.get(fieldName);
        assertThat(fieldNameNode.get(TYPE)).isNotNull();
        assertThat(fieldNameNode.get(TYPE).asText()).isEqualTo(NUMBER);
        assertThat(fieldNameNode.get(FORMAT)).isNotNull();
        assertThat(fieldNameNode.get(FORMAT).asText()).isEqualTo(DOUBLE);
        assertThat(fieldNameNode.get(ENUM)).isNull();
        //
        List<String> allowedValues = IntStream.range(0, 3)
                .mapToObj(it -> "VALUE" + it)
                .collect(Collectors.toList());
        setNode = PMMLOASUtils.createSetNode();
        propertiesNode = (ObjectNode) setNode.get(PROPERTIES);
        assertThat(propertiesNode).isEmpty();

        PMMLOASUtils.addToSetNode(fieldName, dataType, allowedValues, setNode);
        assertThat(propertiesNode.get(fieldName)).isNotNull();
        fieldNameNode = (ObjectNode) propertiesNode.get(fieldName);
        assertThat(fieldNameNode.get(TYPE)).isNotNull();
        assertThat(fieldNameNode.get(TYPE).asText()).isEqualTo(NUMBER);
        assertThat(fieldNameNode.get(FORMAT)).isNotNull();
        assertThat(fieldNameNode.get(FORMAT).asText()).isEqualTo(DOUBLE);
        ArrayNode availableValuesNode = (ArrayNode) fieldNameNode.get(ENUM);
        assertThat(availableValuesNode).hasSameSizeAs(allowedValues);
        List<JsonNode> nodeList = StreamSupport
                .stream(availableValuesNode.spliterator(), false)
                .collect(Collectors.toList());
        assertThat(nodeList).allMatch(node -> node instanceof TextNode);
        assertThat(allowedValues).allSatisfy(allowedValue -> assertThat(nodeList)
                .anyMatch(availableValueNode -> availableValueNode.asText().equals(allowedValue)));
    }

    @Test
    void conditionallyCreateEnumNode() {
        ObjectNode parentNode = PMMLOASUtils.objectNode();
        ArrayNode created = PMMLOASUtils.conditionallyCreateEnumNode(parentNode);
        assertThat(created).isNotNull().isEmpty();

        JsonNode jsonNode = parentNode.get(ENUM);
        assertThat(jsonNode).isNotNull().isEqualTo(created);

        ArrayNode notCreated = PMMLOASUtils.conditionallyCreateEnumNode(parentNode);
        assertThat(notCreated).isNotNull().isEqualTo(created);
    }

    @Test
    void createSetNodeInParent() {
        ObjectNode parentNode = PMMLOASUtils.objectNode();
        String nodeToCreate = "nodeToCreate";
        ObjectNode retrieved = PMMLOASUtils.createSetNodeInParent(parentNode, nodeToCreate);
        commonValidateSetNode(retrieved);
        assertThat(parentNode.get(nodeToCreate)).isEqualTo(retrieved);
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
        assertThat(toValidate).isNotNull();
        JsonNode typeNode = toValidate.get(TYPE);

        assertThat(typeNode).isNotNull().isInstanceOf(TextNode.class);
        assertThat(((TextNode) typeNode).asText()).isEqualTo(OBJECT);

        JsonNode propertiesNode = toValidate.get(PROPERTIES);
        assertThat(propertiesNode).isNotNull().isInstanceOf(ObjectNode.class).isEmpty();
    }

    private void commonValidateNumericNode(NumericNode toValidate, Number number) {
        String className = number.getClass().getSimpleName();
        switch (className) {
            case "Integer":
                assertThat(toValidate).isInstanceOf(IntNode.class);
                assertThat(((IntNode) toValidate).intValue()).isEqualTo(number);
                break;
            case "Float":
                assertThat(toValidate).isInstanceOf(FloatNode.class);
                assertThat(((FloatNode) toValidate).floatValue()).isEqualTo(number);
                break;
            case "Double":
                assertThat(toValidate).isInstanceOf(DoubleNode.class);
                assertThat(((DoubleNode) toValidate).doubleValue()).isEqualTo(number);
                break;
            case "BigInteger":
                assertThat(toValidate).isInstanceOf(BigIntegerNode.class);
                assertThat(((BigIntegerNode) toValidate).bigIntegerValue()).isEqualTo(number);
                break;
            case "Short":
                assertThat(toValidate).isInstanceOf(ShortNode.class);
                assertThat(((ShortNode) toValidate).shortValue()).isEqualTo(number);
                break;
            case "Long":
                assertThat(toValidate).isInstanceOf(LongNode.class);
                assertThat(((LongNode) toValidate).longValue()).isEqualTo(number);
                break;
            case "BigDecimal":
                assertThat(toValidate).isInstanceOf(DecimalNode.class);
                assertThat(((DecimalNode) toValidate).decimalValue()).isEqualTo(number);
                break;
            default:
                throw new IllegalArgumentException("Failed to find a NumericNode for " + number.getClass());
        }
    }
}
