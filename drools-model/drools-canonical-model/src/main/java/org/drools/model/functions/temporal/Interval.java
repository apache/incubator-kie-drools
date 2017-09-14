/*
 * Copyright 2005 JBoss Inc
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

package org.drools.model.functions.temporal;

import java.util.concurrent.TimeUnit;

import static org.drools.model.functions.temporal.TimeUtil.unitToLong;

public class Interval {
    public static final long MIN = Long.MIN_VALUE;
    public static final long MAX = Long.MAX_VALUE;

    private final long lowerBound;
    private final long upperBound;

    public Interval() {
        this( MIN, MAX );
    }

    public Interval( long lowerBound, long upperBound ) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public Interval( long lowerBound, TimeUnit lowerUnit, long upperBound, TimeUnit upperUnit ) {
        this( unitToLong( lowerBound, lowerUnit), unitToLong( upperBound, upperUnit) );
    }

    public long getLowerBound() {
        return lowerBound;
    }

    public long getUpperBound() {
        return upperBound;
    }

    @Override
    public String toString() {
        return "[" + lowerBound + "," + upperBound + "]";
    }
}