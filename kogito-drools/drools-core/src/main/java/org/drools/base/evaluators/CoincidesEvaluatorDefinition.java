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
 * <p>The implementation of the 'coincides' evaluator definition.</p>
 * 
 * <p>The <b><code>coincides</code></b> evaluator correlates two events and matches when both
 * happen at the same time. Optionally, the evaluator accept thresholds for the distance between
 * events start and finish timestamps.</p> 
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
 * for the start timestamp and the second one is used as a parameter for the end timestamp.</p>
 * 
 * ---------------------- NEED TO FINISH WRITING THE JAVADOC -----------------------------
 * 
 * <pre> 3m30s <= $eventA.startTimestamp - $eventB.endTimeStamp <= 4m </pre>
 * 
 * <p>The temporal distance interval for the <b><code>after</code></b> operator is optional:</p>
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
 * 
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
        if ( this.cache == Collections.EMPTY_MAP ) {
            this.cache = new HashMap<String, CoincidesEvaluator>();
        }
        String key = isNegated + ":" + parameterText;
        CoincidesEvaluator eval = this.cache.get( key );
        if ( eval == null ) {
            Long[] params = parser.parse( parameterText );
            eval = new CoincidesEvaluator( type,
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
     * Implements the 'coincides' evaluator itself
     */
    public static class CoincidesEvaluator extends BaseEvaluator {
        private static final long serialVersionUID = 6031520837249122183L;

        private long              startDev;
        private long              endDev;
        private String            paramText;

        public CoincidesEvaluator() {
        }

        public CoincidesEvaluator(final ValueType type,
                                  final boolean isNegated,
                                  final Long[] parameters,
                                  final String paramText) {
            super( type,
                   isNegated ? COINCIDES_NOT : COINCIDES );
            this.paramText = paramText;
            this.setParameters( parameters );
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            super.readExternal( in );
            startDev = in.readLong();
            endDev = in.readLong();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            super.writeExternal( out );
            out.writeLong( startDev );
            out.writeLong( endDev );
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
            long distStart = Math.abs( ((EventFactHandle) ((ObjectVariableContextEntry) context).right).getStartTimestamp() - ((EventFactHandle) left).getStartTimestamp() );
            long distEnd = Math.abs( ((EventFactHandle) ((ObjectVariableContextEntry) context).right).getEndTimestamp() - ((EventFactHandle) left).getEndTimestamp() );
            return this.getOperator().isNegated() ^ (distStart <= this.startDev && distEnd <= this.endDev);
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context,
                                          final Object right) {
            if ( context.extractor.isNullValue( workingMemory,
                                                right ) ) {
                return false;
            }
            long distStart = Math.abs( ((EventFactHandle) right).getStartTimestamp() - ((EventFactHandle) ((ObjectVariableContextEntry) context).left).getStartTimestamp() );
            long distEnd = Math.abs( ((EventFactHandle) right).getEndTimestamp() - ((EventFactHandle) ((ObjectVariableContextEntry) context).left).getEndTimestamp() );
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
            long distStart = Math.abs( ((EventFactHandle) object1).getStartTimestamp() - ((EventFactHandle) object2).getStartTimestamp() );
            long distEnd = Math.abs( ((EventFactHandle) object1).getEndTimestamp() - ((EventFactHandle) object2).getEndTimestamp() );
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
            } else if ( parameters.length == 1 ) {
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
