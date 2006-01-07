package org.drools.rule;

import org.drools.spi.Evaluator;
import org.drools.spi.BaseEvaluator;

public class EvaluatorFactory {
    private static final EvaluatorFactory INSTANCE = new EvaluatorFactory();

    public static EvaluatorFactory getInstance() {
        return EvaluatorFactory.INSTANCE;
    }

    private EvaluatorFactory() {

    }

    public Evaluator getEvaluator(int type,
                           int operator) {
        switch ( type ) {
            case Evaluator.OBJECT_TYPE :
                return getObjectEvaluator( operator );
            case Evaluator.SHORT_TYPE :
                return getShortEvaluator( operator );
            case Evaluator.INTEGER_TYPE :
                return getIntegerEvaluator( operator );
            case Evaluator.BOOLEAN_TYPE :
                return getBooleanEvaluator( operator );                
            default :
                throw new RuntimeException( "Type '" + type + "' does not exist for BaseEvaluatorFactory" );
        }
    }

    Evaluator getObjectEvaluator(int operator) {
        switch ( operator ) {
            case Evaluator.EQUAL :
                return ObjectEqualEvaluator.getInstance();
            case Evaluator.NOT_EQUAL :
                return ObjectNotEqualEvaluator.getInstance();
            default :
                throw new RuntimeException( "Operator '" + operator + "' does not exist for ObjectEvaluator" );
        }
    }

    static class ObjectEqualEvaluator extends BaseEvaluator {
        private static Evaluator INSTANCE;

        public static Evaluator getInstance() {
            if ( INSTANCE == null ) {
                INSTANCE = new ObjectEqualEvaluator();
            }
            return INSTANCE;
        }

        private ObjectEqualEvaluator() {
            super( Evaluator.OBJECT_TYPE,
                   Evaluator.EQUAL );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            return object1.equals( object2 );
        }
    }

    static class ObjectNotEqualEvaluator extends BaseEvaluator {
        private static Evaluator INSTANCE;

        public static Evaluator getInstance() {
            if ( INSTANCE == null ) {
                INSTANCE = new ObjectNotEqualEvaluator();
            }
            return INSTANCE;
        }

        private ObjectNotEqualEvaluator() {
            super( Evaluator.OBJECT_TYPE,
                   Evaluator.NOT_EQUAL );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            return !object1.equals( object2 );
        }
    }
    
    static class BooleanEqualEvaluator extends BaseEvaluator {
        private static Evaluator INSTANCE;

        public static Evaluator getInstance() {
            if ( INSTANCE == null ) {
                INSTANCE = new BooleanEqualEvaluator();
            }
            return INSTANCE;
        }

        private BooleanEqualEvaluator() {
            super( Evaluator.BOOLEAN_TYPE,
                   Evaluator.EQUAL );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            return ((Boolean)object1).booleanValue() == ((Boolean)object2).booleanValue();
        }
    }
    
    Evaluator getBooleanEvaluator(int operator) {
        switch ( operator ) {
            case Evaluator.EQUAL :
                return ObjectEqualEvaluator.getInstance();
            case Evaluator.NOT_EQUAL :
                return ObjectNotEqualEvaluator.getInstance();
            default :
                throw new RuntimeException( "Operator '" + operator + "' does not exist for BooleanEvaluator" );
        }
    }    

    static class BooleanNotEqualEvaluator extends BaseEvaluator {
        private static Evaluator INSTANCE;

        public static Evaluator getInstance() {
            if ( INSTANCE == null ) {
                INSTANCE = new BooleanNotEqualEvaluator();
            }
            return INSTANCE;
        }

        private BooleanNotEqualEvaluator() {
            super( Evaluator.BOOLEAN_TYPE,
                   Evaluator.NOT_EQUAL );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            return ((Boolean)object1).booleanValue() != ((Boolean)object2).booleanValue();
        }
    }    

    Evaluator getShortEvaluator(int operator) {
        switch ( operator ) {
            case Evaluator.EQUAL :
                return ShortEqualEvaluator.getInstance();
            case Evaluator.NOT_EQUAL :
                return ShortNotEqualEvaluator.getInstance();
            case Evaluator.LESS :
                return ShortLessEvaluator.getInstance();
            case Evaluator.LESS_OR_EQUAL :
                return ShortLessOrEqualEvaluator.getInstance();
            case Evaluator.GREATER :
                return ShortGreaterEvaluator.getInstance();
            case Evaluator.GREATER_OR_EQUAL :
                return ShortGreaterOrEqualEvaluator.getInstance();
            default :
                throw new RuntimeException( "Operator '" + operator + "' does not exist for BooleanEvaluator" );
        }
    }

    static class ShortEqualEvaluator extends BaseEvaluator {
        private static Evaluator INSTANCE;

        public static Evaluator getInstance() {
            if ( INSTANCE == null ) {
                INSTANCE = new ShortEqualEvaluator();
            }
            return INSTANCE;
        }

        private ShortEqualEvaluator() {
            super( Evaluator.SHORT_TYPE,
                   Evaluator.EQUAL );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            return ((Number) object1).shortValue() == ((Number) object2).shortValue();
        }
    }

    static class ShortNotEqualEvaluator extends BaseEvaluator {
        private static Evaluator INSTANCE;

        public static Evaluator getInstance() {
            if ( INSTANCE == null ) {
                INSTANCE = new ShortNotEqualEvaluator();
            }
            return INSTANCE;
        }

        private ShortNotEqualEvaluator() {
            super( Evaluator.SHORT_TYPE,
                   Evaluator.NOT_EQUAL );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            return ((Number) object1).shortValue() != ((Number) object2).shortValue();
        }
    }

    static class ShortLessEvaluator extends BaseEvaluator {
        private static Evaluator INSTANCE;

        public static Evaluator getInstance() {
            if ( INSTANCE == null ) {
                INSTANCE = new ShortLessEvaluator();
            }
            return INSTANCE;
        }

        private ShortLessEvaluator() {
            super( Evaluator.SHORT_TYPE,
                   Evaluator.LESS );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            return ((Number) object1).shortValue() < ((Number) object2).shortValue();
        }
    }

    static class ShortLessOrEqualEvaluator extends BaseEvaluator {
        private static Evaluator INSTANCE;

        public static Evaluator getInstance() {
            if ( INSTANCE == null ) {
                INSTANCE = new ShortLessOrEqualEvaluator();
            }
            return INSTANCE;
        }

        private ShortLessOrEqualEvaluator() {
            super( Evaluator.SHORT_TYPE,
                   Evaluator.LESS_OR_EQUAL );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            return ((Number) object1).shortValue() <= ((Number) object2).shortValue();
        }
    }

    static class ShortGreaterEvaluator extends BaseEvaluator {
        private static Evaluator INSTANCE;

        public static Evaluator getInstance() {
            if ( INSTANCE == null ) {
                INSTANCE = new ShortGreaterEvaluator();
            }
            return INSTANCE;
        }

        private ShortGreaterEvaluator() {
            super( Evaluator.SHORT_TYPE,
                   Evaluator.GREATER );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            return ((Number) object1).shortValue() > ((Number) object2).shortValue();
        }
    }

    static class ShortGreaterOrEqualEvaluator extends BaseEvaluator {
        private static Evaluator INSTANCE;

        public static Evaluator getInstance() {
            if ( INSTANCE == null ) {
                INSTANCE = new ShortGreaterOrEqualEvaluator();
            }
            return INSTANCE;
        }

        private ShortGreaterOrEqualEvaluator() {
            super( Evaluator.SHORT_TYPE,
                   Evaluator.GREATER_OR_EQUAL );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            return ((Number) object1).shortValue() >= ((Number) object2).shortValue();
        }
    }

    Evaluator getIntegerEvaluator(int operator) {
        switch ( operator ) {
            case Evaluator.EQUAL :
                return IntegerEqualEvaluator.getInstance();
            case Evaluator.NOT_EQUAL :
                return IntegerNotEqualEvaluator.getInstance();
            case Evaluator.LESS :
                return IntegerLessEvaluator.getInstance();
            case Evaluator.LESS_OR_EQUAL :
                return IntegerLessOrEqualEvaluator.getInstance();
            case Evaluator.GREATER :
                return IntegerGreaterEvaluator.getInstance();
            case Evaluator.GREATER_OR_EQUAL :
                return IntegerGreaterOrEqualEvaluator.getInstance();
            default :
                throw new RuntimeException( "Operator '" + operator + "' does not exist for BooleanEvaluator" );
        }
    }

    static class IntegerEqualEvaluator extends BaseEvaluator {
        private static Evaluator INSTANCE;

        public static Evaluator getInstance() {
            if ( INSTANCE == null ) {
                INSTANCE = new IntegerEqualEvaluator();
            }
            return INSTANCE;
        }

        private IntegerEqualEvaluator() {
            super( Evaluator.SHORT_TYPE,
                   Evaluator.EQUAL );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            return ((Number) object1).intValue() == ((Number) object2).intValue();
        }
    }

    static class IntegerNotEqualEvaluator extends BaseEvaluator {
        private static Evaluator INSTANCE;

        public static Evaluator getInstance() {
            if ( INSTANCE == null ) {
                INSTANCE = new IntegerNotEqualEvaluator();
            }
            return INSTANCE;
        }

        private IntegerNotEqualEvaluator() {
            super( Evaluator.SHORT_TYPE,
                   Evaluator.NOT_EQUAL );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            return ((Number) object1).intValue() != ((Number) object2).intValue();
        }
    }

    static class IntegerLessEvaluator extends BaseEvaluator {
        private static Evaluator INSTANCE;

        public static Evaluator getInstance() {
            if ( INSTANCE == null ) {
                INSTANCE = new IntegerLessEvaluator();
            }
            return INSTANCE;
        }

        private IntegerLessEvaluator() {
            super( Evaluator.SHORT_TYPE,
                   Evaluator.LESS );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            return ((Number) object1).intValue() < ((Number) object2).intValue();
        }
    }

    static class IntegerLessOrEqualEvaluator extends BaseEvaluator {
        private static Evaluator INSTANCE;

        public static Evaluator getInstance() {
            if ( INSTANCE == null ) {
                INSTANCE = new IntegerLessOrEqualEvaluator();
            }
            return INSTANCE;
        }

        private IntegerLessOrEqualEvaluator() {
            super( Evaluator.SHORT_TYPE,
                   Evaluator.LESS_OR_EQUAL );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            return ((Number) object1).intValue() <= ((Number) object2).intValue();
        }
    }

    static class IntegerGreaterEvaluator extends BaseEvaluator {
        private static Evaluator INSTANCE;

        public static Evaluator getInstance() {
            if ( INSTANCE == null ) {
                INSTANCE = new IntegerGreaterEvaluator();
            }
            return INSTANCE;
        }

        private IntegerGreaterEvaluator() {
            super( Evaluator.SHORT_TYPE,
                   Evaluator.GREATER );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            return ((Number) object1).intValue() > ((Number) object2).intValue();
        }
    }

    static class IntegerGreaterOrEqualEvaluator extends BaseEvaluator {
        private static Evaluator INSTANCE;

        public static Evaluator getInstance() {
            if ( INSTANCE == null ) {
                INSTANCE = new IntegerGreaterOrEqualEvaluator();
            }
            return INSTANCE;
        }

        private IntegerGreaterOrEqualEvaluator() {
            super( Evaluator.SHORT_TYPE,
                   Evaluator.GREATER_OR_EQUAL );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            return ((Number) object1).intValue() >= ((Number) object2).intValue();
        }
    }
}
