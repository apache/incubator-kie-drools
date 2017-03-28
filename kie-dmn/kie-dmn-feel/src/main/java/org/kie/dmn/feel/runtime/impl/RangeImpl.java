/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.feel.runtime.impl;

import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.runtime.Range;

public class RangeImpl
        implements Range {

    private RangeBoundary lowBoundary;
    private RangeBoundary highBoundary;
    private Comparable    lowEndPoint;
    private Comparable    highEndPoint;

    public RangeImpl() {
    }

    public RangeImpl(RangeBoundary lowBoundary, Comparable lowEndPoint, Comparable highEndPoint, RangeBoundary highBoundary) {
        this.lowBoundary = lowBoundary;
        this.highBoundary = highBoundary;
        this.lowEndPoint = lowEndPoint;
        this.highEndPoint = highEndPoint;
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
    public Boolean includes(Comparable param) {
        if ( lowBoundary == RangeBoundary.OPEN && highBoundary == RangeBoundary.OPEN ) {
            return param == null || lowEndPoint == null || highEndPoint == null ? null : ((Comparable) param).compareTo( lowEndPoint ) > 0 && ((Comparable) param).compareTo( highEndPoint ) < 0;
        } else if ( lowBoundary == RangeBoundary.OPEN && highBoundary == RangeBoundary.CLOSED ) {
            return param == null || lowEndPoint == null || highEndPoint == null ? null : ((Comparable) param).compareTo( lowEndPoint ) > 0 && ((Comparable) param).compareTo( highEndPoint ) <= 0;
        } else if ( lowBoundary == RangeBoundary.CLOSED && highBoundary == RangeBoundary.OPEN ) {
            return param == null || lowEndPoint == null || highEndPoint == null ? null : ((Comparable) param).compareTo( lowEndPoint ) >= 0 && ((Comparable) param).compareTo( highEndPoint ) < 0;
        } else if ( lowBoundary == RangeBoundary.CLOSED && highBoundary == RangeBoundary.CLOSED ) {
            return param == null || lowEndPoint == null || highEndPoint == null ? null : ((Comparable) param).compareTo( lowEndPoint ) >= 0 && ((Comparable) param).compareTo( highEndPoint ) <= 0;
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( !(o instanceof RangeImpl) ) return false;

        RangeImpl range = (RangeImpl) o;

        if ( lowBoundary != range.lowBoundary ) return false;
        if ( highBoundary != range.highBoundary ) return false;
        if ( lowEndPoint != null ? !lowEndPoint.equals( range.lowEndPoint ) : range.lowEndPoint != null ) return false;
        return highEndPoint != null ? highEndPoint.equals( range.highEndPoint ) : range.highEndPoint == null;

    }

    @Override
    public int hashCode() {
        int result = lowBoundary != null ? lowBoundary.hashCode() : 0;
        result = 31 * result + (highBoundary != null ? highBoundary.hashCode() : 0);
        result = 31 * result + (lowEndPoint != null ? lowEndPoint.hashCode() : 0);
        result = 31 * result + (highEndPoint != null ? highEndPoint.hashCode() : 0);
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
