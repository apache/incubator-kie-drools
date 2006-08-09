package org.drools.base.evaluators;

/*
 * Copyright 2005 JBoss Inc
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

import java.math.BigInteger;

import org.drools.base.BaseEvaluator;
import org.drools.base.ValueType;
import org.drools.spi.Evaluator;

public class BigIntegerFactory implements EvaluatorFactory {
    private static EvaluatorFactory INSTANCE = new BigIntegerFactory();
    
    private BigIntegerFactory() {
        
    }
    
    public static EvaluatorFactory getInstance() {
        if ( INSTANCE == null ) {
            INSTANCE = new BigIntegerFactory();
        }
        return INSTANCE;
    }

    public Evaluator getEvaluator(final Operator operator) {
        if ( operator == Operator.EQUAL ) {
            return BigIntegerEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.NOT_EQUAL ) {
            return BigIntegerNotEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.LESS ) {
            return BigIntegerLessEvaluator.INSTANCE;
        } else if ( operator == Operator.LESS_OR_EQUAL ) {
            return BigIntegerLessOrEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.GREATER ) {
            return BigIntegerGreaterEvaluator.INSTANCE;
        } else if ( operator == Operator.GREATER_OR_EQUAL ) {
            return BigIntegerGreaterOrEqualEvaluator.INSTANCE;
        }  else {
            throw new RuntimeException( "Operator '" + operator + "' does not exist for BigIntegerEvaluator" );
        }
    }

    static class BigIntegerEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new BigIntegerEqualEvaluator();

        private BigIntegerEqualEvaluator() {
            super( ValueType.BIG_INTEGER_TYPE,
                   Operator.EQUAL );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
            if ( object1 == null ) {
                return object2 == null;
            }
            return ((Number) object1).equals( object2 );
        }

        public String toString() {
            return "BigInteger ==";
        }
    }

    static class BigIntegerNotEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new BigIntegerNotEqualEvaluator();

        private BigIntegerNotEqualEvaluator() {
            super( ValueType.BIG_INTEGER_TYPE,
                   Operator.NOT_EQUAL );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
            if ( object1 == null ) {
                return object2 != null;
            }
            return !((BigInteger) object1).equals( object2 );
        }

        public String toString() {
            return "BigInteger !=";
        }
    }

    static class BigIntegerLessEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new BigIntegerLessEvaluator();

        private BigIntegerLessEvaluator() {
            super( ValueType.BIG_INTEGER_TYPE,
                   Operator.LESS );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
        	return ((BigInteger) object1).compareTo( (BigInteger) object2) < 0;            
        }

        public String toString() {
            return "BigInteger <";
        }
    }

    static class BigIntegerLessOrEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new BigIntegerLessOrEqualEvaluator();

        private BigIntegerLessOrEqualEvaluator() {
            super( ValueType.BIG_INTEGER_TYPE,
                   Operator.LESS_OR_EQUAL );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
        	return ((BigInteger) object1).compareTo((BigInteger)object2) <= 0;            
        }

        public String toString() {
            return "BigInteger <=";
        }
    }

    static class BigIntegerGreaterEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new BigIntegerGreaterEvaluator();

        private BigIntegerGreaterEvaluator() {
            super( ValueType.BIG_INTEGER_TYPE,
                   Operator.GREATER );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
        	return ((BigInteger) object1).compareTo((BigInteger)object2) > 0;               
        }

        public String toString() {
            return "BigInteger >";
        }
    }

    static class BigIntegerGreaterOrEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long      serialVersionUID = 320;
        private final static Evaluator INSTANCE         = new BigIntegerGreaterOrEqualEvaluator();

        private BigIntegerGreaterOrEqualEvaluator() {
            super( ValueType.BIG_INTEGER_TYPE,
                   Operator.GREATER_OR_EQUAL );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
        	return ((BigInteger) object1).compareTo((BigInteger)object2) >= 0;   
        }

        public String toString() {
            return "BigInteger >=";
        }
    }
}