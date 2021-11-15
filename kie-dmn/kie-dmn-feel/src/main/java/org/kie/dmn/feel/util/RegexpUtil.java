/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.dmn.feel.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.time.format.SignStyle;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.kie.dmn.feel.runtime.functions.extended.DateFunction;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;

public class RegexpUtil {

    public static final Pattern BEGIN_YEAR = Pattern.compile(DateFunction.BEGIN_YEAR_PATTERN); // FEEL spec, "specified by XML Schema Part 2 Datatypes", hence: yearFrag ::= '-'? (([1-9] digit digit digit+)) | ('0' digit digit digit))

    public static boolean find(final String input, final String pattern, final String flags) {
        int f = processFlags(flags);
        Pattern p = Pattern.compile(pattern, f);
        Matcher m = p.matcher(input);
        return m.find();
    }

    public static String formatDate(final LocalDate date) {
        return DateFunction.FEEL_DATE.format(date);
    }

    public static LocalDate parseFeelDate(final String val) {
        return LocalDate.from(DateFunction.FEEL_DATE.parse(val));
    }

    public static boolean findFindYear(final String val) {
        return BEGIN_YEAR.matcher(val).find();
    }

    public static List<String> split(final String string, final String delimiter, final String flags) {
        int f = processFlags(flags);
        Pattern p = Pattern.compile(delimiter, f);
        String[] split = p.split(string, -1);
        return Arrays.asList(split);
    }

    private static int processFlags(String flags) {
        int f = 0;
        if (flags != null) {
            if (flags.contains("s")) {
                f |= Pattern.DOTALL;
            }
            if (flags.contains("m")) {
                f |= Pattern.MULTILINE;
            }
            if (flags.contains("i")) {
                f |= Pattern.CASE_INSENSITIVE;
            }
        }
        return f;
    }
}
