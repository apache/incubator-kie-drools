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
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.IntStream;

import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.enums.FIELD_USAGE_TYPE;
import org.kie.pmml.api.models.Interval;
import org.kie.pmml.api.models.MiningField;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BigIntegerNode;
import com.fasterxml.jackson.databind.node.DecimalNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.FloatNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ShortNode;
import com.fasterxml.jackson.databind.node.TextNode;

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

public class PMMLOASUtils {

    public static final String INFINITY_SYMBOL = new String(Character.toString('\u221E').getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);

    private static final JsonNodeFactory factory = JsonNodeFactory.instance;

    private PMMLOASUtils() {
    }

    public static ObjectNode objectNode() {
        return factory.objectNode();
    }

    public static ArrayNode arrayNode() {
        return factory.arrayNode();
    }

    public static boolean isRequired(MiningField toVerify) {
        if (FIELD_USAGE_TYPE.PREDICTED.equals(toVerify.getUsageType()) ||
                FIELD_USAGE_TYPE.TARGET.equals(toVerify.getUsageType())) {
            return false;
        }
        return toVerify.getMissingValueReplacement() == null;
    }

    public static boolean isPredicted(MiningField toVerify) {
        return FIELD_USAGE_TYPE.PREDICTED.equals(toVerify.getUsageType()) ||
                FIELD_USAGE_TYPE.TARGET.equals(toVerify.getUsageType());
    }

    public static String getMappedType(DATA_TYPE toMap) {
        switch (toMap) {
            case DATE:
            case DATE_TIME:
            case STRING:
                return STRING;
            case BOOLEAN:
                return BOOLEAN;
            case INTEGER:
                return INTEGER;
            default:
                return NUMBER;
        }
    }

    public static String getMappedFormat(DATA_TYPE toMap) {
        switch (toMap) {
            case DOUBLE:
                return DOUBLE;
            case FLOAT:
                return FLOAT;
            default:
                return null;
        }
    }

    public static void addIntervals(final ObjectNode typeFieldNode, final List<Interval> intervals) {
        if (intervals.isEmpty()) {
            return;
        }
        if (intervals.size() == 1) {
            Interval interval = intervals.get(0);
            if (interval.getLeftMargin() != null) {
                typeFieldNode.set(MINIMUM, getNumericNode(interval.getLeftMargin()));
            }
            if (interval.getRightMargin() != null) {
                typeFieldNode.set(MAXIMUM, getNumericNode(interval.getRightMargin()));
            }
        } else {
            ArrayNode intervalsNode = PMMLOASUtils.arrayNode();
            IntStream.range(0, intervals.size()).forEach(i -> {
                Interval interval = intervals.get(i);
                String leftMargin = interval.getLeftMargin() != null ? interval.getLeftMargin().toString() : "-" + INFINITY_SYMBOL;
                String rightMargin = interval.getRightMargin() != null ? interval.getRightMargin().toString() : INFINITY_SYMBOL;
                String formattedInterval = String.format("%s %s", leftMargin, rightMargin);
                intervalsNode.add(new TextNode(formattedInterval));
            });
            typeFieldNode.set(INTERVALS, intervalsNode);
        }
    }

    public static void addToSetNode(String fieldName, DATA_TYPE dataType, List<String> allowedValues, ObjectNode setNode) {
        final ObjectNode propertiesNode = (ObjectNode) setNode.get(PROPERTIES);
        final ObjectNode typeFieldNode = PMMLOASUtils.objectNode();
        String mappedType = getMappedType(dataType);
        typeFieldNode.set(TYPE, new TextNode(mappedType));
        String mappedFormat = getMappedFormat(dataType);
        if (mappedFormat != null) {
            typeFieldNode.set(FORMAT, new TextNode(mappedFormat));
        }
        propertiesNode.set(fieldName, typeFieldNode);
        if (allowedValues != null && !allowedValues.isEmpty()) {
            ArrayNode availableValues = conditionallyCreateEnumNode(typeFieldNode);
            allowedValues.forEach(availableValues::add);
        }
    }

    public static ArrayNode conditionallyCreateEnumNode(final ObjectNode parent) {
        if (parent.get(ENUM) == null) {
            ArrayNode availableValues = PMMLOASUtils.arrayNode();
            parent.set(ENUM, availableValues);
        }
        return (ArrayNode) parent.get(ENUM);
    }

    public static ObjectNode createSetNodeInParent(final ObjectNode parentNode, String nodeToCreate) {
        final ObjectNode setNode = createSetNode();
        parentNode.set(nodeToCreate, setNode);
        return (ObjectNode) parentNode.get(nodeToCreate);
    }

    public static ObjectNode createSetNode() {
        final ObjectNode toReturn = PMMLOASUtils.objectNode();
        toReturn.set(TYPE, new TextNode(OBJECT));
        final ObjectNode propertiesNode = PMMLOASUtils.objectNode();
        toReturn.set(PROPERTIES, propertiesNode);
        return toReturn;
    }

    public static NumericNode getNumericNode(Number number) {
        String className = number.getClass().getSimpleName();
        switch (className) {
            case "Integer":
                return new IntNode((Integer) number);
            case "Float":
                return new FloatNode((Float) number);
            case "Double":
                return new DoubleNode((Double) number);
            case "BigInteger":
                return new BigIntegerNode((BigInteger) number);
            case "Short":
                return new ShortNode((Short) number);
            case "Long":
                return new LongNode((Long) number);
            case "BigDecimal":
                return new DecimalNode((BigDecimal) number);
            default:
                throw new IllegalArgumentException("Failed to find a NumericNode for " + number.getClass());
        }
    }
}
