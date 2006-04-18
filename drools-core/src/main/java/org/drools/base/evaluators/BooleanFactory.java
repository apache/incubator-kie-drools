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

public class BooleanFactory {

    public static Evaluator getBooleanEvaluator(int operator) {
        switch ( operator ) {
            case Evaluator.EQUAL :
                return BooleanEqualEvaluator.INSTANCE;
            case Evaluator.NOT_EQUAL :
                return BooleanNotEqualEvaluator.INSTANCE;
            default :
                throw new RuntimeException( "Operator '" + operator + "' does not exist for BooleanEvaluator" );
        }
    }

    static class BooleanEqualEvaluator extends BaseEvaluator {
        private final static Evaluator INSTANCE = new BooleanEqualEvaluator();

        private BooleanEqualEvaluator() {
            super( Evaluator.BOOLEAN_TYPE,
                   Evaluator.EQUAL );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            if ( object1 == null ) return object2 == null;
            return ((Boolean) object1).equals( object2 );
        }

        public String toString() {
            return "Boolean ==";
        }
    }

    static class BooleanNotEqualEvaluator extends BaseEvaluator {
        public final static Evaluator INSTANCE = new BooleanNotEqualEvaluator();

        private BooleanNotEqualEvaluator() {
            super( Evaluator.BOOLEAN_TYPE,
                   Evaluator.NOT_EQUAL );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            if ( object1 == null ) return object2 != null;
            return !((Boolean) object1).equals( object2 );
        }

        public String toString() {
            return "Boolean !=";
        }
    }

}