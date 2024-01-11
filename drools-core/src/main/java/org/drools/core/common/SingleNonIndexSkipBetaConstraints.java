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
package org.drools.core.common;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;
import java.util.Optional;

import org.drools.base.base.ObjectType;
import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.ContextEntry;
import org.drools.base.rule.MutableTypeConstraint;
import org.drools.base.rule.Pattern;
import org.drools.base.rule.constraint.BetaConstraint;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.Tuple;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.util.bitmask.BitMask;
import org.kie.api.runtime.rule.FactHandle;

public class SingleNonIndexSkipBetaConstraints 
    implements
    BetaConstraints<ContextEntry> {
    
    private SingleBetaConstraints constraints;
    
    private BetaConstraint constraint;
    
    public SingleNonIndexSkipBetaConstraints() {

    }

    public SingleNonIndexSkipBetaConstraints cloneIfInUse() {
        if (constraint instanceof MutableTypeConstraint && ((MutableTypeConstraint)constraint).setInUse()) {
            return new SingleNonIndexSkipBetaConstraints(constraints.cloneIfInUse());
        }
        return this;
    }

    public SingleNonIndexSkipBetaConstraints(SingleBetaConstraints constraints) {
        this.constraints = constraints;
        this.constraint = constraints.getConstraint();
    }

    public void init(BuildContext context, int betaNodeType) {
        constraints.init(context, betaNodeType);
    }

    public void initIndexes(int depth, int betaNodeType, RuleBaseConfiguration config) {
        constraints.initIndexes(depth, betaNodeType, config);
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

    public ContextEntry createContext() {
        return constraints.createContext();
    }

    public void updateFromTuple(ContextEntry context,
                                ValueResolver valueResolver,
                                Tuple tuple) {
        constraints.updateFromTuple( context, valueResolver, tuple );
    }

    public void updateFromFactHandle(ContextEntry context,
                                     ValueResolver valueResolver,
                                     FactHandle handle) {
        constraints.updateFromFactHandle( context, valueResolver, handle );
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
                                       final int nodeType) {
        return constraints.createBetaMemory( config,
                                             nodeType );
    }

    public int hashCode() {
        return constraints.hashCode();
    }

    public BetaConstraint[] getConstraints() {
        return constraints.getConstraints();
    }

    public boolean equals(Object object) {
        return object instanceof SingleNonIndexSkipBetaConstraints && constraints.equals( ((SingleNonIndexSkipBetaConstraints)object).constraints );
    }

    public void resetFactHandle(ContextEntry context) {
        constraints.resetFactHandle( context );
    }

    public void resetTuple(ContextEntry context) {
        constraints.resetTuple( context );
    }

    public String toString() {
        return constraints.toString();
    }

    public boolean isAllowedCachedLeft(final ContextEntry context,
                                       final FactHandle handle) {
        return this.constraint.isAllowedCachedLeft( context,
                                                     handle );
    }

    public boolean isAllowedCachedRight(final BaseTuple tuple,
                                        final ContextEntry context) {
        return this.constraints.isAllowedCachedRight(tuple, context);
    }

    public BitMask getListenedPropertyMask(Pattern pattern, ObjectType modifiedType, List<String> settableProperties) {
        return constraint.getListenedPropertyMask(Optional.of(pattern), modifiedType, settableProperties);
    }

    public boolean isLeftUpdateOptimizationAllowed() {
        return true;
    }

    public void registerEvaluationContext(BuildContext buildContext) {
        this.constraint.registerEvaluationContext(buildContext);
    }
}
