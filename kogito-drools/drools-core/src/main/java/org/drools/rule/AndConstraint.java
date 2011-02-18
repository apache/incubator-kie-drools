/*
 * Copyright 2010 JBoss Inc
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

package org.drools.rule;

import java.util.Arrays;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.core.util.ArrayUtils;
import org.drools.reteoo.LeftTuple;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.BetaNodeFieldConstraint;

/**
 * AND composite constraint, so the user can do things like:
 * 
 * Person( name == 'Edson' && surname == 'Tirelli' )
 * 
 *
 */
public class AndConstraint extends AbstractCompositeConstraint {

    private static final long serialVersionUID = 510l;

    /**
     * {@inheritDoc}
     */
    public boolean isAllowed(InternalFactHandle handle,
                             InternalWorkingMemory workingMemory,
                             final ContextEntry ctx) {
        for ( int i = 0; i < this.alphaConstraints.length; i++ ) {
            if ( !this.alphaConstraints[i].isAllowed( handle,
                                                      workingMemory,
                                                      ((MultiFieldConstraintContextEntry) ctx).alphas[i] ) ) {
                return false;
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAllowedCachedLeft(ContextEntry context,
                                       InternalFactHandle handle) {
        for ( int i = 0; i < this.alphaConstraints.length; i++ ) {
            if ( !this.alphaConstraints[i].isAllowed( handle,
                                                      ((MultiFieldConstraintContextEntry) context).workingMemory,
                                                      ((MultiFieldConstraintContextEntry) context).alphas[i] ) ) {
                return false;
            }
        }
        for ( int i = 0; i < this.betaConstraints.length; i++ ) {
            if ( !this.betaConstraints[i].isAllowedCachedLeft( ((MultiFieldConstraintContextEntry) context).betas[i],
                                                               handle ) ) {
                return false;
            }
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAllowedCachedRight(LeftTuple tuple,
                                        ContextEntry context) {
        for ( int i = 0; i < this.alphaConstraints.length; i++ ) {
            if ( !this.alphaConstraints[i].isAllowed( ((MultiFieldConstraintContextEntry) context).handle,
                                                      ((MultiFieldConstraintContextEntry) context).workingMemory,
                                                      ((MultiFieldConstraintContextEntry) context).alphas[i] ) ) {
                return false;
            }
        }
        for ( int i = 0; i < this.betaConstraints.length; i++ ) {
            if ( !this.betaConstraints[i].isAllowedCachedRight( tuple,
                                                                ((MultiFieldConstraintContextEntry) context).betas[i] ) ) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ArrayUtils.hashCode( this.alphaConstraints );
        result = PRIME * result + ArrayUtils.hashCode( this.betaConstraints );
        result = PRIME * result + ArrayUtils.hashCode( this.requiredDeclarations );
        return result;
    }

    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }
        if ( object == null || object.getClass() != AndConstraint.class ) {
            return false;
        }
        final AndConstraint other = (AndConstraint) object;

        return Arrays.equals( this.alphaConstraints,
                              other.alphaConstraints ) && Arrays.equals( this.betaConstraints,
                                                                         other.betaConstraints ) && Arrays.equals( this.requiredDeclarations,
                                                                                                                   other.requiredDeclarations );
    }

    public Object clone() {
        AndConstraint clone = new AndConstraint();

        // clone alpha constraints
        clone.alphaConstraints = new AlphaNodeFieldConstraint[this.alphaConstraints.length];
        for ( int i = 0; i < this.alphaConstraints.length; i++ ) {
            clone.alphaConstraints[i] = (AlphaNodeFieldConstraint) this.alphaConstraints[i].clone();
            clone.updateRequiredDeclarations( clone.alphaConstraints[i] );
        }

        // clone beta constraints
        clone.betaConstraints = new BetaNodeFieldConstraint[this.betaConstraints.length];
        for ( int i = 0; i < this.betaConstraints.length; i++ ) {
            clone.betaConstraints[i] = (BetaNodeFieldConstraint) this.betaConstraints[i].clone();
            clone.updateRequiredDeclarations( clone.betaConstraints[i] );
        }

        return clone;
    }

}
