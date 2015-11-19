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
import org.drools.core.spi.BetaNodeFieldConstraint;
import org.drools.core.spi.Tuple;
import org.drools.core.util.bitmask.BitMask;
import org.drools.core.util.index.TupleList;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

import static org.drools.core.reteoo.PropertySpecificUtil.getEmptyPropertyReactiveMask;

public class EmptyBetaConstraints
    implements
    BetaConstraints {

    private static final BetaConstraints INSTANCE = new EmptyBetaConstraints();
    private static final ContextEntry[]  EMPTY    = new ContextEntry[0];

    public static BetaConstraints getInstance() {
        return EmptyBetaConstraints.INSTANCE;
    }

    private static final long serialVersionUID = 510l;

    public EmptyBetaConstraints() {
    }

    public EmptyBetaConstraints cloneIfInUse() {
        return this;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }
    /* (non-Javadoc)
     * @see org.kie.common.BetaNodeConstraints#updateFromTuple(org.kie.reteoo.ReteTuple)
     */
    public void updateFromTuple(final ContextEntry[] context,
                                final InternalWorkingMemory workingMemory,
                                final Tuple tuple) {
    }

    /* (non-Javadoc)
     * @see org.kie.common.BetaNodeConstraints#updateFromFactHandle(org.kie.common.InternalFactHandle)
     */
    public void updateFromFactHandle(final ContextEntry[] context,
                                     final InternalWorkingMemory workingMemory,
                                     final InternalFactHandle handle) {
    }

    public void resetTuple(final ContextEntry[] context) {
    }

    public void resetFactHandle(final ContextEntry[] context) {
    }

    /* (non-Javadoc)
     * @see org.kie.common.BetaNodeConstraints#isAllowedCachedLeft(java.lang.Object)
     */
    public boolean isAllowedCachedLeft(final ContextEntry[] context,
                                       final InternalFactHandle handle) {
        return true;
    }

    /* (non-Javadoc)
     * @see org.kie.common.BetaNodeConstraints#isAllowedCachedRight(org.kie.reteoo.ReteTuple)
     */
    public boolean isAllowedCachedRight(final ContextEntry[] context,
                                        final Tuple tuple) {
        return true;
    }

    public boolean isIndexed() {
        return false;
    }

    public int getIndexCount() {
        return 0;
    }

    public boolean isEmpty() {
        return true;
    }

    public BetaMemory createBetaMemory(final RuleBaseConfiguration config,
                                       final short nodeType) {
        final BetaMemory memory = new BetaMemory( config.isSequential() ? null : new TupleList(),
                                                  new TupleList(),
                                                  this.createContext(),
                                                  nodeType );

        return memory;
    }

    public int hashCode() {
        return 1;
    }

    /* (non-Javadoc)
     * @see org.kie.common.BetaNodeConstraints#getConstraints()
     */
    public BetaNodeFieldConstraint[] getConstraints() {
        return new BetaNodeFieldConstraint[0];
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

        return (object != null && (object instanceof EmptyBetaConstraints));
    }

    public ContextEntry[] createContext() {
        return EMPTY;
    }

    public BetaConstraints getOriginalConstraint() {
        throw new UnsupportedOperationException();
    }

    public BitMask getListenedPropertyMask(List<String> settableProperties) {
        return getEmptyPropertyReactiveMask(settableProperties.size());
    }

    public void init(BuildContext context, short betaNodeType) { }
    public void initIndexes(int depth, short betaNodeType) { }

    public boolean isLeftUpdateOptimizationAllowed() {
        return true;
    }

    public void registerEvaluationContext(BuildContext buildContext) { }
}
