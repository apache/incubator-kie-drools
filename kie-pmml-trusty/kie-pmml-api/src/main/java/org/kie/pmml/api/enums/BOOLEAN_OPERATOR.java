/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.pmml.api.enums;

import java.util.Arrays;

import org.kie.pmml.api.exceptions.KieEnumException;

/**
 * @see <a href=http://dmg.org/pmml/v4-4/TreeModel.html#xsdElement_CompoundPredicate>CompoundPredicate</a>
 */
public enum BOOLEAN_OPERATOR {

    OR("or", "||"),
    AND("and", "&&"),
    XOR("xor", "^"),
    SURROGATE("surrogate", "surrogate");

    private String name;
    private String customOperator;

    BOOLEAN_OPERATOR(String name, String customOperator) {
        this.name = name;
        this.customOperator = customOperator;
    }

    public static BOOLEAN_OPERATOR byName(String name) {
        return Arrays.stream(BOOLEAN_OPERATOR.values())
                .filter(value -> name.equals(value.name))
                .findFirst()
                .orElseThrow(() -> new KieEnumException("Failed to find BOOLEAN_OPERATOR with name: " + name));
    }

    public String getName() {
        return name;
    }

    public String getCustomOperator() {
        return customOperator;
    }
}
