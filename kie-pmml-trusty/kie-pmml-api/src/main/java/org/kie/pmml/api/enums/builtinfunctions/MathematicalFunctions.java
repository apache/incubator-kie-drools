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
public enum MathematicalFunctions {

    EXPM1("expm1"),
    HYPOT("hypot"),
    LN1P("ln1p"),
    RINT("rint"),
    SIN("sin"),
    ASIN("asin"),
    SINH("sinh"),
    COS("cos"),
    ACOS("acos"),
    COSH("cosh"),
    TAN("tan"),
    ATAN("atan"),
    TANH("tanh");

    private final String name;

    MathematicalFunctions(String name) {
        this.name = name;
    }

    public static boolean isMathematicalFunctions(String name) {
        return Arrays.stream(MathematicalFunctions.values())
                .anyMatch(value -> name.equals(value.name));
    }

    public static MathematicalFunctions byName(String name) {
        return Arrays.stream(MathematicalFunctions.values())
                .filter(value -> name.equals(value.name))
                .findFirst()
                .orElseThrow(() -> new KieEnumException("Failed to find MathematicalFunctions with name: " + name));
    }

    public String getName() {
        return name;
    }

    public Object getValue(final Object[] inputData) {
        switch (this) {
            default:
                throw new KiePMMLException("Unmanaged MathematicalFunctions " + this);
        }
    }

}
