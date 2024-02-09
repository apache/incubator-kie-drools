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

public class MetbyPredicate extends AbstractTemporalPredicate<MetbyPredicate> {

    private final long finalRange;

    public MetbyPredicate() {
        this(0);
    }

    public MetbyPredicate(long finalRange, TimeUnit finalRangeTimeUnit) {
        this( unitToLong(finalRange, finalRangeTimeUnit) );
    }

    private MetbyPredicate(long finalRange) {
        this.finalRange = finalRange;
    }

    @Override
    public String toString() {
        return (negated ? "not " : "") + "metBy[" + finalRange + "]";
    }

    @Override
    public Interval getInterval() {
        return negated ? new Interval( Interval.MIN, Interval.MAX ) : new Interval( Interval.MIN, 0 );
    }

    @Override
    public boolean evaluate(long start1, long duration1, long end1, long start2, long duration2, long end2) {
        long dist = Math.abs( start1 - end2 );
        return negated ^ ( dist <= this.finalRange );
    }

    @Override
    protected boolean isTemporalPredicateEqualTo( MetbyPredicate other ) {
        return finalRange == other.finalRange;
    }
}
