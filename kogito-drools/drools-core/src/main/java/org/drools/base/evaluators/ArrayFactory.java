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

import java.util.Arrays;

import org.drools.base.BaseEvaluator;
import org.drools.base.ValueType;
import org.drools.rule.VariableConstraint.ObjectVariableContextEntry;
import org.drools.rule.VariableConstraint.VariableContextEntry;
import org.drools.spi.Evaluator;
import org.drools.spi.Extractor;
import org.drools.spi.FieldValue;

/**
 * For handling simple (non collection) array types.
 * @author Michael Neale
 */
public class ArrayFactory
    implements
    EvaluatorFactory {

    private static final long       serialVersionUID = -5485618486269637287L;
    private static EvaluatorFactory INSTANCE         = new ArrayFactory();

    private ArrayFactory() {

    }

    public static EvaluatorFactory getInstance() {
        if ( INSTANCE == null ) {
            INSTANCE = new ArrayFactory();
        }
        return INSTANCE;
    }

    public Evaluator getEvaluator(final Operator operator) {
        if ( operator == Operator.EQUAL ) {
            return ArrayEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.NOT_EQUAL ) {
            return ArrayNotEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.LESS ) {
            return ArrayContainsEvaluator.INSTANCE;
        } else if ( operator == Operator.CONTAINS ) {
            return ArrayContainsEvaluator.INSTANCE;
        } else {
            throw new RuntimeException( "Operator '" + operator + "' does not exist for ArrayEvaluator" );
        }
    }

    static class ArrayEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new ArrayEqualEvaluator();

        private ArrayEqualEvaluator() {
            super( ValueType.ARRAY_TYPE,
                   Operator.EQUAL );
        }

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            Object value1 = extractor.getValue( object1 );
            Object value2 = object2.getValue();
            if ( value1 == null ) {
                return value2 == null;
            }
            return value1.equals( value2 );
        }

        public boolean evaluate(final FieldValue object1,
                                final Extractor extractor,
                                final Object object2) {
            Object value1 = object1.getValue();
            Object value2 = extractor.getValue( object2 );
            if ( value1 == null ) {
                return value2 == null;
            }
            return value1.equals( value2 );
        }

        public boolean evaluateCachedRight(VariableContextEntry context,
                                           Object left) {
            Object value = context.declaration.getExtractor().getValue( left );
            if ( value == null ) {
                return ((ObjectVariableContextEntry) context).right == null;
            }
            return value.equals( ((ObjectVariableContextEntry) context).right );
        }

        public boolean evaluateCachedLeft(VariableContextEntry context,
                                          Object right) {
            Object value = context.extractor.getValue( right );
            if ( ((ObjectVariableContextEntry) context).left == null ) {
                return value == null;
            }
            return ((ObjectVariableContextEntry) context).left.equals( value );
        }

        public String toString() {
            return "Array ==";
        }
    }

    static class ArrayNotEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new ArrayNotEqualEvaluator();

        private ArrayNotEqualEvaluator() {
            super( ValueType.ARRAY_TYPE,
                   Operator.NOT_EQUAL );
        }

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            Object value1 = extractor.getValue( object1 );
            Object value2 = object2.getValue();
            if ( value1 == null ) {
                return value2 == null;
            }
            return !value1.equals( value2 );
        }

        public boolean evaluate(final FieldValue object1,
                                final Extractor extractor,
                                final Object object2) {
            Object value1 = object1.getValue();
            Object value2 = extractor.getValue( object2 );
            if ( value1 == null ) {
                return value2 == null;
            }
            return !value1.equals( value2 );
        }

        public boolean evaluateCachedRight(VariableContextEntry context,
                                           Object left) {
            Object value = context.declaration.getExtractor().getValue( left );
            if ( value == null ) {
                return ((ObjectVariableContextEntry) context).right == null;
            }
            return !value.equals( ((ObjectVariableContextEntry) context).right );
        }

        public boolean evaluateCachedLeft(VariableContextEntry context,
                                          Object right) {
            Object value = context.extractor.getValue( right );
            if ( ((ObjectVariableContextEntry) context).left == null ) {
                return value == null;
            }
            return !((ObjectVariableContextEntry) context).left.equals( value );
        }

        public String toString() {
            return "Array !=";
        }
    }

    static class ArrayContainsEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new ArrayContainsEvaluator();

        private ArrayContainsEvaluator() {
            super( ValueType.ARRAY_TYPE,
                   Operator.CONTAINS );
        }

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            Object value = object2.getValue();
            final Object[] array = (Object[]) extractor.getValue( object1 );

            if ( Arrays.binarySearch( array,
                                      value ) == -1 ) {
                return false;
            }
            return true;
        }

        public boolean evaluate(final FieldValue object1,
                                final Extractor extractor,
                                final Object object2) {
            Object value = extractor.getValue( object2 );
            final Object[] array = (Object[]) object1.getValue();
            if ( Arrays.binarySearch( array,
                                      value ) == -1 ) {
                return false;
            }
            return true;
        }

        public boolean evaluateCachedRight(VariableContextEntry context,
                                           Object left) {
            Object value = ((ObjectVariableContextEntry) context).right;
            final Object[] array = (Object[]) context.declaration.getExtractor().getValue( left );
            if ( Arrays.binarySearch( array,
                                      value ) == -1 ) {
                return false;
            }
            return true;
        }

        public boolean evaluateCachedLeft(VariableContextEntry context,
                                          Object right) {
            Object value = context.extractor.getValue( right );
            final Object[] array = (Object[]) ((ObjectVariableContextEntry) context).left;
            if ( Arrays.binarySearch( array,
                                      value ) == -1 ) {
                return false;
            }
            return true;
        }

        public String toString() {
            return "Array contains";
        }
    }

}