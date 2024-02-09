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
import java.util.Date;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.kie.pmml.api.exceptions.KieEnumException;
import org.kie.pmml.api.exceptions.KiePMMLException;

import static org.kie.pmml.api.enums.BUILTIN_FUNCTIONS.checkDate;
import static org.kie.pmml.api.enums.BUILTIN_FUNCTIONS.checkInteger;
import static org.kie.pmml.api.enums.BUILTIN_FUNCTIONS.checkLength;
import static org.kie.pmml.api.enums.BUILTIN_FUNCTIONS.checkMinimumLength;
import static org.kie.pmml.api.enums.BUILTIN_FUNCTIONS.checkNumber;
import static org.kie.pmml.api.enums.BUILTIN_FUNCTIONS.checkString;
import static org.kie.pmml.api.enums.BUILTIN_FUNCTIONS.checkStrings;

public enum StringFunctions {

    UPPERCASE("uppercase"),
    LOWERCASE("lowercase"),
    STRING_LENGTH("stringLength"),
    SUBSTRING("substring"),
    TRIM_BLANKS("trimBlanks"),
    CONCAT("concat"),
    REPLACE("replace"),
    MATCHES("matches"),
    FORMAT_NUMBER("formatNumber"),
    FORMAT_DATE_TIME("formatDatetime");

    private final String name;

    StringFunctions(String name) {
        this.name = name;
    }

    public static boolean isStringFunctions(String name) {
        return Arrays.stream(StringFunctions.values())
                .anyMatch(value -> name.equals(value.name));
    }

    public static StringFunctions byName(String name) {
        return Arrays.stream(StringFunctions.values())
                .filter(value -> name.equals(value.name))
                .findFirst()
                .orElseThrow(() -> new KieEnumException("Failed to find StringFunctions with name: " + name));
    }

    public String getName() {
        return name;
    }

    public Object getValue(final Object[] inputData) {
        switch (this) {
            case LOWERCASE:
                return lowercase(inputData);
            case UPPERCASE:
                return uppercase(inputData);
            case STRING_LENGTH:
                return stringLength(inputData);
            case SUBSTRING:
                return substring(inputData);
            case TRIM_BLANKS:
                return trimBlanks(inputData);
            case CONCAT:
                return concat(inputData);
            case REPLACE:
                return replace(inputData);
            case MATCHES:
                return matches(inputData);
            case FORMAT_NUMBER:
                return formatNumber(inputData);
            case FORMAT_DATE_TIME:
                return formatDatetime(inputData);
            default:
                throw new KiePMMLException("Unmanaged StringFunctions " + this);
        }
    }

    private String lowercase(final Object[] inputData) {
        checkStrings(inputData, 1);
        return ((String) inputData[0]).toLowerCase();
    }

    private String uppercase(final Object[] inputData) {
        checkStrings(inputData, 1);
        return ((String) inputData[0]).toUpperCase();
    }

    private int stringLength(final Object[] inputData) {
        checkStrings(inputData, 1);
        return ((String) inputData[0]).length();
    }

    private String substring(final Object[] inputData) {
        checkLength(inputData, 3);
        checkString(inputData[0]);
        checkInteger(inputData[1]);
        checkInteger(inputData[2]);
        int startPos = ((int) inputData[1]) -1;
        int length = (int) inputData[2];
        int endPos = startPos + length;
        return ((String) inputData[0]).substring(startPos, endPos);
    }

    private String trimBlanks(final Object[] inputData) {
        checkStrings(inputData, 1);
        return ((String) inputData[0]).trim();
    }

    private String concat(final Object[] inputData) {
        checkMinimumLength(inputData, 2);
        return Arrays.stream(inputData)
                .map(obj -> obj != null ? obj.toString() : null)
                .collect(Collectors.joining(""));
    }

    private String replace(final Object[] inputData) {
        checkStrings(inputData, 3);
        String pattern = (String) inputData[1];
        String replacement = ((String) inputData[2]);
        return ((String) inputData[0]).replaceAll(pattern, replacement);
    }

    private boolean matches(final Object[] inputData) {
        checkStrings(inputData, 2);
        String input = (String) inputData[0];
        String pattern = ((String) inputData[1]);
        return input.contains(pattern) || Pattern.compile(pattern).matcher(input).find();
    }

    private String formatNumber(final Object[] inputData) {
        checkLength(inputData, 2);
        checkNumber(inputData[0]);
        checkString(inputData[1]);
        Number input = (Number) inputData[0];
        String pattern = ((String) inputData[1]);
        return String.format(pattern, input);
    }

    private String formatDatetime(final Object[] inputData) {
        checkLength(inputData, 2);
        checkDate(inputData[0]);
        checkString(inputData[1]);
        Date input = (Date) inputData[0];
        String pattern = ((String) inputData[1])
                .replace("%", "%1$t");
        return String.format(pattern, input);
    }
}
