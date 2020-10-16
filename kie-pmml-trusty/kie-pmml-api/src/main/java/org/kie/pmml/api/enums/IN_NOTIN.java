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
 * @see <a href=http://dmg.org/pmml/v4-4/TreeModel.html#xsdElement_SimpleSetPredicate>SimpleSetPredicate</a>
 */
public enum IN_NOTIN {

    IN("isIn"),
    NOT_IN("isNotIn");

    private final String name;

    IN_NOTIN(String name) {
        this.name = name;
    }

    public static IN_NOTIN byName(String name) {
        return Arrays.stream(IN_NOTIN.values())
                .filter(value -> name.equals(value.name))
                .findFirst()
                .orElseThrow(() -> new KieEnumException("Failed to find IN_NOTIN with name: " + name));
    }

    public String getName() {
        return name;
    }

}
