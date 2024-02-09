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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Utility class to map a <b>primitive</b> with its <b>boxed</b> equivalent.
 * Needed to avoid reflection at runtime.
 */
public class PrimitiveBoxedUtils {

    private static final Set<PrimitiveBoxed> KIE_PMML_PRIMITIVE_BOXEDS = new HashSet<>(
            Arrays.asList(new PrimitiveBoxed(Boolean.TYPE, Boolean.class),
                          new PrimitiveBoxed(Byte.TYPE, Byte.class),
                          new PrimitiveBoxed(Character.TYPE, Character.class),
                          new PrimitiveBoxed(Float.TYPE, Float.class),
                          new PrimitiveBoxed(Integer.TYPE, Integer.class),
                          new PrimitiveBoxed(Long.TYPE, Long.class),
                          new PrimitiveBoxed(Short.TYPE, Short.class),
                          new PrimitiveBoxed(Double.TYPE, Double.class)));

    private PrimitiveBoxedUtils() {
        // Avoid instantiation
    }

    public static Optional<PrimitiveBoxed> getKiePMMLPrimitiveBoxed(Class<?> c) {
        return KIE_PMML_PRIMITIVE_BOXEDS.stream().filter(pBoxed -> c.equals(pBoxed.getPrimitive()) || c.equals(pBoxed.getBoxed())).findFirst();
    }

    /**
     * Returns <code>true</code> if one of the given <code>Class&lt;?&gt;</code> is a <b>primitive</b>
     * or <b>boxed</b> of a <code>KiePMMLPrimitiveBoxed</code> <b>and</b> the other <code>Class&lt;?&gt;</code> is the
     * <b>boxed/unboxed</b> counterpart of the same <code>KiePMMLPrimitiveBoxed</code> <b>OR</b> the same one.
     * Returns <code>false</code> otherwise. Please note it returns <code>false</code> even if both classes are <code>equals</code>
     * <b>but</b> are not a found between <code>KIE_PMML_PRIMITIVE_BOXEDS</code>
     * @param a
     * @param b
     * @return
     */
    public static boolean areSameWithBoxing(Class<?> a, Class<?> b) {
        Optional<PrimitiveBoxed> pmmlPrimitiveBoxed = getKiePMMLPrimitiveBoxed(a);
        return pmmlPrimitiveBoxed.filter(kiePMMLPrimitiveBoxed -> a.equals(b) || kiePMMLPrimitiveBoxed.isSameWithBoxing(b)).isPresent();
    }

    public static class PrimitiveBoxed {

        private final Class<?> primitive;
        private final Class<?> boxed;

        public PrimitiveBoxed(Class<?> primitive, Class<?> boxed) {
            this.primitive = primitive;
            this.boxed = boxed;
        }

        public Class<?> getPrimitive() {
            return primitive;
        }

        public Class<?> getBoxed() {
            return boxed;
        }

        /**
         * Returns <code>true</code> if the given <code>Class&lt;?&gt;</code> is the <b>primitive</b>
         * or the <b>boxed</b> one of the current <code>KiePMMLPrimitiveBoxed</code>
         * @param c
         * @return
         */
        public boolean isSameWithBoxing(Class<?> c) {
            return c.equals(primitive) || c.equals(boxed);
        }
    }
}
