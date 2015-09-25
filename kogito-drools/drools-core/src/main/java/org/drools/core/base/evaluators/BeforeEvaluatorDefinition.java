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

import org.drools.core.base.BaseEvaluator;
import org.drools.core.base.ValueType;
import org.drools.core.common.EventFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.rule.VariableRestriction.LeftStartRightEndContextEntry;
import org.drools.core.rule.VariableRestriction.VariableContextEntry;
import org.drools.core.spi.Evaluator;
import org.drools.core.spi.FieldValue;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.time.Interval;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
 * <ul><li>If two values are defined (like in the example below), the interval starts on the
 * first value and finishes on the second.</li>
 * <li>If only one value is defined, then the interval starts on the value and finishes on 
 * the positive infinity.</li>
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
 */
public class BeforeEvaluatorDefinition
    implements
    EvaluatorDefinition {

    public static final String          beforeOp = "before";

    public static Operator              BEFORE;

    public static Operator              NOT_BEFORE;

    private static String[]             SUPPORTED_IDS;

    private Map<String, BeforeEvaluator> cache         = Collections.emptyMap();

    { init(); }

    static void init() {
        if ( Operator.determineOperator( beforeOp, false ) == null ) {
            BEFORE = Operator.addOperatorToRegistry( beforeOp, false );
            NOT_BEFORE = Operator.addOperatorToRegistry( beforeOp, true );
            SUPPORTED_IDS = new String[] { beforeOp };
        }
    }

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
            this.cache = new HashMap<String, BeforeEvaluator>();
        }
        String key = left+":"+right+":"+isNegated + ":" + parameterText;
        BeforeEvaluator eval = this.cache.get( key );
        if ( eval == null ) {
            Long[] params = TimeIntervalParser.parse( parameterText );
            eval = new BeforeEvaluator( type,
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
     * Implements the 'before' evaluator itself
     */
    public static class BeforeEvaluator extends BaseEvaluator {
        private static final long serialVersionUID = 510l;

        private long              initRange;
        private long              finalRange;
        private String            paramText;
        private boolean           unwrapLeft;
        private boolean           unwrapRight;

        {
            BeforeEvaluatorDefinition.init();
        }

        public BeforeEvaluator() {
        }

        public BeforeEvaluator(final ValueType type,
                               final boolean isNegated,
                               final Long[] parameters,
                               final String paramText,
                               final boolean unwrapLeft,
                               final boolean unwrapRight) {
            super( type,
                   isNegated ? NOT_BEFORE : BEFORE );
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
            paramText = (String) in.readObject();
            unwrapLeft = in.readBoolean();
            unwrapRight = in.readBoolean();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            super.writeExternal( out );
            out.writeLong( initRange );
            out.writeLong( finalRange );
            out.writeObject( paramText );
            out.writeBoolean( unwrapLeft );
            out.writeBoolean( unwrapRight );
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
                                final InternalFactHandle object1,
                                final FieldValue object2) {
            long rightTS;
            if ( extractor.isSelfReference() ) {
                rightTS = ((EventFactHandle) object1).getStartTimestamp();
            } else {
                rightTS = extractor.getLongValue( workingMemory, object1.getObject() );
            }

            long leftTS = ((Date)object2.getValue()).getTime();

            return evaluate(rightTS, leftTS);
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context,
                                           final InternalFactHandle left) {
            if ( context.rightNull || 
                    context.declaration.getExtractor().isNullValue( workingMemory, left.getObject() )) {
                return false;
            }
            
            long rightTS = ((LeftStartRightEndContextEntry)context).timestamp;
            long leftTS;
            if ( context.declaration.getExtractor().isSelfReference() ) {
                leftTS = ((EventFactHandle) left).getStartTimestamp();
            } else {
                leftTS = context.declaration.getExtractor().getLongValue( workingMemory, left.getObject() );
            }

            return evaluate(rightTS, leftTS);
        }

        private boolean evaluate(long rightTS, long leftTS) {
            long dist = leftTS - rightTS;
            return this.getOperator().isNegated() ^ (dist >= this.initRange && dist <= this.finalRange);
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context,
                                          final InternalFactHandle right) {
            if ( context.leftNull ||
                    context.extractor.isNullValue( workingMemory, right.getObject() ) ) {
                return false;
            }

            long leftTS = ((LeftStartRightEndContextEntry)context).timestamp;
            long rightTS;
            if ( context.getFieldExtractor().isSelfReference() ) {
                rightTS = ((EventFactHandle) right).getEndTimestamp();
            } else {
                rightTS = context.getFieldExtractor().getLongValue( workingMemory, right.getObject() );
            }
            return evaluate(rightTS, leftTS);
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final InternalFactHandle handle1,
                                final InternalReadAccessor extractor2,
                                final InternalFactHandle handle2) {
            if ( extractor1.isNullValue( workingMemory, handle1.getObject() ) || 
                    extractor2.isNullValue( workingMemory, handle2.getObject() ) ) {
                return false;
            }
            
            long rightTS;
            if ( extractor1.isSelfReference() ) {
                rightTS = ((EventFactHandle) handle1).getEndTimestamp();
            } else {
                rightTS = extractor1.getLongValue( workingMemory, handle1.getObject() );
            }
            
            long leftTS;
            if ( extractor2.isSelfReference() ) {
                leftTS = ((EventFactHandle) handle2).getStartTimestamp();
            } else {
                leftTS = extractor2.getLongValue( workingMemory, handle2.getObject() );
            }

            return evaluate(rightTS, leftTS);
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
            result = PRIME * result + ((paramText == null) ? 0 : paramText.hashCode());
            result = PRIME * result + (unwrapLeft ? 1231 : 1237);
            result = PRIME * result + (unwrapRight ? 1231 : 1237);
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
            BeforeEvaluator other = (BeforeEvaluator) obj;
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
                return;
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
                throw new RuntimeException( "[Before Evaluator]: Not possible to have more than 2 parameters: '" + paramText + "'" );
            }
        }

    }

}
