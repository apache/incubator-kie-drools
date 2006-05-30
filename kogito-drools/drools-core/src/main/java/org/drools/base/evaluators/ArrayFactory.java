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

import java.util.Arrays;

import org.drools.base.BaseEvaluator;
import org.drools.spi.Evaluator;

/**
 * For handling simple (non collection) array types.
 * @author Michael Neale
 */
public class ArrayFactory {

    public static Evaluator getArrayEvaluator(final int operator) {
        switch ( operator ) {
            case Evaluator.EQUAL :
                return ArrayEqualEvaluator.INSTANCE;
            case Evaluator.NOT_EQUAL :
                return ArrayNotEqualEvaluator.INSTANCE;
            case Evaluator.CONTAINS :
                return ArrayContainsEvaluator.INSTANCE;
            default :
                throw new RuntimeException( "Operator '" + operator + "' does not exist for ArrayEvaluator" );
        }
    }

    static class ArrayEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = -3988506265461766585L;
        public final static Evaluator INSTANCE         = new ArrayEqualEvaluator();

        private ArrayEqualEvaluator() {
            super( Evaluator.ARRAY_TYPE,
                   Evaluator.EQUAL );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
            return object1.equals( object2 );
        }

        public String toString() {
            return "Array ==";
        }
    }

    static class ArrayNotEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 4021357517502692236L;
        public final static Evaluator INSTANCE         = new ArrayNotEqualEvaluator();

        private ArrayNotEqualEvaluator() {
            super( Evaluator.ARRAY_TYPE,
                   Evaluator.NOT_EQUAL );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
            return !Arrays.equals( (Object[]) object1,
                                   (Object[]) object2 );
        }

        public String toString() {
            return "Object !=";
        }
    }

    static class ArrayContainsEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = -4541491453186891644L;
        public final static Evaluator INSTANCE         = new ArrayContainsEvaluator();

        private ArrayContainsEvaluator() {
            super( Evaluator.ARRAY_TYPE,
                   Evaluator.CONTAINS );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
            if ( object2 == null ) {
                return false;
            }
            if ( Arrays.binarySearch( (Object[]) object1,
                                      object2 ) == -1 ) {
                return false;
            } else {
                return true;
            }
        }

        public String toString() {
            return "Array contains";
        }
    }

}