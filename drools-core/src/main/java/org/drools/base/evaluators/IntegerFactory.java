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
import org.drools.spi.Evaluator;

public class IntegerFactory {
    public static Evaluator getIntegerEvaluator(int operator) {
        switch ( operator ) {
            case Evaluator.EQUAL :
                return IntegerEqualEvaluator.INSTANCE;
            case Evaluator.NOT_EQUAL :
                return IntegerNotEqualEvaluator.INSTANCE;
            case Evaluator.LESS :
                return IntegerLessEvaluator.INSTANCE;
            case Evaluator.LESS_OR_EQUAL :
                return IntegerLessOrEqualEvaluator.INSTANCE;
            case Evaluator.GREATER :
                return IntegerGreaterEvaluator.INSTANCE;
            case Evaluator.GREATER_OR_EQUAL :
                return IntegerGreaterOrEqualEvaluator.INSTANCE;
            default :
                throw new RuntimeException( "Operator '" + operator + "' does not exist for IntegerEvaluator" );
        }
    }

    static class IntegerEqualEvaluator extends BaseEvaluator {
        public final static Evaluator INSTANCE = new IntegerEqualEvaluator();

        private IntegerEqualEvaluator() {
            super( Evaluator.INTEGER_TYPE,
                   Evaluator.EQUAL );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            if ( object1 == null ) return object2 == null;
            return ((Number) object1).equals( object2 );
        }

        public String toString() {
            return "Integer ==";
        }
    }

    static class IntegerNotEqualEvaluator extends BaseEvaluator {
        public final static Evaluator INSTANCE = new IntegerNotEqualEvaluator();

        private IntegerNotEqualEvaluator() {
            super( Evaluator.INTEGER_TYPE,
                   Evaluator.NOT_EQUAL );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            if ( object1 == null ) return (object2 != null);
            return !((Number) object1).equals( object2 );
        }

        public String toString() {
            return "Integer !=";
        }
    }

    static class IntegerLessEvaluator extends BaseEvaluator {
        public final static Evaluator INSTANCE = new IntegerLessEvaluator();

        private IntegerLessEvaluator() {
            super( Evaluator.INTEGER_TYPE,
                   Evaluator.LESS );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            return ((Number) object1).intValue() < ((Number) object2).intValue();
        }

        public String toString() {
            return "Integer <";
        }
    }

    static class IntegerLessOrEqualEvaluator extends BaseEvaluator {
        public final static Evaluator INSTANCE = new IntegerLessOrEqualEvaluator();

        private IntegerLessOrEqualEvaluator() {
            super( Evaluator.INTEGER_TYPE,
                   Evaluator.LESS_OR_EQUAL );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            return ((Number) object1).intValue() <= ((Number) object2).intValue();
        }

        public String toString() {
            return "Integer <=";
        }
    }

    static class IntegerGreaterEvaluator extends BaseEvaluator {
        public final static Evaluator INSTANCE = new IntegerGreaterEvaluator();

        private IntegerGreaterEvaluator() {
            super( Evaluator.INTEGER_TYPE,
                   Evaluator.GREATER );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            return ((Number) object1).intValue() > ((Number) object2).intValue();
        }

        public String toString() {
            return "Integer >";
        }
    }

    static class IntegerGreaterOrEqualEvaluator extends BaseEvaluator {
        private final static Evaluator INSTANCE = new IntegerGreaterOrEqualEvaluator();

        private IntegerGreaterOrEqualEvaluator() {
            super( Evaluator.INTEGER_TYPE,
                   Evaluator.GREATER_OR_EQUAL );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            return ((Number) object1).intValue() >= ((Number) object2).intValue();
        }

        public String toString() {
            return "Integer >=";
        }
    }

}