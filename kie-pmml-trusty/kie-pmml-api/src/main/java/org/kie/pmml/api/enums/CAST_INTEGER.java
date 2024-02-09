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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

import org.apache.commons.math3.util.Precision;
import org.kie.pmml.api.exceptions.KieEnumException;

public enum CAST_INTEGER implements Named {

    ROUND("round", CAST_INTEGER::getRound),
    CEILING("ceiling", CAST_INTEGER::getCeiling),
    FLOOR("floor", CAST_INTEGER::getFloor);

    private String name;
    private Function<Double, Integer> castingFunction;

    CAST_INTEGER(String name, Function<Double, Integer> castingFunction) {
        this.name = name;
        this.castingFunction = castingFunction;
    }

    public static CAST_INTEGER byName(String name) {
        return Arrays.stream(CAST_INTEGER.values()).filter(value -> Objects.equals(name, value.name)).findFirst().orElseThrow(() -> new KieEnumException("Failed to find CAST_INTEGER with name: " + name));
    }

    public String getName() {
        return name;
    }

    public Integer getScaledValue(Double toScale) {
        return castingFunction.apply(toScale);
    }

    static Integer getRound(Double toScale) {
        return (int) Precision.round(toScale, 0, BigDecimal.ROUND_HALF_UP);
    }

    static Integer getCeiling(Double toScale) {
        return (int) Precision.round(toScale, 0, BigDecimal.ROUND_CEILING);
    }

    static Integer getFloor(Double toScale) {
        return (int) Precision.round(toScale, 0, BigDecimal.ROUND_FLOOR);
    }
}
