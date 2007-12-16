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
 * The implementation of the 'metby' evaluator definition
 * 
 * @author mgroch
 */
public class MetByEvaluatorDefinition
    implements
    EvaluatorDefinition {

    public static final Operator  MET_BY       = Operator.addOperatorToRegistry( "metby",
                                                                                  false );
    public static final Operator  NOT_MET_BY   = Operator.addOperatorToRegistry( "metby",
                                                                                  true );
    
    private static final String[] SUPPORTED_IDS = { MET_BY.getOperatorString() };
    
    private Map<String, MetByEvaluator> cache        = Collections.emptyMap();

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
            this.cache = new HashMap<String, MetByEvaluator>();
        }
        String key = isNegated + ":" + parameterText;
        MetByEvaluator eval = this.cache.get( key );
        if ( eval == null ) {
            eval = new MetByEvaluator( type,
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
     * Implements the 'metby' evaluator itself
     */
    public static class MetByEvaluator extends BaseEvaluator {
		private static final long serialVersionUID = 7907908401657594347L;
		
		private long                  finalRange;

        public MetByEvaluator(final ValueType type,
                              final boolean isNegated,
                              final String parameters) {
            super( type,
                   isNegated ? NOT_MET_BY : MET_BY );
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
            throw new RuntimeDroolsException( "The 'metby' operator can only be used to compare one event to another, and never to compare to literal constraints." );
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context,
                                           final Object left) {
            if ( context.rightNull ) {
                return false;
            }
            long rightStartTS = ((EventFactHandle)((ObjectVariableContextEntry) context).right).getStartTimestamp();
            long dist = Math.abs(rightStartTS - ((EventFactHandle) left ).getEndTimestamp());
            return this.getOperator().isNegated() ^ ( ((EventFactHandle) left ).getStartTimestamp() < rightStartTS && dist <= this.finalRange );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context,
                                          final Object right) {
            if ( context.extractor.isNullValue( workingMemory,
                                                right ) ) {
                return false;
            }
            long rightStartTS = ((EventFactHandle) right ).getStartTimestamp();
            long dist = Math.abs(rightStartTS - ((EventFactHandle) ((ObjectVariableContextEntry) context).left).getEndTimestamp());

            return this.getOperator().isNegated() ^ ( ((EventFactHandle) ((ObjectVariableContextEntry) context).left).getStartTimestamp() <= rightStartTS && 
            	    dist <= this.finalRange );
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
            long obj1StartTS = ((EventFactHandle) object1 ).getStartTimestamp();
            long dist = Math.abs(obj1StartTS - ((EventFactHandle) object2 ).getEndTimestamp());
            return this.getOperator().isNegated() ^ ( ((EventFactHandle) object2 ).getStartTimestamp() <= obj1StartTS && dist <= this.finalRange );
        }

        public String toString() {
            return "metby[" + finalRange + "]";
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
         * This methods tries to parse the string of parameters to customize 
         * the evaluator.
         * 
         * @param parameters
         */
        private void parseParameters(String parameters) {
            if ( parameters == null || parameters.trim().length() == 0 ) {
                // exact metby
                this.finalRange = 0;
                return;
            }

            try {
                String[] ranges = parameters.split( "," );
                if ( ranges.length == 1 ) {
                    // limit of tolerance for overlap or gap, respectively
                    this.finalRange = Long.parseLong( ranges[0] );
                } else {
                    throw new RuntimeDroolsException( "[Metby Evaluator]: Not possible to parse parameters: '" + parameters + "'" );
                }
            } catch ( NumberFormatException e ) {
                throw new RuntimeDroolsException( "[Metby Evaluator]: Not possible to parse parameters: '" + parameters + "'",
                                                  e );
            }
        }

    }

}
