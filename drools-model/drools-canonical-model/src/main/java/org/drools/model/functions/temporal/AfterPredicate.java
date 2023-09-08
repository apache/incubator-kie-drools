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
package org.drools.model.functions.temporal;

import java.util.concurrent.TimeUnit;

import static org.drools.model.functions.temporal.TimeUtil.unitToLong;

public class AfterPredicate extends AbstractTemporalPredicate<AfterPredicate> {

    private final long initRange;
    private final long finalRange;

    public AfterPredicate() {
        this( 1, Long.MAX_VALUE );
    }

    public AfterPredicate(long dev, TimeUnit devUnit) {
        this( unitToLong(dev, devUnit), Long.MAX_VALUE );
    }

    public AfterPredicate(long startDev, TimeUnit startDevUnit, long endDev, TimeUnit endDevUnit) {
        this( unitToLong(startDev, startDevUnit), unitToLong(endDev, endDevUnit) );
    }

    private AfterPredicate(long initRange, long finalRange) {
        this.initRange = initRange;
        this.finalRange = finalRange;
    }

    @Override
    public String toString() {
        return (negated ? "not " : "") + "after[" + initRange + ", " + finalRange + "]";
    }

    @Override
    public boolean evaluate(long start1, long duration1, long end1, long start2, long duration2, long end2) {
        long diff = start1 - end2;
        return negated ^ (diff >= initRange && diff <= finalRange);
    }

    @Override
    public Interval getInterval() {
        long init = this.initRange;
        long end = this.finalRange;
        if ( negated ) {
            if ( init == Interval.MIN && end != Interval.MAX ) {
                init = finalRange + 1;
                end = Interval.MAX;
            } else if ( init != Interval.MIN && end == Interval.MAX ) {
                init = Interval.MIN;
                end = initRange - 1;
            } else if ( init == Interval.MIN ) {
                init = 0;
                end = -1;
            } else {
                init = Interval.MIN;
                end = Interval.MAX;
            }
        }
        return new Interval( init, end );
    }

    @Override
    protected boolean isTemporalPredicateEqualTo( AfterPredicate other ) {
        return initRange == other.initRange && finalRange == other.finalRange;
    }
}
