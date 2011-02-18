/*
 * Copyright 2010 JBoss Inc
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

package org.drools.time;

/**
 * A class to represent a time interval. Specially useful to 
 * calculate time distance between events constrained by
 * temporal constraints.
 * 
 * If the interval is open, i.e., from -infinitum to +infinitum,
 * the representation is created by using lowerBound = Long.MIN_VALUE
 * and upperBound = Long.MAX_VALUE.
 */
public class Interval implements Cloneable {
    public static final long MIN = Long.MIN_VALUE;
    public static final long MAX = Long.MAX_VALUE;
    
    private long lowerBound;
    private long upperBound;

    public Interval() {
        this.lowerBound = MIN;
        this.upperBound = MAX;
    }
    
    public Interval(long lowerBound, long upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    /**
     * Calculates the value of the intersection between
     * this Interval and another interval.
     * This is given by the following rule:
     * 
     * lowerBound = max( this.lowerBound, another.lowerBound )
     * upperBound = min( this.upperBound, another.upperBound )
     * 
     * @param another the other interval to calculate the intersection with.
     */
    public void intersect( Interval another ) {
        this.lowerBound = Math.max( this.lowerBound, another.lowerBound );
        this.upperBound = Math.min( this.upperBound, another.upperBound );
    }

    /**
     * Calculates the path addition of this interval with another interval.
     * This is given by the following rule:
     * 
     * lowerBound = ( this.lowerBound == MIN || another.lowerBound == MIN ) ? MIN : this.lowerBound+another.lowerBound;
     * upperBound = ( this.upperBound == MAX || another.upperBound == MAX ) ? MAX : this.upperBound+another.upperBound;
     * 
     * @param another the other interval to add into this interval
     */
    public void add( Interval another ) {
        this.lowerBound = ( this.lowerBound == MIN || another.lowerBound == MIN ) ? MIN : this.lowerBound+another.lowerBound;
        this.upperBound = ( this.upperBound == MAX || another.upperBound == MAX ) ? MAX : this.upperBound+another.upperBound;
    }

    public long getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(long lowerBound) {
        this.lowerBound = lowerBound;
    }

    public long getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(long upperBound) {
        this.upperBound = upperBound;
    }
    
    public Interval clone() {
        return new Interval( this.lowerBound, this.upperBound );
    }

    /**
     * @inheritDoc
     */
    public String toString() {
        String result = "[ " + (( this.lowerBound == MIN ) ? "-NA" : this.lowerBound ) + ", "+
                (( this.upperBound == MAX ) ? " NA" : this.upperBound ) + " ]";
        return result;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (lowerBound ^ (lowerBound >>> 32));
        result = prime * result + (int) (upperBound ^ (upperBound >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        Interval other = (Interval) obj;
        if ( lowerBound != other.lowerBound ) return false;
        if ( upperBound != other.upperBound ) return false;
        return true;
    }
    
    
}
