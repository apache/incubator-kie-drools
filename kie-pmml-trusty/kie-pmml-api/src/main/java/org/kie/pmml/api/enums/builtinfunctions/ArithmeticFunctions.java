/**
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
package org.kie.pmml.api.enums.builtinfunctions;

import java.util.Arrays;
import java.util.OptionalDouble;
import java.util.stream.DoubleStream;

import org.kie.pmml.api.exceptions.KieEnumException;
import org.kie.pmml.api.exceptions.KiePMMLException;

import static org.kie.pmml.api.enums.BUILTIN_FUNCTIONS.checkNumbers;

public enum ArithmeticFunctions {

    PLUS("+"),
    MINUS("-"),
    MULTI("*"),
    DIVISION("/"),
    MIN("min"),
    MAX("max"),
    SUM("sum"),
    AVG("avg"),
    MEDIAN("median"),
    PRODUCT("product"),
    LOG10("log10"),
    LN("ln"),
    SQRT("sqrt"),
    ABS("abs"),
    EXP("exp"),
    POW("pow"),
    THRESHOLD("threshold"),
    FLOOR("floor"),
    CEIL("ceil"),
    ROUND("round"),
    MODULO("modulo");

    private final String name;

    ArithmeticFunctions(String name) {
        this.name = name;
    }

    public static boolean isArithmeticFunctions(String name) {
        return Arrays.stream(ArithmeticFunctions.values())
                .anyMatch(value -> name.equals(value.name));
    }

    public static ArithmeticFunctions byName(String name) {
        return Arrays.stream(ArithmeticFunctions.values())
                .filter(value -> name.equals(value.name))
                .findFirst()
                .orElseThrow(() -> new KieEnumException("Failed to find ArithmeticFunctions with name: " + name));
    }

    public String getName() {
        return name;
    }

    public Object getValue(final Object[] inputData) {
        switch (this) {
            case AVG:
                return avg(inputData);
            case MAX:
                return max(inputData);
            case MEDIAN:
                return median(inputData);
            case MIN:
                return min(inputData);
            case MINUS:
                return minus(inputData);
            case MULTI:
                return multi(inputData);
            case DIVISION:
                return division(inputData);
            case PLUS:
                return plus(inputData);
            case PRODUCT:
                return product(inputData);
            case SUM:
                return sum(inputData);
            case LOG10:
                return log10(inputData);
            case LN:
                return ln(inputData);
            case SQRT:
                return sqrt(inputData);
            case ABS:
                return abs(inputData);
            case EXP:
                return exp(inputData);
            case POW:
                return pow(inputData);
            case THRESHOLD:
                return threshold(inputData);
            case FLOOR:
                return floor(inputData);
            case CEIL:
                return ceil(inputData);
            case ROUND:
                return round(inputData);
            case MODULO:
                return modulo(inputData);
            default:
                throw new KiePMMLException("Unmanaged ArithmeticFunctions " + this);
        }
    }

    private double avg(final Object[] inputData) {
        checkNumbers(inputData, inputData.length);
        return Arrays.stream(inputData)
                .mapToDouble(num -> ((Number) num).doubleValue())
                .average()
                .orElseThrow(() -> new IllegalArgumentException("Failed to find average value"));
    }

    private double division(final Object[] inputData) {
        checkNumbers(inputData, 2);
        double a = ((Number) inputData[0]).doubleValue();
        double b = ((Number) inputData[1]).doubleValue();
        return a / b;
    }

    private double max(final Object[] inputData) {
        checkNumbers(inputData, inputData.length);
        return Arrays.stream(inputData)
                .mapToDouble(num -> ((Number) num).doubleValue())
                .max()
                .orElseThrow(() -> new KieEnumException("Failed to find maximum value"));
    }

    private double median(final Object[] inputData) {
        checkNumbers(inputData, inputData.length);
        DoubleStream sortedValues = Arrays.stream(inputData)
                .mapToDouble(num -> ((Number) num).doubleValue())
                .sorted();
        OptionalDouble toReturn = inputData.length % 2 == 0 ?
                sortedValues.skip(inputData.length / 2 - (long) 1).limit(2).average() :
                sortedValues.skip(inputData.length / 2).findFirst();
        return toReturn.orElseThrow(() -> new KieEnumException("Failed to find median value"));
    }

    private double min(final Object[] inputData) {
        checkNumbers(inputData, inputData.length);
        return Arrays.stream(inputData)
                .mapToDouble(num -> ((Number) num).doubleValue())
                .min()
                .orElseThrow(() -> new KieEnumException("Failed to find minimum value"));
    }

    private double minus(final Object[] inputData) {
        checkNumbers(inputData, 2);
        double a = ((Number) inputData[0]).doubleValue();
        double b = ((Number) inputData[1]).doubleValue();
        return a - b;
    }

    private double multi(final Object[] inputData) {
        checkNumbers(inputData, 2);
        double a = ((Number) inputData[0]).doubleValue();
        double b = ((Number) inputData[1]).doubleValue();
        return a * b;
    }

    private double plus(final Object[] inputData) {
        checkNumbers(inputData, 2);
        double a = ((Number) inputData[0]).doubleValue();
        double b = ((Number) inputData[1]).doubleValue();
        return a + b;
    }

    private double product(final Object[] inputData) {
        checkNumbers(inputData, inputData.length);
        return Arrays.stream(inputData)
                .mapToDouble(num -> ((Number) num).doubleValue())
                .reduce(1, (a, b) -> a * b);
    }

    private double sum(final Object[] inputData) {
        checkNumbers(inputData, inputData.length);
        return Arrays.stream(inputData)
                .mapToDouble(num -> ((Number) num).doubleValue())
                .sum();
    }

    private double log10(final Object[] inputData) {
        checkNumbers(inputData, 1);
        return Math.log10(((Number) inputData[0]).doubleValue());
    }

    private double ln(final Object[] inputData) {
        checkNumbers(inputData, 1);
        return Math.log(((Number) inputData[0]).doubleValue());
    }

    private double sqrt(final Object[] inputData) {
        checkNumbers(inputData, 1);
        return Math.sqrt(((Number) inputData[0]).doubleValue());
    }

    private double abs(final Object[] inputData) {
        checkNumbers(inputData, 1);
        return Math.abs(((Number) inputData[0]).doubleValue());
    }

    private double exp(final Object[] inputData) {
        checkNumbers(inputData, 1);
        return Math.exp(((Number) inputData[0]).doubleValue());
    }

    private double pow(final Object[] inputData) {
        checkNumbers(inputData, 2);
        double a = ((Number) inputData[0]).doubleValue();
        double b = ((Number) inputData[1]).doubleValue();
        return Math.pow(a, b);
    }

    private double threshold(final Object[] inputData) {
        checkNumbers(inputData, 2);
        double a = ((Number) inputData[0]).doubleValue();
        double b = ((Number) inputData[1]).doubleValue();
        return a > b ? 1 : 0;
    }

    private double floor(final Object[] inputData) {
        checkNumbers(inputData, 1);
        return Math.floor(((Number) inputData[0]).doubleValue());
    }

    private double ceil(final Object[] inputData) {
        checkNumbers(inputData, 1);
        return Math.ceil(((Number) inputData[0]).doubleValue());
    }

    private double round(final Object[] inputData) {
        checkNumbers(inputData, 1);
        return Math.round(((Number) inputData[0]).doubleValue());
    }

    private double modulo(final Object[] inputData) {
        checkNumbers(inputData, 2);
        double a = ((Number) inputData[0]).doubleValue();
        double b = ((Number) inputData[1]).doubleValue();
        return a % b;
    }
}
