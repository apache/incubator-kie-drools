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
        long start1 = getStartTimestamp( fhs[0], workingMemory, getDeclarations()[0], temporalConstraint.getF1() );
        long duration1 = getDuration( fhs[0] );
        long end1 = start1 + duration1;
        long start2 = getStartTimestamp( fhs[1], workingMemory, getDeclarations()[1], temporalConstraint.getF2() );
        long duration2 = getDuration( fhs[1] );
        long end2 = start2 + duration2;
        return temporalConstraint.getTemporalPredicate().evaluate( start1, duration1, end1, start2, duration2, end2);
    }

    private long getDuration( InternalFactHandle fh ) {
        return fh instanceof EventFactHandle ? ( (EventFactHandle ) fh).getDuration() : 0L;
    }

    private long getStartTimestamp( InternalFactHandle fh, InternalWorkingMemory workingMemory, Declaration decl, Function1<Object, ?> f ) {
        if (f != null) {
            return getTimestampFromDate( f.apply( decl.getValue( workingMemory, fh.getObject() ) ) );
        }
        return fh instanceof EventFactHandle && !(decl.getExtractor() instanceof LambdaReadAccessor) ?
                ( (EventFactHandle ) fh).getStartTimestamp() :
                getTimestampFromDate( decl.getValue( workingMemory, fh.getObject() ) );
    }

    @Override
    public boolean evaluate( InternalFactHandle handle, InternalWorkingMemory workingMemory ) {
        TemporalConstraint temporalConstraint = (TemporalConstraint) constraint;
        long start1 = getStartTimestamp( handle, workingMemory, getDeclarations()[0], temporalConstraint.getF1() );
        long duration1 = getDuration( handle );
        long end1 = start1 + duration1;
        long start2 = getNonEventTimestamp(temporalConstraint, handle, workingMemory);
        long duration2 = 0;
        long end2 = start2 + duration2;
        return temporalConstraint.getTemporalPredicate().evaluate( start1, duration1, end1, start2, duration2, end2);
    }

    private long getNonEventTimestamp(TemporalConstraint temporalConstraint, InternalFactHandle handle, InternalWorkingMemory workingMemory) {
        return constraint instanceof FixedTemporalConstraint ?
                (( FixedTemporalConstraint ) constraint).getValue() :
                getStartTimestamp( handle, workingMemory, getDeclarations()[1], temporalConstraint.getF2() );
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
