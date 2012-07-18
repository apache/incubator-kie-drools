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

package org.drools.common;

import org.drools.RuleBaseConfiguration;
import org.drools.conf.IndexPrecedenceOption;
import org.drools.core.util.index.IndexUtil;
import org.drools.reteoo.BetaMemory;
import org.drools.reteoo.BetaNode;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.ContextEntry;
import org.drools.rule.constraint.MvelConstraint;
import org.drools.spi.BetaNodeFieldConstraint;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.List;

import static org.drools.core.util.index.IndexUtil.compositeAllowed;
import static org.drools.core.util.index.IndexUtil.isIndexableForNode;


public class DefaultBetaConstraints
    implements
    BetaConstraints {

    private static final long serialVersionUID = 510l;

    private transient boolean           disableIndexing;

    private BetaNodeFieldConstraint[] constraints;

    private IndexPrecedenceOption indexPrecedenceOption;

    private int               indexed;

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

    public void init(BuildContext context, BetaNode betaNode) {
        RuleBaseConfiguration config = context.getRuleBase().getConfiguration();

        if ( disableIndexing || (!config.isIndexLeftBetaMemory() && !config.isIndexRightBetaMemory()) ) {
            indexed = 0;
        } else {
            int depth = config.getCompositeKeyDepth();
            if ( !compositeAllowed( constraints, betaNode.getType() ) ) {
                // UnificationRestrictions cannot be allowed in composite indexes
                // We also ensure that if there is a mixture that standard restriction is first
                depth = 1;
            }
            initIndexes( depth, betaNode.getType() );
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
     * @see org.drools.common.BetaNodeConstraints#updateFromTuple(org.drools.reteoo.ReteTuple)
     */
    public void updateFromTuple(final ContextEntry[] context,
                                final InternalWorkingMemory workingMemory,
                                final LeftTuple tuple) {
        for (ContextEntry aContext : context) {
            aContext.updateFromTuple(workingMemory, tuple);
        }
    }

    /* (non-Javadoc)
     * @see org.drools.common.BetaNodeConstraints#updateFromFactHandle(org.drools.common.InternalFactHandle)
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
     * @see org.drools.common.BetaNodeConstraints#isAllowedCachedLeft(java.lang.Object)
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
     * @see org.drools.common.BetaNodeConstraints#isAllowedCachedRight(org.drools.reteoo.ReteTuple)
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
     * @see org.drools.common.BetaNodeConstraints#getConstraints()
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

    public long getListenedPropertyMask(List<String> settableProperties) {
        long mask = 0L;
        for (BetaNodeFieldConstraint constraint : constraints) {
            if (constraint instanceof MvelConstraint) {
                mask |= ((MvelConstraint)constraint).getListenedPropertyMask(settableProperties);
            } else {
                return Long.MAX_VALUE;
            }
        }
        return mask;
    }
}
