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
import java.util.ArrayList;
import java.util.List;

import org.gwtproject.regexp.shared.MatchResult;
import org.gwtproject.regexp.shared.RegExp;
import org.gwtproject.regexp.shared.SplitResult;
import org.kie.dmn.feel.runtime.functions.extended.DateFunction;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;

public class RegexpUtil {
    public static final RegExp BEGIN_YEAR = RegExp.compile(DateFunction.BEGIN_YEAR_PATTERN); // FEEL spec, "specified by XML Schema Part 2 Datatypes", hence: yearFrag ::= '-'? (([1-9] digit digit digit+)) | ('0' digit digit digit))

    public static boolean find(final String input, final String pattern, String flags) {

        if (flags == null) {
            flags = "";
        }
        final RegExp p = RegExp.compile(pattern, flags);
        final MatchResult m = p.exec(input);
        return m != null;
    }


    public static LocalDate parseFeelDate(final String val) {
        return LocalDate.from(DateFunction.FEEL_DATE.parse(val));
    }

    public static boolean findFindYear(final String val) { // TODO find find?
        return BEGIN_YEAR.exec(val) != null;
    }
    public static List<String> split(final String string, final String delimiter, final String flags) {
        ArrayList<String> result = new ArrayList<>();
        SplitResult splitResult = getRegExp(delimiter, flags).split(string, -1);
        for (int i = 0; i < splitResult.length(); i++) {
            result.add(splitResult.get(i));
        }
        return result;
    }

    private static RegExp getRegExp(final String delimiter, final String flags) {
        if (flags == null) {
            return RegExp.compile(delimiter);
        } else {
            return RegExp.compile(delimiter, flags);
        }
    }
}
