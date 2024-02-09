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
import org.drools.base.util.index.IndexUtil;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.Tuple;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.util.index.IndexFactory;
import org.drools.util.bitmask.BitMask;
import org.kie.api.runtime.rule.FactHandle;

public class SingleBetaConstraints
    implements
    BetaConstraints<ContextEntry> {

    private static final long serialVersionUID = 510l;

    protected BetaConstraint<ContextEntry> constraint;

    protected boolean indexed;

    protected transient boolean disableIndex;

    public SingleBetaConstraints() {

    }

    public SingleBetaConstraints(final BetaConstraint[] constraint,
                                 final RuleBaseConfiguration conf) {
        this(constraint[0],
             conf,
             false);
    }

    public SingleBetaConstraints(final BetaConstraint constraint,
                                 final RuleBaseConfiguration conf) {
        this(constraint,
             conf,
             false);
    }

    public SingleBetaConstraints(final BetaConstraint constraint,
                                 final RuleBaseConfiguration conf,
                                 final boolean disableIndex) {
        this.constraint = constraint;
        this.disableIndex = disableIndex;
    }

    public void init(BuildContext context, int betaNodeType) {
        RuleBaseConfiguration config = context.getRuleBase().getRuleBaseConfiguration();

        if ((disableIndex) || (!config.isIndexLeftBetaMemory() && !config.isIndexRightBetaMemory())) {
            this.indexed = false;
        } else {
            initIndexes(config.getCompositeKeyDepth(), betaNodeType, config);
        }
    }

    public void initIndexes(int depth, int betaNodeType, RuleBaseConfiguration config) {
        indexed = depth >= 1 && IndexUtil.isIndexableForNode(betaNodeType, constraint, config);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        constraint = (BetaConstraint) in.readObject();
        indexed = in.readBoolean();

    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(constraint);
        out.writeBoolean(indexed);
    }

    public SingleBetaConstraints cloneIfInUse() {
        if (constraint instanceof MutableTypeConstraint && ((MutableTypeConstraint) constraint).setInUse()) {
            return clone();
        }
        return this;
    }

    public SingleBetaConstraints clone() {
        SingleBetaConstraints clone = new SingleBetaConstraints(constraint.cloneIfInUse(), null, disableIndex);
        clone.indexed = indexed;
        return clone;
    }

    public ContextEntry createContext() {
        return this.constraint.createContext();
    }

    /* (non-Javadoc)
     * @see org.kie.common.BetaNodeConstraints#updateFromTuple(org.kie.reteoo.ReteTuple)
     */
    public void updateFromTuple(final ContextEntry context,
                                final ValueResolver valueResolver,
                                final Tuple tuple) {
        context.updateFromTuple(valueResolver, tuple);
    }

    /* (non-Javadoc)
     * @see org.kie.common.BetaNodeConstraints#updateFromFactHandle(org.kie.common.InternalFactHandle)
     */
    public void updateFromFactHandle(final ContextEntry context,
                                     final ValueResolver valueResolver,
                                     final FactHandle handle) {
        context.updateFromFactHandle(valueResolver, handle);
    }

    /* (non-Javadoc)
     * @see org.kie.common.BetaNodeConstraints#isAllowedCachedLeft(java.lang.Object)
     */
    public boolean isAllowedCachedLeft(final ContextEntry context,
                                       final FactHandle handle) {
        return this.indexed || this.constraint.isAllowedCachedLeft(context,
                                                                   handle);
    }

    /* (non-Javadoc)
     * @see org.kie.common.BetaNodeConstraints#isAllowedCachedRight(org.kie.reteoo.ReteTuple)
     */
    public boolean isAllowedCachedRight(final BaseTuple tuple, final ContextEntry context) {
        return this.constraint.isAllowedCachedRight(tuple, context);
    }

    public boolean isIndexed() {
        return this.indexed;
    }

    public int getIndexCount() {
        return (this.indexed ? 1 : 0);
    }

    public boolean isEmpty() {
        return false;
    }

    public BetaMemory createBetaMemory(final RuleBaseConfiguration config,
                                       final int nodeType) {
        return IndexFactory.createBetaMemory(config, nodeType, constraint);
    }

    public int hashCode() {
        return this.constraint.hashCode();
    }

    public BetaConstraint getConstraint() {
        return this.constraint;
    }

    /* (non-Javadoc)
     * @see org.kie.common.BetaNodeConstraints#getConstraints()
     */
    public BetaConstraint[] getConstraints() {
        return new BetaConstraint[]{this.constraint};
    }

    /**
     * Determine if another object is equal to this.
     *
     * @param object The object to test.
     * @return <code>true</code> if <code>object</code> is equal to this,
     * otherwise <code>false</code>.
     */
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        final SingleBetaConstraints other = (SingleBetaConstraints) object;

        return this.constraint == other.constraint || this.constraint.equals(other.constraint);
    }

    public void resetFactHandle(ContextEntry context) {
        context.resetFactHandle();
    }

    public void resetTuple(ContextEntry context) {
        context.resetTuple();
    }

    public BetaConstraints getOriginalConstraint() {
        throw new UnsupportedOperationException();
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
