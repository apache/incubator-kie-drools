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
import org.drools.model.Global;
import org.drools.model.SingleConstraint;
import org.drools.model.constraints.FixedTemporalConstraint;
import org.drools.model.constraints.TemporalConstraint;
import org.drools.model.functions.Function1;
import org.drools.model.functions.temporal.TemporalPredicate;

import static org.drools.core.base.evaluators.PointInTimeEvaluator.getTimestampFromDate;

public class TemporalConstraintEvaluator extends ConstraintEvaluator {

    private final Interval interval;

    public TemporalConstraintEvaluator( Declaration[] declarations, Pattern pattern, SingleConstraint constraint ) {
        super( declarations, pattern, constraint );
        TemporalPredicate temporalPredicate = ((TemporalConstraint) constraint).getTemporalPredicate();
        this.interval = new Interval( temporalPredicate.getInterval().getLowerBound(), temporalPredicate.getInterval().getUpperBound() );
    }

    @Override
    public boolean evaluate( InternalFactHandle handle, Tuple tuple, InternalWorkingMemory workingMemory  ) {
        TemporalConstraint temporalConstraint = (TemporalConstraint) constraint;
        InternalFactHandle[] fhs = getBetaInvocationFactHandles( handle, tuple );
        long start1 = getStartTimestamp( fhs[0], temporalConstraint.getF1() );
        long duration1 = ( (EventFactHandle) fhs[0] ).getDuration();
        long end1 = start1 + duration1;
        long start2 = getStartTimestamp( fhs[1], temporalConstraint.getF2() );
        long duration2 = ( (EventFactHandle) fhs[1] ).getDuration();
        long end2 = start2 + duration2;
        return temporalConstraint.getTemporalPredicate().evaluate( start1, duration1, end1, start2, duration2, end2);
    }

    private long getStartTimestamp( InternalFactHandle fh, Function1<Object, ?> f ) {
        return f != null ? getTimestampFromDate( f.apply( fh.getObject() ) ) : ( (EventFactHandle ) fh).getStartTimestamp();
    }

    @Override
    public boolean evaluate( InternalFactHandle handle, InternalWorkingMemory workingMemory ) {
        TemporalConstraint temporalConstraint = (TemporalConstraint) constraint;
        long start1 = getStartTimestamp( handle, temporalConstraint.getF1() );
        long duration1 = ( (EventFactHandle) handle ).getDuration();
        long end1 = start1 + duration1;
        long start2 = getNonEventTimestamp(temporalConstraint, workingMemory);
        long duration2 = 0;
        long end2 = start2 + duration2;
        return temporalConstraint.getTemporalPredicate().evaluate( start1, duration1, end1, start2, duration2, end2);
    }

    private long getNonEventTimestamp(TemporalConstraint temporalConstraint, InternalWorkingMemory workingMemory) {
        if (temporalConstraint.getVariables().length == 2 && temporalConstraint.getVariables()[1] instanceof Global) {
            Object value = workingMemory.getGlobal( (( Global ) temporalConstraint.getVariables()[1]).getName() );
            return getTimestampFromDate( temporalConstraint.getF2() != null ? temporalConstraint.getF2().apply( value ) : value );
        }
        return (( FixedTemporalConstraint ) constraint).getValue();
    }

    @Override
    public TemporalConstraintEvaluator clone() {
        return new TemporalConstraintEvaluator( getDeclarations(), getPattern(), constraint );
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
