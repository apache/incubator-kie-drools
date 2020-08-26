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
package org.kie.pmml.commons.model.enums;

import java.util.Arrays;

import org.kie.pmml.commons.exceptions.KieEnumException;
import org.kie.pmml.commons.exceptions.KiePMMLException;

/**
 * @see <a href=http://dmg.org/pmml/v4-4/TreeModel.html#xsdGroup_PREDICATE>PREDICATE</a>
 */
public enum ARRAY_TYPE {

    INT("int"),
    STRING("string"),
    REAL("real");

    private final String name;

    ARRAY_TYPE(String name) {
        this.name = name;
    }

    public static ARRAY_TYPE byName(String name) {
        return Arrays.stream(ARRAY_TYPE.values())
                .filter(value -> name.equals(value.name))
                .findFirst()
                .orElseThrow(() -> new KieEnumException("Failed to find ARRAY_TYPE with name: " + name));
    }

    public String getName() {
        return name;
    }

    public Object getValue(String rawValue) {
        switch (this) {
            case INT:
                return Integer.valueOf(rawValue);
            case STRING:
                return rawValue;
            case REAL:
                return Double.valueOf(rawValue);
            default:
                throw new KiePMMLException("Unknown ARRAY_TYPE " + this);
        }
    }
}
