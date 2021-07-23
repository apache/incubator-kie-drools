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

import java.util.Arrays;

import org.kie.pmml.api.exceptions.KieEnumException;
import org.kie.pmml.api.exceptions.KiePMMLException;

import static org.kie.pmml.api.enums.BUILTIN_FUNCTIONS.checkStrings;

/**
 * @see <a http://dmg.org/pmml/v4-4-1/BuiltinFunctions.html>Built-in functions</a>
 */
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

}
