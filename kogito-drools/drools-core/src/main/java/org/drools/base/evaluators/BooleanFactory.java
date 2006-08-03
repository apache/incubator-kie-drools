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

public class BooleanFactory implements EvaluatorFactory {
    private static EvaluatorFactory INSTANCE = new BooleanFactory();
    
    private BooleanFactory() {
        
    }
    
    public static EvaluatorFactory getInstance() {
        if ( INSTANCE == null ) {
            INSTANCE = new BooleanFactory();
        }
        return INSTANCE;
    }
    
    public Evaluator getEvaluator(final Operator operator) {
        if ( operator == Operator.EQUAL ) {
            return BooleanEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.NOT_EQUAL ) {
            return BooleanNotEqualEvaluator.INSTANCE;
        }  else {
            throw new RuntimeException( "Operator '" + operator + "' does not exist for BooleanEvaluator" );
        }    
    }
    

    static class BooleanEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long      serialVersionUID = 320;
        private final static Evaluator INSTANCE         = new BooleanEqualEvaluator();

        private BooleanEqualEvaluator() {
            super( ValueType.BOOLEAN_TYPE,
                   Operator.EQUAL );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
            if ( object1 == null ) {
                return object2 == null;
            }
            return ((Boolean) object1).equals( object2 );
        }

        public String toString() {
            return "Boolean ==";
        }
    }

    static class BooleanNotEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new BooleanNotEqualEvaluator();

        private BooleanNotEqualEvaluator() {
            super( ValueType.BOOLEAN_TYPE,
                   Operator.NOT_EQUAL );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
            if ( object1 == null ) {
                return object2 != null;
            }
            return !((Boolean) object1).equals( object2 );
        }

        public String toString() {
            return "Boolean !=";
        }
    }

}