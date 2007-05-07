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

public class ShortFactory
    implements
    EvaluatorFactory {

    private static final long       serialVersionUID = -1295210800055648796L;
    private static EvaluatorFactory INSTANCE         = new ShortFactory();

    private ShortFactory() {

    }

    public static EvaluatorFactory getInstance() {
        if ( ShortFactory.INSTANCE == null ) {
            ShortFactory.INSTANCE = new ShortFactory();
        }
        return ShortFactory.INSTANCE;
    }

    public Evaluator getEvaluator(final Operator operator) {
        if ( operator == Operator.EQUAL ) {
            return ShortEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.NOT_EQUAL ) {
            return ShortNotEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.LESS ) {
            return ShortLessEvaluator.INSTANCE;
        } else if ( operator == Operator.LESS_OR_EQUAL ) {
            return ShortLessOrEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.GREATER ) {
            return ShortGreaterEvaluator.INSTANCE;
        } else if ( operator == Operator.GREATER_OR_EQUAL ) {
            return ShortGreaterOrEqualEvaluator.INSTANCE;
        } else if ( operator == Operator.MEMBEROF ) {
            return ShortMemberOfEvaluator.INSTANCE;
        } else if ( operator == Operator.NOTMEMBEROF ) {
            return ShortNotMemberOfEvaluator.INSTANCE;
        } else {
            throw new RuntimeException( "Operator '" + operator + "' does not exist for ShortEvaluator" );
        }
    }

    static class ShortEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long      serialVersionUID = 320;
        private static final Evaluator INSTANCE         = new ShortEqualEvaluator();

        private ShortEqualEvaluator() {
            super( ValueType.PSHORT_TYPE,
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
            
            return extractor.getShortValue( object1 ) == object2.getShortValue();
        }

        public boolean evaluateCachedRight(final VariableContextEntry context,
                                           final Object left) {
            if ( context.declaration.getExtractor().isNullValue( left ) ) {
                return context.isRightNull();
            } else if ( context.isRightNull() ) {
                return false;
            }
            
            return context.declaration.getExtractor().getShortValue( left ) == ((LongVariableContextEntry) context).right;
        }

        public boolean evaluateCachedLeft(final VariableContextEntry context,
                                          final Object right) {
            if ( context.extractor.isNullValue( right )) {
                return context.isLeftNull();
            } else if ( context.isLeftNull() ) {
                return false;
            }
            
            return ((LongVariableContextEntry) context).left == context.extractor.getShortValue( right );
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
            
            return extractor1.getShortValue( object1 ) == extractor2.getShortValue( object2 );
        }

        public String toString() {
            return "Short ==";
        }
    }

    static class ShortNotEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long      serialVersionUID = 320;
        private static final Evaluator INSTANCE         = new ShortNotEqualEvaluator();

        private ShortNotEqualEvaluator() {
            super( ValueType.PSHORT_TYPE,
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
            
            return extractor.getShortValue( object1 ) != object2.getShortValue();
        }

        public boolean evaluateCachedRight(final VariableContextEntry context,
                                           final Object left) {
            if ( context.declaration.getExtractor().isNullValue( left ) ) {
                return !context.isRightNull();
            } else if ( context.isRightNull() ) {
                return true;
            }
            
            return context.declaration.getExtractor().getShortValue( left ) != ((LongVariableContextEntry) context).right;
        }

        public boolean evaluateCachedLeft(final VariableContextEntry context,
                                          final Object right) {
            if ( context.extractor.isNullValue( right ) ) {
                return !context.isLeftNull();
            } else if ( context.isLeftNull() ) {
                return true;
            }
            
            return ((LongVariableContextEntry) context).left != context.extractor.getShortValue( right );
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
            
            return extractor1.getShortValue( object1 ) != extractor2.getShortValue( object2 );
        }

        public String toString() {
            return "Short !=";
        }
    }

    static class ShortLessEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long      serialVersionUID = 320;
        private static final Evaluator INSTANCE         = new ShortLessEvaluator();

        private ShortLessEvaluator() {
            super( ValueType.PSHORT_TYPE,
                   Operator.LESS );
        }

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            if( extractor.isNullValue( object1 ) ) {
                return false;
            }
            return extractor.getShortValue( object1 ) < object2.getShortValue();
        }

        public boolean evaluateCachedRight(final VariableContextEntry context,
                                           final Object left) {
            if( context.rightNull ) {
                return false;
            }
            return ((LongVariableContextEntry) context).right < context.declaration.getExtractor().getShortValue( left );
        }

        public boolean evaluateCachedLeft(final VariableContextEntry context,
                                          final Object right) {
            if( context.extractor.isNullValue( right ) ) {
                return false;
            }
            return context.extractor.getShortValue( right ) < ((LongVariableContextEntry) context).left;
        }

        public boolean evaluate(final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2,
                                final Object object2) {
            if( extractor1.isNullValue( object1 ) ) {
                return false;
            }
            return extractor1.getShortValue( object1 ) < extractor2.getShortValue( object2 );
        }

        public String toString() {
            return "Short <";
        }
    }

    static class ShortLessOrEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long      serialVersionUID = 320;
        private static final Evaluator INSTANCE         = new ShortLessOrEqualEvaluator();

        private ShortLessOrEqualEvaluator() {
            super( ValueType.PSHORT_TYPE,
                   Operator.LESS_OR_EQUAL );
        }

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            if( extractor.isNullValue( object1 ) ) {
                return false;
            }
            return extractor.getShortValue( object1 ) <= object2.getShortValue();
        }

        public boolean evaluateCachedRight(final VariableContextEntry context,
                                           final Object left) {
            if( context.rightNull ) {
                return false;
            }
            return ((LongVariableContextEntry) context).right <= context.declaration.getExtractor().getShortValue( left );
        }

        public boolean evaluateCachedLeft(final VariableContextEntry context,
                                          final Object right) {
            if( context.extractor.isNullValue( right ) ) {
                return false;
            }
            return context.extractor.getShortValue( right ) <= ((LongVariableContextEntry) context).left;
        }

        public boolean evaluate(final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2,
                                final Object object2) {
            if( extractor1.isNullValue( object1 ) ) {
                return false;
            }
            return extractor1.getShortValue( object1 ) <= extractor2.getShortValue( object2 );
        }

        public String toString() {
            return "Boolean <=";
        }
    }

    static class ShortGreaterEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long      serialVersionUID = 320;
        private static final Evaluator INSTANCE         = new ShortGreaterEvaluator();

        private ShortGreaterEvaluator() {
            super( ValueType.PSHORT_TYPE,
                   Operator.GREATER );
        }

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            if( extractor.isNullValue( object1 ) ) {
                return false;
            }
            return extractor.getShortValue( object1 ) > object2.getShortValue();
        }

        public boolean evaluateCachedRight(final VariableContextEntry context,
                                           final Object left) {
            if( context.rightNull ) {
                return false;
            }
            return ((LongVariableContextEntry) context).right > context.declaration.getExtractor().getShortValue( left );
        }

        public boolean evaluateCachedLeft(final VariableContextEntry context,
                                          final Object right) {
            if( context.extractor.isNullValue( right ) ) {
                return false;
            }
            return context.extractor.getShortValue( right ) > ((LongVariableContextEntry) context).left;
        }

        public boolean evaluate(final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2,
                                final Object object2) {
            if( extractor1.isNullValue( object1 ) ) {
                return false;
            }
            return extractor1.getShortValue( object1 ) > extractor2.getShortValue( object2 );
        }

        public String toString() {
            return "Short >";
        }
    }

    static class ShortGreaterOrEqualEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long      serialVersionUID = 320;
        private static final Evaluator INSTANCE         = new ShortGreaterOrEqualEvaluator();

        private ShortGreaterOrEqualEvaluator() {
            super( ValueType.PSHORT_TYPE,
                   Operator.GREATER_OR_EQUAL );
        }

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            if( extractor.isNullValue( object1 ) ) {
                return false;
            }
            return extractor.getShortValue( object1 ) >= object2.getShortValue();
        }

        public boolean evaluateCachedRight(final VariableContextEntry context,
                                           final Object left) {
            if( context.rightNull ) {
                return false;
            }
            return ((LongVariableContextEntry) context).right >= context.declaration.getExtractor().getShortValue( left );
        }

        public boolean evaluateCachedLeft(final VariableContextEntry context,
                                          final Object right) {
            if( context.extractor.isNullValue( right ) ) {
                return false;
            }
            return context.extractor.getShortValue( right ) >= ((LongVariableContextEntry) context).left;
        }

        public boolean evaluate(final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2,
                                final Object object2) {
            if( extractor1.isNullValue( object1 ) ) {
                return false;
            }
            return extractor1.getShortValue( object1 ) >= extractor2.getShortValue( object2 );
        }

        public String toString() {
            return "Short >=";
        }
    }

    static class ShortMemberOfEvaluator extends BaseMemberOfEvaluator {

        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new ShortMemberOfEvaluator();

        private ShortMemberOfEvaluator() {
            super( ValueType.PSHORT_TYPE,
                   Operator.MEMBEROF );
        }

        public String toString() {
            return "Short memberOf";
        }
    }

    static class ShortNotMemberOfEvaluator extends BaseNotMemberOfEvaluator {

        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new ShortNotMemberOfEvaluator();

        private ShortNotMemberOfEvaluator() {
            super( ValueType.PSHORT_TYPE,
                   Operator.NOTMEMBEROF );
        }

        public String toString() {
            return "Short not memberOf";
        }
    }
    
}