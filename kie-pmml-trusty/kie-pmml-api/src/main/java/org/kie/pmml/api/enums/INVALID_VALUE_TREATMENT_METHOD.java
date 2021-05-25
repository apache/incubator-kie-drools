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
import java.util.Objects;

import org.kie.pmml.api.exceptions.KieEnumException;

/**
 * @see <a href=http://dmg.org/pmml/v4-4/MiningSchema.html#xsdType_INVALID-VALUE-TREATMENT-METHOD>INVALID-VALUE-TREATMENT-METHOD</a>
 */
public enum INVALID_VALUE_TREATMENT_METHOD {

    RETURN_INVALID("returnInvalid"),
    AS_IS("asIs"),
    AS_MISSING("asMissing"),
    AS_VALUE("asValue");

    private String name;

    INVALID_VALUE_TREATMENT_METHOD(String name) {
        this.name = name;
    }

    public static INVALID_VALUE_TREATMENT_METHOD byName(String name) {
        return Arrays.stream(INVALID_VALUE_TREATMENT_METHOD.values()).filter(value -> Objects.equals(name, value.name)).findFirst().orElseThrow(() -> new KieEnumException("Failed to find INVALID_VALUE_TREATMENT_METHOD with name: " + name));
    }

    public String getName() {
        return name;
    }
}
