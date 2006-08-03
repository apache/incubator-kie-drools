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

/**
 * This is the misc "bucket" evaluator factory for objects.
 * It is fairly limited in operations, 
 * and what operations are available are dependent on the exact type.
 * 
 * @author Michael Neale
 */
public class StringFactory implements EvaluatorFactory {
    private static EvaluatorFactory INSTANCE = new StringFactory();
    
    private StringFactory() {
        
    }
    
    public static EvaluatorFactory getInstance() {
        if ( INSTANCE == null ) {
            INSTANCE = new StringFactory();
        }
        return INSTANCE;
    }

    public Evaluator getEvaluator(final Operator operator) {
        if ( operator == Operator.EQUAL ) {
            return StringEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.NOT_EQUAL ) {
            return StringNotEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.MATCHES ) {
            return StringMatchesEvaluator.INSTANCE;
        }  else {
            throw new RuntimeException( "Operator '" + operator + "' does not exist for StringEvaluator" );
        }    
    }

    static class StringEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new StringEqualEvaluator();

        private StringEqualEvaluator() {
            super( ValueType.STRING_TYPE,
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
            return "String ==";
        }
    }

    static class StringNotEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new StringNotEqualEvaluator();

        private StringNotEqualEvaluator() {
            super( ValueType.STRING_TYPE,
                   Operator.NOT_EQUAL );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
            if ( object1 == null ) {
                return !(object2 == null);
            }

            return !object1.equals( object2 );
        }

        public String toString() {
            return "String !=";
        }
    }

    static class StringMatchesEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new StringMatchesEvaluator();

        private StringMatchesEvaluator() {
            super( ValueType.STRING_TYPE,
                   Operator.MATCHES );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
            final String pattern = (String) object2;
            final String target = (String) object1;

            if ( object1 == null ) {
                return false;
            }

            //TODO: possibly use a WeakHashMap cache of regex expressions
            //downside is could cause a lot of hashing if the patterns are dynamic
            //if the patterns are static, then it will not be a problem. Perhaps compiler can recognise patterns
            //in the input string using /pattern/ etc.. and precompile it, in which case object2 will be a Pattern.
            return target.matches( pattern );
        }

        public String toString() {
            return "String !=";
        }
    }

}