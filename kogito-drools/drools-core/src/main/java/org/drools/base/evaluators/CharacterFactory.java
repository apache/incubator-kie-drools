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

public class CharacterFactory implements EvaluatorFactory {
    private static EvaluatorFactory INSTANCE = new CharacterFactory();
    
    private CharacterFactory() {
        
    }
    
    public static EvaluatorFactory getInstance() {
        if ( INSTANCE == null ) {
            INSTANCE = new CharacterFactory();
        }
        return INSTANCE;
    }

    public Evaluator getEvaluator(final Operator operator) {
        if ( operator == Operator.EQUAL ) {
            return CharacterEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.NOT_EQUAL ) {
            return CharacterNotEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.LESS ) {
            return CharacterLessEvaluator.INSTANCE;
        } else if ( operator == Operator.LESS_OR_EQUAL ) {
            return CharacterLessOrEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.GREATER ) {
            return CharacterGreaterEvaluator.INSTANCE;
        } else if ( operator == Operator.GREATER_OR_EQUAL ) {
            return CharacterGreaterOrEqualEvaluator.INSTANCE;
        }  else {
            throw new RuntimeException( "Operator '" + operator + "' does not exist for CharacterEvaluator" );
        }    
    }


    static class CharacterEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new CharacterEqualEvaluator();

        private CharacterEqualEvaluator() {
            super( ValueType.CHAR_TYPE,
                   Operator.EQUAL );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
            if ( object1 == null ) {
                return object2 == null;
            }
            return ((Character) object1).equals( object2 );
        }

        public String toString() {
            return "Character ==";
        }
    }

    static class CharacterNotEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new CharacterNotEqualEvaluator();

        private CharacterNotEqualEvaluator() {
            super( ValueType.CHAR_TYPE,
                   Operator.NOT_EQUAL );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
            if ( object1 == null ) {
                return object2 != null;
            }
            return !((Character) object1).equals( object2 );
        }

        public String toString() {
            return "Character !=";
        }
    }

    static class CharacterLessEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new CharacterLessEvaluator();

        private CharacterLessEvaluator() {
            super( ValueType.CHAR_TYPE,
                   Operator.LESS );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
            return ((Character) object1).charValue() < ((Character) object2).charValue();
        }

        public String toString() {
            return "Character <";
        }
    }

    static class CharacterLessOrEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new CharacterLessOrEqualEvaluator();

        private CharacterLessOrEqualEvaluator() {
            super( ValueType.CHAR_TYPE,
                   Operator.LESS_OR_EQUAL );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
            return ((Character) object1).charValue() <= ((Character) object2).charValue();
        }

        public String toString() {
            return "Character <=";
        }
    }

    static class CharacterGreaterEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new CharacterGreaterEvaluator();

        private CharacterGreaterEvaluator() {
            super( ValueType.CHAR_TYPE,
                   Operator.GREATER );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
            return ((Character) object1).charValue() > ((Character) object2).charValue();
        }

        public String toString() {
            return "Character >";
        }
    }

    static class CharacterGreaterOrEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long      serialVersionUID = 320;
        private final static Evaluator INSTANCE         = new CharacterGreaterOrEqualEvaluator();

        private CharacterGreaterOrEqualEvaluator() {
            super( ValueType.CHAR_TYPE,
                   Operator.GREATER_OR_EQUAL );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
            return ((Character) object1).charValue() >= ((Character) object2).charValue();
        }

        public String toString() {
            return "Character >=";
        }
    }

}