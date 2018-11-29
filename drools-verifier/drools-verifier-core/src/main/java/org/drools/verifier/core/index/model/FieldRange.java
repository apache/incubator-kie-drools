/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.drools.verifier.core.index.model;

public class FieldRange<T> {

    private final T min;
    private final T max;

    public FieldRange(final T min,
                      final T max) {
        this.min = min;
        this.max = max;
    }

    public static double getDoubleMaxValue(final ObjectField field) {
        if (field.getRange().isPresent()) {
            try {
                return (Double) field.getRange().get().getMax();
            } catch (Exception e) {
                return Double.MAX_VALUE;
            }
        } else {
            return Double.MAX_VALUE;
        }
    }

    public static double getDoubleMinValue(final ObjectField field) {
        if (field.getRange().isPresent()) {
            try {
                return (Double) field.getRange().get().getMin();
            } catch (Exception e) {
                return Double.MIN_VALUE;
            }
        } else {
            return Double.MIN_VALUE;
        }
    }

    public static int getIntegerMaxValue(final ObjectField field) {
        if (field.getRange().isPresent()) {
            try {
                return (Integer) field.getRange().get().getMax();
            } catch (Exception e) {
                return Integer.MAX_VALUE;
            }
        } else {
            return Integer.MAX_VALUE;
        }
    }

    public static int getIntegerMinValue(final ObjectField field) {
        if (field.getRange().isPresent()) {
            try {
                return (Integer) field.getRange().get().getMin();
            } catch (Exception e) {
                return Integer.MIN_VALUE;
            }
        } else {
            return Integer.MIN_VALUE;
        }
    }

    public T getMin() {
        return min;
    }

    public T getMax() {
        return max;
    }
}
