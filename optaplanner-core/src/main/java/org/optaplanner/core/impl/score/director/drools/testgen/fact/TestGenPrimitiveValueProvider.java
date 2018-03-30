/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.optaplanner.core.impl.score.director.drools.testgen.fact;

class TestGenPrimitiveValueProvider extends TestGenAbstractValueProvider<Object> {

    private final Object uninitialized;

    public TestGenPrimitiveValueProvider(Object value) {
        super(value);
        Class<?> valueClass = value.getClass();
        if (valueClass.equals(Byte.class)) {
            uninitialized = (byte) 0;
        } else if (valueClass.equals(Short.class)) {
            uninitialized = (short) 0;
        } else if (valueClass.equals(Integer.class)) {
            uninitialized = 0;
        } else if (valueClass.equals(Long.class)) {
            uninitialized = 0L;
        } else if (valueClass.equals(Float.class)) {
            uninitialized = 0.0F;
        } else if (valueClass.equals(Double.class)) {
            uninitialized = 0.0;
        } else if (valueClass.equals(Boolean.class)) {
            uninitialized = false;
        } else if (valueClass.equals(Character.class)) {
            uninitialized = (char) 0;
        } else {
            throw new IllegalStateException("Unsupported type (" + valueClass + ").");
        }
    }

    @Override
    public Object getUninitialized() {
        return uninitialized;
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
