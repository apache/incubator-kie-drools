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
import org.drools.base.evaluators.IntegerFactory.IntegerEqualEvaluator;
import org.drools.base.evaluators.IntegerFactory.IntegerGreaterEvaluator;
import org.drools.base.evaluators.IntegerFactory.IntegerGreaterOrEqualEvaluator;
import org.drools.base.evaluators.IntegerFactory.IntegerLessEvaluator;
import org.drools.base.evaluators.IntegerFactory.IntegerLessOrEqualEvaluator;
import org.drools.base.evaluators.IntegerFactory.IntegerNotEqualEvaluator;
import org.drools.base.evaluators.ShortFactory.ShortEqualEvaluator;
import org.drools.base.evaluators.ShortFactory.ShortGreaterEvaluator;
import org.drools.base.evaluators.ShortFactory.ShortGreaterOrEqualEvaluator;
import org.drools.base.evaluators.ShortFactory.ShortLessEvaluator;
import org.drools.base.evaluators.ShortFactory.ShortLessOrEqualEvaluator;
import org.drools.base.evaluators.ShortFactory.ShortNotEqualEvaluator;
import org.drools.spi.Evaluator;

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
public class ObjectFactory implements EvaluatorFactory {
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
        }  else {
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

        public boolean evaluate(final Object object1,
                                final Object object2) {
            if ( object1 == null ) {
                return object2 == null;
            }
            return object1.equals( object2 );
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

        public boolean evaluate(final Object object1,
                                final Object object2) {
            if ( object1 == null ) {
                return !(object2 == null);
            }
            return !object1.equals( object2 );
        }

        public String toString() {
            return "Object !=";
        }
    }

    static class ObjectLessEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new ObjectLessEvaluator();

        private ObjectLessEvaluator() {
            super( ValueType.OBJECT_TYPE,
                   Operator.LESS );
        }

        public boolean evaluate(final Object object1,
                                final Object object2) {
            final Comparable comp = (Comparable) object1;
            final int val = comp.compareTo( object2 );
            return val < 0;
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

        public boolean evaluate(final Object object1,
                                final Object object2) {
            final Comparable comp = (Comparable) object1;
            return comp.compareTo( object2 ) <= 0;
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

        public boolean evaluate(final Object object1,
                                final Object object2) {
            final Comparable comp = (Comparable) object1;
            return comp.compareTo( object2 ) > 0;
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

        public boolean evaluate(final Object object1,
                                final Object object2) {
            final Comparable comp = (Comparable) object1;
            return comp.compareTo( object2 ) >= 0;
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

        public boolean evaluate(final Object object1,
                                final Object object2) {
            if ( object2 == null ) {
                return false;
            }
            final Collection col = (Collection) object1;
            return col.contains( object2 );
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

        public boolean evaluate(final Object object1,
                                final Object object2) {
            if ( object2 == null ) {
                return false;
            }
            final Collection col = (Collection) object1;
            return !col.contains( object2 );
        }

        public String toString() {
            return "Object excludes";
        }
    }

}