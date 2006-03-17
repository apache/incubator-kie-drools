package org.drools.base;

import org.drools.base.evaluators.BooleanFactory;
import org.drools.base.evaluators.ByteFactory;
import org.drools.base.evaluators.CharacterFactory;
import org.drools.base.evaluators.DateFactory;
import org.drools.base.evaluators.DoubleFactory;
import org.drools.base.evaluators.FloatFactory;
import org.drools.base.evaluators.IntegerFactory;
import org.drools.base.evaluators.LongFactory;
import org.drools.base.evaluators.ObjectFactory;
import org.drools.base.evaluators.ShortFactory;
import org.drools.base.evaluators.StringFactory;
import org.drools.spi.Evaluator;

/**
 * This is a factory to generate evaluators for all types and operations that can be used 
 * in constraints. Uses the helper classes in the evaluators sub package.
 * 
 * eg Person(object<type> opr object)
 * Where valid type's are are defined as contants in Evaluator.
 * 
 * Adding support for other types or operators is quite easy, just remmeber to add it to the getEvaluator method(s)
 * below, and also to the unit tests.
 * TODO: Should this be made dynamic, such that users can add their own types, a-la hibernate?
 */
public class EvaluatorFactory {
    private static final EvaluatorFactory INSTANCE = new EvaluatorFactory();

    public static EvaluatorFactory getInstance() {
        return EvaluatorFactory.INSTANCE;
    }

    private EvaluatorFactory() {

    }
    
    public static Evaluator getEvaluator(int type, 
                                         String operator) {
        Evaluator evaluator = null;
        if ( operator.equals("==") ) {
            evaluator = getEvaluator(type, Evaluator.EQUAL);
        } else if ( operator.equals("!=") ) {
            evaluator = getEvaluator(type, Evaluator.NOT_EQUAL);
        } else if ( operator.equals("<") ) {
            evaluator = getEvaluator(type, Evaluator.LESS);
        } else if ( operator.equals("<=") ) {
            evaluator = getEvaluator(type, Evaluator.LESS_OR_EQUAL);
        } else if ( operator.equals(">") ) {
            evaluator = getEvaluator(type, Evaluator.GREATER);
        } else if ( operator.equals(">=") ) {
            evaluator = getEvaluator(type, Evaluator.GREATER_OR_EQUAL);
        } else if (operator.equals( "contains" ) ) {
            evaluator = getEvaluator(type, Evaluator.CONTAINS);
        } else if (operator.equals( "matches" ) ) {
            evaluator = getEvaluator(type, Evaluator.MATCHES);
        } else {
            throw new IllegalArgumentException("Unknown operator: '" + operator + "'");
        }
        
        return evaluator;
    }

    public static Evaluator getEvaluator(int type,
                                         int operator) {
        switch ( type ) {
            case Evaluator.STRING_TYPE :
                return StringFactory.getStringEvaluator( operator );
            case Evaluator.OBJECT_TYPE :
                return ObjectFactory.getObjectEvaluator( operator );
            case Evaluator.SHORT_TYPE :
                return ShortFactory.getShortEvaluator( operator );
            case Evaluator.INTEGER_TYPE :
                return IntegerFactory.getIntegerEvaluator( operator );
            case Evaluator.BOOLEAN_TYPE :
                return BooleanFactory.getBooleanEvaluator( operator );  
            case Evaluator.DOUBLE_TYPE :
                return DoubleFactory.getDoubleEvaluator( operator );
            case Evaluator.CHAR_TYPE :
                return CharacterFactory.getCharacterEvaluator( operator );
            case Evaluator.BYTE_TYPE :
                return ByteFactory.getByteEvaluator( operator );
            case Evaluator.FLOAT_TYPE :
                return FloatFactory.getFloatEvaluator( operator );
            case Evaluator.LONG_TYPE :
                return LongFactory.getLongEvaluator( operator );
            case Evaluator.DATE_TYPE :
                return DateFactory.getDateEvaluator( operator );
            default :
                throw new RuntimeException( "Type '" + type + "' does not exist for BaseEvaluatorFactory" );
        }
    }

    
    
    
    
}
