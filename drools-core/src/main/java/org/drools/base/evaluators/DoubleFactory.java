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
import org.drools.rule.VariableContextEntry;
import org.drools.rule.VariableRestriction.DoubleVariableContextEntry;
import org.drools.spi.Evaluator;
import org.drools.spi.Extractor;
import org.drools.spi.FieldValue;

public class DoubleFactory
    implements
    EvaluatorFactory {

    private static final long       serialVersionUID = -3853062740291829023L;
    private static EvaluatorFactory INSTANCE         = new DoubleFactory();

    private DoubleFactory() {

    }

    public static EvaluatorFactory getInstance() {
        if ( DoubleFactory.INSTANCE == null ) {
            DoubleFactory.INSTANCE = new DoubleFactory();
        }
        return DoubleFactory.INSTANCE;
    }

    public Evaluator getEvaluator(final Operator operator) {
        if ( operator == Operator.EQUAL ) {
            return DoubleEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.NOT_EQUAL ) {
            return DoubleNotEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.LESS ) {
            return DoubleLessEvaluator.INSTANCE;
        } else if ( operator == Operator.LESS_OR_EQUAL ) {
            return DoubleLessOrEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.GREATER ) {
            return DoubleGreaterEvaluator.INSTANCE;
        } else if ( operator == Operator.GREATER_OR_EQUAL ) {
            return DoubleGreaterOrEqualEvaluator.INSTANCE;
        } else {
            throw new RuntimeException( "Operator '" + operator + "' does not exist for DoubleEvaluator" );
        }
    }

    static class DoubleEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new DoubleEqualEvaluator();

        private DoubleEqualEvaluator() {
            super( ValueType.DOUBLE_TYPE,
                   Operator.EQUAL );
        }

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            // TODO: we are not handling delta right now... maybe we should
            return extractor.getDoubleValue( object1 ) == object2.getDoubleValue();
        }

        public boolean evaluate(final FieldValue object1,
                                final Extractor extractor,
                                final Object object2) {
            // TODO: we are not handling delta right now... maybe we should
            return object1.getDoubleValue() == extractor.getDoubleValue( object2 );
        }

        public boolean evaluateCachedRight(final VariableContextEntry context,
                                           final Object left) {
            // TODO: we are not handling delta right now... maybe we should
            return context.getVariableDeclaration().getExtractor().getDoubleValue( left ) == ((DoubleVariableContextEntry) context).right;
        }

        public boolean evaluateCachedLeft(final VariableContextEntry context,
                                          final Object right) {
            // TODO: we are not handling delta right now... maybe we should
            return ((DoubleVariableContextEntry) context).left == context.getFieldExtractor().getDoubleValue( right );
        }

        public String toString() {
            return "Double ==";
        }
    }

    static class DoubleNotEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new DoubleNotEqualEvaluator();

        private DoubleNotEqualEvaluator() {
            super( ValueType.DOUBLE_TYPE,
                   Operator.NOT_EQUAL );
        }

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            // TODO: we are not handling delta right now... maybe we should
            return extractor.getDoubleValue( object1 ) != object2.getDoubleValue();
        }

        public boolean evaluate(final FieldValue object1,
                                final Extractor extractor,
                                final Object object2) {
            // TODO: we are not handling delta right now... maybe we should
            return object1.getDoubleValue() != extractor.getDoubleValue( object2 );
        }

        public boolean evaluateCachedRight(final VariableContextEntry context,
                                           final Object left) {
            // TODO: we are not handling delta right now... maybe we should
            return context.getVariableDeclaration().getExtractor().getDoubleValue( left ) != ((DoubleVariableContextEntry) context).right;
        }

        public boolean evaluateCachedLeft(final VariableContextEntry context,
                                          final Object right) {
            // TODO: we are not handling delta right now... maybe we should
            return ((DoubleVariableContextEntry) context).left != context.getFieldExtractor().getDoubleValue( right );
        }

        public String toString() {
            return "Double !=";
        }
    }

    static class DoubleLessEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new DoubleLessEvaluator();

        private DoubleLessEvaluator() {
            super( ValueType.DOUBLE_TYPE,
                   Operator.LESS );
        }

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            // TODO: we are not handling delta right now... maybe we should
            return extractor.getDoubleValue( object1 ) < object2.getDoubleValue();
        }

        public boolean evaluate(final FieldValue object1,
                                final Extractor extractor,
                                final Object object2) {
            // TODO: we are not handling delta right now... maybe we should
            return object1.getDoubleValue() < extractor.getDoubleValue( object2 );
        }

        public boolean evaluateCachedRight(final VariableContextEntry context,
                                           final Object left) {
            // TODO: we are not handling delta right now... maybe we should
            return context.getVariableDeclaration().getExtractor().getDoubleValue( left ) < ((DoubleVariableContextEntry) context).right;
        }

        public boolean evaluateCachedLeft(final VariableContextEntry context,
                                          final Object right) {
            // TODO: we are not handling delta right now... maybe we should
            return ((DoubleVariableContextEntry) context).left < context.getFieldExtractor().getDoubleValue( right );
        }

        public String toString() {
            return "Double <";
        }
    }

    static class DoubleLessOrEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new DoubleLessOrEqualEvaluator();

        private DoubleLessOrEqualEvaluator() {
            super( ValueType.DOUBLE_TYPE,
                   Operator.LESS_OR_EQUAL );
        }

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            // TODO: we are not handling delta right now... maybe we should
            return extractor.getDoubleValue( object1 ) <= object2.getDoubleValue();
        }

        public boolean evaluate(final FieldValue object1,
                                final Extractor extractor,
                                final Object object2) {
            // TODO: we are not handling delta right now... maybe we should
            return object1.getDoubleValue() <= extractor.getDoubleValue( object2 );
        }

        public boolean evaluateCachedRight(final VariableContextEntry context,
                                           final Object left) {
            // TODO: we are not handling delta right now... maybe we should
            return context.getVariableDeclaration().getExtractor().getDoubleValue( left ) <= ((DoubleVariableContextEntry) context).right;
        }

        public boolean evaluateCachedLeft(final VariableContextEntry context,
                                          final Object right) {
            // TODO: we are not handling delta right now... maybe we should
            return ((DoubleVariableContextEntry) context).left <= context.getFieldExtractor().getDoubleValue( right );
        }

        public String toString() {
            return "Double <=";
        }
    }

    static class DoubleGreaterEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new DoubleGreaterEvaluator();

        private DoubleGreaterEvaluator() {
            super( ValueType.DOUBLE_TYPE,
                   Operator.GREATER );
        }

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            // TODO: we are not handling delta right now... maybe we should
            return extractor.getDoubleValue( object1 ) > object2.getDoubleValue();
        }

        public boolean evaluate(final FieldValue object1,
                                final Extractor extractor,
                                final Object object2) {
            // TODO: we are not handling delta right now... maybe we should
            return object1.getDoubleValue() > extractor.getDoubleValue( object2 );
        }

        public boolean evaluateCachedRight(final VariableContextEntry context,
                                           final Object left) {
            // TODO: we are not handling delta right now... maybe we should
            return context.getVariableDeclaration().getExtractor().getDoubleValue( left ) > ((DoubleVariableContextEntry) context).right;
        }

        public boolean evaluateCachedLeft(final VariableContextEntry context,
                                          final Object right) {
            // TODO: we are not handling delta right now... maybe we should
            return ((DoubleVariableContextEntry) context).left > context.getFieldExtractor().getDoubleValue( right );
        }

        public String toString() {
            return "Double >";
        }
    }

    static class DoubleGreaterOrEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long      serialVersionUID = 320;
        private final static Evaluator INSTANCE         = new DoubleGreaterOrEqualEvaluator();

        private DoubleGreaterOrEqualEvaluator() {
            super( ValueType.DOUBLE_TYPE,
                   Operator.GREATER_OR_EQUAL );
        }

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            // TODO: we are not handling delta right now... maybe we should
            return extractor.getDoubleValue( object1 ) >= object2.getDoubleValue();
        }

        public boolean evaluate(final FieldValue object1,
                                final Extractor extractor,
                                final Object object2) {
            // TODO: we are not handling delta right now... maybe we should
            return object1.getDoubleValue() >= extractor.getDoubleValue( object2 );
        }

        public boolean evaluateCachedRight(final VariableContextEntry context,
                                           final Object left) {
            // TODO: we are not handling delta right now... maybe we should
            return context.getVariableDeclaration().getExtractor().getDoubleValue( left ) >= ((DoubleVariableContextEntry) context).right;
        }

        public boolean evaluateCachedLeft(final VariableContextEntry context,
                                          final Object right) {
            // TODO: we are not handling delta right now... maybe we should
            return ((DoubleVariableContextEntry) context).left >= context.getFieldExtractor().getDoubleValue( right );
        }

        public String toString() {
            return "Double >=";
        }
    }
}