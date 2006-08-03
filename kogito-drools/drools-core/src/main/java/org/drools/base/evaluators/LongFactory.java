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

public class LongFactory implements EvaluatorFactory {
    private static EvaluatorFactory INSTANCE = new LongFactory();
    
    private LongFactory() {
        
    }
    
    public static EvaluatorFactory getInstance() {
        if ( INSTANCE == null ) {
            INSTANCE = new LongFactory();
        }
        return INSTANCE;
    }

    public Evaluator getEvaluator(final Operator operator) {
        if ( operator == Operator.EQUAL ) {
            return LongEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.NOT_EQUAL ) {
            return LongNotEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.LESS ) {
            return LongLessEvaluator.INSTANCE;
        } else if ( operator == Operator.LESS_OR_EQUAL ) {
            return LongLessOrEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.GREATER ) {
            return LongGreaterEvaluator.INSTANCE;
        } else if ( operator == Operator.GREATER_OR_EQUAL ) {
            return LongGreaterOrEqualEvaluator.INSTANCE;
        }  else {
            throw new RuntimeException( "Operator '" + operator + "' does not exist for LongEvaluator" );
        }    
    }


    static class LongEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new LongEqualEvaluator();

        private LongEqualEvaluator() {
            super( ValueType.LONG_TYPE,
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
            return "Long ==";
        }
    }

    static class LongNotEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new LongNotEqualEvaluator();

        private LongNotEqualEvaluator() {
            super( ValueType.LONG_TYPE,
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
            return "Long !=";
        }
    }

    static class LongLessEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new LongLessEvaluator();

        private LongLessEvaluator() {
            super( ValueType.LONG_TYPE,
                   Operator.LESS );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
            return ((Number) object1).longValue() < ((Number) object2).longValue();
        }

        public String toString() {
            return "Long <";
        }
    }

    static class LongLessOrEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new LongLessOrEqualEvaluator();

        private LongLessOrEqualEvaluator() {
            super( ValueType.LONG_TYPE,
                   Operator.LESS_OR_EQUAL );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
            return ((Number) object1).longValue() <= ((Number) object2).longValue();
        }

        public String toString() {
            return "Long <=";
        }
    }

    static class LongGreaterEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new LongGreaterEvaluator();

        private LongGreaterEvaluator() {
            super( ValueType.LONG_TYPE,
                   Operator.GREATER );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
            return ((Number) object1).longValue() > ((Number) object2).longValue();
        }

        public String toString() {
            return "Long >";
        }
    }

    static class LongGreaterOrEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long      serialVersionUID = 320;
        private final static Evaluator INSTANCE         = new LongGreaterOrEqualEvaluator();

        private LongGreaterOrEqualEvaluator() {
            super( ValueType.LONG_TYPE,
                   Operator.GREATER_OR_EQUAL );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
            return ((Number) object1).longValue() >= ((Number) object2).longValue();
        }

        public String toString() {
            return "Long >=";
        }
    }

}