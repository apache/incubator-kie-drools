package org.drools.base.evaluators;

import org.drools.base.BaseEvaluator;

import org.drools.spi.Evaluator;

public class FloatFactory {
    
    public static Evaluator getFloatEvaluator(int operator) {
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
        public final static Evaluator INSTANCE = new FloatEqualEvaluator();

        private FloatEqualEvaluator() {
            super( Evaluator.FLOAT_TYPE,
                   Evaluator.EQUAL );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            return ((Number) object1).floatValue() == ((Number) object2).floatValue();
        }
        
        public String toString() {
            return "Float ==";
        }         
    }

    static class FloatNotEqualEvaluator extends BaseEvaluator {
        public final static Evaluator INSTANCE = new FloatNotEqualEvaluator();

        private FloatNotEqualEvaluator() {
            super( Evaluator.FLOAT_TYPE,
                   Evaluator.NOT_EQUAL );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            return ((Number) object1).floatValue() != ((Number) object2).floatValue();
        }
        
        public String toString() {
            return "Float !=";
        }                 
    }

    static class FloatLessEvaluator extends BaseEvaluator {
        public final static Evaluator INSTANCE = new FloatLessEvaluator();

        private FloatLessEvaluator() {
            super( Evaluator.FLOAT_TYPE,
                   Evaluator.LESS );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            return ((Number) object1).floatValue() < ((Number) object2).floatValue();
        }
        
        public String toString() {
            return "Float <";
        }                 
    }

    static class FloatLessOrEqualEvaluator extends BaseEvaluator {
        public final static Evaluator INSTANCE = new FloatLessOrEqualEvaluator();

        private FloatLessOrEqualEvaluator() {
            super( Evaluator.FLOAT_TYPE,
                   Evaluator.LESS_OR_EQUAL );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            return ((Number) object1).floatValue() <= ((Number) object2).floatValue();
        }
        
        public String toString() {
            return "Float <=";
        }         
    }

    static class FloatGreaterEvaluator extends BaseEvaluator {
        public final static Evaluator INSTANCE = new FloatGreaterEvaluator();

        private FloatGreaterEvaluator() {
            super( Evaluator.FLOAT_TYPE,
                   Evaluator.GREATER );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            return ((Number) object1).floatValue() > ((Number) object2).floatValue();
        }
        
        public String toString() {
            return "Float >";
        }         
    }

    static class FloatGreaterOrEqualEvaluator extends BaseEvaluator {
        private final static Evaluator INSTANCE = new FloatGreaterOrEqualEvaluator();

        private FloatGreaterOrEqualEvaluator() {
            super( Evaluator.FLOAT_TYPE,
                   Evaluator.GREATER_OR_EQUAL );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            return ((Number) object1).floatValue() >= ((Number) object2).floatValue();
        }
        
        public String toString() {
            return "Float >=";
        }         
    } 
}
