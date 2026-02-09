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
import org.drools.core.reteoo.*;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.util.index.TupleList;
import org.drools.util.bitmask.BitMask;
import org.kie.api.runtime.rule.FactHandle;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;
import java.util.Objects;

/**
 * BiLinearBetaConstraints wraps standard BetaConstraints to provide cross-network
 * variable resolution for BiLinearJoinNode. It intercepts constraint evaluation
 * calls and ensures that variables from both input networks are available during
 * evaluation.
 */
public class BiLinearBetaConstraints implements BetaConstraints<BiLinearContextEntry> {

    private final BetaConstraints wrappedConstraints;

    private BiLinearDeclarationContext declarationContext;

    public BiLinearBetaConstraints(BetaConstraints wrappedConstraints) {
        this.wrappedConstraints = wrappedConstraints;
    }

    public BiLinearBetaConstraints(BetaConstraints wrappedConstraints,
                                   BiLinearDeclarationContext declarationContext) {
        this(wrappedConstraints);
        this.declarationContext = declarationContext;
    }
    public void setDeclarationContext(BiLinearDeclarationContext declarationContext) {
        this.declarationContext = declarationContext;
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
    public BiLinearContextEntry createContext() {
        return new BiLinearContextEntry(declarationContext);
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
        return wrappedConstraints.isEmpty();
    }

    @Override
    public BetaMemory createBetaMemory(RuleBaseConfiguration config, int betaNodeType) {
        return new BetaMemory<BiLinearContextEntry>(
                config.isSequential() ? null : new TupleList(),
                new TupleList(),
                createContext(),
                betaNodeType
        );
    }

    @Override
    public void updateFromTuple(BiLinearContextEntry context, ValueResolver valueResolver, Tuple tuple) {
        context.updateFromTuple(valueResolver, tuple);

        Object wrappedContext = getWrappedContext(context);
        if (tuple != null) {
            wrappedConstraints.updateFromTuple(wrappedContext, valueResolver, tuple);
        }
    }

    public void updateFromBiLinearTuples(BiLinearContextEntry context,
                                         ValueResolver valueResolver,
                                         Tuple firstNetworkTuple,
                                         Tuple secondNetworkTuple) {
        context.updateFromBiLinearTuples(valueResolver, firstNetworkTuple, secondNetworkTuple);

        Object wrappedContext = getWrappedContext(context);
        wrappedConstraints.updateFromTuple(wrappedContext, valueResolver, (Tuple) firstNetworkTuple);
    }

    @Override
    public void updateFromFactHandle(BiLinearContextEntry context, ValueResolver valueResolver, FactHandle handle) {
        context.updateFromFactHandle(valueResolver, handle);

        Object wrappedContext = getWrappedContext(context);
        wrappedConstraints.updateFromFactHandle(wrappedContext, valueResolver, handle);
    }

    @Override
    public void resetTuple(BiLinearContextEntry context) {
        context.resetTuple();

        Object wrappedContext = getWrappedContext(context);
        wrappedConstraints.resetTuple(wrappedContext);
    }

    @SuppressWarnings("unchecked")
    public void resetTupleContext(Object context) {
        if (context instanceof BiLinearContextEntry) {
            resetTuple((BiLinearContextEntry) context);
        } else {
            wrappedConstraints.resetTuple(context);
        }
    }

    @Override
    public void resetFactHandle(BiLinearContextEntry context) {
        context.resetFactHandle();

        Object wrappedContext = getWrappedContext(context);
        wrappedConstraints.resetFactHandle(wrappedContext);
    }

    @Override
    public boolean isAllowedCachedLeft(BiLinearContextEntry context, FactHandle handle) {
        if (wrappedConstraints instanceof org.drools.core.common.EmptyBetaConstraints) {
            return wrappedConstraints.isAllowedCachedLeft(wrappedConstraints.createContext(), handle);
        }

        Object wrappedContext = getWrappedContext(context);
        return wrappedConstraints.isAllowedCachedLeft(wrappedContext, handle);
    }

    @Override
    public boolean isAllowedCachedRight(BaseTuple tuple, BiLinearContextEntry context) {
        if (wrappedConstraints instanceof org.drools.core.common.EmptyBetaConstraints) {
            return wrappedConstraints.isAllowedCachedRight(tuple, wrappedConstraints.createContext());
        }

        Object wrappedContext = getWrappedContext(context);
        return wrappedConstraints.isAllowedCachedRight(tuple, wrappedContext);
    }

    @SuppressWarnings("unchecked")
    private Object getWrappedContext(BiLinearContextEntry context) {
        Object wrappedContext = wrappedConstraints.createContext();

        if (context.getFirstNetworkTuple() != null) {
            wrappedConstraints.updateFromTuple(wrappedContext, context.getValueResolver(), (Tuple) context.getFirstNetworkTuple());
        }
        if (context.getRightHandle() != null) {
            wrappedConstraints.updateFromFactHandle(wrappedContext, context.getValueResolver(), context.getRightHandle());
        }

        return wrappedContext;
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
        BiLinearBetaConstraints cloned = new BiLinearBetaConstraints(clonedWrapped, declarationContext);
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
        out.writeObject(declarationContext);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        throw new UnsupportedOperationException("Deserialization not yet implemented for BiLinearBetaConstraints");
    }

    @Override
    public String toString() {
        return "BiLinearBetaConstraints{" +
                "wrapped=" + wrappedConstraints +
                ", declarationContext=" + declarationContext +
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
        return Objects.equals(wrappedConstraints, other.wrappedConstraints) &&
                Objects.equals(declarationContext, other.declarationContext);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wrappedConstraints, declarationContext);
    }
}