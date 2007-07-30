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
import org.drools.common.InternalWorkingMemory;
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

    private static final long       serialVersionUID = 400L;
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
        } else if ( operator == Operator.EXCLUDES ) {
            return ArrayExcludesEvaluator.INSTANCE;
        } else if ( operator == Operator.NOT_CONTAINS ) {
            return ArrayExcludesEvaluator.INSTANCE; // 'not contains' and 'excludes' are synonyms
        } else if ( operator == Operator.MEMBEROF ) {
            return ArrayMemberOfEvaluator.INSTANCE;
        } else if ( operator == Operator.NOTMEMBEROF ) {
            return ArrayNotMemberOfEvaluator.INSTANCE;
        } else {
            throw new RuntimeException( "Operator '" + operator + "' does not exist for ArrayEvaluator" );
        }
    }
    
    static class ArrayEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new ArrayEqualEvaluator();

        private ArrayEqualEvaluator() {
            super( ValueType.ARRAY_TYPE,
                   Operator.EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            final Object value1 = extractor.getValue( workingMemory, object1 );
            final Object value2 = object2.getValue();
            if ( value1 == null ) {
                return value2 == null;
            }
            return value1.equals( value2 );
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            final Object value = context.declaration.getExtractor().getValue( workingMemory, left );
            if ( value == null ) {
                return ((ObjectVariableContextEntry) context).right == null;
            }
            return value.equals( ((ObjectVariableContextEntry) context).right );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            final Object value = context.extractor.getValue( workingMemory, right );
            if ( ((ObjectVariableContextEntry) context).left == null ) {
                return value == null;
            }
            return ((ObjectVariableContextEntry) context).left.equals( value );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            final Object value1 = extractor1.getValue( workingMemory, object1 );
            final Object value2 = extractor2.getValue( workingMemory, object2 );
            if ( value1 == null ) {
                return value2 == null;
            }
            return value1.equals( value2 );
        }

        public String toString() {
            return "Array ==";
        }

    }

    static class ArrayNotEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new ArrayNotEqualEvaluator();

        private ArrayNotEqualEvaluator() {
            super( ValueType.ARRAY_TYPE,
                   Operator.NOT_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            final Object value1 = extractor.getValue( workingMemory, object1 );
            final Object value2 = object2.getValue();
            if ( value1 == null ) {
                return value2 != null;
            }
            return !value1.equals( value2 );
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            final Object value = context.declaration.getExtractor().getValue( workingMemory, left );
            if ( value == null ) {
                return ((ObjectVariableContextEntry) context).right != null;
            }
            return !value.equals( ((ObjectVariableContextEntry) context).right );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            final Object value = context.extractor.getValue( workingMemory, right );
            if ( ((ObjectVariableContextEntry) context).left == null ) {
                return value != null;
            }
            return !((ObjectVariableContextEntry) context).left.equals( value );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            final Object value1 = extractor1.getValue( workingMemory, object1 );
            final Object value2 = extractor2.getValue( workingMemory, object2 );
            if ( value1 == null ) {
                return value2 != null;
            }
            return !value1.equals( value2 );
        }

        public String toString() {
            return "Array !=";
        }
    }

    static class ArrayContainsEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new ArrayContainsEvaluator();

        private ArrayContainsEvaluator() {
            super( ValueType.ARRAY_TYPE,
                   Operator.CONTAINS );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            final Object value = object2.getValue();
            final Object[] array = (Object[]) extractor.getValue( workingMemory, object1 );
            if( array == null )
                return false;
            return ArrayUtils.search( array,
                                        value ) >= 0;
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            final Object value = context.declaration.getExtractor().getValue( workingMemory, left );
            final Object[] array = (Object[]) ((ObjectVariableContextEntry) context).right;
            if( array == null )
                return false;
            return ArrayUtils.search( array,
                                        value ) >= 0;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            final Object value = ((ObjectVariableContextEntry) context).left;
            final Object[] array = (Object[]) context.extractor.getValue( workingMemory, right );
            if( array == null )
                return false;
            return ArrayUtils.search( array,
                                        value ) >= 0;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            final Object value = extractor2.getValue( workingMemory, object2 );
            final Object[] array = (Object[]) extractor1.getValue( workingMemory, object1 );

            if( array == null )
                return false;
            return ArrayUtils.search( array,
                                        value ) >= 0;
        }

        public String toString() {
            return "Array contains";
        }
    }

    static class ArrayExcludesEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new ArrayExcludesEvaluator();

        private ArrayExcludesEvaluator() {
            super( ValueType.ARRAY_TYPE,
                   Operator.EXCLUDES );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            final Object value = object2.getValue();
            final Object[] array = (Object[]) extractor.getValue( workingMemory, object1 );
            if( array == null )
                return false;
            return ArrayUtils.search( array,
                                        value ) < 0;
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            final Object value = context.declaration.getExtractor().getValue( workingMemory, left );
            final Object[] array = (Object[]) ((ObjectVariableContextEntry) context).right;
            if( array == null )
                return false;
            return ArrayUtils.search( array,
                                        value ) < 0;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            final Object value = ((ObjectVariableContextEntry) context).left;
            final Object[] array = (Object[]) context.extractor.getValue( workingMemory, right );
            if( array == null )
                return false;
            return ArrayUtils.search( array,
                                        value ) < 0;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            final Object value = extractor2.getValue( workingMemory, object2 );
            final Object[] array = (Object[]) extractor1.getValue( workingMemory, object1 );

            if( array == null )
                return false;
            return ArrayUtils.search( array,
                                        value ) < 0;
        }

        public String toString() {
            return "Array excludes";
        }
    }

    static class ArrayMemberOfEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new ArrayMemberOfEvaluator();

        private ArrayMemberOfEvaluator() {
            super( ValueType.ARRAY_TYPE,
                   Operator.MEMBEROF );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            final Object[] array = (Object[]) object2.getValue();
            final Object value = extractor.getValue( workingMemory, object1 );
            if( array == null )
                return false;
            return ArrayUtils.search( array,
                                        value ) >= 0;
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            final Object[] array = (Object[]) context.declaration.getExtractor().getValue( workingMemory, left );
            final Object value = ((ObjectVariableContextEntry) context).right;
            if( array == null )
                return false;
            return ArrayUtils.search( array,
                                        value ) >= 0;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            final Object[] array = (Object[]) ((ObjectVariableContextEntry) context).left;
            final Object value = context.extractor.getValue( workingMemory, right );
            if( array == null )
                return false;
            return ArrayUtils.search( array,
                                        value ) >= 0;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            final Object[] array = (Object[]) extractor2.getValue( workingMemory, object2 );
            final Object value = extractor1.getValue( workingMemory, object1 );

            if( array == null )
                return false;
            return ArrayUtils.search( array,
                                        value ) >= 0;
        }

        public String toString() {
            return "Array memberOf";
        }
    }

    static class ArrayNotMemberOfEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new ArrayNotMemberOfEvaluator();

        private ArrayNotMemberOfEvaluator() {
            super( ValueType.ARRAY_TYPE,
                   Operator.NOTMEMBEROF );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            final Object[] array = (Object[]) object2.getValue();
            final Object value = extractor.getValue( workingMemory, object1 );
            if( array == null )
                return false;
            return ArrayUtils.search( array,
                                        value ) < 0;
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            final Object[] array = (Object[]) context.declaration.getExtractor().getValue( workingMemory, left );
            final Object value = ((ObjectVariableContextEntry) context).right;
            if( array == null )
                return false;
            return ArrayUtils.search( array,
                                        value ) < 0;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            final Object[] array = (Object[]) ((ObjectVariableContextEntry) context).left;
            final Object value = context.extractor.getValue( workingMemory, right );
            if( array == null )
                return false;
            return ArrayUtils.search( array,
                                        value ) < 0;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            final Object[] array = (Object[]) extractor2.getValue( workingMemory, object2 );
            final Object value = extractor1.getValue( workingMemory, object1 );

            if( array == null )
                return false;
            return ArrayUtils.search( array,
                                        value ) < 0;
        }

        public String toString() {
            return "Array not memberOf";
        }
    }
    
    /**
     * Utility functions for arrays
     * 
     * @author etirelli
     */
    static final class ArrayUtils {
        
        public static final int search( Object[] array, Object value ) {
            int index = -1;
            for( int i = 0; i < array.length; i++ ) {
                if(( array[i] == null && value == null ) ||
                   ( array[i] != null && array[i].equals( value ) ) ) {
                    index = i;
                    break;
                }
            }
            return index;
        }

        public static final int search( boolean[] array, boolean value ) {
            int index = -1;
            for( int i = 0; i < array.length; i++ ) {
                if( array[i] == value ) {
                    index = i;
                    break;
                }
            }
            return index;
        }

        public static final int search( byte[] array, byte value ) {
            int index = -1;
            for( int i = 0; i < array.length; i++ ) {
                if( array[i] == value ) {
                    index = i;
                    break;
                }
            }
            return index;
        }

        public static final int search( short[] array, short value ) {
            int index = -1;
            for( int i = 0; i < array.length; i++ ) {
                if( array[i] == value ) {
                    index = i;
                    break;
                }
            }
            return index;
        }

        public static final int search( int[] array, int value ) {
            int index = -1;
            for( int i = 0; i < array.length; i++ ) {
                if( array[i] == value ) {
                    index = i;
                    break;
                }
            }
            return index;
        }

        public static final int search( long[] array, long value ) {
            int index = -1;
            for( int i = 0; i < array.length; i++ ) {
                if( array[i] == value ) {
                    index = i;
                    break;
                }
            }
            return index;
        }

        public static final int search( float[] array, float value ) {
            int index = -1;
            for( int i = 0; i < array.length; i++ ) {
                if( array[i] == value ) {
                    index = i;
                    break;
                }
            }
            return index;
        }

        public static final int search( double[] array, double value ) {
            int index = -1;
            for( int i = 0; i < array.length; i++ ) {
                if( array[i] == value ) {
                    index = i;
                    break;
                }
            }
            return index;
        }

        public static final int search( char[] array, char value ) {
            int index = -1;
            for( int i = 0; i < array.length; i++ ) {
                if( array[i] == value ) {
                    index = i;
                    break;
                }
            }
            return index;
        }

    }
    
}