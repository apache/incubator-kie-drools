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
import java.util.Objects;

import org.kie.pmml.commons.exceptions.KieEnumException;

/**
 * @see <a href=http://dmg.org/pmml/v4-2-1/DataDictionary.html#xsdType_OPTYPE>OPTYPE</a>
 */
public enum OP_TYPE {

    CATEGORICAL("categorical"),
    ORDINAL("ordinal"),
    CONTINUOUS("continuous");

    private String name;

    OP_TYPE(String name) {
        this.name = name;
    }

    public static OP_TYPE byName(String name) {
        return Arrays.stream(OP_TYPE.values()).filter(value -> Objects.equals(name, value.name)).findFirst().orElseThrow(() -> new KieEnumException("Failed to find MINING_FUNCTION with name: " + name));
    }

    public String getName() {
        return name;
    }
}
