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
 * <p>The implementation of the <code>overlappedby</code> evaluator definition.</p>
 * 
 * <p>The <b><code>overlappedby</code></b> evaluator correlates two events and matches when the correlated event 
 * starts before the current event starts and finishes after the current event starts, but before
 * the current event finishes. In other words, both events have an overlapping period.</p> 
 * 
 * <p>Lets look at an example:</p>
 * 
 * <pre>$eventA : EventA( this overlappedby $eventB )</pre>
 *
 * <p>The previous pattern will match if and only if:</p>
 * 
 * <pre> $eventB.startTimestamp < $eventA.startTimestamp < $eventB.endTimestamp < $eventA.endTimestamp </pre>
 * 
 * <p>The <b><code>overlappedby</code></b> operator accepts 1 or 2 optional parameters as follow:</p>
 * 
 * <ul><li>If one parameter is defined, this will be the maximum distance between the start timestamp of the
 * current event and the end timestamp of the correlated event. Example:</li></lu>
 * 
 * <pre>$eventA : EventA( this overlappedby[ 5s ] $eventB )</pre>
 * 
 * Will match if and only if:
 * 
 * <pre> 
 * $eventB.startTimestamp < $eventA.startTimestamp < $eventB.endTimestamp < $eventA.endTimestamp &&
 * 0 <= $eventB.endTimestamp - $eventA.startTimestamp <= 5s 
 * </pre>
 * 
 * <ul><li>If two values are defined, the first value will be the minimum distance and the second value will be 
 * the maximum distance between the start timestamp of the current event and the end timestamp of the correlated 
 * event. Example:</li></lu>
 * 
 * <pre>$eventA : EventA( this overlappedby[ 5s, 10s ] $eventB )</pre>
 * 
 * Will match if and only if:
 * 
 * <pre> 
 * $eventB.startTimestamp < $eventA.startTimestamp < $eventB.endTimestamp < $eventA.endTimestamp &&
 * 5s <= $eventB.endTimestamp - $eventA.startTimestamp <= 10s 
 * </pre>
 */
public class OverlappedByEvaluatorDefinition
    implements
    EvaluatorDefinition {

    public static final String          overlappedbyOp = "overlappedby";

    public static Operator              OVERLAPPED_BY;

    public static Operator              NOT_OVERLAPPED_BY;

    private static String[]             SUPPORTED_IDS;

    private Map<String, OverlappedByEvaluator> cache   = Collections.emptyMap();

    { init(); }

    static void init() {
        if ( Operator.determineOperator( overlappedbyOp, false ) == null ) {
            OVERLAPPED_BY = Operator.addOperatorToRegistry( overlappedbyOp, false );
            NOT_OVERLAPPED_BY = Operator.addOperatorToRegistry( overlappedbyOp, true );
            SUPPORTED_IDS = new String[] { overlappedbyOp };
        }
    }

    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        cache  = (Map<String, OverlappedByEvaluator>)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(cache);
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
            this.cache = new HashMap<String, OverlappedByEvaluator>();
        }
        String key = isNegated + ":" + parameterText;
        OverlappedByEvaluator eval = this.cache.get( key );
        if ( eval == null ) {
            long[] params = TimeIntervalParser.parse( parameterText );
            eval = new OverlappedByEvaluator( type,
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
     * Implements the 'overlappedby' evaluator itself
     */
    public static class OverlappedByEvaluator extends BaseEvaluator {
        private static final long serialVersionUID = 510l;

        private long                  minDev, maxDev;
        private String                paramText;

        {
            OverlappedByEvaluatorDefinition.init();
        }

        public OverlappedByEvaluator() {
        }

        public OverlappedByEvaluator(final ValueType type,
                              final boolean isNegated,
                              final long[] parameters,
                              final String paramText) {
            super( type,
                   isNegated ? NOT_OVERLAPPED_BY : OVERLAPPED_BY );
            this.paramText = paramText;
            this.setParameters( parameters );
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            super.readExternal(in);
            minDev = in.readLong();
            maxDev = in.readLong();
            paramText = (String) in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            super.writeExternal(out);
            out.writeLong(minDev);
            out.writeLong(maxDev);
            out.writeObject( paramText );
        }
        
        @Override
        public boolean isTemporal() {
            return true;
        }
        
        @Override
        public Interval getInterval() {
            if( this.getOperator().isNegated() ) {
                return new Interval( Interval.MIN, Interval.MAX );
            }
            return new Interval( 0, Interval.MAX );
        }
        
        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final InternalFactHandle object1,
                                final FieldValue object2) {
            throw new RuntimeException( "The 'overlappedby' operator can only be used to compare one event to another, and never to compare to literal constraints." );
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context,
                                           final InternalFactHandle left) {
            if ( context.rightNull || 
                    context.declaration.getExtractor().isNullValue( workingMemory, left.getObject() )) {
                return false;
            }
            
            long rightStartTS = ((TemporalVariableContextEntry) context).startTS;
            long leftEndTS = ((EventFactHandle) left ).getEndTimestamp();
            long dist = leftEndTS - rightStartTS;
            return this.getOperator().isNegated() ^ ( 
                    ((EventFactHandle) left ).getStartTimestamp() < rightStartTS &&
                    leftEndTS < ((TemporalVariableContextEntry) context).endTS &&
                    dist >= this.minDev && dist <= maxDev );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context,
                                          final InternalFactHandle right) {
            if ( context.leftNull ||
                    context.extractor.isNullValue( workingMemory, right.getObject() ) ) {
                return false;
            }
            
            long leftEndTS = ((TemporalVariableContextEntry) context).endTS;
            long rightStartTS = ((EventFactHandle) right ).getStartTimestamp();
            long dist = leftEndTS - rightStartTS;
            return this.getOperator().isNegated() ^ ( 
                    ((TemporalVariableContextEntry) context).startTS < rightStartTS &&
                    leftEndTS < ((EventFactHandle) right).getEndTimestamp() &&
                    dist >= this.minDev && dist <= maxDev );
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
            
            long startTS = ((EventFactHandle) handle1).getStartTimestamp();
            long endTS = ((EventFactHandle) handle2).getEndTimestamp();
            long dist = endTS - startTS;
            return this.getOperator().isNegated() ^ ( ((EventFactHandle) handle2).getStartTimestamp() < startTS &&
                    endTS < ((EventFactHandle) handle1).getEndTimestamp() &&
                    dist >= this.minDev && dist <= this.maxDev );
        }

        public String toString() {
            return "overlappedby[" + ( ( paramText != null ) ? paramText : "" ) + "]";
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int PRIME = 31;
            int result = super.hashCode();
            result = PRIME * result + (int) (maxDev ^ (maxDev >>> 32));
            result = PRIME * result + (int) (minDev ^ (minDev >>> 32));
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
            final OverlappedByEvaluator other = (OverlappedByEvaluator) obj;
            return maxDev == other.maxDev && minDev == other.minDev;
        }

        /**
         * This methods sets the parameters appropriately.
         *
         * @param parameters
         */
        private void setParameters(long[] parameters) {
            if ( parameters == null || parameters.length == 0 ) {
                // open bounded range
                this.minDev = 1;
                this.maxDev = Long.MAX_VALUE;
            } else if ( parameters.length == 1 ) {
                // open bounded ranges
                this.minDev = 1;
                this.maxDev = parameters[0];
            } else if ( parameters.length == 2 ) {
                // open bounded ranges
                this.minDev = parameters[0];
                this.maxDev = parameters[1];
            } else {
                throw new RuntimeException( "[Overlaps Evaluator]: Not possible to use " + parameters.length + " parameters: '" + paramText + "'" );
            }
        }

    }

}
