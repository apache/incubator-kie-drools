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

public class FinishesPredicate extends AbstractTemporalPredicate<FinishesPredicate> {

    private final long endDev;

    public FinishesPredicate() {
        this(0);
    }

    public FinishesPredicate(long endDev, TimeUnit endDevTimeUnit) {
        this( unitToLong(endDev, endDevTimeUnit) );
    }

    private FinishesPredicate(long endDev) {
        this.endDev = endDev;
    }

    @Override
    public String toString() {
        return (negated ? "not " : "") + "finishes[" + endDev + "]";
    }

    @Override
    public Interval getInterval() {
        return negated ? new Interval( Interval.MIN, Interval.MAX ) : new Interval( 0, Interval.MAX );
    }

    @Override
    public boolean evaluate(long start1, long duration1, long end1, long start2, long duration2, long end2) {

        long distStart = start1 - start2;
        long distEnd = Math.abs( end2 - end1 );
        return negated ^ (distStart > 0 && distEnd <= this.endDev);
    }

    @Override
    protected boolean isTemporalPredicateEqualTo( FinishesPredicate other ) {
        return endDev == other.endDev;
    }
}
