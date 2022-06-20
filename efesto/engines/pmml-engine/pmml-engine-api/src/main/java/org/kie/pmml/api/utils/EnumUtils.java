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

package org.kie.pmml.api.utils;

import org.kie.pmml.api.enums.Named;
import org.kie.pmml.api.exceptions.KieEnumException;

import java.util.Arrays;
import java.util.Objects;

public class EnumUtils {

    public static <K extends Enum<K> & Named> K enumByName(Class<K> enumClass, String name) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(value -> Objects.equals(name, value.getName()))
                .findFirst()
                .orElseThrow(() -> new KieEnumException("Failed to find " + enumClass.getSimpleName() + " enum constant with name: " + name));
    }

    private EnumUtils() {
        // Avoid instantiation
    }

}
