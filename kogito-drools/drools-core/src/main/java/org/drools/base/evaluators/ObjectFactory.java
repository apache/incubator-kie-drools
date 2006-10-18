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

import java.util.Collection;

import org.drools.base.BaseEvaluator;
import org.drools.base.ValueType;
import org.drools.rule.VariableConstraint.ObjectVariableContextEntry;
import org.drools.rule.VariableConstraint.VariableContextEntry;
import org.drools.spi.Evaluator;
import org.drools.spi.Extractor;
import org.drools.spi.FieldValue;

/**
 * This is the misc "bucket" evaluator factory for objects.
 * It is fairly limited in operations, 
 * and what operations are available are dependent on the exact type.
 * 
 * This supports "<" and ">" etc by requiring objects to implement the comparable interface.
 * Of course, literals will not work with comparator, as it has no way
 * of converting from literal to the appropriate type.
 * 
 * @author Michael Neale
 */
public class ObjectFactory
    implements
    EvaluatorFactory {

    private static final long serialVersionUID = -8547142029512452551L;
    private static EvaluatorFactory INSTANCE = new ObjectFactory();

    private ObjectFactory() {

    }

    public static EvaluatorFactory getInstance() {
        if ( INSTANCE == null ) {
            INSTANCE = new ObjectFactory();
        }
        return INSTANCE;
    }

    public Evaluator getEvaluator(final Operator operator) {
        if ( operator == Operator.EQUAL ) {
            return ObjectEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.NOT_EQUAL ) {
            return ObjectNotEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.LESS ) {
            return ObjectLessEvaluator.INSTANCE;
        } else if ( operator == Operator.LESS_OR_EQUAL ) {
            return ObjectLessOrEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.GREATER ) {
            return ObjectGreaterEvaluator.INSTANCE;
        } else if ( operator == Operator.GREATER_OR_EQUAL ) {
            return ObjectGreaterOrEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.CONTAINS ) {
            return ObjectContainsEvaluator.INSTANCE;
        } else if ( operator == Operator.EXCLUDES ) {
            return ObjectExcludesEvaluator.INSTANCE;
        } else {
            throw new RuntimeException( "Operator '" + operator + "' does not exist for ShortEvaluator" );
        }
    }

    static class ObjectEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new ObjectEqualEvaluator();

        private ObjectEqualEvaluator() {
            super( ValueType.OBJECT_TYPE,
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
            return "Object ==";
        }

    }

    static class ObjectNotEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new ObjectNotEqualEvaluator();

        private ObjectNotEqualEvaluator() {
            super( ValueType.OBJECT_TYPE,
                   Operator.NOT_EQUAL );
        }

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            Object value1 = extractor.getValue( object1 );
            Object value2 = object2.getValue();
            if ( value1 == null ) {
                return value2 != null;
            }
            return !value1.equals( value2 );
        }

        public boolean evaluate(final FieldValue object1,
                                final Extractor extractor,
                                final Object object2) {
            Object value1 = object1.getValue();
            Object value2 = extractor.getValue( object2 );
            if ( value1 == null ) {
                return value2 != null;
            }
            return !value1.equals( value2 );
        }

        public boolean evaluateCachedRight(VariableContextEntry context,
                                           Object left) {
            Object value = context.declaration.getExtractor().getValue( left );
            if ( value == null ) {
                return ((ObjectVariableContextEntry) context).right != null;
            }
            return !value.equals( ((ObjectVariableContextEntry) context).right );
        }

        public boolean evaluateCachedLeft(VariableContextEntry context,
                                          Object right) {
            Object value = context.extractor.getValue( right );
            if ( ((ObjectVariableContextEntry) context).left == null ) {
                return value != null;
            }
            return !((ObjectVariableContextEntry) context).left.equals( value );
        }
        
        public String toString() {
            return "Object !=";
        }
    }

    static class ObjectLessEvaluator extends BaseEvaluator {
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new ObjectLessEvaluator();

        private ObjectLessEvaluator() {
            super( ValueType.OBJECT_TYPE,
                   Operator.LESS );
        }

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            final Comparable comp = (Comparable) extractor.getValue( object1 );
            return comp.compareTo( object2.getValue() ) < 0;
        }

        public boolean evaluate(final FieldValue object1,
                                final Extractor extractor,
                                final Object object2) {
            final Comparable comp = (Comparable) object1.getValue();
            return comp.compareTo( extractor.getValue( object2 ) ) < 0;
        }

        public boolean evaluateCachedRight(VariableContextEntry context,
                                           Object left) {
            final Comparable comp = (Comparable) context.declaration.getExtractor().getValue( left );
            return comp.compareTo( ((ObjectVariableContextEntry) context).right ) < 0;
        }

        public boolean evaluateCachedLeft(VariableContextEntry context,
                                          Object right) {
            final Comparable comp = (Comparable) ((ObjectVariableContextEntry) context).left;
            return comp.compareTo( context.extractor.getValue( right ) ) < 0;
        }
        
        public String toString() {
            return "Object <";
        }
    }

    static class ObjectLessOrEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new ObjectLessOrEqualEvaluator();

        private ObjectLessOrEqualEvaluator() {
            super( ValueType.OBJECT_TYPE,
                   Operator.LESS_OR_EQUAL );
        }

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            final Comparable comp = (Comparable) extractor.getValue( object1 );
            return comp.compareTo( object2.getValue() ) <= 0;
        }

        public boolean evaluate(final FieldValue object1,
                                final Extractor extractor,
                                final Object object2) {
            final Comparable comp = (Comparable) object1.getValue();
            return comp.compareTo( extractor.getValue( object2 ) ) <= 0;
        }

        public boolean evaluateCachedRight(VariableContextEntry context,
                                           Object left) {
            final Comparable comp = (Comparable) context.declaration.getExtractor().getValue( left );
            return comp.compareTo( ((ObjectVariableContextEntry) context).right ) <= 0;
        }

        public boolean evaluateCachedLeft(VariableContextEntry context,
                                          Object right) {
            final Comparable comp = (Comparable) ((ObjectVariableContextEntry) context).left;
            return comp.compareTo( context.extractor.getValue( right ) ) <= 0;
        }
        
        public String toString() {
            return "Object <=";
        }
    }

    static class ObjectGreaterEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new ObjectGreaterEvaluator();

        private ObjectGreaterEvaluator() {
            super( ValueType.OBJECT_TYPE,
                   Operator.GREATER );
        }

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            final Comparable comp = (Comparable) extractor.getValue( object1 );
            return comp.compareTo( object2.getValue() ) >= 0;
        }

        public boolean evaluate(final FieldValue object1,
                                final Extractor extractor,
                                final Object object2) {
            final Comparable comp = (Comparable) object1.getValue();
            return comp.compareTo( extractor.getValue( object2 ) ) >= 0;
        }

        public boolean evaluateCachedRight(VariableContextEntry context,
                                           Object left) {
            final Comparable comp = (Comparable) context.declaration.getExtractor().getValue( left );
            return comp.compareTo( ((ObjectVariableContextEntry) context).right ) >= 0;
        }

        public boolean evaluateCachedLeft(VariableContextEntry context,
                                          Object right) {
            final Comparable comp = (Comparable) ((ObjectVariableContextEntry) context).left;
            return comp.compareTo( context.extractor.getValue( right ) ) >= 0;
        }
        
        public String toString() {
            return "Object >";
        }
    }

    static class ObjectGreaterOrEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new ObjectGreaterOrEqualEvaluator();

        private ObjectGreaterOrEqualEvaluator() {
            super( ValueType.OBJECT_TYPE,
                   Operator.GREATER_OR_EQUAL );
        }

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            final Comparable comp = (Comparable) extractor.getValue( object1 );
            return comp.compareTo( object2.getValue() ) >= 0;
        }

        public boolean evaluate(final FieldValue object1,
                                final Extractor extractor,
                                final Object object2) {
            final Comparable comp = (Comparable) object1.getValue();
            return comp.compareTo( extractor.getValue( object2 ) ) >= 0;
        }

        public boolean evaluateCachedRight(VariableContextEntry context,
                                           Object left) {
            final Comparable comp = (Comparable) context.declaration.getExtractor().getValue( left );
            return comp.compareTo( ((ObjectVariableContextEntry) context).right ) >= 0;
        }

        public boolean evaluateCachedLeft(VariableContextEntry context,
                                          Object right) {
            final Comparable comp = (Comparable) ((ObjectVariableContextEntry) context).left;
            return comp.compareTo( context.extractor.getValue( right ) ) >= 0;
        }
        
        public String toString() {
            return "Object >=";
        }
    }

    static class ObjectContainsEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new ObjectContainsEvaluator();

        private ObjectContainsEvaluator() {
            super( ValueType.OBJECT_TYPE,
                   Operator.CONTAINS );
        }

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            Object value = object2.getValue();
            final Collection col = (Collection) extractor.getValue( object1 );
            return col.contains( value );
        }

        public boolean evaluate(final FieldValue object1,
                                final Extractor extractor,
                                final Object object2) {
            Object value = extractor.getValue( object2);
            final Collection col = (Collection) object1.getValue();
            return col.contains( value );
        }

        public boolean evaluateCachedRight(VariableContextEntry context,
                                           Object left) {
            Object value = ((ObjectVariableContextEntry) context).right;
            final Collection col = (Collection) context.declaration.getExtractor().getValue( left );
            return col.contains( value );
        }

        public boolean evaluateCachedLeft(VariableContextEntry context,
                                          Object right) {
            Object value = context.extractor.getValue( right );
            final Collection col = (Collection) ((ObjectVariableContextEntry) context).left;
            return col.contains( value );
        }
        
        public String toString() {
            return "Object contains";
        }
    }

    static class ObjectExcludesEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new ObjectExcludesEvaluator();

        private ObjectExcludesEvaluator() {
            super( ValueType.OBJECT_TYPE,
                   Operator.EXCLUDES );
        }

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            Object value = object2.getValue();
            final Collection col = (Collection) extractor.getValue( object1 );
            return !col.contains( value );
        }

        public boolean evaluate(final FieldValue object1,
                                final Extractor extractor,
                                final Object object2) {
            Object value = extractor.getValue( object2);
            final Collection col = (Collection) object1.getValue();
            return !col.contains( value );
        }

        public boolean evaluateCachedRight(VariableContextEntry context,
                                           Object left) {
            Object value = ((ObjectVariableContextEntry) context).right;
            final Collection col = (Collection) context.declaration.getExtractor().getValue( left );
            return !col.contains( value );
        }

        public boolean evaluateCachedLeft(VariableContextEntry context,
                                          Object right) {
            Object value = context.extractor.getValue( right );
            final Collection col = (Collection) ((ObjectVariableContextEntry) context).left;
            return !col.contains( value );
        }

        public String toString() {
            return "Object excludes";
        }
    }

}