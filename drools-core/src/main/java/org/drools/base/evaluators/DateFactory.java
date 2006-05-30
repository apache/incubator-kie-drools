package org.drools.base.evaluators;

/*
 * Copyright 2005 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
    private static final String DATE_FORMAT_MASK    = getDateFormatMask();

    public static Evaluator getDateEvaluator(final int operator) {
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
        /**
         * 
         */
        private static final long     serialVersionUID = -7248999526793624416L;
        public final static Evaluator INSTANCE         = new DateEqualEvaluator();

        private DateEqualEvaluator() {
            super( Evaluator.DATE_TYPE,
                   Evaluator.EQUAL );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
            if ( object1 == null ) {
                return object2 == null;
            }
            if ( object2 == null ) {
                return false;
            }
            final Date left = (Date) object1;

            if ( left.compareTo( getRightDate( object2 ) ) == 0 ) {
                return true;
            } else {
                return false;
            }
        }

        public String toString() {
            return "Date ==";
        }
    }

    static class DateNotEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = -999744404766802074L;
        public final static Evaluator INSTANCE         = new DateNotEqualEvaluator();

        private DateNotEqualEvaluator() {
            super( Evaluator.DATE_TYPE,
                   Evaluator.NOT_EQUAL );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
            if ( object1 == null ) {
                return object2 != null;
            }
            if ( object2 == null ) {
                return true;
            }
            final Date left = (Date) object1;
            if ( left.compareTo( getRightDate( object2 ) ) != 0 ) {
                return true;
            } else {
                return false;
            }
        }

        public String toString() {
            return "Date !=";
        }
    }

    static class DateLessEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = -4362504881470806670L;
        public final static Evaluator INSTANCE         = new DateLessEvaluator();

        private DateLessEvaluator() {
            super( Evaluator.DATE_TYPE,
                   Evaluator.LESS );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
            final Date left = (Date) object1;
            if ( left.compareTo( getRightDate( object2 ) ) < 0 ) {
                return true;
            } else {
                return false;
            }
        }

        public String toString() {
            return "Date <";
        }
    }

    static class DateLessOrEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = -1545183091770593710L;
        public final static Evaluator INSTANCE         = new DateLessOrEqualEvaluator();

        private DateLessOrEqualEvaluator() {
            super( Evaluator.DATE_TYPE,
                   Evaluator.LESS_OR_EQUAL );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
            final Date left = (Date) object1;
            if ( left.compareTo( getRightDate( object2 ) ) <= 0 ) {
                return true;
            } else {
                return false;
            }
        }

        public String toString() {
            return "Date <=";
        }
    }

    static class DateGreaterEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 1450531664603794369L;
        public final static Evaluator INSTANCE         = new DateGreaterEvaluator();

        private DateGreaterEvaluator() {
            super( Evaluator.DATE_TYPE,
                   Evaluator.GREATER );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
            final Date left = (Date) object1;
            if ( left.compareTo( getRightDate( object2 ) ) > 0 ) {
                return true;
            } else {
                return false;
            }
        }

        public String toString() {
            return "Date >";
        }
    }

    static class DateGreaterOrEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long      serialVersionUID = -6149840707848164332L;
        private final static Evaluator INSTANCE         = new DateGreaterOrEqualEvaluator();

        private DateGreaterOrEqualEvaluator() {
            super( Evaluator.DATE_TYPE,
                   Evaluator.GREATER_OR_EQUAL );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
            final Date left = (Date) object1;
            if ( left.compareTo( getRightDate( object2 ) ) >= 0 ) {
                return true;
            } else {
                return false;
            }
        }

        public String toString() {
            return "Date >=";
        }
    }

    /** Use the simple date formatter to read the date from a string */
    private static Date parseDate(final String input) {

        final SimpleDateFormat df = new SimpleDateFormat( DateFactory.DATE_FORMAT_MASK );
        try {
            return df.parse( input );
        } catch ( final ParseException e ) {
            throw new IllegalArgumentException( "Invalid date input format: [" + input + "] it should follow: [" + DateFactory.DATE_FORMAT_MASK + "]" );
        }
    }

    /** Converts the right hand side date as appropriate */
    private static Date getRightDate(final Object object2) {
        if ( object2 == null ) {
            return null;
        }
        if ( object2 instanceof String ) {
            return parseDate( (String) object2 );
        } else if ( object2 instanceof Date ) {
            return (Date) object2;
        } else {
            throw new IllegalArgumentException( "Unable to convert " + object2.getClass() + " to a Date." );
        }
    }

    /** Check for the system property override, if it exists */
    private static String getDateFormatMask() {
        String fmt = System.getProperty( "drools.dateformat" );
        if ( fmt == null ) {
            fmt = DateFactory.DEFAULT_FORMAT_MASK;
        }
        return fmt;
    }

}