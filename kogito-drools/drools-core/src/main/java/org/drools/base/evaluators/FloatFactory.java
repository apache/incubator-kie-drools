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
        } else if ( operator == Operator.MEMBEROF ) {
            return FloatMemberOfEvaluator.INSTANCE;
        } else if ( operator == Operator.NOTMEMBEROF ) {
            return FloatNotMemberOfEvaluator.INSTANCE;
        } else {
            throw new RuntimeException( "Operator '" + operator + "' does not exist for FloatEvaluator" );
        }
    }

    static class FloatEqualEvaluator extends BaseEvaluator {

        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new FloatEqualEvaluator();

        private FloatEqualEvaluator() {
            super( ValueType.PFLOAT_TYPE,
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
            
            // TODO: we are not handling delta right now... maybe we should
            return extractor.getFloatValue( object1 ) == object2.getFloatValue();
        }

        public boolean evaluateCachedRight(final VariableContextEntry context,
                                           final Object left) {
            if ( context.declaration.getExtractor().isNullValue( left ) ) {
                return context.isRightNull();
            } else if ( context.isRightNull() ) {
                return false;
            }
            
            // TODO: we are not handling delta right now... maybe we should
            return context.declaration.getExtractor().getFloatValue( left ) == ((DoubleVariableContextEntry) context).right;
        }

        public boolean evaluateCachedLeft(final VariableContextEntry context,
                                          final Object right) {
            if ( context.extractor.isNullValue( right )) {
                return context.isLeftNull();
            } else if ( context.isLeftNull() ) {
                return false;
            }
            
            // TODO: we are not handling delta right now... maybe we should
            return ((DoubleVariableContextEntry) context).left == context.extractor.getFloatValue( right );
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
            
            // TODO: we are not handling delta right now... maybe we should
            return extractor1.getFloatValue( object1 ) == extractor2.getFloatValue( object2 );
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
            super( ValueType.PFLOAT_TYPE,
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
            
            // TODO: we are not handling delta right now... maybe we should
            return extractor.getFloatValue( object1 ) != object2.getFloatValue();
        }

        public boolean evaluateCachedRight(final VariableContextEntry context,
                                           final Object left) {
            if ( context.declaration.getExtractor().isNullValue( left ) ) {
                return !context.isRightNull();
            } else if ( context.isRightNull() ) {
                return true;
            }
            
            // TODO: we are not handling delta right now... maybe we should
            return context.declaration.getExtractor().getFloatValue( left ) != ((DoubleVariableContextEntry) context).right;
        }

        public boolean evaluateCachedLeft(final VariableContextEntry context,
                                          final Object right) {
            if ( context.extractor.isNullValue( right ) ) {
                return !context.isLeftNull();
            } else if ( context.isLeftNull() ) {
                return true;
            }
            
            // TODO: we are not handling delta right now... maybe we should
            return ((DoubleVariableContextEntry) context).left != context.extractor.getFloatValue( right );
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
            
            // TODO: we are not handling delta right now... maybe we should
            return extractor1.getFloatValue( object1 ) != extractor2.getFloatValue( object2 );
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
            super( ValueType.PFLOAT_TYPE,
                   Operator.LESS );
        }

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            if( extractor.isNullValue( object1 ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return extractor.getFloatValue( object1 ) < object2.getFloatValue();
        }

        public boolean evaluateCachedRight(final VariableContextEntry context,
                                           final Object left) {
            if( context.rightNull ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return ((DoubleVariableContextEntry) context).right < context.declaration.getExtractor().getFloatValue( left );
        }

        public boolean evaluateCachedLeft(final VariableContextEntry context,
                                          final Object right) {
            if( context.extractor.isNullValue( right ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return context.extractor.getFloatValue( right ) < ((DoubleVariableContextEntry) context).left;
        }

        public boolean evaluate(final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2,
                                final Object object2) {
            if( extractor1.isNullValue( object1 ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return extractor1.getFloatValue( object1 ) < extractor2.getFloatValue( object2 );
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
            super( ValueType.PFLOAT_TYPE,
                   Operator.LESS_OR_EQUAL );
        }

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            if( extractor.isNullValue( object1 ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return extractor.getFloatValue( object1 ) <= object2.getFloatValue();
        }

        public boolean evaluateCachedRight(final VariableContextEntry context,
                                           final Object left) {
            if( context.rightNull ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return ((DoubleVariableContextEntry) context).right <= context.declaration.getExtractor().getFloatValue( left );
        }

        public boolean evaluateCachedLeft(final VariableContextEntry context,
                                          final Object right) {
            if( context.extractor.isNullValue( right ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return context.extractor.getFloatValue( right ) <= ((DoubleVariableContextEntry) context).left;
        }

        public boolean evaluate(final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2,
                                final Object object2) {
            if( extractor1.isNullValue( object1 ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return extractor1.getFloatValue( object1 ) <= extractor2.getFloatValue( object2 );
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
            super( ValueType.PFLOAT_TYPE,
                   Operator.GREATER );
        }

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            if( extractor.isNullValue( object1 ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return extractor.getFloatValue( object1 ) > object2.getFloatValue();
        }

        public boolean evaluateCachedRight(final VariableContextEntry context,
                                           final Object left) {
            if( context.rightNull ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return ((DoubleVariableContextEntry) context).right > context.declaration.getExtractor().getFloatValue( left );
        }

        public boolean evaluateCachedLeft(final VariableContextEntry context,
                                          final Object right) {
            if( context.extractor.isNullValue( right ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return context.extractor.getFloatValue( right ) > ((DoubleVariableContextEntry) context).left;
        }

        public boolean evaluate(final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2,
                                final Object object2) {
            if( extractor1.isNullValue( object1 ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return extractor1.getFloatValue( object1 ) > extractor2.getFloatValue( object2 );
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
            super( ValueType.PFLOAT_TYPE,
                   Operator.GREATER_OR_EQUAL );
        }

        public boolean evaluate(final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            if( extractor.isNullValue( object1 ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return extractor.getFloatValue( object1 ) >= object2.getFloatValue();
        }

        public boolean evaluateCachedRight(final VariableContextEntry context,
                                           final Object left) {
            if( context.rightNull ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return ((DoubleVariableContextEntry) context).right >= context.declaration.getExtractor().getFloatValue( left );
        }

        public boolean evaluateCachedLeft(final VariableContextEntry context,
                                          final Object right) {
            if( context.extractor.isNullValue( right ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return context.extractor.getFloatValue( right ) >= ((DoubleVariableContextEntry) context).left;
        }

        public boolean evaluate(final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2,
                                final Object object2) {
            if( extractor1.isNullValue( object1 ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return extractor1.getFloatValue( object1 ) >= extractor2.getFloatValue( object2 );
        }

        public String toString() {
            return "Float >=";
        }
    }
    
    static class FloatMemberOfEvaluator extends BaseMemberOfEvaluator {

        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new FloatMemberOfEvaluator();

        private FloatMemberOfEvaluator() {
            super( ValueType.PFLOAT_TYPE,
                   Operator.MEMBEROF );
        }

        public String toString() {
            return "Float memberOf";
        }
    }

    static class FloatNotMemberOfEvaluator extends BaseNotMemberOfEvaluator {

        private static final long     serialVersionUID = 320;
        public final static Evaluator INSTANCE         = new FloatNotMemberOfEvaluator();

        private FloatNotMemberOfEvaluator() {
            super( ValueType.PFLOAT_TYPE,
                   Operator.NOTMEMBEROF );
        }

        public String toString() {
            return "Float not memberOf";
        }
    }
    
}