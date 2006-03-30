package org.drools.base.evaluators;

import java.util.Collection;

import org.drools.base.BaseEvaluator;

import org.drools.spi.Evaluator;

/**
 * This is the misc "bucket" evaluator factory for objects.
 * It is fairly limited in operations, 
 * and what operations are available are dependent on the exact type.
 * 
 * @author Michael Neale
 */
public class ObjectFactory {
    
    public static Evaluator getObjectEvaluator(int operator) {
        switch ( operator ) {
            case Evaluator.EQUAL :
                return ObjectEqualEvaluator.INSTANCE;
            case Evaluator.NOT_EQUAL :
                return ObjectNotEqualEvaluator.INSTANCE;
            case Evaluator.CONTAINS :
                return ObjectContainsEvaluator.INSTANCE;
            default :
                throw new RuntimeException( "Operator '" + operator + "' does not exist for ObjectEvaluator" );
        }
    }

    static class ObjectEqualEvaluator extends BaseEvaluator {
        public final static Evaluator INSTANCE = new ObjectEqualEvaluator();

        private ObjectEqualEvaluator() {
            super( Evaluator.OBJECT_TYPE,
                   Evaluator.EQUAL );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            if (object1 == null) return object2 == null;
            return object1.equals( object2 );
        }
        
        public String toString() {
            return "Object ==";
        }
    }

    static class ObjectNotEqualEvaluator extends BaseEvaluator {
        public final static Evaluator INSTANCE = new ObjectNotEqualEvaluator();

        private ObjectNotEqualEvaluator() {
            super( Evaluator.OBJECT_TYPE,
                   Evaluator.NOT_EQUAL );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            if (object1 == null) return ! (object2 == null);
            return !object1.equals( object2 );
        }
        
        public String toString() {
            return "Object !=";
        }        
    }
    
    static class ObjectContainsEvaluator extends BaseEvaluator {
        public final static Evaluator INSTANCE = new ObjectContainsEvaluator();

        private ObjectContainsEvaluator() {
            super( Evaluator.OBJECT_TYPE,
                   Evaluator.CONTAINS );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            if (object2 == null) return false;
            
            //TODO: add support for hashes, normal arrays etc
            Collection col = (Collection) object1;
            return col.contains( object2 );
        }
        
        public String toString() {
            return "Object contains";
        }        
    }    

}
