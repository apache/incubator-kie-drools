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
package org.kie.dmn.feel.runtime.impl;

import java.util.function.BiPredicate;

import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.util.BooleanEvalHelper;

public class RangeImpl
        implements Range {

    private RangeBoundary lowBoundary;
    private RangeBoundary highBoundary;
    private Comparable    lowEndPoint;
    private Comparable    highEndPoint;
    private boolean isLowerBoundaryValueUndefined;
    private boolean isUpperBoundaryValueUndefined;

    public RangeImpl() {
    }

    public RangeImpl(RangeBoundary lowBoundary, Comparable lowEndPoint, Comparable highEndPoint, RangeBoundary highBoundary, 
            boolean isLowerBoundaryValueUndefined, boolean isUpperBoundaryValueUndefined) {
        this.lowBoundary = lowBoundary;
        this.highBoundary = highBoundary;
        this.lowEndPoint = lowEndPoint;
        this.highEndPoint = highEndPoint;
        this.isLowerBoundaryValueUndefined = isLowerBoundaryValueUndefined;
        this.isUpperBoundaryValueUndefined = isUpperBoundaryValueUndefined;
    }

    @Override
    public RangeBoundary getLowBoundary() {
        return lowBoundary;
    }

    @Override
    public Comparable getLowEndPoint() {
        return lowEndPoint;
    }

    @Override
    public Comparable getHighEndPoint() {
        return highEndPoint;
    }

    @Override
    public RangeBoundary getHighBoundary() {
        return highBoundary;
    }

    @Override
    public boolean isLowerBoundaryValueUndefined() {
        return isLowerBoundaryValueUndefined;
    }

    @Override
    public boolean isUpperBoundaryValueUndefined() {
        return isUpperBoundaryValueUndefined;
    }

    @Override
    public Boolean includes(Object param) {
        if (param == null) {
            return null;
        }
        if (lowEndPoint == null) {
            if (highEndPoint == null) {
                return null;
            } else if (isLowerBoundaryValueUndefined) {
                return negInfRangeIncludes(param);
            } else {
                return false;
            }
        } else {
            if (highEndPoint == null && isUpperBoundaryValueUndefined) {
                return posInfRangeIncludes(param);
            } else if (highEndPoint != null) {
                return finiteRangeIncludes(param);
            } else {
                return false;
            }
        }
    }

    private Boolean finiteRangeIncludes(Object param) {
        if (lowBoundary == RangeBoundary.OPEN && highBoundary == RangeBoundary.OPEN) {
            return bothOrThrow(compare(lowEndPoint, param, (l, r) -> l.compareTo(r) < 0) , compare(highEndPoint, param,  (l, r) -> l.compareTo(r) > 0), param);
        } else if (lowBoundary == RangeBoundary.OPEN && highBoundary == RangeBoundary.CLOSED) {
            return bothOrThrow(compare(lowEndPoint, param, (l, r) -> l.compareTo(r) < 0) , compare(highEndPoint, param,  (l, r) -> l.compareTo(r) >= 0), param);
        } else if (lowBoundary == RangeBoundary.CLOSED && highBoundary == RangeBoundary.OPEN) {
            return bothOrThrow(compare(lowEndPoint, param, (l, r) -> l.compareTo(r) <= 0) , compare(highEndPoint, param,  (l, r) -> l.compareTo(r) > 0), param);
        } else if (lowBoundary == RangeBoundary.CLOSED && highBoundary == RangeBoundary.CLOSED) {
            return bothOrThrow(compare(lowEndPoint, param, (l, r) -> l.compareTo(r) <= 0) , compare(highEndPoint, param,  (l, r) -> l.compareTo(r) >= 0), param);
        }
        throw new RuntimeException("unknown boundary combination");
    }

    private Boolean posInfRangeIncludes(Object param) {
        if (lowBoundary == RangeBoundary.OPEN) {
            return compare(lowEndPoint, param, (l, r) -> l.compareTo(r) < 0);
        } else {
            return compare(lowEndPoint, param, (l, r) -> l.compareTo(r) <= 0);
        }
    }

    private Boolean negInfRangeIncludes(Object param) {
        if (highBoundary == RangeBoundary.OPEN) {
            return compare(highEndPoint, param,  (l, r) -> l.compareTo(r) > 0);
        } else {
            return compare(highEndPoint, param,  (l, r) -> l.compareTo(r) >= 0);
        }
    }
    
    private Boolean bothOrThrow(Boolean left, Boolean right, Object param) {
        if (left == null || right == null) {
            throw new IllegalArgumentException("Range.include("+classOf(param)+") not comparable with "+classOf(lowEndPoint)+", "+classOf(highEndPoint));
        }
        return left && right;
    }
    
    private static String classOf(Object p) {
        return p != null ? p.getClass().toString() : "null";
    }
    
    private static Boolean compare(Comparable left, Object right, BiPredicate<Comparable, Comparable> op) {
        if (left.getClass().isAssignableFrom(right.getClass())) { // short path
                return op.test(left, (Comparable) right);
        }
        return BooleanEvalHelper.compare(left, right, op); // defer to full DMN/FEEL semantic
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( !(o instanceof RangeImpl) ) return false;

        RangeImpl range = (RangeImpl) o;

        if ( lowBoundary != range.lowBoundary ) return false;
        if ( highBoundary != range.highBoundary ) return false;
        if (isLowerBoundaryValueUndefined != range.isLowerBoundaryValueUndefined()) return false;
        if (isUpperBoundaryValueUndefined != range.isUpperBoundaryValueUndefined()) return false;
        if ( lowEndPoint != null ? !lowEndPoint.equals( range.lowEndPoint ) : range.lowEndPoint != null ) return false;
        return highEndPoint != null ? highEndPoint.equals( range.highEndPoint ) : range.highEndPoint == null;

    }

    @Override
    public int hashCode() {
        int result = lowBoundary != null ? lowBoundary.hashCode() : 0;
        result = 31 * result + (highBoundary != null ? highBoundary.hashCode() : 0);
        result = 31 * result + (lowEndPoint != null ? lowEndPoint.hashCode() : 0);
        result = 31 * result + (highEndPoint != null ? highEndPoint.hashCode() : 0);
        result = 31 * result + (isLowerBoundaryValueUndefined ? 1 : 0);
        result = 31 * result + (isUpperBoundaryValueUndefined ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return (lowBoundary == RangeBoundary.OPEN ? "(" : "[") +
               " " + lowEndPoint +
               " .. " + highEndPoint +
               " " + ( highBoundary == RangeBoundary.OPEN ? ")" : "]" );
    }
}
