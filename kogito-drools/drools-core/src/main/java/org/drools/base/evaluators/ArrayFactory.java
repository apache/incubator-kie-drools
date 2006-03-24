package org.drools.base.evaluators;

import java.util.Arrays;
import java.util.Collection;

import org.drools.base.BaseEvaluator;

import org.drools.spi.Evaluator;

/**
 * For handling simple (non collection) array types.
 * @author Michael Neale
 */
public class ArrayFactory {
    
    public static Evaluator getArrayEvaluator(int operator) {
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
        public final static Evaluator INSTANCE = new ArrayEqualEvaluator();

        private ArrayEqualEvaluator() {
            super( Evaluator.ARRAY_TYPE,
                   Evaluator.EQUAL );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            return object1.equals( object2 );
        }
        
        public String toString() {
            return "Array ==";
        }
    }

    static class ArrayNotEqualEvaluator extends BaseEvaluator {
        public final static Evaluator INSTANCE = new ArrayNotEqualEvaluator();

        private ArrayNotEqualEvaluator() {
            super( Evaluator.ARRAY_TYPE,
                   Evaluator.NOT_EQUAL );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            return !Arrays.equals( (Object[]) object1,(Object[]) object2 );
        }
        
        public String toString() {
            return "Object !=";
        }        
    }
    
    static class ArrayContainsEvaluator extends BaseEvaluator {
        public final static Evaluator INSTANCE = new ArrayContainsEvaluator();

        private ArrayContainsEvaluator() {
            super( Evaluator.ARRAY_TYPE,
                   Evaluator.CONTAINS );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            if (object2 == null) return false;
            if (Arrays.binarySearch( (Object[]) object1, object2 ) == -1) 
                return false;
            else 
                return true;
        }
        
        public String toString() {
            return "Array contains";
        }        
    }    

}
