/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.util.index;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.base.ValueType;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.NodeTypeEnums;
import org.drools.core.rule.ContextEntry;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.IndexableConstraint;
import org.drools.core.spi.BetaNodeFieldConstraint;
import org.drools.core.spi.Constraint;
import org.drools.core.spi.FieldValue;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.spi.Tuple;
import org.drools.core.spi.TupleValueExtractor;
import org.drools.core.util.AbstractHashTable.DoubleCompositeIndex;
import org.drools.core.util.AbstractHashTable.FieldIndex;
import org.drools.core.util.AbstractHashTable.Index;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IndexUtilTest {

    @Test
    public void isEqualIndexable() {
        FakeBetaNodeFieldConstraint intEqualsConstraint = new FakeBetaNodeFieldConstraint(IndexUtil.ConstraintType.EQUAL, new FakeInternalReadAccessor(ValueType.PINTEGER_TYPE));
        assertThat(IndexUtil.isEqualIndexable(intEqualsConstraint)).isTrue();

        FakeBetaNodeFieldConstraint intLessThanConstraint = new FakeBetaNodeFieldConstraint(IndexUtil.ConstraintType.LESS_THAN, new FakeInternalReadAccessor(ValueType.PINTEGER_TYPE));
        assertThat(IndexUtil.isEqualIndexable(intLessThanConstraint)).isFalse();

        FakeBetaNodeFieldConstraint bigDecimalEqualsConstraint = new FakeBetaNodeFieldConstraint(IndexUtil.ConstraintType.EQUAL, new FakeInternalReadAccessor(ValueType.BIG_DECIMAL_TYPE));
        assertThat(IndexUtil.isEqualIndexable(bigDecimalEqualsConstraint)).as("BigDecimal equality cannot be indexed because of scale").isFalse();
    }

    @Test
    public void createBetaMemoryWithIntEquals_shouldBeTupleIndexHashTable() {
        RuleBaseConfiguration config = new RuleBaseConfiguration();
        FakeBetaNodeFieldConstraint intEqualsConstraint = new FakeBetaNodeFieldConstraint(IndexUtil.ConstraintType.EQUAL, new FakeInternalReadAccessor(ValueType.PINTEGER_TYPE));
        BetaMemory betaMemory = IndexUtil.Factory.createBetaMemory(config, NodeTypeEnums.JoinNode, intEqualsConstraint);
        assertThat(betaMemory.getLeftTupleMemory()).isInstanceOf(TupleIndexHashTable.class);
        assertThat(betaMemory.getRightTupleMemory()).isInstanceOf(TupleIndexHashTable.class);
    }

    @Test
    public void createBetaMemoryWithBigDecimalEquals_shouldNotBeTupleIndexHashTable() {
        RuleBaseConfiguration config = new RuleBaseConfiguration();
        FakeBetaNodeFieldConstraint bigDecimalEqualsConstraint = new FakeBetaNodeFieldConstraint(IndexUtil.ConstraintType.EQUAL, new FakeInternalReadAccessor(ValueType.BIG_DECIMAL_TYPE));
        BetaMemory betaMemory = IndexUtil.Factory.createBetaMemory(config, NodeTypeEnums.JoinNode, bigDecimalEqualsConstraint);
        assertThat(betaMemory.getLeftTupleMemory()).isInstanceOf(TupleList.class);
        assertThat(betaMemory.getRightTupleMemory()).isInstanceOf(TupleList.class);
    }

    @Test
    public void createBetaMemoryWithBigDecimalEqualsAndOtherIndexableConstraints_shouldBeTupleIndexHashTableButBigDecimalIsNotIndexed() {
        RuleBaseConfiguration config = new RuleBaseConfiguration();
        FakeBetaNodeFieldConstraint intEqualsConstraint = new FakeBetaNodeFieldConstraint(IndexUtil.ConstraintType.EQUAL, new FakeInternalReadAccessor(ValueType.PINTEGER_TYPE));
        FakeBetaNodeFieldConstraint bigDecimalEqualsConstraint = new FakeBetaNodeFieldConstraint(IndexUtil.ConstraintType.EQUAL, new FakeInternalReadAccessor(ValueType.BIG_DECIMAL_TYPE));
        FakeBetaNodeFieldConstraint stringEqualsConstraint = new FakeBetaNodeFieldConstraint(IndexUtil.ConstraintType.EQUAL, new FakeInternalReadAccessor(ValueType.STRING_TYPE));
        BetaMemory betaMemory = IndexUtil.Factory.createBetaMemory(config, NodeTypeEnums.JoinNode, intEqualsConstraint, bigDecimalEqualsConstraint, stringEqualsConstraint);

        // BigDecimal is not included in Indexes
        assertThat(betaMemory.getLeftTupleMemory()).isInstanceOf(TupleIndexHashTable.class);
        Index leftIndex = ((TupleIndexHashTable) betaMemory.getLeftTupleMemory()).getIndex();
        assertThat(leftIndex).isInstanceOf(DoubleCompositeIndex.class);
        FieldIndex leftFieldIndex0 = leftIndex.getFieldIndex(0);
        assertThat(leftFieldIndex0.getLeftExtractor().getValueType()).isEqualTo(ValueType.PINTEGER_TYPE);
        FieldIndex leftFieldIndex1 = leftIndex.getFieldIndex(1);
        assertThat(leftFieldIndex1.getLeftExtractor().getValueType()).isEqualTo(ValueType.STRING_TYPE);

        assertThat(betaMemory.getRightTupleMemory()).isInstanceOf(TupleIndexHashTable.class);
        Index rightIndex = ((TupleIndexHashTable) betaMemory.getRightTupleMemory()).getIndex();
        assertThat(rightIndex).isInstanceOf(DoubleCompositeIndex.class);
        FieldIndex rightFieldIndex0 = rightIndex.getFieldIndex(0);
        assertThat(rightFieldIndex0.getRightExtractor().getValueType()).isEqualTo(ValueType.PINTEGER_TYPE);
        FieldIndex rightFieldIndex1 = rightIndex.getFieldIndex(1);
        assertThat(rightFieldIndex1.getRightExtractor().getValueType()).isEqualTo(ValueType.STRING_TYPE);
    }

    static class FakeBetaNodeFieldConstraint implements BetaNodeFieldConstraint,
                                                        IndexableConstraint {

        private IndexUtil.ConstraintType constraintType;
        private InternalReadAccessor fieldExtractor;

        public FakeBetaNodeFieldConstraint() {
        }

        public FakeBetaNodeFieldConstraint(IndexUtil.ConstraintType constraintType, InternalReadAccessor fieldExtractor) {
            this.constraintType = constraintType;
            this.fieldExtractor = fieldExtractor;
        }

        @Override
        public Declaration[] getRequiredDeclarations() {
            return null;
        }

        @Override
        public void replaceDeclaration(Declaration oldDecl, Declaration newDecl) {
        }

        @Override
        public Constraint clone() {
            return null;
        }

        @Override
        public ConstraintType getType() {
            return null;
        }

        @Override
        public boolean isTemporal() {
            return false;
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        }

        @Override
        public boolean isAllowedCachedLeft(ContextEntry context, InternalFactHandle handle) {
            return false;
        }

        @Override
        public boolean isAllowedCachedRight(Tuple tuple, ContextEntry context) {
            return false;
        }

        @Override
        public ContextEntry createContextEntry() {
            return null;
        }

        @Override
        public BetaNodeFieldConstraint cloneIfInUse() {
            return null;
        }

        @Override
        public boolean isUnification() {
            return false;
        }

        @Override
        public boolean isIndexable(short nodeType, RuleBaseConfiguration config) {
            return false;
        }

        @Override
        public org.drools.core.util.index.IndexUtil.ConstraintType getConstraintType() {
            return constraintType;
        }

        @Override
        public FieldValue getField() {
            return null;
        }

        @Override
        public FieldIndex getFieldIndex() {
            return new FieldIndex(fieldExtractor, new Declaration("$p1", fieldExtractor, null));
        }

        @Override
        public InternalReadAccessor getFieldExtractor() {
            return fieldExtractor;
        }

        @Override
        public TupleValueExtractor getIndexExtractor() {
            return null;
        }
    }

    static class FakeInternalReadAccessor implements InternalReadAccessor {

        private final ValueType valueType;

        private FakeInternalReadAccessor(ValueType valueType) {
            this.valueType = valueType;
        }

        @Override
        public Object getValue(Object object) {
            return null;
        }

        @Override
        public BigDecimal getBigDecimalValue(Object object) {
            return null;
        }

        @Override
        public BigInteger getBigIntegerValue(Object object) {
            return null;
        }

        @Override
        public char getCharValue(Object object) {
            return 0;
        }

        @Override
        public int getIntValue(Object object) {
            return 0;
        }

        @Override
        public byte getByteValue(Object object) {
            return 0;
        }

        @Override
        public short getShortValue(Object object) {
            return 0;
        }

        @Override
        public long getLongValue(Object object) {
            return 0;
        }

        @Override
        public float getFloatValue(Object object) {
            return 0;
        }

        @Override
        public double getDoubleValue(Object object) {
            return 0;
        }

        @Override
        public boolean getBooleanValue(Object object) {
            return false;
        }

        @Override
        public boolean isNullValue(Object object) {
            return false;
        }

        @Override
        public ValueType getValueType() {
            return valueType;
        }

        @Override
        public Class<?> getExtractToClass() {
            return null;
        }

        @Override
        public String getExtractToClassName() {
            return null;
        }

        @Override
        public Method getNativeReadMethod() {
            return null;
        }

        @Override
        public String getNativeReadMethodName() {
            return null;
        }

        @Override
        public int getHashCode(Object object) {
            return 0;
        }

        @Override
        public int getIndex() {
            return 0;
        }

        @Override
        public Object getValue(InternalWorkingMemory workingMemory, Object object) {
            return null;
        }

        @Override
        public BigDecimal getBigDecimalValue(InternalWorkingMemory workingMemory, Object object) {
            return null;
        }

        @Override
        public BigInteger getBigIntegerValue(InternalWorkingMemory workingMemory, Object object) {
            return null;
        }

        @Override
        public char getCharValue(InternalWorkingMemory workingMemory, Object object) {
            return 0;
        }

        @Override
        public int getIntValue(InternalWorkingMemory workingMemory, Object object) {
            return 0;
        }

        @Override
        public byte getByteValue(InternalWorkingMemory workingMemory, Object object) {
            return 0;
        }

        @Override
        public short getShortValue(InternalWorkingMemory workingMemory, Object object) {
            return 0;
        }

        @Override
        public long getLongValue(InternalWorkingMemory workingMemory, Object object) {
            return 0;
        }

        @Override
        public float getFloatValue(InternalWorkingMemory workingMemory, Object object) {
            return 0;
        }

        @Override
        public double getDoubleValue(InternalWorkingMemory workingMemory, Object object) {
            return 0;
        }

        @Override
        public boolean getBooleanValue(InternalWorkingMemory workingMemory, Object object) {
            return false;
        }

        @Override
        public boolean isNullValue(InternalWorkingMemory workingMemory, Object object) {
            return false;
        }

        @Override
        public int getHashCode(InternalWorkingMemory workingMemory, Object object) {
            return 0;
        }

        @Override
        public boolean isGlobal() {
            return false;
        }

        @Override
        public boolean isSelfReference() {
            return false;
        }
    }
}
