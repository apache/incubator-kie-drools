/*
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

package org.optaplanner.core.impl.domain.solution.cloner;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;

/**
 * @implNote This class is thread-safe.
 */
final class DeepCloningFieldCloner {

    private final AtomicReference<Metadata> valueDeepCloneDecision = new AtomicReference<>();
    private final AtomicInteger fieldDeepCloneDecision = new AtomicInteger(-1);
    private final Field field;

    public DeepCloningFieldCloner(Field field) {
        this.field = Objects.requireNonNull(field);
    }

    public Field getField() {
        return field;
    }

    /**
     *
     * @param solutionDescriptor never null
     * @param original never null, source object
     * @param clone never null, target object
     * @return null if cloned, the original uncloned value otherwise
     * @param <C>
     */
    public <C> Object clone(SolutionDescriptor<?> solutionDescriptor, C original, C clone) {
        Object originalValue = FieldCloningUtils.getObjectFieldValue(original, field);
        if (deepClone(solutionDescriptor, original.getClass(), originalValue)) { // Defer filling in the field.
            return originalValue;
        } else { // Shallow copy.
            FieldCloningUtils.setObjectFieldValue(clone, field, originalValue);
            return null;
        }
    }

    /**
     * Obtaining the decision on whether or not to deep-clone is expensive.
     * This method exists to cache those computations as much as possible,
     * while maintaining thread-safety.
     *
     * @param solutionDescriptor never null
     * @param fieldTypeClass never null
     * @param originalValue never null
     * @return true if the value needs to be deep-cloned
     */
    private boolean deepClone(SolutionDescriptor<?> solutionDescriptor, Class<?> fieldTypeClass, Object originalValue) {
        if (originalValue == null) {
            return false;
        }
        /*
         * This caching mechanism takes advantage of the fact that, for a particular field on a particular class,
         * the types of values contained are unlikely to change and therefore it is safe to cache the calculation.
         * In the unlikely event of a cache miss, we recompute.
         */
        boolean isValueDeepCloned = valueDeepCloneDecision.updateAndGet(old -> {
            Class<?> originalClass = originalValue.getClass();
            if (old == null || old.clz != originalClass) {
                return new Metadata(originalClass, DeepCloningUtils.isClassDeepCloned(solutionDescriptor, originalClass));
            } else {
                return old;
            }
        }).decision;
        if (isValueDeepCloned) { // The value has to be deep-cloned. Does not matter what the field says.
            return true;
        }
        /*
         * The decision to clone a field is constant once it has been made.
         * The fieldTypeClass is guaranteed to not change for the particular field.
         */
        if (fieldDeepCloneDecision.get() < 0) {
            fieldDeepCloneDecision.set(DeepCloningUtils.isFieldDeepCloned(solutionDescriptor, field, fieldTypeClass) ? 1 : 0);
        }
        return fieldDeepCloneDecision.get() == 1;
    }

    private static final class Metadata {

        private final Class<?> clz;
        private final boolean decision;

        public Metadata(Class<?> clz, boolean decision) {
            this.clz = clz;
            this.decision = decision;
        }

    }
}
