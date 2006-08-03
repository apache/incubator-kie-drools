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
import org.drools.base.evaluators.ShortFactory.ShortEqualEvaluator;
import org.drools.base.evaluators.ShortFactory.ShortGreaterEvaluator;
import org.drools.base.evaluators.ShortFactory.ShortGreaterOrEqualEvaluator;
import org.drools.base.evaluators.ShortFactory.ShortLessEvaluator;
import org.drools.base.evaluators.ShortFactory.ShortLessOrEqualEvaluator;
import org.drools.base.evaluators.ShortFactory.ShortNotEqualEvaluator;
import org.drools.spi.Evaluator;

public class ByteFactory implements EvaluatorFactory {
    private static EvaluatorFactory INSTANCE = new ByteFactory();
    
    private ByteFactory() {
        
    }
    
    public static EvaluatorFactory getInstance() {
        if ( INSTANCE == null ) {
            INSTANCE = new ByteFactory();
        }
        return INSTANCE;
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
        }  else {
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

        public boolean evaluate(final Object object1,
                                final Object object2) {
            if ( object1 == null ) {
                return object2 == null;
            }
            return ((Byte) object1).equals( object2 );
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

        public boolean evaluate(final Object object1,
                                final Object object2) {
            if ( object1 == null ) {
                return object2 != null;
            }
            return !((Byte) object1).equals( object2 );
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

        public boolean evaluate(final Object object1,
                                final Object object2) {
            return ((Byte) object1).byteValue() < ((Byte) object2).byteValue();
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

        public boolean evaluate(final Object object1,
                                final Object object2) {
            return ((Byte) object1).byteValue() <= ((Byte) object2).byteValue();
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

        public boolean evaluate(final Object object1,
                                final Object object2) {
            return ((Byte) object1).byteValue() > ((Byte) object2).byteValue();
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

        public boolean evaluate(final Object object1,
                                final Object object2) {
            return ((Byte) object1).byteValue() >= ((Byte) object2).byteValue();
        }

        public String toString() {
            return "Byte >=";
        }
    }

}