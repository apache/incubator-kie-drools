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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

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
    void getLowercaseValueCorrectInput() {
        final Object[] input = {"AwdC"};
        Object retrieved = StringFunctions.LOWERCASE.getValue(input);
        assertThat(retrieved).isEqualTo("awdc");
    }

    @Test
    void getLowercaseValueWrongSizeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {"AwdC", "AwdB"};
            StringFunctions.LOWERCASE.getValue(input);
        });
    }

    @Test
    void getLowercaseValueWrongTypeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {34};
            StringFunctions.LOWERCASE.getValue(input);
        });
    }

    @Test
    void getUppercaseValueCorrectInput() {
        final Object[] input = {"AwdC"};
        Object retrieved = StringFunctions.UPPERCASE.getValue(input);
        assertThat(retrieved).isEqualTo("AWDC");
    }

    @Test
    void getUppercaseValueWrongSizeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {"AwdC", "AwdB"};
            StringFunctions.UPPERCASE.getValue(input);
        });
    }

    @Test
    void getUppercaseValueWrongTypeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {34};
            StringFunctions.UPPERCASE.getValue(input);
        });
    }

    @Test
    void getStringLengthValueCorrectInput() {
        final Object[] input = {"AwdC"};
        Object retrieved = StringFunctions.STRING_LENGTH.getValue(input);
        assertThat(retrieved).isEqualTo(4);
    }

    @Test
    void getStringLengthValueWrongSizeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {"AwdC", "AwdB"};
            StringFunctions.STRING_LENGTH.getValue(input);
        });
    }

    @Test
    void getStringLengthValueWrongTypeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {34};
            StringFunctions.STRING_LENGTH.getValue(input);
        });
    }

    @Test
    void getSubstringValueCorrectInput() {
        final Object[] input = {"aBc9x", 2, 3};
        Object retrieved = StringFunctions.SUBSTRING.getValue(input);
        assertThat(retrieved).isEqualTo("Bc9");
    }

    @Test
    void getSubstringValueWrongSizeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {"AwdC", 1};
            StringFunctions.SUBSTRING.getValue(input);
        });
    }

    @Test
    void getSubstringValueWrongTypeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {"aBc9x", "2", 3};
            StringFunctions.SUBSTRING.getValue(input);
        });
    }

    @Test
    void getTrimBlanksValueCorrectInput() {
        final Object[] input1 = {" aBcas9x  "};
        Object retrieved = StringFunctions.TRIM_BLANKS.getValue(input1);
        assertThat(retrieved).isEqualTo("aBcas9x");
        final Object[] input2 = {" aB ca  s9x  "};
        retrieved = StringFunctions.TRIM_BLANKS.getValue(input2);
        assertThat(retrieved).isEqualTo("aB ca  s9x");
    }

    @Test
    void getTrimBlanksValueWrongSizeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {"AwdC", 1};
            StringFunctions.TRIM_BLANKS.getValue(input);
        });
    }

    @Test
    void getTrimBlanksValueWrongTypeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {3};
            StringFunctions.TRIM_BLANKS.getValue(input);
        });
    }

    @Test
    void getConcatValueCorrectInput() {
        final Object[] input1 = {" aBc", "as9x  "};
        Object retrieved = StringFunctions.CONCAT.getValue(input1);
        assertThat(retrieved).isEqualTo(" aBcas9x  ");
        final Object[] input2 = {" aB ", "ca  s9x"};
        retrieved = StringFunctions.CONCAT.getValue(input2);
        assertThat(retrieved).isEqualTo(" aB ca  s9x");
        final Object[] input3 = {2, "-", 2000};
        retrieved = StringFunctions.CONCAT.getValue(input3);
        assertThat(retrieved).isEqualTo("2-2000");
        final Object[] input4 = {2, null, 2000};
        retrieved = StringFunctions.CONCAT.getValue(input4);
        assertThat(retrieved).isEqualTo("2null2000");
    }

    @Test
    void getConcatValueWrongSizeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {"BBBB"};
            StringFunctions.CONCAT.getValue(input);
        });
    }

    @Test
    void getReplaceValueCorrectInput() {
        final Object[] input = {"BBBB", "B+", "c"};
        Object retrieved = StringFunctions.REPLACE.getValue(input);
        assertThat(retrieved).isEqualTo("c");
    }

    @Test
    void getReplaceValueWrongSizeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {"BBBB", "B+"};
            StringFunctions.REPLACE.getValue(input);
        });
    }

    @Test
    void getReplaceValueWrongTypeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {"BBBB", "B+", 3};
            StringFunctions.REPLACE.getValue(input);
        });
    }

    @Test
    void getMatchesValueCorrectInput() {
        final Object[] input1 = {"BBBB", "B+"};
        assertThat((boolean) StringFunctions.MATCHES.getValue(input1)).isTrue();
        final Object[] input2 = {"BBBB", "."};
        assertThat((boolean) StringFunctions.MATCHES.getValue(input2)).isTrue();
        final Object[] input3 = {"aBcDDeFF", "DeF"};
        assertThat((boolean) StringFunctions.MATCHES.getValue(input3)).isTrue();
        final Object[] input4 = {"BBBB", "\\d"};
        assertThat((boolean) StringFunctions.MATCHES.getValue(input4)).isFalse();
        final Object[] input5 = {"aBcDDeFF", "dell"};
        assertThat((boolean) StringFunctions.MATCHES.getValue(input5)).isFalse();
    }

    @Test
    void getMatchesValueWrongSizeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {"BBBB", "B+", "c"};
            StringFunctions.MATCHES.getValue(input);
        });
    }

    @Test
    void getMatchesValueWrongTypeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {"BBBB", 3};
            StringFunctions.MATCHES.getValue(input);
        });
    }

    @Test
    void getFormatNumberValueCorrectInput() {
        final Object[] input1 = {2, "%3d"};
        Object retrieved = StringFunctions.FORMAT_NUMBER.getValue(input1);
        assertThat(retrieved).isEqualTo("  2");
        final Object[] input2 = {4.2352989244d, "%.2f"};
        retrieved = StringFunctions.FORMAT_NUMBER.getValue(input2);
        assertThat(retrieved).isEqualTo("4.24");
        final Object[] input3 = {4.2352989244d, "%.3f"};
        retrieved = StringFunctions.FORMAT_NUMBER.getValue(input3);
        assertThat(retrieved).isEqualTo("4.235");
    }

    @Test
    void getFormatNumberValueWrongSizeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {3, "B+", "c"};
            StringFunctions.FORMAT_NUMBER.getValue(input);
        });
    }

    @Test
    void getFormatNumberValueWrongTypeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {"BBBB", 3};
            StringFunctions.FORMAT_NUMBER.getValue(input);
        });
    }

    @Test
    void getFormatDatetimeValueCorrectInput() {
        Date inputDate = new GregorianCalendar(2004, Calendar.AUGUST, 20).getTime();
        final Object[] input1 = {inputDate, "%m/%d/%y"};
        Object retrieved = StringFunctions.FORMAT_DATE_TIME.getValue(input1);
        assertThat(retrieved).isEqualTo("08/20/04");
        final Object[] input2 = {inputDate, "%B/%d/%y"};
        retrieved = StringFunctions.FORMAT_DATE_TIME.getValue(input2);
        String month = String.format("%1$tB", inputDate);
        String expected = String.format("%s/20/04", month);
        assertThat(retrieved).isEqualTo(expected);
    }

    @Test
    void getFormatDatetimeValueWrongSizeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {3, "B+", "c"};
            StringFunctions.FORMAT_DATE_TIME.getValue(input);
        });
    }

    @Test
    void getFormatDatetimeValueWrongTypeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {"BBBB", 3};
            StringFunctions.FORMAT_DATE_TIME.getValue(input);
        });
    }
}