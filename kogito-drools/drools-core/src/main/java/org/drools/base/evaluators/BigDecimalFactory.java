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

import java.math.BigDecimal;

import org.drools.base.BaseEvaluator;
import org.drools.base.ValueType;
import org.drools.spi.Evaluator;

public class BigDecimalFactory implements EvaluatorFactory {
    private static EvaluatorFactory INSTANCE = new BigDecimalFactory();
    
    private BigDecimalFactory() {
        
    }
    
    public static EvaluatorFactory getInstance() {
        if ( INSTANCE == null ) {
            INSTANCE = new BigDecimalFactory();
        }
        return INSTANCE;
    }

    public Evaluator getEvaluator(final Operator operator) {
        if ( operator == Operator.EQUAL ) {
            return BigDecimalEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.NOT_EQUAL ) {
            return BigDecimalNotEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.LESS ) {
            return BigDecimalLessEvaluator.INSTANCE;
        } else if ( operator == Operator.LESS_OR_EQUAL ) {
            return BigDecimalLessOrEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.GREATER ) {
            return BigDecimalGreaterEvaluator.INSTANCE;
        } else if ( operator == Operator.GREATER_OR_EQUAL ) {
            return BigDecimalGreaterOrEqualEvaluator.INSTANCE;
        }  else {
            throw new RuntimeException( "Operator '" + operator + "' does not exist for BigDecimalEvaluator" );
        }
    }

    static class BigDecimalEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new BigDecimalEqualEvaluator();

        private BigDecimalEqualEvaluator() {
            super( ValueType.BIG_DECIMAL_TYPE,
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
            return "BigDecimal ==";
        }
    }

    static class BigDecimalNotEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new BigDecimalNotEqualEvaluator();

        private BigDecimalNotEqualEvaluator() {
            super( ValueType.BIG_DECIMAL_TYPE,
                   Operator.NOT_EQUAL );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
            if ( object1 == null ) {
                return object2 != null;
            }
            return !((BigDecimal) object1).equals( object2 );
        }

        public String toString() {
            return "BigDecimal !=";
        }
    }

    static class BigDecimalLessEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new BigDecimalLessEvaluator();

        private BigDecimalLessEvaluator() {
            super( ValueType.BIG_DECIMAL_TYPE,
                   Operator.LESS );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
        	return ((BigDecimal) object1).compareTo( (BigDecimal) object2) < 0;            
        }

        public String toString() {
            return "BigDecimal <";
        }
    }

    static class BigDecimalLessOrEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new BigDecimalLessOrEqualEvaluator();

        private BigDecimalLessOrEqualEvaluator() {
            super( ValueType.BIG_DECIMAL_TYPE,
                   Operator.LESS_OR_EQUAL );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
        	return ((BigDecimal) object1).compareTo((BigDecimal)object2) <= 0;            
        }

        public String toString() {
            return "BigDecimal <=";
        }
    }

    static class BigDecimalGreaterEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new BigDecimalGreaterEvaluator();

        private BigDecimalGreaterEvaluator() {
            super( ValueType.BIG_DECIMAL_TYPE,
                   Operator.GREATER );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
        	return ((BigDecimal) object1).compareTo((BigDecimal)object2) > 0;               
        }

        public String toString() {
            return "BigDecimal >";
        }
    }

    static class BigDecimalGreaterOrEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long      serialVersionUID = 320;
        private final static Evaluator INSTANCE         = new BigDecimalGreaterOrEqualEvaluator();

        private BigDecimalGreaterOrEqualEvaluator() {
            super( ValueType.BIG_DECIMAL_TYPE,
                   Operator.GREATER_OR_EQUAL );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
        	return ((BigDecimal) object1).compareTo((BigDecimal)object2) >= 0;   
        }

        public String toString() {
            return "BigDecimal >=";
        }
    }
}