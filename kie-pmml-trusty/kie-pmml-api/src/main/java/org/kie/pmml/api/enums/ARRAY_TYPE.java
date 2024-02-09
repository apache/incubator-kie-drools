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
import org.kie.pmml.api.exceptions.KiePMMLException;

public enum ARRAY_TYPE implements Named {

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
