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
        } else if ( operator == Operator.NOT_MATCHES ) {
            return StringNotMatchesEvaluator.INSTANCE;
        } else if ( operator == Operator.MEMBEROF ) {
            return StringMemberOfEvaluator.INSTANCE;
        } else if ( operator == Operator.NOTMEMBEROF ) {
            return StringNotMemberOfEvaluator.INSTANCE;
        } else {
            throw new RuntimeException( "Operator '" + operator + "' does not exist for StringEvaluator" );
        }
    }

    static class StringEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new StringEqualEvaluator();

        private StringEqualEvaluator() {
            super( ValueType.STRING_TYPE,
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
            return "String ==";
        }

    }

    static class StringNotEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new StringNotEqualEvaluator();

        private StringNotEqualEvaluator() {
            super( ValueType.STRING_TYPE,
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
            return "String !=";
        }
    }

    static class StringMatchesEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new StringMatchesEvaluator();

        private StringMatchesEvaluator() {
            super( ValueType.STRING_TYPE,
                   Operator.MATCHES );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            final String value1 = (String) extractor.getValue( workingMemory, object1 );
            final String value2 = (String) object2.getValue();
            if ( value1 == null ) {
                return false;
            }
            return value1.matches( value2 );
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            final String value = (String) ((ObjectVariableContextEntry) context).right;
            if ( value == null ) {
                return false;
            }
            return value.matches( (String) context.declaration.getExtractor().getValue( workingMemory, left ) );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            final String value = (String) context.extractor.getValue( workingMemory, right );
            if ( value == null ) {
                return false;
            }
            return value.matches( (String) ((ObjectVariableContextEntry) context).left );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            final Object value1 = extractor1.getValue( workingMemory, object1 );
            final Object value2 = extractor2.getValue( workingMemory, object2 );
            if ( value1 == null ) {
                return false;
            }
            return ((String) value1).matches( (String) value2 );
        }

        public String toString() {
            return "String matches";
        }
    }
    
    static class StringNotMatchesEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new StringNotMatchesEvaluator();

        private StringNotMatchesEvaluator() {
            super( ValueType.STRING_TYPE,
                   Operator.NOT_MATCHES );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            final String value1 = (String) extractor.getValue( workingMemory, object1 );
            final String value2 = (String) object2.getValue();
            if ( value1 == null ) {
                return false;
            }
            return ! value1.matches( value2 );
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            final String value = (String) ((ObjectVariableContextEntry) context).right;
            if ( value == null ) {
                return false;
            }
            return ! value.matches( (String) context.declaration.getExtractor().getValue( workingMemory, left ) );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            final String value = (String) context.extractor.getValue( workingMemory, right );
            if ( value == null ) {
                return false;
            }
            return ! value.matches( (String) ((ObjectVariableContextEntry) context).left );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            final Object value1 = extractor1.getValue( workingMemory, object1 );
            final Object value2 = extractor2.getValue( workingMemory, object2 );
            if ( value1 == null ) {
                return false;
            }
            return ! ((String) value1).matches( (String) value2 );
        }

        public String toString() {
            return "String not matches";
        }
    }
    
    static class StringMemberOfEvaluator extends BaseMemberOfEvaluator {

        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new StringMemberOfEvaluator();

        private StringMemberOfEvaluator() {
            super( ValueType.STRING_TYPE,
                   Operator.MEMBEROF );
        }

        public String toString() {
            return "String memberOf";
        }
    }

    static class StringNotMemberOfEvaluator extends BaseNotMemberOfEvaluator {

        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new StringNotMemberOfEvaluator();

        private StringNotMemberOfEvaluator() {
            super( ValueType.STRING_TYPE,
                   Operator.NOTMEMBEROF );
        }

        public String toString() {
            return "String not memberOf";
        }
    }
    

}