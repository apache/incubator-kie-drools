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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;

import org.drools.RuntimeDroolsException;
import org.drools.base.BaseEvaluator;
import org.drools.base.ValueType;
import org.drools.common.EventFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.rule.VariableRestriction.ObjectVariableContextEntry;
import org.drools.rule.VariableRestriction.VariableContextEntry;
import org.drools.spi.Evaluator;
import org.drools.spi.Extractor;
import org.drools.spi.FieldValue;

/**
 * The implementation of the 'overlappedby' evaluator definition
 *
 * @author mgroch
 */
public class OverlappedByEvaluatorDefinition
    implements
    EvaluatorDefinition {

    public static final Operator  OVERLAPPED_BY       = Operator.addOperatorToRegistry( "overlappedby",
                                                                                  false );
    public static final Operator  NOT_OVERLAPPED_BY   = Operator.addOperatorToRegistry( "overlappedby",
                                                                                  true );

    private static final String[] SUPPORTED_IDS = { OVERLAPPED_BY.getOperatorString() };

    private Map<String, OverlappedByEvaluator> cache        = Collections.emptyMap();

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
        if ( this.cache == Collections.EMPTY_MAP ) {
            this.cache = new HashMap<String, OverlappedByEvaluator>();
        }
        String key = isNegated + ":" + parameterText;
        OverlappedByEvaluator eval = this.cache.get( key );
        if ( eval == null ) {
            eval = new OverlappedByEvaluator( type,
                                       isNegated,
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
     * Implements the 'overlappedby' evaluator itself
     */
    public static class OverlappedByEvaluator extends BaseEvaluator {
		private static final long serialVersionUID = -2768899194494247889L;

		private long                  startMinDev, startMaxDev;
        private long                  endMinDev, endMaxDev;

        public OverlappedByEvaluator() {
        }

        public OverlappedByEvaluator(final ValueType type,
                              final boolean isNegated,
                              final String parameters) {
            super( type,
                   isNegated ? NOT_OVERLAPPED_BY : OVERLAPPED_BY );
            this.parseParameters( parameters );
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            super.readExternal(in);
            startMinDev = in.readLong();
            startMaxDev = in.readLong();
            endMinDev   = in.readLong();
            endMaxDev   = in.readLong();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            super.writeExternal(out);
            out.writeLong(startMinDev);
            out.writeLong(startMaxDev);
            out.writeLong(endMinDev);
            out.writeLong(endMaxDev);
        }

        @Override
        public Object prepareObject(InternalFactHandle handle) {
            return handle;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            throw new RuntimeDroolsException( "The 'overlappedby' operator can only be used to compare one event to another, and never to compare to literal constraints." );
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context,
                                           final Object left) {
            if ( context.rightNull ) {
                return false;
            }
            long rightStartTS = ((EventFactHandle)((ObjectVariableContextEntry) context).right).getStartTimestamp();
			long leftEndTS = ((EventFactHandle) left ).getEndTimestamp();
            long distStart = rightStartTS - ((EventFactHandle) left ).getStartTimestamp();
            long distEnd = ((EventFactHandle)((ObjectVariableContextEntry) context).right).getEndTimestamp() - leftEndTS;
            return this.getOperator().isNegated() ^ ( distStart >= this.startMinDev && distStart <= this.startMaxDev
            		&& distEnd >= this.endMinDev && distEnd <= this.endMaxDev && rightStartTS < leftEndTS );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context,
                                          final Object right) {
            if ( context.extractor.isNullValue( workingMemory,
                                                right ) ) {
                return false;
            }
            long leftEndTS = ((EventFactHandle) ((ObjectVariableContextEntry) context).left).getEndTimestamp();
			long rightStartTS = ((EventFactHandle) right ).getStartTimestamp();
            long distStart = rightStartTS - ((EventFactHandle) ((ObjectVariableContextEntry) context).left).getStartTimestamp();
            long distEnd = ((EventFactHandle) right ).getEndTimestamp() - leftEndTS;
            return this.getOperator().isNegated() ^ ( distStart >= this.startMinDev && distStart <= this.startMaxDev
            		&& distEnd >= this.endMinDev && distEnd <= this.endMaxDev && rightStartTS < leftEndTS );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2,
                                final Object object2) {
            if ( extractor1.isNullValue( workingMemory,
                                         object1 ) ) {
                return false;
            }
            long o1startTS = ((EventFactHandle) object1 ).getStartTimestamp();
            long o2endTS = ((EventFactHandle) object2 ).getEndTimestamp();
            long distStart = o1startTS - ((EventFactHandle) object2 ).getStartTimestamp();
            long distEnd = ((EventFactHandle) object1 ).getEndTimestamp() - o2endTS;
            return this.getOperator().isNegated() ^ ( distStart >= this.startMinDev && distStart <= this.startMaxDev
            		&& distEnd >= this.endMinDev && distEnd <= this.endMaxDev && o1startTS < o2endTS );
        }

        public String toString() {
            return "overlappedby[" + startMinDev + ", " + startMaxDev + ", " + endMinDev + ", " + endMaxDev + "]";
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
            final OverlappedByEvaluator other = (OverlappedByEvaluator) obj;
            return endMaxDev == other.endMaxDev && endMinDev == other.endMinDev
            	&& startMaxDev == other.startMaxDev && startMinDev == other.startMinDev;
        }

        /**
         * This methods tries to parse the string of parameters to customize
         * the evaluator.
         *
         * @param parameters
         */
        private void parseParameters(String parameters) {
            if ( parameters == null || parameters.trim().length() == 0 ) {
            	// open bounded ranges
                this.startMinDev = 1;
                this.startMaxDev = Long.MAX_VALUE;
                this.endMinDev = 1;
                this.endMaxDev = Long.MAX_VALUE;
                return;
            }

            try {
                String[] ranges = parameters.split( "," );
                if ( ranges.length == 1 ) {
                    // deterministic point in time for deviation of the starts of the intervals
                	this.startMinDev = Long.parseLong( ranges[0] );
                    this.startMaxDev = this.startMinDev;
                    this.endMinDev = this.startMinDev;
                    this.endMaxDev = this.startMinDev;
                } else if ( ranges.length == 2 ) {
                    // deterministic points in time for deviations of the starts and the ends of the intervals
                    this.startMinDev = Long.parseLong( ranges[0] );
                    this.startMaxDev = this.startMinDev;
                    this.endMinDev = Long.parseLong( ranges[1] );
                    this.endMaxDev = this.endMinDev;
                } else if ( ranges.length == 4 ) {
                    // ranges for deviations of the starts and the ends of the intervals
                	this.startMinDev = Long.parseLong( ranges[0] );
                    this.startMaxDev = Long.parseLong( ranges[1] );
                    this.endMinDev = Long.parseLong( ranges[2] );
                    this.endMaxDev = Long.parseLong( ranges[3] );
                } else {
                    throw new RuntimeDroolsException( "[Overlappedby Evaluator]: Not possible to parse parameters: '" + parameters + "'" );
                }
            } catch ( NumberFormatException e ) {
                throw new RuntimeDroolsException( "[Overlappedby Evaluator]: Not possible to parse parameters: '" + parameters + "'",
                                                  e );
            }
        }

    }

}
