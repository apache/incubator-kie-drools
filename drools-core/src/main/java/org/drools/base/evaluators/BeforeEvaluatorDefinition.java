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
 * The implementation of the 'before' evaluator definition
 *
 * @author mgroch
 */
public class BeforeEvaluatorDefinition
    implements
    EvaluatorDefinition {

    public static final Operator  BEFORE       = Operator.addOperatorToRegistry( "before",
                                                                                  false );
    public static final Operator  NOT_BEFORE   = Operator.addOperatorToRegistry( "before",
                                                                                  true );

    private static final String[] SUPPORTED_IDS = { BEFORE.getOperatorString() };

    private Map<String, BeforeEvaluator> cache        = Collections.emptyMap();

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        cache  = (Map<String, BeforeEvaluator>)in.readObject();
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
            this.cache = new HashMap<String, BeforeEvaluator>();
        }
        String key = isNegated + ":" + parameterText;
        BeforeEvaluator eval = this.cache.get( key );
        if ( eval == null ) {
            eval = new BeforeEvaluator( type,
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
     * Implements the 'before' evaluator itself
     */
    public static class BeforeEvaluator extends BaseEvaluator {
		private static final long serialVersionUID = -4778826341073034320L;

		private long                  initRange;
        private long                  finalRange;

        public BeforeEvaluator() {
        }

        public BeforeEvaluator(final ValueType type,
                              final boolean isNegated,
                              final String parameters) {
            super( type,
                   isNegated ? NOT_BEFORE : BEFORE );
            this.parseParameters( parameters );
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            super.readExternal(in);
            initRange    = in.readLong();
            finalRange   = in.readLong();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            super.writeExternal(out);
            out.writeLong(initRange);
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
            throw new RuntimeDroolsException( "The 'before' operator can only be used to compare one event to another, and never to compare to literal constraints." );
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context,
                                           final Object left) {
            if ( context.rightNull ) {
                return false;
            }
            long dist = ((EventFactHandle) left ).getStartTimestamp() -
            			((EventFactHandle)((ObjectVariableContextEntry) context).right).getEndTimestamp();
            return this.getOperator().isNegated() ^ (dist >= this.initRange && dist <= this.finalRange);
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context,
                                          final Object right) {
            if ( context.extractor.isNullValue( workingMemory,
                                                right ) ) {
                return false;
            }
            long dist = ((EventFactHandle) ((ObjectVariableContextEntry) context).left).getStartTimestamp() -
            			((EventFactHandle) right ).getEndTimestamp();

            return this.getOperator().isNegated() ^ (  dist >= this.initRange && dist <= this.finalRange );
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
            long dist = ((EventFactHandle) object2 ).getStartTimestamp() - ((EventFactHandle) object1 ).getEndTimestamp();
            return this.getOperator().isNegated() ^ (  dist >= this.initRange && dist <= this.finalRange ) ;
        }

        public String toString() {
            return this.getOperator().toString() + "[" + initRange + ", " + finalRange + "]";
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int PRIME = 31;
            int result = super.hashCode();
            result = PRIME * result + (int) (finalRange ^ (finalRange >>> 32));
            result = PRIME * result + (int) (initRange ^ (initRange >>> 32));
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
            final BeforeEvaluator other = (BeforeEvaluator) obj;
            return finalRange == other.finalRange && initRange == other.initRange;
        }

        /**
         * This methods tries to parse the string of parameters to customize
         * the evaluator.
         *
         * @param parameters
         */
        private void parseParameters(String parameters) {
            if ( parameters == null || parameters.trim().length() == 0 ) {
                // open bounded range
                this.initRange = 1;
                this.finalRange = Long.MAX_VALUE;
                return;
            }

            try {
                String[] ranges = parameters.split( "," );
                if ( ranges.length == 1 ) {
                    // deterministic point in time
                    this.initRange = Long.parseLong( ranges[0] );
                    this.finalRange = this.initRange;
                } else if ( ranges.length == 2 ) {
                    // regular range
                    this.initRange = Long.parseLong( ranges[0] );
                    this.finalRange = Long.parseLong( ranges[1] );
                } else {
                    throw new RuntimeDroolsException( "[Before Evaluator]: Not possible to parse parameters: '" + parameters + "'" );
                }
            } catch ( NumberFormatException e ) {
                throw new RuntimeDroolsException( "[Before Evaluator]: Not possible to parse parameters: '" + parameters + "'",
                                                  e );
            }
        }

    }

}
