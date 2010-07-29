/**
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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.Date;

import org.drools.base.BaseEvaluator;
import org.drools.base.ValueType;
import org.drools.common.InternalWorkingMemory;
import org.drools.core.util.DateUtils;
import org.drools.core.util.MathUtils;
import org.drools.rule.VariableRestriction.CharVariableContextEntry;
import org.drools.rule.VariableRestriction.DoubleVariableContextEntry;
import org.drools.rule.VariableRestriction.LongVariableContextEntry;
import org.drools.rule.VariableRestriction.ObjectVariableContextEntry;
import org.drools.rule.VariableRestriction.VariableContextEntry;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldValue;
import org.drools.spi.InternalReadAccessor;

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
        private static final long serialVersionUID = 510l;
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
    public static class BigDecimalLessEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new BigDecimalLessEvaluator();

        public BigDecimalLessEvaluator() {
            super( ValueType.BIG_DECIMAL_TYPE,
                   Operator.LESS );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) || object2.getValue() == null ) {
                return false;
            }
            final BigDecimal comp = extractor.getBigDecimalValue( workingMemory, object1 );
            return comp.compareTo( object2.getBigDecimalValue() ) < 0;
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull || context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return false;
            }
            final BigDecimal comp = MathUtils.getBigDecimal( ((ObjectVariableContextEntry) context).right );
            return comp.compareTo( context.declaration.getExtractor().getBigDecimalValue( workingMemory, left ) ) < 0;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) || ((ObjectVariableContextEntry) context).left == null ) {
                return false;
            }
            final BigDecimal comp = context.extractor.getBigDecimalValue( workingMemory, right );
            return comp.compareTo( MathUtils.getBigDecimal( ((ObjectVariableContextEntry) context).left )) < 0;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) || extractor2.isNullValue( workingMemory, object2 ) ) {
                return false;
            }
            final BigDecimal comp = extractor1.getBigDecimalValue( workingMemory, object1 );
            return comp.compareTo( extractor2.getBigDecimalValue( workingMemory, object2 ) ) < 0;
        }

        public String toString() {
            return "BigDecimal <";
        }
    }

    public static class BigDecimalLessOrEqualEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new BigDecimalLessOrEqualEvaluator();

        public BigDecimalLessOrEqualEvaluator() {
            super( ValueType.BIG_DECIMAL_TYPE,
                   Operator.LESS_OR_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) || object2.getValue() == null ) {
                return false;
            }
            final BigDecimal comp = extractor.getBigDecimalValue( workingMemory, object1 );
            return comp.compareTo( object2.getBigDecimalValue() ) <= 0;
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull || context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return false;
            }
            final BigDecimal comp = MathUtils.getBigDecimal( ((ObjectVariableContextEntry) context).right );
            return comp.compareTo( context.declaration.getExtractor().getBigDecimalValue( workingMemory, left ) ) <= 0;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) || ((ObjectVariableContextEntry) context).left == null ) {
                return false;
            }
            final BigDecimal comp = context.extractor.getBigDecimalValue( workingMemory, right );
            return comp.compareTo( MathUtils.getBigDecimal( ((ObjectVariableContextEntry) context).left ) ) <= 0;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) || extractor2.isNullValue( workingMemory, object2 ) ) {
                return false;
            }
            final BigDecimal comp = extractor1.getBigDecimalValue( workingMemory, object1 );
            return comp.compareTo( extractor2.getBigDecimalValue( workingMemory, object2 ) ) <= 0;
        }

        public String toString() {
            return "BigDecimal <=";
        }
    }

    public static class BigDecimalGreaterEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new BigDecimalGreaterEvaluator();

        public BigDecimalGreaterEvaluator() {
            super( ValueType.BIG_DECIMAL_TYPE,
                   Operator.GREATER );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) || object2.getValue() == null ) {
                return false;
            }
            final BigDecimal comp = extractor.getBigDecimalValue( workingMemory, object1 );
            return comp.compareTo( object2.getBigDecimalValue() ) > 0;
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull || context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return false;
            }
            final BigDecimal comp = MathUtils.getBigDecimal( ((ObjectVariableContextEntry) context).right );
            return comp.compareTo( context.declaration.getExtractor().getBigDecimalValue( workingMemory, left ) ) > 0;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) || ((ObjectVariableContextEntry) context).left == null ) {
                return false;
            }
            final BigDecimal comp = context.extractor.getBigDecimalValue( workingMemory, right );
            return comp.compareTo( MathUtils.getBigDecimal( ((ObjectVariableContextEntry) context).left ) ) > 0;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) || extractor2.isNullValue( workingMemory, object2 ) ) {
                return false;
            }
            final BigDecimal comp = extractor1.getBigDecimalValue( workingMemory, object1 );
            return comp.compareTo( extractor2.getBigDecimalValue( workingMemory, object2 ) ) > 0;
        }

        public String toString() {
            return "BigDecimal >";
        }
    }

    public static class BigDecimalGreaterOrEqualEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long      serialVersionUID = 400L;
        private final static Evaluator INSTANCE         = new BigDecimalGreaterOrEqualEvaluator();

        public BigDecimalGreaterOrEqualEvaluator() {
            super( ValueType.BIG_DECIMAL_TYPE,
                   Operator.GREATER_OR_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) || object2.getValue() == null ) {
                return false;
            }
            final BigDecimal comp = extractor.getBigDecimalValue( workingMemory, object1 );
            return comp.compareTo( object2.getBigDecimalValue() ) >= 0;
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull || context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return false;
            }
            final BigDecimal comp = MathUtils.getBigDecimal( ((ObjectVariableContextEntry) context).right );
            return comp.compareTo( context.declaration.getExtractor().getBigDecimalValue( workingMemory, left ) ) >= 0;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) || ((ObjectVariableContextEntry) context).left == null ) {
                return false;
            }
            final BigDecimal comp = context.extractor.getBigDecimalValue( workingMemory, right );
            return comp.compareTo( MathUtils.getBigDecimal( ((ObjectVariableContextEntry) context).left ) ) >= 0;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) || extractor2.isNullValue( workingMemory, object2 ) ) {
                return false;
            }
            final BigDecimal comp = extractor1.getBigDecimalValue( workingMemory, object1 );
            return comp.compareTo( extractor2.getBigDecimalValue( workingMemory, object2 ) ) >= 0;
        }

        public String toString() {
            return "BigDecimal >=";
        }
    }

    public static class BigIntegerLessEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new BigIntegerLessEvaluator();

        public BigIntegerLessEvaluator() {
            super( ValueType.BIG_INTEGER_TYPE,
                   Operator.LESS );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) || object2.getValue() == null ) {
                return false;
            }
            final BigInteger comp =  extractor.getBigIntegerValue( workingMemory, object1 );
            return comp.compareTo( object2.getBigIntegerValue() ) < 0;
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull || context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return false;
            }
            final BigInteger comp = MathUtils.getBigInteger( ((ObjectVariableContextEntry) context).right );
            return comp.compareTo(  context.declaration.getExtractor().getBigIntegerValue( workingMemory, left ) ) < 0;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) || ((ObjectVariableContextEntry) context).left == null ) {
                return false;
            }
            final BigInteger comp =  context.extractor.getBigIntegerValue( workingMemory, right );
            return comp.compareTo( MathUtils.getBigInteger(  ((ObjectVariableContextEntry) context).left )) < 0;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) || extractor2.isNullValue( workingMemory, object2 ) ) {
                return false;
            }
            final BigInteger comp =  extractor1.getBigIntegerValue( workingMemory, object1 );
            return comp.compareTo(  extractor2.getBigIntegerValue( workingMemory, object2 ) ) < 0;
        }

        public String toString() {
            return "BigInteger <";
        }
    }

    public static class BigIntegerLessOrEqualEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new BigIntegerLessOrEqualEvaluator();

        //TODO - fix deserialization so that same instances will be used.
        public BigIntegerLessOrEqualEvaluator() {
            super( ValueType.BIG_INTEGER_TYPE,
                   Operator.LESS_OR_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) || object2.getValue() == null ) {
                return false;
            }
            final BigInteger comp =  extractor.getBigIntegerValue( workingMemory, object1 );
            return comp.compareTo( object2.getBigIntegerValue() ) <= 0;
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull || context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return false;
            }
            final BigInteger comp = MathUtils.getBigInteger( ((ObjectVariableContextEntry) context).right );
            return comp.compareTo(  context.declaration.getExtractor().getBigIntegerValue( workingMemory, left ) ) <= 0;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) || ((ObjectVariableContextEntry) context).left == null ) {
                return false;
            }
            final BigInteger comp = context.extractor.getBigIntegerValue( workingMemory, right );
            return comp.compareTo( MathUtils.getBigInteger( ((ObjectVariableContextEntry) context).left )) <= 0;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) || extractor2.isNullValue( workingMemory, object2 ) ) {
                return false;
            }
            final BigInteger comp =  extractor1.getBigIntegerValue( workingMemory, object1 );
            return comp.compareTo(  extractor2.getBigIntegerValue( workingMemory, object2 ) ) <= 0;
        }

        public String toString() {
            return "BigInteger <=";
        }
    }

    public static class BigIntegerGreaterEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new BigIntegerGreaterEvaluator();

        public BigIntegerGreaterEvaluator() {
            super( ValueType.BIG_INTEGER_TYPE,
                   Operator.GREATER );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) || object2.getValue() == null ) {
                return false;
            }
            final BigInteger comp =  extractor.getBigIntegerValue( workingMemory, object1 );
            return comp.compareTo( object2.getBigIntegerValue() ) > 0;
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull || context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return false;
            }
            final BigInteger comp = MathUtils.getBigInteger( ((ObjectVariableContextEntry) context).right );
            return comp.compareTo( context.declaration.getExtractor().getBigIntegerValue( workingMemory, left ) ) > 0;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) || ((ObjectVariableContextEntry) context).left == null ) {
                return false;
            }
            final BigInteger comp =  context.extractor.getBigIntegerValue( workingMemory, right );
            return comp.compareTo( MathUtils.getBigInteger( ((ObjectVariableContextEntry) context).left ) ) > 0;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) || extractor2.isNullValue( workingMemory, object2 ) ) {
                return false;
            }
            final BigInteger comp =  extractor1.getBigIntegerValue( workingMemory, object1 );
            return comp.compareTo(  extractor2.getBigIntegerValue( workingMemory, object2 ) ) > 0;
        }

        public String toString() {
            return "BigInteger >";
        }
    }

    public static class BigIntegerGreaterOrEqualEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long      serialVersionUID = 400L;
        private final static Evaluator INSTANCE         = new BigIntegerGreaterOrEqualEvaluator();

        public BigIntegerGreaterOrEqualEvaluator() {
            super( ValueType.BIG_INTEGER_TYPE,
                   Operator.GREATER_OR_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) || object2.getValue() == null ) {
                return false;
            }
            final BigInteger comp =  extractor.getBigIntegerValue( workingMemory, object1 );
            return comp.compareTo( object2.getBigIntegerValue() ) >= 0;
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull || context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return false;
            }
            final BigInteger comp = MathUtils.getBigInteger( ((ObjectVariableContextEntry) context).right );
            return comp.compareTo(  context.declaration.getExtractor().getBigIntegerValue( workingMemory, left ) ) >= 0;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) || ((ObjectVariableContextEntry) context).left == null ) {
                return false;
            }
            final BigInteger comp =  context.extractor.getBigIntegerValue( workingMemory, right );
            return comp.compareTo( MathUtils.getBigInteger( ((ObjectVariableContextEntry) context).left ) ) >= 0;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) || extractor2.isNullValue( workingMemory, object2 ) ) {
                return false;
            }
            final BigInteger comp =  extractor1.getBigIntegerValue( workingMemory, object1 );
            return comp.compareTo(  extractor2.getBigIntegerValue( workingMemory, object2 ) ) >= 0;
        }

        public String toString() {
            return "BigInteger >=";
        }
    }

    public static class ByteLessEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new ByteLessEvaluator();

        public ByteLessEvaluator() {
            super( ValueType.PBYTE_TYPE,
                   Operator.LESS );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) || object2.getValue() == null ) {
                return false;
            }
            return extractor.getByteValue( workingMemory, object1 ) < object2.getByteValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull || context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return false;
            }
            return ((LongVariableContextEntry) context).right < context.declaration.getExtractor().getByteValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) || context.leftNull ) {
                return false;
            }
            return context.extractor.getByteValue( workingMemory, right ) < ((LongVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) || extractor2.isNullValue( workingMemory, object2 ) ) {
                return false;
            }
            return extractor1.getByteValue( workingMemory, object1 ) < extractor2.getByteValue( workingMemory, object2 );
        }

        public String toString() {
            return "Byte <";
        }
    }

    public static class ByteLessOrEqualEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new ByteLessOrEqualEvaluator();

        public ByteLessOrEqualEvaluator() {
            super( ValueType.PBYTE_TYPE,
                   Operator.LESS_OR_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) || object2.getValue() == null ) {
                return false;
            }
            return extractor.getByteValue( workingMemory, object1 ) <= object2.getByteValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull || context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return false;
            }
            return ((LongVariableContextEntry) context).right <= context.declaration.getExtractor().getByteValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) || context.leftNull ) {
                return false;
            }
            return context.extractor.getByteValue( workingMemory, right ) <= ((LongVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) || extractor2.isNullValue( workingMemory, object2 ) ) {
                return false;
            }
            return extractor1.getByteValue( workingMemory, object1 ) <= extractor2.getByteValue( workingMemory, object2 );
        }

        public String toString() {
            return "Byte <=";
        }
    }

    public static class ByteGreaterEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new ByteGreaterEvaluator();

        public ByteGreaterEvaluator() {
            super( ValueType.PBYTE_TYPE,
                   Operator.GREATER );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) || object2.getValue() == null ) {
                return false;
            }
            return extractor.getByteValue( workingMemory, object1 ) > object2.getByteValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull || context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return false;
            }
            return ((LongVariableContextEntry) context).right > context.declaration.getExtractor().getByteValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) || context.leftNull ) {
                return false;
            }
            return context.extractor.getByteValue( workingMemory, right ) > ((LongVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) || extractor2.isNullValue( workingMemory, object2 ) ) {
                return false;
            }
            return extractor1.getByteValue( workingMemory, object1 ) > extractor2.getByteValue( workingMemory, object2 );
        }

        public String toString() {
            return "Byte >";
        }
    }

    public static class ByteGreaterOrEqualEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long      serialVersionUID = 400L;
        private final static Evaluator INSTANCE         = new ByteGreaterOrEqualEvaluator();

        public ByteGreaterOrEqualEvaluator() {
            super( ValueType.PBYTE_TYPE,
                   Operator.GREATER_OR_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) || object2.getValue() == null ) {
                return false;
            }
            return extractor.getByteValue( workingMemory, object1 ) >= object2.getByteValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull || context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return false;
            }
            return ((LongVariableContextEntry) context).right >= context.declaration.getExtractor().getByteValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) || context.leftNull ) {
                return false;
            }
            return context.extractor.getByteValue( workingMemory, right ) >= ((LongVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) || extractor2.isNullValue( workingMemory, object2 ) ) {
                return false;
            }
            return extractor1.getByteValue( workingMemory, object1 ) >= extractor2.getByteValue( workingMemory, object2 );
        }

        public String toString() {
            return "Byte >=";
        }
    }

    public static class CharacterLessEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new CharacterLessEvaluator();

        public CharacterLessEvaluator() {
            super( ValueType.PCHAR_TYPE,
                   Operator.LESS );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) || object2.getValue() == null ) {
                return false;
            }
            return extractor.getCharValue( workingMemory, object1 ) < object2.getCharValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull || context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return false;
            }
            return ((CharVariableContextEntry) context).right < context.declaration.getExtractor().getCharValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) || context.leftNull ) {
                return false;
            }
            return context.extractor.getCharValue( workingMemory, right ) < ((CharVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) || extractor2.isNullValue( workingMemory, object2 ) ) {
                return false;
            }
            return extractor1.getCharValue( workingMemory, object1 ) < extractor2.getCharValue( workingMemory, object2 );
        }

        public String toString() {
            return "Character <";
        }
    }

    public static class CharacterLessOrEqualEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new CharacterLessOrEqualEvaluator();

        public CharacterLessOrEqualEvaluator() {
            super( ValueType.PCHAR_TYPE,
                   Operator.LESS_OR_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) || object2.getValue() == null ) {
                return false;
            }
            return extractor.getCharValue( workingMemory, object1 ) <= object2.getCharValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull || context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return false;
            }
            return ((CharVariableContextEntry) context).right <= context.declaration.getExtractor().getCharValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) || context.leftNull ) {
                return false;
            }
            return context.extractor.getCharValue( workingMemory, right ) <= ((CharVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) || extractor2.isNullValue( workingMemory, object2 ) ) {
                return false;
            }
            return extractor1.getCharValue( workingMemory, object1 ) <= extractor2.getCharValue( workingMemory, object2 );
        }

        public String toString() {
            return "Character <=";
        }
    }

    public static class CharacterGreaterEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new CharacterGreaterEvaluator();

        public CharacterGreaterEvaluator() {
            super( ValueType.PCHAR_TYPE,
                   Operator.GREATER );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) || object2.getValue() == null ) {
                return false;
            }
            return extractor.getCharValue( workingMemory, object1 ) > object2.getCharValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull || context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return false;
            }
            return ((CharVariableContextEntry) context).right > context.declaration.getExtractor().getCharValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) || context.leftNull ) {
                return false;
            }
            return context.extractor.getCharValue( workingMemory, right ) > ((CharVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) || extractor2.isNullValue( workingMemory, object2 ) ) {
                return false;
            }
            return extractor1.getCharValue( workingMemory, object1 ) > extractor2.getCharValue( workingMemory, object2 );
        }

        public String toString() {
            return "Character >";
        }
    }

    public static class CharacterGreaterOrEqualEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long      serialVersionUID = 400L;
        private final static Evaluator INSTANCE         = new CharacterGreaterOrEqualEvaluator();

        public CharacterGreaterOrEqualEvaluator() {
            super( ValueType.PCHAR_TYPE,
                   Operator.GREATER_OR_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) || object2.getValue() == null ) {
                return false;
            }
            return extractor.getCharValue( workingMemory, object1 ) >= object2.getCharValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull || context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return false;
            }
            return ((CharVariableContextEntry) context).right >= context.declaration.getExtractor().getCharValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) || context.leftNull ) {
                return false;
            }
            return context.extractor.getCharValue( workingMemory, right ) >= ((CharVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) || extractor2.isNullValue( workingMemory, object2 ) ) {
                return false;
            }
            return extractor1.getCharValue( workingMemory, object1 ) >= extractor2.getCharValue( workingMemory, object2 );
        }

        public String toString() {
            return "Character >=";
        }
    }

    public static class DateLessEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new DateLessEvaluator();

        public DateLessEvaluator() {
            super( ValueType.DATE_TYPE,
                   Operator.LESS );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) || object2.getValue() == null ) {
                return false;
            }
            final Date value1 = (Date) extractor.getValue( workingMemory, object1 );
            final Object value2 = object2.getValue();
            return value1.compareTo( DateUtils.getRightDate( value2, workingMemory.getDateFormats() ) ) < 0;
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull || context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return false;
            }
            final Date value1 = (Date) context.declaration.getExtractor().getValue( workingMemory, left );
            final Object value2 = ((ObjectVariableContextEntry) context).right;
            return DateUtils.getRightDate( value2, workingMemory.getDateFormats() ).compareTo( value1 ) < 0;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) || context.leftNull ) {
                return false;
            }
            final Date value1 = (Date) ((ObjectVariableContextEntry) context).left;
            final Object value2 = context.extractor.getValue( workingMemory, right );
            return DateUtils.getRightDate( value2, workingMemory.getDateFormats() ).compareTo( value1 ) < 0;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) || extractor2.isNullValue( workingMemory, object2 ) ) {
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

    public static class DateLessOrEqualEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new DateLessOrEqualEvaluator();

        public DateLessOrEqualEvaluator() {
            super( ValueType.DATE_TYPE,
                   Operator.LESS_OR_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) || object2.getValue() == null ) {
                return false;
            }
            final Date value1 = (Date) extractor.getValue( workingMemory, object1 );
            final Object value2 = object2.getValue();
            return value1.compareTo( DateUtils.getRightDate( value2, workingMemory.getDateFormats() ) ) <= 0;
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull || context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return false;
            }
            final Date value1 = (Date) context.declaration.getExtractor().getValue( workingMemory, left );
            final Object value2 = ((ObjectVariableContextEntry) context).right;
            return DateUtils.getRightDate( value2, workingMemory.getDateFormats() ).compareTo( value1 ) <= 0;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) || ((ObjectVariableContextEntry) context).left == null ) {
                return false;
            }
            final Date value1 = (Date) ((ObjectVariableContextEntry) context).left;
            final Object value2 = context.extractor.getValue( workingMemory, right );
            return DateUtils.getRightDate( value2, workingMemory.getDateFormats() ).compareTo( value1 ) <= 0;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) || extractor2.isNullValue( workingMemory, object2 ) ) {
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

    public static class DateGreaterEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new DateGreaterEvaluator();

        public DateGreaterEvaluator() {
            super( ValueType.DATE_TYPE,
                   Operator.GREATER );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) || object2.getValue() == null ) {
                return false;
            }
            final Date value1 = (Date) extractor.getValue( workingMemory, object1 );
            final Object value2 = object2.getValue();
            return value1.compareTo( DateUtils.getRightDate( value2, workingMemory.getDateFormats() ) ) > 0;
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull || context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return false;
            }
            final Date value1 = (Date) context.declaration.getExtractor().getValue( workingMemory, left );
            final Object value2 = ((ObjectVariableContextEntry) context).right;
            return DateUtils.getRightDate( value2, workingMemory.getDateFormats() ).compareTo( value1 ) > 0;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) || ((ObjectVariableContextEntry) context).left == null ) {
                return false;
            }
            final Date value1 = (Date) ((ObjectVariableContextEntry) context).left;
            final Object value2 = context.extractor.getValue( workingMemory, right );
            return DateUtils.getRightDate( value2, workingMemory.getDateFormats() ).compareTo( value1 ) > 0;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) || extractor2.isNullValue( workingMemory, object2 ) ) {
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

    public static class DateGreaterOrEqualEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long      serialVersionUID = 400L;
        private final static Evaluator INSTANCE         = new DateGreaterOrEqualEvaluator();

        public DateGreaterOrEqualEvaluator() {
            super( ValueType.DATE_TYPE,
                   Operator.GREATER_OR_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) || object2.getValue() == null ) {
                return false;
            }
            final Date value1 = (Date) extractor.getValue( workingMemory, object1 );
            final Object value2 = object2.getValue();
            return value1.compareTo( DateUtils.getRightDate( value2, workingMemory.getDateFormats() ) ) >= 0;
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull || context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return false;
            }
            final Date value1 = (Date) context.declaration.getExtractor().getValue( workingMemory, left );
            final Object value2 = ((ObjectVariableContextEntry) context).right;
            return DateUtils.getRightDate( value2, workingMemory.getDateFormats() ).compareTo( value1 ) >= 0;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) || ((ObjectVariableContextEntry) context).left == null ) {
                return false;
            }
            final Date value1 = (Date) ((ObjectVariableContextEntry) context).left;
            final Object value2 = context.extractor.getValue( workingMemory, right );
            return DateUtils.getRightDate( value2, workingMemory.getDateFormats() ).compareTo( value1 ) >= 0;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) || extractor2.isNullValue( workingMemory, object2 ) ) {
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

    public static class DoubleLessEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new DoubleLessEvaluator();

        public DoubleLessEvaluator() {
            super( ValueType.PDOUBLE_TYPE,
                   Operator.LESS );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) || object2.getValue() == null ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return extractor.getDoubleValue( workingMemory, object1 ) < object2.getDoubleValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull || context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return ((DoubleVariableContextEntry) context).right < context.declaration.getExtractor().getDoubleValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) || context.leftNull ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return context.extractor.getDoubleValue( workingMemory, right ) < ((DoubleVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) || extractor2.isNullValue( workingMemory, object2 ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return extractor1.getDoubleValue( workingMemory, object1 ) < extractor2.getDoubleValue( workingMemory, object2 );
        }

        public String toString() {
            return "Double <";
        }
    }

    public static class DoubleLessOrEqualEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new DoubleLessOrEqualEvaluator();

        public DoubleLessOrEqualEvaluator() {
            super( ValueType.PDOUBLE_TYPE,
                   Operator.LESS_OR_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) || object2.getValue() == null ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return extractor.getDoubleValue( workingMemory, object1 ) <= object2.getDoubleValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull || context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return ((DoubleVariableContextEntry) context).right <= context.declaration.getExtractor().getDoubleValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) || context.leftNull ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return context.extractor.getDoubleValue( workingMemory, right ) <= ((DoubleVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) || extractor2.isNullValue( workingMemory, object2 ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return extractor1.getDoubleValue( workingMemory, object1 ) <= extractor2.getDoubleValue( workingMemory, object2 );
        }

        public String toString() {
            return "Double <=";
        }
    }

    public static class DoubleGreaterEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new DoubleGreaterEvaluator();

        public DoubleGreaterEvaluator() {
            super( ValueType.PDOUBLE_TYPE,
                   Operator.GREATER );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) || object2.getValue() == null ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return extractor.getDoubleValue( workingMemory, object1 ) > object2.getDoubleValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull || context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return ((DoubleVariableContextEntry) context).right > context.declaration.getExtractor().getDoubleValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) || context.leftNull ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return context.extractor.getDoubleValue( workingMemory, right ) > ((DoubleVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) || extractor2.isNullValue( workingMemory, object2 ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return extractor1.getDoubleValue( workingMemory, object1 ) > extractor2.getDoubleValue( workingMemory, object2 );
        }

        public String toString() {
            return "Double >";
        }
    }

    public static class DoubleGreaterOrEqualEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long      serialVersionUID = 400L;
        private final static Evaluator INSTANCE         = new DoubleGreaterOrEqualEvaluator();

        public DoubleGreaterOrEqualEvaluator() {
            super( ValueType.PDOUBLE_TYPE,
                   Operator.GREATER_OR_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) || object2.getValue() == null ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return extractor.getDoubleValue( workingMemory, object1 ) >= object2.getDoubleValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull || context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return ((DoubleVariableContextEntry) context).right >= context.declaration.getExtractor().getDoubleValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) || context.leftNull ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return context.extractor.getDoubleValue( workingMemory, right ) >= ((DoubleVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) || extractor2.isNullValue( workingMemory, object2 ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return extractor1.getDoubleValue( workingMemory, object1 ) >= extractor2.getDoubleValue( workingMemory, object2 );
        }

        public String toString() {
            return "Double >=";
        }
    }

    public static class FloatLessEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new FloatLessEvaluator();

        public FloatLessEvaluator() {
            super( ValueType.PFLOAT_TYPE,
                   Operator.LESS );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) || object2.getValue() == null ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return extractor.getFloatValue( workingMemory, object1 ) < object2.getFloatValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull || context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return ((DoubleVariableContextEntry) context).right < context.declaration.getExtractor().getFloatValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) || context.leftNull ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return context.extractor.getFloatValue( workingMemory, right ) < ((DoubleVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) || extractor2.isNullValue( workingMemory, object2 ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return extractor1.getFloatValue( workingMemory, object1 ) < extractor2.getFloatValue( workingMemory, object2 );
        }

        public String toString() {
            return "Float <";
        }
    }

    public static class FloatLessOrEqualEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new FloatLessOrEqualEvaluator();

        public FloatLessOrEqualEvaluator() {
            super( ValueType.PFLOAT_TYPE,
                   Operator.LESS_OR_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) || object2.getValue() == null ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return extractor.getFloatValue( workingMemory, object1 ) <= object2.getFloatValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull || context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return ((DoubleVariableContextEntry) context).right <= context.declaration.getExtractor().getFloatValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) || context.leftNull ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return context.extractor.getFloatValue( workingMemory, right ) <= ((DoubleVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) || extractor2.isNullValue( workingMemory, object2 ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return extractor1.getFloatValue( workingMemory, object1 ) <= extractor2.getFloatValue( workingMemory, object2 );
        }

        public String toString() {
            return "Float <=";
        }
    }

    public static class FloatGreaterEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new FloatGreaterEvaluator();

        public FloatGreaterEvaluator() {
            super( ValueType.PFLOAT_TYPE,
                   Operator.GREATER );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) || object2.getValue() == null ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return extractor.getFloatValue( workingMemory, object1 ) > object2.getFloatValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull || context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return ((DoubleVariableContextEntry) context).right > context.declaration.getExtractor().getFloatValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) || context.leftNull ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return context.extractor.getFloatValue( workingMemory, right ) > ((DoubleVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) || extractor2.isNullValue( workingMemory, object2 ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return extractor1.getFloatValue( workingMemory, object1 ) > extractor2.getFloatValue( workingMemory, object2 );
        }

        public String toString() {
            return "Float >";
        }
    }

    public static class FloatGreaterOrEqualEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long      serialVersionUID = 400L;
        private final static Evaluator INSTANCE         = new FloatGreaterOrEqualEvaluator();

        public FloatGreaterOrEqualEvaluator() {
            super( ValueType.PFLOAT_TYPE,
                   Operator.GREATER_OR_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) || object2.getValue() == null ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return extractor.getFloatValue( workingMemory, object1 ) >= object2.getFloatValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull || context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return ((DoubleVariableContextEntry) context).right >= context.declaration.getExtractor().getFloatValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) || context.leftNull ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return context.extractor.getFloatValue( workingMemory, right ) >= ((DoubleVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) || extractor2.isNullValue( workingMemory, object2 ) ) {
                return false;
            }
            // TODO: we are not handling delta right now... maybe we should
            return extractor1.getFloatValue( workingMemory, object1 ) >= extractor2.getFloatValue( workingMemory, object2 );
        }

        public String toString() {
            return "Float >=";
        }
    }

    public static class IntegerLessEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new IntegerLessEvaluator();

        public IntegerLessEvaluator() {
            super( ValueType.PINTEGER_TYPE,
                   Operator.LESS );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) || object2.getValue() == null ) {
                return false;
            }
            return extractor.getIntValue( workingMemory, object1 ) < object2.getIntValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull || context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return false;
            }
            return ((LongVariableContextEntry) context).right < context.declaration.getExtractor().getIntValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) || context.leftNull ) {
                return false;
            }
            return context.extractor.getIntValue( workingMemory, right ) < ((LongVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) || extractor2.isNullValue( workingMemory, object2 ) ) {
                return false;
            }
            return extractor1.getIntValue( workingMemory, object1 ) < extractor2.getIntValue( workingMemory, object2 );
        }

        public String toString() {
            return "Integer <";
        }
    }

    public static class IntegerLessOrEqualEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new IntegerLessOrEqualEvaluator();

        public IntegerLessOrEqualEvaluator() {
            super( ValueType.PINTEGER_TYPE,
                   Operator.LESS_OR_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) || object2.getValue() == null ) {
                return false;
            }
            return extractor.getIntValue( workingMemory, object1 ) <= object2.getIntValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull || context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return false;
            }
            return ((LongVariableContextEntry) context).right <= context.declaration.getExtractor().getIntValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) || context.leftNull ) {
                return false;
            }
            return context.extractor.getIntValue( workingMemory, right ) <= ((LongVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) || extractor2.isNullValue( workingMemory, object2 ) ) {
                return false;
            }
            return extractor1.getIntValue( workingMemory, object1 ) <= extractor2.getIntValue( workingMemory, object2 );
        }

        public String toString() {
            return "Integer <=";
        }
    }

    static public class IntegerGreaterEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new IntegerGreaterEvaluator();

        public IntegerGreaterEvaluator() {
            super( ValueType.PINTEGER_TYPE,
                   Operator.GREATER );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) || object2.getValue() == null ) {
                return false;
            }
            return extractor.getIntValue( workingMemory, object1 ) > object2.getIntValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull || context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return false;
            }
            return ((LongVariableContextEntry) context).right > context.declaration.getExtractor().getIntValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) || context.leftNull ) {
                return false;
            }
            return context.extractor.getIntValue( workingMemory, right ) > ((LongVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) || extractor2.isNullValue( workingMemory, object2 ) ) {
                return false;
            }
            return extractor1.getIntValue( workingMemory, object1 ) > extractor2.getIntValue( workingMemory, object2 );
        }

        public String toString() {
            return "Integer >";
        }
    }

    public static class IntegerGreaterOrEqualEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long      serialVersionUID = 400L;
        private final static Evaluator INSTANCE         = new IntegerGreaterOrEqualEvaluator();

        public IntegerGreaterOrEqualEvaluator() {
            super( ValueType.PINTEGER_TYPE,
                   Operator.GREATER_OR_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) || object2.getValue() == null ) {
                return false;
            }
            return extractor.getIntValue( workingMemory, object1 ) >= object2.getIntValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull || context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return false;
            }
            return ((LongVariableContextEntry) context).right >= context.declaration.getExtractor().getIntValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) || context.leftNull ) {
                return false;
            }
            return context.extractor.getIntValue( workingMemory, right ) >= ((LongVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) || extractor2.isNullValue( workingMemory, object2 ) ) {
                return false;
            }
            return extractor1.getIntValue( workingMemory, object1 ) >= extractor2.getIntValue( workingMemory, object2 );
        }

        public String toString() {
            return "Integer >=";
        }
    }

    public static class LongLessEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new LongLessEvaluator();

        public LongLessEvaluator() {
            super( ValueType.PLONG_TYPE,
                   Operator.LESS );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) || object2.getValue() == null ) {
                return false;
            }
            return extractor.getLongValue( workingMemory, object1 ) < object2.getLongValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull || context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return false;
            }
            return ((LongVariableContextEntry) context).right < context.declaration.getExtractor().getLongValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) || context.leftNull ) {
                return false;
            }
            return context.extractor.getLongValue( workingMemory, right ) < ((LongVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) || extractor2.isNullValue( workingMemory, object2 ) ) {
                return false;
            }
            return extractor1.getLongValue( workingMemory, object1 ) < extractor2.getLongValue( workingMemory, object2 );
        }

        public String toString() {
            return "Long <";
        }
    }

    public static class LongLessOrEqualEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new LongLessOrEqualEvaluator();

        public LongLessOrEqualEvaluator() {
            super( ValueType.PLONG_TYPE,
                   Operator.LESS_OR_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) || object2.getValue() == null ) {
                return false;
            }
            return extractor.getLongValue( workingMemory, object1 ) <= object2.getLongValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull || context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return false;
            }
            return ((LongVariableContextEntry) context).right <= context.declaration.getExtractor().getLongValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) || context.leftNull ) {
                return false;
            }
            return context.extractor.getLongValue( workingMemory, right ) <= ((LongVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) || extractor2.isNullValue( workingMemory, object2 ) ) {
                return false;
            }
            return extractor1.getLongValue( workingMemory, object1 ) <= extractor2.getLongValue( workingMemory, object2 );
        }

        public String toString() {
            return "Long <=";
        }
    }

    public static class LongGreaterEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new LongGreaterEvaluator();

        public LongGreaterEvaluator() {
            super( ValueType.PLONG_TYPE,
                   Operator.GREATER );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) || object2.getValue() == null ) {
                return false;
            }
            return extractor.getLongValue( workingMemory, object1 ) > object2.getLongValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull || context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return false;
            }
            return ((LongVariableContextEntry) context).right > context.declaration.getExtractor().getLongValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) || context.leftNull ) {
                return false;
            }
            return context.extractor.getLongValue( workingMemory, right ) > ((LongVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) || extractor2.isNullValue( workingMemory, object2 ) ) {
                return false;
            }
            return extractor1.getLongValue( workingMemory, object1 ) > extractor2.getLongValue( workingMemory, object2 );
        }

        public String toString() {
            return "Long >";
        }
    }

    public static class LongGreaterOrEqualEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long      serialVersionUID = 400L;
        private final static Evaluator INSTANCE         = new LongGreaterOrEqualEvaluator();

        public LongGreaterOrEqualEvaluator() {
            super( ValueType.PLONG_TYPE,
                   Operator.GREATER_OR_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) || object2.getValue() == null ) {
                return false;
            }
            return extractor.getLongValue( workingMemory, object1 ) >= object2.getLongValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull || context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return false;
            }
            return ((LongVariableContextEntry) context).right >= context.declaration.getExtractor().getLongValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) || context.leftNull ) {
                return false;
            }
            return context.extractor.getLongValue( workingMemory, right ) >= ((LongVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) || extractor2.isNullValue( workingMemory, object2 ) ) {
                return false;
            }
            return extractor1.getLongValue( workingMemory, object1 ) >= extractor2.getLongValue( workingMemory, object2 );
        }

        public String toString() {
            return "Long >=";
        }
    }

    public static class ObjectLessEvaluator extends BaseEvaluator {
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new ObjectLessEvaluator();
        private static final ObjectComparator comparator = new ObjectComparator();


        public ObjectLessEvaluator() {
            super( ValueType.OBJECT_TYPE,
                   Operator.LESS );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) || object2.getValue() == null ) {
                return false;
            }
            final Comparable comp = (Comparable) extractor.getValue( workingMemory, object1 );
            return comparator.compare( comp, object2.getValue() ) < 0;
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull || context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return false;
            }
            final Comparable comp = (Comparable) ((ObjectVariableContextEntry) context).right;
            return comparator.compare( comp,  context.declaration.getExtractor().getValue( workingMemory, left ) ) < 0;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) || ((ObjectVariableContextEntry) context).left == null ) {
                return false;
            }
            final Comparable comp = (Comparable) context.extractor.getValue( workingMemory, right );
            return comparator.compare( comp, ((ObjectVariableContextEntry) context).left ) < 0;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) || extractor2.isNullValue( workingMemory, object2 ) ) {
                return false;
            }
            final Comparable comp = (Comparable) extractor1.getValue( workingMemory, object1 );
            return comparator.compare( comp, extractor2.getValue( workingMemory, object2 ) ) < 0;
        }

        public String toString() {
            return "Object <";
        }
    }

    public static class ObjectLessOrEqualEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new ObjectLessOrEqualEvaluator();
        private static final ObjectComparator comparator = new ObjectComparator();

        public ObjectLessOrEqualEvaluator() {
            super( ValueType.OBJECT_TYPE,
                   Operator.LESS_OR_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) || object2.getValue() == null ) {
                return false;
            }
            final Comparable comp = (Comparable) extractor.getValue( workingMemory, object1 );
            return comparator.compare( comp, object2.getValue() ) <= 0;
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull || context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return false;
            }
            final Comparable comp = (Comparable) ((ObjectVariableContextEntry) context).right;
            return comparator.compare( comp, context.declaration.getExtractor().getValue( workingMemory, left ) ) <= 0;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) || ((ObjectVariableContextEntry) context).left == null ) {
                return false;
            }
            final Comparable comp = (Comparable) context.extractor.getValue( workingMemory, right );
            return comparator.compare( comp, ((ObjectVariableContextEntry) context).left ) <= 0;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) || extractor2.isNullValue( workingMemory, object2 ) ) {
                return false;
            }
            final Comparable comp = (Comparable) extractor1.getValue( workingMemory, object1 );
            return comparator.compare( comp, extractor2.getValue( workingMemory, object2 ) ) <= 0;
        }

        public String toString() {
            return "Object <=";
        }
    }

    public static class ObjectGreaterEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new ObjectGreaterEvaluator();
        private static final ObjectComparator comparator = new ObjectComparator();

        public ObjectGreaterEvaluator() {
            super( ValueType.OBJECT_TYPE,
                   Operator.GREATER );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) || object2.getValue() == null ) {
                return false;
            }
            final Comparable comp = (Comparable) extractor.getValue( workingMemory, object1 );
            return comparator.compare( comp, object2.getValue() ) > 0;
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull || context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return false;
            }
            final Comparable comp = (Comparable) ((ObjectVariableContextEntry) context).right;
            return comparator.compare( comp, context.declaration.getExtractor().getValue( workingMemory, left ) ) > 0;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) || ((ObjectVariableContextEntry) context).left == null ) {
                return false;
            }
            final Comparable comp = (Comparable) context.extractor.getValue( workingMemory, right );
            return comparator.compare( comp, ((ObjectVariableContextEntry) context).left ) > 0;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) || extractor2.isNullValue( workingMemory, object2 ) ) {
                return false;
            }
            final Comparable comp = (Comparable) extractor1.getValue( workingMemory, object1 );
            return comparator.compare( comp, extractor2.getValue( workingMemory, object2 ) ) > 0;
        }

        public String toString() {
            return "Object >";
        }
    }

    public static class ObjectGreaterOrEqualEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new ObjectGreaterOrEqualEvaluator();
        private static final ObjectComparator comparator = new ObjectComparator();

        public ObjectGreaterOrEqualEvaluator() {
            super( ValueType.OBJECT_TYPE,
                   Operator.GREATER_OR_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) || object2.getValue() == null ) {
                return false;
            }
            final Comparable comp = (Comparable) extractor.getValue( workingMemory, object1 );
            return comparator.compare( comp, object2.getValue() ) >= 0;
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull || context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return false;
            }
            final Comparable comp = (Comparable) ((ObjectVariableContextEntry) context).right;
            return comparator.compare( comp, context.declaration.getExtractor().getValue( workingMemory, left ) ) >= 0;
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) || ((ObjectVariableContextEntry) context).left == null ) {
                return false;
            }
            final Comparable comp = (Comparable) context.extractor.getValue( workingMemory, right );
            return comparator.compare( comp, ((ObjectVariableContextEntry) context).left ) >= 0;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) || extractor2.isNullValue( workingMemory, object2 ) ) {
                return false;
            }
            final Comparable comp = (Comparable) extractor1.getValue( workingMemory, object1 );
            return comparator.compare( comp, extractor2.getValue( workingMemory, object2 ) ) >= 0;
        }

        public String toString() {
            return "Object >=";
        }
    }

    public static class ShortLessEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long      serialVersionUID = 400L;
        private static final Evaluator INSTANCE         = new ShortLessEvaluator();

        public ShortLessEvaluator() {
            super( ValueType.PSHORT_TYPE,
                   Operator.LESS );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) || object2.getValue() == null ) {
                return false;
            }
            return extractor.getShortValue( workingMemory, object1 ) < object2.getShortValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull || context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return false;
            }
            return ((LongVariableContextEntry) context).right < context.declaration.getExtractor().getShortValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) || context.leftNull ) {
                return false;
            }
            return context.extractor.getShortValue( workingMemory, right ) < ((LongVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) || extractor2.isNullValue( workingMemory, object2 ) ) {
                return false;
            }
            return extractor1.getShortValue( workingMemory, object1 ) < extractor2.getShortValue( workingMemory, object2 );
        }

        public String toString() {
            return "Short <";
        }
    }

    public static class ShortLessOrEqualEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long      serialVersionUID = 400L;
        private static final Evaluator INSTANCE         = new ShortLessOrEqualEvaluator();

        public ShortLessOrEqualEvaluator() {
            super( ValueType.PSHORT_TYPE,
                   Operator.LESS_OR_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) || object2.getValue() == null ) {
                return false;
            }
            return extractor.getShortValue( workingMemory, object1 ) <= object2.getShortValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull || context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return false;
            }
            return ((LongVariableContextEntry) context).right <= context.declaration.getExtractor().getShortValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) || context.leftNull ) {
                return false;
            }
            return context.extractor.getShortValue( workingMemory, right ) <= ((LongVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) || extractor2.isNullValue( workingMemory, object2 ) ) {
                return false;
            }
            return extractor1.getShortValue( workingMemory, object1 ) <= extractor2.getShortValue( workingMemory, object2 );
        }

        public String toString() {
            return "Boolean <=";
        }
    }

    public static class ShortGreaterEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long      serialVersionUID = 400L;
        private static final Evaluator INSTANCE         = new ShortGreaterEvaluator();

        public ShortGreaterEvaluator() {
            super( ValueType.PSHORT_TYPE,
                   Operator.GREATER );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) || object2.getValue() == null ) {
                return false;
            }
            return extractor.getShortValue( workingMemory, object1 ) > object2.getShortValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull || context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return false;
            }
            return ((LongVariableContextEntry) context).right > context.declaration.getExtractor().getShortValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) || context.leftNull ) {
                return false;
            }
            return context.extractor.getShortValue( workingMemory, right ) > ((LongVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) || extractor2.isNullValue( workingMemory, object2 ) ) {
                return false;
            }
            return extractor1.getShortValue( workingMemory, object1 ) > extractor2.getShortValue( workingMemory, object2 );
        }

        public String toString() {
            return "Short >";
        }
    }

    public static class ShortGreaterOrEqualEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long      serialVersionUID = 400L;
        private static final Evaluator INSTANCE         = new ShortGreaterOrEqualEvaluator();

        public ShortGreaterOrEqualEvaluator() {
            super( ValueType.PSHORT_TYPE,
                   Operator.GREATER_OR_EQUAL );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            if( extractor.isNullValue( workingMemory, object1 ) || object2.getValue() == null ) {
                return false;
            }
            return extractor.getShortValue( workingMemory, object1 ) >= object2.getShortValue();
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            if( context.rightNull || context.declaration.getExtractor().isNullValue( workingMemory, left ) ) {
                return false;
            }
            return ((LongVariableContextEntry) context).right >= context.declaration.getExtractor().getShortValue( workingMemory, left );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            if( context.extractor.isNullValue( workingMemory, right ) || context.leftNull ) {
                return false;
            }
            return context.extractor.getShortValue( workingMemory, right ) >= ((LongVariableContextEntry) context).left;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            if( extractor1.isNullValue( workingMemory, object1 ) || extractor2.isNullValue( workingMemory, object2 ) ) {
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
