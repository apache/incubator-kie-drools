/**
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
 * <p>The implementation of the 'coincides' evaluator definition.</p>
 * 
 * <p>The <b><code>coincides</code></b> evaluator correlates two events and matches when both
 * happen at the same time. Optionally, the evaluator accept thresholds for the distance between
 * events' start and finish timestamps.</p> 
 * 
 * <p>Lets look at an example:</p>
 * 
 * <pre>$eventA : EventA( this coincides $eventB )</pre>
 *
 * <p>The previous pattern will match if and only if the start timestamps of both $eventA
 * and $eventB are the same AND the end timestamp of both $eventA and $eventB also are
 * the same.</p>
 * 
 * <p>Optionally, this operator accepts one or two parameters. These parameters are the thresholds
 * for the distance between matching timestamps. If only one paratemer is given, it is used for 
 * both start and end timestamps. If two parameters are given, then the first is used as a threshold
 * for the start timestamp and the second one is used as a threshold for the end timestamp. In other
 * words:</p>
 * 
 * <pre> $eventA : EventA( this coincides[15s, 10s] $eventB ) </pre> 
 * 
 * Above pattern will match if and only if:
 * 
 * <pre>
 * abs( $eventA.startTimestamp - $eventB.startTimestamp ) <= 15s &&
 * abs( $eventA.endTimestamp - $eventB.endTimestamp ) <= 10s 
 * </pre>
 * 
 * <p><b>NOTE:</b> it makes no sense to use negative interval values for the parameters and the 
 * engine will raise an error if that happens.</p>
 * 
 * @author etirelli
 * @author mgroch
 */
public class CoincidesEvaluatorDefinition
    implements
    EvaluatorDefinition {

    public static final Operator            COINCIDES     = Operator.addOperatorToRegistry( "coincides",
                                                                                            false );
    public static final Operator            COINCIDES_NOT = Operator.addOperatorToRegistry( "coincides",
                                                                                            true );

    private static final String[]           SUPPORTED_IDS = {COINCIDES.getOperatorString()};

    private Map<String, CoincidesEvaluator> cache         = Collections.emptyMap();
    private volatile TimeIntervalParser     parser        = new TimeIntervalParser();

    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        cache = (Map<String, CoincidesEvaluator>) in.readObject();
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
            this.cache = new HashMap<String, CoincidesEvaluator>();
        }
        String key = left + ":" + right + ":" + isNegated + ":" + parameterText;
        CoincidesEvaluator eval = this.cache.get( key );
        if ( eval == null ) {
            Long[] params = parser.parse( parameterText );
            eval = new CoincidesEvaluator( type,
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
     * Implements the 'coincides' evaluator itself
     */
    public static class CoincidesEvaluator extends BaseEvaluator {
        private static final long serialVersionUID = 510l;

        private long              startDev;
        private long              endDev;
        private String            paramText;
        private boolean           unwrapLeft;
        private boolean           unwrapRight;

        public CoincidesEvaluator() {
        }

        public CoincidesEvaluator(final ValueType type,
                                  final boolean isNegated,
                                  final Long[] parameters,
                                  final String paramText,
                                  final boolean unwrapLeft,
                                  final boolean unwrapRight) {
            super( type,
                   isNegated ? COINCIDES_NOT : COINCIDES );
            this.paramText = paramText;
            this.unwrapLeft = unwrapLeft;
            this.unwrapRight = unwrapRight;
            this.setParameters( parameters );
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            super.readExternal( in );
            startDev = in.readLong();
            endDev = in.readLong();
            unwrapLeft = in.readBoolean();
            unwrapRight = in.readBoolean();
            paramText = (String) in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            super.writeExternal( out );
            out.writeLong( startDev );
            out.writeLong( endDev );
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
            if ( this.getOperator().isNegated() ) {
                return new Interval( Interval.MIN,
                                     Interval.MAX );
            }
            return new Interval( 0,
                                 0 );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1,
                                final FieldValue object2) {
            throw new RuntimeDroolsException( "The 'coincides' operator can only be used to compare one event to another, and never to compare to literal constraints." );
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context,
                                           final Object left) {
            if ( context.rightNull ) {
                return false;
            }
            long rightStartTS, rightEndTS;
            long leftStartTS, leftEndTS;
            if ( this.unwrapRight ) {
                if ( context instanceof ObjectVariableContextEntry ) {
                    if ( ((ObjectVariableContextEntry) context).right instanceof Date ) {
                        rightStartTS = ((Date) ((ObjectVariableContextEntry) context).right).getTime();
                    } else {
                        rightStartTS = ((Number) ((ObjectVariableContextEntry) context).right).longValue();
                    }
                } else {
                    rightStartTS = ((LongVariableContextEntry) context).right;
                }
                rightEndTS = rightStartTS;
            } else {
                rightStartTS = ((EventFactHandle) ((ObjectVariableContextEntry) context).right).getStartTimestamp();
                rightEndTS = ((EventFactHandle) ((ObjectVariableContextEntry) context).right).getEndTimestamp();
            }
            leftStartTS = this.unwrapLeft ? context.declaration.getExtractor().getLongValue( workingMemory,
                                                                                             left ) : ((EventFactHandle) left).getStartTimestamp();
            leftEndTS = this.unwrapLeft ? rightStartTS : ((EventFactHandle) left).getEndTimestamp();

            long distStart = Math.abs( rightStartTS - leftStartTS );
            long distEnd = Math.abs( rightEndTS - leftEndTS );
            return this.getOperator().isNegated() ^ (distStart <= this.startDev && distEnd <= this.endDev);
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context,
                                          final Object right) {
            if ( context.extractor.isNullValue( workingMemory,
                                                right ) ) {
                return false;
            }

            long rightStartTS, rightEndTS;
            long leftStartTS, leftEndTS;

            rightStartTS = this.unwrapRight ? context.extractor.getLongValue( workingMemory,
                                                                              right ) : ((EventFactHandle) right).getStartTimestamp();
            rightEndTS = this.unwrapRight ? rightStartTS : ((EventFactHandle) right).getEndTimestamp();

            if ( this.unwrapLeft ) {
                if ( context instanceof ObjectVariableContextEntry ) {
                    if ( ((ObjectVariableContextEntry) context).left instanceof Date ) {
                        leftStartTS = ((Date) ((ObjectVariableContextEntry) context).left).getTime();
                    } else {
                        leftStartTS = ((Number) ((ObjectVariableContextEntry) context).left).longValue();
                    }
                } else {
                    leftStartTS = ((LongVariableContextEntry) context).left;
                }
                leftEndTS = leftStartTS;
            } else {
                leftStartTS = ((EventFactHandle) ((ObjectVariableContextEntry) context).left).getStartTimestamp();
                leftEndTS = ((EventFactHandle) ((ObjectVariableContextEntry) context).left).getEndTimestamp();
            }

            long distStart = Math.abs( rightStartTS - leftStartTS );
            long distEnd = Math.abs( rightEndTS - leftEndTS );
            return this.getOperator().isNegated() ^ (distStart <= this.startDev && distEnd <= this.endDev);
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
            long rightStartTS, rightEndTS;
            long leftStartTS, leftEndTS;

            rightStartTS = this.unwrapRight ? extractor1.getLongValue( workingMemory,
                                                                       object1 ) : ((EventFactHandle) object1).getStartTimestamp();
            rightEndTS = this.unwrapRight ? rightStartTS : ((EventFactHandle) object1).getEndTimestamp();

            leftStartTS = this.unwrapLeft ? extractor2.getLongValue( workingMemory,
                                                                     object2 ) : ((EventFactHandle) object2).getStartTimestamp();
            leftEndTS = this.unwrapLeft ? leftStartTS : ((EventFactHandle) object2).getEndTimestamp();

            long distStart = Math.abs( rightStartTS - leftStartTS );
            long distEnd = Math.abs( rightEndTS - leftEndTS );
            return this.getOperator().isNegated() ^ (distStart <= this.startDev && distEnd <= this.endDev);
        }

        public String toString() {
            return "coincides[" + startDev + ", " + endDev + "]";
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int PRIME = 31;
            int result = super.hashCode();
            result = PRIME * result + (int) (endDev ^ (endDev >>> 32));
            result = PRIME * result + (int) (startDev ^ (startDev >>> 32));
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
            final CoincidesEvaluator other = (CoincidesEvaluator) obj;
            return endDev == other.endDev && startDev == other.startDev;
        }

        /**
         * This methods sets the parameters appropriately.
         *
         * @param parameters
         */
        private void setParameters(Long[] parameters) {
            if ( parameters == null || parameters.length == 0 ) {
                // open bounded range
                this.startDev = 0;
                this.endDev = 0;
                return;
            } else {
                for ( Long param : parameters ) {
                    if ( param.longValue() < 0 ) {
                        throw new RuntimeDroolsException( "[Coincides Evaluator]: negative values not allowed for temporal distance thresholds: '" + paramText + "'" );
                    }
                }
                if ( parameters.length == 1 ) {
                    // same deviation for both
                    this.startDev = parameters[0].longValue();
                    this.endDev = parameters[0].longValue();
                } else if ( parameters.length == 2 ) {
                    // different deviation 
                    this.startDev = parameters[0].longValue();
                    this.endDev = parameters[1].longValue();
                } else {
                    throw new RuntimeDroolsException( "[Coincides Evaluator]: Not possible to have more than 2 parameters: '" + paramText + "'" );
                }
            }
        }

    }

}
