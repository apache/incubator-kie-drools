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
 * The implementation of the 'during' evaluator definition
 *
 * @author mgroch
 */
public class DuringEvaluatorDefinition
    implements
    EvaluatorDefinition {

    public static final Operator  DURING       = Operator.addOperatorToRegistry( "during",
                                                                                  false );
    public static final Operator  NOT_DURING   = Operator.addOperatorToRegistry( "during",
                                                                                  true );

    private static final String[] SUPPORTED_IDS = { DURING.getOperatorString() };

    private Map<String, DuringEvaluator> cache        = Collections.emptyMap();

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        cache  = (Map<String, DuringEvaluator>)in.readObject();
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
            this.cache = new HashMap<String, DuringEvaluator>();
        }
        String key = isNegated + ":" + parameterText;
        DuringEvaluator eval = this.cache.get( key );
        if ( eval == null ) {
            eval = new DuringEvaluator( type,
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
     * Implements the 'during' evaluator itself
     */
    public static class DuringEvaluator extends BaseEvaluator {
		private static final long serialVersionUID = -5856043346192967722L;

		private long                  startMinDev, startMaxDev;
        private long                  endMinDev, endMaxDev;

        public DuringEvaluator() {
        }

        public DuringEvaluator(final ValueType type,
                              final boolean isNegated,
                              final String parameters) {
            super( type,
                   isNegated ? NOT_DURING : DURING );
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
                                final Object object1,
                                final FieldValue object2) {
            throw new RuntimeDroolsException( "The 'during' operator can only be used to compare one event to another, and never to compare to literal constraints." );
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                final VariableContextEntry context,
                final Object left) {

        	if ( context.rightNull ) {
        		return false;
				}
			long distStart = ((EventFactHandle)((ObjectVariableContextEntry) context).right).getStartTimestamp() - ((EventFactHandle) left ).getStartTimestamp();
			long distEnd = ((EventFactHandle) left ).getEndTimestamp() - ((EventFactHandle)((ObjectVariableContextEntry) context).right).getEndTimestamp();
			return this.getOperator().isNegated() ^ ( distStart >= this.startMinDev && distStart <= this.startMaxDev
					&& distEnd >= this.endMinDev && distEnd <= this.endMaxDev );
		}

		public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
			               final VariableContextEntry context,
			               final Object right) {
			if ( context.extractor.isNullValue( workingMemory,
			                     right ) ) {
			return false;
			}
			long distStart = ((EventFactHandle) right ).getStartTimestamp() - ((EventFactHandle) ((ObjectVariableContextEntry) context).left).getStartTimestamp();
			long distEnd = ((EventFactHandle) ((ObjectVariableContextEntry) context).left).getEndTimestamp() - ((EventFactHandle) right ).getEndTimestamp();
			return this.getOperator().isNegated() ^ ( distStart >= this.startMinDev && distStart <= this.startMaxDev
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
			long distStart = ((EventFactHandle) object1 ).getStartTimestamp() - ((EventFactHandle) object2 ).getStartTimestamp();
			long distEnd = ((EventFactHandle) object2 ).getEndTimestamp() - ((EventFactHandle) object1 ).getEndTimestamp();
			return this.getOperator().isNegated() ^ ( distStart >= this.startMinDev && distStart <= this.startMaxDev
					&& distEnd >= this.endMinDev && distEnd <= this.endMaxDev );
		}

        public String toString() {
            return "during[" + startMinDev + ", " + startMaxDev + ", " + endMinDev + ", " + endMaxDev + "]";
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
            final DuringEvaluator other = (DuringEvaluator) obj;
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
                    throw new RuntimeDroolsException( "[During Evaluator]: Not possible to parse parameters: '" + parameters + "'" );
                }
            } catch ( NumberFormatException e ) {
                throw new RuntimeDroolsException( "[During Evaluator]: Not possible to parse parameters: '" + parameters + "'",
                                                  e );
            }
        }

    }

}
