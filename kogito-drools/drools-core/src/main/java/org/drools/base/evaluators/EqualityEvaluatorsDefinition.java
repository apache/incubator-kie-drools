/*
 * Copyright 2010 JBoss Inc
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

package org.drools.base.evaluators;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;
import java.math.BigDecimal;

import org.drools.base.BaseEvaluator;
import org.drools.base.ValueType;
import org.drools.common.InternalWorkingMemory;
import org.drools.core.util.DateUtils;
import org.drools.core.util.MathUtils;
import org.drools.rule.VariableRestriction.BooleanVariableContextEntry;
import org.drools.rule.VariableRestriction.CharVariableContextEntry;
import org.drools.rule.VariableRestriction.DoubleVariableContextEntry;
import org.drools.rule.VariableRestriction.LongVariableContextEntry;
import org.drools.rule.VariableRestriction.ObjectVariableContextEntry;
import org.drools.rule.VariableRestriction.VariableContextEntry;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldValue;
import org.drools.spi.InternalReadAccessor;

/**
 * This class defines the default built in equality
 * evaluators == and !=
 */
public class EqualityEvaluatorsDefinition implements EvaluatorDefinition {

    private static final String[] SUPPORTED_IDS = { Operator.EQUAL.getOperatorString(), Operator.NOT_EQUAL.getOperatorString() };
    private EvaluatorCache evaluators = new EvaluatorCache() {
        private static final long serialVersionUID = 510l;
        {
            addEvaluator( ValueType.ARRAY_TYPE,         Operator.EQUAL,         ArrayEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.ARRAY_TYPE,         Operator.NOT_EQUAL,     ArrayNotEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.BIG_DECIMAL_TYPE,   Operator.EQUAL,         BigDecimalEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.BIG_DECIMAL_TYPE,   Operator.NOT_EQUAL,     BigDecimalNotEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.BIG_INTEGER_TYPE,   Operator.EQUAL,         BigIntegerEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.BIG_INTEGER_TYPE,   Operator.NOT_EQUAL,     BigIntegerNotEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.BOOLEAN_TYPE,       Operator.EQUAL,         BooleanEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.BOOLEAN_TYPE,       Operator.NOT_EQUAL,     BooleanNotEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.PBOOLEAN_TYPE,      Operator.EQUAL,         BooleanEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.PBOOLEAN_TYPE,      Operator.NOT_EQUAL,     BooleanNotEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.BYTE_TYPE,          Operator.EQUAL,         ByteEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.BYTE_TYPE,          Operator.NOT_EQUAL,     ByteNotEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.PBYTE_TYPE,         Operator.EQUAL,         ByteEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.PBYTE_TYPE,         Operator.NOT_EQUAL,     ByteNotEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.CHAR_TYPE,          Operator.EQUAL,         CharacterEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.CHAR_TYPE,          Operator.NOT_EQUAL,     CharacterNotEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.PCHAR_TYPE,         Operator.EQUAL,         CharacterEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.PCHAR_TYPE,         Operator.NOT_EQUAL,     CharacterNotEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.DATE_TYPE,          Operator.EQUAL,         DateEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.DATE_TYPE,          Operator.NOT_EQUAL,     DateNotEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.DOUBLE_TYPE,        Operator.EQUAL,         DoubleEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.DOUBLE_TYPE,        Operator.NOT_EQUAL,     DoubleNotEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.PDOUBLE_TYPE,       Operator.EQUAL,         DoubleEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.PDOUBLE_TYPE,       Operator.NOT_EQUAL,     DoubleNotEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.FACTTEMPLATE_TYPE,  Operator.EQUAL,         FactTemplateEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.FACTTEMPLATE_TYPE,  Operator.NOT_EQUAL,     FactTemplateNotEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.FLOAT_TYPE,         Operator.EQUAL,         FloatEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.FLOAT_TYPE,         Operator.NOT_EQUAL,     FloatNotEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.PFLOAT_TYPE,        Operator.EQUAL,         FloatEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.PFLOAT_TYPE,        Operator.NOT_EQUAL,     FloatNotEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.INTEGER_TYPE,       Operator.EQUAL,         IntegerEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.INTEGER_TYPE,       Operator.NOT_EQUAL,     IntegerNotEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.PINTEGER_TYPE,      Operator.EQUAL,         IntegerEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.PINTEGER_TYPE,      Operator.NOT_EQUAL,     IntegerNotEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.LONG_TYPE,          Operator.EQUAL,         LongEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.LONG_TYPE,          Operator.NOT_EQUAL,     LongNotEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.PLONG_TYPE,         Operator.EQUAL,         LongEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.PLONG_TYPE,         Operator.NOT_EQUAL,     LongNotEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.OBJECT_TYPE,        Operator.EQUAL,         ObjectEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.OBJECT_TYPE,        Operator.NOT_EQUAL,     ObjectNotEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.SHORT_TYPE,         Operator.EQUAL,         ShortEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.SHORT_TYPE,         Operator.NOT_EQUAL,     ShortNotEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.PSHORT_TYPE,        Operator.EQUAL,         ShortEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.PSHORT_TYPE,        Operator.NOT_EQUAL,     ShortNotEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.STRING_TYPE,        Operator.EQUAL,         StringEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.STRING_TYPE,        Operator.NOT_EQUAL,     StringNotEqualEvaluator.INSTANCE );
        }
    };

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        evaluators  = (EvaluatorCache)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(evaluators);
    }

    /**
     * @inheridDoc
     */
    public Evaluator getEvaluator(ValueType type,
                                  Operator operator) {
        return this.evaluators.getEvaluator( type,
                                             operator );
    }

    /**
     * @inheridDoc
     */
    public Evaluator getEvaluator(ValueType type,
                                  Operator operator,
                                  String parameterText) {
        return this.evaluators.getEvaluator( type,
                                             operator );
    }


    public Evaluator getEvaluator(final ValueType type,
                                  final String operatorId,
                                  final boolean isNegated,
                                  final String parameterText) {
        return this.getEvaluator( type,
                                  operatorId,
                                  isNegated,
                                  parameterText,
                                  Target.FACT,
                                  Target.FACT );
        
    }
    
    /**
     * @inheritDoc
     */
    public Evaluator getEvaluator(final ValueType type,
                                  final String operatorId,
                                  final boolean isNegated,
                                  final String parameterText,
                                  final Target left,
                                  final Target right ) {
        return this.evaluators.getEvaluator( type, Operator.determineOperator( operatorId, isNegated ) );
    }

    public String[] getEvaluatorIds() {
        return SUPPORTED_IDS;
    }

    public boolean isNegatable() {
        return false;
    }

    public Target getTarget() {
        return Target.FACT;
    }

    public boolean supportsType(ValueType type) {
        return this.evaluators.supportsType( type );
    }

    /*  *********************************************************
     *           Evaluator Implementations
     *  *********************************************************
     */
    public static class ArrayEqualEvaluator extends BaseEvaluator {

        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new ArrayEqualEvaluator();

        public ArrayEqualEvaluator() {
            super( ValueType.ARRAY_TYPE,
                   Operator.EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1,
                                final FieldValue object2) {
            final Object value1 = extractor.getValue( workingMemory,
                                                      object1 );
            final Object value2 = object2.getValue();
            if ( value1 == null ) {
                return value2 == null;
            }
            return value1.equals( value2 );
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context,
                                           final Object left) {
            final Object value = context.declaration.getExtractor().getValue( workingMemory,
                                                                              left );
            if ( value == null ) {
                return ((ObjectVariableContextEntry) context).right == null;
            }
            return value.equals( ((ObjectVariableContextEntry) context).right );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context,
                                          final Object right) {
            final Object value = context.extractor.getValue( workingMemory,
                                                             right );
            if ( ((ObjectVariableContextEntry) context).left == null ) {
                return value == null;
            }
            return ((ObjectVariableContextEntry) context).left.equals( value );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2,
                                final Object object2) {
            final Object value1 = extractor1.getValue( workingMemory,
                                                       object1 );
            final Object value2 = extractor2.getValue( workingMemory,
                                                       object2 );
            if ( value1 == null ) {
                return value2 == null;
            }
            return value1.equals( value2 );
        }

        public String toString() {
            return "Array ==";
        }

    }

    public static class ArrayNotEqualEvaluator extends BaseEvaluator {
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new ArrayNotEqualEvaluator();

        public ArrayNotEqualEvaluator() {
            super( ValueType.ARRAY_TYPE,
                   Operator.NOT_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1,
                                final FieldValue object2) {
            final Object value1 = extractor.getValue( workingMemory,
                                                      object1 );
            final Object value2 = object2.getValue();
            if ( value1 == null ) {
                return value2 != null;
            }
            return !value1.equals( value2 );
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context,
                                           final Object left) {
            final Object value = context.declaration.getExtractor().getValue( workingMemory,
                                                                              left );
            if ( value == null ) {
                return ((ObjectVariableContextEntry) context).right != null;
            }
            return !value.equals( ((ObjectVariableContextEntry) context).right );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context,
                                          final Object right) {
            final Object value = context.extractor.getValue( workingMemory,
                                                             right );
            if ( ((ObjectVariableContextEntry) context).left == null ) {
                return value != null;
            }
            return !((ObjectVariableContextEntry) context).left.equals( value );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2,
                                final Object object2) {
            final Object value1 = extractor1.getValue( workingMemory,
                                                       object1 );
            final Object value2 = extractor2.getValue( workingMemory,
                                                       object2 );
            if ( value1 == null ) {
                return value2 != null;
            }
            return !value1.equals( value2 );
        }

        public String toString() {
            return "Array !=";
        }
    }



    public static class BigDecimalEqualEvaluator extends BaseEvaluator {

        static boolean isEqual(Object value1, Object value2) {
            if (!(value1 instanceof BigDecimal ) || !(value2 instanceof BigDecimal)) return false;
            return  ((BigDecimal)value1).compareTo((BigDecimal) value2) == 0;

        }
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new BigDecimalEqualEvaluator();

        public BigDecimalEqualEvaluator() {
            super( ValueType.BIG_DECIMAL_TYPE,
                   Operator.EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            final Object value1 = extractor.getBigDecimalValue( workingMemory, object1 );
            final BigDecimal value2 = object2.getBigDecimalValue();
            if ( value1 == null ) {
                return value2 == null;
            }

            return isEqual(value1, value2);
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            final Object value = context.declaration.getExtractor().getBigDecimalValue( workingMemory, left );
            if ( value == null ) {
                return ((ObjectVariableContextEntry) context).right == null;
            }
            return isEqual(value, MathUtils.getBigDecimal( ((ObjectVariableContextEntry) context).right ));
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            final Object value = context.extractor.getBigDecimalValue( workingMemory, right );
            if ( ((ObjectVariableContextEntry) context).left == null ) {
                return value == null;
            }

            return isEqual(MathUtils.getBigDecimal( ((ObjectVariableContextEntry) context).left ), value);
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            final Object value1 = extractor1.getBigDecimalValue( workingMemory, object1 );
            final Object value2 = extractor2.getBigDecimalValue( workingMemory, object2 );
            if ( value1 == null ) {
                return value2 == null;
            }
            return isEqual(value1, value2);
        }

        public String toString() {
            return "BigDecimal ==";
        }

    }

    public static class BigDecimalNotEqualEvaluator extends BaseEvaluator {
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new BigDecimalNotEqualEvaluator();




        public BigDecimalNotEqualEvaluator() {
            super( ValueType.BIG_DECIMAL_TYPE,
                   Operator.NOT_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            final Object value1 = extractor.getBigDecimalValue( workingMemory, object1 );
            final Object value2 = object2.getBigDecimalValue();
            if ( value1 == null ) {
                return value2 != null;
            }
            return !BigDecimalEqualEvaluator.isEqual(value1, value2 );
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            final Object value = context.declaration.getExtractor().getBigDecimalValue( workingMemory, left );
            if ( value == null ) {
                return ((ObjectVariableContextEntry) context).right != null;
            }
            return !BigDecimalEqualEvaluator.isEqual(value, MathUtils.getBigDecimal( ((ObjectVariableContextEntry) context).right ));
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            final Object value = context.extractor.getBigDecimalValue( workingMemory, right );
            if ( ((ObjectVariableContextEntry) context).left == null ) {
                return value != null;
            }
            return !BigDecimalEqualEvaluator.isEqual(MathUtils.getBigDecimal( ((ObjectVariableContextEntry) context).left ),  value );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            final Object value1 = extractor1.getBigDecimalValue( workingMemory, object1 );
            final Object value2 = extractor2.getBigDecimalValue( workingMemory, object2 );
            if ( value1 == null ) {
                return value2 != null;
            }
            return !BigDecimalEqualEvaluator.isEqual(value1, value2 );
        }

        public String toString() {
            return "BigDecimal !=";
        }
    }

    public static class BigIntegerEqualEvaluator extends BaseEvaluator {
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new BigIntegerEqualEvaluator();

        public BigIntegerEqualEvaluator() {
            super( ValueType.BIG_INTEGER_TYPE,
                   Operator.EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            final Object value1 = extractor.getBigIntegerValue( workingMemory, object1 );
            final Object value2 = object2.getBigIntegerValue();
            if ( value1 == null ) {
                return value2 == null;
            }
            return value1.equals( value2 );
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            final Object value = context.declaration.getExtractor().getBigIntegerValue( workingMemory, left );
            if ( value == null ) {
                return ((ObjectVariableContextEntry) context).right == null;
            }
            return value.equals( MathUtils.getBigInteger( ((ObjectVariableContextEntry) context).right ) );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            final Object value = context.extractor.getBigIntegerValue( workingMemory, right );
            if ( ((ObjectVariableContextEntry) context).left == null ) {
                return value == null;
            }
            return MathUtils.getBigInteger( ((ObjectVariableContextEntry) context).left ).equals( value );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            final Object value1 = extractor1.getBigIntegerValue( workingMemory, object1 );
            final Object value2 = extractor2.getBigIntegerValue( workingMemory, object2 );
            if ( value1 == null ) {
                return value2 == null;
            }
            return value1.equals( value2 );
        }

        public String toString() {
            return "BigInteger ==";
        }
    }

    public static class BigIntegerNotEqualEvaluator extends BaseEvaluator {
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new BigIntegerNotEqualEvaluator();

        public BigIntegerNotEqualEvaluator() {
            super( ValueType.BIG_INTEGER_TYPE,
                   Operator.NOT_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            final Object value1 = extractor.getBigIntegerValue( workingMemory, object1 );
            final Object value2 = object2.getBigIntegerValue();
            if ( value1 == null ) {
                return value2 != null;
            }
            return !value1.equals( value2 );
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            final Object value = context.declaration.getExtractor().getBigIntegerValue( workingMemory, left );
            if ( value == null ) {
                return ((ObjectVariableContextEntry) context).right != null;
            }
            return !value.equals(MathUtils.getBigInteger( ((ObjectVariableContextEntry) context).right ));
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            final Object value = context.extractor.getBigIntegerValue( workingMemory, right );
            if ( ((ObjectVariableContextEntry) context).left == null ) {
                return value != null;
            }
            return !MathUtils.getBigInteger( ((ObjectVariableContextEntry) context).left).equals( value );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            final Object value1 = extractor1.getBigIntegerValue( workingMemory, object1 );
            final Object value2 = extractor2.getBigIntegerValue( workingMemory, object2 );
            if ( value1 == null ) {
                return value2 != null;
            }
            return !value1.equals( value2 );
        }

        public String toString() {
            return "BigInteger !=";
        }
    }

    public static class BooleanEqualEvaluator extends BaseEvaluator {

        private static final long      serialVersionUID = 400L;
        private final static Evaluator INSTANCE         = new BooleanEqualEvaluator();

        public BooleanEqualEvaluator() {
            super( ValueType.PBOOLEAN_TYPE,
                   Operator.EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if ( extractor.isNullValue( workingMemory, object1 ) ) {
                return object2.isNull();
            } else if ( object2.isNull() ) {
                return false;
            }

            return extractor.getBooleanValue( workingMemory, object1 ) == object2.getBooleanValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if ( context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return context.isRightNull();
            } else if ( context.isRightNull() ) {
                return false;
            }

            return context.declaration.getExtractor().getBooleanValue( workingMemory, left ) == ((BooleanVariableContextEntry) context).right;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object object2) {
            if ( context.extractor.isNullValue( workingMemory, object2 )) {
                return context.isLeftNull();
            } else if ( context.isLeftNull() ) {
                return false;
            }

            return context.extractor.getBooleanValue( workingMemory, object2 ) == ((BooleanVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if (extractor1.isNullValue( workingMemory, object1 )) {
                return extractor2.isNullValue( workingMemory, object2 );
            } else if (extractor2.isNullValue( workingMemory, object2 )) {
                return false;
            }

            return extractor1.getBooleanValue( workingMemory, object1 ) == extractor2.getBooleanValue( workingMemory, object2 );
        }

        public String toString() {
            return "Boolean ==";
        }

    }

    public static class BooleanNotEqualEvaluator extends BaseEvaluator {
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new BooleanNotEqualEvaluator();

        public BooleanNotEqualEvaluator() {
            super( ValueType.PBOOLEAN_TYPE,
                   Operator.NOT_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if ( extractor.isNullValue( workingMemory, object1 ) ) {
                return !object2.isNull();
            } else if ( object2.isNull() ) {
                return true;
            }

            return extractor.getBooleanValue( workingMemory, object1 ) != object2.getBooleanValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if ( context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return !context.isRightNull();
            } else if ( context.isRightNull() ) {
                return true;
            }
            return context.declaration.getExtractor().getBooleanValue( workingMemory, left ) != ((BooleanVariableContextEntry) context).right;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object object2) {
            if ( context.extractor.isNullValue( workingMemory, object2 )) {
                return !context.isLeftNull();
            } else if ( context.isLeftNull() ) {
                return true;
            }

            return context.extractor.getBooleanValue( workingMemory, object2 ) != ((BooleanVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if (extractor1.isNullValue( workingMemory, object1 )) {
                return !extractor2.isNullValue( workingMemory, object2 );
            } else if (extractor2.isNullValue( workingMemory, object2 )) {
                return true;
            }

            return extractor1.getBooleanValue( workingMemory, object1 ) != extractor2.getBooleanValue( workingMemory, object2 );
        }

        public String toString() {
            return "Boolean !=";
        }
    }

    public static class ByteEqualEvaluator extends BaseEvaluator {
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new ByteEqualEvaluator();

        public ByteEqualEvaluator() {
            super( ValueType.PBYTE_TYPE,
                   Operator.EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if ( extractor.isNullValue( workingMemory, object1 ) ) {
                return object2.isNull();
            } else if ( object2.isNull() ) {
                return false;
            }

            return extractor.getByteValue( workingMemory, object1 ) == object2.getByteValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if ( context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return context.isRightNull();
            } else if ( context.isRightNull() ) {
                return false;
            }

            return context.declaration.getExtractor().getByteValue( workingMemory, left ) == ((LongVariableContextEntry) context).right;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if ( context.extractor.isNullValue( workingMemory, right )) {
                return context.isLeftNull();
            } else if ( context.isLeftNull() ) {
                return false;
            }

            return ((LongVariableContextEntry) context).left == context.extractor.getByteValue( workingMemory, right );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if (extractor1.isNullValue( workingMemory, object1 )) {
                return extractor2.isNullValue( workingMemory, object2 );
            } else if (extractor2.isNullValue( workingMemory, object2 )) {
                return false;
            }

            return extractor1.getByteValue( workingMemory, object1 ) == extractor2.getByteValue( workingMemory, object2 );
        }

        public String toString() {
            return "Byte ==";
        }

    }

    public static class ByteNotEqualEvaluator extends BaseEvaluator {
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new ByteNotEqualEvaluator();

        public ByteNotEqualEvaluator() {
            super( ValueType.PBYTE_TYPE,
                   Operator.NOT_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if ( extractor.isNullValue( workingMemory, object1 ) ) {
                return !object2.isNull();
            } else if ( object2.isNull() ) {
                return true;
            }

            return extractor.getByteValue( workingMemory, object1 ) != object2.getByteValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if ( context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return !context.isRightNull();
            } else if ( context.isRightNull() ) {
                return true;
            }

            return context.declaration.getExtractor().getByteValue( workingMemory, left ) != ((LongVariableContextEntry) context).right;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object object2) {
            if ( context.extractor.isNullValue( workingMemory, object2 ) ) {
                return !context.isLeftNull();
            } else if ( context.isLeftNull() ) {
                return true;
            }

            return ((LongVariableContextEntry) context).left != context.extractor.getByteValue( workingMemory, object2 );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if (extractor1.isNullValue( workingMemory, object1 )) {
                return !extractor2.isNullValue( workingMemory, object2 );
            } else if (extractor2.isNullValue( workingMemory, object2 )) {
                return true;
            }

            return extractor1.getByteValue( workingMemory, object1 ) != extractor2.getByteValue( workingMemory, object2 );
        }

        public String toString() {
            return "Byte !=";
        }
    }

    public static class CharacterEqualEvaluator extends BaseEvaluator {
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new CharacterEqualEvaluator();

        public CharacterEqualEvaluator() {
            super( ValueType.PCHAR_TYPE,
                   Operator.EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
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

            return context.declaration.getExtractor().getCharValue( workingMemory, left ) == ((CharVariableContextEntry) context).right;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if ( context.extractor.isNullValue( workingMemory, right )) {
                return context.isLeftNull();
            } else if ( context.isLeftNull() ) {
                return false;
            }

            return ((CharVariableContextEntry) context).left == context.extractor.getCharValue( workingMemory, right );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
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

    public static class CharacterNotEqualEvaluator extends BaseEvaluator {
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new CharacterNotEqualEvaluator();

        public CharacterNotEqualEvaluator() {
            super( ValueType.PCHAR_TYPE,
                   Operator.NOT_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
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

            return context.declaration.getExtractor().getCharValue( workingMemory, left ) != ((CharVariableContextEntry) context).right;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if ( context.extractor.isNullValue( workingMemory, right ) ) {
                return !context.isLeftNull();
            } else if ( context.isLeftNull() ) {
                return true;
            }

            return ((CharVariableContextEntry) context).left != context.extractor.getCharValue( workingMemory, right );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
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

    public static class DateEqualEvaluator extends BaseEvaluator {
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new DateEqualEvaluator();

        public DateEqualEvaluator() {
            super( ValueType.DATE_TYPE,
                   Operator.EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            final Date value1 = (Date) extractor.getValue( workingMemory, object1 );
            final Object value2 = object2.getValue();
            if ( value1 == null ) {
                return value2 == null;
            }
            if ( value2 == null ) {
                return false;
            }
            return value1.compareTo( DateUtils.getRightDate( value2, workingMemory.getDateFormats() ) ) == 0;
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            final Date value1 = (Date) context.declaration.getExtractor().getValue( workingMemory, left );
            final Object value2 = ((ObjectVariableContextEntry) context).right;
            if ( value1 == null ) {
                return value2 == null;
            }
            if ( value2 == null ) {
                return false;
            }
            return value1.compareTo( DateUtils.getRightDate( value2, workingMemory.getDateFormats() ) ) == 0;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            final Date value1 = (Date) ((ObjectVariableContextEntry) context).left;
            final Object value2 = context.extractor.getValue( workingMemory, right );
            if ( value1 == null ) {
                return value2 == null;
            }
            if ( value2 == null ) {
                return false;
            }
            return value1.compareTo( DateUtils.getRightDate( value2, workingMemory.getDateFormats() ) ) == 0;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            final Date value1 = (Date) extractor1.getValue( workingMemory, object1 );
            final Date value2 = (Date) extractor2.getValue( workingMemory, object2 );
            if ( value1 == null ) {
                return value2 == null;
            }
            if ( value2 == null ) {
                return false;
            }
            return value1.compareTo( value2 ) == 0;
        }

        public String toString() {
            return "Date ==";
        }

    }

    public static class DateNotEqualEvaluator extends BaseEvaluator {
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new DateNotEqualEvaluator();

        public DateNotEqualEvaluator() {
            super( ValueType.DATE_TYPE,
                   Operator.NOT_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            final Date value1 = (Date) extractor.getValue( workingMemory, object1 );
            final Object value2 = object2.getValue();
            if ( value1 == null ) {
                return value2 != null;
            }
            if ( value2 == null ) {
                return true;
            }
            return value1.compareTo( DateUtils.getRightDate( value2, workingMemory.getDateFormats() ) ) != 0;
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            final Date value1 = (Date) context.declaration.getExtractor().getValue( workingMemory, left );
            final Object value2 = ((ObjectVariableContextEntry) context).right;
            if ( value1 == null ) {
                return value2 != null;
            }
            if ( value2 == null ) {
                return true;
            }
            return value1.compareTo( DateUtils.getRightDate( value2, workingMemory.getDateFormats() ) ) != 0;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            final Date value1 = (Date) ((ObjectVariableContextEntry) context).left;
            final Object value2 = context.extractor.getValue( workingMemory, right );
            if ( value1 == null ) {
                return value2 != null;
            }
            if ( value2 == null ) {
                return true;
            }
            return value1.compareTo( DateUtils.getRightDate( value2, workingMemory.getDateFormats() ) ) != 0;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            final Date value1 = (Date) extractor1.getValue( workingMemory, object1 );
            final Date value2 = (Date) extractor2.getValue( workingMemory, object2 );
            if ( value1 == null ) {
                return value2 != null;
            }
            if ( value2 == null ) {
                return true;
            }
            return value1.compareTo( value2 ) != 0;
        }

        public String toString() {
            return "Date !=";
        }
    }

    public static class DoubleEqualEvaluator extends BaseEvaluator {
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new DoubleEqualEvaluator();

        public DoubleEqualEvaluator() {
            super( ValueType.PDOUBLE_TYPE,
                   Operator.EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if ( extractor.isNullValue( workingMemory, object1 ) ) {
                return object2.isNull();
            } else if ( object2.isNull() ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return extractor.getDoubleValue( workingMemory, object1 ) == object2.getDoubleValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if ( context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return context.isRightNull();
            } else if ( context.isRightNull() ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return context.declaration.getExtractor().getDoubleValue( workingMemory, left ) == ((DoubleVariableContextEntry) context).right;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if ( context.extractor.isNullValue( workingMemory, right )) {
                return context.isLeftNull();
            } else if ( context.isLeftNull() ) {
                return false;
            }

            // TODO: we are not handling delta right now... maybe we should
            return ((DoubleVariableContextEntry) context).left == context.extractor.getDoubleValue( workingMemory, right );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if (extractor1.isNullValue( workingMemory, object1 )) {
                return extractor2.isNullValue( workingMemory, object2 );
            } else if (extractor2.isNullValue( workingMemory, object2 )) {
                return false;
            }

            // TODO: we are not handling delta right now... maybe we should
            return extractor1.getDoubleValue( workingMemory, object1 ) == extractor2.getDoubleValue( workingMemory, object2 );
        }

        public String toString() {
            return "Double ==";
        }
    }

    public static class DoubleNotEqualEvaluator extends BaseEvaluator {
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new DoubleNotEqualEvaluator();

        public DoubleNotEqualEvaluator() {
            super( ValueType.PDOUBLE_TYPE,
                   Operator.NOT_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if ( extractor.isNullValue( workingMemory, object1 ) ) {
                return !object2.isNull();
            } else if ( object2.isNull() ) {
                return true;
            }

            // TODO: we are not handling delta right now... maybe we should
            return extractor.getDoubleValue( workingMemory, object1 ) != object2.getDoubleValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if ( context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return !context.isRightNull();
            } else if ( context.isRightNull() ) {
                return true;
            }

            // TODO: we are not handling delta right now... maybe we should
            return context.declaration.getExtractor().getDoubleValue( workingMemory, left ) != ((DoubleVariableContextEntry) context).right;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if ( context.extractor.isNullValue( workingMemory, right )) {
                return !context.isLeftNull();
            } else if ( context.isLeftNull() ) {
                return true;
            }

            // TODO: we are not handling delta right now... maybe we should
            return ((DoubleVariableContextEntry) context).left != context.extractor.getDoubleValue( workingMemory, right );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if (extractor1.isNullValue( workingMemory, object1 )) {
                return !extractor2.isNullValue( workingMemory, object2 );
            } else if (extractor2.isNullValue( workingMemory, object2 )) {
                return true;
            }

            // TODO: we are not handling delta right now... maybe we should
            return extractor1.getDoubleValue( workingMemory, object1 ) != extractor2.getDoubleValue( workingMemory, object2 );
        }

        public String toString() {
            return "Double !=";
        }
    }

    public static class FactTemplateEqualEvaluator extends BaseEvaluator {
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new FactTemplateEqualEvaluator();

        public FactTemplateEqualEvaluator() {
            super( ValueType.FACTTEMPLATE_TYPE,
                   Operator.EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
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
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            final Object value1 = extractor1.getValue( workingMemory, object1 );
            final Object value2 = extractor2.getValue( workingMemory, object2 );
            if ( value1 == null ) {
                return value2 == null;
            }
            return value1.equals( value2 );
        }

        public String toString() {
            return "FactTemplate ==";
        }

    }

    public static class FactTemplateNotEqualEvaluator extends BaseEvaluator {
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new FactTemplateNotEqualEvaluator();

        public FactTemplateNotEqualEvaluator() {
            super( ValueType.FACTTEMPLATE_TYPE,
                   Operator.NOT_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
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
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            final Object value1 = extractor1.getValue( workingMemory, object1 );
            final Object value2 = extractor2.getValue( workingMemory, object2 );
            if ( value1 == null ) {
                return value2 != null;
            }
            return !value1.equals( value2 );
        }

        public String toString() {
            return "FactTemplate !=";
        }
    }

    public static class FloatEqualEvaluator extends BaseEvaluator {

        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new FloatEqualEvaluator();

        public FloatEqualEvaluator() {
            super( ValueType.PFLOAT_TYPE,
                   Operator.EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if ( extractor.isNullValue( workingMemory, object1 ) ) {
                return object2.isNull();
            } else if ( object2.isNull() ) {
                return false;
            }

            // TODO: we are not handling delta right now... maybe we should
            return extractor.getFloatValue( workingMemory, object1 ) == object2.getFloatValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if ( context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return context.isRightNull();
            } else if ( context.isRightNull() ) {
                return false;
            }

            // TODO: we are not handling delta right now... maybe we should
            return context.declaration.getExtractor().getFloatValue( workingMemory, left ) == ((DoubleVariableContextEntry) context).right;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if ( context.extractor.isNullValue( workingMemory, right )) {
                return context.isLeftNull();
            } else if ( context.isLeftNull() ) {
                return false;
            }

            // TODO: we are not handling delta right now... maybe we should
            return ((DoubleVariableContextEntry) context).left == context.extractor.getFloatValue( workingMemory, right );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if (extractor1.isNullValue( workingMemory, object1 )) {
                return extractor2.isNullValue( workingMemory, object2 );
            } else if (extractor2.isNullValue( workingMemory, object2 )) {
                return false;
            }

            // TODO: we are not handling delta right now... maybe we should
            return extractor1.getFloatValue( workingMemory, object1 ) == extractor2.getFloatValue( workingMemory, object2 );
        }

        public String toString() {
            return "Float ==";
        }
    }

    public static class FloatNotEqualEvaluator extends BaseEvaluator {
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new FloatNotEqualEvaluator();

        public FloatNotEqualEvaluator() {
            super( ValueType.PFLOAT_TYPE,
                   Operator.NOT_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if ( extractor.isNullValue( workingMemory, object1 ) ) {
                return !object2.isNull();
            } else if ( object2.isNull() ) {
                return true;
            }

            // TODO: we are not handling delta right now... maybe we should
            return extractor.getFloatValue( workingMemory, object1 ) != object2.getFloatValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if ( context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return !context.isRightNull();
            } else if ( context.isRightNull() ) {
                return true;
            }

            // TODO: we are not handling delta right now... maybe we should
            return context.declaration.getExtractor().getFloatValue( workingMemory, left ) != ((DoubleVariableContextEntry) context).right;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if ( context.extractor.isNullValue( workingMemory, right ) ) {
                return !context.isLeftNull();
            } else if ( context.isLeftNull() ) {
                return true;
            }

            // TODO: we are not handling delta right now... maybe we should
            return ((DoubleVariableContextEntry) context).left != context.extractor.getFloatValue( workingMemory, right );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if (extractor1.isNullValue( workingMemory, object1 )) {
                return !extractor2.isNullValue( workingMemory, object2 );
            } else if (extractor2.isNullValue( workingMemory, object2 )) {
                return true;
            }

            // TODO: we are not handling delta right now... maybe we should
            return extractor1.getFloatValue( workingMemory, object1 ) != extractor2.getFloatValue( workingMemory, object2 );
        }

        public String toString() {
            return "Float !=";
        }
    }

    static public class IntegerEqualEvaluator extends BaseEvaluator {
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new IntegerEqualEvaluator();

        public IntegerEqualEvaluator() {
            super( ValueType.PINTEGER_TYPE,
                   Operator.EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
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
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
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

    public static class IntegerNotEqualEvaluator extends BaseEvaluator {
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new IntegerNotEqualEvaluator();

        public IntegerNotEqualEvaluator() {
            super( ValueType.PINTEGER_TYPE,
                   Operator.NOT_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
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
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
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

    public static class LongEqualEvaluator extends BaseEvaluator {
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new LongEqualEvaluator();

        public LongEqualEvaluator() {
            super( ValueType.PLONG_TYPE,
                   Operator.EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if ( extractor.isNullValue( workingMemory, object1 ) ) {
                return object2.isNull();
            } else if ( object2.isNull() ) {
                return false;
            }

            return extractor.getLongValue( workingMemory, object1 ) == object2.getLongValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if ( context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return context.isRightNull();
            } else if ( context.isRightNull() ) {
                return false;
            }

            return context.declaration.getExtractor().getLongValue( workingMemory, left ) == ((LongVariableContextEntry) context).right;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if ( context.extractor.isNullValue( workingMemory, right )) {
                return context.isLeftNull();
            } else if ( context.isLeftNull() ) {
                return false;
            }

            return ((LongVariableContextEntry) context).left == context.extractor.getLongValue( workingMemory, right );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if (extractor1.isNullValue( workingMemory, object1 )) {
                return extractor2.isNullValue( workingMemory, object2 );
            } else if (extractor2.isNullValue( workingMemory, object2 )) {
                return false;
            }

            return extractor1.getLongValue( workingMemory, object1 ) == extractor2.getLongValue( workingMemory, object2 );
        }

        public String toString() {
            return "Long ==";
        }
    }

    public static class LongNotEqualEvaluator extends BaseEvaluator {
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new LongNotEqualEvaluator();

        public LongNotEqualEvaluator() {
            super( ValueType.PLONG_TYPE,
                   Operator.NOT_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if ( extractor.isNullValue( workingMemory, object1 ) ) {
                return !object2.isNull();
            } else if ( object2.isNull() ) {
                return true;
            }

            return extractor.getLongValue( workingMemory, object1 ) != object2.getLongValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if ( context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return !context.isRightNull();
            } else if ( context.isRightNull() ) {
                return true;
            }

            return context.declaration.getExtractor().getLongValue( workingMemory, left ) != ((LongVariableContextEntry) context).right;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if ( context.extractor.isNullValue( workingMemory, right ) ) {
                return !context.isLeftNull();
            } else if ( context.isLeftNull() ) {
                return true;
            }

            return ((LongVariableContextEntry) context).left != context.extractor.getLongValue( workingMemory, right );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if (extractor1.isNullValue( workingMemory, object1 )) {
                return !extractor2.isNullValue( workingMemory, object2 );
            } else if (extractor2.isNullValue( workingMemory, object2 )) {
                return true;
            }

            return extractor1.getLongValue( workingMemory, object1 ) != extractor2.getLongValue( workingMemory, object2 );
        }

        public String toString() {
            return "Long !=";
        }
    }

    public static class ObjectEqualEvaluator extends BaseEvaluator {
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new ObjectEqualEvaluator();
        private static final ObjectEqualsComparator comparator = new ObjectEqualsComparator();

        public ObjectEqualEvaluator() {
            super( ValueType.OBJECT_TYPE,
                   Operator.EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            final Object value1 = extractor.getValue( workingMemory, object1 );
            final Object value2 = object2.getValue();
            return comparator.equals( value1, value2 );
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            final Object value = context.declaration.getExtractor().getValue( workingMemory, left );
            return comparator.equals( ((ObjectVariableContextEntry) context).right, value );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            final Object value = context.extractor.getValue( workingMemory, right );
            return comparator.equals( value, ((ObjectVariableContextEntry) context).left );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            final Object value1 = extractor1.getValue( workingMemory, object1 );
            final Object value2 = extractor2.getValue( workingMemory, object2 );
            return comparator.equals( value1, value2 );
        }

        public String toString() {
            return "Object ==";
        }

    }

    public static class ObjectNotEqualEvaluator extends BaseEvaluator {
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new ObjectNotEqualEvaluator();
        private static final ObjectEqualsComparator comparator = new ObjectEqualsComparator();

        public ObjectNotEqualEvaluator() {
            super( ValueType.OBJECT_TYPE,
                   Operator.NOT_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            final Object value1 = extractor.getValue( workingMemory, object1 );
            final Object value2 = object2.getValue();
            return !comparator.equals( value1, value2 );
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            final Object value = context.declaration.getExtractor().getValue( workingMemory, left );
            return !comparator.equals( ((ObjectVariableContextEntry) context).right, value );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            final Object value = context.extractor.getValue( workingMemory, right );
            return !comparator.equals( value, ((ObjectVariableContextEntry) context).left );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            final Object value1 = extractor1.getValue( workingMemory, object1 );
            final Object value2 = extractor2.getValue( workingMemory, object2 );
            return !comparator.equals( value1, value2 );
        }

        public String toString() {
            return "Object !=";
        }
    }

    public static class ShortEqualEvaluator extends BaseEvaluator {
        private static final long      serialVersionUID = 400L;
        private static final Evaluator INSTANCE         = new ShortEqualEvaluator();

        public ShortEqualEvaluator() {
            super( ValueType.PSHORT_TYPE,
                   Operator.EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if ( extractor.isNullValue( workingMemory, object1 ) ) {
                return object2.isNull();
            } else if ( object2.isNull() ) {
                return false;
            }

            return extractor.getShortValue( workingMemory, object1 ) == object2.getShortValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if ( context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return context.isRightNull();
            } else if ( context.isRightNull() ) {
                return false;
            }

            return context.declaration.getExtractor().getShortValue( workingMemory, left ) == ((LongVariableContextEntry) context).right;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if ( context.extractor.isNullValue( workingMemory, right )) {
                return context.isLeftNull();
            } else if ( context.isLeftNull() ) {
                return false;
            }

            return ((LongVariableContextEntry) context).left == context.extractor.getShortValue( workingMemory, right );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if (extractor1.isNullValue( workingMemory, object1 )) {
                return extractor2.isNullValue( workingMemory, object2 );
            } else if (extractor2.isNullValue( workingMemory, object2 )) {
                return false;
            }

            return extractor1.getShortValue( workingMemory, object1 ) == extractor2.getShortValue( workingMemory, object2 );
        }

        public String toString() {
            return "Short ==";
        }
    }

    public static class ShortNotEqualEvaluator extends BaseEvaluator {
        private static final long      serialVersionUID = 400L;
        private static final Evaluator INSTANCE         = new ShortNotEqualEvaluator();

        public ShortNotEqualEvaluator() {
            super( ValueType.PSHORT_TYPE,
                   Operator.NOT_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if ( extractor.isNullValue( workingMemory, object1 ) ) {
                return !object2.isNull();
            } else if ( object2.isNull() ) {
                return true;
            }

            return extractor.getShortValue( workingMemory, object1 ) != object2.getShortValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if ( context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return !context.isRightNull();
            } else if ( context.isRightNull() ) {
                return true;
            }

            return context.declaration.getExtractor().getShortValue( workingMemory, left ) != ((LongVariableContextEntry) context).right;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if ( context.extractor.isNullValue( workingMemory, right ) ) {
                return !context.isLeftNull();
            } else if ( context.isLeftNull() ) {
                return true;
            }

            return ((LongVariableContextEntry) context).left != context.extractor.getShortValue( workingMemory, right );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if (extractor1.isNullValue( workingMemory, object1 )) {
                return !extractor2.isNullValue( workingMemory, object2 );
            } else if (extractor2.isNullValue( workingMemory, object2 )) {
                return true;
            }

            return extractor1.getShortValue( workingMemory, object1 ) != extractor2.getShortValue( workingMemory, object2 );
        }

        public String toString() {
            return "Short !=";
        }
    }

    static public class StringEqualEvaluator extends BaseEvaluator {
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new StringEqualEvaluator();

        public StringEqualEvaluator() {
            super( ValueType.STRING_TYPE,
                   Operator.EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
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
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
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

    public static class StringNotEqualEvaluator extends BaseEvaluator {
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new StringNotEqualEvaluator();

        public StringNotEqualEvaluator() {
            super( ValueType.STRING_TYPE,
                   Operator.NOT_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
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
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
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

    protected static class ObjectEqualsComparator {

        // trying to implement runtime type coercion
        public boolean equals( Object arg0, Object arg1 ) {
            if ( arg0 == null || arg1 == null ) {
                return arg0 == arg1;
            }
            if( arg0 instanceof Number ){
                double val0 = ((Number) arg0).doubleValue();
                double val1 = 0;
                if( arg1 instanceof Number ) {
                    val1 = ((Number) arg1).doubleValue();
                } else if( arg1 instanceof String ) {
                    try {
                        val1 = Double.parseDouble( ( String ) arg1 );
                    } catch( Exception ex ) {
                        // parsing failed, so not a number
                        return false;
                    }
                } else {
                    // if it is not a number, it is not equals
                    return false;
                }
                return val0 == val1; // in the future we may need to handle rounding errors
            }
            if( arg0 instanceof String ) {
                return arg0.equals( arg1.toString() );
            }
            if( arg0 instanceof Boolean ) {
                if( arg1 instanceof String ) {
                    return ((Boolean)arg0).booleanValue() == Boolean.valueOf( (String)arg1 ).booleanValue();
                }
            }
            if( arg0 instanceof Character ) {
                if( arg1 instanceof String && ((String) arg1).length() == 1 ) {
                    return ((Character)arg0).charValue() == ((String)arg1).charAt( 0 );
                }
            }
            return arg0.equals( arg1 );
        }
    }


}
