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

package org.drools.mvel.evaluators;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.drools.core.base.ValueType;
import org.drools.compiler.rule.builder.EvaluatorDefinition;
import org.drools.drl.parser.impl.Operator;
import org.drools.core.util.TimeIntervalParser;
import org.drools.core.common.EventFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.ReteEvaluator;
import org.drools.mvel.evaluators.VariableRestriction.LeftEndRightStartContextEntry;
import org.drools.mvel.evaluators.VariableRestriction.VariableContextEntry;
import org.drools.core.rule.accessor.Evaluator;
import org.drools.core.rule.accessor.FieldValue;
import org.drools.core.rule.accessor.ReadAccessor;
import org.drools.core.time.Interval;

/**
 * <p>The implementation of the <code>metby</code> evaluator definition.</p>
 * 
 * <p>The <b><code>metby</code></b> evaluator correlates two events and matches when the current event's 
 * start timestamp happens at the same time as the correlated event's end timestamp.</p> 
 * 
 * <p>Lets look at an example:</p>
 * 
 * <pre>$eventA : EventA( this metby $eventB )</pre>
 *
 * <p>The previous pattern will match if and only if the $eventA starts at the same time $eventB finishes. 
 * In other words:</p>
 * 
 * <pre> 
 * abs( $eventA.startTimestamp - $eventB.endTimestamp ) == 0
 * </pre>
 * 
 * <p>The <b><code>metby</code></b> evaluator accepts one optional parameter. If it is defined, it determines
 * the maximum distance between the end timestamp of the correlated event and the start timestamp of the current
 * event in order for the operator to match. Example:</p>
 * 
 * <pre>$eventA : EventA( this metby[ 5s ] $eventB )</pre>
 * 
 * Will match if and only if:
 * 
 * <pre> 
 * abs( $eventA.startTimestamp - $eventB.endTimestamp) <= 5s 
 * </pre>
 * 
 * <p><b>NOTE:</b> it makes no sense to use a negative interval value for the parameter and the 
 * engine will raise an exception if that happens.</p>
 */
public class MetByEvaluatorDefinition
    implements
        EvaluatorDefinition {

    protected static final String metByOp = Operator.BuiltInOperator.MET_BY.getSymbol();

    public static Operator MET_BY = Operator.determineOperator( metByOp, false );

    public static Operator NOT_MET_BY = Operator.determineOperator( metByOp, true );

    private static String[] SUPPORTED_IDS = new String[] { metByOp };

    private Map<String, MetByEvaluator> cache         = Collections.emptyMap();

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        cache = (Map<String, MetByEvaluator>) in.readObject();
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
                                  final Target right ) {
        if ( this.cache == Collections.EMPTY_MAP ) {
            this.cache = new HashMap<String, MetByEvaluator>();
        }
        String key = isNegated + ":" + parameterText;
        MetByEvaluator eval = this.cache.get( key );
        if ( eval == null ) {
            long[] params = TimeIntervalParser.parse( parameterText );
            eval = new MetByEvaluator( type,
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
    public Target getTarget() {
        return Target.HANDLE;
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
     * Implements the 'metby' evaluator itself
     */
    public static class MetByEvaluator extends BaseEvaluator {
        private static final long serialVersionUID = 510l;

        private long              finalRange;
        private String            paramText;

        public MetByEvaluator() {
        }

        public MetByEvaluator(final ValueType type,
                              final boolean isNegated,
                              final long[] parameters,
                              final String paramText) {
            super( type,
                   isNegated ? NOT_MET_BY : MET_BY );
            this.paramText = paramText;
            this.setParameters( parameters );
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            super.readExternal( in );
            finalRange = in.readLong();
            paramText = (String) in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            super.writeExternal( out );
            out.writeLong( finalRange );
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
            return new Interval( Interval.MIN,
                                 0 );
        }

        public boolean evaluate(ReteEvaluator reteEvaluator,
                                final ReadAccessor extractor,
                                final InternalFactHandle object1,
                                final FieldValue object2) {
            throw new RuntimeException( "The 'metby' operator can only be used to compare one event to another, and never to compare to literal constraints." );
        }

        public boolean evaluateCachedRight(ReteEvaluator reteEvaluator,
                                           final VariableContextEntry context,
                                           final InternalFactHandle left) {
            if ( context.rightNull || 
                    context.declaration.getExtractor().isNullValue( reteEvaluator, left.getObject() )) {
                return false;
            }
            
            long rightStartTS = ((LeftEndRightStartContextEntry)context).timestamp;
            long dist = Math.abs( rightStartTS - ((EventFactHandle) left).getEndTimestamp() );
            return this.getOperator().isNegated() ^ ( dist <= this.finalRange );
        }

        public boolean evaluateCachedLeft(ReteEvaluator reteEvaluator,
                                          final VariableContextEntry context,
                                          final InternalFactHandle right) {
            if ( context.leftNull ||
                    context.extractor.isNullValue( reteEvaluator, right.getObject() ) ) {
                return false;
            }
            
            long rightStartTS = ((EventFactHandle) right).getStartTimestamp();
            long dist = Math.abs( rightStartTS - ((LeftEndRightStartContextEntry)context).timestamp );

            return this.getOperator().isNegated() ^ ( dist <= this.finalRange );
        }

        public boolean evaluate(ReteEvaluator reteEvaluator,
                                final ReadAccessor extractor1,
                                final InternalFactHandle handle1,
                                final ReadAccessor extractor2,
                                final InternalFactHandle handle2) {
            if ( extractor1.isNullValue( reteEvaluator, handle1.getObject() ) ||
                    extractor2.isNullValue( reteEvaluator, handle2.getObject() ) ) {
                return false;
            }
            
            long obj1StartTS = ((EventFactHandle) handle1).getStartTimestamp();
            long dist = Math.abs( obj1StartTS - ((EventFactHandle) handle2).getEndTimestamp() );
            return this.getOperator().isNegated() ^ ( dist <= this.finalRange );
        }

        public String toString() {
            return "metby[" + ((paramText != null) ? paramText : "") + "]";
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int PRIME = 31;
            int result = super.hashCode();
            result = PRIME * result + (int) (finalRange ^ (finalRange >>> 32));
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
            final MetByEvaluator other = (MetByEvaluator) obj;
            return finalRange == other.finalRange;
        }

        /**
         * This methods sets the parameters appropriately.
         *
         * @param parameters
         */
        private void setParameters(long[] parameters) {
            if ( parameters == null || parameters.length == 0 ) {
                this.finalRange = 0;
            } else if ( parameters.length == 1 ) {
                if ( parameters[0] >= 0 ) {
                    // defined max distance
                    this.finalRange = parameters[0];
                } else {
                    throw new RuntimeException( "[MetBy Evaluator]: Not possible to use negative parameter: '" + paramText + "'" );
                }
            } else {
                throw new RuntimeException( "[MetBy Evaluator]: Not possible to use " + parameters.length + " parameters: '" + paramText + "'" );
            }
        }

    }

}
