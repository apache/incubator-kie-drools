package org.drools.base.evaluators;

import org.drools.base.BaseEvaluator;
import org.drools.spi.Evaluator;

public class CharacterFactory {
    
    public static Evaluator getCharacterEvaluator(int operator) {
        switch ( operator ) {
            case Evaluator.EQUAL :
                return CharacterEqualEvaluator.INSTANCE;
            case Evaluator.NOT_EQUAL :
                return CharacterNotEqualEvaluator.INSTANCE;
            case Evaluator.LESS :
                return CharacterLessEvaluator.INSTANCE;
            case Evaluator.LESS_OR_EQUAL :
                return CharacterLessOrEqualEvaluator.INSTANCE;
            case Evaluator.GREATER :
                return CharacterGreaterEvaluator.INSTANCE;
            case Evaluator.GREATER_OR_EQUAL :
                return CharacterGreaterOrEqualEvaluator.INSTANCE;
            default :
                throw new RuntimeException( "Operator '" + operator + "' does not exist for CharacterEvaluator" );
        }
    }

    static class CharacterEqualEvaluator extends BaseEvaluator {
        public final static Evaluator INSTANCE = new CharacterEqualEvaluator();

        private CharacterEqualEvaluator() {
            super( Evaluator.CHAR_TYPE,
                   Evaluator.EQUAL );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            if (object1 == null) return object2 == null;
            return ((Character) object1).equals( object2 );
        }
        
        public String toString() {
            return "Character ==";
        }         
    }

    static class CharacterNotEqualEvaluator extends BaseEvaluator {
        public final static Evaluator INSTANCE = new CharacterNotEqualEvaluator();

        private CharacterNotEqualEvaluator() {
            super( Evaluator.CHAR_TYPE,
                   Evaluator.NOT_EQUAL );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            if (object1 == null) return object2 != null;
            return !((Character) object1).equals(object2);
        }
        
        public String toString() {
            return "Character !=";
        }                 
    }

    static class CharacterLessEvaluator extends BaseEvaluator {
        public final static Evaluator INSTANCE = new CharacterLessEvaluator();

        private CharacterLessEvaluator() {
            super( Evaluator.CHAR_TYPE,
                   Evaluator.LESS );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            return ((Character) object1).charValue() < ((Character) object2).charValue();
        }
        
        public String toString() {
            return "Character <";
        }                 
    }

    static class CharacterLessOrEqualEvaluator extends BaseEvaluator {
        public final static Evaluator INSTANCE = new CharacterLessOrEqualEvaluator();

        private CharacterLessOrEqualEvaluator() {
            super( Evaluator.CHAR_TYPE,
                   Evaluator.LESS_OR_EQUAL );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            return ((Character) object1).charValue() <= ((Character) object2).charValue();
        }
        
        public String toString() {
            return "Character <=";
        }         
    }

    static class CharacterGreaterEvaluator extends BaseEvaluator {
        public final static Evaluator INSTANCE = new CharacterGreaterEvaluator();

        private CharacterGreaterEvaluator() {
            super( Evaluator.CHAR_TYPE,
                   Evaluator.GREATER );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            return ((Character) object1).charValue() > ((Character) object2).charValue();
        }
        
        public String toString() {
            return "Character >";
        }         
    }

    static class CharacterGreaterOrEqualEvaluator extends BaseEvaluator {
        private final static Evaluator INSTANCE = new CharacterGreaterOrEqualEvaluator();

        private CharacterGreaterOrEqualEvaluator() {
            super( Evaluator.CHAR_TYPE,
                   Evaluator.GREATER_OR_EQUAL );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            return ((Character) object1).charValue() >= ((Character) object2).charValue();
        }
        
        public String toString() {
            return "Character >=";
        }         
    }

}
