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
package org.kie.pmml.commons.model;

import java.util.Collections;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.CAST_INTEGER;
import org.kie.pmml.api.models.TargetField;

import static org.assertj.core.api.Assertions.assertThat;

public class KiePMMLTargetTest {

    private final static String TARGET_NAME = "TARGET_NAME";

    @Test
    void modifyPrediction() {
        Object object = "STRING";
        TargetField targetField = new TargetField(Collections.emptyList(), null, "string", null, null, null, null,
                                                  null);
        KiePMMLTarget kiePMMLTarget = getBuilder(targetField).build();
        assertThat(kiePMMLTarget.modifyPrediction(object)).isEqualTo(object);
        object = 4.33;
        assertThat(kiePMMLTarget.modifyPrediction(object)).isEqualTo(object);

        targetField = new TargetField(Collections.emptyList(), null, "string", null, 4.34, null, null, null);
        kiePMMLTarget = getBuilder(targetField).build();
        object = "STRING";
        assertThat(kiePMMLTarget.modifyPrediction(object)).isEqualTo(object);
        object = 4.33;
        assertThat(kiePMMLTarget.modifyPrediction(object)).isEqualTo(4.34);
    }

    @Test
    void applyMin() {
        TargetField targetField = new TargetField(Collections.emptyList(), null, "string", null, null, null, null,
                                                  null);
        KiePMMLTarget kiePMMLTarget = getBuilder(targetField).build();
        assertThat(kiePMMLTarget.applyMin(4.33)).isCloseTo(4.33, Offset.offset(0.0));
        targetField = new TargetField(Collections.emptyList(), null, "string", null, 4.34, null, null, null);
        kiePMMLTarget = getBuilder(targetField).build();
        assertThat(kiePMMLTarget.applyMin(4.33)).isCloseTo(4.34, Offset.offset(0.0));
        assertThat(kiePMMLTarget.applyMin(4.35)).isCloseTo(4.35, Offset.offset(0.0));
    }

    @Test
    void applyMax() {
        TargetField targetField = new TargetField(Collections.emptyList(), null, "string", null, null, null, null,
                                                  null);
        KiePMMLTarget kiePMMLTarget = getBuilder(targetField).build();
        assertThat(kiePMMLTarget.applyMax(4.33)).isCloseTo(4.33, Offset.offset(0.0));
        targetField = new TargetField(Collections.emptyList(), null, "string", null, null, 4.34, null, null);
        kiePMMLTarget = getBuilder(targetField).build();
        assertThat(kiePMMLTarget.applyMax(4.33)).isCloseTo(4.33, Offset.offset(0.0));
        assertThat(kiePMMLTarget.applyMax(4.35)).isCloseTo(4.34, Offset.offset(0.0));
    }

    @Test
    void applyRescaleFactor() {
        TargetField targetField = new TargetField(Collections.emptyList(), null, "string", null, null, null, null,
                                                  null);
        KiePMMLTarget kiePMMLTarget = getBuilder(targetField).build();
        assertThat(kiePMMLTarget.applyRescaleFactor(4.0)).isCloseTo(4.0, Offset.offset(0.0));
        targetField = new TargetField(Collections.emptyList(), null, "string", null, null, null, null, 2.0);
        kiePMMLTarget = getBuilder(targetField).build();
        assertThat(kiePMMLTarget.applyRescaleFactor(4.0)).isCloseTo(8.0, Offset.offset(0.0));
    }

    @Test
    void applyRescaleConstant() {
        TargetField targetField = new TargetField(Collections.emptyList(), null, "string", null, null, null, null,
                                                  null);
        KiePMMLTarget kiePMMLTarget = getBuilder(targetField).build();
        assertThat(kiePMMLTarget.applyRescaleConstant(6.0)).isCloseTo(6.0, Offset.offset(0.0));
        targetField = new TargetField(Collections.emptyList(), null, "string", null, null, null, 2.0, null);
        kiePMMLTarget = getBuilder(targetField).build();
        assertThat(kiePMMLTarget.applyRescaleConstant(6.0)).isCloseTo(8.0, Offset.offset(0.0));
    }

    @Test
    void applyCastInteger() {
        TargetField targetField = new TargetField(Collections.emptyList(), null, "string", null, null, null, null,
                                                  null);
        KiePMMLTarget kiePMMLTarget = getBuilder(targetField).build();
        assertThat((double) kiePMMLTarget.applyCastInteger(2.718)).isCloseTo(2.718, Offset.offset(0.0));
        targetField = new TargetField(Collections.emptyList(), null, "string", CAST_INTEGER.ROUND, null, null, null,
                                      null);
        kiePMMLTarget = getBuilder(targetField).build();
        assertThat((double) kiePMMLTarget.applyCastInteger(2.718)).isCloseTo(3.0, Offset.offset(0.0));
    }

    private KiePMMLTarget.Builder getBuilder(TargetField targetField) {
        return KiePMMLTarget.builder(TARGET_NAME, Collections.emptyList(), targetField);
    }
}