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

import java.math.BigInteger;

import org.drools.base.BaseEvaluator;
import org.drools.base.ValueType;
import org.drools.rule.VariableConstraint.ObjectVariableContextEntry;
import org.drools.rule.VariableConstraint.VariableContextEntry;
import org.drools.spi.Evaluator;
import org.drools.spi.Extractor;
import org.drools.spi.FieldValue;

public class BigIntegerFactory
    implements
    EvaluatorFactory {

    private static final long       serialVersionUID = 4180922947425495749L;
    private static EvaluatorFactory INSTANCE         = new BigIntegerFactory();

    private BigIntegerFactory() {

    }

    public static EvaluatorFactory getInstance() {
        if ( BigIntegerFactory.INSTANCE == null ) {
            BigIntegerFactory.INSTANCE = new BigIntegerFactory();
        }
        return BigIntegerFactory.INSTANCE;
    }

    public Evaluator getEvaluator(final Operator operator) {
        if ( operator == Operator.EQUAL ) {
            return BigIntegerEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.NOT_EQUAL ) {
            return BigIntegerNotEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.LESS ) {
            return BigIntegerLessEvaluator.INSTANCE;
        } else if ( operator == Operator.LESS_OR_EQUAL ) {
            return BigIntegerLessOrEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.GREATER ) {
            return BigIntegerGreaterEvaluator.INSTANCE;
        } else if ( operator == Operator.GREATER_OR_EQUAL ) {
            return BigIntegerGreaterOrEqualEvaluator.INSTANCE;
        } else {
            throw new RuntimeException( "Operator '" + operator + "' does not exist for BigIntegerEvaluator" );
        }
    }

    static class BigIntegerEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new BigIntegerEqualEvaluator();

        private BigIntegerEqualEvaluator() {
            super( ValueType.BIG_INTEGER_TYPE,
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
            return "BigInteger ==";
        }
    }

    static class BigIntegerNotEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new BigIntegerNotEqualEvaluator();

        private BigIntegerNotEqualEvaluator() {
            super( ValueType.BIG_INTEGER_TYPE,
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
            return !value.equals( ((ObjectVariableContextEntry) context).right );
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
            return "BigInteger !=";
        }
    }

    static class BigIntegerLessEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new BigIntegerLessEvaluator();

        private BigIntegerLessEvaluator() {
            super( ValueType.BIG_INTEGER_TYPE,
                   Operator.LESS );
        }

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            final BigInteger comp = (BigInteger) extractor.getValue( object1 );
            return comp.compareTo( object2.getValue() ) < 0;
        }

        public boolean evaluate(final FieldValue object1,
                                final Extractor extractor,
                                final Object object2) {
            final BigInteger comp = (BigInteger) object1.getValue();
            return comp.compareTo( extractor.getValue( object2 ) ) < 0;
        }

        public boolean evaluateCachedRight(final VariableContextEntry context,
                                           final Object left) {
            final BigInteger comp = (BigInteger) context.declaration.getExtractor().getValue( left );
            return comp.compareTo( ((ObjectVariableContextEntry) context).right ) < 0;
        }

        public boolean evaluateCachedLeft(final VariableContextEntry context,
                                          final Object right) {
            final BigInteger comp = (BigInteger) ((ObjectVariableContextEntry) context).left;
            return comp.compareTo( context.extractor.getValue( right ) ) < 0;
        }

        public String toString() {
            return "BigInteger <";
        }
    }

    static class BigIntegerLessOrEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new BigIntegerLessOrEqualEvaluator();

        private BigIntegerLessOrEqualEvaluator() {
            super( ValueType.BIG_INTEGER_TYPE,
                   Operator.LESS_OR_EQUAL );
        }

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            final BigInteger comp = (BigInteger) extractor.getValue( object1 );
            return comp.compareTo( object2.getValue() ) <= 0;
        }

        public boolean evaluate(final FieldValue object1,
                                final Extractor extractor,
                                final Object object2) {
            final BigInteger comp = (BigInteger) object1.getValue();
            return comp.compareTo( extractor.getValue( object2 ) ) <= 0;
        }

        public boolean evaluateCachedRight(final VariableContextEntry context,
                                           final Object left) {
            final BigInteger comp = (BigInteger) context.declaration.getExtractor().getValue( left );
            return comp.compareTo( ((ObjectVariableContextEntry) context).right ) <= 0;
        }

        public boolean evaluateCachedLeft(final VariableContextEntry context,
                                          final Object right) {
            final BigInteger comp = (BigInteger) ((ObjectVariableContextEntry) context).left;
            return comp.compareTo( context.extractor.getValue( right ) ) <= 0;
        }

        public String toString() {
            return "BigInteger <=";
        }
    }

    static class BigIntegerGreaterEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new BigIntegerGreaterEvaluator();

        private BigIntegerGreaterEvaluator() {
            super( ValueType.BIG_INTEGER_TYPE,
                   Operator.GREATER );
        }

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            final BigInteger comp = (BigInteger) extractor.getValue( object1 );
            return comp.compareTo( object2.getValue() ) > 0;
        }

        public boolean evaluate(final FieldValue object1,
                                final Extractor extractor,
                                final Object object2) {
            final BigInteger comp = (BigInteger) object1.getValue();
            return comp.compareTo( extractor.getValue( object2 ) ) > 0;
        }

        public boolean evaluateCachedRight(final VariableContextEntry context,
                                           final Object left) {
            final BigInteger comp = (BigInteger) context.declaration.getExtractor().getValue( left );
            return comp.compareTo( ((ObjectVariableContextEntry) context).right ) > 0;
        }

        public boolean evaluateCachedLeft(final VariableContextEntry context,
                                          final Object right) {
            final BigInteger comp = (BigInteger) ((ObjectVariableContextEntry) context).left;
            return comp.compareTo( context.extractor.getValue( right ) ) > 0;
        }

        public String toString() {
            return "BigInteger >";
        }
    }

    static class BigIntegerGreaterOrEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long      serialVersionUID = 320;
        private final static Evaluator INSTANCE         = new BigIntegerGreaterOrEqualEvaluator();

        private BigIntegerGreaterOrEqualEvaluator() {
            super( ValueType.BIG_INTEGER_TYPE,
                   Operator.GREATER_OR_EQUAL );
        }

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            final BigInteger comp = (BigInteger) extractor.getValue( object1 );
            return comp.compareTo( object2.getValue() ) >= 0;
        }

        public boolean evaluate(final FieldValue object1,
                                final Extractor extractor,
                                final Object object2) {
            final BigInteger comp = (BigInteger) object1.getValue();
            return comp.compareTo( extractor.getValue( object2 ) ) >= 0;
        }

        public boolean evaluateCachedRight(final VariableContextEntry context,
                                           final Object left) {
            final BigInteger comp = (BigInteger) context.declaration.getExtractor().getValue( left );
            return comp.compareTo( ((ObjectVariableContextEntry) context).right ) >= 0;
        }

        public boolean evaluateCachedLeft(final VariableContextEntry context,
                                          final Object right) {
            final BigInteger comp = (BigInteger) ((ObjectVariableContextEntry) context).left;
            return comp.compareTo( context.extractor.getValue( right ) ) >= 0;
        }

        public String toString() {
            return "BigInteger >=";
        }
    }
}