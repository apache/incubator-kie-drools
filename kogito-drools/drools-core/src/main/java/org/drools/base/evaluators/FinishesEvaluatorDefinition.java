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
 * The implementation of the 'finishes' evaluator definition
 * 
 * @author mgroch
 */
public class FinishesEvaluatorDefinition
    implements
    EvaluatorDefinition {

    public static final Operator  FINISHES       = Operator.addOperatorToRegistry( "finishes",
                                                                                  false );
    public static final Operator  FINISHES_NOT   = Operator.addOperatorToRegistry( "finishes",
                                                                                  true );
    
    private static final String[] SUPPORTED_IDS = { FINISHES.getOperatorString() };
    
    private Map<String, FinishesEvaluator> cache        = Collections.emptyMap();

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
            this.cache = new HashMap<String, FinishesEvaluator>();
        }
        String key = isNegated + ":" + parameterText;
        FinishesEvaluator eval = this.cache.get( key );
        if ( eval == null ) {
            eval = new FinishesEvaluator( type,
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
     * Implements the 'finishes' evaluator itself
     */
    public static class FinishesEvaluator extends BaseEvaluator {
		private static final long serialVersionUID = 6232789044144077522L;
		
		private long                  startMinDev, startMaxDev;
        private long                  endDev;

        public FinishesEvaluator(final ValueType type,
                              final boolean isNegated,
                              final String parameters) {
            super( type,
                   isNegated ? FINISHES_NOT : FINISHES );
            this.parseParameters( parameters );
        }
        
        @Override
        public Object prepareObject(InternalFactHandle handle) {
            return handle;
        }
        
        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            throw new RuntimeDroolsException( "The 'finishes' operator can only be used to compare one event to another, and never to compare to literal constraints." );
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                final VariableContextEntry context,
                final Object left) {
			
        	if ( context.rightNull ) {
        		return false;
				}
			long distStart = ((EventFactHandle)((ObjectVariableContextEntry) context).right).getStartTimestamp() - ((EventFactHandle) left ).getStartTimestamp();
			long distEnd = Math.abs(((EventFactHandle) left ).getEndTimestamp() - ((EventFactHandle)((ObjectVariableContextEntry) context).right).getEndTimestamp());
			return this.getOperator().isNegated() ^ ( distStart >= this.startMinDev && distStart <= this.startMaxDev 
					&& distEnd <= this.endDev );
		}
			
		public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
			               final VariableContextEntry context,
			               final Object right) {
			if ( context.extractor.isNullValue( workingMemory,
			                     right ) ) {
			return false;
			}
			long distStart = ((EventFactHandle) right ).getStartTimestamp() - ((EventFactHandle) ((ObjectVariableContextEntry) context).left).getStartTimestamp();
			long distEnd = Math.abs(((EventFactHandle) ((ObjectVariableContextEntry) context).left).getEndTimestamp() - ((EventFactHandle) right ).getEndTimestamp());
			return this.getOperator().isNegated() ^ ( distStart >= this.startMinDev && distStart <= this.startMaxDev 
					&& distEnd <= this.endDev );
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
			long distStart = ((EventFactHandle) object1 ).getStartTimestamp() - ((EventFactHandle) object2 ).getStartTimestamp();
			long distEnd = Math.abs(((EventFactHandle) object2 ).getEndTimestamp() - ((EventFactHandle) object1 ).getEndTimestamp());
			return this.getOperator().isNegated() ^ ( distStart >= this.startMinDev && distStart <= this.startMaxDev 
					&& distEnd <= this.endDev );
		}

        public String toString() {
            return "finishes[" + startMinDev + ", " + startMaxDev + ", " + endDev + "]";
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int PRIME = 31;
            int result = super.hashCode();
            result = PRIME * result + (int) (endDev ^ (endDev >>> 32));
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
            final FinishesEvaluator other = (FinishesEvaluator) obj;
            return endDev == other.endDev && startMaxDev == other.startMaxDev && startMinDev == other.startMinDev;
        }

        /**
         * This methods tries to parse the string of parameters to customize 
         * the evaluator.
         * 
         * @param parameters
         */
        private void parseParameters(String parameters) {
        	if ( parameters == null || parameters.trim().length() == 0 ) {
                // exact matching at the end of the intervals, open bounded range for the starts
                this.startMinDev = 1;
                this.startMaxDev = Long.MAX_VALUE;
                this.endDev = 0;
                return;
            }

            try {
                String[] ranges = parameters.split( "," );
                if ( ranges.length == 1 ) {
                    // exact matching at the end of the intervals
                	// deterministic point in time for deviations of the starts of the intervals
                	this.startMinDev = Long.parseLong( ranges[0] );
                    this.startMaxDev = this.startMinDev;
                    this.endDev = 0;
                } else if ( ranges.length == 2 ) {
                    // exact matching at the end of the intervals
                	// range for deviations of the starts of the intervals 
                    this.startMinDev = Long.parseLong( ranges[0] );
                    this.startMaxDev = Long.parseLong( ranges[1] );
                    this.endDev = 0;
                } else if ( ranges.length == 3 ) {
                	// max. deviation at the ends of the intervals
                	// range for deviations of the starts of the intervals 
                    this.startMinDev = Long.parseLong( ranges[0] );
                    this.startMaxDev = Long.parseLong( ranges[1] );
                    this.endDev = Long.parseLong( ranges[2] );
                } else {
                    throw new RuntimeDroolsException( "[Finishes Evaluator]: Not possible to parse parameters: '" + parameters + "'" );
                }
            } catch ( NumberFormatException e ) {
                throw new RuntimeDroolsException( "[Finishes Evaluator]: Not possible to parse parameters: '" + parameters + "'",
                                                  e );
            }
        }

    }

}
