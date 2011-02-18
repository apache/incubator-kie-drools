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

package org.drools.base.evaluators;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.drools.RuntimeDroolsException;
import org.drools.base.BaseEvaluator;
import org.drools.base.ValueType;
import org.drools.common.EventFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.rule.VariableRestriction.LongVariableContextEntry;
import org.drools.rule.VariableRestriction.ObjectVariableContextEntry;
import org.drools.rule.VariableRestriction.VariableContextEntry;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldValue;
import org.drools.spi.InternalReadAccessor;
import org.drools.time.Interval;

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
 * <ul><li>If two values are defined (like in the example bellow), the interval starts on the
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

    public static final Operator        AFTER         = Operator.addOperatorToRegistry( "after",
                                                                                        false );
    public static final Operator        NOT_AFTER     = Operator.addOperatorToRegistry( "after",
                                                                                        true );

    private static final String[]       SUPPORTED_IDS = {AFTER.getOperatorString()};

    private Map<String, Evaluator>      cache         = Collections.emptyMap();
    private volatile TimeIntervalParser parser        = new TimeIntervalParser();

    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        cache = (Map<String, Evaluator>) in.readObject();
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
                                  null,
                                  Target.HANDLE,
                                  Target.HANDLE );
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
                                  parameterText,
                                  Target.HANDLE,
                                  Target.HANDLE );
    }

    /**
     * @inheritDoc
     */
    public Evaluator getEvaluator(final ValueType type,
                                  final String operatorId,
                                  final boolean isNegated,
                                  final String parameterText) {
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
    public Evaluator getEvaluator(final ValueType type,
                                  final String operatorId,
                                  final boolean isNegated,
                                  final String parameterText,
                                  final Target left,
                                  final Target right) {
        if ( this.cache == Collections.EMPTY_MAP ) {
            this.cache = new HashMap<String, Evaluator>();
        }
        String key = left+":"+right+":"+isNegated + ":" + parameterText;
        Evaluator eval = this.cache.get( key );
        if ( eval == null ) {
            Long[] params = parser.parse( parameterText );
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
    public boolean supportsType(ValueType type) {
        // supports all types, since it operates over fact handles
        // Note: should we change this interface to allow checking of event classes only?
        return true;
    }

    /**
     * Implements the 'after' evaluator itself
     */
    public static class AfterEvaluator extends BaseEvaluator {
        private static final long serialVersionUID = 510l;

        private long              initRange;
        private long              finalRange;
        private String            paramText;
        private boolean           unwrapLeft;
        private boolean           unwrapRight;

        public AfterEvaluator() {
        }

        public AfterEvaluator(final ValueType type,
                              final boolean isNegated,
                              final Long[] parameters,
                              final String paramText,
                              final boolean unwrapLeft,
                              final boolean unwrapRight) {
            super( type,
                   isNegated ? NOT_AFTER : AFTER );
            this.paramText = paramText;
            this.unwrapLeft = unwrapLeft;
            this.unwrapRight = unwrapRight;
            this.setParameters( parameters );
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            super.readExternal( in );
            initRange = in.readLong();
            finalRange = in.readLong();
            unwrapLeft = in.readBoolean();
            unwrapRight = in.readBoolean();
            paramText = (String) in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            super.writeExternal( out );
            out.writeLong( initRange );
            out.writeLong( finalRange );
            out.writeBoolean( unwrapLeft );
            out.writeBoolean( unwrapRight );
            out.writeObject( paramText );
        }

        @Override
        public Object prepareLeftObject(InternalFactHandle handle) {
            return unwrapLeft ? handle.getObject() : handle;
        }

        @Override
        public Object prepareRightObject(InternalFactHandle handle) {
            return unwrapRight ? handle.getObject() : handle;
        }

        @Override
        public boolean isTemporal() {
            return true;
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
            throw new RuntimeDroolsException( "The 'after' operator can only be used to compare one event to another, and never to compare to literal constraints." );
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context,
                                           final Object left) {
            if ( context.rightNull ) {
                return false;
            }
            long rightTS;
            if( this.unwrapRight ) {
                if( context instanceof ObjectVariableContextEntry ) {
                    if( ((ObjectVariableContextEntry) context).right instanceof Date ) {
                        rightTS = ((Date)((ObjectVariableContextEntry) context).right).getTime();
                    } else {
                        rightTS = ((Number)((ObjectVariableContextEntry) context).right).longValue();
                    }
                } else {
                    rightTS = ((LongVariableContextEntry) context).right;
                }
            } else {
                rightTS = ((EventFactHandle) ((ObjectVariableContextEntry) context).right).getStartTimestamp();
            }
            long leftTS = this.unwrapLeft ? context.declaration.getExtractor().getLongValue( workingMemory,
                                                                                             left ) : ((EventFactHandle) left).getEndTimestamp();

            long dist = rightTS - leftTS;
            return this.getOperator().isNegated() ^ (dist >= this.initRange && dist <= this.finalRange);
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context,
                                          final Object right) {
            if ( context.extractor.isNullValue( workingMemory,
                                                right ) ) {
                return false;
            }
            long rightTS = this.unwrapRight ? context.extractor.getLongValue( workingMemory,
                                                                              right ) : ((EventFactHandle) right).getStartTimestamp();

            long leftTS;
            if( this.unwrapLeft ) {
                if( context instanceof ObjectVariableContextEntry ) {
                    if( ((ObjectVariableContextEntry) context).left instanceof Date ) {
                        leftTS = ((Date)((ObjectVariableContextEntry) context).left).getTime();
                    } else {
                        leftTS = ((Number)((ObjectVariableContextEntry) context).left).longValue();
                    }
                } else {
                    leftTS = ((LongVariableContextEntry) context).left;
                }
            } else {
                leftTS = ((EventFactHandle) ((ObjectVariableContextEntry) context).left).getEndTimestamp();
            }
            long dist = rightTS - leftTS;

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
            long rightTS = this.unwrapRight ? 
                           extractor1.getLongValue( workingMemory, object1 ) : 
                           ((EventFactHandle) object1).getStartTimestamp();

            long leftTS = this.unwrapLeft ? 
                          extractor2.getLongValue( workingMemory, object2 ) :
                          ((EventFactHandle) object2).getEndTimestamp();

            long dist = rightTS - leftTS;
            return this.getOperator().isNegated() ^ (dist >= this.initRange && dist <= this.finalRange);
        }

        public String toString() {
            return this.getOperator().toString() + "[" + paramText + "]";
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + (int) (finalRange ^ (finalRange >>> 32));
            result = prime * result + (int) (initRange ^ (initRange >>> 32));
            result = prime * result + ((paramText == null) ? 0 : paramText.hashCode());
            result = prime * result + (unwrapLeft ? 1231 : 1237);
            result = prime * result + (unwrapRight ? 1231 : 1237);
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if ( this == obj ) return true;
            if ( !super.equals( obj ) ) return false;
            if ( getClass() != obj.getClass() ) return false;
            AfterEvaluator other = (AfterEvaluator) obj;
            if ( finalRange != other.finalRange ) return false;
            if ( initRange != other.initRange ) return false;
            if ( paramText == null ) {
                if ( other.paramText != null ) return false;
            } else if ( !paramText.equals( other.paramText ) ) return false;
            if ( unwrapLeft != other.unwrapLeft ) return false;
            if ( unwrapRight != other.unwrapRight ) return false;
            return true;
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
            } else if ( parameters.length == 1 ) {
                this.initRange = parameters[0].longValue();
                this.finalRange = Long.MAX_VALUE;
            } else if ( parameters.length == 2 ) {
                if ( parameters[0].longValue() <= parameters[1].longValue() ) {
                    this.initRange = parameters[0].longValue();
                    this.finalRange = parameters[1].longValue();
                } else {
                    this.initRange = parameters[1].longValue();
                    this.finalRange = parameters[0].longValue();
                }
            } else {
                throw new RuntimeDroolsException( "[After Evaluator]: Not possible to have more than 2 parameters: '" + paramText + "'" );
            }
        }

    }

}
