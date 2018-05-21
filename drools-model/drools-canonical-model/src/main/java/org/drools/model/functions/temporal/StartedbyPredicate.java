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

public class StartedbyPredicate extends AbstractTemporalPredicate<StartedbyPredicate> {

    private final long startDev;

    public StartedbyPredicate() {
        this(0);
    }

    public StartedbyPredicate(long endDev, TimeUnit endDevTimeUnit) {
        this( unitToLong(endDev, endDevTimeUnit) );
    }

    private StartedbyPredicate(long startDev) {
        this.startDev = startDev;
    }

    @Override
    public String toString() {
        return (negated ? "not " : "") + "startedby[" + startDev + "]";
    }

    @Override
    public Interval getInterval() {
        return negated ? new Interval( Interval.MIN, Interval.MAX ) : new Interval( 0, 0 );
    }

    @Override
    public boolean evaluate(long start1, long duration1, long end1, long start2, long duration2, long end2) {

        long distStart = Math.abs(start1 - start2);
        long distEnd = end1 - end2;
        return negated ^ (distStart <= this.startDev && distEnd > 0);
    }

    @Override
    protected boolean isTemporalPredicateEqualTo( StartedbyPredicate other ) {
        return startDev == other.startDev;
    }
}
