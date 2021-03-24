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

package org.kie.pmml.api.utils;

import org.junit.Test;
import org.kie.pmml.api.utils.PrimitiveBoxedUtils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PrimitiveBoxedUtilsTest {

    private static final Class<?>[] primitives = {Boolean.TYPE,
            Byte.TYPE, Character.TYPE, Float.TYPE, Integer.TYPE,
            Long.TYPE, Short.TYPE, Double.TYPE};
    private static final Class<?>[] boxeds = {Boolean.class,
            Byte.class, Character.class, Float.class, Integer.class,
            Long.class, Short.class, Double.class};
    private static final int types = primitives.length;

    @Test
    public void areSameWithBoxing() {
        for (int i = 0; i < types; i++) {
            assertTrue(PrimitiveBoxedUtils.areSameWithBoxing(primitives[i], boxeds[i]));
            assertTrue(PrimitiveBoxedUtils.areSameWithBoxing(boxeds[i], primitives[i]));
            assertTrue(PrimitiveBoxedUtils.areSameWithBoxing(primitives[i], primitives[i]));
            assertTrue(PrimitiveBoxedUtils.areSameWithBoxing(boxeds[i], boxeds[i]));
        }
        for (int i = 0; i < types; i++) {
            assertFalse(PrimitiveBoxedUtils.areSameWithBoxing(primitives[i], boxeds[types - 1 - i]));
            assertFalse(PrimitiveBoxedUtils.areSameWithBoxing(boxeds[i], primitives[types - 1 - i]));
        }
        assertFalse(PrimitiveBoxedUtils.areSameWithBoxing(String.class, String.class));
        assertFalse(PrimitiveBoxedUtils.areSameWithBoxing(double.class, String.class));
        assertFalse(PrimitiveBoxedUtils.areSameWithBoxing(String.class, Double.class));
    }

    @Test
    public void getKiePMMLPrimitiveBoxed() {
        for (int i = 0; i < types; i++) {
            assertTrue(PrimitiveBoxedUtils.getKiePMMLPrimitiveBoxed(primitives[i]).isPresent());
            assertTrue(PrimitiveBoxedUtils.getKiePMMLPrimitiveBoxed(boxeds[i]).isPresent());
        }
        assertFalse(PrimitiveBoxedUtils.getKiePMMLPrimitiveBoxed(String.class).isPresent());
    }

    @Test
    public void isSameWithBoxing() {
        for (int i = 0; i < types; i++) {
            assertTrue(PrimitiveBoxedUtils.getKiePMMLPrimitiveBoxed(primitives[i]).get().isSameWithBoxing(boxeds[i]));
            assertTrue(PrimitiveBoxedUtils.getKiePMMLPrimitiveBoxed(boxeds[i]).get().isSameWithBoxing(primitives[i]));
            assertTrue(PrimitiveBoxedUtils.getKiePMMLPrimitiveBoxed(primitives[i]).get().isSameWithBoxing(primitives[i]));
            assertTrue(PrimitiveBoxedUtils.getKiePMMLPrimitiveBoxed(boxeds[i]).get().isSameWithBoxing(boxeds[i]));
            assertFalse(PrimitiveBoxedUtils.getKiePMMLPrimitiveBoxed(primitives[i]).get().isSameWithBoxing(String.class));
            assertFalse(PrimitiveBoxedUtils.getKiePMMLPrimitiveBoxed(boxeds[i]).get().isSameWithBoxing(String.class));
        }
    }
}