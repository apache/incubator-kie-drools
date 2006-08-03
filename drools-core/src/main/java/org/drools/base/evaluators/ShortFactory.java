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
import org.drools.base.evaluators.DoubleFactory.DoubleEqualEvaluator;
import org.drools.base.evaluators.DoubleFactory.DoubleGreaterEvaluator;
import org.drools.base.evaluators.DoubleFactory.DoubleGreaterOrEqualEvaluator;
import org.drools.base.evaluators.DoubleFactory.DoubleLessEvaluator;
import org.drools.base.evaluators.DoubleFactory.DoubleLessOrEqualEvaluator;
import org.drools.base.evaluators.DoubleFactory.DoubleNotEqualEvaluator;
import org.drools.spi.Evaluator;

public class ShortFactory implements EvaluatorFactory {
    private static EvaluatorFactory INSTANCE = new ShortFactory();
    
    private ShortFactory() {
        
    }
    
    public static EvaluatorFactory getInstance() {
        if ( INSTANCE == null ) {
            INSTANCE = new ShortFactory();
        }
        return INSTANCE;
    }        

    public Evaluator getEvaluator(final Operator operator) {
        if ( operator == Operator.EQUAL ) {
            return ShortEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.NOT_EQUAL ) {
            return ShortNotEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.LESS ) {
            return ShortLessEvaluator.INSTANCE;
        } else if ( operator == Operator.LESS_OR_EQUAL ) {
            return ShortLessOrEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.GREATER ) {
            return ShortGreaterEvaluator.INSTANCE;
        } else if ( operator == Operator.GREATER_OR_EQUAL ) {
            return ShortGreaterOrEqualEvaluator.INSTANCE;
        }  else {
            throw new RuntimeException( "Operator '" + operator + "' does not exist for ShortEvaluator" );
        }    
    }
    
    static class ShortEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long serialVersionUID = 320;
        private static Evaluator  INSTANCE = new ShortEqualEvaluator();

        private ShortEqualEvaluator() {
            super( ValueType.SHORT_TYPE,
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
            return "Short ==";
        }
    }

    static class ShortNotEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long serialVersionUID = 320;
        private static Evaluator  INSTANCE = new ShortNotEqualEvaluator();
        
        private ShortNotEqualEvaluator() {
            super( ValueType.SHORT_TYPE,
                   Operator.NOT_EQUAL );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
            if ( object1 == null ) {
                return !(object2 == null);
            }
            return !((Number) object1).equals( object2 );
        }

        public String toString() {
            return "Short !=";
        }
    }

    static class ShortLessEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long serialVersionUID = 320;
        private static Evaluator  INSTANCE = new ShortLessEvaluator();

        private ShortLessEvaluator() {
            super( ValueType.SHORT_TYPE,
                   Operator.LESS );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
            return ((Number) object1).shortValue() < ((Number) object2).shortValue();
        }

        public String toString() {
            return "Short <";
        }
    }

    static class ShortLessOrEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long serialVersionUID = 320;
        private static Evaluator  INSTANCE = new ShortLessOrEqualEvaluator();

        private ShortLessOrEqualEvaluator() {
            super( ValueType.SHORT_TYPE,
                   Operator.LESS_OR_EQUAL );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
            return ((Number) object1).shortValue() <= ((Number) object2).shortValue();
        }

        public String toString() {
            return "Boolean <=";
        }
    }

    static class ShortGreaterEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long serialVersionUID = 320;
        private static Evaluator  INSTANCE = new ShortGreaterEvaluator();

        private ShortGreaterEvaluator() {
            super( ValueType.SHORT_TYPE,
                   Operator.GREATER );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
            return ((Number) object1).shortValue() > ((Number) object2).shortValue();
        }

        public String toString() {
            return "Short >";
        }
    }

    static class ShortGreaterOrEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long serialVersionUID = 320;
        private static Evaluator  INSTANCE = new ShortGreaterOrEqualEvaluator();

        private ShortGreaterOrEqualEvaluator() {
            super( ValueType.SHORT_TYPE,
                   Operator.GREATER_OR_EQUAL );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
            return ((Number) object1).shortValue() >= ((Number) object2).shortValue();
        }

        public String toString() {
            return "Short >=";
        }
    }

}