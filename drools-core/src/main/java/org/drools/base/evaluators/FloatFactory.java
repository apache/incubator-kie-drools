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

public class FloatFactory {

    public static Evaluator getFloatEvaluator(final int operator) {
        switch ( operator ) {
            case Evaluator.EQUAL :
                return FloatEqualEvaluator.INSTANCE;
            case Evaluator.NOT_EQUAL :
                return FloatNotEqualEvaluator.INSTANCE;
            case Evaluator.LESS :
                return FloatLessEvaluator.INSTANCE;
            case Evaluator.LESS_OR_EQUAL :
                return FloatLessOrEqualEvaluator.INSTANCE;
            case Evaluator.GREATER :
                return FloatGreaterEvaluator.INSTANCE;
            case Evaluator.GREATER_OR_EQUAL :
                return FloatGreaterOrEqualEvaluator.INSTANCE;
            default :
                throw new RuntimeException( "Operator '" + operator + "' does not exist for FloatEvaluator" );
        }
    }

    static class FloatEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = -3295563005669423883L;
        public final static Evaluator INSTANCE         = new FloatEqualEvaluator();

        private FloatEqualEvaluator() {
            super( Evaluator.FLOAT_TYPE,
                   Evaluator.EQUAL );
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
        private static final long     serialVersionUID = -4852271063945330337L;
        public final static Evaluator INSTANCE         = new FloatNotEqualEvaluator();

        private FloatNotEqualEvaluator() {
            super( Evaluator.FLOAT_TYPE,
                   Evaluator.NOT_EQUAL );
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
        private static final long     serialVersionUID = -4971007931169565583L;
        public final static Evaluator INSTANCE         = new FloatLessEvaluator();

        private FloatLessEvaluator() {
            super( Evaluator.FLOAT_TYPE,
                   Evaluator.LESS );
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
        private static final long     serialVersionUID = 8475866839302691518L;
        public final static Evaluator INSTANCE         = new FloatLessOrEqualEvaluator();

        private FloatLessOrEqualEvaluator() {
            super( Evaluator.FLOAT_TYPE,
                   Evaluator.LESS_OR_EQUAL );
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
        private static final long     serialVersionUID = 7121251641514162807L;
        public final static Evaluator INSTANCE         = new FloatGreaterEvaluator();

        private FloatGreaterEvaluator() {
            super( Evaluator.FLOAT_TYPE,
                   Evaluator.GREATER );
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
        private static final long      serialVersionUID = -6885383763349349798L;
        private final static Evaluator INSTANCE         = new FloatGreaterOrEqualEvaluator();

        private FloatGreaterOrEqualEvaluator() {
            super( Evaluator.FLOAT_TYPE,
                   Evaluator.GREATER_OR_EQUAL );
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