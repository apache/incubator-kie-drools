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
package org.drools.modelcompiler.constraints;

import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.Pattern;
import org.drools.base.time.Interval;
import org.drools.core.common.DefaultEventHandle;
import org.drools.model.SingleConstraint;
import org.drools.model.constraints.FixedTemporalConstraint;
import org.drools.model.constraints.TemporalConstraint;
import org.drools.model.functions.Function1;
import org.drools.model.functions.temporal.TemporalPredicate;
import org.kie.api.runtime.rule.FactHandle;

import static org.drools.base.util.TimeIntervalParser.getTimestampFromDate;

public class TemporalConstraintEvaluator extends ConstraintEvaluator {

    private final Interval interval;
    private Declaration patternDeclaration;

    public TemporalConstraintEvaluator( Declaration[] declarations, Pattern pattern, SingleConstraint constraint ) {
        super( declarations, pattern, constraint );
        TemporalPredicate temporalPredicate = ((TemporalConstraint) constraint).getTemporalPredicate();
        this.interval = new Interval( temporalPredicate.getInterval().getLowerBound(), temporalPredicate.getInterval().getUpperBound() );
    }

    @Override
    public boolean evaluate(FactHandle handle, BaseTuple tuple, ValueResolver reteEvaluator  ) {
        TemporalConstraint temporalConstraint = (TemporalConstraint) constraint;
        FactHandle[] fhs = getBetaInvocationFactHandles( handle, tuple );
        long start1 = getStartTimestamp( fhs[0], reteEvaluator, getDeclarations()[0], temporalConstraint.getF1() );
        long duration1 = getDuration( fhs[0] );
        long end1 = start1 + duration1;
        long start2 = getStartTimestamp( fhs[1], reteEvaluator, getDeclarations()[1], temporalConstraint.getF2() );
        long duration2 = getDuration( fhs[1] );
        long end2 = start2 + duration2;
        TemporalPredicate temporalPredicate = temporalConstraint.getTemporalPredicate();
        if (temporalPredicate.isThisOnRight()) {
            return temporalPredicate.evaluate( start2, duration2, end2, start1, duration1, end1);
        } else {
            return temporalPredicate.evaluate( start1, duration1, end1, start2, duration2, end2);
        }
    }

    private FactHandle[] getBetaInvocationFactHandles(FactHandle handle, BaseTuple tuple) {
        FactHandle[] fhs = new FactHandle[declarations.length];
        for (int i = 0; i < fhs.length; i++) {
            fhs[i] = declarations[i] == patternDeclaration ?
                    handle :
                    tuple.get(declarations[i].getTupleIndex());
        }
        return fhs;
    }

    private long getDuration( FactHandle fh ) {
        return fh instanceof DefaultEventHandle ? ( (DefaultEventHandle) fh).getDuration() : 0L;
    }

    private long getStartTimestamp( FactHandle fh, ValueResolver reteEvaluator, Declaration decl, Function1<Object, ?> f ) {
        if (f != null) {
            return getTimestampFromDate( f.apply( decl.getValue( reteEvaluator, fh.getObject() ) ) );
        }
        return fh instanceof DefaultEventHandle && !(decl.getExtractor() instanceof LambdaReadAccessor) ?
                ( (DefaultEventHandle) fh).getStartTimestamp() :
                getTimestampFromDate( decl.getValue( reteEvaluator, fh.getObject() ) );
    }

    @Override
    public boolean evaluate(FactHandle handle, ValueResolver reteEvaluator ) {
        TemporalConstraint temporalConstraint = (TemporalConstraint) constraint;
        long start1 = getStartTimestamp( handle, reteEvaluator, getDeclarations()[0], temporalConstraint.getF1() );
        long duration1 = getDuration( handle );
        long end1 = start1 + duration1;
        long start2 = getNonEventTimestamp(temporalConstraint, handle, reteEvaluator);
        long duration2 = 0;
        long end2 = start2 + duration2;
        TemporalPredicate temporalPredicate = temporalConstraint.getTemporalPredicate();
        if (temporalPredicate.isThisOnRight()) {
            return temporalPredicate.evaluate( start2, duration2, end2, start1, duration1, end1);
        } else {
            return temporalPredicate.evaluate( start1, duration1, end1, start2, duration2, end2);
        }
    }

    private long getNonEventTimestamp(TemporalConstraint temporalConstraint, FactHandle handle, ValueResolver reteEvaluator) {
        return constraint instanceof FixedTemporalConstraint ?
                (( FixedTemporalConstraint ) constraint).getValue() :
                getStartTimestamp( handle, reteEvaluator, getDeclarations()[1], temporalConstraint.getF2() );
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

    @Override
    protected void setPatternDeclaration( Declaration patternDeclaration ) {
        this.patternDeclaration = patternDeclaration;
    }
}
