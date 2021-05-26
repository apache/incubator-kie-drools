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

package org.kie.pmml.commons.model;

import java.util.Collections;

import org.junit.Test;
import org.kie.pmml.api.enums.CAST_INTEGER;

import static org.junit.Assert.*;

public class KiePMMLTargetTest {

    private final static String TARGET_NAME = "TARGET_NAME";

    @Test
    public void modifyPrediction() {
        Object object = "STRING";
        KiePMMLTarget kiePMMLTarget = getBuilder().build();
        assertEquals(object, kiePMMLTarget.modifyPrediction(object));
        object = 4.33;
        assertEquals(object, kiePMMLTarget.modifyPrediction(object));
        kiePMMLTarget = getBuilder().withMin(4.34).build();
        object = "STRING";
        assertEquals(object, kiePMMLTarget.modifyPrediction(object));
        object = 4.33;
        assertEquals(4.34, kiePMMLTarget.modifyPrediction(object));
    }

    @Test
    public void applyMin() {
        KiePMMLTarget kiePMMLTarget = getBuilder().build();
        assertEquals(4.33, kiePMMLTarget.applyMin(4.33), 0.0);
        kiePMMLTarget = getBuilder().withMin(4.34).build();
        assertEquals(4.34, kiePMMLTarget.applyMin(4.33), 0.0);
        assertEquals(4.35, kiePMMLTarget.applyMin(4.35), 0.0);
    }

    @Test
    public void applyMax() {
        KiePMMLTarget kiePMMLTarget = getBuilder().build();
        assertEquals(4.33, kiePMMLTarget.applyMax(4.33), 0.0);
        kiePMMLTarget = getBuilder().withMax(4.34).build();
        assertEquals(4.33, kiePMMLTarget.applyMax(4.33), 0.0);
        assertEquals(4.34, kiePMMLTarget.applyMax(4.35), 0.0);
    }

    @Test
    public void applyRescaleFactor() {
        KiePMMLTarget kiePMMLTarget = getBuilder().build();
        assertEquals(4.0, kiePMMLTarget.applyRescaleFactor(4.0), 0.0);
        kiePMMLTarget = getBuilder().withRescaleFactor(2).build();
        assertEquals(8.0, kiePMMLTarget.applyRescaleFactor(4.0), 0.0);
    }

    @Test
    public void applyRescaleConstant() {
        KiePMMLTarget kiePMMLTarget = getBuilder().build();
        assertEquals(6.0, kiePMMLTarget.applyRescaleConstant(6.0), 0.0);
        kiePMMLTarget = getBuilder().withRescaleConstant(2).build();
        assertEquals(8.0, kiePMMLTarget.applyRescaleConstant(6.0), 0.0);
    }

    @Test
    public void applyCastInteger() {
        KiePMMLTarget kiePMMLTarget = getBuilder().build();
        assertEquals(2.718, (double) kiePMMLTarget.applyCastInteger(2.718), 0.0);
        kiePMMLTarget = getBuilder().withCastInteger(CAST_INTEGER.ROUND).build();
        assertEquals(3.0, (double) kiePMMLTarget.applyCastInteger(2.718), 0.0);
    }

    private KiePMMLTarget.Builder getBuilder() {
        return KiePMMLTarget.builder(TARGET_NAME, Collections.emptyList());
    }
}