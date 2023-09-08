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
package org.kie.dmn.validation.dtanalysis.model;

import java.time.LocalDate;

import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.Range.RangeBoundary;
import org.kie.dmn.feel.util.Generated;

public class Bound<V extends Comparable<V>> implements Comparable<Bound<V>> {

    private final V value;
    private final Range.RangeBoundary boundaryType;
    private final Interval parent;

    public Bound(V value, RangeBoundary boundaryType, Interval parent) {
        this.value = value;
        this.boundaryType = boundaryType;
        this.parent = parent;
    }

    @Override
    public int compareTo(Bound<V> o) {
        int valueCompare = BoundValueComparator.compareValueDispatchingToInf(this, o);
        if (valueCompare != 0) {
            return valueCompare;
        }

        if (parent != null && o.parent != null) {
            if (this.isLowerBound() && o.isLowerBound() && this.boundaryType == o.boundaryType) {
                return 0;
            } 
            if (this.isUpperBound() && o.isUpperBound() && this.boundaryType == o.boundaryType) {
                return 0;
            }
            if (this.isUpperBound() && this.boundaryType == RangeBoundary.OPEN) {
                return -1;
            }
            if (this.isLowerBound() && this.boundaryType == RangeBoundary.OPEN) {
                return 1;
            }
            if (this.isUpperBound() && this.boundaryType == RangeBoundary.CLOSED) {
                if (o.isLowerBound()) {
                    if (o.boundaryType == RangeBoundary.CLOSED) {
                        return 1;
                    } else {
                        return -1;
                    }
                } else {
                    return 1;
                }
            }
            if (this.isLowerBound() && this.boundaryType == RangeBoundary.CLOSED) {
                if (o.isUpperBound()) {
                    if (o.boundaryType == RangeBoundary.CLOSED) {
                        return -1;
                    } else {
                        return 1;
                    }
                } else {
                    return -1;
                }
            }
        }

        if (this.boundaryType == o.boundaryType) {
            return 0;
        } else if (this.boundaryType == RangeBoundary.OPEN) {
            return -1;
        } else {
            return 1;
        }
    }

    public V getValue() {
        return value;
    }

    public Range.RangeBoundary getBoundaryType() {
        return boundaryType;
    }

    public Interval getParent() {
        return parent;
    }

    public boolean isLowerBound() {
        return parent.getLowerBound() == this;
    }

    public boolean isUpperBound() {
        return parent.getUpperBound() == this;
    }

    @Override
    public String toString() {
        if (isLowerBound()) {
            return (boundaryType == RangeBoundary.OPEN ? "(" : "[") + value;
        } else {
            return value + (boundaryType == RangeBoundary.OPEN ? ")" : "]");
        }
    }

    @Generated("org.eclipse.jdt.internal.corext.codemanipulation")
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((boundaryType == null) ? 0 : boundaryType.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Generated("org.eclipse.jdt.internal.corext.codemanipulation")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Bound other = (Bound) obj;
        if (boundaryType != other.boundaryType) {
            return false;
        }
        if (value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!value.equals(other.value)) {
            return false;
        }
        return true;
    }

    /**
     * Returns true if left is overlapping or adjacent to right
     */
    public static boolean adOrOver(Bound<?> left, Bound<?> right) {
        final Comparable<?> leftValue = left.getValue();
        final Comparable<?> rightValue = right.getValue();
        final boolean isValueEqual = leftValue.equals(rightValue);
        final boolean isBothOpen = left.getBoundaryType() == RangeBoundary.OPEN && right.getBoundaryType() == RangeBoundary.OPEN;
        if (isValueEqual && !isBothOpen) { // trivial case.
            return true;
        }
        if (leftValue instanceof LocalDate && rightValue instanceof LocalDate) {
            final boolean date1dayOff = leftValue.equals(((LocalDate)rightValue).minusDays(1));
            final boolean isBothClosed = left.getBoundaryType() == RangeBoundary.CLOSED && right.getBoundaryType() == RangeBoundary.CLOSED;
            return date1dayOff && isBothClosed; // unless we already returned for the trivial case, two date-based Bounds are adjacent for 1day diff closed Bounds.
        }
        return false;
    }

    public static String boundValueToString(Comparable<?> value) {
        return value instanceof String ? "\"" + value + "\"" : value.toString();
    }
}
