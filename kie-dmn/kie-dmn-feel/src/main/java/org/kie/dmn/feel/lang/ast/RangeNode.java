/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.feel.lang.ast;

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.UnaryTest;
import org.kie.dmn.feel.runtime.impl.RangeImpl;
import org.kie.dmn.feel.util.Msg;

public class RangeNode
        extends BaseNode {

    public static enum IntervalBoundary {
        OPEN, CLOSED;
    }

    private IntervalBoundary lowerBound;
    private IntervalBoundary upperBound;
    private BaseNode         start;
    private BaseNode         end;

    public RangeNode(ParserRuleContext ctx, IntervalBoundary lowerBound, BaseNode start, BaseNode end, IntervalBoundary upperBound) {
        super( ctx );
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.start = start;
        this.end = end;
    }

    public IntervalBoundary getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(IntervalBoundary lowerBound) {
        this.lowerBound = lowerBound;
    }

    public IntervalBoundary getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(IntervalBoundary upperBound) {
        this.upperBound = upperBound;
    }

    public BaseNode getStart() {
        return start;
    }

    public void setStart(BaseNode start) {
        this.start = start;
    }

    public BaseNode getEnd() {
        return end;
    }

    public void setEnd(BaseNode end) {
        this.end = end;
    }

    @Override
    public Range evaluate(EvaluationContext ctx) {
        Comparable s = (Comparable) start.evaluate( ctx );
        Comparable e = (Comparable) end.evaluate( ctx );
        
        boolean problem = false;
        if ( s == null ) { ctx.notifyEvt( astEvent(Severity.ERROR, Msg.createMessage(Msg.IS_NULL, "Start"))); problem = true; }
        if ( e == null ) { ctx.notifyEvt( astEvent(Severity.ERROR, Msg.createMessage(Msg.IS_NULL, "End"))); problem = true; }
        if (problem) { return null; }
        
        if ( BuiltInType.determineTypeFromInstance( s ) != BuiltInType.determineTypeFromInstance( e ) 
                && !s.getClass().isAssignableFrom( e.getClass() ) ) {
            ctx.notifyEvt( astEvent(Severity.ERROR, Msg.createMessage(Msg.X_TYPE_INCOMPATIBLE_WITH_Y_TYPE, "Start", "End")));
            return null;
        }

        return new RangeImpl( lowerBound==IntervalBoundary.OPEN ? Range.RangeBoundary.OPEN : Range.RangeBoundary.CLOSED,
                              s,
                              e,
                              upperBound==IntervalBoundary.OPEN ? Range.RangeBoundary.OPEN : Range.RangeBoundary.CLOSED );
    }
}
