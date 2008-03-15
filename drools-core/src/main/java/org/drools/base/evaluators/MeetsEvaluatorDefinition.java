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
 * The implementation of the 'meets' evaluator definition
 *
 * @author mgroch
 */
public class MeetsEvaluatorDefinition
    implements
    EvaluatorDefinition {

    public static final Operator  MEETS       = Operator.addOperatorToRegistry( "meets",
                                                                                  false );
    public static final Operator  MEETS_NOT   = Operator.addOperatorToRegistry( "meets",
                                                                                  true );

    private static final String[] SUPPORTED_IDS = { MEETS.getOperatorString() };

    private Map<String, MeetsEvaluator> cache        = Collections.emptyMap();

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        cache  = (Map<String, MeetsEvaluator>)in.readObject();
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
            this.cache = new HashMap<String, MeetsEvaluator>();
        }
        String key = isNegated + ":" + parameterText;
        MeetsEvaluator eval = this.cache.get( key );
        if ( eval == null ) {
            eval = new MeetsEvaluator( type,
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
     * Implements the 'meets' evaluator itself
     */
    public static class MeetsEvaluator extends BaseEvaluator {
		private static final long serialVersionUID = 9091548399308812447L;

		private long                  finalRange;

        public MeetsEvaluator(){
        }

        public MeetsEvaluator(final ValueType type,
                              final boolean isNegated,
                              final String parameters) {
            super( type,
                   isNegated ? MEETS_NOT : MEETS );
            this.parseParameters( parameters );
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            super.readExternal(in);
            finalRange   = in.readLong();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            super.writeExternal(out);
            out.writeLong(finalRange);
        }

        @Override
        public Object prepareObject(InternalFactHandle handle) {
            return handle;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            throw new RuntimeDroolsException( "The 'meets' operator can only be used to compare one event to another, and never to compare to literal constraints." );
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                final VariableContextEntry context,
                final Object left) {
			if ( context.rightNull ) {
			return false;
			}
			long leftStartTS = ((EventFactHandle) left ).getStartTimestamp();
			long dist = Math.abs(leftStartTS -
						((EventFactHandle)((ObjectVariableContextEntry) context).right).getEndTimestamp());
			return this.getOperator().isNegated() ^ ( ((EventFactHandle)((ObjectVariableContextEntry) context).right).getStartTimestamp() <= leftStartTS
					&& dist <= this.finalRange );
		}

		public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
			               final VariableContextEntry context,
			               final Object right) {
			if ( context.extractor.isNullValue( workingMemory,
			                     right ) ) {
			return false;
			}
			long leftStartTS = ((EventFactHandle) ((ObjectVariableContextEntry) context).left).getStartTimestamp();
			long dist = Math.abs(leftStartTS - ((EventFactHandle) right ).getEndTimestamp());
			return this.getOperator().isNegated() ^ ( ((EventFactHandle) right ).getStartTimestamp() <= leftStartTS && dist <= this.finalRange);
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
			long obj2StartTS = ((EventFactHandle) object2 ).getStartTimestamp();
			long dist = Math.abs(obj2StartTS - ((EventFactHandle) object1 ).getEndTimestamp());
			return this.getOperator().isNegated() ^ ( ((EventFactHandle) object1 ).getStartTimestamp() <= obj2StartTS && dist <= this.finalRange);
		}

        public String toString() {
            return "meets[" + finalRange + "]";
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
            final MeetsEvaluator other = (MeetsEvaluator) obj;
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
                // exact meet
                this.finalRange = 0;
                return;
            }

            try {
                String[] ranges = parameters.split( "," );
                if ( ranges.length == 1 ) {
                	// limit of tolerance for overlap or gap, respectively
                    this.finalRange = Long.parseLong( ranges[0] );
                } else {
                    throw new RuntimeDroolsException( "[Meets Evaluator]: Not possible to parse parameters: '" + parameters + "'" );
                }
            } catch ( NumberFormatException e ) {
                throw new RuntimeDroolsException( "[Meets Evaluator]: Not possible to parse parameters: '" + parameters + "'",
                                                  e );
            }
        }

    }

}
