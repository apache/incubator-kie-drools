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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.kie.pmml.api.exceptions.KiePMMLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class BUILTIN_FUNCTIONSTest {

    private final static List<BUILTIN_FUNCTIONS> supportedBuiltinFunctions;
    private final static List<BUILTIN_FUNCTIONS> unsupportedBuiltinFunctions;

    static {
        supportedBuiltinFunctions = new ArrayList<>();
        supportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.PLUS);

        unsupportedBuiltinFunctions = new ArrayList<>();
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.MINUS);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.MULTI);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.DIVISION);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.MIN);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.MAX);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.SUM);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.AVG);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.MEDIAN);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.PRODUCT);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.LOG10);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.LN);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.SQRT);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.ABS);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.EXP);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.POW);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.THRESHOLD);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.FLOOR);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.CEIL);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.ROUND);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.MODULO);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.IS_MISSING);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.IS_NOT_MISSING);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.IS_VALID);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.IS_NOT_VALID);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.EQUAL);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.NOT_EQUAL);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.LESS_THAN);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.LESS_OR_EQUAL);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.GREATER_THAN);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.GREATER_OR_EQUAL);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.AND);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.OR);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.NOT);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.IS_IN);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.IS_NOT_IN);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.IF);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.UPPERCASE);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.LOWERCASE);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.STRING_LENGTH);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.SUBSTRING);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.TRIM_BLANKS);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.CONCAT);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.REPLACE);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.MATCHES);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.FORMAT_NUMBER);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.FORMAT_DATE_TIME);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.DATE_DAYS_SINCE_YEAR);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.DATE_SECONDS_SINCE_YEAR);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.DATE_SECONDS_SINCE_MIDNIGHT);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.NORMAL_CDF);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.NORMAL_PDF);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.STD_NORMAL_CDF);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.STD_NORMAL_PDF);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.ERF);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.NORMAL_IDF);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.STD_NORMAL_IDF);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.EXPM1);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.HYPOT);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.LN1P);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.RINT);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.SIN);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.ASIN);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.SINH);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.COS);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.ACOS);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.COSH);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.TAN);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.ATAN);
        unsupportedBuiltinFunctions.add(BUILTIN_FUNCTIONS.TANH);
    }

    @Test
    public void getUnsupportedValue() {
        final Object[] input = {35, 12};
        unsupportedBuiltinFunctions.forEach(builtinFunction -> {
            try {
                builtinFunction.getValue(input);
                fail("Expecting KiePMMLException");
            } catch (Exception e) {
                assertTrue(e instanceof KiePMMLException);
            }
        });

    }

    @Test
    public void getPlusValueCorrectInput() {
        final Object[] input = {35, 12};
        Object retrieved = BUILTIN_FUNCTIONS.PLUS.getValue(input);
        assertEquals(47.0, retrieved);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getPlusValueWrongSizeInput() {
        final Object[] input = {35};
        BUILTIN_FUNCTIONS.PLUS.getValue(input);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getPlusValueWrongTypeInput() {
        final Object[] input = {"A", 34};
        BUILTIN_FUNCTIONS.PLUS.getValue(input);
    }
}