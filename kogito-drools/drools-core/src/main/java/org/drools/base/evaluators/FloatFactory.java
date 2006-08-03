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

public class FloatFactory implements EvaluatorFactory {
    private static EvaluatorFactory INSTANCE = new FloatFactory();
    
    private FloatFactory() {
        
    }
    
    public static EvaluatorFactory getInstance() {
        if ( INSTANCE == null ) {
            INSTANCE = new FloatFactory();
        }
        return INSTANCE;
    }

    public Evaluator getEvaluator(final Operator operator) {
        if ( operator == Operator.EQUAL ) {
            return FloatEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.NOT_EQUAL ) {
            return FloatNotEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.LESS ) {
            return FloatLessEvaluator.INSTANCE;
        } else if ( operator == Operator.LESS_OR_EQUAL ) {
            return FloatLessOrEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.GREATER ) {
            return FloatGreaterEvaluator.INSTANCE;
        } else if ( operator == Operator.GREATER_OR_EQUAL ) {
            return FloatGreaterOrEqualEvaluator.INSTANCE;
        }  else {
            throw new RuntimeException( "Operator '" + operator + "' does not exist for FloatEvaluator" );
        }    
    }


    static class FloatEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new FloatEqualEvaluator();

        private FloatEqualEvaluator() {
            super( ValueType.FLOAT_TYPE,
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
            return "Float ==";
        }
    }

    static class FloatNotEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new FloatNotEqualEvaluator();

        private FloatNotEqualEvaluator() {
            super( ValueType.FLOAT_TYPE,
                   Operator.NOT_EQUAL );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
            if ( object1 == null ) {
                return object2 != null;
            }
            return !((Number) object1).equals( object2 );
        }

        public String toString() {
            return "Float !=";
        }
    }

    static class FloatLessEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new FloatLessEvaluator();

        private FloatLessEvaluator() {
            super( ValueType.FLOAT_TYPE,
                   Operator.LESS );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
            return ((Number) object1).floatValue() < ((Number) object2).floatValue();
        }

        public String toString() {
            return "Float <";
        }
    }

    static class FloatLessOrEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new FloatLessOrEqualEvaluator();

        private FloatLessOrEqualEvaluator() {
            super( ValueType.FLOAT_TYPE,
                   Operator.LESS_OR_EQUAL );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
            return ((Number) object1).floatValue() <= ((Number) object2).floatValue();
        }

        public String toString() {
            return "Float <=";
        }
    }

    static class FloatGreaterEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new FloatGreaterEvaluator();

        private FloatGreaterEvaluator() {
            super( ValueType.FLOAT_TYPE,
                   Operator.GREATER );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
            return ((Number) object1).floatValue() > ((Number) object2).floatValue();
        }

        public String toString() {
            return "Float >";
        }
    }

    static class FloatGreaterOrEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long      serialVersionUID = 320;
        private final static Evaluator INSTANCE         = new FloatGreaterOrEqualEvaluator();

        private FloatGreaterOrEqualEvaluator() {
            super( ValueType.FLOAT_TYPE,
                   Operator.GREATER_OR_EQUAL );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
            return ((Number) object1).floatValue() >= ((Number) object2).floatValue();
        }

        public String toString() {
            return "Float >=";
        }
    }
}