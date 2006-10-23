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
import org.drools.rule.VariableRestriction.ObjectVariableContextEntry;
import org.drools.rule.VariableRestriction.VariableContextEntry;
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
        if ( ArrayFactory.INSTANCE == null ) {
            ArrayFactory.INSTANCE = new ArrayFactory();
        }
        return ArrayFactory.INSTANCE;
    }

    public Evaluator getEvaluator(final Operator operator) {
        if ( operator == Operator.EQUAL ) {
            return ArrayEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.NOT_EQUAL ) {
            return ArrayNotEqualEvaluator.INSTANCE;
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
            final Object value1 = extractor.getValue( object1 );
            final Object value2 = object2.getValue();
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
            final Object value1 = extractor.getValue( object1 );
            final Object value2 = object2.getValue();
            if ( value1 == null ) {
                return value2 == null;
            }
            return !value1.equals( value2 );
        }

        public boolean evaluateCachedRight(final VariableContextEntry context,
                                           final Object left) {
            final Object value = context.declaration.getExtractor().getValue( left );
            if ( value == null ) {
                return ((ObjectVariableContextEntry) context).right == null;
            }
            return !value.equals( ((ObjectVariableContextEntry) context).right );
        }

        public boolean evaluateCachedLeft(final VariableContextEntry context,
                                          final Object right) {
            final Object value = context.extractor.getValue( right );
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
            final Object value = object2.getValue();
            final Object[] array = (Object[]) extractor.getValue( object1 );

            if ( Arrays.binarySearch( array,
                                      value ) == -1 ) {
                return false;
            }
            return true;
        }

        public boolean evaluateCachedRight(final VariableContextEntry context,
                                           final Object left) {
            final Object value = context.declaration.getExtractor().getValue( left );
            final Object[] array = (Object[]) ((ObjectVariableContextEntry) context).right;
            if ( Arrays.binarySearch( array,
                                      value ) == -1 ) {
                return false;
            }
            return true;
        }

        public boolean evaluateCachedLeft(final VariableContextEntry context,
                                          final Object right) {
            final Object value = ((ObjectVariableContextEntry) context).left;
            final Object[] array = (Object[]) context.extractor.getValue( right );
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