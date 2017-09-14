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

package org.drools.modelcompiler.constraints;

import org.drools.core.common.EventFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.Pattern;
import org.drools.core.spi.Tuple;
import org.drools.core.time.Interval;
import org.drools.model.SingleConstraint;
import org.drools.model.constraints.TemporalConstraint;
import org.drools.model.functions.temporal.TemporalPredicate;

public class TemporalConstraintEvaluator extends ConstraintEvaluator {

    private final Interval interval;

    public TemporalConstraintEvaluator( Declaration[] declarations, Pattern pattern, SingleConstraint constraint ) {
        super( declarations, pattern, constraint );
        TemporalPredicate temporalPredicate = getTemporalPredicate();
        this.interval = new Interval( temporalPredicate.getInterval().getLowerBound(), temporalPredicate.getInterval().getUpperBound() );
    }

    public TemporalPredicate getTemporalPredicate() {
        return ((TemporalConstraint) constraint).getTemporalPredicate();
    }

    @Override
    public boolean evaluate( InternalFactHandle handle, Tuple tuple ) {
        InternalFactHandle[] fhs = getBetaInvocationFactHandles( handle, tuple );
        long start1 = ( (EventFactHandle) fhs[0] ).getStartTimestamp();
        long duration1 = ( (EventFactHandle) fhs[0] ).getDuration();
        long start2 = ( (EventFactHandle) fhs[1] ).getStartTimestamp();
        long duration2 = ( (EventFactHandle) fhs[1] ).getDuration();
        return getTemporalPredicate().evaluate( start1, duration1, start2, duration2 );
    }

    @Override
    public boolean evaluate( InternalFactHandle handle, InternalWorkingMemory workingMemory ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isTemporal() {
        return true;
    }

    @Override
    public Interval getInterval() {
        return interval;
    }
}
