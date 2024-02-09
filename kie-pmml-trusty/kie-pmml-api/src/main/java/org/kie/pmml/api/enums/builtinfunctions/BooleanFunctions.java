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
import java.util.List;
import java.util.Objects;

import org.kie.pmml.api.enums.INVALID_VALUE_TREATMENT_METHOD;
import org.kie.pmml.api.exceptions.KieEnumException;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.models.Interval;
import org.kie.pmml.api.models.MiningField;

import static org.kie.pmml.api.enums.BUILTIN_FUNCTIONS.checkBooleans;
import static org.kie.pmml.api.enums.BUILTIN_FUNCTIONS.checkLength;
import static org.kie.pmml.api.enums.BUILTIN_FUNCTIONS.checkMinimumLength;
import static org.kie.pmml.api.enums.BUILTIN_FUNCTIONS.checkNumbers;
import static org.kie.pmml.api.enums.BUILTIN_FUNCTIONS.checkRangeLength;

public enum BooleanFunctions {

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
    IF("if");

    /**
     * <code>OPERATOR</code>s that operates <b>ONLY</b >with <code>Number</code>s
     */
    static final List<BooleanFunctions> BOOLEAN_FUNCTIONS_VALIDATION = Arrays.asList(
            IS_MISSING,
            IS_NOT_MISSING,
            IS_VALID,
            IS_NOT_VALID);

    private final String name;

    BooleanFunctions(String name) {
        this.name = name;
    }

    public static boolean isBooleanFunctions(String name) {
        return Arrays.stream(BooleanFunctions.values())
                .anyMatch(value -> name.equals(value.name));
    }

    public static boolean isBooleanFunctionsValidation(String name) {
        return isBooleanFunctions(name) && byName(name).isValidation();
    }

    public static BooleanFunctions byName(String name) {
        return Arrays.stream(BooleanFunctions.values())
                .filter(value -> name.equals(value.name))
                .findFirst()
                .orElseThrow(() -> new KieEnumException("Failed to find BooleanFunctions with name: " + name));
    }

    public String getName() {
        return name;
    }

    public Object getValue(final Object[] inputData, final MiningField referredByFieldRef) {
        switch (this) {
            case IS_MISSING:
                return isMissing(inputData, referredByFieldRef);
            case IS_NOT_MISSING:
                return isNotMissing(inputData, referredByFieldRef);
            case IS_VALID:
                return isValid(inputData, referredByFieldRef);
            case IS_NOT_VALID:
                return isNotValid(inputData, referredByFieldRef);
            case EQUAL:
                return equal(inputData);
            case NOT_EQUAL:
                return notEqual(inputData);
            case LESS_THAN:
                return lessThan(inputData);
            case LESS_OR_EQUAL:
                return lessOrEqual(inputData);
            case GREATER_THAN:
                return greaterThan(inputData);
            case GREATER_OR_EQUAL:
                return greaterOrEqual(inputData);
            case AND:
                return and(inputData);
            case OR:
                return or(inputData);
            case NOT:
                return not(inputData);
            case IS_IN:
                return isIn(inputData);
            case IS_NOT_IN:
                return isNotIn(inputData);
            case IF:
                return ifF(inputData);
            default:
                throw new KiePMMLException("Unmanaged BooleanFunctions " + this);
        }
    }

    public boolean isValidation() {
        return BOOLEAN_FUNCTIONS_VALIDATION.contains(this);
    }

    private boolean isMissing(final Object[] inputData, final MiningField referredByFieldRef) {
        checkLength(inputData, 1);
        if (inputData[0] == null) {
            return true;
        }
        if (isValid(inputData, referredByFieldRef)) {
            return false;
        }
        return INVALID_VALUE_TREATMENT_METHOD.AS_MISSING.equals(referredByFieldRef.getInvalidValueTreatmentMethod());
    }

    private boolean isNotMissing(final Object[] inputData, final MiningField referredByFieldRef) {
        return !isMissing(inputData, referredByFieldRef);
    }

    private boolean isValid(final Object[] inputData, final MiningField referredByFieldRef) {
        checkLength(inputData, 1);
        if (referredByFieldRef == null) {
            throw new IllegalArgumentException("Expecting MiningField, received null");
        }
        if (inputData[0] == null) {
            return false;
        }
        List<Interval> intervals = referredByFieldRef.getIntervals();
        boolean toReturn = true;
        if (intervals != null && !intervals.isEmpty()) {
            checkNumbers(inputData, 1);
            double a = ((Number) inputData[0]).doubleValue();
            for (Interval interval : intervals) {
                if (a >= interval.getLeftMargin().doubleValue() && a <= interval.getRightMargin().doubleValue()) {
                    return true;
                }
            }
            toReturn = false;
        }
        List<String> allowedValues = referredByFieldRef.getAllowedValues();
        if (allowedValues != null && !allowedValues.isEmpty()) {
            String a = inputData[0].toString();
            if (allowedValues.contains(a)) {
                return true;
            }
            toReturn = false;
        }
        return toReturn;
    }

    private boolean isNotValid(final Object[] inputData, final MiningField referredByFieldRef) {
        checkLength(inputData, 1);
        if (inputData[0] == null) {
            return false;
        }
        if (isValid(inputData, referredByFieldRef)) {
            return false;
        } else {
            return !INVALID_VALUE_TREATMENT_METHOD.AS_MISSING.equals(referredByFieldRef.getInvalidValueTreatmentMethod());
        }
    }

    private boolean equal(final Object[] inputData) {
        checkLength(inputData, 2);
        return Objects.equals(inputData[0], inputData[1]);
    }

    private boolean notEqual(final Object[] inputData) {
        return !equal(inputData);
    }

    private boolean lessThan(final Object[] inputData) {
        checkNumbers(inputData, 2);
        double a = ((Number) inputData[0]).doubleValue();
        double b = ((Number) inputData[1]).doubleValue();
        return a < b;
    }

    private boolean lessOrEqual(final Object[] inputData) {
        checkNumbers(inputData, 2);
        double a = ((Number) inputData[0]).doubleValue();
        double b = ((Number) inputData[1]).doubleValue();
        return a <= b;
    }

    private boolean greaterThan(final Object[] inputData) {
        checkNumbers(inputData, 2);
        double a = ((Number) inputData[0]).doubleValue();
        double b = ((Number) inputData[1]).doubleValue();
        return a > b;
    }

    private boolean greaterOrEqual(final Object[] inputData) {
        checkNumbers(inputData, 2);
        double a = ((Number) inputData[0]).doubleValue();
        double b = ((Number) inputData[1]).doubleValue();
        return a >= b;
    }

    private boolean and(final Object[] inputData) {
        checkBooleans(inputData, 2);
        boolean a = (boolean) inputData[0];
        boolean b = (boolean) inputData[1];
        return a && b;
    }

    private boolean or(final Object[] inputData) {
        checkBooleans(inputData, 2);
        boolean a = (boolean) inputData[0];
        boolean b = (boolean) inputData[1];
        return a || b;
    }

    private boolean not(final Object[] inputData) {
        checkBooleans(inputData, 1);
        boolean a = (boolean) inputData[0];
        return !a;
    }

    private boolean isIn(final Object[] inputData) {
        checkMinimumLength(inputData, 2);
        Object a = inputData[0];
        for (int i = 1; i < inputData.length; i++) {
            if (a.equals(inputData[i])) {
                return true;
            }
        }
        return false;
    }

    private boolean isNotIn(final Object[] inputData) {
        return !isIn(inputData);
    }

    private Object ifF(final Object[] inputData) {
        checkRangeLength(inputData, 2, 3);
        if (!(inputData[0] instanceof Boolean)) {
            throw new IllegalArgumentException("Expected Boolean as first parameter");
        }
        boolean a = (boolean) inputData[0];
        if (a) {
            return inputData[1];
        } else {
            return inputData.length == 3 ? inputData[2] : null;
        }
    }
}
