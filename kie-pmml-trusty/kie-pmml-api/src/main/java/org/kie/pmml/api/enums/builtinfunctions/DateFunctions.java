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

/**
 * @see <a http://dmg.org/pmml/v4-4-1/BuiltinFunctions.html>Built-in functions</a>
 */
public enum DateFunctions {

    DATE_DAYS_SINCE_YEAR("dateDaysSinceYear"),
    DATE_SECONDS_SINCE_YEAR("dateSecondsSinceYear"),
    DATE_SECONDS_SINCE_MIDNIGHT("dateSecondsSinceMidnight");

    private final String name;

    DateFunctions(String name) {
        this.name = name;
    }

    public static boolean isDateFunctions(String name) {
        return Arrays.stream(DateFunctions.values())
                .anyMatch(value -> name.equals(value.name));
    }

    public static DateFunctions byName(String name) {
        return Arrays.stream(DateFunctions.values())
                .filter(value -> name.equals(value.name))
                .findFirst()
                .orElseThrow(() -> new KieEnumException("Failed to find DateFunctions with name: " + name));
    }

    public String getName() {
        return name;
    }

    public Object getValue(final Object[] inputData) {
        switch (this) {
            default:
                throw new KiePMMLException("Unmanaged DateFunctions " + this);
        }
    }

}
