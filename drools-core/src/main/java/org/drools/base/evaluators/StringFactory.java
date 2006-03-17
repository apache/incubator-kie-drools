package org.drools.base.evaluators;

import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.regex.Pattern;

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
    
    public static Evaluator getStringEvaluator(int operator) {
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
        public final static Evaluator INSTANCE = new StringEqualEvaluator();

        private StringEqualEvaluator() {
            super( Evaluator.STRING_TYPE,
                   Evaluator.EQUAL );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            return object1.equals( object2 );
        }
        
        public String toString() {
            return "String ==";
        }
    }

    static class StringNotEqualEvaluator extends BaseEvaluator {
        public final static Evaluator INSTANCE = new StringNotEqualEvaluator();

        private StringNotEqualEvaluator() {
            super( Evaluator.STRING_TYPE,
                   Evaluator.NOT_EQUAL );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            return !object1.equals( object2 );
        }
        
        public String toString() {
            return "String !=";
        }        
    }
    
    static class StringMatchesEvaluator extends BaseEvaluator {
        public final static Evaluator INSTANCE = new StringMatchesEvaluator();
        
        private StringMatchesEvaluator() {
            super( Evaluator.STRING_TYPE,
                   Evaluator.MATCHES);
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            String pattern = (String) object2;
            String target = (String) object1;
            
            if (object1 == null) return false;
            
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
