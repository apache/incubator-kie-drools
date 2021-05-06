/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.pmml.api.enums;

import java.util.Arrays;

import org.kie.pmml.api.exceptions.KieEnumException;
import org.kie.pmml.api.exceptions.KiePMMLException;

/**
 * @see <a http://dmg.org/pmml/v4-4-1/BuiltinFunctions.html>Built-in functions</a>
 */
public enum BUILTIN_FUNCTIONS {



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
    MODULO("modulo"),
    IS_MISSING("isMissing"),
    IS_NOT_MISSING("isNotMissing"),
    IS_VALID("isValid"),
    IS_NOT_VALID("isNotValid"),
    EQUAL("equal"),
    NOT_EQUAL("notEqual"),
    LESS_THAN("lessThan"),
    LESS_OR_EQUAL("lessOrEqual"),
    GREATER_THAN("greaterThan"),
    GREATER_OR_EQUAL("greaterOrEqual"),
    AND("and"),
    OR("or"),
    NOT("not"),
    IS_IN("isIn"),
    IS_NOT_IN("isNotIn"),
    IF("if"),
    UPPERCASE("uppercase"),
    LOWERCASE("lowercase"),
    STRING_LENGTH("stringLength"),
    SUBSTRING("substring"),
    TRIM_BLANKS("trimBlanks"),
    CONCAT("concat"),
    REPLACE("replace"),
    MATCHES("matches"),
    FORMAT_NUMBER("formatNumber"),
    FORMAT_DATE_TIME("formatDatetime"),
    DATE_DAYS_SINCE_YEAR("dateDaysSinceYear"),
    DATE_SECONDS_SINCE_YEAR("dateSecondsSinceYear"),
    DATE_SECONDS_SINCE_MIDNIGHT("dateSecondsSinceMidnight"),
    NORMAL_CDF("normalCDF"),
    NORMAL_PDF("normalPDF"),
    STD_NORMAL_CDF("stdNormalCDF"),
    STD_NORMAL_PDF("stdNormalPDF"),
    ERF("erf"),
    NORMAL_IDF("normalIDF"),
    STD_NORMAL_IDF("stdNormalIDF"),
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

    BUILTIN_FUNCTIONS(String name) {
        this.name = name;
    }

    public static BUILTIN_FUNCTIONS byName(String name) {
        return Arrays.stream(BUILTIN_FUNCTIONS.values())
                .filter(value -> name.equals(value.name))
                .findFirst()
                .orElseThrow(() -> new KieEnumException("Failed to find BUILTIN_FUNCTIONS with name: " + name));
    }

    public String getName() {
        return name;
    }

    public Object getValue(final Object[] inputData) {
        switch (this) {
            case PLUS:
                return plus(inputData);
            default:
                throw new KiePMMLException("Unmanaged BUILTIN_FUNCTIONS " + this);
        }
    }

    private double plus(final Object[] inputData) {
        if (inputData.length != 2) {
            throw new IllegalArgumentException("Expected two parameters for " + this);
        }
        try {
            double a = ((Number) inputData[0]).doubleValue();
            double b = ((Number) inputData[1]).doubleValue();
            return a + b;
        } catch (Exception e) {
            throw new IllegalArgumentException("Expected two numbers for " + this, e);
        }
    }
}
