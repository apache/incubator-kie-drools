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
package org.kie.pmml.api.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PrimitiveBoxedUtilsTest {

    private static final Class<?>[] primitives = {Boolean.TYPE,
            Byte.TYPE, Character.TYPE, Float.TYPE, Integer.TYPE,
            Long.TYPE, Short.TYPE, Double.TYPE};
    private static final Class<?>[] boxeds = {Boolean.class,
            Byte.class, Character.class, Float.class, Integer.class,
            Long.class, Short.class, Double.class};
    private static final int types = primitives.length;

    @Test
    void areSameWithBoxing() {
        for (int i = 0; i < types; i++) {
            assertThat(PrimitiveBoxedUtils.areSameWithBoxing(primitives[i], boxeds[i])).isTrue();
            assertThat(PrimitiveBoxedUtils.areSameWithBoxing(boxeds[i], primitives[i])).isTrue();
            assertThat(PrimitiveBoxedUtils.areSameWithBoxing(primitives[i], primitives[i])).isTrue();
            assertThat(PrimitiveBoxedUtils.areSameWithBoxing(boxeds[i], boxeds[i])).isTrue();
        }
        for (int i = 0; i < types; i++) {
            assertThat(PrimitiveBoxedUtils.areSameWithBoxing(primitives[i], boxeds[types - 1 - i])).isFalse();
            assertThat(PrimitiveBoxedUtils.areSameWithBoxing(boxeds[i], primitives[types - 1 - i])).isFalse();
        }
        assertThat(PrimitiveBoxedUtils.areSameWithBoxing(String.class, String.class)).isFalse();
        assertThat(PrimitiveBoxedUtils.areSameWithBoxing(double.class, String.class)).isFalse();
        assertThat(PrimitiveBoxedUtils.areSameWithBoxing(String.class, Double.class)).isFalse();
    }

    @Test
    void getKiePMMLPrimitiveBoxed() {
        for (int i = 0; i < types; i++) {
            assertThat(PrimitiveBoxedUtils.getKiePMMLPrimitiveBoxed(primitives[i]).isPresent()).isTrue();
            assertThat(PrimitiveBoxedUtils.getKiePMMLPrimitiveBoxed(boxeds[i]).isPresent()).isTrue();
        }
        assertThat(PrimitiveBoxedUtils.getKiePMMLPrimitiveBoxed(String.class)).isNotPresent();
    }

    @Test
    void isSameWithBoxing() {
        for (int i = 0; i < types; i++) {
            assertThat(PrimitiveBoxedUtils.getKiePMMLPrimitiveBoxed(primitives[i]).get().isSameWithBoxing(boxeds[i])).isTrue();
            assertThat(PrimitiveBoxedUtils.getKiePMMLPrimitiveBoxed(boxeds[i]).get().isSameWithBoxing(primitives[i])).isTrue();
            assertThat(PrimitiveBoxedUtils.getKiePMMLPrimitiveBoxed(primitives[i]).get().isSameWithBoxing(primitives[i])).isTrue();
            assertThat(PrimitiveBoxedUtils.getKiePMMLPrimitiveBoxed(boxeds[i]).get().isSameWithBoxing(boxeds[i])).isTrue();
            assertThat(PrimitiveBoxedUtils.getKiePMMLPrimitiveBoxed(primitives[i]).get().isSameWithBoxing(String.class)).isFalse();
            assertThat(PrimitiveBoxedUtils.getKiePMMLPrimitiveBoxed(boxeds[i]).get().isSameWithBoxing(String.class)).isFalse();
        }
    }
}