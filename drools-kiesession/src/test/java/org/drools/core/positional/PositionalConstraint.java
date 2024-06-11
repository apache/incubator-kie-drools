/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.drools.core.positional;

import org.drools.base.base.ObjectType;
import org.drools.base.base.ValueResolver;
import org.drools.base.base.ValueType;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.MutableTypeConstraint;
import org.drools.base.rule.Pattern;
import org.drools.base.rule.accessor.FieldValue;
import org.drools.base.rule.accessor.ReadAccessor;
import org.drools.base.rule.accessor.TupleValueExtractor;
import org.drools.base.rule.constraint.BetaConstraint;
import org.drools.base.rule.IndexableConstraint;
import org.drools.base.rule.IntervalProviderConstraint;
import org.drools.base.time.Interval;
import org.drools.base.util.IndexedValueReader;
import org.drools.base.util.index.ConstraintTypeOperator;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.BetaConstraints;
import org.drools.core.positional.Functions.Function1;
import org.drools.core.positional.Functions.Function2;
import org.drools.core.positional.PositionalConstraint.PositionalContextEntry;
import org.drools.core.positional.Predicates.Predicate1;
import org.drools.core.positional.Predicates.Predicate2;
import org.drools.core.positional.Predicates.Predicate3;
import org.drools.core.positional.Predicates.Predicate4;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.Tuple;
import org.drools.core.reteoo.TupleMemory;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.util.index.IndexMemory;
import org.drools.core.util.index.IndexSpec;
import org.drools.core.util.index.TupleList;
import org.drools.util.bitmask.BitMask;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.runtime.rule.FactHandle;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PositionalConstraint extends MutableTypeConstraint<PositionalContextEntry> implements BetaConstraints<PositionalContextEntry>, IndexableConstraint, IntervalProviderConstraint  {

    protected final Declaration[] declarations;
    private final Pattern pattern;

    private ConstraintTypeOperator operatorType;

    private IndexSpec indexSpec;

    private Predicate1<Object>       p1 = (o) -> true;
    private Predicate2<Object, Object>    p2;
    private Predicate3<Object, Object, Object> p3;
    private Predicate4<Object, Object, Object, Object> p4;

    private int pIndex;

    public PositionalConstraint(Declaration[] declarations, Pattern pattern) {
        this.declarations = declarations;
        this.pattern      = pattern;
    }

    @Override
    public Declaration[] getRequiredDeclarations() {
        return declarations;
    }

    @Override
    public void replaceDeclaration(Declaration oldDecl, Declaration newDecl) {

    }

    public Pattern getPattern() {
        return pattern;
    }

    public IndexSpec getIndex() {
        return indexSpec;
    }

    public void setIndex(IndexSpec indexSpec) {
        this.indexSpec = indexSpec;
    }

    public int getPIndex() {
        return pIndex;
    }

    public <A> void setPredicate(Predicate1<A> p1) {
        this.p1 = (Predicate1<Object>) p1;
        pIndex = 1;
    }

    public <A, B> void setPredicate(Predicate2<A, B> p2) {
        this.p2 = (Predicate2<Object, Object>) p2;
        pIndex = 2;
    }

    public <A, B, C> void setPredicate(Predicate3<A, B, C> p3) {
        this.p3 = (Predicate3<Object, Object, Object>) p3;
        pIndex = 3;
    }

    public <A, B, C, D> void setPredicate(Predicate4<A, B, C, D> p4) {
        this.p4 = (Predicate4<Object, Object, Object, Object>) p4;
        pIndex = 4;
    }

    @Override
    public PositionalConstraint clone() {
        Declaration[]        clonedDeclrs = Arrays.stream(declarations).map(d -> d.clone()).collect(Collectors.toList()).toArray(new Declaration[0]);
        PositionalConstraint clone        = new PositionalConstraint(clonedDeclrs, pattern);
        clone.setType(getType());
        clone.pIndex = pIndex;
        clone.p1 = p1;
        clone.p2 = p2;
        clone.p3 = p3;
        clone.p4 = p4;

        Declaration[] clonedDeclarations = new Declaration[declarations.length];
        for (int i = 0; i < declarations.length; i++) {
            clonedDeclarations[i] = declarations[i].clone();
        }

        return clone;
    }

    @Override
    public boolean isTemporal() {
        return false;
    }

    @Override
    public PositionalContextEntry createContext() {
        return new PositionalContextEntry();
    }

    @Override
    public void updateFromTuple(PositionalContextEntry context, ValueResolver valueResolver, Tuple tuple) {
        context.tp = tuple;
    }

    @Override
    public void updateFromFactHandle(PositionalContextEntry context, ValueResolver valueResolver, FactHandle handle) {
        context.fh = handle;
    }

    @Override
    public boolean isAllowed(FactHandle handle, ValueResolver valueResolver) {
        return p1.test(handle.getObject());
    }

    @Override
    public boolean isAllowedCachedLeft(PositionalContextEntry context, FactHandle h) {
        return isAllowed(context.tp, h);
    }

    @Override
    public boolean isAllowedCachedRight(BaseTuple t, PositionalContextEntry context) {
        return isAllowed(t, context.fh);
    }

    public boolean isAllowed(BaseTuple t, FactHandle h) {
        switch (pIndex) {
            case 0: {
                return true;
            }
            case 2: {
                return p2.test(t.getFactHandle().getObject(), h.getObject());
            } case 3: {
                return p3.test(t.getParent().getFactHandle().getObject(),
                               t.getFactHandle().getObject(),
                               h.getObject());
            } case 4: {
                BaseTuple    v2 = t.getParent();
                return p4.test(v2.getParent(), v2, t, h.getObject());
            } default:
                throw new RuntimeException("No matching predicate on index: " + pIndex);
        }
    }

    @Override
    public BetaConstraint[] getConstraints() {
        return new BetaConstraint[] {this};
    }

    @Override
    public BetaConstraints getOriginalConstraint() {
        return this;
    }

    @Override
    public boolean isIndexed() {
        return p2 != null;
    }

    @Override
    public int getIndexCount() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public BetaMemory createBetaMemory(RuleBaseConfiguration config, int nodeType) {

        if (config.getCompositeKeyDepth() < 1) {
            return new BetaMemory(config.isSequential() ? null : new TupleList(),
                                  new TupleList(),
                                  createContext(),
                                  nodeType );
        }

        return new BetaMemory(createLeftMemory(config, indexSpec),
                              createRightMemory(config, indexSpec),
                              createContext(),
                              nodeType );
    }

    private static TupleMemory createRightMemory(RuleBaseConfiguration config, IndexSpec indexSpec) {
        if ( !config.isIndexRightBetaMemory() || indexSpec == null || !indexSpec.getConstraintType().isIndexable() || indexSpec.getIndexes().length == 0 ) {
            return new TupleList();
        }

        if (indexSpec.getConstraintType() == ConstraintTypeOperator.EQUAL) {
            return IndexMemory.createEqualityMemory(indexSpec, false);
        }

        if (indexSpec.getConstraintType().isComparison()) {
            return IndexMemory.createComparisonMemory(indexSpec, false);
        }

        return new TupleList();
    }

    private static TupleMemory createLeftMemory(RuleBaseConfiguration config, IndexSpec indexSpec) {
        if (config.isSequential()) {
            return null;
        }
        if ( !config.isIndexLeftBetaMemory() || indexSpec == null || !indexSpec.getConstraintType().isIndexable() || indexSpec.getIndexes().length == 0 ) {
            return new TupleList();
        }

        if (indexSpec.getConstraintType() == ConstraintTypeOperator.EQUAL) {
            return IndexMemory.createEqualityMemory(indexSpec, true);
        }

        if (indexSpec.getConstraintType().isComparison()) {
            return IndexMemory.createComparisonMemory(indexSpec, true);
        }

        return new TupleList();
    }

    @Override
    public void resetTuple(PositionalContextEntry context) {
        context.tp = null;
    }

    @Override
    public void resetFactHandle(PositionalContextEntry context) {
        context.fh = null;
    }

    @Override
    public void init(BuildContext context, int betaNodeType) {

    }

    @Override
    public void initIndexes(int depth, int betaNodeType, RuleBaseConfiguration config) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MutableTypeConstraint cloneIfInUse() {
        return super.cloneIfInUse();
    }



    @Override
    public boolean isLeftUpdateOptimizationAllowed() {
        return false;
    }

    @Override
    public void registerEvaluationContext(BuildContext buildContext) {

    }

    @Override
    public BitMask getListenedPropertyMask(Pattern pattern, ObjectType modifiedType, List settableProperties) {
        return null;
    }

    @Override
    public boolean isUnification() {
        return false;
    }

    @Override
    public boolean isIndexable(int nodeType, KieBaseConfiguration config) {
        return false;
    }

    @Override
    public ConstraintTypeOperator getConstraintType() {
        return operatorType;
    }

    public void setConstraintTypeOperator(ConstraintTypeOperator operatorType) {
        this.operatorType = operatorType;
    }

    @Override
    public FieldValue getField() {
        return null;
    }

    @Override
    public IndexedValueReader getFieldIndex() {
        return null;
    }

    @Override
    public ReadAccessor getFieldExtractor() {
        return null;
    }

    @Override
    public TupleValueExtractor getRightIndexExtractor() {
        return null;
    }

    @Override
    public TupleValueExtractor getLeftIndexExtractor() {
        return null;
    }

    @Override
    public Interval getInterval() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {

    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

    }

    public static class PositionalContextEntry {
        public Tuple      tp;
        public FactHandle fh;
    }

    public static class PositionalTupleValueExtractor1<A, B> implements TupleValueExtractor {
        private Function1<A, B> func1;

        private ValueType valueType;

        public PositionalTupleValueExtractor1(Function1<A, B> func1, ValueType valueType) {
            this.func1     = func1;
            this.valueType = valueType;
        }

        public ValueType getValueType() {
            return valueType;
        }

        @Override
        public B getValue(BaseTuple tuple) {
            return func1.apply((A) tuple.getFactHandle().getObject());
        }

        @Override
        public B getValue(ValueResolver valueResolver, BaseTuple tuple) {
            return func1.apply((A) tuple.getFactHandle().getObject());
        }

        @Override
        public TupleValueExtractor clone() {
            PositionalTupleValueExtractor1 clone = new PositionalTupleValueExtractor1(func1, valueType);
            return clone;
        }
    }

    public static class PositionalTupleValueExtractor2<A, B, R> implements TupleValueExtractor {
        private Function2<A, B, R>  func2;

        private ValueType valueType;

        public PositionalTupleValueExtractor2(Function2<A, B, R>  func2, ValueType valueType) {
            this.func2     = func2;
            this.valueType = valueType;
        }

        public ValueType getValueType() {
            return valueType;
        }

        @Override
        public R getValue(BaseTuple tuple) {
            return func2.apply((A) tuple.getParent().getFactHandle().getObject(), (B) tuple.getFactHandle().getObject());
        }

        @Override
        public R getValue(ValueResolver valueResolver, BaseTuple tuple) {
            return func2.apply((A) tuple.getParent().getFactHandle().getObject(), (B) tuple.getFactHandle().getObject());
        }

        @Override
        public TupleValueExtractor clone() {
            PositionalTupleValueExtractor2 clone = new PositionalTupleValueExtractor2(func2, valueType);
            return clone;
        }
    }
}
