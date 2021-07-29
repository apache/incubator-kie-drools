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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StringFunctionsTest {

    public final static List<StringFunctions> supportedStringFunctions;
    public final static List<StringFunctions> unsupportedStringFunctions;

    static {
        supportedStringFunctions = new ArrayList<>();
        supportedStringFunctions.add(StringFunctions.LOWERCASE);
        supportedStringFunctions.add(StringFunctions.UPPERCASE);
        supportedStringFunctions.add(StringFunctions.STRING_LENGTH);
        supportedStringFunctions.add(StringFunctions.SUBSTRING);
        supportedStringFunctions.add(StringFunctions.TRIM_BLANKS);
        supportedStringFunctions.add(StringFunctions.CONCAT);
        supportedStringFunctions.add(StringFunctions.REPLACE);
        supportedStringFunctions.add(StringFunctions.MATCHES);
        supportedStringFunctions.add(StringFunctions.FORMAT_NUMBER);
        supportedStringFunctions.add(StringFunctions.FORMAT_DATE_TIME);

        unsupportedStringFunctions = new ArrayList<>();
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

    @Test
    public void getStringLengthValueCorrectInput() {
        final Object[] input = {"AwdC"};
        Object retrieved = StringFunctions.STRING_LENGTH.getValue(input);
        assertEquals(4, retrieved);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getStringLengthValueWrongSizeInput() {
        final Object[] input = {"AwdC", "AwdB"};
        StringFunctions.STRING_LENGTH.getValue(input);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getStringLengthValueWrongTypeInput() {
        final Object[] input = {34};
        StringFunctions.STRING_LENGTH.getValue(input);
    }

    @Test
    public void getSubstringValueCorrectInput() {
        final Object[] input = {"aBc9x", 2, 3};
        Object retrieved = StringFunctions.SUBSTRING.getValue(input);
        assertEquals("Bc9", retrieved);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSubstringValueWrongSizeInput() {
        final Object[] input = {"AwdC", 1};
        StringFunctions.SUBSTRING.getValue(input);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSubstringValueWrongTypeInput() {
        final Object[] input = {"aBc9x", "2", 3};
        StringFunctions.SUBSTRING.getValue(input);
    }

    @Test
    public void getTrimBlanksValueCorrectInput() {
        final Object[] input1 = {" aBcas9x  "};
        Object retrieved = StringFunctions.TRIM_BLANKS.getValue(input1);
        assertEquals("aBcas9x", retrieved);
        final Object[] input2 = {" aB ca  s9x  "};
        retrieved = StringFunctions.TRIM_BLANKS.getValue(input2);
        assertEquals("aB ca  s9x", retrieved);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getTrimBlanksValueWrongSizeInput() {
        final Object[] input = {"AwdC", 1};
        StringFunctions.TRIM_BLANKS.getValue(input);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getTrimBlanksValueWrongTypeInput() {
        final Object[] input = {3};
        StringFunctions.TRIM_BLANKS.getValue(input);
    }

    @Test
    public void getConcatValueCorrectInput() {
        final Object[] input1 = {" aBc", "as9x  "};
        Object retrieved = StringFunctions.CONCAT.getValue(input1);
        assertEquals(" aBcas9x  ", retrieved);
        final Object[] input2 = {" aB ", "ca  s9x"};
        retrieved = StringFunctions.CONCAT.getValue(input2);
        assertEquals(" aB ca  s9x", retrieved);
        final Object[] input3 = {2, "-", 2000};
        retrieved = StringFunctions.CONCAT.getValue(input3);
        assertEquals("2-2000", retrieved);
        final Object[] input4 = {2, null, 2000};
        retrieved = StringFunctions.CONCAT.getValue(input4);
        assertEquals("2null2000", retrieved);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getConcatValueWrongSizeInput() {
        final Object[] input = {"BBBB"};
        StringFunctions.CONCAT.getValue(input);
    }

    @Test
    public void getReplaceValueCorrectInput() {
        final Object[] input = {"BBBB", "B+", "c"};
        Object retrieved = StringFunctions.REPLACE.getValue(input);
        assertEquals("c", retrieved);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getReplaceValueWrongSizeInput() {
        final Object[] input = {"BBBB", "B+"};
        StringFunctions.REPLACE.getValue(input);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getReplaceValueWrongTypeInput() {
        final Object[] input = {"BBBB", "B+", 3};
        StringFunctions.REPLACE.getValue(input);
    }

    @Test
    public void getMatchesValueCorrectInput() {
        final Object[] input1 = {"BBBB", "B+"};
        assertTrue((boolean)StringFunctions.MATCHES.getValue(input1));
        final Object[] input2 = {"BBBB", "."};
        assertTrue((boolean)StringFunctions.MATCHES.getValue(input2));
        final Object[] input3 = {"aBcDDeFF", "DeF"};
        assertTrue((boolean)StringFunctions.MATCHES.getValue(input3));
        final Object[] input4 = {"BBBB", "\\d"};
        assertFalse((boolean)StringFunctions.MATCHES.getValue(input4));
        final Object[] input5 = {"aBcDDeFF", "dell"};
        assertFalse((boolean)StringFunctions.MATCHES.getValue(input5));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getMatchesValueWrongSizeInput() {
        final Object[] input = {"BBBB", "B+", "c"};
        StringFunctions.MATCHES.getValue(input);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getMatchesValueWrongTypeInput() {
        final Object[] input = {"BBBB", 3};
        StringFunctions.MATCHES.getValue(input);
    }

    @Test
    public void getFormatNumberValueCorrectInput() {
        final Object[] input1 = {2, "%3d"};
        Object retrieved = StringFunctions.FORMAT_NUMBER.getValue(input1);
        assertEquals("  2", retrieved);
        final Object[] input2 = {4.2352989244d, "%.2f"};
        retrieved = StringFunctions.FORMAT_NUMBER.getValue(input2);
        assertEquals("4.24", retrieved);
        final Object[] input3 = {4.2352989244d, "%.3f"};
        retrieved = StringFunctions.FORMAT_NUMBER.getValue(input3);
        assertEquals("4.235", retrieved);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getFormatNumberValueWrongSizeInput() {
        final Object[] input = {3, "B+", "c"};
        StringFunctions.FORMAT_NUMBER.getValue(input);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getFormatNumberValueWrongTypeInput() {
        final Object[] input = {"BBBB", 3};
        StringFunctions.FORMAT_NUMBER.getValue(input);
    }

    @Test
    public void getFormatDatetimeValueCorrectInput() {
        Date inputDate = new GregorianCalendar(2004, Calendar.AUGUST, 20).getTime();
        final Object[] input1 = {inputDate, "%m/%d/%y"};
        Object retrieved = StringFunctions.FORMAT_DATE_TIME.getValue(input1);
        assertEquals("08/20/04", retrieved);
        final Object[] input2 = {inputDate, "%B/%d/%y"};
        retrieved = StringFunctions.FORMAT_DATE_TIME.getValue(input2);
        String month = String.format("%1$tB", inputDate);
        String expected = String.format("%s/20/04", month);
        assertEquals(expected, retrieved);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getFormatDatetimeValueWrongSizeInput() {
        final Object[] input = {3, "B+", "c"};
        StringFunctions.FORMAT_DATE_TIME.getValue(input);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getFormatDatetimeValueWrongTypeInput() {
        final Object[] input = {"BBBB", 3};
        StringFunctions.FORMAT_DATE_TIME.getValue(input);
    }


}