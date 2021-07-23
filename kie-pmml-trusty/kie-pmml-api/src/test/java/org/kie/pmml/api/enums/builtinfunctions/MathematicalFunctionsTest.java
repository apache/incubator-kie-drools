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

package org.kie.pmml.api.enums.builtinfunctions;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.kie.pmml.api.exceptions.KiePMMLException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MathematicalFunctionsTest {

    public final static List<MathematicalFunctions> supportedMathematicalFunctions;
    public final static List<MathematicalFunctions> unsupportedMathematicalFunctions;

    static {
        supportedMathematicalFunctions = new ArrayList<>();

        unsupportedMathematicalFunctions = new ArrayList<>();
        unsupportedMathematicalFunctions.add(MathematicalFunctions.EXPM1);
        unsupportedMathematicalFunctions.add(MathematicalFunctions.HYPOT);
        unsupportedMathematicalFunctions.add(MathematicalFunctions.LN1P);
        unsupportedMathematicalFunctions.add(MathematicalFunctions.RINT);
        unsupportedMathematicalFunctions.add(MathematicalFunctions.SIN);
        unsupportedMathematicalFunctions.add(MathematicalFunctions.ASIN);
        unsupportedMathematicalFunctions.add(MathematicalFunctions.SINH);
        unsupportedMathematicalFunctions.add(MathematicalFunctions.COS);
        unsupportedMathematicalFunctions.add(MathematicalFunctions.ACOS);
        unsupportedMathematicalFunctions.add(MathematicalFunctions.COSH);
        unsupportedMathematicalFunctions.add(MathematicalFunctions.TAN);
        unsupportedMathematicalFunctions.add(MathematicalFunctions.ATAN);
        unsupportedMathematicalFunctions.add(MathematicalFunctions.TANH);
    }

    @Test
    public void getSupportedValueEmptyInput() {
        final Object[] input = {};
        supportedMathematicalFunctions.forEach(mathematicalFunction -> {
            try {
                mathematicalFunction.getValue(input);
                fail("Expecting IllegalArgumentException");
            } catch (Exception e) {
                assertTrue(e instanceof IllegalArgumentException);
            }
        });

    }

    @Test
    public void getUnsupportedValue() {
        final Object[] input = {35, 12};
        unsupportedMathematicalFunctions.forEach(mathematicalFunction -> {
            try {
                mathematicalFunction.getValue(input);
                fail("Expecting KiePMMLException");
            } catch (Exception e) {
                assertTrue(e instanceof KiePMMLException);
            }
        });

    }

}