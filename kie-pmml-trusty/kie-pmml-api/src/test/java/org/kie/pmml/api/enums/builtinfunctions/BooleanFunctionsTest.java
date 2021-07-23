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

public class BooleanFunctionsTest {

    private final static List<BooleanFunctions> supportedBooleanFunctions;
    private final static List<BooleanFunctions> unsupportedBooleanFunctions;

    static {
        supportedBooleanFunctions = new ArrayList<>();

        unsupportedBooleanFunctions = new ArrayList<>();
        unsupportedBooleanFunctions.add(BooleanFunctions.IS_MISSING);
        unsupportedBooleanFunctions.add(BooleanFunctions.IS_NOT_MISSING);
        unsupportedBooleanFunctions.add(BooleanFunctions.IS_VALID);
        unsupportedBooleanFunctions.add(BooleanFunctions.IS_NOT_VALID);
        unsupportedBooleanFunctions.add(BooleanFunctions.EQUAL);
        unsupportedBooleanFunctions.add(BooleanFunctions.NOT_EQUAL);
        unsupportedBooleanFunctions.add(BooleanFunctions.LESS_THAN);
        unsupportedBooleanFunctions.add(BooleanFunctions.LESS_OR_EQUAL);
        unsupportedBooleanFunctions.add(BooleanFunctions.GREATER_THAN);
        unsupportedBooleanFunctions.add(BooleanFunctions.GREATER_OR_EQUAL);
        unsupportedBooleanFunctions.add(BooleanFunctions.AND);
        unsupportedBooleanFunctions.add(BooleanFunctions.OR);
        unsupportedBooleanFunctions.add(BooleanFunctions.NOT);
        unsupportedBooleanFunctions.add(BooleanFunctions.IS_IN);
        unsupportedBooleanFunctions.add(BooleanFunctions.IS_NOT_IN);
        unsupportedBooleanFunctions.add(BooleanFunctions.IF);
    }

    @Test
    public void getSupportedValueEmptyInput() {
        final Object[] input = {};
        supportedBooleanFunctions.forEach(booleanFunction -> {
            try {
                booleanFunction.getValue(input);
                fail("Expecting IllegalArgumentException");
            } catch (Exception e) {
                assertTrue(e instanceof IllegalArgumentException);
            }
        });

    }

    @Test
    public void getUnsupportedValue() {
        final Object[] input = {35, 12};
        unsupportedBooleanFunctions.forEach(booleanFunction -> {
            try {
                booleanFunction.getValue(input);
                fail("Expecting KiePMMLException");
            } catch (Exception e) {
                assertTrue(e instanceof KiePMMLException);
            }
        });

    }
}