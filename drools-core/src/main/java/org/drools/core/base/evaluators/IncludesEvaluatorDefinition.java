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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

/**
 * <p>The implementation of the <code>includes</code> evaluator definition.</p>
 * 
 * <p>The <b><code>includes</code></b> evaluator correlates two events and matches when the event 
 * being correlated happens during the current event. It is the symmetrical opposite of <code>during</code>
 * evaluator.</p> 
 * 
 * <p>Lets look at an example:</p>
 * 
 * <pre>$eventA : EventA( this includes $eventB )</pre>
 *
 * <p>The previous pattern will match if and only if the $eventB starts after $eventA starts and finishes
 * before $eventA finishes. In other words:</p>
 * 
 * <pre> $eventA.startTimestamp < $eventB.startTimestamp <= $eventB.endTimestamp < $eventA.endTimestamp </pre>
 * 
 * <p>The <b><code>includes</code></b> operator accepts 1, 2 or 4 optional parameters as follow:</p>
 * 
 * <ul><li>If one value is defined, this will be the maximum distance between the start timestamp of both
 * event and the maximum distance between the end timestamp of both events in order to operator match. Example:</li></lu>
 * 
 * <pre>$eventA : EventA( this includes[ 5s ] $eventB )</pre>
 * 
 * Will match if and only if:
 * 
 * <pre> 
 * 0 < $eventB.startTimestamp - $eventA.startTimestamp <= 5s &&
 * 0 < $eventA.endTimestamp - $eventB.endTimestamp <= 5s
 * </pre>
 * 
 * <ul><li>If two values are defined, the first value will be the minimum distance between the timestamps
 * of both events, while the second value will be the maximum distance between the timestamps of both events. 
 * Example:</li></lu>
 * 
 * <pre>$eventA : EventA( this includes[ 5s, 10s ] $eventB )</pre>
 * 
 * Will match if and only if:
 * 
 * <pre> 
 * 5s <= $eventB.startTimestamp - $eventA.startTimestamp <= 10s &&
 * 5s <= $eventA.endTimestamp - $eventB.endTimestamp <= 10s
 * </pre>
 * 
 * <ul><li>If four values are defined, the first two values will be the minimum and maximum distances between the 
 * start timestamp of both events, while the last two values will be the minimum and maximum distances between the 
 * end timestamp of both events. Example:</li></lu>
 * 
 * <pre>$eventA : EventA( this includes[ 2s, 6s, 4s, 10s ] $eventB )</pre>
 * 
 * Will match if and only if:
 * 
 * <pre> 
 * 2s <= $eventB.startTimestamp - $eventA.startTimestamp <= 6s &&
 * 4s <= $eventA.endTimestamp - $eventB.endTimestamp <= 10s
 * </pre>
 */
public class IncludesEvaluatorDefinition
    implements
    EvaluatorDefinition {

    public static final String          includesOp = "includes";

    public static Operator              INCLUDES;

    public static Operator              INCLUDES_NOT;

    private static String[]             SUPPORTED_IDS;

    private Map<String, IncludesEvaluator> cache       = Collections.emptyMap();
    private volatile TimeIntervalParser  parser        = new TimeIntervalParser();

    { init(); }

    static void init() {
        if ( Operator.determineOperator( includesOp, false ) == null ) {
            INCLUDES = Operator.addOperatorToRegistry( includesOp, false );
            INCLUDES_NOT = Operator.addOperatorToRegistry( includesOp, true );
            SUPPORTED_IDS = new String[] { includesOp };
        }
    }

    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        cache = (Map<String, IncludesEvaluator>) in.readObject();
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
            this.cache = new HashMap<String, IncludesEvaluator>();
        }
        String key = isNegated + ":" + parameterText;
        IncludesEvaluator eval = this.cache.get( key );
        if ( eval == null ) {
            Long[] params = parser.parse( parameterText );
            eval = new IncludesEvaluator( type,
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
     * Implements the 'includes' evaluator itself
     */
    public static class IncludesEvaluator extends BaseEvaluator {
        private static final long serialVersionUID = 510l;

        private long              startMinDev, startMaxDev;
        private long              endMinDev, endMaxDev;
        private String            paramText;

        {
            IncludesEvaluatorDefinition.init();
        }

        public IncludesEvaluator() {
        }

        public IncludesEvaluator(final ValueType type,
                                 final boolean isNegated,
                                 final Long[] parameters,
                                 final String paramText) {
            super( type,
                   isNegated ? INCLUDES_NOT : INCLUDES );
            this.paramText = paramText;
            this.setParameters( parameters );
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            super.readExternal( in );
            startMinDev = in.readLong();
            startMaxDev = in.readLong();
            endMinDev = in.readLong();
            endMaxDev = in.readLong();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            super.writeExternal( out );
            out.writeLong( startMinDev );
            out.writeLong( startMaxDev );
            out.writeLong( endMinDev );
            out.writeLong( endMaxDev );
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

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final InternalFactHandle object1,
                                final FieldValue object2) {
            throw new RuntimeException( "The 'includes' operator can only be used to compare one event to another, and never to compare to literal constraints." );
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context,
                                           final InternalFactHandle left) {
            if ( context.rightNull || 
                    context.declaration.getExtractor().isNullValue( workingMemory, left.getObject() )) {
                return false;
            }
            
            long distStart = ((EventFactHandle) left).getStartTimestamp() - ((TemporalVariableContextEntry) context).startTS;
            long distEnd = ((TemporalVariableContextEntry) context).endTS - ((EventFactHandle) left).getEndTimestamp();
            return this.getOperator().isNegated() ^ (distStart >= this.startMinDev && distStart <= this.startMaxDev && distEnd >= this.endMinDev && distEnd <= this.endMaxDev);
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context,
                                          final InternalFactHandle right) {
            if ( context.leftNull ||
                    context.extractor.isNullValue( workingMemory, right.getObject() ) ) {
                return false;
            }
            
            long distStart = ((TemporalVariableContextEntry) context).startTS- ((EventFactHandle) right).getStartTimestamp();
            long distEnd = ((EventFactHandle) right).getEndTimestamp() - ((TemporalVariableContextEntry) context).endTS;
            return this.getOperator().isNegated() ^ (distStart >= this.startMinDev && distStart <= this.startMaxDev && distEnd >= this.endMinDev && distEnd <= this.endMaxDev);
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
            
            long distStart = ((EventFactHandle) handle2).getStartTimestamp() - ((EventFactHandle) handle1).getStartTimestamp();
            long distEnd = ((EventFactHandle) handle1).getEndTimestamp() - ((EventFactHandle) handle2).getEndTimestamp();
            return this.getOperator().isNegated() ^ (distStart >= this.startMinDev && distStart <= this.startMaxDev && distEnd >= this.endMinDev && distEnd <= this.endMaxDev);
        }

        public String toString() {
            return "includes[" + startMinDev + ", " + startMaxDev + ", " + endMinDev + ", " + endMaxDev + "]";
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int PRIME = 31;
            int result = super.hashCode();
            result = PRIME * result + (int) (endMaxDev ^ (endMaxDev >>> 32));
            result = PRIME * result + (int) (endMinDev ^ (endMinDev >>> 32));
            result = PRIME * result + (int) (startMaxDev ^ (startMaxDev >>> 32));
            result = PRIME * result + (int) (startMinDev ^ (startMinDev >>> 32));
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
            final IncludesEvaluator other = (IncludesEvaluator) obj;
            return endMaxDev == other.endMaxDev && endMinDev == other.endMinDev && startMaxDev == other.startMaxDev && startMinDev == other.startMinDev;
        }

        /**
         * This methods sets the parameters appropriately.
         *
         * @param parameters
         */
        private void setParameters(Long[] parameters) {
            if ( parameters == null || parameters.length == 0 ) {
                // open bounded range
                this.startMinDev = 1;
                this.startMaxDev = Long.MAX_VALUE;
                this.endMinDev = 1;
                this.endMaxDev = Long.MAX_VALUE;
            } else if ( parameters.length == 1 ) {
                // open bounded ranges
                this.startMinDev = 1;
                this.startMaxDev = parameters[0].longValue();
                this.endMinDev = 1;
                this.endMaxDev = parameters[0].longValue();
            } else if ( parameters.length == 2 ) {
                // open bounded ranges
                this.startMinDev = parameters[0].longValue();
                this.startMaxDev = parameters[1].longValue();
                this.endMinDev = parameters[0].longValue();
                this.endMaxDev = parameters[1].longValue();
            } else if ( parameters.length == 4 ) {
                // open bounded ranges
                this.startMinDev = parameters[0].longValue();
                this.startMaxDev = parameters[1].longValue();
                this.endMinDev = parameters[2].longValue();
                this.endMaxDev = parameters[3].longValue();
            } else {
                throw new RuntimeException( "[During Evaluator]: Not possible to use " + parameters.length + " parameters: '" + paramText + "'" );
            }
        }

    }

}
