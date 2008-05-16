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
import org.drools.spi.InternalReadAccessor;
import org.drools.spi.FieldValue;

/**
 * The implementation of the 'starts' evaluator definition
 *
 * @author mgroch
 */
public class StartsEvaluatorDefinition
    implements
    EvaluatorDefinition {

    public static final Operator  STARTS       = Operator.addOperatorToRegistry( "starts",
                                                                                  false );
    public static final Operator  STARTS_NOT   = Operator.addOperatorToRegistry( "starts",
                                                                                  true );

    private static final String[] SUPPORTED_IDS = { STARTS.getOperatorString() };

    private Map<String, StartsEvaluator> cache        = Collections.emptyMap();

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        cache  = (Map<String, StartsEvaluator>)in.readObject();
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
            this.cache = new HashMap<String, StartsEvaluator>();
        }
        String key = isNegated + ":" + parameterText;
        StartsEvaluator eval = this.cache.get( key );
        if ( eval == null ) {
            eval = new StartsEvaluator( type,
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
     * Implements the 'starts' evaluator itself
     */
    public static class StartsEvaluator extends BaseEvaluator {
		private static final long serialVersionUID = 5622952247746290865L;

		private long                  startDev;
        private long                  endMinDev, endMaxDev;

        public StartsEvaluator(final ValueType type,
                              final boolean isNegated,
                              final String parameters) {
            super( type,
                   isNegated ? STARTS_NOT : STARTS );
            this.parseParameters( parameters );
        }

        public StartsEvaluator() {
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            super.readExternal(in);
            startDev = in.readLong();
            endMinDev   = in.readLong();
            endMaxDev   = in.readLong();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            super.writeExternal(out);
            out.writeLong(startDev);
            out.writeLong(endMinDev);
            out.writeLong(endMaxDev);
        }

        @Override
        public Object prepareObject(InternalFactHandle handle) {
            return handle;
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
			long distStart = Math.abs(((EventFactHandle)((ObjectVariableContextEntry) context).right).getStartTimestamp() - ((EventFactHandle) left ).getStartTimestamp());
			long distEnd = ((EventFactHandle) left ).getEndTimestamp() - ((EventFactHandle)((ObjectVariableContextEntry) context).right).getEndTimestamp();
			return this.getOperator().isNegated() ^ ( distStart <= this.startDev
					&& distEnd >= this.endMinDev && distEnd <= this.endMaxDev );
		}

		public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
			               final VariableContextEntry context,
			               final Object right) {
			if ( context.extractor.isNullValue( workingMemory,
			                     right ) ) {
			return false;
			}
			long distStart = Math.abs(((EventFactHandle) right ).getStartTimestamp() - ((EventFactHandle) ((ObjectVariableContextEntry) context).left).getStartTimestamp());
			long distEnd = ((EventFactHandle) ((ObjectVariableContextEntry) context).left).getEndTimestamp() - ((EventFactHandle) right ).getEndTimestamp();
			return this.getOperator().isNegated() ^ ( distStart <= this.startDev
					&& distEnd >= this.endMinDev && distEnd <= this.endMaxDev );
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
			long distStart = Math.abs(((EventFactHandle) object1 ).getStartTimestamp() - ((EventFactHandle) object2 ).getStartTimestamp());
			long distEnd = ((EventFactHandle) object2 ).getEndTimestamp() - ((EventFactHandle) object1 ).getEndTimestamp();
			return this.getOperator().isNegated() ^ ( distStart  <= this.startDev
					&& distEnd >= this.endMinDev && distEnd <= this.endMaxDev );
		}

        public String toString() {
            return "starts[" + startDev + ", " + endMinDev + ", " + endMaxDev + "]";
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
            return endMaxDev == other.endMaxDev && endMinDev == other.endMinDev && startDev == other.startDev;
        }

        /**
         * This methods tries to parse the string of parameters to customize
         * the evaluator.
         *
         * @param parameters
         */
        private void parseParameters(String parameters) {
            if ( parameters == null || parameters.trim().length() == 0 ) {
                // exact matching at the beginning of the intervals, open bounded ranges for the ends
                this.startDev = 0;
                this.endMinDev = 1;
                this.endMaxDev = Long.MAX_VALUE;
                return;
            }

            try {
                String[] ranges = parameters.split( "," );
                if ( ranges.length == 1 ) {
                    // exact matching at the beginning of the intervals
                	// deterministic point in time for deviations of the ends of the intervals
                	this.startDev = 0;
                	this.endMinDev = Long.parseLong( ranges[0] );
                    this.endMaxDev = this.endMinDev;
                } else if ( ranges.length == 2 ) {
                    // exact matching at the beginning of the intervals
                	// range for deviations of the ends of the intervals
                	this.startDev = 0;
                    this.endMinDev = Long.parseLong( ranges[0] );
                    this.endMaxDev = Long.parseLong( ranges[1] );
                } else if ( ranges.length == 3 ) {
                	// max. deviation at the starts of the intervals
                	// range for deviations of the ends of the intervals
                	this.startDev = Long.parseLong( ranges[0] );;
                    this.endMinDev = Long.parseLong( ranges[1] );
                    this.endMaxDev = Long.parseLong( ranges[2] );
                } else {
                    throw new RuntimeDroolsException( "[Starts Evaluator]: Not possible to parse parameters: '" + parameters + "'" );
                }
            } catch ( NumberFormatException e ) {
                throw new RuntimeDroolsException( "[Starts Evaluator]: Not possible to parse parameters: '" + parameters + "'",
                                                  e );
            }
        }

    }

}
