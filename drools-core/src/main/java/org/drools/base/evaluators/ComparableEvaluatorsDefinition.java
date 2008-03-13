/*
 * Copyright 2007 JBoss Inc
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
 *
 * Created on Dec 6, 2007
 */
package org.drools.base.evaluators;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.Date;

import org.drools.base.BaseEvaluator;
import org.drools.base.ShadowProxy;
import org.drools.base.ValueType;
import org.drools.common.InternalWorkingMemory;
import org.drools.rule.VariableRestriction.CharVariableContextEntry;
import org.drools.rule.VariableRestriction.DoubleVariableContextEntry;
import org.drools.rule.VariableRestriction.LongVariableContextEntry;
import org.drools.rule.VariableRestriction.ObjectVariableContextEntry;
import org.drools.rule.VariableRestriction.VariableContextEntry;
import org.drools.spi.Evaluator;
import org.drools.spi.Extractor;
import org.drools.spi.FieldValue;
import org.drools.util.DateUtils;

/**
 * This class defines all the comparable built in
 * evaluators like >, >=, etc.
 *
 * @author etirelli
 */
public class ComparableEvaluatorsDefinition implements EvaluatorDefinition {

    private static final String[] SUPPORTED_IDS = { Operator.LESS.getOperatorString(), Operator.LESS_OR_EQUAL.getOperatorString(),
                                                    Operator.GREATER.getOperatorString(), Operator.GREATER_OR_EQUAL.getOperatorString() };
    private EvaluatorCache evaluators = new EvaluatorCache() {
        private static final long serialVersionUID = 4782368623L;
        {
            addEvaluator( ValueType.BIG_DECIMAL_TYPE,   Operator.LESS,                BigDecimalLessEvaluator.INSTANCE );
            addEvaluator( ValueType.BIG_DECIMAL_TYPE,   Operator.LESS_OR_EQUAL,       BigDecimalLessOrEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.BIG_DECIMAL_TYPE,   Operator.GREATER,             BigDecimalGreaterEvaluator.INSTANCE );
            addEvaluator( ValueType.BIG_DECIMAL_TYPE,   Operator.GREATER_OR_EQUAL,    BigDecimalGreaterOrEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.BIG_INTEGER_TYPE,   Operator.LESS,                BigIntegerLessEvaluator.INSTANCE );
            addEvaluator( ValueType.BIG_INTEGER_TYPE,   Operator.LESS_OR_EQUAL,       BigIntegerLessOrEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.BIG_INTEGER_TYPE,   Operator.GREATER,             BigIntegerGreaterEvaluator.INSTANCE );
            addEvaluator( ValueType.BIG_INTEGER_TYPE,   Operator.GREATER_OR_EQUAL,    BigIntegerGreaterOrEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.BYTE_TYPE,          Operator.LESS,                ByteLessEvaluator.INSTANCE );
            addEvaluator( ValueType.BYTE_TYPE,          Operator.LESS_OR_EQUAL,       ByteLessOrEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.BYTE_TYPE,          Operator.GREATER,             ByteGreaterEvaluator.INSTANCE );
            addEvaluator( ValueType.BYTE_TYPE,          Operator.GREATER_OR_EQUAL,    ByteGreaterOrEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.PBYTE_TYPE,         Operator.LESS,                ByteLessEvaluator.INSTANCE );
            addEvaluator( ValueType.PBYTE_TYPE,         Operator.LESS_OR_EQUAL,       ByteLessOrEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.PBYTE_TYPE,         Operator.GREATER,             ByteGreaterEvaluator.INSTANCE );
            addEvaluator( ValueType.PBYTE_TYPE,         Operator.GREATER_OR_EQUAL,    ByteGreaterOrEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.CHAR_TYPE,          Operator.LESS,                CharacterLessEvaluator.INSTANCE );
            addEvaluator( ValueType.CHAR_TYPE,          Operator.LESS_OR_EQUAL,       CharacterLessOrEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.CHAR_TYPE,          Operator.GREATER,             CharacterGreaterEvaluator.INSTANCE );
            addEvaluator( ValueType.CHAR_TYPE,          Operator.GREATER_OR_EQUAL,    CharacterGreaterOrEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.PCHAR_TYPE,         Operator.LESS,                CharacterLessEvaluator.INSTANCE );
            addEvaluator( ValueType.PCHAR_TYPE,         Operator.LESS_OR_EQUAL,       CharacterLessOrEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.PCHAR_TYPE,         Operator.GREATER,             CharacterGreaterEvaluator.INSTANCE );
            addEvaluator( ValueType.PCHAR_TYPE,         Operator.GREATER_OR_EQUAL,    CharacterGreaterOrEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.DATE_TYPE,          Operator.LESS,                DateLessEvaluator.INSTANCE );
            addEvaluator( ValueType.DATE_TYPE,          Operator.LESS_OR_EQUAL,       DateLessOrEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.DATE_TYPE,          Operator.GREATER,             DateGreaterEvaluator.INSTANCE );
            addEvaluator( ValueType.DATE_TYPE,          Operator.GREATER_OR_EQUAL,    DateGreaterOrEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.DOUBLE_TYPE,        Operator.LESS,                DoubleLessEvaluator.INSTANCE );
            addEvaluator( ValueType.DOUBLE_TYPE,        Operator.LESS_OR_EQUAL,       DoubleLessOrEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.DOUBLE_TYPE,        Operator.GREATER,             DoubleGreaterEvaluator.INSTANCE );
            addEvaluator( ValueType.DOUBLE_TYPE,        Operator.GREATER_OR_EQUAL,    DoubleGreaterOrEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.PDOUBLE_TYPE,       Operator.LESS,                DoubleLessEvaluator.INSTANCE );
            addEvaluator( ValueType.PDOUBLE_TYPE,       Operator.LESS_OR_EQUAL,       DoubleLessOrEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.PDOUBLE_TYPE,       Operator.GREATER,             DoubleGreaterEvaluator.INSTANCE );
            addEvaluator( ValueType.PDOUBLE_TYPE,       Operator.GREATER_OR_EQUAL,    DoubleGreaterOrEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.FLOAT_TYPE,         Operator.LESS,                FloatLessEvaluator.INSTANCE );
            addEvaluator( ValueType.FLOAT_TYPE,         Operator.LESS_OR_EQUAL,       FloatLessOrEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.FLOAT_TYPE,         Operator.GREATER,             FloatGreaterEvaluator.INSTANCE );
            addEvaluator( ValueType.FLOAT_TYPE,         Operator.GREATER_OR_EQUAL,    FloatGreaterOrEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.PFLOAT_TYPE,        Operator.LESS,                FloatLessEvaluator.INSTANCE );
            addEvaluator( ValueType.PFLOAT_TYPE,        Operator.LESS_OR_EQUAL,       FloatLessOrEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.PFLOAT_TYPE,        Operator.GREATER,             FloatGreaterEvaluator.INSTANCE );
            addEvaluator( ValueType.PFLOAT_TYPE,        Operator.GREATER_OR_EQUAL,    FloatGreaterOrEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.INTEGER_TYPE,       Operator.LESS,                IntegerLessEvaluator.INSTANCE );
            addEvaluator( ValueType.INTEGER_TYPE,       Operator.LESS_OR_EQUAL,       IntegerLessOrEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.INTEGER_TYPE,       Operator.GREATER,             IntegerGreaterEvaluator.INSTANCE );
            addEvaluator( ValueType.INTEGER_TYPE,       Operator.GREATER_OR_EQUAL,    IntegerGreaterOrEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.PINTEGER_TYPE,      Operator.LESS,                IntegerLessEvaluator.INSTANCE );
            addEvaluator( ValueType.PINTEGER_TYPE,      Operator.LESS_OR_EQUAL,       IntegerLessOrEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.PINTEGER_TYPE,      Operator.GREATER,             IntegerGreaterEvaluator.INSTANCE );
            addEvaluator( ValueType.PINTEGER_TYPE,      Operator.GREATER_OR_EQUAL,    IntegerGreaterOrEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.LONG_TYPE,          Operator.LESS,                LongLessEvaluator.INSTANCE );
            addEvaluator( ValueType.LONG_TYPE,          Operator.LESS_OR_EQUAL,       LongLessOrEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.LONG_TYPE,          Operator.GREATER,             LongGreaterEvaluator.INSTANCE );
            addEvaluator( ValueType.LONG_TYPE,          Operator.GREATER_OR_EQUAL,    LongGreaterOrEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.PLONG_TYPE,         Operator.LESS,                LongLessEvaluator.INSTANCE );
            addEvaluator( ValueType.PLONG_TYPE,         Operator.LESS_OR_EQUAL,       LongLessOrEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.PLONG_TYPE,         Operator.GREATER,             LongGreaterEvaluator.INSTANCE );
            addEvaluator( ValueType.PLONG_TYPE,         Operator.GREATER_OR_EQUAL,    LongGreaterOrEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.OBJECT_TYPE,        Operator.LESS,                ObjectLessEvaluator.INSTANCE );
            addEvaluator( ValueType.OBJECT_TYPE,        Operator.LESS_OR_EQUAL,       ObjectLessOrEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.OBJECT_TYPE,        Operator.GREATER,             ObjectGreaterEvaluator.INSTANCE );
            addEvaluator( ValueType.OBJECT_TYPE,        Operator.GREATER_OR_EQUAL,    ObjectGreaterOrEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.SHORT_TYPE,         Operator.LESS,                ShortLessEvaluator.INSTANCE );
            addEvaluator( ValueType.SHORT_TYPE,         Operator.LESS_OR_EQUAL,       ShortLessOrEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.SHORT_TYPE,         Operator.GREATER,             ShortGreaterEvaluator.INSTANCE );
            addEvaluator( ValueType.SHORT_TYPE,         Operator.GREATER_OR_EQUAL,    ShortGreaterOrEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.PSHORT_TYPE,        Operator.LESS,                ShortLessEvaluator.INSTANCE );
            addEvaluator( ValueType.PSHORT_TYPE,        Operator.LESS_OR_EQUAL,       ShortLessOrEqualEvaluator.INSTANCE );
            addEvaluator( ValueType.PSHORT_TYPE,        Operator.GREATER,             ShortGreaterEvaluator.INSTANCE );
            addEvaluator( ValueType.PSHORT_TYPE,        Operator.GREATER_OR_EQUAL,    ShortGreaterOrEqualEvaluator.INSTANCE );
        }
    };

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
        return this.evaluators.getEvaluator( type,
                                             Operator.determineOperator( operatorId,
                                                                         isNegated ) );
    }

    public String[] getEvaluatorIds() {
        return SUPPORTED_IDS;
    }

    public boolean isNegatable() {
        return false;
    }

    public boolean operatesOnFactHandles() {
        return false;
    }

    public boolean supportsType(ValueType type) {
        return this.evaluators.supportsType( type );
    }

    /*  *********************************************************
     *           Evaluator Implementations
     *  *********************************************************
     */
    static class BigDecimalLessEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new BigDecimalLessEvaluator();

        private BigDecimalLessEvaluator() {
            super( ValueType.BIG_DECIMAL_TYPE,
                   Operator.LESS );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            final BigDecimal comp = (BigDecimal) extractor.getValue( workingMemory, object1 );
            return comp.compareTo( object2.getBigDecimalValue() ) < 0;
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull ) {
                return false;
            }
            final BigDecimal comp = (BigDecimal) ((ObjectVariableContextEntry) context).right;
            return comp.compareTo( (BigDecimal) context.declaration.getExtractor().getValue( workingMemory, left ) ) < 0;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            final BigDecimal comp = (BigDecimal) context.extractor.getValue( workingMemory, right );
            return comp.compareTo( (BigDecimal) ((ObjectVariableContextEntry) context).left ) < 0;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            final BigDecimal comp = (BigDecimal) extractor1.getValue( workingMemory, object1 );
            return comp.compareTo( (BigDecimal) extractor2.getValue( workingMemory, object2 ) ) < 0;
        }

        public String toString() {
            return "BigDecimal <";
        }
    }

    static class BigDecimalLessOrEqualEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new BigDecimalLessOrEqualEvaluator();

        private BigDecimalLessOrEqualEvaluator() {
            super( ValueType.BIG_DECIMAL_TYPE,
                   Operator.LESS_OR_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            final BigDecimal comp = (BigDecimal) extractor.getValue( workingMemory, object1 );
            return comp.compareTo( object2.getBigDecimalValue() ) <= 0;
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull ) {
                return false;
            }
            final BigDecimal comp = (BigDecimal) ((ObjectVariableContextEntry) context).right;
            return comp.compareTo( (BigDecimal) context.declaration.getExtractor().getValue( workingMemory, left ) ) <= 0;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            final BigDecimal comp = (BigDecimal) context.extractor.getValue( workingMemory, right );
            return comp.compareTo( (BigDecimal) ((ObjectVariableContextEntry) context).left ) <= 0;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            final BigDecimal comp = (BigDecimal) extractor1.getValue( workingMemory, object1 );
            return comp.compareTo( (BigDecimal) extractor2.getValue( workingMemory, object2 ) ) <= 0;
        }

        public String toString() {
            return "BigDecimal <=";
        }
    }

    static class BigDecimalGreaterEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new BigDecimalGreaterEvaluator();

        private BigDecimalGreaterEvaluator() {
            super( ValueType.BIG_DECIMAL_TYPE,
                   Operator.GREATER );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            final BigDecimal comp = (BigDecimal) extractor.getValue( workingMemory, object1 );
            return comp.compareTo( object2.getBigDecimalValue() ) > 0;
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull ) {
                return false;
            }
            final BigDecimal comp = (BigDecimal) ((ObjectVariableContextEntry) context).right;
            return comp.compareTo( (BigDecimal) context.declaration.getExtractor().getValue( workingMemory, left ) ) > 0;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            final BigDecimal comp = (BigDecimal) context.extractor.getValue( workingMemory, right );
            return comp.compareTo( (BigDecimal) ((ObjectVariableContextEntry) context).left ) > 0;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            final BigDecimal comp = (BigDecimal) extractor1.getValue( workingMemory, object1 );
            return comp.compareTo( (BigDecimal) extractor2.getValue( workingMemory, object2 ) ) > 0;
        }

        public String toString() {
            return "BigDecimal >";
        }
    }

    static class BigDecimalGreaterOrEqualEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long      serialVersionUID = 400L;
        private final static Evaluator INSTANCE         = new BigDecimalGreaterOrEqualEvaluator();

        private BigDecimalGreaterOrEqualEvaluator() {
            super( ValueType.BIG_DECIMAL_TYPE,
                   Operator.GREATER_OR_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            final BigDecimal comp = (BigDecimal) extractor.getValue( workingMemory, object1 );
            return comp.compareTo( object2.getBigDecimalValue() ) >= 0;
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull ) {
                return false;
            }
            final BigDecimal comp = (BigDecimal) ((ObjectVariableContextEntry) context).right;
            return comp.compareTo( (BigDecimal) context.declaration.getExtractor().getValue( workingMemory, left ) ) >= 0;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            final BigDecimal comp = (BigDecimal) context.extractor.getValue( workingMemory, right );
            return comp.compareTo( (BigDecimal) ((ObjectVariableContextEntry) context).left ) >= 0;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            final BigDecimal comp = (BigDecimal) extractor1.getValue( workingMemory, object1 );
            return comp.compareTo( (BigDecimal) extractor2.getValue( workingMemory, object2 ) ) >= 0;
        }

        public String toString() {
            return "BigDecimal >=";
        }
    }

    static class BigIntegerLessEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new BigIntegerLessEvaluator();

        private BigIntegerLessEvaluator() {
            super( ValueType.BIG_INTEGER_TYPE,
                   Operator.LESS );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            final BigInteger comp = (BigInteger) extractor.getValue( workingMemory, object1 );
            return comp.compareTo( object2.getBigIntegerValue() ) < 0;
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull ) {
                return false;
            }
            final BigInteger comp = (BigInteger) ((ObjectVariableContextEntry) context).right;
            return comp.compareTo( (BigInteger) context.declaration.getExtractor().getValue( workingMemory, left ) ) < 0;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            final BigInteger comp = (BigInteger) context.extractor.getValue( workingMemory, right );
            return comp.compareTo( (BigInteger) ((ObjectVariableContextEntry) context).left ) < 0;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            final BigInteger comp = (BigInteger) extractor1.getValue( workingMemory, object1 );
            return comp.compareTo( (BigInteger) extractor2.getValue( workingMemory, object2 ) ) < 0;
        }

        public String toString() {
            return "BigInteger <";
        }
    }

    static class BigIntegerLessOrEqualEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new BigIntegerLessOrEqualEvaluator();

        private BigIntegerLessOrEqualEvaluator() {
            super( ValueType.BIG_INTEGER_TYPE,
                   Operator.LESS_OR_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            final BigInteger comp = (BigInteger) extractor.getValue( workingMemory, object1 );
            return comp.compareTo( object2.getBigIntegerValue() ) <= 0;
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull ) {
                return false;
            }
            final BigInteger comp = (BigInteger) ((ObjectVariableContextEntry) context).right;
            return comp.compareTo( (BigInteger) context.declaration.getExtractor().getValue( workingMemory, left ) ) <= 0;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            final BigInteger comp = (BigInteger) context.extractor.getValue( workingMemory, right );
            return comp.compareTo( (BigInteger) ((ObjectVariableContextEntry) context).left ) <= 0;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            final BigInteger comp = (BigInteger) extractor1.getValue( workingMemory, object1 );
            return comp.compareTo( (BigInteger) extractor2.getValue( workingMemory, object2 ) ) <= 0;
        }

        public String toString() {
            return "BigInteger <=";
        }
    }

    static class BigIntegerGreaterEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new BigIntegerGreaterEvaluator();

        private BigIntegerGreaterEvaluator() {
            super( ValueType.BIG_INTEGER_TYPE,
                   Operator.GREATER );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            final BigInteger comp = (BigInteger) extractor.getValue( workingMemory, object1 );
            return comp.compareTo( object2.getBigIntegerValue() ) > 0;
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull ) {
                return false;
            }
            final BigInteger comp = (BigInteger) ((ObjectVariableContextEntry) context).right;
            return comp.compareTo( (BigInteger) context.declaration.getExtractor().getValue( workingMemory, left ) ) > 0;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            final BigInteger comp = (BigInteger) context.extractor.getValue( workingMemory, right );
            return comp.compareTo( (BigInteger) ((ObjectVariableContextEntry) context).left ) > 0;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            final BigInteger comp = (BigInteger) extractor1.getValue( workingMemory, object1 );
            return comp.compareTo( (BigInteger) extractor2.getValue( workingMemory, object2 ) ) > 0;
        }

        public String toString() {
            return "BigInteger >";
        }
    }

    static class BigIntegerGreaterOrEqualEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long      serialVersionUID = 400L;
        private final static Evaluator INSTANCE         = new BigIntegerGreaterOrEqualEvaluator();

        private BigIntegerGreaterOrEqualEvaluator() {
            super( ValueType.BIG_INTEGER_TYPE,
                   Operator.GREATER_OR_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            final BigInteger comp = (BigInteger) extractor.getValue( workingMemory, object1 );
            return comp.compareTo( object2.getBigIntegerValue() ) >= 0;
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull ) {
                return false;
            }
            final BigInteger comp = (BigInteger) ((ObjectVariableContextEntry) context).right;
            return comp.compareTo( (BigInteger) context.declaration.getExtractor().getValue( workingMemory, left ) ) >= 0;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            final BigInteger comp = (BigInteger) context.extractor.getValue( workingMemory, right );
            return comp.compareTo( (BigInteger) ((ObjectVariableContextEntry) context).left ) >= 0;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            final BigInteger comp = (BigInteger) extractor1.getValue( workingMemory, object1 );
            return comp.compareTo( (BigInteger) extractor2.getValue( workingMemory, object2 ) ) >= 0;
        }

        public String toString() {
            return "BigInteger >=";
        }
    }

    static class ByteLessEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new ByteLessEvaluator();

        private ByteLessEvaluator() {
            super( ValueType.PBYTE_TYPE,
                   Operator.LESS );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            return extractor.getByteValue( workingMemory, object1 ) < object2.getByteValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull ) {
                return false;
            }
            return ((LongVariableContextEntry) context).right < context.declaration.getExtractor().getByteValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            return context.extractor.getByteValue( workingMemory, right ) < ((LongVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            return extractor1.getByteValue( workingMemory, object1 ) < extractor2.getByteValue( workingMemory, object2 );
        }

        public String toString() {
            return "Byte <";
        }
    }

    static class ByteLessOrEqualEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new ByteLessOrEqualEvaluator();

        private ByteLessOrEqualEvaluator() {
            super( ValueType.PBYTE_TYPE,
                   Operator.LESS_OR_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            return extractor.getByteValue( workingMemory, object1 ) <= object2.getByteValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull ) {
                return false;
            }
            return ((LongVariableContextEntry) context).right <= context.declaration.getExtractor().getByteValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            return context.extractor.getByteValue( workingMemory, right ) <= ((LongVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            return extractor1.getByteValue( workingMemory, object1 ) <= extractor2.getByteValue( workingMemory, object2 );
        }

        public String toString() {
            return "Byte <=";
        }
    }

    static class ByteGreaterEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new ByteGreaterEvaluator();

        private ByteGreaterEvaluator() {
            super( ValueType.PBYTE_TYPE,
                   Operator.GREATER );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            return extractor.getByteValue( workingMemory, object1 ) > object2.getByteValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull ) {
                return false;
            }
            return ((LongVariableContextEntry) context).right > context.declaration.getExtractor().getByteValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            return context.extractor.getByteValue( workingMemory, right ) > ((LongVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            return extractor1.getByteValue( workingMemory, object1 ) > extractor2.getByteValue( workingMemory, object2 );
        }

        public String toString() {
            return "Byte >";
        }
    }

    static class ByteGreaterOrEqualEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long      serialVersionUID = 400L;
        private final static Evaluator INSTANCE         = new ByteGreaterOrEqualEvaluator();

        private ByteGreaterOrEqualEvaluator() {
            super( ValueType.PBYTE_TYPE,
                   Operator.GREATER_OR_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            return extractor.getByteValue( workingMemory, object1 ) >= object2.getByteValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull ) {
                return false;
            }
            return ((LongVariableContextEntry) context).right >= context.declaration.getExtractor().getByteValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            return context.extractor.getByteValue( workingMemory, right ) >= ((LongVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            return extractor1.getByteValue( workingMemory, object1 ) >= extractor2.getByteValue( workingMemory, object2 );
        }

        public String toString() {
            return "Byte >=";
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
            return ((CharVariableContextEntry) context).right < context.declaration.getExtractor().getCharValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            return context.extractor.getCharValue( workingMemory, right ) < ((CharVariableContextEntry) context).left;
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
            return ((CharVariableContextEntry) context).right <= context.declaration.getExtractor().getCharValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            return context.extractor.getCharValue( workingMemory, right ) <= ((CharVariableContextEntry) context).left;
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
            return ((CharVariableContextEntry) context).right > context.declaration.getExtractor().getCharValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            return context.extractor.getCharValue( workingMemory, right ) > ((CharVariableContextEntry) context).left;
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
            return ((CharVariableContextEntry) context).right >= context.declaration.getExtractor().getCharValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            return context.extractor.getCharValue( workingMemory, right ) >= ((CharVariableContextEntry) context).left;
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

    static class DateLessEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new DateLessEvaluator();

        private DateLessEvaluator() {
            super( ValueType.DATE_TYPE,
                   Operator.LESS );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            final Date value1 = (Date) extractor.getValue( workingMemory, object1 );
            final Object value2 = object2.getValue();
            return value1.compareTo( DateUtils.getRightDate( value2 ) ) < 0;
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull ) {
                return false;
            }
            final Date value1 = (Date) context.declaration.getExtractor().getValue( workingMemory, left );
            final Object value2 = ((ObjectVariableContextEntry) context).right;
            return DateUtils.getRightDate( value2 ).compareTo( value1 ) < 0;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            final Date value1 = (Date) ((ObjectVariableContextEntry) context).left;
            final Object value2 = context.extractor.getValue( workingMemory, right );
            return DateUtils.getRightDate( value2 ).compareTo( value1 ) < 0;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            final Date value1 = (Date) extractor1.getValue( workingMemory, object1 );
            final Date value2 = (Date) extractor2.getValue( workingMemory, object2 );
            if (null == value2) throw new NullPointerException(extractor2.toString());
            return value1.compareTo( value2 ) < 0;
        }

        public String toString() {
            return "Date <";
        }
    }

    static class DateLessOrEqualEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new DateLessOrEqualEvaluator();

        private DateLessOrEqualEvaluator() {
            super( ValueType.DATE_TYPE,
                   Operator.LESS_OR_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            final Date value1 = (Date) extractor.getValue( workingMemory, object1 );
            final Object value2 = object2.getValue();
            return value1.compareTo( DateUtils.getRightDate( value2 ) ) <= 0;
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull ) {
                return false;
            }
            final Date value1 = (Date) context.declaration.getExtractor().getValue( workingMemory, left );
            final Object value2 = ((ObjectVariableContextEntry) context).right;
            return DateUtils.getRightDate( value2 ).compareTo( value1 ) <= 0;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            final Date value1 = (Date) ((ObjectVariableContextEntry) context).left;
            final Object value2 = context.extractor.getValue( workingMemory, right );
            return DateUtils.getRightDate( value2 ).compareTo( value1 ) <= 0;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            final Date value1 = (Date) extractor1.getValue( workingMemory, object1 );
            final Date value2 = (Date) extractor2.getValue( workingMemory, object2 );
            if (null == value2) throw new NullPointerException(extractor2.toString());
            return value1.compareTo( value2 ) <= 0;
        }

        public String toString() {
            return "Date <=";
        }
    }

    static class DateGreaterEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new DateGreaterEvaluator();

        private DateGreaterEvaluator() {
            super( ValueType.DATE_TYPE,
                   Operator.GREATER );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            final Date value1 = (Date) extractor.getValue( workingMemory, object1 );
            final Object value2 = object2.getValue();
            return value1.compareTo( DateUtils.getRightDate( value2 ) ) > 0;
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull ) {
                return false;
            }
            final Date value1 = (Date) context.declaration.getExtractor().getValue( workingMemory, left );
            final Object value2 = ((ObjectVariableContextEntry) context).right;
            return DateUtils.getRightDate( value2 ).compareTo( value1 ) > 0;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            final Date value1 = (Date) ((ObjectVariableContextEntry) context).left;
            final Object value2 = context.extractor.getValue( workingMemory, right );
            return DateUtils.getRightDate( value2 ).compareTo( value1 ) > 0;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            final Date value1 = (Date) extractor1.getValue( workingMemory, object1 );
            final Date value2 = (Date) extractor2.getValue( workingMemory, object2 );
            if (null == value2) throw new NullPointerException(extractor2.toString());
            return value1.compareTo( value2 ) > 0;
        }

        public String toString() {
            return "Date >";
        }
    }

    static class DateGreaterOrEqualEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long      serialVersionUID = 400L;
        private final static Evaluator INSTANCE         = new DateGreaterOrEqualEvaluator();

        private DateGreaterOrEqualEvaluator() {
            super( ValueType.DATE_TYPE,
                   Operator.GREATER_OR_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            final Date value1 = (Date) extractor.getValue( workingMemory, object1 );
            final Object value2 = object2.getValue();
            return value1.compareTo( DateUtils.getRightDate( value2 ) ) >= 0;
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull ) {
                return false;
            }
            final Date value1 = (Date) context.declaration.getExtractor().getValue( workingMemory, left );
            final Object value2 = ((ObjectVariableContextEntry) context).right;
            return DateUtils.getRightDate( value2 ).compareTo( value1 ) >= 0;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            final Date value1 = (Date) ((ObjectVariableContextEntry) context).left;
            final Object value2 = context.extractor.getValue( workingMemory, right );
            return DateUtils.getRightDate( value2 ).compareTo( value1 ) >= 0;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            final Date value1 = (Date) extractor1.getValue( workingMemory, object1 );
            final Date value2 = (Date) extractor2.getValue( workingMemory, object2 );
            if (null == value2) throw new NullPointerException(extractor2.toString());
            return value1.compareTo( value2 ) >= 0;
        }

        public String toString() {
            return "Date >=";
        }
    }

    static class DoubleLessEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new DoubleLessEvaluator();

        private DoubleLessEvaluator() {
            super( ValueType.PDOUBLE_TYPE,
                   Operator.LESS );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return extractor.getDoubleValue( workingMemory, object1 ) < object2.getDoubleValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return ((DoubleVariableContextEntry) context).right < context.declaration.getExtractor().getDoubleValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return context.extractor.getDoubleValue( workingMemory, right ) < ((DoubleVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return extractor1.getDoubleValue( workingMemory, object1 ) < extractor2.getDoubleValue( workingMemory, object2 );
        }

        public String toString() {
            return "Double <";
        }
    }

    static class DoubleLessOrEqualEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new DoubleLessOrEqualEvaluator();

        private DoubleLessOrEqualEvaluator() {
            super( ValueType.PDOUBLE_TYPE,
                   Operator.LESS_OR_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return extractor.getDoubleValue( workingMemory, object1 ) <= object2.getDoubleValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return ((DoubleVariableContextEntry) context).right <= context.declaration.getExtractor().getDoubleValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return context.extractor.getDoubleValue( workingMemory, right ) <= ((DoubleVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return extractor1.getDoubleValue( workingMemory, object1 ) <= extractor2.getDoubleValue( workingMemory, object2 );
        }

        public String toString() {
            return "Double <=";
        }
    }

    static class DoubleGreaterEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new DoubleGreaterEvaluator();

        private DoubleGreaterEvaluator() {
            super( ValueType.PDOUBLE_TYPE,
                   Operator.GREATER );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return extractor.getDoubleValue( workingMemory, object1 ) > object2.getDoubleValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return ((DoubleVariableContextEntry) context).right > context.declaration.getExtractor().getDoubleValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return context.extractor.getDoubleValue( workingMemory, right ) > ((DoubleVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return extractor1.getDoubleValue( workingMemory, object1 ) > extractor2.getDoubleValue( workingMemory, object2 );
        }

        public String toString() {
            return "Double >";
        }
    }

    static class DoubleGreaterOrEqualEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long      serialVersionUID = 400L;
        private final static Evaluator INSTANCE         = new DoubleGreaterOrEqualEvaluator();

        private DoubleGreaterOrEqualEvaluator() {
            super( ValueType.PDOUBLE_TYPE,
                   Operator.GREATER_OR_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return extractor.getDoubleValue( workingMemory, object1 ) >= object2.getDoubleValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return ((DoubleVariableContextEntry) context).right >= context.declaration.getExtractor().getDoubleValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return context.extractor.getDoubleValue( workingMemory, right ) >= ((DoubleVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return extractor1.getDoubleValue( workingMemory, object1 ) >= extractor2.getDoubleValue( workingMemory, object2 );
        }

        public String toString() {
            return "Double >=";
        }
    }

    static class FloatLessEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new FloatLessEvaluator();

        private FloatLessEvaluator() {
            super( ValueType.PFLOAT_TYPE,
                   Operator.LESS );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return extractor.getFloatValue( workingMemory, object1 ) < object2.getFloatValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return ((DoubleVariableContextEntry) context).right < context.declaration.getExtractor().getFloatValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return context.extractor.getFloatValue( workingMemory, right ) < ((DoubleVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return extractor1.getFloatValue( workingMemory, object1 ) < extractor2.getFloatValue( workingMemory, object2 );
        }

        public String toString() {
            return "Float <";
        }
    }

    static class FloatLessOrEqualEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new FloatLessOrEqualEvaluator();

        private FloatLessOrEqualEvaluator() {
            super( ValueType.PFLOAT_TYPE,
                   Operator.LESS_OR_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return extractor.getFloatValue( workingMemory, object1 ) <= object2.getFloatValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return ((DoubleVariableContextEntry) context).right <= context.declaration.getExtractor().getFloatValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return context.extractor.getFloatValue( workingMemory, right ) <= ((DoubleVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return extractor1.getFloatValue( workingMemory, object1 ) <= extractor2.getFloatValue( workingMemory, object2 );
        }

        public String toString() {
            return "Float <=";
        }
    }

    static class FloatGreaterEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new FloatGreaterEvaluator();

        private FloatGreaterEvaluator() {
            super( ValueType.PFLOAT_TYPE,
                   Operator.GREATER );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return extractor.getFloatValue( workingMemory, object1 ) > object2.getFloatValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return ((DoubleVariableContextEntry) context).right > context.declaration.getExtractor().getFloatValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return context.extractor.getFloatValue( workingMemory, right ) > ((DoubleVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return extractor1.getFloatValue( workingMemory, object1 ) > extractor2.getFloatValue( workingMemory, object2 );
        }

        public String toString() {
            return "Float >";
        }
    }

    static class FloatGreaterOrEqualEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long      serialVersionUID = 400L;
        private final static Evaluator INSTANCE         = new FloatGreaterOrEqualEvaluator();

        private FloatGreaterOrEqualEvaluator() {
            super( ValueType.PFLOAT_TYPE,
                   Operator.GREATER_OR_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return extractor.getFloatValue( workingMemory, object1 ) >= object2.getFloatValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return ((DoubleVariableContextEntry) context).right >= context.declaration.getExtractor().getFloatValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return context.extractor.getFloatValue( workingMemory, right ) >= ((DoubleVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return extractor1.getFloatValue( workingMemory, object1 ) >= extractor2.getFloatValue( workingMemory, object2 );
        }

        public String toString() {
            return "Float >=";
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

    static class LongLessEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new LongLessEvaluator();

        private LongLessEvaluator() {
            super( ValueType.PLONG_TYPE,
                   Operator.LESS );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            return extractor.getLongValue( workingMemory, object1 ) < object2.getLongValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull ) {
                return false;
            }
            return ((LongVariableContextEntry) context).right < context.declaration.getExtractor().getLongValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            return context.extractor.getLongValue( workingMemory, right ) < ((LongVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            return extractor1.getLongValue( workingMemory, object1 ) < extractor2.getLongValue( workingMemory, object2 );
        }

        public String toString() {
            return "Long <";
        }
    }

    static class LongLessOrEqualEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new LongLessOrEqualEvaluator();

        private LongLessOrEqualEvaluator() {
            super( ValueType.PLONG_TYPE,
                   Operator.LESS_OR_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            return extractor.getLongValue( workingMemory, object1 ) <= object2.getLongValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull ) {
                return false;
            }
            return ((LongVariableContextEntry) context).right <= context.declaration.getExtractor().getLongValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            return context.extractor.getLongValue( workingMemory, right ) <= ((LongVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            return extractor1.getLongValue( workingMemory, object1 ) <= extractor2.getLongValue( workingMemory, object2 );
        }

        public String toString() {
            return "Long <=";
        }
    }

    static class LongGreaterEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new LongGreaterEvaluator();

        private LongGreaterEvaluator() {
            super( ValueType.PLONG_TYPE,
                   Operator.GREATER );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            return extractor.getLongValue( workingMemory, object1 ) > object2.getLongValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull ) {
                return false;
            }
            return ((LongVariableContextEntry) context).right > context.declaration.getExtractor().getLongValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            return context.extractor.getLongValue( workingMemory, right ) > ((LongVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            return extractor1.getLongValue( workingMemory, object1 ) > extractor2.getLongValue( workingMemory, object2 );
        }

        public String toString() {
            return "Long >";
        }
    }

    static class LongGreaterOrEqualEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long      serialVersionUID = 400L;
        private final static Evaluator INSTANCE         = new LongGreaterOrEqualEvaluator();

        private LongGreaterOrEqualEvaluator() {
            super( ValueType.PLONG_TYPE,
                   Operator.GREATER_OR_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            return extractor.getLongValue( workingMemory, object1 ) >= object2.getLongValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull ) {
                return false;
            }
            return ((LongVariableContextEntry) context).right >= context.declaration.getExtractor().getLongValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            return context.extractor.getLongValue( workingMemory, right ) >= ((LongVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            return extractor1.getLongValue( workingMemory, object1 ) >= extractor2.getLongValue( workingMemory, object2 );
        }

        public String toString() {
            return "Long >=";
        }
    }

    static class ObjectLessEvaluator extends BaseEvaluator {
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new ObjectLessEvaluator();
        private static final ObjectComparator comparator = new ObjectComparator();


        private ObjectLessEvaluator() {
            super( ValueType.OBJECT_TYPE,
                   Operator.LESS );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            final Comparable comp = (Comparable) extractor.getValue( workingMemory, object1 );
            return comparator.compare( comp, object2.getValue() ) < 0;
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull ) {
                return false;
            }
            final Comparable comp = (Comparable) ((ObjectVariableContextEntry) context).right;
            return comparator.compare( comp,  context.declaration.getExtractor().getValue( workingMemory, left ) ) < 0;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            final Comparable comp = (Comparable) context.extractor.getValue( workingMemory, right );
            return comparator.compare( comp, ((ObjectVariableContextEntry) context).left ) < 0;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            final Comparable comp = (Comparable) extractor1.getValue( workingMemory, object1 );
            return comparator.compare( comp, extractor2.getValue( workingMemory, object2 ) ) < 0;
        }

        public String toString() {
            return "Object <";
        }
    }

    static class ObjectLessOrEqualEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new ObjectLessOrEqualEvaluator();
        private static final ObjectComparator comparator = new ObjectComparator();

        private ObjectLessOrEqualEvaluator() {
            super( ValueType.OBJECT_TYPE,
                   Operator.LESS_OR_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            final Comparable comp = (Comparable) extractor.getValue( workingMemory, object1 );
            return comparator.compare( comp, object2.getValue() ) <= 0;
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull ) {
                return false;
            }
            final Comparable comp = (Comparable) ((ObjectVariableContextEntry) context).right;
            return comparator.compare( comp, context.declaration.getExtractor().getValue( workingMemory, left ) ) <= 0;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            final Comparable comp = (Comparable) context.extractor.getValue( workingMemory, right );
            return comparator.compare( comp, ((ObjectVariableContextEntry) context).left ) <= 0;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            final Comparable comp = (Comparable) extractor1.getValue( workingMemory, object1 );
            return comparator.compare( comp, extractor2.getValue( workingMemory, object2 ) ) <= 0;
        }

        public String toString() {
            return "Object <=";
        }
    }

    static class ObjectGreaterEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new ObjectGreaterEvaluator();
        private static final ObjectComparator comparator = new ObjectComparator();

        private ObjectGreaterEvaluator() {
            super( ValueType.OBJECT_TYPE,
                   Operator.GREATER );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            final Comparable comp = (Comparable) extractor.getValue( workingMemory, object1 );
            return comparator.compare( comp, object2.getValue() ) > 0;
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull ) {
                return false;
            }
            final Comparable comp = (Comparable) ((ObjectVariableContextEntry) context).right;
            return comparator.compare( comp, context.declaration.getExtractor().getValue( workingMemory, left ) ) > 0;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            final Comparable comp = (Comparable) context.extractor.getValue( workingMemory, right );
            return comparator.compare( comp, ((ObjectVariableContextEntry) context).left ) > 0;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            final Comparable comp = (Comparable) extractor1.getValue( workingMemory, object1 );
            return comparator.compare( comp, extractor2.getValue( workingMemory, object2 ) ) > 0;
        }

        public String toString() {
            return "Object >";
        }
    }

    static class ObjectGreaterOrEqualEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new ObjectGreaterOrEqualEvaluator();
        private static final ObjectComparator comparator = new ObjectComparator();

        private ObjectGreaterOrEqualEvaluator() {
            super( ValueType.OBJECT_TYPE,
                   Operator.GREATER_OR_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            final Comparable comp = (Comparable) extractor.getValue( workingMemory, object1 );
            return comparator.compare( comp, object2.getValue() ) >= 0;
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull ) {
                return false;
            }
            final Comparable comp = (Comparable) ((ObjectVariableContextEntry) context).right;
            return comparator.compare( comp, context.declaration.getExtractor().getValue( workingMemory, left ) ) >= 0;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            final Comparable comp = (Comparable) context.extractor.getValue( workingMemory, right );
            return comparator.compare( comp, ((ObjectVariableContextEntry) context).left ) >= 0;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            final Comparable comp = (Comparable) extractor1.getValue( workingMemory, object1 );
            return comparator.compare( comp, extractor2.getValue( workingMemory, object2 ) ) >= 0;
        }

        public String toString() {
            return "Object >=";
        }
    }

    static class ShortLessEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long      serialVersionUID = 400L;
        private static final Evaluator INSTANCE         = new ShortLessEvaluator();

        private ShortLessEvaluator() {
            super( ValueType.PSHORT_TYPE,
                   Operator.LESS );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            return extractor.getShortValue( workingMemory, object1 ) < object2.getShortValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull ) {
                return false;
            }
            return ((LongVariableContextEntry) context).right < context.declaration.getExtractor().getShortValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            return context.extractor.getShortValue( workingMemory, right ) < ((LongVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            return extractor1.getShortValue( workingMemory, object1 ) < extractor2.getShortValue( workingMemory, object2 );
        }

        public String toString() {
            return "Short <";
        }
    }

    static class ShortLessOrEqualEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long      serialVersionUID = 400L;
        private static final Evaluator INSTANCE         = new ShortLessOrEqualEvaluator();

        private ShortLessOrEqualEvaluator() {
            super( ValueType.PSHORT_TYPE,
                   Operator.LESS_OR_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            return extractor.getShortValue( workingMemory, object1 ) <= object2.getShortValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull ) {
                return false;
            }
            return ((LongVariableContextEntry) context).right <= context.declaration.getExtractor().getShortValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            return context.extractor.getShortValue( workingMemory, right ) <= ((LongVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            return extractor1.getShortValue( workingMemory, object1 ) <= extractor2.getShortValue( workingMemory, object2 );
        }

        public String toString() {
            return "Boolean <=";
        }
    }

    static class ShortGreaterEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long      serialVersionUID = 400L;
        private static final Evaluator INSTANCE         = new ShortGreaterEvaluator();

        private ShortGreaterEvaluator() {
            super( ValueType.PSHORT_TYPE,
                   Operator.GREATER );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            return extractor.getShortValue( workingMemory, object1 ) > object2.getShortValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull ) {
                return false;
            }
            return ((LongVariableContextEntry) context).right > context.declaration.getExtractor().getShortValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            return context.extractor.getShortValue( workingMemory, right ) > ((LongVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            return extractor1.getShortValue( workingMemory, object1 ) > extractor2.getShortValue( workingMemory, object2 );
        }

        public String toString() {
            return "Short >";
        }
    }

    static class ShortGreaterOrEqualEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long      serialVersionUID = 400L;
        private static final Evaluator INSTANCE         = new ShortGreaterOrEqualEvaluator();

        private ShortGreaterOrEqualEvaluator() {
            super( ValueType.PSHORT_TYPE,
                   Operator.GREATER_OR_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            return extractor.getShortValue( workingMemory, object1 ) >= object2.getShortValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull ) {
                return false;
            }
            return ((LongVariableContextEntry) context).right >= context.declaration.getExtractor().getShortValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) ) {
                return false;
            }
            return context.extractor.getShortValue( workingMemory, right ) >= ((LongVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) ) {
                return false;
            }
            return extractor1.getShortValue( workingMemory, object1 ) >= extractor2.getShortValue( workingMemory, object2 );
        }

        public String toString() {
            return "Short >=";
        }
    }


    protected static class ObjectComparator implements Comparator {
        // this is a stateless object, and so, can be shared among threads
        // PLEASE: do not add state to it, unless you remove all concurrent
        // calls to this class instances

        public int compare(Object arg0,
                           Object arg1) {
            if( arg0 instanceof Double || arg0 instanceof Float ) {
                double val0 = ((Number) arg0).doubleValue();
                double val1 = 0;
                if( arg1 instanceof Number ) {
                    val1 = ((Number) arg1).doubleValue();
                } else if( arg1 instanceof String ) {
                    val1 = Double.parseDouble( ( String ) arg1 );
                } else {
                    throw new ClassCastException( "Not possible to convert "+arg1.getClass()+" into a double value to compare it to "+arg0.getClass() );
                }
                return val0 > val1 ? 1 : val0 < val1 ? -1 : 0;
            } else if( arg0 instanceof Number ){
                long val0 = ((Number) arg0).longValue();
                long val1 = 0;
                if( arg1 instanceof Number ) {
                    val1 = ((Number) arg1).longValue();
                } else if( arg1 instanceof String ) {
                    val1 = Long.parseLong( ( String ) arg1 );
                } else {
                    throw new ClassCastException( "Not possible to convert "+arg1.getClass()+" into a long value to compare it to "+arg0.getClass() );
                }
                return val0 > val1 ? 1 : val0 < val1 ? -1 : 0;
            } else if( arg0 instanceof String ) {
                try {
                    double val0 = Double.parseDouble( (String) arg0 );
                    double val1 = 0;
                    if( arg1 instanceof Number ) {
                        val1 = ((Number) arg1).doubleValue();
                    } else if( arg1 instanceof String ) {
                        val1 = Double.parseDouble( ( String ) arg1 );
                    } else {
                        throw new ClassCastException( "Not possible to convert "+arg1.getClass()+" into a double value to compare it to "+arg0.getClass() );
                    }
                    return val0 > val1 ? 1 : val0 < val1 ? -1 : 0;
                } catch( NumberFormatException nfe ) {
                    return ( (String) arg0).compareTo( arg1.toString() );
                }

            }
            try {
                return ((Comparable)arg0).compareTo( arg1 );
            } catch ( ClassCastException cce ) {
                throw new ClassCastException( "Not possible to compare a "+arg0.getClass()+" with a "+arg1.getClass());
            }
        }
    }

}
