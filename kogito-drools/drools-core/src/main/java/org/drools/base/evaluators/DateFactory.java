package org.drools.base.evaluators;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.drools.base.BaseEvaluator;
import org.drools.spi.Evaluator;

/**
 * This will generate evaluators that handle dates.
 * This will also parse strings into dates, according to 
 * DEFAULT_FORMAT_MASK, unless it is overridden by the drools.dateformat system property.
 * 
 * When parsing dates from a string, no time is included.
 * 
 * So you can do expressions like 
 * <code>Person(birthday <= "10-Jul-1974")</code> etc.
 * 
 * @author Michael Neale
 */
public class DateFactory {
    
    private static final String DEFAULT_FORMAT_MASK = "dd-MMM-yyyy";
    private static String DATE_FORMAT_MASK = getDateFormatMask();
    
    public static Evaluator getDateEvaluator(int operator) {
        switch ( operator ) {
            case Evaluator.EQUAL :
                return DateEqualEvaluator.INSTANCE;
            case Evaluator.NOT_EQUAL :
                return DateNotEqualEvaluator.INSTANCE;
            case Evaluator.LESS :
                return DateLessEvaluator.INSTANCE;
            case Evaluator.LESS_OR_EQUAL :
                return DateLessOrEqualEvaluator.INSTANCE;
            case Evaluator.GREATER :
                return DateGreaterEvaluator.INSTANCE;
            case Evaluator.GREATER_OR_EQUAL :
                return DateGreaterOrEqualEvaluator.INSTANCE;
            default :
                throw new RuntimeException( "Operator '" + operator + "' does not exist for DateEvaluator" );
        }
    }

    static class DateEqualEvaluator extends BaseEvaluator {
        public final static Evaluator INSTANCE = new DateEqualEvaluator();

        private DateEqualEvaluator() {
            super( Evaluator.DATE_TYPE,
                   Evaluator.EQUAL );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            if (object1 == null) return object2 == null;
            if (object2 == null) return false;
            Date left = (Date) object1;
            
            if (left.compareTo( getRightDate( object2 ) ) == 0) 
                return true;
            else 
                return false;
        }


        
        public String toString() {
            return "Date ==";
        }         
    }

    static class DateNotEqualEvaluator extends BaseEvaluator {
        public final static Evaluator INSTANCE = new DateNotEqualEvaluator();

        private DateNotEqualEvaluator() {
            super( Evaluator.DATE_TYPE,
                   Evaluator.NOT_EQUAL );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            if (object1 == null) return object2 != null;
            if (object2 == null) return true;
            Date left = (Date) object1;
            if (left.compareTo( getRightDate( object2 ) ) != 0)
                return true;
            else 
                return false;
        }
        
        public String toString() {
            return "Date !=";
        }                 
    }

    static class DateLessEvaluator extends BaseEvaluator {
        public final static Evaluator INSTANCE = new DateLessEvaluator();

        private DateLessEvaluator() {
            super( Evaluator.DATE_TYPE,
                   Evaluator.LESS );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            Date left = (Date) object1;
            if (left.compareTo( getRightDate( object2 ) ) < 0)
                return true;
            else 
                return false;
        }
        
        public String toString() {
            return "Date <";
        }                 
    }

    static class DateLessOrEqualEvaluator extends BaseEvaluator {
        public final static Evaluator INSTANCE = new DateLessOrEqualEvaluator();

        private DateLessOrEqualEvaluator() {
            super( Evaluator.DATE_TYPE,
                   Evaluator.LESS_OR_EQUAL );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            Date left = (Date) object1;
            if (left.compareTo( getRightDate( object2 ) ) <= 0)
                return true;
            else 
                return false;        
            }
        
        public String toString() {
            return "Date <=";
        }         
    }

    static class DateGreaterEvaluator extends BaseEvaluator {
        public final static Evaluator INSTANCE = new DateGreaterEvaluator();

        private DateGreaterEvaluator() {
            super( Evaluator.DATE_TYPE,
                   Evaluator.GREATER );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            Date left = (Date) object1;
            if (left.compareTo( getRightDate( object2 ) ) > 0)
                return true;
            else 
                return false;        
            }
        
        public String toString() {
            return "Date >";
        }         
    }

    static class DateGreaterOrEqualEvaluator extends BaseEvaluator {
        private final static Evaluator INSTANCE = new DateGreaterOrEqualEvaluator();

        private DateGreaterOrEqualEvaluator() {
            super( Evaluator.DATE_TYPE,
                   Evaluator.GREATER_OR_EQUAL );
        }

        public boolean evaluate(Object object1,
                                Object object2) {
            Date left = (Date) object1;
            if (left.compareTo( getRightDate( object2 ) ) >= 0)
                return true;
            else 
                return false;        
            }
        
        public String toString() {
            return "Date >=";
        }         
    }
    
    /** Use the simple date formatter to read the date from a string */
    private static Date parseDate(String input) {
        
        SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT_MASK);
        try {
            return df.parse( input );
        } catch ( ParseException e ) {
            throw new IllegalArgumentException("Invalid date input format: [" 
                                               + input + "] it should follow: [" + DATE_FORMAT_MASK + "]");
        }
    }
    
    /** Converts the right hand side date as appropriate */
    private static Date getRightDate(Object object2) {
        if (object2 == null) return null;
        if (object2 instanceof String) 
            return parseDate((String)object2);
        else if (object2 instanceof Date)
            return (Date) object2;
        else 
            throw new IllegalArgumentException("Unable to convert " + object2.getClass() + " to a Date.");        
    }    
    
    /** Check for the system property override, if it exists */
    private static String getDateFormatMask() {
        String fmt = System.getProperty( "drools.dateformat" );
        if (fmt == null) {
            fmt = DEFAULT_FORMAT_MASK;
        }
        return fmt;
    }

}
