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

public class FinishesPredicate extends AbstractTemporalPredicate {

    private final long endDev;

    public FinishesPredicate() {
        super(new Interval(0, Interval.MAX));
        this.endDev = 0;
    }

    public FinishesPredicate(long endDev, TimeUnit endDevLongUnit) {
        super(new Interval(0, Interval.MAX));
        this.endDev = unitToLong(endDev, endDevLongUnit);
    }

    @Override
    public String toString() {
        return "finishes" + interval;
    }

    @Override
    public boolean evaluate(long start1, long duration1, long end1, long start2, long duration2, long end2) {

        long distStart = start1 - start2;
        long distEnd = Math.abs( end2 - end1 );
        return (distStart > 0 && distEnd <= this.endDev);
    }
}
