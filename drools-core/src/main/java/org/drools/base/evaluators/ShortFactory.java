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

public class ShortFactory {
    public static Evaluator getShortEvaluator(int operator) {
        switch ( operator ) {
            case Evaluator.EQUAL :
                return ShortEqualEvaluator.getInstance();
            case Evaluator.NOT_EQUAL :
                return ShortNotEqualEvaluator.getInstance();
            case Evaluator.LESS :
                return ShortLessEvaluator.getInstance();
            case Evaluator.LESS_OR_EQUAL :
                return ShortLessOrEqualEvaluator.getInstance();
            case Evaluator.GREATER :
                return ShortGreaterEvaluator.getInstance();
            case Evaluator.GREATER_OR_EQUAL :
                return ShortGreaterOrEqualEvaluator.getInstance();
            default :
                throw new RuntimeException( "Operator '" + operator + "' does not exist for ShortEvaluator" );
        }
    }

    static class ShortEqualEvaluator extends BaseEvaluator {
        private static Evaluator INSTANCE;

        public static Evaluator getInstance() {
            if ( INSTANCE == null ) {
                INSTANCE = new ShortEqualEvaluator();
            }
            return INSTANCE;
        }

        private ShortEqualEvaluator() {
            super( Evaluator.SHORT_TYPE,
                   Evaluator.EQUAL );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            if ( object1 == null ) return object2 == null;
            return ((Number) object1).equals( object2 );
        }

        public String toString() {
            return "Short ==";
        }
    }

    static class ShortNotEqualEvaluator extends BaseEvaluator {
        private static Evaluator INSTANCE;

        public static Evaluator getInstance() {
            if ( INSTANCE == null ) {
                INSTANCE = new ShortNotEqualEvaluator();
            }
            return INSTANCE;
        }

        private ShortNotEqualEvaluator() {
            super( Evaluator.SHORT_TYPE,
                   Evaluator.NOT_EQUAL );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            if ( object1 == null ) return !(object2 == null);
            return !((Number) object1).equals( object2 );
        }

        public String toString() {
            return "Short !=";
        }
    }

    static class ShortLessEvaluator extends BaseEvaluator {
        private static Evaluator INSTANCE;

        public static Evaluator getInstance() {
            if ( INSTANCE == null ) {
                INSTANCE = new ShortLessEvaluator();
            }
            return INSTANCE;
        }

        private ShortLessEvaluator() {
            super( Evaluator.SHORT_TYPE,
                   Evaluator.LESS );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            return ((Number) object1).shortValue() < ((Number) object2).shortValue();
        }

        public String toString() {
            return "Short <";
        }
    }

    static class ShortLessOrEqualEvaluator extends BaseEvaluator {
        private static Evaluator INSTANCE;

        public static Evaluator getInstance() {
            if ( INSTANCE == null ) {
                INSTANCE = new ShortLessOrEqualEvaluator();
            }
            return INSTANCE;
        }

        private ShortLessOrEqualEvaluator() {
            super( Evaluator.SHORT_TYPE,
                   Evaluator.LESS_OR_EQUAL );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            return ((Number) object1).shortValue() <= ((Number) object2).shortValue();
        }

        public String toString() {
            return "Boolean <=";
        }
    }

    static class ShortGreaterEvaluator extends BaseEvaluator {
        private static Evaluator INSTANCE;

        public static Evaluator getInstance() {
            if ( INSTANCE == null ) {
                INSTANCE = new ShortGreaterEvaluator();
            }
            return INSTANCE;
        }

        private ShortGreaterEvaluator() {
            super( Evaluator.SHORT_TYPE,
                   Evaluator.GREATER );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            return ((Number) object1).shortValue() > ((Number) object2).shortValue();
        }

        public String toString() {
            return "Short >";
        }
    }

    static class ShortGreaterOrEqualEvaluator extends BaseEvaluator {
        private static Evaluator INSTANCE;

        public static Evaluator getInstance() {
            if ( INSTANCE == null ) {
                INSTANCE = new ShortGreaterOrEqualEvaluator();
            }
            return INSTANCE;
        }

        private ShortGreaterOrEqualEvaluator() {
            super( Evaluator.SHORT_TYPE,
                   Evaluator.GREATER_OR_EQUAL );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            return ((Number) object1).shortValue() >= ((Number) object2).shortValue();
        }

        public String toString() {
            return "Short >=";
        }
    }

}