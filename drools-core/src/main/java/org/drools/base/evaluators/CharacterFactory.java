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

public class CharacterFactory
    implements
    EvaluatorFactory {

    private static final long       serialVersionUID = -8006570416583057447L;
    private static EvaluatorFactory INSTANCE         = new CharacterFactory();

    private CharacterFactory() {

    }

    public static EvaluatorFactory getInstance() {
        if ( CharacterFactory.INSTANCE == null ) {
            CharacterFactory.INSTANCE = new CharacterFactory();
        }
        return CharacterFactory.INSTANCE;
    }

    public Evaluator getEvaluator(final Operator operator) {
        if ( operator == Operator.EQUAL ) {
            return CharacterEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.NOT_EQUAL ) {
            return CharacterNotEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.LESS ) {
            return CharacterLessEvaluator.INSTANCE;
        } else if ( operator == Operator.LESS_OR_EQUAL ) {
            return CharacterLessOrEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.GREATER ) {
            return CharacterGreaterEvaluator.INSTANCE;
        } else if ( operator == Operator.GREATER_OR_EQUAL ) {
            return CharacterGreaterOrEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.MEMBEROF ) {
            return CharacterMemberOfEvaluator.INSTANCE;
        } else if ( operator == Operator.NOTMEMBEROF ) {
            return CharacterNotMemberOfEvaluator.INSTANCE;
        } else {
            throw new RuntimeException( "Operator '" + operator + "' does not exist for CharacterEvaluator" );
        }
    }

    static class CharacterEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new CharacterEqualEvaluator();

        private CharacterEqualEvaluator() {
            super( ValueType.PCHAR_TYPE,
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
            
            return extractor.getCharValue( workingMemory, object1 ) == object2.getCharValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if ( context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return context.isRightNull();
            } else if ( context.isRightNull() ) {
                return false;
            }
            
            return context.declaration.getExtractor().getCharValue( workingMemory, left ) == ((LongVariableContextEntry) context).right;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if ( context.extractor.isNullValue( workingMemory, right )) {
                return context.isLeftNull();
            } else if ( context.isLeftNull() ) {
                return false;
            }
            
            return ((LongVariableContextEntry) context).left == context.extractor.getCharValue( workingMemory, right );
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
            
            return extractor1.getCharValue( workingMemory, object1 ) == extractor2.getCharValue( workingMemory, object2 );
        }

        public String toString() {
            return "Character ==";
        }
    }

    static class CharacterNotEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new CharacterNotEqualEvaluator();

        private CharacterNotEqualEvaluator() {
            super( ValueType.PCHAR_TYPE,
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
            
            return extractor.getCharValue( workingMemory, object1 ) != object2.getCharValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if ( context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return !context.isRightNull();
            } else if ( context.isRightNull() ) {
                return true;
            }
            
            return context.declaration.getExtractor().getCharValue( workingMemory, left ) != ((LongVariableContextEntry) context).right;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if ( context.extractor.isNullValue( workingMemory, right ) ) {
                return !context.isLeftNull();
            } else if ( context.isLeftNull() ) {
                return true;
            }
            
            return ((LongVariableContextEntry) context).left != context.extractor.getCharValue( workingMemory, right );
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
            
            return extractor1.getCharValue( workingMemory, object1 ) != extractor2.getCharValue( workingMemory, object2 );
        }

        public String toString() {
            return "Character !=";
        }
    }

    static class CharacterLessEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new CharacterLessEvaluator();

        private CharacterLessEvaluator() {
            super( ValueType.PCHAR_TYPE,
                   Operator.LESS );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            return extractor.getCharValue( workingMemory, object1 ) < object2.getCharValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull ) {
                return false;
            }
            return ((LongVariableContextEntry) context).right < context.declaration.getExtractor().getCharValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            return context.extractor.getCharValue( workingMemory, right ) < ((LongVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            return extractor1.getCharValue( workingMemory, object1 ) < extractor2.getCharValue( workingMemory, object2 );
        }

        public String toString() {
            return "Character <";
        }
    }

    static class CharacterLessOrEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new CharacterLessOrEqualEvaluator();

        private CharacterLessOrEqualEvaluator() {
            super( ValueType.PCHAR_TYPE,
                   Operator.LESS_OR_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            return extractor.getCharValue( workingMemory, object1 ) <= object2.getCharValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull ) {
                return false;
            }
            return ((LongVariableContextEntry) context).right <= context.declaration.getExtractor().getCharValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            return context.extractor.getCharValue( workingMemory, right ) <= ((LongVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            return extractor1.getCharValue( workingMemory, object1 ) <= extractor2.getCharValue( workingMemory, object2 );
        }

        public String toString() {
            return "Character <=";
        }
    }

    static class CharacterGreaterEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new CharacterGreaterEvaluator();

        private CharacterGreaterEvaluator() {
            super( ValueType.PCHAR_TYPE,
                   Operator.GREATER );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            return extractor.getCharValue( workingMemory, object1 ) > object2.getCharValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull ) {
                return false;
            }
            return ((LongVariableContextEntry) context).right > context.declaration.getExtractor().getCharValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            return context.extractor.getCharValue( workingMemory, right ) > ((LongVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            return extractor1.getCharValue( workingMemory, object1 ) > extractor2.getCharValue( workingMemory, object2 );
        }

        public String toString() {
            return "Character >";
        }
    }

    static class CharacterGreaterOrEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long      serialVersionUID = 400L;
        private final static Evaluator INSTANCE         = new CharacterGreaterOrEqualEvaluator();

        private CharacterGreaterOrEqualEvaluator() {
            super( ValueType.PCHAR_TYPE,
                   Operator.GREATER_OR_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            return extractor.getCharValue( workingMemory, object1 ) >= object2.getCharValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull ) {
                return false;
            }
            return ((LongVariableContextEntry) context).right >= context.declaration.getExtractor().getCharValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            return context.extractor.getCharValue( workingMemory, right ) >= ((LongVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            return extractor1.getCharValue( workingMemory, object1 ) >= extractor2.getCharValue( workingMemory, object2 );
        }

        public String toString() {
            return "Character >=";
        }
    }

    static class CharacterMemberOfEvaluator extends BaseMemberOfEvaluator {

        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new CharacterMemberOfEvaluator();

        private CharacterMemberOfEvaluator() {
            super( ValueType.PCHAR_TYPE,
                   Operator.MEMBEROF );
        }

        public String toString() {
            return "Character memberOf";
        }
    }

    static class CharacterNotMemberOfEvaluator extends BaseNotMemberOfEvaluator {

        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new CharacterNotMemberOfEvaluator();

        private CharacterNotMemberOfEvaluator() {
            super( ValueType.PCHAR_TYPE,
                   Operator.NOTMEMBEROF );
        }

        public String toString() {
            return "Character not memberOf";
        }
    }
    
}