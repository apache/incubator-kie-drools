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
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.ContextEntry;
import org.drools.core.rule.MutableTypeConstraint;
import org.drools.core.rule.constraint.MvelConstraint;
import org.drools.core.spi.BetaNodeFieldConstraint;
import org.drools.core.spi.Tuple;
import org.drools.core.util.bitmask.BitMask;
import org.kie.internal.conf.IndexPrecedenceOption;

import java.util.List;

import static org.drools.core.reteoo.PropertySpecificUtil.allSetButTraitBitMask;

public class DoubleBetaConstraints extends MultipleBetaConstraint {

    private static final long             serialVersionUID = 510l;

    public DoubleBetaConstraints() { }

    public DoubleBetaConstraints(final BetaNodeFieldConstraint[] constraints,
                                 final RuleBaseConfiguration conf) {
        this( constraints,
              conf,
              false );
    }

    public DoubleBetaConstraints(final BetaNodeFieldConstraint[] constraints,
                                 final RuleBaseConfiguration conf,
                                 final boolean disableIndexing) {
        super(constraints, conf, disableIndexing);
    }

    private DoubleBetaConstraints( BetaNodeFieldConstraint[] constraints,
                                   IndexPrecedenceOption indexPrecedenceOption,
                                   boolean disableIndexing) {
        super(constraints, indexPrecedenceOption, disableIndexing);
    }

    public DoubleBetaConstraints cloneIfInUse() {
        if (constraints[0] instanceof MutableTypeConstraint && ((MutableTypeConstraint)constraints[0]).setInUse()) {
            BetaNodeFieldConstraint[] clonedConstraints = new BetaNodeFieldConstraint[constraints.length];
            for (int i = 0; i < constraints.length; i++) {
                clonedConstraints[i] = constraints[i].cloneIfInUse();
            }
            DoubleBetaConstraints clone = new DoubleBetaConstraints(clonedConstraints, indexPrecedenceOption, disableIndexing);
            clone.indexed = indexed;
            return clone;
        }
        return this;
    }

    /* (non-Javadoc)
     * @see org.kie.common.BetaNodeConstraints#updateFromTuple(org.kie.reteoo.ReteTuple)
     */
    public void updateFromTuple(final ContextEntry[] context,
                                final InternalWorkingMemory workingMemory,
                                final Tuple tuple) {
        context[0].updateFromTuple( workingMemory,
                                    tuple );
        context[1].updateFromTuple( workingMemory,
                                    tuple );
    }

    /* (non-Javadoc)
     * @see org.kie.common.BetaNodeConstraints#updateFromFactHandle(org.kie.common.InternalFactHandle)
     */
    public void updateFromFactHandle(final ContextEntry[] context,
                                     final InternalWorkingMemory workingMemory,
                                     final InternalFactHandle handle) {
        context[0].updateFromFactHandle( workingMemory,
                                         handle );
        context[1].updateFromFactHandle( workingMemory,
                                         handle );
    }

    public void resetTuple(final ContextEntry[] context) {
        context[0].resetTuple();
        context[1].resetTuple();
    }

    public void resetFactHandle(final ContextEntry[] context) {
        context[0].resetFactHandle();
        context[1].resetFactHandle();
    }

    /* (non-Javadoc)
     * @see org.kie.common.BetaNodeConstraints#isAllowedCachedLeft(java.lang.Object)
     */
    public boolean isAllowedCachedLeft(final ContextEntry[] context,
                                       final InternalFactHandle handle) {
        return (indexed[0] || constraints[0].isAllowedCachedLeft( context[0], handle )) &&
               (indexed[1] || constraints[1].isAllowedCachedLeft( context[1], handle ));
    }

    /* (non-Javadoc)
     * @see org.kie.common.BetaNodeConstraints#isAllowedCachedRight(org.kie.reteoo.ReteTuple)
     */
    public boolean isAllowedCachedRight(final ContextEntry[] context,
                                        final Tuple tuple) {
        return constraints[0].isAllowedCachedRight( tuple, context[0] ) &&
               constraints[1].isAllowedCachedRight( tuple, context[1] );
    }

    public int hashCode() {
        return constraints[0].hashCode() ^ this.constraints[1].hashCode();
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

        if ( object == null || !(object instanceof DoubleBetaConstraints) ) {
            return false;
        }

        final DoubleBetaConstraints other = (DoubleBetaConstraints) object;

        if ( constraints[0] != other.constraints[0] && !constraints[0].equals( other.constraints[0] ) ) {
            return false;
        }

        if ( constraints[1] != other.constraints[1] && !constraints[1].equals( other.constraints[1] ) ) {
            return false;
        }

        return true;
    }

    public BetaConstraints getOriginalConstraint() {
        throw new UnsupportedOperationException();
    }

    public BitMask getListenedPropertyMask(List<String> settableProperties) {
        if (constraints[0] instanceof MvelConstraint && constraints[1] instanceof MvelConstraint) {
            return ((MvelConstraint)constraints[0]).getListenedPropertyMask(settableProperties)
                                                   .setAll(((MvelConstraint) constraints[1]).getListenedPropertyMask(settableProperties));
        }
        return allSetButTraitBitMask();
    }

    public void registerEvaluationContext(BuildContext buildContext) {
        if (constraints[0] instanceof MvelConstraint) {
            ((MvelConstraint) constraints[0]).registerEvaluationContext(buildContext);
        }
        if (constraints[1] instanceof MvelConstraint) {
            ((MvelConstraint) constraints[1]).registerEvaluationContext(buildContext);
        }
    }
}
