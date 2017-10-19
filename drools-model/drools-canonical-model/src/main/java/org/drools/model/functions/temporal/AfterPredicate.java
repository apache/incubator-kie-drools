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

public class AfterPredicate extends AbstractTemporalPredicate {

    public AfterPredicate() {
        this( new Interval() );
    }

    public AfterPredicate( Interval interval ) {
        super( interval );
    }

    @Override
    public String toString() {
        return "after" + interval;
    }

    @Override
    public boolean evaluate(long start1, long duration1, long start2, long duration2) {
        long diff = start1 - start2;
        return diff >= interval.getLowerBound() && diff <= interval.getUpperBound();
    }
}
