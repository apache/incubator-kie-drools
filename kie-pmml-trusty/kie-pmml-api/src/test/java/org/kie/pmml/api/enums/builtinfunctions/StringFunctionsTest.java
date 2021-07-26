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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class StringFunctionsTest {

    public final static List<StringFunctions> supportedStringFunctions;
    public final static List<StringFunctions> unsupportedStringFunctions;

    static {
        supportedStringFunctions = new ArrayList<>();
        supportedStringFunctions.add(StringFunctions.LOWERCASE);
        supportedStringFunctions.add(StringFunctions.UPPERCASE);

        unsupportedStringFunctions = new ArrayList<>();
        unsupportedStringFunctions.add(StringFunctions.STRING_LENGTH);
        unsupportedStringFunctions.add(StringFunctions.SUBSTRING);
        unsupportedStringFunctions.add(StringFunctions.TRIM_BLANKS);
        unsupportedStringFunctions.add(StringFunctions.CONCAT);
        unsupportedStringFunctions.add(StringFunctions.REPLACE);
        unsupportedStringFunctions.add(StringFunctions.MATCHES);
        unsupportedStringFunctions.add(StringFunctions.FORMAT_NUMBER);
        unsupportedStringFunctions.add(StringFunctions.FORMAT_DATE_TIME);
    }

    @Test
    public void getSupportedValueEmptyInput() {
        final Object[] input = {};
        supportedStringFunctions.forEach(stringFunction -> {
            try {
                stringFunction.getValue(input);
                fail("Expecting IllegalArgumentException");
            } catch (Exception e) {
                assertTrue(e instanceof IllegalArgumentException);
            }
        });

    }

    @Test
    public void getUnsupportedValue() {
        final Object[] input = {35, 12};
        unsupportedStringFunctions.forEach(stringFunction -> {
            try {
                stringFunction.getValue(input);
                fail("Expecting KiePMMLException");
            } catch (Exception e) {
                assertTrue(e instanceof KiePMMLException);
            }
        });

    }

    @Test
    public void getLowercaseValueCorrectInput() {
        final Object[] input = {"AwdC"};
        Object retrieved = StringFunctions.LOWERCASE.getValue(input);
        assertEquals("awdc", retrieved);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getLowercaseValueWrongSizeInput() {
        final Object[] input = {"AwdC", "AwdB"};
        StringFunctions.LOWERCASE.getValue(input);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getLowercaseValueWrongTypeInput() {
        final Object[] input = {34};
        StringFunctions.LOWERCASE.getValue(input);
    }

    @Test
    public void getUppercaseValueCorrectInput() {
        final Object[] input = {"AwdC"};
        Object retrieved = StringFunctions.UPPERCASE.getValue(input);
        assertEquals("AWDC", retrieved);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getUppercaseValueWrongSizeInput() {
        final Object[] input = {"AwdC", "AwdB"};
        StringFunctions.UPPERCASE.getValue(input);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getUppercaseValueWrongTypeInput() {
        final Object[] input = {34};
        StringFunctions.UPPERCASE.getValue(input);
    }
}