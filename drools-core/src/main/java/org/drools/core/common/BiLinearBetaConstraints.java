/*
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
package org.drools.core.common;

import org.drools.base.base.ObjectType;
import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.Pattern;
import org.drools.base.rule.constraint.BetaConstraint;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.Tuple;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.util.bitmask.BitMask;
import org.kie.api.runtime.rule.FactHandle;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;
import java.util.Objects;

/**
 * BiLinearBetaConstraints wraps standard BetaConstraints for BiLinearJoinNode.
 * This is a thin wrapper that delegates all operations to the wrapped constraints.
 */
public class BiLinearBetaConstraints implements BetaConstraints<Object> {

    private BetaConstraints wrappedConstraints;
    private boolean isEmpty;

    public BiLinearBetaConstraints() {
        // Required for Externalizable deserialization
    }

    public BiLinearBetaConstraints(BetaConstraints wrappedConstraints) {
        this.wrappedConstraints = wrappedConstraints;
        this.isEmpty = (wrappedConstraints instanceof EmptyBetaConstraints) || wrappedConstraints.isEmpty();
    }

    @Override
    public void init(BuildContext context, int betaNodeType) {
        wrappedConstraints.init(context, betaNodeType);
    }

    @Override
    public void initIndexes(int depth, int betaNodeType, RuleBaseConfiguration config) {
        wrappedConstraints.initIndexes(depth, betaNodeType, config);
    }

    @Override
    public Object createContext() {
        return wrappedConstraints.createContext();
    }

    @Override
    public boolean isIndexed() {
        return wrappedConstraints.isIndexed();
    }

    @Override
    public int getIndexCount() {
        return wrappedConstraints.getIndexCount();
    }

    @Override
    public boolean isEmpty() {
        return isEmpty;
    }

    /**
     * Returns true if no constraint evaluation is needed (empty constraints).
     * This allows callers to skip expensive context setup in inner loops.
     */
    public boolean isUnconstrainedJoin() {
        return isEmpty;
    }

    @Override
    public BetaMemory createBetaMemory(RuleBaseConfiguration config, int betaNodeType) {
        return wrappedConstraints.createBetaMemory(config, betaNodeType);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void updateFromTuple(Object context, ValueResolver valueResolver, Tuple tuple) {
        wrappedConstraints.updateFromTuple(context, valueResolver, tuple);
    }

    @Override
    public void updateFromFactHandle(Object context, ValueResolver valueResolver, FactHandle handle) {
        throw new UnsupportedOperationException("BiLinear joins do not use updateFromFactHandle");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void resetTuple(Object context) {
        wrappedConstraints.resetTuple(context);
    }

    @Override
    public void resetFactHandle(Object context) {
        throw new UnsupportedOperationException("BiLinear joins do not use resetFactHandle");
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean isAllowedCachedLeft(Object context, FactHandle handle) {
        if (isEmpty) {
            return true;
        }
        return wrappedConstraints.isAllowedCachedLeft(context, handle);
    }

    @Override
    public boolean isAllowedCachedRight(BaseTuple tuple, Object context) {
        throw new UnsupportedOperationException("BiLinear joins do not use isAllowedCachedRight");
    }

    @Override
    public BetaConstraint[] getConstraints() {
        return wrappedConstraints.getConstraints();
    }

    @Override
    public BetaConstraints getOriginalConstraint() {
        return wrappedConstraints.getOriginalConstraint();
    }

    @Override
    public <T> T cloneIfInUse() {
        BetaConstraints clonedWrapped = (BetaConstraints) wrappedConstraints.cloneIfInUse();
        BiLinearBetaConstraints cloned = new BiLinearBetaConstraints(clonedWrapped);
        return (T) cloned;
    }

    @Override
    public BitMask getListenedPropertyMask(Pattern pattern, ObjectType modifiedType, List<String> settableProperties) {
        return wrappedConstraints.getListenedPropertyMask(pattern, modifiedType, settableProperties);
    }

    @Override
    public boolean isLeftUpdateOptimizationAllowed() {
        return wrappedConstraints.isLeftUpdateOptimizationAllowed();
    }

    @Override
    public void registerEvaluationContext(BuildContext buildContext) {
        wrappedConstraints.registerEvaluationContext(buildContext);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(wrappedConstraints);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        wrappedConstraints = (BetaConstraints) in.readObject();
        this.isEmpty = (wrappedConstraints instanceof EmptyBetaConstraints) || wrappedConstraints.isEmpty();
    }

    @Override
    public String toString() {
        return "BiLinearBetaConstraints{" +
                "wrapped=" + wrappedConstraints +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BiLinearBetaConstraints)) {
            return false;
        }
        BiLinearBetaConstraints other = (BiLinearBetaConstraints) obj;
        return Objects.equals(wrappedConstraints, other.wrappedConstraints);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wrappedConstraints);
    }
}
