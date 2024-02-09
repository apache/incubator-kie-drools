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
package org.kie.pmml.api.enums;

import java.util.Arrays;
import java.util.Date;

import org.kie.pmml.api.enums.builtinfunctions.ArithmeticFunctions;
import org.kie.pmml.api.enums.builtinfunctions.BooleanFunctions;
import org.kie.pmml.api.enums.builtinfunctions.DateFunctions;
import org.kie.pmml.api.enums.builtinfunctions.DistributionFunctions;
import org.kie.pmml.api.enums.builtinfunctions.MathematicalFunctions;
import org.kie.pmml.api.enums.builtinfunctions.StringFunctions;
import org.kie.pmml.api.exceptions.KieEnumException;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.models.MiningField;

import static org.kie.pmml.api.enums.builtinfunctions.ArithmeticFunctions.isArithmeticFunctions;
import static org.kie.pmml.api.enums.builtinfunctions.BooleanFunctions.isBooleanFunctions;
import static org.kie.pmml.api.enums.builtinfunctions.DateFunctions.isDateFunctions;
import static org.kie.pmml.api.enums.builtinfunctions.DistributionFunctions.isDistributionFunctions;
import static org.kie.pmml.api.enums.builtinfunctions.MathematicalFunctions.isMathematicalFunctions;
import static org.kie.pmml.api.enums.builtinfunctions.StringFunctions.isStringFunctions;

public enum BUILTIN_FUNCTIONS implements Named {

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

    public static boolean isBUILTIN_FUNCTIONS(String name) {
        return Arrays.stream(BUILTIN_FUNCTIONS.values())
                .anyMatch(value -> name.equals(value.name));
    }

    public static boolean isBUILTIN_FUNCTIONS_VALIDATION(String name) {
        return BooleanFunctions.isBooleanFunctionsValidation(name);
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

    public Object getValue(final Object[] inputData, final MiningField referredByFieldRef) {
        if (isArithmeticFunctions(this.name)) {
            return ArithmeticFunctions.byName(name).getValue(inputData);
        } else if (isBooleanFunctions(this.name)) {
            return BooleanFunctions.byName(name).getValue(inputData, referredByFieldRef);
        } else if (isDateFunctions(this.name)) {
            return DateFunctions.byName(name).getValue(inputData);
        } else if (isDistributionFunctions(this.name)) {
            return DistributionFunctions.byName(name).getValue(inputData);
        } else if (isMathematicalFunctions(this.name)) {
            return MathematicalFunctions.byName(name).getValue(inputData);
        } else if (isStringFunctions(this.name)) {
            return StringFunctions.byName(name).getValue(inputData);
        } else {
            throw new KiePMMLException("Unmanaged BUILTIN_FUNCTIONS " + this);
        }
    }

    public static void checkNumbers(final Object[] inputData, final int expectedSize) {
        checkLength(inputData, expectedSize);
        for (Object object : inputData) {
            checkNumber(object);
        }
    }

    public static void checkNumber(final Object object) {
        if (!(object instanceof Number)) {
            throw new IllegalArgumentException("Expected only Numbers");
        }
    }

    public static void checkInteger(final Object object) {
        if (!(object instanceof Integer)) {
            throw new IllegalArgumentException("Expected only Integer");
        }
    }

    public static void checkStrings(final Object[] inputData, final int expectedSize) {
        checkLength(inputData, expectedSize);
        for (Object object : inputData) {
            checkString(object);
        }
    }

    public static void checkString(final Object object) {
        if (!(object instanceof String)) {
            throw new IllegalArgumentException("Expected only String");
        }
    }

    public static void checkBooleans(final Object[] inputData, final int expectedSize) {
        checkLength(inputData, expectedSize);
        for (Object object : inputData) {
            checkBoolean(object);
        }
    }

    public static void checkBoolean(final Object object) {
        if (!(object instanceof Boolean)) {
            throw new IllegalArgumentException("Expected only Booleans");
        }
    }

    public static void checkDates(final Object[] inputData, final int expectedSize) {
        checkLength(inputData, expectedSize);
        for (Object object : inputData) {
            checkDate(object);
        }
    }

    public static void checkDate(final Object object) {
        if (!(object instanceof Date)) {
            throw new IllegalArgumentException("Expected only Dates");
        }
    }

    public static void checkLength(final Object[] inputData, final int expectedSize) {
        if (inputData.length < 1) {
            throw new IllegalArgumentException("Expected at least one parameter");
        }
        if (inputData.length != expectedSize) {
            throw new IllegalArgumentException(String.format("Expected %s parameters ", expectedSize));
        }
    }

    public static void checkMinimumLength(final Object[] inputData, final int minimumLength) {
        if (inputData.length < minimumLength) {
            throw new IllegalArgumentException(String.format("Expected at least %s parameters ", minimumLength));
        }
    }

    public static void checkRangeLength(final Object[] inputData, final int minimumLength, final int maximumLength) {
        if (inputData.length < minimumLength) {
            throw new IllegalArgumentException(String.format("Expected at least %s parameters ", minimumLength));
        }
        if (inputData.length > maximumLength) {
            throw new IllegalArgumentException(String.format("Expected at most %s parameters ", maximumLength));
        }
    }
}
