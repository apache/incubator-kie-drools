/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.pmml.api.enums;

import java.util.Arrays;

import org.kie.pmml.api.exceptions.KieEnumException;

public enum REASONCODE_ALGORITHM implements Named {

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
