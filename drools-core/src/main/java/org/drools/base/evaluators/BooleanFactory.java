package org.drools.base.evaluators;

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
