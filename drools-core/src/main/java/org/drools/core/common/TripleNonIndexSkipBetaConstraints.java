/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.common;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.ContextEntry;
import org.drools.core.rule.MutableTypeConstraint;
import org.drools.core.rule.constraint.MvelConstraint;
import org.drools.core.spi.BetaNodeFieldConstraint;
import org.drools.core.spi.Tuple;
import org.drools.core.util.bitmask.BitMask;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

import static org.drools.core.reteoo.PropertySpecificUtil.allSetButTraitBitMask;

public class TripleNonIndexSkipBetaConstraints 
    implements
    BetaConstraints {
    
    private TripleBetaConstraints constraints;
    
    private BetaNodeFieldConstraint constraint0;
    private BetaNodeFieldConstraint constraint1;
    private BetaNodeFieldConstraint constraint2;
    
    public TripleNonIndexSkipBetaConstraints() {

    }
    
    public TripleNonIndexSkipBetaConstraints(TripleBetaConstraints constraints) {
        this.constraints = constraints;
        BetaNodeFieldConstraint[] constraint = constraints.getConstraints();
        this.constraint0 = constraint[0];
        this.constraint1 = constraint[1];
        this.constraint2 = constraint[2];
    }

    public TripleNonIndexSkipBetaConstraints cloneIfInUse() {
        if (constraint0 instanceof MutableTypeConstraint && ((MutableTypeConstraint)constraint0).setInUse()) {
            return new TripleNonIndexSkipBetaConstraints(constraints.cloneIfInUse());
        }
        return this;
    }

    public void init(BuildContext context, short betaNodeType) {
        constraints.init(context, betaNodeType);
    }

    public void initIndexes(int depth, short betaNodeType) {
        constraints.initIndexes(depth, betaNodeType);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        throw new UnsupportedOperationException( );
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        throw new UnsupportedOperationException( );
    }
    
    public BetaConstraints getOriginalConstraint() {
        return this.constraints;
    }

    public ContextEntry[] createContext() {
        return constraints.createContext();
    }

    public void updateFromTuple(ContextEntry[] context,
                                InternalWorkingMemory workingMemory,
                                Tuple tuple) {
        constraints.updateFromTuple(context, workingMemory, tuple);
    }

    public void updateFromFactHandle(ContextEntry[] context,
                                     InternalWorkingMemory workingMemory,
                                     InternalFactHandle handle) {
        constraints.updateFromFactHandle( context,
                                          workingMemory,
                                          handle );
    }

    public boolean isIndexed() {
        return constraints.isIndexed();
    }

    public int getIndexCount() {
        return constraints.getIndexCount();
    }

    public boolean isEmpty() {
        return constraints.isEmpty();
    }

    public BetaMemory createBetaMemory(final RuleBaseConfiguration config, 
                                       final short nodeType) {
        return constraints.createBetaMemory(config,
                nodeType);
    }

    public int hashCode() {
        return constraints.hashCode();
    }

    public BetaNodeFieldConstraint[] getConstraints() {
        return constraints.getConstraints();
    }

    public boolean equals(Object object) {
        return constraints.equals( object );
    }

    public void resetFactHandle(ContextEntry[] context) {
        constraints.resetFactHandle(context);
    }

    public void resetTuple(ContextEntry[] context) {
        constraints.resetTuple( context );
    }

    public String toString() {
        return constraints.toString();
    }

    public boolean isAllowedCachedLeft(final ContextEntry[] context,
                                       final InternalFactHandle handle) {
        return this.constraint0.isAllowedCachedLeft( context[0],
                                                     handle ) && this.constraint1.isAllowedCachedLeft( context[1],
                                                                                                       handle ) && this.constraint2.isAllowedCachedLeft( context[2],
                                                                                                                                                         handle );
    }

    public boolean isAllowedCachedRight(ContextEntry[] context,
                                        Tuple tuple) {
        return this.constraints.isAllowedCachedRight( context, tuple );
    }

    public BitMask getListenedPropertyMask(List<String> settableProperties) {
        if (constraint0 instanceof MvelConstraint && constraint1 instanceof MvelConstraint && constraint2 instanceof MvelConstraint) {
            return ((MvelConstraint)constraint0).getListenedPropertyMask(settableProperties)
                                                .setAll(((MvelConstraint) constraint1).getListenedPropertyMask(settableProperties))
                                                .setAll(((MvelConstraint) constraint2).getListenedPropertyMask(settableProperties));
        }
        return allSetButTraitBitMask();
    }

    public boolean isLeftUpdateOptimizationAllowed() {
        return true;
    }

    public void registerEvaluationContext(BuildContext buildContext) {
        if (constraint0 instanceof MvelConstraint) {
            ((MvelConstraint) constraint0).registerEvaluationContext(buildContext);
        }
        if (constraint1 instanceof MvelConstraint) {
            ((MvelConstraint) constraint1).registerEvaluationContext(buildContext);
        }
        if (constraint2 instanceof MvelConstraint) {
            ((MvelConstraint) constraint2).registerEvaluationContext(buildContext);
        }
    }
}
