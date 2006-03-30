package org.drools.base.evaluators;

import org.drools.base.BaseEvaluator;
import org.drools.spi.Evaluator;

public class LongFactory {
    public static Evaluator getLongEvaluator(int operator) {
        switch ( operator ) {
            case Evaluator.EQUAL :
                return LongEqualEvaluator.INSTANCE;
            case Evaluator.NOT_EQUAL :
                return LongNotEqualEvaluator.INSTANCE;
            case Evaluator.LESS :
                return LongLessEvaluator.INSTANCE;
            case Evaluator.LESS_OR_EQUAL :
                return LongLessOrEqualEvaluator.INSTANCE;
            case Evaluator.GREATER :
                return LongGreaterEvaluator.INSTANCE;
            case Evaluator.GREATER_OR_EQUAL :
                return LongGreaterOrEqualEvaluator.INSTANCE;
            default :
                throw new RuntimeException( "Operator '" + operator + "' does not exist for LongEvaluator" );
        }
    }

    static class LongEqualEvaluator extends BaseEvaluator {
        public final static Evaluator INSTANCE = new LongEqualEvaluator();

        private LongEqualEvaluator() {
            super( Evaluator.LONG_TYPE,
                   Evaluator.EQUAL );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            if (object1 == null) return object2 == null;            
            return ((Number) object1).equals(object2);
        }
        
        public String toString() {
            return "Long ==";
        }         
    }

    static class LongNotEqualEvaluator extends BaseEvaluator {
        public final static Evaluator INSTANCE = new LongNotEqualEvaluator();

        private LongNotEqualEvaluator() {
            super( Evaluator.LONG_TYPE,
                   Evaluator.NOT_EQUAL );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            if (object1 == null) return object2 != null;
            return !((Number) object1).equals(object2);
        }
        
        public String toString() {
            return "Long !=";
        }                 
    }

    static class LongLessEvaluator extends BaseEvaluator {
        public final static Evaluator INSTANCE = new LongLessEvaluator();

        private LongLessEvaluator() {
            super( Evaluator.LONG_TYPE,
                   Evaluator.LESS );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            return ((Number) object1).longValue() < ((Number) object2).longValue();
        }
        
        public String toString() {
            return "Long <";
        }                 
    }

    static class LongLessOrEqualEvaluator extends BaseEvaluator {
        public final static Evaluator INSTANCE = new LongLessOrEqualEvaluator();

        private LongLessOrEqualEvaluator() {
            super( Evaluator.LONG_TYPE,
                   Evaluator.LESS_OR_EQUAL );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            return ((Number) object1).longValue() <= ((Number) object2).longValue();
        }
        
        public String toString() {
            return "Long <=";
        }         
    }

    static class LongGreaterEvaluator extends BaseEvaluator {
        public final static Evaluator INSTANCE = new LongGreaterEvaluator();

        private LongGreaterEvaluator() {
            super( Evaluator.LONG_TYPE,
                   Evaluator.GREATER );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            return ((Number) object1).longValue() > ((Number) object2).longValue();
        }
        
        public String toString() {
            return "Long >";
        }         
    }

    static class LongGreaterOrEqualEvaluator extends BaseEvaluator {
        private final static Evaluator INSTANCE = new LongGreaterOrEqualEvaluator();

        private LongGreaterOrEqualEvaluator() {
            super( Evaluator.LONG_TYPE,
                   Evaluator.GREATER_OR_EQUAL );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            return ((Number) object1).longValue() >= ((Number) object2).longValue();
        }
        
        public String toString() {
            return "Long >=";
        }         
    }

}
