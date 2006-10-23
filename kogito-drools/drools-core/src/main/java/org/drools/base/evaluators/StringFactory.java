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

import org.drools.base.BaseEvaluator;
import org.drools.base.ValueType;
import org.drools.rule.VariableRestriction.ObjectVariableContextEntry;
import org.drools.rule.VariableRestriction.VariableContextEntry;
import org.drools.spi.Evaluator;
import org.drools.spi.Extractor;
import org.drools.spi.FieldValue;

/**
 * This is the misc "bucket" evaluator factory for objects.
 * It is fairly limited in operations, 
 * and what operations are available are dependent on the exact type.
 * 
 * @author Michael Neale
 */
public class StringFactory
    implements
    EvaluatorFactory {

    private static final long       serialVersionUID = -1761752249672590658L;
    private static EvaluatorFactory INSTANCE         = new StringFactory();

    private StringFactory() {

    }

    public static EvaluatorFactory getInstance() {
        if ( StringFactory.INSTANCE == null ) {
            StringFactory.INSTANCE = new StringFactory();
        }
        return StringFactory.INSTANCE;
    }

    public Evaluator getEvaluator(final Operator operator) {
        if ( operator == Operator.EQUAL ) {
            return StringEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.NOT_EQUAL ) {
            return StringNotEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.MATCHES ) {
            return StringMatchesEvaluator.INSTANCE;
        } else {
            throw new RuntimeException( "Operator '" + operator + "' does not exist for StringEvaluator" );
        }
    }

    static class StringEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new StringEqualEvaluator();

        private StringEqualEvaluator() {
            super( ValueType.STRING_TYPE,
                   Operator.EQUAL );
        }

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            final Object value1 = extractor.getValue( object1 );
            final Object value2 = object2.getValue();
            if ( value1 == null ) {
                return value2 == null;
            }
            return value1.equals( value2 );
        }

        public boolean evaluate(final FieldValue object1,
                                final Extractor extractor,
                                final Object object2) {
            final Object value1 = object1.getValue();
            final Object value2 = extractor.getValue( object2 );
            if ( value1 == null ) {
                return value2 == null;
            }
            return value1.equals( value2 );
        }

        public boolean evaluateCachedRight(final VariableContextEntry context,
                                           final Object left) {
            final Object value = context.declaration.getExtractor().getValue( left );
            if ( value == null ) {
                return ((ObjectVariableContextEntry) context).right == null;
            }
            return value.equals( ((ObjectVariableContextEntry) context).right );
        }

        public boolean evaluateCachedLeft(final VariableContextEntry context,
                                          final Object right) {
            final Object value = context.extractor.getValue( right );
            if ( ((ObjectVariableContextEntry) context).left == null ) {
                return value == null;
            }
            return ((ObjectVariableContextEntry) context).left.equals( value );
        }

        public String toString() {
            return "String ==";
        }
    }

    static class StringNotEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new StringNotEqualEvaluator();

        private StringNotEqualEvaluator() {
            super( ValueType.STRING_TYPE,
                   Operator.NOT_EQUAL );
        }

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            final Object value1 = extractor.getValue( object1 );
            final Object value2 = object2.getValue();
            if ( value1 == null ) {
                return value2 != null;
            }
            return !value1.equals( value2 );
        }

        public boolean evaluate(final FieldValue object1,
                                final Extractor extractor,
                                final Object object2) {
            final Object value1 = object1.getValue();
            final Object value2 = extractor.getValue( object2 );
            if ( value1 == null ) {
                return value2 != null;
            }
            return !value1.equals( value2 );
        }

        public boolean evaluateCachedRight(final VariableContextEntry context,
                                           final Object left) {
            final Object value = context.declaration.getExtractor().getValue( left );
            if ( value == null ) {
                return ((ObjectVariableContextEntry) context).right != null;
            }
            return value.equals( ((ObjectVariableContextEntry) context).right );
        }

        public boolean evaluateCachedLeft(final VariableContextEntry context,
                                          final Object right) {
            final Object value = context.extractor.getValue( right );
            if ( ((ObjectVariableContextEntry) context).left == null ) {
                return value != null;
            }
            return !((ObjectVariableContextEntry) context).left.equals( value );
        }

        public String toString() {
            return "String !=";
        }
    }

    static class StringMatchesEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new StringMatchesEvaluator();

        private StringMatchesEvaluator() {
            super( ValueType.STRING_TYPE,
                   Operator.MATCHES );
        }

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            final String value1 = (String) extractor.getValue( object1 );
            final String value2 = (String) object2.getValue();
            if ( value1 == null ) {
                return false;
            }
            return value1.matches( value2 );
        }

        public boolean evaluate(final FieldValue object1,
                                final Extractor extractor,
                                final Object object2) {
            final String value1 = (String) object1.getValue();
            final String value2 = (String) extractor.getValue( object2 );
            if ( value1 == null ) {
                return false;
            }
            return value1.matches( value2 );
        }

        public boolean evaluateCachedRight(final VariableContextEntry context,
                                           final Object left) {
            final String value = (String) context.declaration.getExtractor().getValue( left );
            if ( value == null ) {
                return false;
            }
            return value.matches( (String) ((ObjectVariableContextEntry) context).right );
        }

        public boolean evaluateCachedLeft(final VariableContextEntry context,
                                          final Object right) {
            if ( ((ObjectVariableContextEntry) context).left == null ) {
                return false;
            }
            final String value = (String) context.extractor.getValue( right );
            return ((String) ((ObjectVariableContextEntry) context).left).matches( value );
        }

        public String toString() {
            return "String !=";
        }
    }

}