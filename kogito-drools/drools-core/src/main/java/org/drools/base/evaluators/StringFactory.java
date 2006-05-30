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

/**
 * This is the misc "bucket" evaluator factory for objects.
 * It is fairly limited in operations, 
 * and what operations are available are dependent on the exact type.
 * 
 * @author Michael Neale
 */
public class StringFactory {

    public static Evaluator getStringEvaluator(final int operator) {
        switch ( operator ) {
            case Evaluator.EQUAL :
                return StringEqualEvaluator.INSTANCE;
            case Evaluator.NOT_EQUAL :
                return StringNotEqualEvaluator.INSTANCE;
            case Evaluator.MATCHES :
                return StringMatchesEvaluator.INSTANCE;
            default :
                throw new RuntimeException( "Operator '" + operator + "' does not exist for StringEvaluator" );
        }
    }

    static class StringEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 5282693491345148054L;
        public final static Evaluator INSTANCE         = new StringEqualEvaluator();

        private StringEqualEvaluator() {
            super( Evaluator.STRING_TYPE,
                   Evaluator.EQUAL );
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
        private static final long     serialVersionUID = -3385245390840913608L;
        public final static Evaluator INSTANCE         = new StringNotEqualEvaluator();

        private StringNotEqualEvaluator() {
            super( Evaluator.STRING_TYPE,
                   Evaluator.NOT_EQUAL );
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
        private static final long     serialVersionUID = 5934192092501066510L;
        public final static Evaluator INSTANCE         = new StringMatchesEvaluator();

        private StringMatchesEvaluator() {
            super( Evaluator.STRING_TYPE,
                   Evaluator.MATCHES );
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