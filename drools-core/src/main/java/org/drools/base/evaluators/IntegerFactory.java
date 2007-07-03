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
import org.drools.rule.VariableRestriction.LongVariableContextEntry;
import org.drools.rule.VariableRestriction.VariableContextEntry;
import org.drools.spi.Evaluator;
import org.drools.spi.Extractor;
import org.drools.spi.FieldValue;

public class IntegerFactory
    implements
    EvaluatorFactory {

    private static final long       serialVersionUID = -6863552870087722275L;
    private static EvaluatorFactory INSTANCE         = new IntegerFactory();

    private IntegerFactory() {

    }

    public static EvaluatorFactory getInstance() {
        if ( IntegerFactory.INSTANCE == null ) {
            IntegerFactory.INSTANCE = new IntegerFactory();
        }
        return IntegerFactory.INSTANCE;
    }

    public Evaluator getEvaluator(final Operator operator) {
        if ( operator == Operator.EQUAL ) {
            return IntegerEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.NOT_EQUAL ) {
            return IntegerNotEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.LESS ) {
            return IntegerLessEvaluator.INSTANCE;
        } else if ( operator == Operator.LESS_OR_EQUAL ) {
            return IntegerLessOrEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.GREATER ) {
            return IntegerGreaterEvaluator.INSTANCE;
        } else if ( operator == Operator.GREATER_OR_EQUAL ) {
            return IntegerGreaterOrEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.MEMBEROF ) {
            return IntegerMemberOfEvaluator.INSTANCE;
        } else if ( operator == Operator.NOTMEMBEROF ) {
            return IntegerNotMemberOfEvaluator.INSTANCE;
        } else {
            throw new RuntimeException( "Operator '" + operator + "' does not exist for IntegerEvaluator" );
        }
    }

    static class IntegerEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new IntegerEqualEvaluator();

        private IntegerEqualEvaluator() {
            super( ValueType.PINTEGER_TYPE,
                   Operator.EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            if ( extractor.isNullValue( workingMemory, object1 ) ) {
                return object2.isNull();
            } else if ( object2.isNull() ) {
                return false;
            }
            
            return extractor.getIntValue( workingMemory, object1 ) == object2.getIntValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if ( context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return context.isRightNull();
            } else if ( context.isRightNull() ) {
                return false;
            }
            
            return context.declaration.getExtractor().getIntValue( workingMemory, left ) == ((LongVariableContextEntry) context).right; 
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object object2) {
            if ( context.extractor.isNullValue( workingMemory, object2 )) {
                return context.isLeftNull();
            } else if ( context.isLeftNull() ) {
                return false;
            }
            
            return context.extractor.getIntValue( workingMemory, object2 ) == ((LongVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {            
            if (extractor1.isNullValue( workingMemory, object1 )) {
                return extractor2.isNullValue( workingMemory, object2 );
            } else if (extractor2.isNullValue( workingMemory, object2 )) {
                return false;
            }
            
            return extractor1.getIntValue( workingMemory, object1 ) == extractor2.getIntValue( workingMemory, object2 );
        }

        public String toString() {
            return "Integer ==";
        }

    }

    static class IntegerNotEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new IntegerNotEqualEvaluator();

        private IntegerNotEqualEvaluator() {
            super( ValueType.PINTEGER_TYPE,
                   Operator.NOT_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {        	          
            if ( extractor.isNullValue( workingMemory, object1 ) ) {
                return !object2.isNull();
            } else if ( object2.isNull() ) {
                return true;
            }
            
            return extractor.getIntValue( workingMemory, object1 ) != object2.getIntValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if ( context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return !context.isRightNull();
            } else if ( context.isRightNull() ) {
                return true;
            }
            
            return context.declaration.getExtractor().getIntValue( workingMemory, left ) != ((LongVariableContextEntry) context).right;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object object2) {
            if ( context.extractor.isNullValue( workingMemory, object2 ) ) {
                return !context.isLeftNull();
            } else if ( context.isLeftNull() ) {
                return true;
            }
            
            return context.extractor.getIntValue( workingMemory, object2 ) != ((LongVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            if (extractor1.isNullValue( workingMemory, object1 )) {
                return !extractor2.isNullValue( workingMemory, object2 );
            } else if (extractor2.isNullValue( workingMemory, object2 )) {
                return true;
            }
            
            return extractor1.getIntValue( workingMemory, object1 ) != extractor2.getIntValue( workingMemory, object2 );
        }

        public String toString() {
            return "Integer !=";
        }
    }

    static class IntegerLessEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new IntegerLessEvaluator();

        private IntegerLessEvaluator() {
            super( ValueType.PINTEGER_TYPE,
                   Operator.LESS );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            return extractor.getIntValue( workingMemory, object1 ) < object2.getIntValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull ) {
                return false;
            }
            return ((LongVariableContextEntry) context).right < context.declaration.getExtractor().getIntValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            return context.extractor.getIntValue( workingMemory, right ) < ((LongVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            return extractor1.getIntValue( workingMemory, object1 ) < extractor2.getIntValue( workingMemory, object2 );
        }

        public String toString() {
            return "Integer <";
        }
    }

    static class IntegerLessOrEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new IntegerLessOrEqualEvaluator();

        private IntegerLessOrEqualEvaluator() {
            super( ValueType.PINTEGER_TYPE,
                   Operator.LESS_OR_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            return extractor.getIntValue( workingMemory, object1 ) <= object2.getIntValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull ) {
                return false;
            }
            return ((LongVariableContextEntry) context).right <= context.declaration.getExtractor().getIntValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            return context.extractor.getIntValue( workingMemory, right ) <= ((LongVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            return extractor1.getIntValue( workingMemory, object1 ) <= extractor2.getIntValue( workingMemory, object2 );
        }

        public String toString() {
            return "Integer <=";
        }
    }

    static class IntegerGreaterEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new IntegerGreaterEvaluator();

        private IntegerGreaterEvaluator() {
            super( ValueType.PINTEGER_TYPE,
                   Operator.GREATER );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            return extractor.getIntValue( workingMemory, object1 ) > object2.getIntValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull ) {
                return false;
            }
            return ((LongVariableContextEntry) context).right > context.declaration.getExtractor().getIntValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            return context.extractor.getIntValue( workingMemory, right ) > ((LongVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            return extractor1.getIntValue( workingMemory, object1 ) > extractor2.getIntValue( workingMemory, object2 );
        }

        public String toString() {
            return "Integer >";
        }
    }

    static class IntegerGreaterOrEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long      serialVersionUID = 400L;
        private final static Evaluator INSTANCE         = new IntegerGreaterOrEqualEvaluator();

        private IntegerGreaterOrEqualEvaluator() {
            super( ValueType.PINTEGER_TYPE,
                   Operator.GREATER_OR_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            return extractor.getIntValue( workingMemory, object1 ) >= object2.getIntValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull ) {
                return false;
            }
            return ((LongVariableContextEntry) context).right >= context.declaration.getExtractor().getIntValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            return context.extractor.getIntValue( workingMemory, right ) >= ((LongVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            return extractor1.getIntValue( workingMemory, object1 ) >= extractor2.getIntValue( workingMemory, object2 );
        }

        public String toString() {
            return "Integer >=";
        }
    }
    
    static class IntegerMemberOfEvaluator extends BaseMemberOfEvaluator {

        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new IntegerMemberOfEvaluator();

        private IntegerMemberOfEvaluator() {
            super( ValueType.PINTEGER_TYPE,
                   Operator.MEMBEROF );
        }

        public String toString() {
            return "Integer memberOf";
        }
    }

    static class IntegerNotMemberOfEvaluator extends BaseNotMemberOfEvaluator {

        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new IntegerNotMemberOfEvaluator();

        private IntegerNotMemberOfEvaluator() {
            super( ValueType.PINTEGER_TYPE,
                   Operator.NOTMEMBEROF );
        }

        public String toString() {
            return "Integer not memberOf";
        }
    }
    

}