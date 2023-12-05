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

public enum BASELINE_METHOD implements Named {

    MAX("max"),
    MIN("min"),
    MEAN("mean"),
    NEUTRAL("neutral"),
    OTHER("other");

    private String name;

    BASELINE_METHOD(String name) {
        this.name = name;
    }

    public static BASELINE_METHOD byName(String name) {
        return Arrays.stream(BASELINE_METHOD.values())
                .filter(value -> name.equals(value.name))
                .findFirst()
                .orElseThrow(() -> new KieEnumException("Failed to find BASELINE_METHOD with name: " + name));
    }

    public String getName() {
        return name;
    }

}
