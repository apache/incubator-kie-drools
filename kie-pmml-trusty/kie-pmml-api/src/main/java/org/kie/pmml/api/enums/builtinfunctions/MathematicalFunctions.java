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

import org.kie.pmml.api.exceptions.KieEnumException;
import org.kie.pmml.api.exceptions.KiePMMLException;

import static org.kie.pmml.api.enums.BUILTIN_FUNCTIONS.checkNumbers;

public enum MathematicalFunctions {

    EXPM1("expm1"),
    HYPOT("hypot"),
    LN1P("ln1p"),
    RINT("rint"),
    SIN("sin"),
    ASIN("asin"),
    SINH("sinh"),
    COS("cos"),
    ACOS("acos"),
    COSH("cosh"),
    TAN("tan"),
    ATAN("atan"),
    TANH("tanh");

    private final String name;

    MathematicalFunctions(String name) {
        this.name = name;
    }

    public static boolean isMathematicalFunctions(String name) {
        return Arrays.stream(MathematicalFunctions.values())
                .anyMatch(value -> name.equals(value.name));
    }

    public static MathematicalFunctions byName(String name) {
        return Arrays.stream(MathematicalFunctions.values())
                .filter(value -> name.equals(value.name))
                .findFirst()
                .orElseThrow(() -> new KieEnumException("Failed to find MathematicalFunctions with name: " + name));
    }

    public String getName() {
        return name;
    }

    public Object getValue(final Object[] inputData) {
        switch (this) {
            case EXPM1:
                return expm1(inputData);
            case HYPOT:
                return hypot(inputData);
            case LN1P:
                return ln1p(inputData);
            case RINT:
                return rint(inputData);
            case SIN:
                return sin(inputData);
            case ASIN:
                return asin(inputData);
            case SINH:
                return sinh(inputData);
            case COS:
                return cos(inputData);
            case ACOS:
                return acos(inputData);
            case COSH:
                return cosh(inputData);
            case TAN:
                return tan(inputData);
            case ATAN:
                return atan(inputData);
            case TANH:
                return tanh(inputData);
            default:
                throw new KiePMMLException("Unmanaged MathematicalFunctions " + this);
        }
    }

    private double expm1(final Object[] inputData) {
        checkNumbers(inputData, 1);
        double x = ((Number) inputData[0]).doubleValue();
        return Math.expm1(x);
    }

    private double hypot(final Object[] inputData) {
        checkNumbers(inputData, 2);
        double x = ((Number) inputData[0]).doubleValue();
        double y = ((Number) inputData[1]).doubleValue();
        return Math.hypot(x, y);
    }

    private double ln1p(final Object[] inputData) {
        checkNumbers(inputData, 1);
        double x = ((Number) inputData[0]).doubleValue();
        return Math.log1p(x);
    }

    private double rint(final Object[] inputData) {
        checkNumbers(inputData, 1);
        double x = ((Number) inputData[0]).doubleValue();
        return Math.rint(x);
    }

    private double sin(final Object[] inputData) {
        checkNumbers(inputData, 1);
        double x = ((Number) inputData[0]).doubleValue();
        return Math.sin(x);
    }

    private double asin(final Object[] inputData) {
        checkNumbers(inputData, 1);
        double x = ((Number) inputData[0]).doubleValue();
        return Math.asin(x);
    }

    private double sinh(final Object[] inputData) {
        checkNumbers(inputData, 1);
        double x = ((Number) inputData[0]).doubleValue();
        return Math.sinh(x);
    }

    private double cos(final Object[] inputData) {
        checkNumbers(inputData, 1);
        double x = ((Number) inputData[0]).doubleValue();
        return Math.cos(x);
    }

    private double acos(final Object[] inputData) {
        checkNumbers(inputData, 1);
        double x = ((Number) inputData[0]).doubleValue();
        return Math.acos(x);
    }

    private double cosh(final Object[] inputData) {
        checkNumbers(inputData, 1);
        double x = ((Number) inputData[0]).doubleValue();
        return Math.cosh(x);
    }

    private double tan(final Object[] inputData) {
        checkNumbers(inputData, 1);
        double x = ((Number) inputData[0]).doubleValue();
        return Math.tan(x);
    }

    private double atan(final Object[] inputData) {
        checkNumbers(inputData, 1);
        double x = ((Number) inputData[0]).doubleValue();
        return Math.atan(x);
    }

    private double tanh(final Object[] inputData) {
        checkNumbers(inputData, 1);
        double x = ((Number) inputData[0]).doubleValue();
        return Math.tanh(x);
    }

}
