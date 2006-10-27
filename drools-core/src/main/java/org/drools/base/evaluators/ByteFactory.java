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
import org.drools.rule.VariableRestriction.LongVariableContextEntry;
import org.drools.rule.VariableRestriction.VariableContextEntry;
import org.drools.spi.Evaluator;
import org.drools.spi.Extractor;
import org.drools.spi.FieldValue;

public class ByteFactory
    implements
    EvaluatorFactory {

    private static final long       serialVersionUID = -2213953461197502182L;
    private static EvaluatorFactory INSTANCE         = new ByteFactory();

    private ByteFactory() {

    }

    public static EvaluatorFactory getInstance() {
        if ( ByteFactory.INSTANCE == null ) {
            ByteFactory.INSTANCE = new ByteFactory();
        }
        return ByteFactory.INSTANCE;
    }

    public Evaluator getEvaluator(final Operator operator) {
        if ( operator == Operator.EQUAL ) {
            return ByteEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.NOT_EQUAL ) {
            return ByteNotEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.LESS ) {
            return ByteLessEvaluator.INSTANCE;
        } else if ( operator == Operator.LESS_OR_EQUAL ) {
            return ByteLessOrEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.GREATER ) {
            return ByteGreaterEvaluator.INSTANCE;
        } else if ( operator == Operator.GREATER_OR_EQUAL ) {
            return ByteGreaterOrEqualEvaluator.INSTANCE;
        } else {
            throw new RuntimeException( "Operator '" + operator + "' does not exist for ByteEvaluator" );
        }
    }

    static class ByteEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = -2123381297852695049L;
        public final static Evaluator INSTANCE         = new ByteEqualEvaluator();

        private ByteEqualEvaluator() {
            super( ValueType.BYTE_TYPE,
                   Operator.EQUAL );
        }

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            return extractor.getByteValue( object1 ) == object2.getByteValue();
        }

        public boolean evaluateCachedRight(final VariableContextEntry context,
                                           final Object left) {
            return context.declaration.getExtractor().getByteValue( left ) == ((LongVariableContextEntry) context).right;
        }

        public boolean evaluateCachedLeft(final VariableContextEntry context,
                                          final Object right) {
            return ((LongVariableContextEntry) context).left == context.extractor.getByteValue( right );
        }

        public boolean evaluate(Extractor extractor1,
                                Object object1,
                                Extractor extractor2,
                                Object object2) {
            return extractor1.getByteValue( object1 ) == extractor2.getByteValue( object2 );
        }

        public String toString() {
            return "Byte ==";
        }

    }

    static class ByteNotEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 1745720793613936221L;
        public final static Evaluator INSTANCE         = new ByteNotEqualEvaluator();

        private ByteNotEqualEvaluator() {
            super( ValueType.BYTE_TYPE,
                   Operator.NOT_EQUAL );
        }

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            return extractor.getByteValue( object1 ) != object2.getByteValue();
        }

        public boolean evaluateCachedRight(final VariableContextEntry context,
                                           final Object left) {
            return context.declaration.getExtractor().getByteValue( left ) != ((LongVariableContextEntry) context).right;
        }

        public boolean evaluateCachedLeft(final VariableContextEntry context,
                                          final Object right) {
            return ((LongVariableContextEntry) context).left != context.extractor.getByteValue( right );
        }

        public boolean evaluate(Extractor extractor1,
                                Object object1,
                                Extractor extractor2,
                                Object object2) {
            return extractor1.getByteValue( object1 ) != extractor2.getByteValue( object2 );
        }

        public String toString() {
            return "Byte !=";
        }
    }

    static class ByteLessEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 7327200711399789849L;
        public final static Evaluator INSTANCE         = new ByteLessEvaluator();

        private ByteLessEvaluator() {
            super( ValueType.BYTE_TYPE,
                   Operator.LESS );
        }

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            return extractor.getByteValue( object1 ) < object2.getByteValue();
        }

        public boolean evaluateCachedRight(final VariableContextEntry context,
                                           final Object left) {
            return ((LongVariableContextEntry) context).right <  context.declaration.getExtractor().getByteValue( left );
        }

        public boolean evaluateCachedLeft(final VariableContextEntry context,
                                          final Object right) {
            return context.extractor.getByteValue( right ) < ((LongVariableContextEntry) context).left;
        }

        public boolean evaluate(Extractor extractor1,
                                Object object1,
                                Extractor extractor2,
                                Object object2) {
            return extractor1.getByteValue( object1 ) < extractor2.getByteValue( object2 );
        }

        public String toString() {
            return "Byte <";
        }
    }

    static class ByteLessOrEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 5455645713080692170L;
        public final static Evaluator INSTANCE         = new ByteLessOrEqualEvaluator();

        private ByteLessOrEqualEvaluator() {
            super( ValueType.BYTE_TYPE,
                   Operator.LESS_OR_EQUAL );
        }

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            return extractor.getByteValue( object1 ) <= object2.getByteValue();
        }

        public boolean evaluateCachedRight(final VariableContextEntry context,
                                           final Object left) {
            return ((LongVariableContextEntry) context).right <= context.declaration.getExtractor().getByteValue( left );
        }

        public boolean evaluateCachedLeft(final VariableContextEntry context,
                                          final Object right) {
            return context.extractor.getByteValue( right ) <= ((LongVariableContextEntry) context).left;
        }

        public boolean evaluate(Extractor extractor1,
                                Object object1,
                                Extractor extractor2,
                                Object object2) {
            return extractor1.getByteValue( object1 ) <= extractor2.getByteValue( object2 );
        }

        public String toString() {
            return "Byte <=";
        }
    }

    static class ByteGreaterEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = -3319688501086570921L;
        public final static Evaluator INSTANCE         = new ByteGreaterEvaluator();

        private ByteGreaterEvaluator() {
            super( ValueType.BYTE_TYPE,
                   Operator.GREATER );
        }

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            return extractor.getByteValue( object1 ) > object2.getByteValue();
        }

        public boolean evaluateCachedRight(final VariableContextEntry context,
                                           final Object left) {
            return ((LongVariableContextEntry) context).right > context.declaration.getExtractor().getByteValue( left );
        }

        public boolean evaluateCachedLeft(final VariableContextEntry context,
                                          final Object right) {
            return context.extractor.getByteValue( right ) > ((LongVariableContextEntry) context).left;
        }

        public boolean evaluate(Extractor extractor1,
                                Object object1,
                                Extractor extractor2,
                                Object object2) {
            return extractor1.getByteValue( object1 ) > extractor2.getByteValue( object2 );
        }

        public String toString() {
            return "Byte >";
        }
    }

    static class ByteGreaterOrEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long      serialVersionUID = 8173066470532237341L;
        private final static Evaluator INSTANCE         = new ByteGreaterOrEqualEvaluator();

        private ByteGreaterOrEqualEvaluator() {
            super( ValueType.BYTE_TYPE,
                   Operator.GREATER_OR_EQUAL );
        }

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            return extractor.getByteValue( object1 ) >= object2.getByteValue();
        }

        public boolean evaluateCachedRight(final VariableContextEntry context,
                                           final Object left) {
            return ((LongVariableContextEntry) context).right >= context.declaration.getExtractor().getByteValue( left );
        }

        public boolean evaluateCachedLeft(final VariableContextEntry context,
                                          final Object right) {
            return context.extractor.getByteValue( right ) >= ((LongVariableContextEntry) context).left;
        }

        public boolean evaluate(Extractor extractor1,
                                Object object1,
                                Extractor extractor2,
                                Object object2) {
            return extractor1.getByteValue( object1 ) >= extractor2.getByteValue( object2 );
        }

        public String toString() {
            return "Byte >=";
        }
    }

}