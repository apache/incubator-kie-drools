/*
 * Copyright 2010 JBoss Inc
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

package org.drools.core.base.evaluators;

import org.drools.core.base.ValueType;
import org.drools.core.common.EventFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.spi.Evaluator;
import org.drools.core.time.Interval;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>The implementation of the 'after' evaluator definition.</p>
 * 
 * <p>The <b><code>after</code></b> evaluator correlates two events and matches when the temporal
 * distance from the current event to the event being correlated belongs to the distance range declared 
 * for the operator.</p> 
 * 
 * <p>Lets look at an example:</p>
 * 
 * <pre>$eventA : EventA( this after[ 3m30s, 4m ] $eventB )</pre>
 *
 * <p>The previous pattern will match if and only if the temporal distance between the 
 * time when $eventB finished and the time when $eventA started is between ( 3 minutes 
 * and 30 seconds ) and ( 4 minutes ). In other words:</p>
 * 
 * <pre> 3m30s <= $eventA.startTimestamp - $eventB.endTimeStamp <= 4m </pre>
 * 
 * <p>The temporal distance interval for the <b><code>after</code></b> operator is optional:</p>
 * 
 * <ul><li>If two values are defined (like in the example below), the interval starts on the
 * first value and finishes on the second.</li>
 * <li>If only one value is defined, the interval starts on the value and finishes on the positive 
 * infinity.</li>
 * <li>If no value is defined, it is assumed that the initial value is 1ms and the final value
 * is the positive infinity.</li></ul>
 * 
 * <p><b>NOTE:</b> it is allowed to define negative distances for this operator. Example:</p>
 * 
 * <pre>$eventA : EventA( this after[ -3m30s, -2m ] $eventB )</pre>
 *
 * <p><b>NOTE:</b> if the initial value is greater than the finish value, the engine automatically
 * reverse them, as there is no reason to have the initial value greater than the finish value. Example: 
 * the following two patterns are considered to have the same semantics:</p>
 * 
 * <pre>
 * $eventA : EventA( this after[ -3m30s, -2m ] $eventB )
 * $eventA : EventA( this after[ -2m, -3m30s ] $eventB )
 * </pre>
 */
public class AfterEvaluatorDefinition
    implements
    EvaluatorDefinition {

    protected static final String afterOp = "after";

    public static Operator AFTER;
    public static Operator NOT_AFTER;

    private static String[] SUPPORTED_IDS;

    private Map<String, AfterEvaluator> cache = Collections.emptyMap();

    static {
        if ( Operator.determineOperator( afterOp, false ) == null ) {
            AFTER = Operator.addOperatorToRegistry( afterOp, false );
            NOT_AFTER = Operator.addOperatorToRegistry( afterOp, true );
            SUPPORTED_IDS = new String[]{afterOp};
        }
    }

    @SuppressWarnings("unchecked")
    public void readExternal( ObjectInput in ) throws IOException,
                                                      ClassNotFoundException {
        cache = (Map<String, AfterEvaluator>) in.readObject();
    }

    public void writeExternal( ObjectOutput out ) throws IOException {
        out.writeObject( cache );
    }

    /**
     * @inheridDoc
     */
    public Evaluator getEvaluator( ValueType type,
                                   Operator operator ) {
        return this.getEvaluator( type,
                                  operator.getOperatorString(),
                                  operator.isNegated(),
                                  null,
                                  Target.HANDLE,
                                  Target.HANDLE );
    }

    /**
     * @inheridDoc
     */
    public Evaluator getEvaluator( ValueType type,
                                   Operator operator,
                                   String parameterText ) {
        return this.getEvaluator( type,
                                  operator.getOperatorString(),
                                  operator.isNegated(),
                                  parameterText,
                                  Target.HANDLE,
                                  Target.HANDLE );
    }

    /**
     * @inheritDoc
     */
    public Evaluator getEvaluator( final ValueType type,
                                   final String operatorId,
                                   final boolean isNegated,
                                   final String parameterText ) {
        return this.getEvaluator( type,
                                  operatorId,
                                  isNegated,
                                  parameterText,
                                  Target.HANDLE,
                                  Target.HANDLE );

    }

    /**
     * @inheritDoc
     */
    public Evaluator getEvaluator( final ValueType type,
                                   final String operatorId,
                                   final boolean isNegated,
                                   final String parameterText,
                                   final Target left,
                                   final Target right ) {
        if ( this.cache == Collections.EMPTY_MAP ) {
            this.cache = new HashMap<String, AfterEvaluator>();
        }
        String key = left + ":" + right + ":" + isNegated + ":" + parameterText;
        AfterEvaluator eval = this.cache.get( key );
        if ( eval == null ) {
            Long[] params = TimeIntervalParser.parse( parameterText );
            eval = new AfterEvaluator( type,
                                       isNegated,
                                       params,
                                       parameterText,
                                       left == Target.FACT,
                                       right == Target.FACT );
            this.cache.put( key,
                            eval );
        }
        return eval;
    }

    /**
     * @inheritDoc
     */
    public String[] getEvaluatorIds() {
        return SUPPORTED_IDS;
    }

    /**
     * @inheritDoc
     */
    public boolean isNegatable() {
        return true;
    }

    /**
     * @inheritDoc
     */
    public Target getTarget() {
        return Target.BOTH;
    }

    /**
     * @inheritDoc
     */
    public boolean supportsType( ValueType type ) {
        // supports all types, since it operates over fact handles
        // Note: should we change this interface to allow checking of event classes only?
        return true;
    }

    /**
     * Implements the 'after' evaluator itself
     */
    public static class AfterEvaluator extends PointInTimeEvaluator {
        private static final long serialVersionUID = 510l;

        public AfterEvaluator() {
        }

        public AfterEvaluator( final ValueType type,
                               final boolean isNegated,
                               final Long[] parameters,
                               final String paramText,
                               final boolean unwrapLeft,
                               final boolean unwrapRight ) {
            super( type,
                   isNegated ? NOT_AFTER : AFTER,
                   parameters,
                   paramText,
                   unwrapLeft,
                   unwrapRight );
        }

        @Override
        public Interval getInterval() {
            long init = this.initRange;
            long end = this.finalRange;
            if ( this.getOperator().isNegated() ) {
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
        protected boolean evaluate( long rightTS, long leftTS ) {
            long dist = rightTS - leftTS;
            return this.getOperator().isNegated() ^ ( dist >= this.initRange && dist <= this.finalRange );
        }

        @Override
        protected long getLeftTimestamp( InternalFactHandle handle ) {
            return ( (EventFactHandle) handle ).getEndTimestamp();
        }

        @Override
        protected long getRightTimestamp( InternalFactHandle handle ) {
            return ( (EventFactHandle) handle ).getStartTimestamp();
        }
    }
}