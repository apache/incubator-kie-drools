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
import org.drools.rule.VariableRestriction.ObjectVariableContextEntry;
import org.drools.rule.VariableRestriction.VariableContextEntry;
import org.drools.spi.Evaluator;
import org.drools.spi.Extractor;
import org.drools.spi.FieldValue;

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

    private static final long       serialVersionUID    = -9190991797780589450L;
    private static final String     DEFAULT_FORMAT_MASK = "dd-MMM-yyyy";
    private static final String     DATE_FORMAT_MASK    = getDateFormatMask();

    private static EvaluatorFactory INSTANCE            = new DateFactory();

    private DateFactory() {

    }

    public static EvaluatorFactory getInstance() {
        if ( DateFactory.INSTANCE == null ) {
            DateFactory.INSTANCE = new DateFactory();
        }
        return DateFactory.INSTANCE;
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

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            final Date value1 = (Date) extractor.getValue( object1 );
            final Object value2 = object2.getValue();
            if ( value1 == null ) {
                return value2 == null;
            }
            if ( value2 == null ) {
                return false;
            }
            return value1.compareTo( getRightDate( value2 ) ) == 0;
        }

        public boolean evaluateCachedRight(final VariableContextEntry context,
                                           final Object left) {
            final Date value1 = (Date) context.declaration.getExtractor().getValue( left );
            final Object value2 = ((ObjectVariableContextEntry) context).right;
            if ( value1 == null ) {
                return value2 == null;
            }
            if ( value2 == null ) {
                return false;
            }
            return value1.compareTo( getRightDate( value2 ) ) == 0;
        }

        public boolean evaluateCachedLeft(final VariableContextEntry context,
                                          final Object right) {
            final Date value1 = (Date) ((ObjectVariableContextEntry) context).left;
            final Object value2 = context.extractor.getValue( right );
            if ( value1 == null ) {
                return value2 == null;
            }
            if ( value2 == null ) {
                return false;
            }
            return value1.compareTo( getRightDate( value2 ) ) == 0;
        }

        public boolean evaluate(Extractor extractor1,
                                Object object1,
                                Extractor extractor2,
                                Object object2) {
            final Date value1 = (Date) extractor1.getValue( object1 );
            final Date value2 = (Date) extractor2.getValue( object2 );
            if ( value1 == null ) {
                return value2 == null;
            }
            return value1.compareTo( value2 ) == 0;
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

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            final Date value1 = (Date) extractor.getValue( object1 );
            final Object value2 = object2.getValue();
            if ( value1 == null ) {
                return value2 != null;
            }
            if ( value2 == null ) {
                return true;
            }
            return value1.compareTo( getRightDate( value2 ) ) != 0;
        }

        public boolean evaluateCachedRight(final VariableContextEntry context,
                                           final Object left) {
            final Date value1 = (Date) context.declaration.getExtractor().getValue( left );
            final Object value2 = ((ObjectVariableContextEntry) context).right;
            if ( value1 == null ) {
                return value2 != null;
            }
            if ( value2 == null ) {
                return true;
            }
            return value1.compareTo( getRightDate( value2 ) ) != 0;
        }

        public boolean evaluateCachedLeft(final VariableContextEntry context,
                                          final Object right) {
            final Date value1 = (Date) ((ObjectVariableContextEntry) context).left;
            final Object value2 = context.extractor.getValue( right );
            if ( value1 == null ) {
                return value2 != null;
            }
            if ( value2 == null ) {
                return true;
            }
            return value1.compareTo( getRightDate( value2 ) ) != 0;
        }

        public boolean evaluate(Extractor extractor1,
                                Object object1,
                                Extractor extractor2,
                                Object object2) {
            final Date value1 = (Date) extractor1.getValue( object1 );
            final Date value2 = (Date) extractor2.getValue( object2 );
            if ( value1 == null ) {
                return value2 == null;
            }
            return value1.compareTo( value2 ) != 0;
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

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            final Date value1 = (Date) extractor.getValue( object1 );
            final Object value2 = object2.getValue();
            return value1.compareTo( getRightDate( value2 ) ) < 0;
        }

        public boolean evaluateCachedRight(final VariableContextEntry context,
                                           final Object left) {
            final Date value1 = (Date) context.declaration.getExtractor().getValue( left );
            final Object value2 = ((ObjectVariableContextEntry) context).right;
            return getRightDate( value2 ).compareTo( value1 ) < 0;
        }

        public boolean evaluateCachedLeft(final VariableContextEntry context,
                                          final Object right) {
            final Date value1 = (Date) ((ObjectVariableContextEntry) context).left;
            final Object value2 = context.extractor.getValue( right );
            return getRightDate( value2 ).compareTo( value1 ) < 0;
        }

        public boolean evaluate(Extractor extractor1,
                                Object object1,
                                Extractor extractor2,
                                Object object2) {
            final Date value1 = (Date) extractor1.getValue( object1 );
            final Date value2 = (Date) extractor2.getValue( object2 );
            if ( value1 == null ) {
                return value2 == null;
            }
            return value1.compareTo( value2 ) < 0;
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

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            final Date value1 = (Date) extractor.getValue( object1 );
            final Object value2 = object2.getValue();
            return value1.compareTo( getRightDate( value2 ) ) <= 0;
        }

        public boolean evaluateCachedRight(final VariableContextEntry context,
                                           final Object left) {
            final Date value1 = (Date) context.declaration.getExtractor().getValue( left );
            final Object value2 = ((ObjectVariableContextEntry) context).right;
            return getRightDate( value2 ).compareTo( value1 ) <= 0;
        }

        public boolean evaluateCachedLeft(final VariableContextEntry context,
                                          final Object right) {
            final Date value1 = (Date) ((ObjectVariableContextEntry) context).left;
            final Object value2 = context.extractor.getValue( right );
            return getRightDate( value2 ).compareTo( value1 ) <= 0;
        }

        public boolean evaluate(Extractor extractor1,
                                Object object1,
                                Extractor extractor2,
                                Object object2) {
            final Date value1 = (Date) extractor1.getValue( object1 );
            final Date value2 = (Date) extractor2.getValue( object2 );
            if ( value1 == null ) {
                return value2 == null;
            }
            return value1.compareTo( value2 ) <= 0;
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

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            final Date value1 = (Date) extractor.getValue( object1 );
            final Object value2 = object2.getValue();
            return value1.compareTo( getRightDate( value2 ) ) > 0;
        }

        public boolean evaluateCachedRight(final VariableContextEntry context,
                                           final Object left) {
            final Date value1 = (Date) context.declaration.getExtractor().getValue( left );
            final Object value2 = ((ObjectVariableContextEntry) context).right;
            return getRightDate( value2 ).compareTo( value1 ) > 0;
        }

        public boolean evaluateCachedLeft(final VariableContextEntry context,
                                          final Object right) {
            final Date value1 = (Date) ((ObjectVariableContextEntry) context).left;
            final Object value2 = context.extractor.getValue( right );
            return getRightDate( value2 ).compareTo( value1 ) > 0;
        }

        public boolean evaluate(Extractor extractor1,
                                Object object1,
                                Extractor extractor2,
                                Object object2) {
            final Date value1 = (Date) extractor1.getValue( object1 );
            final Date value2 = (Date) extractor2.getValue( object2 );
            if ( value1 == null ) {
                return value2 == null;
            }
            return value1.compareTo( value2 ) > 0;
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

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            final Date value1 = (Date) extractor.getValue( object1 );
            final Object value2 = object2.getValue();
            return value1.compareTo( getRightDate( value2 ) ) >= 0;
        }

        public boolean evaluateCachedRight(final VariableContextEntry context,
                                           final Object left) {
            final Date value1 = (Date) context.declaration.getExtractor().getValue( left );
            final Object value2 = ((ObjectVariableContextEntry) context).right;
            return getRightDate( value2 ).compareTo( value1 ) >= 0;
        }

        public boolean evaluateCachedLeft(final VariableContextEntry context,
                                          final Object right) {
            final Date value1 = (Date) ((ObjectVariableContextEntry) context).left;
            final Object value2 = context.extractor.getValue( right );
            return getRightDate( value2 ).compareTo( value1 ) >= 0;
        }

        public boolean evaluate(Extractor extractor1,
                                Object object1,
                                Extractor extractor2,
                                Object object2) {
            final Date value1 = (Date) extractor1.getValue( object1 );
            final Date value2 = (Date) extractor2.getValue( object2 );
            if ( value1 == null ) {
                return value2 == null;
            }
            return value1.compareTo( value2 ) >= 0;
        }

        public String toString() {
            return "Date >=";
        }
    }

    /** Use the simple date formatter to read the date from a string */
    public static Date parseDate(final String input) {

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