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

public class DuringPredicate extends AbstractTemporalPredicate<DuringPredicate> {

    private final long startMinDev, startMaxDev;
    private final long endMinDev, endMaxDev;

    public DuringPredicate() {
        this(1, Long.MAX_VALUE);
    }

    public DuringPredicate(long max, TimeUnit maxUnit) {
        this(1, unitToLong(max, maxUnit));
    }

    public DuringPredicate(long min, TimeUnit minUnit, long max, TimeUnit maxUnit) {
        this(unitToLong(min, minUnit), unitToLong(max, maxUnit));
    }

    private DuringPredicate( long min, long max ) {
        this.startMinDev = min;
        this.startMaxDev = max;
        this.endMinDev = min;
        this.endMaxDev = max;
    }

    @Override
    public Interval getInterval() {
        return negated ? new Interval( Interval.MIN, Interval.MAX ) : new Interval( 1, Interval.MAX );
    }

    @Override
    public String toString() {
        return (negated ? "not " : "") + "during[" + startMinDev + ", " + startMaxDev + "]";
    }

    @Override
    public boolean evaluate(long start1, long duration1, long end1, long start2, long duration2, long end2) {
        long distStart = start1 - start2;
        long distEnd = end2 - end1;
        return negated ^ (distStart >= this.startMinDev && distStart <= this.startMaxDev && distEnd >= this.endMinDev && distEnd <= this.endMaxDev);
    }

    @Override
    protected boolean isTemporalPredicateEqualTo( DuringPredicate other ) {
        return startMinDev == other.startMinDev && startMaxDev == other.startMaxDev &&
                endMinDev == other.endMinDev && endMaxDev == other.endMaxDev;
    }
}
