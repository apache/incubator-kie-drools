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

public class DateFunctionsTest {

    public final static List<DateFunctions> supportedDateFunctions;
    public final static List<DateFunctions> unsupportedDateFunctions;

    static {
        supportedDateFunctions = new ArrayList<>();

        unsupportedDateFunctions = new ArrayList<>();
        unsupportedDateFunctions.add(DateFunctions.DATE_DAYS_SINCE_YEAR);
        unsupportedDateFunctions.add(DateFunctions.DATE_SECONDS_SINCE_YEAR);
        unsupportedDateFunctions.add(DateFunctions.DATE_SECONDS_SINCE_MIDNIGHT);
    }

    @Test
    public void getSupportedValueEmptyInput() {
        final Object[] input = {};
        supportedDateFunctions.forEach(dateFunction -> {
            try {
                dateFunction.getValue(input);
                fail("Expecting IllegalArgumentException");
            } catch (Exception e) {
                assertTrue(e instanceof IllegalArgumentException);
            }
        });

    }

    @Test
    public void getUnsupportedValue() {
        final Object[] input = {35, 12};
        unsupportedDateFunctions.forEach(dateFunction -> {
            try {
                dateFunction.getValue(input);
                fail("Expecting KiePMMLException");
            } catch (Exception e) {
                assertTrue(e instanceof KiePMMLException);
            }
        });
    }

}