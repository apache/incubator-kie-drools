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
public enum BooleanFunctions {

    IS_MISSING("isMissing"),
    IS_NOT_MISSING("isNotMissing"),
    IS_VALID("isValid"),
    IS_NOT_VALID("isNotValid"),
    EQUAL("equal"),
    NOT_EQUAL("notEqual"),
    LESS_THAN("lessThan"),
    LESS_OR_EQUAL("lessOrEqual"),
    GREATER_THAN("greaterThan"),
    GREATER_OR_EQUAL("greaterOrEqual"),
    AND("and"),
    OR("or"),
    NOT("not"),
    IS_IN("isIn"),
    IS_NOT_IN("isNotIn"),
    IF("if");

    private final String name;

    BooleanFunctions(String name) {
        this.name = name;
    }

    public static boolean isBooleanFunctions(String name) {
        return Arrays.stream(BooleanFunctions.values())
                .anyMatch(value -> name.equals(value.name));
    }

    public static BooleanFunctions byName(String name) {
        return Arrays.stream(BooleanFunctions.values())
                .filter(value -> name.equals(value.name))
                .findFirst()
                .orElseThrow(() -> new KieEnumException("Failed to find BooleanFunctions with name: " + name));
    }

    public String getName() {
        return name;
    }

    public Object getValue(final Object[] inputData) {
        switch (this) {
            default:
                throw new KiePMMLException("Unmanaged BooleanFunctions " + this);
        }
    }

}
