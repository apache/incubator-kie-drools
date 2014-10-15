/*
 * Copyright 2005 JBoss Inc
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
import org.drools.core.rule.IndexableConstraint;
import org.drools.core.rule.MutableTypeConstraint;
import org.drools.core.rule.constraint.MvelConstraint;
import org.drools.core.spi.BetaNodeFieldConstraint;
import org.drools.core.util.bitmask.BitMask;
import org.drools.core.util.index.IndexUtil;
import org.kie.internal.conf.IndexPrecedenceOption;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.List;

import static org.drools.core.reteoo.PropertySpecificUtil.getEmptyPropertyReactiveMask;
import static org.drools.core.reteoo.PropertySpecificUtil.allSetButTraitBitMask;
import static org.drools.core.util.index.IndexUtil.compositeAllowed;
import static org.drools.core.util.index.IndexUtil.isIndexableForNode;


public class DefaultBetaConstraints
    implements
    BetaConstraints {

    private static final long serialVersionUID = 510l;

    private transient boolean           disableIndexing;

    private BetaNodeFieldConstraint[]   constraints;

    private IndexPrecedenceOption       indexPrecedenceOption;

    private int                         indexed;

    private transient Boolean           leftUpdateOptimizationAllowed;

    public DefaultBetaConstraints() {

    }
    public DefaultBetaConstraints(final BetaNodeFieldConstraint[] constraints,
                                  final RuleBaseConfiguration conf) {
        this( constraints,
              conf,
              false );

    }

    public DefaultBetaConstraints(final BetaNodeFieldConstraint[] constraints,
                                  final RuleBaseConfiguration conf,
                                  final boolean disableIndexing) {
        this.constraints = constraints;
        this.disableIndexing = disableIndexing;
        this.indexPrecedenceOption = conf.getIndexPrecedenceOption();
    }

    public DefaultBetaConstraints cloneIfInUse() {
        if (constraints[0] instanceof MutableTypeConstraint && ((MutableTypeConstraint)constraints[0]).setInUse()) {
            BetaNodeFieldConstraint[] clonedConstraints = new BetaNodeFieldConstraint[constraints.length];
            for (int i = 0; i < constraints.length; i++) {
                clonedConstraints[i] = constraints[i].cloneIfInUse();
            }
            DefaultBetaConstraints clone = new DefaultBetaConstraints();
            clone.constraints = clonedConstraints;
            clone.disableIndexing = disableIndexing;
            clone.indexPrecedenceOption = indexPrecedenceOption;
            clone.indexed = indexed;
            return clone;
        }
        return this;
    }

    public void init(BuildContext context, short betaNodeType) {
        RuleBaseConfiguration config = context.getKnowledgeBase().getConfiguration();

        if ( disableIndexing || (!config.isIndexLeftBetaMemory() && !config.isIndexRightBetaMemory()) ) {
            indexed = 0;
        } else {
            int depth = config.getCompositeKeyDepth();
            if ( !compositeAllowed( constraints, betaNodeType ) ) {
                // UnificationRestrictions cannot be allowed in composite indexes
                // We also ensure that if there is a mixture that standard restriction is first
                depth = 1;
            }
            initIndexes( depth, betaNodeType );
        }
    }

    public void initIndexes(int depth, short betaNodeType) {
        indexed = 0;
        boolean[] indexable = isIndexableForNode(indexPrecedenceOption, betaNodeType, depth, constraints);
        for (boolean i : indexable) {
            if (i) {
                indexed++;
            } else {
                break;
            }
        }
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        constraints = (BetaNodeFieldConstraint[])in.readObject();
        indexed     = in.readInt();
        indexPrecedenceOption = (IndexPrecedenceOption) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(constraints);
        out.writeInt(indexed);
        out.writeObject(indexPrecedenceOption);
    }

    public ContextEntry[] createContext() {
        ContextEntry[] entries = new ContextEntry[constraints.length];
        for (int i = 0; i < constraints.length; i++) {
            entries[i] = constraints[i].createContextEntry();
        }
        return entries;
    }

    /* (non-Javadoc)
     * @see org.kie.common.BetaNodeConstraints#updateFromTuple(org.kie.reteoo.ReteTuple)
     */
    public void updateFromTuple(final ContextEntry[] context,
                                final InternalWorkingMemory workingMemory,
                                final LeftTuple tuple) {
        for (ContextEntry aContext : context) {
            aContext.updateFromTuple(workingMemory, tuple);
        }
    }

    /* (non-Javadoc)
     * @see org.kie.common.BetaNodeConstraints#updateFromFactHandle(org.kie.common.InternalFactHandle)
     */
    public void updateFromFactHandle(final ContextEntry[] context,
                                     final InternalWorkingMemory workingMemory,
                                     final InternalFactHandle handle) {
        for (ContextEntry aContext : context) {
            aContext.updateFromFactHandle(workingMemory, handle);
        }
    }

    public void resetTuple(final ContextEntry[] context) {
        for (ContextEntry aContext : context) {
            aContext.resetTuple();
        }
    }

    public void resetFactHandle(final ContextEntry[] context) {
        for (ContextEntry aContext : context) {
            aContext.resetFactHandle();
        }
    }

    /* (non-Javadoc)
     * @see org.kie.common.BetaNodeConstraints#isAllowedCachedLeft(java.lang.Object)
     */
    public boolean isAllowedCachedLeft(final ContextEntry[] context,
                                       final InternalFactHandle handle) {
        for (int i = indexed; i < constraints.length; i++) {
            if ( !constraints[i].isAllowedCachedLeft(context[i], handle) ) {
                return false;
            }
        }
        return true;
    }

    /* (non-Javadoc)
     * @see org.kie.common.BetaNodeConstraints#isAllowedCachedRight(org.kie.reteoo.ReteTuple)
     */
    public boolean isAllowedCachedRight(final ContextEntry[] context,
                                        final LeftTuple tuple) {
        for (int i = indexed; i < constraints.length; i++) {
            if ( !constraints[i].isAllowedCachedRight(tuple, context[i]) ) {
                return false;
            }
        }
        return true;
    }

    public boolean isIndexed() {
        return this.indexed > 0;
    }

    public int getIndexCount() {
        return this.indexed;
    }

    public boolean isEmpty() {
        return false;
    }

    public BetaMemory createBetaMemory(final RuleBaseConfiguration config,
                                       final short nodeType ) {
        return IndexUtil.Factory.createBetaMemory(config, nodeType, constraints);
    }

    public int hashCode() {
        return Arrays.hashCode(constraints);
    }

    /* (non-Javadoc)
     * @see org.kie.common.BetaNodeConstraints#getConstraints()
     */
    public BetaNodeFieldConstraint[] getConstraints() {
        return constraints;
    }

    /**
     * Determine if another object is equal to this.
     *
     * @param object
     *            The object to test.
     *
     * @return <code>true</code> if <code>object</code> is equal to this,
     *         otherwise <code>false</code>.
     */
    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }

        if ( !(object instanceof DefaultBetaConstraints) ) {
            return false;
        }

        final DefaultBetaConstraints other = (DefaultBetaConstraints) object;

        if ( this.constraints == other.constraints ) {
            return true;
        }

        if ( this.constraints.length != other.constraints.length ) {
            return false;
        }

        return Arrays.equals(constraints, other.constraints );
    }
    public BetaConstraints getOriginalConstraint() {
        throw new UnsupportedOperationException();
    }

    public BitMask getListenedPropertyMask(List<String> settableProperties) {
        BitMask mask = getEmptyPropertyReactiveMask(settableProperties.size());
        for (BetaNodeFieldConstraint constraint : constraints) {
            if (constraint instanceof MvelConstraint) {
                mask = mask.setAll(((MvelConstraint)constraint).getListenedPropertyMask(settableProperties));
            } else {
                return allSetButTraitBitMask();
            }
        }
        return mask;
    }

    public boolean isLeftUpdateOptimizationAllowed() {
        if (leftUpdateOptimizationAllowed == null) {
            leftUpdateOptimizationAllowed = calcLeftUpdateOptimizationAllowed();
        }
        return leftUpdateOptimizationAllowed;
    }

    private boolean calcLeftUpdateOptimizationAllowed() {
        for (BetaNodeFieldConstraint constraint : constraints) {
            if ( !(constraint instanceof IndexableConstraint && ((IndexableConstraint)constraint).getConstraintType().isEquality()) ) {
                return false;
            }
        }
        return true;
    }
}
