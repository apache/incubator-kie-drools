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
 * <p>The implementation of the <code>starts</code> evaluator definition.</p>
 * 
 * <p>The <b><code>starts</code></b> evaluator correlates two events and matches when the current event's 
 * end timestamp happens before the correlated event's end timestamp, but both start timestamps occur at
 * the same time.</p> 
 * 
 * <p>Lets look at an example:</p>
 * 
 * <pre>$eventA : EventA( this starts $eventB )</pre>
 *
 * <p>The previous pattern will match if and only if the $eventA finishes before $eventB finishes and starts
 * at the same time $eventB starts. In other words:</p>
 * 
 * <pre> 
 * $eventA.startTimestamp == $eventB.startTimestamp &&
 * $eventA.endTimestamp < $eventB.endTimestamp 
 * </pre>
 * 
 * <p>The <b><code>starts</code></b> evaluator accepts one optional parameter. If it is defined, it determines
 * the maximum distance between the start timestamp of both events in order for the operator to match. Example:</p>
 * 
 * <pre>$eventA : EventA( this starts[ 5s ] $eventB )</pre>
 * 
 * Will match if and only if:
 * 
 * <pre> 
 * abs( $eventA.startTimestamp - $eventB.startTimestamp ) <= 5s &&
 * $eventA.endTimestamp < $eventB.endTimestamp 
 * </pre>
 * 
 * <p><b>NOTE:</b> it makes no sense to use a negative interval value for the parameter and the 
 * engine will raise an exception if that happens.</p>
 * 
 * @author etirelli
 * @author mgroch
 */
public class StartsEvaluatorDefinition
    implements
    EvaluatorDefinition {

    public static final Operator         STARTS        = Operator.addOperatorToRegistry( "starts",
                                                                                         false );
    public static final Operator         STARTS_NOT    = Operator.addOperatorToRegistry( "starts",
                                                                                         true );

    private static final String[]        SUPPORTED_IDS = {STARTS.getOperatorString()};

    private Map<String, StartsEvaluator> cache         = Collections.emptyMap();
    private volatile TimeIntervalParser  parser        = new TimeIntervalParser();

    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        cache = (Map<String, StartsEvaluator>) in.readObject();
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
            this.cache = new HashMap<String, StartsEvaluator>();
        }
        String key = isNegated + ":" + parameterText;
        StartsEvaluator eval = this.cache.get( key );
        if ( eval == null ) {
            Long[] params = parser.parse( parameterText );
            eval = new StartsEvaluator( type,
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
     * Implements the 'starts' evaluator itself
     */
    public static class StartsEvaluator extends BaseEvaluator {
        private static final long serialVersionUID = 5622952247746290865L;

        private long              startDev;
        private String            paramText;

        public StartsEvaluator(final ValueType type,
                               final boolean isNegated,
                               final Long[] params,
                               final String paramText) {
            super( type,
                   isNegated ? STARTS_NOT : STARTS );
            this.paramText = paramText;
            this.setParameters( params );
        }

        public StartsEvaluator() {
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            super.readExternal( in );
            startDev = in.readLong();
            paramText = (String) in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            super.writeExternal( out );
            out.writeLong( startDev );
            out.writeObject( paramText );
        }

        @Override
        public Object prepareLeftObject(InternalFactHandle handle) {
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
            throw new RuntimeDroolsException( "The 'starts' operator can only be used to compare one event to another, and never to compare to literal constraints." );
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context,
                                           final Object left) {

            if ( context.rightNull ) {
                return false;
            }
            long distStart = Math.abs( ((EventFactHandle) ((ObjectVariableContextEntry) context).right).getStartTimestamp() - ((EventFactHandle) left).getStartTimestamp() );
            long distEnd = ((EventFactHandle) left).getEndTimestamp() - ((EventFactHandle) ((ObjectVariableContextEntry) context).right).getEndTimestamp();
            return this.getOperator().isNegated() ^ (distStart <= this.startDev && distEnd > 0 );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context,
                                          final Object right) {
            if ( context.extractor.isNullValue( workingMemory,
                                                right ) ) {
                return false;
            }
            long distStart = Math.abs( ((EventFactHandle) right).getStartTimestamp() - ((EventFactHandle) ((ObjectVariableContextEntry) context).left).getStartTimestamp() );
            long distEnd = ((EventFactHandle) ((ObjectVariableContextEntry) context).left).getEndTimestamp() - ((EventFactHandle) right).getEndTimestamp();
            return this.getOperator().isNegated() ^ (distStart <= this.startDev && distEnd > 0 );
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
            long distEnd = ((EventFactHandle) object2).getEndTimestamp() - ((EventFactHandle) object1).getEndTimestamp();
            return this.getOperator().isNegated() ^ (distStart <= this.startDev && distEnd > 0 );
        }

        public String toString() {
            return "starts[" + ((paramText != null) ? paramText : "") + "]";
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int PRIME = 31;
            int result = super.hashCode();
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
            final StartsEvaluator other = (StartsEvaluator) obj;
            return startDev == other.startDev;
        }

        /**
         * This methods sets the parameters appropriately.
         *
         * @param parameters
         */
        private void setParameters(Long[] parameters) {
            if ( parameters == null || parameters.length == 0 ) {
                this.startDev = 0;
            } else if ( parameters.length == 1 ) {
                if( parameters[0].longValue() >= 0 ) {
                    // defined deviation for end timestamp
                    this.startDev = parameters[0].longValue();
                } else {
                    throw new RuntimeDroolsException("[Starts Evaluator]: Not possible to use negative parameter: '" + paramText + "'");
                }
            } else {
                throw new RuntimeDroolsException( "[Starts Evaluator]: Not possible to use " + parameters.length + " parameters: '" + paramText + "'" );
            }
        }


    }

}
