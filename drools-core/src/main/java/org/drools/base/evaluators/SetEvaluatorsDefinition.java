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

import java.util.Collection;

import org.drools.base.BaseEvaluator;
import org.drools.base.ValueType;
import org.drools.common.InternalWorkingMemory;
import org.drools.rule.VariableRestriction.ObjectVariableContextEntry;
import org.drools.rule.VariableRestriction.VariableContextEntry;
import org.drools.spi.Evaluator;
import org.drools.spi.Extractor;
import org.drools.spi.FieldValue;
import org.drools.util.ShadowProxyUtils;

/**
 * This class defines all the set built in 
 * evaluators like contains, memberOf, etc.
 * 
 * @author etirelli
 */
public class SetEvaluatorsDefinition
    implements
    EvaluatorDefinition {

    public static final Operator  CONTAINS      = Operator.addOperatorToRegistry( "contains",
                                                                                  false );
    public static final Operator  NOT_CONTAINS  = Operator.addOperatorToRegistry( "contains",
                                                                                  true );
    public static final Operator  EXCLUDES      = Operator.addOperatorToRegistry( "excludes",
                                                                                  false );
    public static final Operator  NOT_EXCLUDES  = Operator.addOperatorToRegistry( "excludes",
                                                                                  true );
    public static final Operator  MEMBEROF      = Operator.addOperatorToRegistry( "memberOf",
                                                                                  false );
    public static final Operator  NOT_MEMBEROF  = Operator.addOperatorToRegistry( "memberOf",
                                                                                  true );

    private static final String[] SUPPORTED_IDS = {CONTAINS.getOperatorString(), EXCLUDES.getOperatorString(), MEMBEROF.getOperatorString()};
    private EvaluatorCache     evaluators    = new EvaluatorCache() {
        private static final long serialVersionUID = 4782368623L;
        {
            addEvaluator( ValueType.ARRAY_TYPE,                          CONTAINS,                          ArrayContainsEvaluator.INSTANCE );
            addEvaluator( ValueType.ARRAY_TYPE,                          NOT_CONTAINS,                      ArrayExcludesEvaluator.INSTANCE );
            addEvaluator( ValueType.ARRAY_TYPE,                          EXCLUDES,                          ArrayExcludesEvaluator.INSTANCE );
            addEvaluator( ValueType.ARRAY_TYPE,                          NOT_EXCLUDES,                      ArrayContainsEvaluator.INSTANCE );
            addEvaluator( ValueType.ARRAY_TYPE,                          MEMBEROF,                          ArrayMemberOfEvaluator.INSTANCE );
            addEvaluator( ValueType.ARRAY_TYPE,                          NOT_MEMBEROF,                      ArrayNotMemberOfEvaluator.INSTANCE );
            addEvaluator( ValueType.BIG_DECIMAL_TYPE,                    MEMBEROF,                          BigDecimalMemberOfEvaluator.INSTANCE );
            addEvaluator( ValueType.BIG_DECIMAL_TYPE,                    NOT_MEMBEROF,                      BigDecimalNotMemberOfEvaluator.INSTANCE );
            addEvaluator( ValueType.BIG_INTEGER_TYPE,                    MEMBEROF,                          BigIntegerMemberOfEvaluator.INSTANCE );
            addEvaluator( ValueType.BIG_INTEGER_TYPE,                    NOT_MEMBEROF,                      BigIntegerNotMemberOfEvaluator.INSTANCE );
            addEvaluator( ValueType.BOOLEAN_TYPE,                        MEMBEROF,                          BooleanMemberOfEvaluator.INSTANCE );
            addEvaluator( ValueType.BOOLEAN_TYPE,                        NOT_MEMBEROF,                      BooleanNotMemberOfEvaluator.INSTANCE );
            addEvaluator( ValueType.PBOOLEAN_TYPE,                       MEMBEROF,                          BooleanMemberOfEvaluator.INSTANCE );
            addEvaluator( ValueType.PBOOLEAN_TYPE,                       NOT_MEMBEROF,                      BooleanNotMemberOfEvaluator.INSTANCE );
            addEvaluator( ValueType.BYTE_TYPE,                           MEMBEROF,                          ByteMemberOfEvaluator.INSTANCE );
            addEvaluator( ValueType.BYTE_TYPE,                           NOT_MEMBEROF,                      ByteNotMemberOfEvaluator.INSTANCE );
            addEvaluator( ValueType.PBYTE_TYPE,                          MEMBEROF,                          ByteMemberOfEvaluator.INSTANCE );
            addEvaluator( ValueType.PBYTE_TYPE,                          NOT_MEMBEROF,                      ByteNotMemberOfEvaluator.INSTANCE );
            addEvaluator( ValueType.CHAR_TYPE,                           MEMBEROF,                          CharacterMemberOfEvaluator.INSTANCE );
            addEvaluator( ValueType.CHAR_TYPE,                           NOT_MEMBEROF,                      CharacterNotMemberOfEvaluator.INSTANCE );
            addEvaluator( ValueType.PCHAR_TYPE,                          MEMBEROF,                          CharacterMemberOfEvaluator.INSTANCE );
            addEvaluator( ValueType.PCHAR_TYPE,                          NOT_MEMBEROF,                      CharacterNotMemberOfEvaluator.INSTANCE );
            addEvaluator( ValueType.DATE_TYPE,                           MEMBEROF,                          DateMemberOfEvaluator.INSTANCE );
            addEvaluator( ValueType.DATE_TYPE,                           NOT_MEMBEROF,                      DateNotMemberOfEvaluator.INSTANCE );
            addEvaluator( ValueType.DOUBLE_TYPE,                         MEMBEROF,                          DoubleMemberOfEvaluator.INSTANCE );
            addEvaluator( ValueType.DOUBLE_TYPE,                         NOT_MEMBEROF,                      DoubleNotMemberOfEvaluator.INSTANCE );
            addEvaluator( ValueType.PDOUBLE_TYPE,                        MEMBEROF,                          DoubleMemberOfEvaluator.INSTANCE );
            addEvaluator( ValueType.PDOUBLE_TYPE,                        NOT_MEMBEROF,                      DoubleNotMemberOfEvaluator.INSTANCE );
            addEvaluator( ValueType.FLOAT_TYPE,                          MEMBEROF,                          FloatMemberOfEvaluator.INSTANCE );
            addEvaluator( ValueType.FLOAT_TYPE,                          NOT_MEMBEROF,                      FloatNotMemberOfEvaluator.INSTANCE );
            addEvaluator( ValueType.PFLOAT_TYPE,                         MEMBEROF,                          FloatMemberOfEvaluator.INSTANCE );
            addEvaluator( ValueType.PFLOAT_TYPE,                         NOT_MEMBEROF,                      FloatNotMemberOfEvaluator.INSTANCE );
            addEvaluator( ValueType.INTEGER_TYPE,                        MEMBEROF,                          IntegerMemberOfEvaluator.INSTANCE );
            addEvaluator( ValueType.INTEGER_TYPE,                        NOT_MEMBEROF,                      IntegerNotMemberOfEvaluator.INSTANCE );
            addEvaluator( ValueType.PINTEGER_TYPE,                       MEMBEROF,                          IntegerMemberOfEvaluator.INSTANCE );
            addEvaluator( ValueType.PINTEGER_TYPE,                       NOT_MEMBEROF,                      IntegerNotMemberOfEvaluator.INSTANCE );
            addEvaluator( ValueType.LONG_TYPE,                           MEMBEROF,                          LongMemberOfEvaluator.INSTANCE );
            addEvaluator( ValueType.LONG_TYPE,                           NOT_MEMBEROF,                      LongNotMemberOfEvaluator.INSTANCE );
            addEvaluator( ValueType.PLONG_TYPE,                          MEMBEROF,                          LongMemberOfEvaluator.INSTANCE );
            addEvaluator( ValueType.PLONG_TYPE,                          NOT_MEMBEROF,                      LongNotMemberOfEvaluator.INSTANCE );
            addEvaluator( ValueType.OBJECT_TYPE,                         CONTAINS,                          ObjectContainsEvaluator.INSTANCE );
            addEvaluator( ValueType.OBJECT_TYPE,                         NOT_CONTAINS,                      ObjectExcludesEvaluator.INSTANCE );
            addEvaluator( ValueType.OBJECT_TYPE,                         EXCLUDES,                          ObjectExcludesEvaluator.INSTANCE );
            addEvaluator( ValueType.OBJECT_TYPE,                         NOT_EXCLUDES,                      ObjectContainsEvaluator.INSTANCE );
            addEvaluator( ValueType.OBJECT_TYPE,                         MEMBEROF,                          ObjectMemberOfEvaluator.INSTANCE );
            addEvaluator( ValueType.OBJECT_TYPE,                         NOT_MEMBEROF,                      ObjectNotMemberOfEvaluator.INSTANCE );
            addEvaluator( ValueType.SHORT_TYPE,                          MEMBEROF,                          ShortMemberOfEvaluator.INSTANCE );
            addEvaluator( ValueType.SHORT_TYPE,                          NOT_MEMBEROF,                      ShortNotMemberOfEvaluator.INSTANCE );
            addEvaluator( ValueType.PSHORT_TYPE,                         MEMBEROF,                          ShortMemberOfEvaluator.INSTANCE );
            addEvaluator( ValueType.PSHORT_TYPE,                         NOT_MEMBEROF,                      ShortNotMemberOfEvaluator.INSTANCE );
            addEvaluator( ValueType.STRING_TYPE,                         MEMBEROF,                          StringMemberOfEvaluator.INSTANCE );
            addEvaluator( ValueType.STRING_TYPE,                         NOT_MEMBEROF,                      StringNotMemberOfEvaluator.INSTANCE );
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
        return true;
    }

    public boolean operatesOnFactHandles() {
        return false;
    }

    public boolean supportsType(ValueType type) {
        return this.evaluators.supportsType( type );
    }

    /*  *********************************************************
     *                Evaluator Implementations
     *  ********************************************************* 
     */
    static class ArrayContainsEvaluator extends BaseEvaluator {

        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new ArrayContainsEvaluator();

        private ArrayContainsEvaluator() {
            super( ValueType.ARRAY_TYPE,
                   CONTAINS );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            final Object value = object2.getValue();
            final Object[] array = (Object[]) extractor.getValue( workingMemory,
                                                                  object1 );
            if ( array == null ) return false;
            return ShadowProxyUtils.contains( array,
                                              value );
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context,
                                           final Object left) {
            final Object value = context.declaration.getExtractor().getValue( workingMemory,
                                                                              left );
            final Object[] array = (Object[]) ((ObjectVariableContextEntry) context).right;
            if ( array == null ) return false;
            return ShadowProxyUtils.contains( array,
                                              value );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context,
                                          final Object right) {
            final Object value = ((ObjectVariableContextEntry) context).left;
            final Object[] array = (Object[]) context.extractor.getValue( workingMemory,
                                                                          right );
            if ( array == null ) return false;
            return ShadowProxyUtils.contains( array,
                                              value );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2,
                                final Object object2) {
            final Object value = extractor2.getValue( workingMemory,
                                                      object2 );
            final Object[] array = (Object[]) extractor1.getValue( workingMemory,
                                                                   object1 );

            if ( array == null ) return false;
            return ShadowProxyUtils.contains( array,
                                              value );
        }

        public String toString() {
            return "Array contains";
        }
    }

    static class ArrayExcludesEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new ArrayExcludesEvaluator();

        private ArrayExcludesEvaluator() {
            super( ValueType.ARRAY_TYPE,
                   EXCLUDES );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            final Object value = object2.getValue();
            final Object[] array = (Object[]) extractor.getValue( workingMemory,
                                                                  object1 );
            if ( array == null ) return true;
            return !ShadowProxyUtils.contains( array,
                                               value );
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context,
                                           final Object left) {
            final Object value = context.declaration.getExtractor().getValue( workingMemory,
                                                                              left );
            final Object[] array = (Object[]) ((ObjectVariableContextEntry) context).right;
            if ( array == null ) return true;
            return !ShadowProxyUtils.contains( array,
                                               value );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context,
                                          final Object right) {
            final Object value = ((ObjectVariableContextEntry) context).left;
            final Object[] array = (Object[]) context.extractor.getValue( workingMemory,
                                                                          right );
            if ( array == null ) return true;
            return !ShadowProxyUtils.contains( array,
                                               value );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2,
                                final Object object2) {
            final Object value = extractor2.getValue( workingMemory,
                                                      object2 );
            final Object[] array = (Object[]) extractor1.getValue( workingMemory,
                                                                   object1 );

            if ( array == null ) return true;
            return !ShadowProxyUtils.contains( array,
                                               value );
        }

        public String toString() {
            return "Array excludes";
        }
    }

    static class ArrayMemberOfEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new ArrayMemberOfEvaluator();

        private ArrayMemberOfEvaluator() {
            super( ValueType.ARRAY_TYPE,
                   MEMBEROF );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            final Object[] array = (Object[]) object2.getValue();
            final Object value = extractor.getValue( workingMemory,
                                                     object1 );
            if ( array == null ) return false;
            return ShadowProxyUtils.contains( array,
                                              value );
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context,
                                           final Object left) {
            final Object[] array = (Object[]) context.declaration.getExtractor().getValue( workingMemory,
                                                                                           left );
            final Object value = ((ObjectVariableContextEntry) context).right;
            if ( array == null ) return false;
            return ShadowProxyUtils.contains( array,
                                              value );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context,
                                          final Object right) {
            final Object[] array = (Object[]) ((ObjectVariableContextEntry) context).left;
            final Object value = context.extractor.getValue( workingMemory,
                                                             right );
            if ( array == null ) return false;
            return ShadowProxyUtils.contains( array,
                                              value );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2,
                                final Object object2) {
            final Object[] array = (Object[]) extractor2.getValue( workingMemory,
                                                                   object2 );
            final Object value = extractor1.getValue( workingMemory,
                                                      object1 );

            if ( array == null ) return false;
            return ShadowProxyUtils.contains( array,
                                              value );
        }

        public String toString() {
            return "Array memberOf";
        }
    }

    static class ArrayNotMemberOfEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new ArrayNotMemberOfEvaluator();

        private ArrayNotMemberOfEvaluator() {
            super( ValueType.ARRAY_TYPE,
                   NOT_MEMBEROF );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            final Object[] array = (Object[]) object2.getValue();
            final Object value = extractor.getValue( workingMemory,
                                                     object1 );
            if ( array == null ) return true;
            return !ShadowProxyUtils.contains( array,
                                               value );
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context,
                                           final Object left) {
            final Object[] array = (Object[]) context.declaration.getExtractor().getValue( workingMemory,
                                                                                           left );
            final Object value = ((ObjectVariableContextEntry) context).right;
            if ( array == null ) return true;
            return !ShadowProxyUtils.contains( array,
                                               value );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context,
                                          final Object right) {
            final Object[] array = (Object[]) ((ObjectVariableContextEntry) context).left;
            final Object value = context.extractor.getValue( workingMemory,
                                                             right );
            if ( array == null ) return true;
            return !ShadowProxyUtils.contains( array,
                                               value );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2,
                                final Object object2) {
            final Object[] array = (Object[]) extractor2.getValue( workingMemory,
                                                                   object2 );
            final Object value = extractor1.getValue( workingMemory,
                                                      object1 );

            if ( array == null ) return true;
            return !ShadowProxyUtils.contains( array,
                                               value );
        }

        public String toString() {
            return "Array not memberOf";
        }
    }

    static abstract class BaseMemberOfEvaluator extends BaseEvaluator {

        private static final long serialVersionUID = 2017803222427893249L;

        public BaseMemberOfEvaluator(ValueType type,
                                     Operator operator) {
            super( type,
                   operator );
        }

        public ValueType getCoercedValueType() {
            // during evaluation, always coerce to object
            return ValueType.OBJECT_TYPE;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            if ( object2.isNull() ) {
                return false;
            } else if ( !object2.isCollectionField() ) {
                throw new ClassCastException( "Can't check if an attribute is member of an object of class " + object2.getValue().getClass() );
            }
            final Collection col = (Collection) object2.getValue();
            final Object value = extractor.getValue( workingMemory,
                                                     object1 );
            return ShadowProxyUtils.contains( col,
                                              value );
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context,
                                           final Object left) {
            final Object object = context.declaration.getExtractor().getValue( workingMemory,
                                                                               left );
            if ( object == null ) {
                return false;
            } else if ( !(object instanceof Collection) ) {
                throw new ClassCastException( "Can't check if an attribute is member of an object of class " + object.getClass() );
            }
            final Collection col = (Collection) object;
            final Object value = ((ObjectVariableContextEntry) context).right;
            return ShadowProxyUtils.contains( col,
                                              value );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context,
                                          final Object right) {
            final Object object = ((ObjectVariableContextEntry) context).left;
            if ( object == null ) {
                return false;
            } else if ( !(object instanceof Collection) ) {
                throw new ClassCastException( "Can't check if an attribute is member of an object of class " + object.getClass() );
            }
            final Collection col = (Collection) object;
            final Object value = context.extractor.getValue( workingMemory,
                                                             right );
            return ShadowProxyUtils.contains( col,
                                              value );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2,
                                final Object object2) {
            final Object object = extractor2.getValue( workingMemory,
                                                       object2 );
            if ( object == null ) {
                return false;
            } else if ( !(object instanceof Collection) ) {
                throw new ClassCastException( "Can't check if an attribute is member of an object of class " + object.getClass() );
            }
            final Collection col = (Collection) object;
            final Object value = extractor1.getValue( workingMemory,
                                                      object1 );
            return ShadowProxyUtils.contains( col,
                                              value );
        }

        public abstract String toString();

    }

    static abstract class BaseNotMemberOfEvaluator extends BaseEvaluator {

        private static final long serialVersionUID = -8730331781980886901L;

        public BaseNotMemberOfEvaluator(ValueType type,
                                        Operator operator) {
            super( type,
                   operator );
        }

        public ValueType getCoercedValueType() {
            // during evaluation, always coerce to object
            return ValueType.OBJECT_TYPE;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            if ( object2.isNull() ) {
                return false;
            } else if ( !object2.isCollectionField() ) {
                throw new ClassCastException( "Can't check if an attribute is not member of an object of class " + object2.getValue().getClass() );
            }
            final Collection col = (Collection) object2.getValue();
            final Object value = extractor.getValue( workingMemory,
                                                     object1 );
            return !ShadowProxyUtils.contains( col,
                                               value );
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context,
                                           final Object left) {
            final Object object = context.declaration.getExtractor().getValue( workingMemory,
                                                                               left );
            if ( object == null ) {
                return false;
            } else if ( !(object instanceof Collection) ) {
                throw new ClassCastException( "Can't check if an attribute is not member of an object of class " + object.getClass() );
            }
            final Collection col = (Collection) object;
            final Object value = ((ObjectVariableContextEntry) context).right;
            return !ShadowProxyUtils.contains( col,
                                               value );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context,
                                          final Object right) {
            final Object object = ((ObjectVariableContextEntry) context).left;
            if ( object == null ) {
                return false;
            } else if ( !(object instanceof Collection) ) {
                throw new ClassCastException( "Can't check if an attribute is not member of an object of class " + object.getClass() );
            }
            final Collection col = (Collection) object;
            final Object value = context.extractor.getValue( workingMemory,
                                                             right );
            return !ShadowProxyUtils.contains( col,
                                               value );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2,
                                final Object object2) {
            final Object object = extractor2.getValue( workingMemory,
                                                       object2 );
            if ( object == null ) {
                return false;
            } else if ( !(object instanceof Collection) ) {
                throw new ClassCastException( "Can't check if an attribute is not member of an object of class " + object.getClass() );
            }
            final Collection col = (Collection) object;
            final Object value = extractor1.getValue( workingMemory,
                                                      object1 );
            return !ShadowProxyUtils.contains( col,
                                               value );
        }

        public abstract String toString();
    }

    static class BigDecimalMemberOfEvaluator extends BaseMemberOfEvaluator {

        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new BigDecimalMemberOfEvaluator();

        private BigDecimalMemberOfEvaluator() {
            super( ValueType.BIG_DECIMAL_TYPE,
                   MEMBEROF );
        }

        public String toString() {
            return "BigDecimal memberOf";
        }
    }

    static class BigDecimalNotMemberOfEvaluator extends BaseNotMemberOfEvaluator {

        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new BigDecimalNotMemberOfEvaluator();

        private BigDecimalNotMemberOfEvaluator() {
            super( ValueType.BIG_DECIMAL_TYPE,
                   NOT_MEMBEROF );
        }

        public String toString() {
            return "BigDecimal not memberOf";
        }
    }

    static class BigIntegerMemberOfEvaluator extends BaseMemberOfEvaluator {

        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new BigIntegerMemberOfEvaluator();

        private BigIntegerMemberOfEvaluator() {
            super( ValueType.BIG_INTEGER_TYPE,
                   MEMBEROF );
        }

        public String toString() {
            return "BigInteger memberOf";
        }
    }

    static class BigIntegerNotMemberOfEvaluator extends BaseNotMemberOfEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new BigIntegerNotMemberOfEvaluator();

        private BigIntegerNotMemberOfEvaluator() {
            super( ValueType.BIG_INTEGER_TYPE,
                   NOT_MEMBEROF );
        }

        public String toString() {
            return "BigInteger not memberOf";
        }
    }

    static class BooleanMemberOfEvaluator extends BaseMemberOfEvaluator {

        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new BooleanMemberOfEvaluator();

        private BooleanMemberOfEvaluator() {
            super( ValueType.PBOOLEAN_TYPE,
                   MEMBEROF );
        }

        public String toString() {
            return "Boolean memberOf";
        }
    }

    static class BooleanNotMemberOfEvaluator extends BaseNotMemberOfEvaluator {

        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new BooleanNotMemberOfEvaluator();

        private BooleanNotMemberOfEvaluator() {
            super( ValueType.PBOOLEAN_TYPE,
                   NOT_MEMBEROF );
        }

        public String toString() {
            return "Boolean not memberOf";
        }
    }

    static class ByteMemberOfEvaluator extends BaseMemberOfEvaluator {

        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new ByteMemberOfEvaluator();

        private ByteMemberOfEvaluator() {
            super( ValueType.PBYTE_TYPE,
                   MEMBEROF );
        }

        public String toString() {
            return "Byte memberOf";
        }
    }

    static class ByteNotMemberOfEvaluator extends BaseNotMemberOfEvaluator {

        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new ByteNotMemberOfEvaluator();

        private ByteNotMemberOfEvaluator() {
            super( ValueType.PBYTE_TYPE,
                   NOT_MEMBEROF );
        }

        public String toString() {
            return "Byte not memberOf";
        }
    }

    static class CharacterMemberOfEvaluator extends BaseMemberOfEvaluator {

        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new CharacterMemberOfEvaluator();

        private CharacterMemberOfEvaluator() {
            super( ValueType.PCHAR_TYPE,
                   MEMBEROF );
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
                   NOT_MEMBEROF );
        }

        public String toString() {
            return "Character not memberOf";
        }
    }

    static class DateMemberOfEvaluator extends BaseMemberOfEvaluator {

        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new DateMemberOfEvaluator();

        private DateMemberOfEvaluator() {
            super( ValueType.DATE_TYPE,
                   MEMBEROF );
        }

        public String toString() {
            return "Date memberOf";
        }
    }

    static class DateNotMemberOfEvaluator extends BaseNotMemberOfEvaluator {

        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new DateNotMemberOfEvaluator();

        private DateNotMemberOfEvaluator() {
            super( ValueType.DATE_TYPE,
                   NOT_MEMBEROF );
        }

        public String toString() {
            return "Date not memberOf";
        }
    }

    static class DoubleMemberOfEvaluator extends BaseMemberOfEvaluator {

        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new DoubleMemberOfEvaluator();

        private DoubleMemberOfEvaluator() {
            super( ValueType.PDOUBLE_TYPE,
                   MEMBEROF );
        }

        public String toString() {
            return "Double memberOf";
        }
    }

    static class DoubleNotMemberOfEvaluator extends BaseNotMemberOfEvaluator {

        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new DoubleNotMemberOfEvaluator();

        private DoubleNotMemberOfEvaluator() {
            super( ValueType.PDOUBLE_TYPE,
                   NOT_MEMBEROF );
        }

        public String toString() {
            return "Double not memberOf";
        }
    }

    static class FloatMemberOfEvaluator extends BaseMemberOfEvaluator {

        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new FloatMemberOfEvaluator();

        private FloatMemberOfEvaluator() {
            super( ValueType.PFLOAT_TYPE,
                   MEMBEROF );
        }

        public String toString() {
            return "Float memberOf";
        }
    }

    static class FloatNotMemberOfEvaluator extends BaseNotMemberOfEvaluator {

        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new FloatNotMemberOfEvaluator();

        private FloatNotMemberOfEvaluator() {
            super( ValueType.PFLOAT_TYPE,
                   NOT_MEMBEROF );
        }

        public String toString() {
            return "Float not memberOf";
        }
    }

    static class IntegerMemberOfEvaluator extends BaseMemberOfEvaluator {

        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new IntegerMemberOfEvaluator();

        private IntegerMemberOfEvaluator() {
            super( ValueType.PINTEGER_TYPE,
                   MEMBEROF );
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
                   NOT_MEMBEROF );
        }

        public String toString() {
            return "Integer not memberOf";
        }
    }

    static class LongMemberOfEvaluator extends BaseMemberOfEvaluator {

        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new LongMemberOfEvaluator();

        private LongMemberOfEvaluator() {
            super( ValueType.PLONG_TYPE,
                   MEMBEROF );
        }

        public String toString() {
            return "Long memberOf";
        }
    }

    static class LongNotMemberOfEvaluator extends BaseNotMemberOfEvaluator {

        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new LongNotMemberOfEvaluator();

        private LongNotMemberOfEvaluator() {
            super( ValueType.PLONG_TYPE,
                   NOT_MEMBEROF );
        }

        public String toString() {
            return "Long not memberOf";
        }
    }

    static class ObjectContainsEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new ObjectContainsEvaluator();

        private ObjectContainsEvaluator() {
            super( ValueType.OBJECT_TYPE,
                   CONTAINS );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            final Object value = object2.getValue();
            final Collection col = (Collection) extractor.getValue( workingMemory,
                                                                    object1 );
            return (col == null) ? false : ShadowProxyUtils.contains( col,
                                                                      value );
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context,
                                           final Object left) {
            final Object value = context.declaration.getExtractor().getValue( workingMemory,
                                                                              left );
            final Collection col = (Collection) ((ObjectVariableContextEntry) context).right;
            return (col == null) ? false : ShadowProxyUtils.contains( col,
                                                                      value );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context,
                                          final Object right) {
            final Object value = ((ObjectVariableContextEntry) context).left;
            final Collection col = (Collection) context.extractor.getValue( workingMemory,
                                                                            right );
            return (col == null) ? false : ShadowProxyUtils.contains( col,
                                                                      value );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2,
                                final Object object2) {
            final Object value = extractor2.getValue( workingMemory,
                                                      object2 );
            final Collection col = (Collection) extractor1.getValue( workingMemory,
                                                                     object1 );
            return (col == null) ? false : ShadowProxyUtils.contains( col,
                                                                      value );
        }

        public String toString() {
            return "Object contains";
        }
    }

    static class ObjectExcludesEvaluator extends BaseEvaluator {
        /**
         * 
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new ObjectExcludesEvaluator();

        private ObjectExcludesEvaluator() {
            super( ValueType.OBJECT_TYPE,
                   EXCLUDES );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor,
                                final Object object1,
                                final FieldValue object2) {
            final Object value = object2.getValue();
            final Collection col = (Collection) extractor.getValue( workingMemory,
                                                                    object1 );
            return (col == null) ? true : !ShadowProxyUtils.contains( col,
                                                                      value );
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context,
                                           final Object left) {
            final Object value = context.declaration.getExtractor().getValue( workingMemory,
                                                                              left );
            final Collection col = (Collection) ((ObjectVariableContextEntry) context).right;
            return (col == null) ? true : !ShadowProxyUtils.contains( col,
                                                                      value );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context,
                                          final Object right) {
            final Object value = ((ObjectVariableContextEntry) context).left;
            final Collection col = (Collection) context.extractor.getValue( workingMemory,
                                                                            right );
            return (col == null) ? true : !ShadowProxyUtils.contains( col,
                                                                      value );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final Extractor extractor1,
                                final Object object1,
                                final Extractor extractor2,
                                final Object object2) {
            final Object value = extractor2.getValue( workingMemory,
                                                      object2 );
            final Collection col = (Collection) extractor1.getValue( workingMemory,
                                                                     object1 );
            return (col == null) ? true : !ShadowProxyUtils.contains( col,
                                                                      value );
        }

        public String toString() {
            return "Object excludes";
        }
    }

    static class ObjectMemberOfEvaluator extends BaseMemberOfEvaluator {

        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new ObjectMemberOfEvaluator();

        private ObjectMemberOfEvaluator() {
            super( ValueType.OBJECT_TYPE,
                   MEMBEROF );
        }

        public String toString() {
            return "Object memberOf";
        }
    }

    static class ObjectNotMemberOfEvaluator extends BaseNotMemberOfEvaluator {

        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new ObjectNotMemberOfEvaluator();

        private ObjectNotMemberOfEvaluator() {
            super( ValueType.OBJECT_TYPE,
                   NOT_MEMBEROF );
        }

        public String toString() {
            return "Object not memberOf";
        }
    }

    static class ShortMemberOfEvaluator extends BaseMemberOfEvaluator {

        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new ShortMemberOfEvaluator();

        private ShortMemberOfEvaluator() {
            super( ValueType.PSHORT_TYPE,
                   MEMBEROF );
        }

        public String toString() {
            return "Short memberOf";
        }
    }

    static class ShortNotMemberOfEvaluator extends BaseNotMemberOfEvaluator {

        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new ShortNotMemberOfEvaluator();

        private ShortNotMemberOfEvaluator() {
            super( ValueType.PSHORT_TYPE,
                   NOT_MEMBEROF );
        }

        public String toString() {
            return "Short not memberOf";
        }
    }

    static class StringMemberOfEvaluator extends BaseMemberOfEvaluator {

        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new StringMemberOfEvaluator();

        private StringMemberOfEvaluator() {
            super( ValueType.STRING_TYPE,
                   MEMBEROF );
        }

        public String toString() {
            return "String memberOf";
        }
    }

    static class StringNotMemberOfEvaluator extends BaseNotMemberOfEvaluator {

        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new StringNotMemberOfEvaluator();

        private StringNotMemberOfEvaluator() {
            super( ValueType.STRING_TYPE,
                   NOT_MEMBEROF );
        }

        public String toString() {
            return "String not memberOf";
        }
    }
}
