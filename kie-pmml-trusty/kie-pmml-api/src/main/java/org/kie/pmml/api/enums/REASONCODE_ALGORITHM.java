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
 * @see <a href=http://dmg.org/pmml/v4-4/Scorecard.html#reasoncodealgorithm>reasonCodeAlgorithm</a>
 */
public enum REASONCODE_ALGORITHM {

    POINTS_ABOVE("pointsAbove"),
    POINTS_BELOW("pointsBelow");

    private String name;

    REASONCODE_ALGORITHM(String name) {
        this.name = name;
    }

    public static REASONCODE_ALGORITHM byName(String name) {
        return Arrays.stream(REASONCODE_ALGORITHM.values())
                .filter(value -> name.equals(value.name))
                .findFirst()
                .orElseThrow(() -> new KieEnumException("Failed to find REASONCODE_ALGORITHM with name: " + name));
    }

    public String getName() {
        return name;
    }
}
