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
import org.drools.base.ValueType;
import org.drools.base.evaluators.ShortFactory.ShortEqualEvaluator;
import org.drools.base.evaluators.ShortFactory.ShortGreaterEvaluator;
import org.drools.base.evaluators.ShortFactory.ShortGreaterOrEqualEvaluator;
import org.drools.base.evaluators.ShortFactory.ShortLessEvaluator;
import org.drools.base.evaluators.ShortFactory.ShortLessOrEqualEvaluator;
import org.drools.base.evaluators.ShortFactory.ShortNotEqualEvaluator;
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
public class DateFactory
    implements
    EvaluatorFactory {

    private static final String     DEFAULT_FORMAT_MASK = "dd-MMM-yyyy";
    private static final String     DATE_FORMAT_MASK    = getDateFormatMask();

    private static EvaluatorFactory INSTANCE            = new DateFactory();

    private DateFactory() {

    }

    public static EvaluatorFactory getInstance() {
        if ( INSTANCE == null ) {
            INSTANCE = new DateFactory();
        }
        return INSTANCE;
    }

    public Evaluator getEvaluator(final Operator operator) {
        if ( operator == Operator.EQUAL ) {
            return DateEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.NOT_EQUAL ) {
            return DateNotEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.LESS ) {
            return DateLessEvaluator.INSTANCE;
        } else if ( operator == Operator.LESS_OR_EQUAL ) {
            return DateLessOrEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.GREATER ) {
            return DateGreaterEvaluator.INSTANCE;
        } else if ( operator == Operator.GREATER_OR_EQUAL ) {
            return DateGreaterOrEqualEvaluator.INSTANCE;
        } else {
            throw new RuntimeException( "Operator '" + operator + "' does not exist for DateEvaluator" );
        }
    }

    static class DateEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new DateEqualEvaluator();

        private DateEqualEvaluator() {
            super( ValueType.DATE_TYPE,
                   Operator.EQUAL );
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
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new DateNotEqualEvaluator();

        private DateNotEqualEvaluator() {
            super( ValueType.DATE_TYPE,
                   Operator.NOT_EQUAL );
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
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new DateLessEvaluator();

        private DateLessEvaluator() {
            super( ValueType.DATE_TYPE,
                   Operator.LESS );
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
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new DateLessOrEqualEvaluator();

        private DateLessOrEqualEvaluator() {
            super( ValueType.DATE_TYPE,
                   Operator.LESS_OR_EQUAL );
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
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new DateGreaterEvaluator();

        private DateGreaterEvaluator() {
            super( ValueType.DATE_TYPE,
                   Operator.GREATER );
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
        private static final long      serialVersionUID = 320;
        private final static Evaluator INSTANCE         = new DateGreaterOrEqualEvaluator();

        private DateGreaterOrEqualEvaluator() {
            super( ValueType.DATE_TYPE,
                   Operator.GREATER_OR_EQUAL );
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