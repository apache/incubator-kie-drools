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
import org.drools.rule.VariableRestriction.BooleanVariableContextEntry;
import org.drools.rule.VariableRestriction.VariableContextEntry;
import org.drools.spi.Evaluator;
import org.drools.spi.Extractor;
import org.drools.spi.FieldValue;

public class BooleanFactory
    implements
    EvaluatorFactory {

    private static final long       serialVersionUID = -1463529133869380215L;
    private static EvaluatorFactory INSTANCE         = new BooleanFactory();

    private BooleanFactory() {

    }

    public static EvaluatorFactory getInstance() {
        if ( BooleanFactory.INSTANCE == null ) {
            BooleanFactory.INSTANCE = new BooleanFactory();
        }
        return BooleanFactory.INSTANCE;
    }

    public Evaluator getEvaluator(final Operator operator) {
        if ( operator == Operator.EQUAL ) {
            return BooleanEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.NOT_EQUAL ) {
            return BooleanNotEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.MEMBEROF ) {
            return BooleanMemberOfEvaluator.INSTANCE;
        } else if ( operator == Operator.NOTMEMBEROF ) {
            return BooleanNotMemberOfEvaluator.INSTANCE;
        } else {
            throw new RuntimeException( "Operator '" + operator + "' does not exist for BooleanEvaluator" );
        }
    }

    static class BooleanEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long      serialVersionUID = 320;
        private final static Evaluator INSTANCE         = new BooleanEqualEvaluator();

        private BooleanEqualEvaluator() {
            super( ValueType.PBOOLEAN_TYPE,
                   Operator.EQUAL );
        }

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            if ( extractor.isNullValue( object1 ) ) {
                return object2.isNull();
            } else if ( object2.isNull() ) {
                return false;
            }
            
            return extractor.getBooleanValue( object1 ) == object2.getBooleanValue();
        }

        public boolean evaluateCachedRight(final VariableContextEntry context,
                                           final Object left) {
            if ( context.declaration.getExtractor().isNullValue( left ) ) {
                return context.isRightNull();
            } else if ( context.isRightNull() ) {
                return false;
            }
            
            return context.declaration.getExtractor().getBooleanValue( left ) == ((BooleanVariableContextEntry) context).right;
        }

        public boolean evaluateCachedLeft(final VariableContextEntry context,
                                          final Object object2) {
            if ( context.extractor.isNullValue( object2 )) {
                return context.isLeftNull();
            } else if ( context.isLeftNull() ) {
                return false;
            }
            
            return context.extractor.getBooleanValue( object2 ) == ((BooleanVariableContextEntry) context).left;
        }

        public boolean evaluate(final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2,
                                final Object object2) {
            if (extractor1.isNullValue( object1 )) {
                return extractor2.isNullValue( object2 );
            } else if (extractor2.isNullValue( object2 )) {
                return false;
            }
            
            return extractor1.getBooleanValue( object1 ) == extractor2.getBooleanValue( object2 );
        }

        public String toString() {
            return "Boolean ==";
        }

    }

    static class BooleanNotEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new BooleanNotEqualEvaluator();

        private BooleanNotEqualEvaluator() {
            super( ValueType.PBOOLEAN_TYPE,
                   Operator.NOT_EQUAL );
        }

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            if ( extractor.isNullValue( object1 ) ) {
                return !object2.isNull();
            } else if ( object2.isNull() ) {
                return true;
            }
            
            return extractor.getBooleanValue( object1 ) != object2.getBooleanValue();
        }

        public boolean evaluateCachedRight(final VariableContextEntry context,
                                           final Object left) {
            if ( context.declaration.getExtractor().isNullValue( left ) ) {
                return !context.isRightNull();
            } else if ( context.isRightNull() ) {
                return true;
            }
            return context.declaration.getExtractor().getBooleanValue( left ) != ((BooleanVariableContextEntry) context).right;
        }

        public boolean evaluateCachedLeft(final VariableContextEntry context,
                                          final Object object2) {
            if ( context.extractor.isNullValue( object2 )) {
                return !context.isLeftNull();
            } else if ( context.isLeftNull() ) {
                return true;
            }
            
            return context.extractor.getBooleanValue( object2 ) != ((BooleanVariableContextEntry) context).left;
        }

        public boolean evaluate(final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2,
                                final Object object2) {
            if (extractor1.isNullValue( object1 )) {
                return !extractor2.isNullValue( object2 );
            } else if (extractor2.isNullValue( object2 )) {
                return true;
            }
            
            return extractor1.getBooleanValue( object1 ) != extractor1.getBooleanValue( object2 );
        }

        public String toString() {
            return "Boolean !=";
        }
    }

    static class BooleanMemberOfEvaluator extends BaseMemberOfEvaluator {

        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new BooleanMemberOfEvaluator();

        private BooleanMemberOfEvaluator() {
            super( ValueType.PBOOLEAN_TYPE,
                   Operator.MEMBEROF );
        }

        public String toString() {
            return "Boolean memberOf";
        }
    }

    static class BooleanNotMemberOfEvaluator extends BaseNotMemberOfEvaluator {

        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new BooleanNotMemberOfEvaluator();

        private BooleanNotMemberOfEvaluator() {
            super( ValueType.PBOOLEAN_TYPE,
                   Operator.NOTMEMBEROF );
        }

        public String toString() {
            return "Boolean not memberOf";
        }
    }
    
}