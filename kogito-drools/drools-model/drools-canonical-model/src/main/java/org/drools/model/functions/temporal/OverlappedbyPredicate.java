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

public class OverlappedbyPredicate extends AbstractTemporalPredicate {

    long minDev;
    long maxDev;

    public OverlappedbyPredicate() {
        super(new Interval(0, Interval.MAX));
        this.minDev = 1;
        this.maxDev = Long.MAX_VALUE;
    }

    public OverlappedbyPredicate(long maxDev, TimeUnit maxDevTimeUnit) {
        super(new Interval(0, Interval.MAX));
        this.minDev = 1;
        this.maxDev = unitToLong(maxDev, maxDevTimeUnit);
    }

    public OverlappedbyPredicate(long minDev, TimeUnit minDevTimeUnit, long maxDev, TimeUnit maxDevTimeUnit) {
        super(new Interval(0, Interval.MAX));
        this.minDev = unitToLong(minDev, minDevTimeUnit);
        this.maxDev = unitToLong(maxDev, maxDevTimeUnit);
    }

    @Override
    public String toString() {
        return "overlappedby" + interval;
    }

    @Override
    public boolean evaluate(long start1, long duration1, long end1, long start2, long duration2, long end2) {

        long startTS = start1;
        long endTS = end2;
        long dist = endTS - startTS;
        return  ( start2 < startTS &&
                endTS < end1 &&
                dist >= this.minDev && dist <= this.maxDev );
    }
}
