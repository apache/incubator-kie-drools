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

import org.drools.base.BaseEvaluator;
import org.drools.base.ValueType;
import org.drools.base.evaluators.ShortFactory.ShortEqualEvaluator;
import org.drools.base.evaluators.ShortFactory.ShortGreaterEvaluator;
import org.drools.base.evaluators.ShortFactory.ShortGreaterOrEqualEvaluator;
import org.drools.base.evaluators.ShortFactory.ShortLessEvaluator;
import org.drools.base.evaluators.ShortFactory.ShortLessOrEqualEvaluator;
import org.drools.base.evaluators.ShortFactory.ShortNotEqualEvaluator;
import org.drools.spi.Evaluator;

public class IntegerFactory implements EvaluatorFactory {
    private static EvaluatorFactory INSTANCE = new IntegerFactory();
    
    private IntegerFactory() {
        
    }
    
    public static EvaluatorFactory getInstance() {
        if ( INSTANCE == null ) {
            INSTANCE = new IntegerFactory();
        }
        return INSTANCE;
    }

    public Evaluator getEvaluator(final Operator operator) {
        if ( operator == Operator.EQUAL ) {
            return IntegerEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.NOT_EQUAL ) {
            return IntegerNotEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.LESS ) {
            return IntegerLessEvaluator.INSTANCE;
        } else if ( operator == Operator.LESS_OR_EQUAL ) {
            return IntegerLessOrEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.GREATER ) {
            return IntegerGreaterEvaluator.INSTANCE;
        } else if ( operator == Operator.GREATER_OR_EQUAL ) {
            return IntegerGreaterOrEqualEvaluator.INSTANCE;
        }  else {
            throw new RuntimeException( "Operator '" + operator + "' does not exist for IntegerEvaluator" );
        }    
    }

    static class IntegerEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new IntegerEqualEvaluator();

        private IntegerEqualEvaluator() {
            super( ValueType.INTEGER_TYPE,
                   Operator.EQUAL );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {             
            if ( object1 == null ) {
                return object2 == null;
            }
            return object1.equals( object2 );
        }

        public String toString() {
            return "Integer ==";
        }
    }

    static class IntegerNotEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new IntegerNotEqualEvaluator();

        private IntegerNotEqualEvaluator() {
            super( ValueType.INTEGER_TYPE,
                   Operator.NOT_EQUAL );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {             
            if ( object1 == null ) {
                return (object2 != null);
            }
            return !object1.equals( object2 );
        }

        public String toString() {
            return "Integer !=";
        }
    }

    static class IntegerLessEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new IntegerLessEvaluator();

        private IntegerLessEvaluator() {
            super( ValueType.INTEGER_TYPE,
                   Operator.LESS );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
            return ((Number) object1).intValue() < ((Number) object2).intValue();
        }

        public String toString() {
            return "Integer <";
        }
    }

    static class IntegerLessOrEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new IntegerLessOrEqualEvaluator();

        private IntegerLessOrEqualEvaluator() {
            super( ValueType.INTEGER_TYPE,
                   Operator.LESS_OR_EQUAL );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
            return ((Number) object1).intValue() <= ((Number) object2).intValue();
        }

        public String toString() {
            return "Integer <=";
        }
    }

    static class IntegerGreaterEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new IntegerGreaterEvaluator();

        private IntegerGreaterEvaluator() {
            super( ValueType.INTEGER_TYPE,
                   Operator.GREATER );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
            return ((Number) object1).intValue() > ((Number) object2).intValue();
        }

        public String toString() {
            return "Integer >";
        }
    }

    static class IntegerGreaterOrEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long      serialVersionUID = 320;
        private final static Evaluator INSTANCE         = new IntegerGreaterOrEqualEvaluator();

        private IntegerGreaterOrEqualEvaluator() {
            super( ValueType.INTEGER_TYPE,
                   Operator.GREATER_OR_EQUAL );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
            return ((Number) object1).intValue() >= ((Number) object2).intValue();
        }

        public String toString() {
            return "Integer >=";
        }
    }

}