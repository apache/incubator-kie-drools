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
import org.drools.rule.VariableRestriction.DoubleVariableContextEntry;
import org.drools.rule.VariableRestriction.VariableContextEntry;
import org.drools.spi.Evaluator;
import org.drools.spi.Extractor;
import org.drools.spi.FieldValue;

public class FloatFactory
    implements
    EvaluatorFactory {

    private static final long       serialVersionUID = -4254964760901343619L;
    private static EvaluatorFactory INSTANCE         = new FloatFactory();

    private FloatFactory() {

    }

    public static EvaluatorFactory getInstance() {
        if ( FloatFactory.INSTANCE == null ) {
            FloatFactory.INSTANCE = new FloatFactory();
        }
        return FloatFactory.INSTANCE;
    }

    public Evaluator getEvaluator(final Operator operator) {
        if ( operator == Operator.EQUAL ) {
            return FloatEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.NOT_EQUAL ) {
            return FloatNotEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.LESS ) {
            return FloatLessEvaluator.INSTANCE;
        } else if ( operator == Operator.LESS_OR_EQUAL ) {
            return FloatLessOrEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.GREATER ) {
            return FloatGreaterEvaluator.INSTANCE;
        } else if ( operator == Operator.GREATER_OR_EQUAL ) {
            return FloatGreaterOrEqualEvaluator.INSTANCE;
        } else {
            throw new RuntimeException( "Operator '" + operator + "' does not exist for FloatEvaluator" );
        }
    }

    static class FloatEqualEvaluator extends BaseEvaluator {

        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new FloatEqualEvaluator();

        private FloatEqualEvaluator() {
            super( ValueType.FLOAT_TYPE,
                   Operator.EQUAL );
        }

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            // TODO: we are not handling delta right now... maybe we should
            return extractor.getFloatValue( object1 ) == object2.getFloatValue();
        }

        public boolean evaluateCachedRight(final VariableContextEntry context,
                                           final Object left) {
            // TODO: we are not handling delta right now... maybe we should
            return context.declaration.getExtractor().getFloatValue( left ) == ((DoubleVariableContextEntry) context).right;
        }

        public boolean evaluateCachedLeft(final VariableContextEntry context,
                                          final Object right) {
            // TODO: we are not handling delta right now... maybe we should
            return ((DoubleVariableContextEntry) context).left == context.extractor.getFloatValue( right );
        }

        public boolean evaluate(Extractor extractor,
                                Object object1,
                                Object object2) {
            // TODO: we are not handling delta right now... maybe we should
            return extractor.getFloatValue( object1 ) == extractor.getFloatValue( object2 );
        }

        public String toString() {
            return "Float ==";
        }
    }

    static class FloatNotEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new FloatNotEqualEvaluator();

        private FloatNotEqualEvaluator() {
            super( ValueType.FLOAT_TYPE,
                   Operator.NOT_EQUAL );
        }

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            // TODO: we are not handling delta right now... maybe we should
            return extractor.getFloatValue( object1 ) != object2.getFloatValue();
        }

        public boolean evaluateCachedRight(final VariableContextEntry context,
                                           final Object left) {
            // TODO: we are not handling delta right now... maybe we should
            return context.declaration.getExtractor().getFloatValue( left ) != ((DoubleVariableContextEntry) context).right;
        }

        public boolean evaluateCachedLeft(final VariableContextEntry context,
                                          final Object right) {
            // TODO: we are not handling delta right now... maybe we should
            return ((DoubleVariableContextEntry) context).left != context.extractor.getFloatValue( right );
        }

        public boolean evaluate(Extractor extractor,
                                Object object1,
                                Object object2) {
            // TODO: we are not handling delta right now... maybe we should
            return extractor.getFloatValue( object1 ) != extractor.getFloatValue( object2 );
        }

        public String toString() {
            return "Float !=";
        }
    }

    static class FloatLessEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new FloatLessEvaluator();

        private FloatLessEvaluator() {
            super( ValueType.FLOAT_TYPE,
                   Operator.LESS );
        }

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            // TODO: we are not handling delta right now... maybe we should
            return extractor.getFloatValue( object1 ) < object2.getFloatValue();
        }

        public boolean evaluateCachedRight(final VariableContextEntry context,
                                           final Object left) {
            // TODO: we are not handling delta right now... maybe we should
            return ((DoubleVariableContextEntry) context).right < context.declaration.getExtractor().getFloatValue( left );
        }

        public boolean evaluateCachedLeft(final VariableContextEntry context,
                                          final Object right) {
            // TODO: we are not handling delta right now... maybe we should
            return context.extractor.getFloatValue( right ) < ((DoubleVariableContextEntry) context).left;
        }

        public boolean evaluate(Extractor extractor,
                                Object object1,
                                Object object2) {
            // TODO: we are not handling delta right now... maybe we should
            return extractor.getFloatValue( object1 ) < extractor.getFloatValue( object2 );
        }

        public String toString() {
            return "Float <";
        }
    }

    static class FloatLessOrEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new FloatLessOrEqualEvaluator();

        private FloatLessOrEqualEvaluator() {
            super( ValueType.FLOAT_TYPE,
                   Operator.LESS_OR_EQUAL );
        }

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            // TODO: we are not handling delta right now... maybe we should
            return extractor.getFloatValue( object1 ) <= object2.getFloatValue();
        }

        public boolean evaluateCachedRight(final VariableContextEntry context,
                                           final Object left) {
            // TODO: we are not handling delta right now... maybe we should
            return  ((DoubleVariableContextEntry) context).right <= context.declaration.getExtractor().getFloatValue( left );
        }

        public boolean evaluateCachedLeft(final VariableContextEntry context,
                                          final Object right) {
            // TODO: we are not handling delta right now... maybe we should
            return context.extractor.getFloatValue( right ) <= ((DoubleVariableContextEntry) context).left;
        }

        public boolean evaluate(Extractor extractor,
                                Object object1,
                                Object object2) {
            // TODO: we are not handling delta right now... maybe we should
            return extractor.getFloatValue( object1 ) <= extractor.getFloatValue( object2 );
        }

        public String toString() {
            return "Float <=";
        }
    }

    static class FloatGreaterEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new FloatGreaterEvaluator();

        private FloatGreaterEvaluator() {
            super( ValueType.FLOAT_TYPE,
                   Operator.GREATER );
        }

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            // TODO: we are not handling delta right now... maybe we should
            return extractor.getFloatValue( object1 ) > object2.getFloatValue();
        }

        public boolean evaluateCachedRight(final VariableContextEntry context,
                                           final Object left) {
            // TODO: we are not handling delta right now... maybe we should
            return  ((DoubleVariableContextEntry) context).right > context.declaration.getExtractor().getFloatValue( left );
        }

        public boolean evaluateCachedLeft(final VariableContextEntry context,
                                          final Object right) {
            // TODO: we are not handling delta right now... maybe we should
            return context.extractor.getFloatValue( right ) > ((DoubleVariableContextEntry) context).left;
        }

        public boolean evaluate(Extractor extractor,
                                Object object1,
                                Object object2) {
            // TODO: we are not handling delta right now... maybe we should
            return extractor.getFloatValue( object1 ) > extractor.getFloatValue( object2 );
        }

        public String toString() {
            return "Float >";
        }
    }

    static class FloatGreaterOrEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long      serialVersionUID = 320;
        private final static Evaluator INSTANCE         = new FloatGreaterOrEqualEvaluator();

        private FloatGreaterOrEqualEvaluator() {
            super( ValueType.FLOAT_TYPE,
                   Operator.GREATER_OR_EQUAL );
        }

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            // TODO: we are not handling delta right now... maybe we should
            return extractor.getFloatValue( object1 ) >= object2.getFloatValue();
        }

        public boolean evaluateCachedRight(final VariableContextEntry context,
                                           final Object left) {
            // TODO: we are not handling delta right now... maybe we should
            return ((DoubleVariableContextEntry) context).right >= context.declaration.getExtractor().getFloatValue( left );
        }

        public boolean evaluateCachedLeft(final VariableContextEntry context,
                                          final Object right) {
            // TODO: we are not handling delta right now... maybe we should
            return context.extractor.getFloatValue( right ) >= ((DoubleVariableContextEntry) context).left;
        }

        public boolean evaluate(Extractor extractor,
                                Object object1,
                                Object object2) {
            // TODO: we are not handling delta right now... maybe we should
            return extractor.getFloatValue( object1 ) >= extractor.getFloatValue( object2 );
        }

        public String toString() {
            return "Float >=";
        }
    }
}