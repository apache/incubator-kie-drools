/*
 * Copyright 2007 JBoss Inc
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
 *
 * Created on Dec 6, 2007
 */
package org.drools.base.evaluators;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.drools.RuntimeDroolsException;
import org.drools.base.BaseEvaluator;
import org.drools.base.ValueType;
import org.drools.common.EventFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.rule.VariableRestriction.ObjectVariableContextEntry;
import org.drools.rule.VariableRestriction.VariableContextEntry;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldValue;
import org.drools.spi.InternalReadAccessor;
import org.drools.time.Interval;

/**
 * <p>The implementation of the 'before' evaluator definition.</p>
 * 
 * <p>The <b><code>before</code></b> evaluator correlates two events and matches when the temporal 
 * distance from the event being correlated to the current correlated belongs to the distance range declared 
 * for the operator.</p> 
 * 
 * <p>Lets look at an example:</p>
 * 
 * <pre>$eventA : EventA( this before[ 3m30s, 4m ] $eventB )</pre>
 *
 * <p>The previous pattern will match if and only if the temporal distance between the 
 * time when $eventA finished and the time when $eventB started is between ( 3 minutes 
 * and 30 seconds ) and ( 4 minutes ). In other words:</p>
 * 
 * <pre> 3m30s <= $eventB.startTimestamp - $eventA.endTimeStamp <= 4m </pre>
 * 
 * <p>The temporal distance interval for the <b><code>before</code></b> operator is optional:</p>
 * 
 * <ul><li>If two values are defined (like in the example bellow), the interval starts on the
 * first value and finishes on the second.</li>
 * <li>If only one value is defined, we have two cases. If the value is negative, then the interval
 * starts on the value and finishes on -1ms. If the value is positive, then the interval starts on 
 * +1ms and finishes on the value.</li>
 * <li>If no value is defined, it is assumed that the initial value is 1ms and the final value
 * is the positive infinity.</li></ul>
 * 
 * <p><b>NOTE:</b> it is allowed to define negative distances for this operator. Example:</p>
 * 
 * <pre>$eventA : EventA( this before[ -3m30s, -2m ] $eventB )</pre>
 *
 * <p><b>NOTE:</b> if the initial value is greater than the finish value, the engine automatically
 * reverse them, as there is no reason to have the initial value greater than the finish value. Example: 
 * the following two patterns are considered to have the same semantics:</p>
 * 
 * <pre>
 * $eventA : EventA( this before[ -3m30s, -2m ] $eventB )
 * $eventA : EventA( this before[ -2m, -3m30s ] $eventB )
 * </pre>
 * 
 *
 * @author etirelli
 * @author mgroch
 */
public class BeforeEvaluatorDefinition
    implements
    EvaluatorDefinition {

    public static final Operator         BEFORE        = Operator.addOperatorToRegistry( "before",
                                                                                         false );
    public static final Operator         NOT_BEFORE    = Operator.addOperatorToRegistry( "before",
                                                                                         true );

    private static final String[]        SUPPORTED_IDS = {BEFORE.getOperatorString()};

    private Map<String, BeforeEvaluator> cache         = Collections.emptyMap();
    private volatile TimeIntervalParser  parser        = new TimeIntervalParser();

    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        cache = (Map<String, BeforeEvaluator>) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( cache );
    }

    /**
     * @inheridDoc
     */
    public Evaluator getEvaluator(ValueType type,
                                  Operator operator) {
        return this.getEvaluator( type,
                                  operator.getOperatorString(),
                                  operator.isNegated(),
                                  null );
    }

    /**
     * @inheridDoc
     */
    public Evaluator getEvaluator(ValueType type,
                                  Operator operator,
                                  String parameterText) {
        return this.getEvaluator( type,
                                  operator.getOperatorString(),
                                  operator.isNegated(),
                                  parameterText );
    }

    /**
     * @inheritDoc
     */
    public Evaluator getEvaluator(final ValueType type,
                                  final String operatorId,
                                  final boolean isNegated,
                                  final String parameterText) {
        if ( this.cache == Collections.EMPTY_MAP ) {
            this.cache = new HashMap<String, BeforeEvaluator>();
        }
        String key = isNegated + ":" + parameterText;
        BeforeEvaluator eval = this.cache.get( key );
        if ( eval == null ) {
            Long[] params = parser.parse( parameterText );
            eval = new BeforeEvaluator( type,
                                        isNegated,
                                        params,
                                        parameterText );
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
    public boolean operatesOnFactHandles() {
        return true;
    }

    /**
     * @inheritDoc
     */
    public boolean supportsType(ValueType type) {
        // supports all types, since it operates over fact handles
        // Note: should we change this interface to allow checking of event classes only?
        return true;
    }

    /**
     * Implements the 'before' evaluator itself
     */
    public static class BeforeEvaluator extends BaseEvaluator {
        private static final long serialVersionUID = -4778826341073034320L;

        private long              initRange;
        private long              finalRange;
        private String            paramText;

        public BeforeEvaluator() {
        }

        public BeforeEvaluator(final ValueType type,
                               final boolean isNegated,
                               final Long[] parameters,
                               final String paramText) {
            super( type,
                   isNegated ? NOT_BEFORE : BEFORE );
            this.paramText = paramText;
            this.setParameters( parameters );
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            super.readExternal( in );
            initRange = in.readLong();
            finalRange = in.readLong();
            paramText = (String) in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            super.writeExternal( out );
            out.writeLong( initRange );
            out.writeLong( finalRange );
            out.writeObject( paramText );
        }

        @Override
        public Object prepareObject(InternalFactHandle handle) {
            return handle;
        }

        @Override
        public boolean isTemporal() {
            return true;
        }

        @Override
        public Interval getInterval() {
            long init = (this.finalRange == Interval.MAX) ? Interval.MIN : -this.finalRange;
            long end = (this.initRange == Interval.MIN) ? Interval.MAX : -this.initRange;
            if ( this.getOperator().isNegated() ) {
                if ( init == Interval.MIN && end != Interval.MAX ) {
                    init = finalRange + 1;
                    end = Interval.MAX;
                } else if ( init != Interval.MIN && end == Interval.MAX ) {
                    init = Interval.MIN;
                    end = initRange - 1;
                } else if ( init == Interval.MIN && end == Interval.MAX ) {
                    init = 0;
                    end = -1;
                } else {
                    init = Interval.MIN;
                    end = Interval.MAX;
                }
            }
            return new Interval( init,
                                 end );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1,
                                final FieldValue object2) {
            throw new RuntimeDroolsException( "The 'before' operator can only be used to compare one event to another, and never to compare to literal constraints." );
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context,
                                           final Object left) {
            if ( context.rightNull ) {
                return false;
            }
            long dist = ((EventFactHandle) left).getStartTimestamp() - ((EventFactHandle) ((ObjectVariableContextEntry) context).right).getEndTimestamp();
            return this.getOperator().isNegated() ^ (dist >= this.initRange && dist <= this.finalRange);
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context,
                                          final Object right) {
            if ( context.extractor.isNullValue( workingMemory,
                                                right ) ) {
                return false;
            }
            long dist = ((EventFactHandle) ((ObjectVariableContextEntry) context).left).getStartTimestamp() - ((EventFactHandle) right).getEndTimestamp();

            return this.getOperator().isNegated() ^ (dist >= this.initRange && dist <= this.finalRange);
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2,
                                final Object object2) {
            if ( extractor1.isNullValue( workingMemory,
                                         object1 ) ) {
                return false;
            }
            long dist = ((EventFactHandle) object2).getStartTimestamp() - ((EventFactHandle) object1).getEndTimestamp();
            return this.getOperator().isNegated() ^ (dist >= this.initRange && dist <= this.finalRange);
        }

        public String toString() {
            return this.getOperator().toString() + "[" + paramText + "]";
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int PRIME = 31;
            int result = super.hashCode();
            result = PRIME * result + (int) (finalRange ^ (finalRange >>> 32));
            result = PRIME * result + (int) (initRange ^ (initRange >>> 32));
            return result;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if ( this == obj ) return true;
            if ( !super.equals( obj ) ) return false;
            if ( getClass() != obj.getClass() ) return false;
            final BeforeEvaluator other = (BeforeEvaluator) obj;
            return finalRange == other.finalRange && initRange == other.initRange;
        }

        /**
         * This methods sets the parameters appropriately.
         *
         * @param parameters
         */
        private void setParameters(Long[] parameters) {
            if ( parameters == null || parameters.length == 0 ) {
                // open bounded range
                this.initRange = 1;
                this.finalRange = Long.MAX_VALUE;
                return;
            } else if ( parameters.length == 1 ) {
                if ( parameters[0].longValue() >= 0 ) {
                    // up to that value
                    this.initRange = 1;
                    this.finalRange = parameters[0].longValue();
                } else {
                    // from that value up to now
                    this.initRange = parameters[0].longValue();
                    this.finalRange = -1;
                }
            } else if ( parameters.length == 2 ) {
                if ( parameters[0].longValue() <= parameters[1].longValue() ) {
                    this.initRange = parameters[0].longValue();
                    this.finalRange = parameters[1].longValue();
                } else {
                    this.initRange = parameters[1].longValue();
                    this.finalRange = parameters[0].longValue();
                }
            } else {
                throw new RuntimeDroolsException( "[Before Evaluator]: Not possible to have more than 2 parameters: '" + paramText + "'" );
            }
        }

    }

}
