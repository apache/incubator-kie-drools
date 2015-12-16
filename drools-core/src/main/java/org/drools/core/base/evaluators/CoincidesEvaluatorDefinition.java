/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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
import org.drools.core.rule.VariableRestriction.TemporalVariableContextEntry;
import org.drools.core.rule.VariableRestriction.VariableContextEntry;
import org.drools.core.spi.Evaluator;
import org.drools.core.spi.FieldValue;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.time.Interval;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
 */
public class CoincidesEvaluatorDefinition
    implements
    EvaluatorDefinition {

    protected static final String   coincidesOp = "coincides";

    public static Operator          COINCIDES;
    public static Operator          COINCIDES_NOT;

    private static String[]         SUPPORTED_IDS;

    private Map<String, CoincidesEvaluator> cache     = Collections.emptyMap();

    { init(); }

    static void init() {
        if ( Operator.determineOperator( coincidesOp, false ) == null ) {
            COINCIDES = Operator.addOperatorToRegistry( coincidesOp, false );
            COINCIDES_NOT = Operator.addOperatorToRegistry( coincidesOp, true );
            SUPPORTED_IDS = new String[] { coincidesOp };
        }
    }

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
            long[] params = TimeIntervalParser.parse( parameterText );
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

        {
            CoincidesEvaluatorDefinition.init();
        }

        public CoincidesEvaluator() {
        }

        public CoincidesEvaluator(final ValueType type,
                                  final boolean isNegated,
                                  final long[] parameters,
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
                                final InternalFactHandle object1,
                                final FieldValue object2) {
            throw new RuntimeException( "The 'coincides' operator can only be used to compare one event to another, and never to compare to literal constraints." );
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context,
                                           final InternalFactHandle left) {
            if ( context.rightNull || 
                    context.declaration.getExtractor().isNullValue( workingMemory, left.getObject() )) {
                return false;
            }
            
            long rightStartTS, rightEndTS;
            long leftStartTS, leftEndTS;
            
            rightStartTS = ((TemporalVariableContextEntry) context).startTS;
            rightEndTS = ((TemporalVariableContextEntry) context).endTS;
            
            if ( context.declaration.getExtractor().isSelfReference() ) {
                leftStartTS = ((EventFactHandle) left).getStartTimestamp();
                leftEndTS = ((EventFactHandle) left).getEndTimestamp();
            } else {
                leftStartTS = context.declaration.getExtractor().getLongValue( workingMemory, left.getObject() );
                leftEndTS = leftStartTS;
            }

            long distStart = Math.abs( rightStartTS - leftStartTS );
            long distEnd = Math.abs( rightEndTS - leftEndTS );
            return this.getOperator().isNegated() ^ (distStart <= this.startDev && distEnd <= this.endDev);
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context,
                                          final InternalFactHandle right) {
            if ( context.leftNull ||
                    context.extractor.isNullValue( workingMemory, right.getObject() ) ) {
                return false;
            }

            long rightStartTS, rightEndTS;
            long leftStartTS, leftEndTS;

            if ( context.extractor.isSelfReference() ) {
                rightStartTS = ((EventFactHandle) right).getStartTimestamp();
                rightEndTS = ((EventFactHandle) right).getEndTimestamp();
            } else {
                rightStartTS = context.extractor.getLongValue( workingMemory, right.getObject() );
                rightEndTS = rightStartTS;
            }                        
            
            leftStartTS = ((TemporalVariableContextEntry) context).startTS;
            leftEndTS = ((TemporalVariableContextEntry) context).endTS;

            long distStart = Math.abs( rightStartTS - leftStartTS );
            long distEnd = Math.abs( rightEndTS - leftEndTS );
            return this.getOperator().isNegated() ^ (distStart <= this.startDev && distEnd <= this.endDev);
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
            
            long rightStartTS, rightEndTS;
            long leftStartTS, leftEndTS;

            if ( extractor1.isSelfReference() ) {
                rightStartTS = ((EventFactHandle) handle1).getStartTimestamp();
                rightEndTS = ((EventFactHandle) handle1).getEndTimestamp();
            } else {
                rightStartTS = extractor1.getLongValue( workingMemory, handle1.getObject() );
                rightEndTS = rightStartTS;
            }       
            
            if ( extractor2.isSelfReference() ) {
                leftStartTS = ((EventFactHandle) handle2).getStartTimestamp();
                leftEndTS = ((EventFactHandle) handle2).getEndTimestamp();
            } else {
                leftStartTS = extractor2.getLongValue( workingMemory, handle2.getObject() );
                leftEndTS = leftStartTS;
            }            

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
        private void setParameters(long[] parameters) {
            if ( parameters == null || parameters.length == 0 ) {
                // open bounded range
                this.startDev = 0;
                this.endDev = 0;
                return;
            } else {
                for ( Long param : parameters ) {
                    if ( param.longValue() < 0 ) {
                        throw new RuntimeException( "[Coincides Evaluator]: negative values not allowed for temporal distance thresholds: '" + paramText + "'" );
                    }
                }
                if ( parameters.length == 1 ) {
                    // same deviation for both
                    this.startDev = parameters[0];
                    this.endDev = parameters[0];
                } else if ( parameters.length == 2 ) {
                    // different deviation 
                    this.startDev = parameters[0];
                    this.endDev = parameters[1];
                } else {
                    throw new RuntimeException( "[Coincides Evaluator]: Not possible to have more than 2 parameters: '" + paramText + "'" );
                }
            }
        }

    }

}
