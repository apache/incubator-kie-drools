/*
 * Copyright 2006 JBoss Inc
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
import org.drools.reteoo.ReteTuple;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.BetaNodeFieldConstraint;
import org.drools.spi.Constraint;
import org.drools.util.ArrayUtils;

/**
 * A superclass for all composite constraints, like "OR" and "AND"
 * 
 * @author etirelli
 */
public abstract class AbstractCompositeConstraint
    implements
    AlphaNodeFieldConstraint,
    BetaNodeFieldConstraint {

    protected AlphaNodeFieldConstraint[] alphaConstraints     = new AlphaNodeFieldConstraint[0];
    protected BetaNodeFieldConstraint[]  betaConstraints      = new BetaNodeFieldConstraint[0];
    protected Declaration[]              requiredDeclarations = new Declaration[0];

    /**
     * Adds an alpha constraint to the multi field OR constraint
     * 
     * @param constraint
     */
    public void addAlphaConstraint(AlphaNodeFieldConstraint constraint) {
        if ( constraint != null ) {
            AlphaNodeFieldConstraint[] tmp = this.alphaConstraints;
            this.alphaConstraints = new AlphaNodeFieldConstraint[tmp.length + 1];
            System.arraycopy( tmp,
                              0,
                              this.alphaConstraints,
                              0,
                              tmp.length );
            this.alphaConstraints[this.alphaConstraints.length - 1] = constraint;
            this.updateRequiredDeclarations( constraint );
        }
    }

    /**
     * Adds a beta constraint to this multi field OR constraint
     * @param constraint
     */
    public void addBetaConstraint(BetaNodeFieldConstraint constraint) {
        if ( constraint != null ) {
            BetaNodeFieldConstraint[] tmp = this.betaConstraints;
            this.betaConstraints = new BetaNodeFieldConstraint[tmp.length + 1];
            System.arraycopy( tmp,
                              0,
                              this.betaConstraints,
                              0,
                              tmp.length );
            this.betaConstraints[this.betaConstraints.length - 1] = constraint;
            this.updateRequiredDeclarations( constraint );
        }
    }
    
    /**
     * Adds a constraint too all lists it belongs to by checking for its type 
     * @param constraint
     */
    public void addConstraint(Constraint constraint) {
        if( constraint instanceof AlphaNodeFieldConstraint ) {
            this.addAlphaConstraint( (AlphaNodeFieldConstraint) constraint );
        }
        if( constraint instanceof BetaNodeFieldConstraint ) {
            this.addBetaConstraint( (BetaNodeFieldConstraint) constraint ); 
        }
    }

    /**
     * Updades the cached required declaration array
     * 
     * @param constraint
     */
    private void updateRequiredDeclarations(Constraint constraint) {
        Declaration[] decs = constraint.getRequiredDeclarations();
        if ( decs != null && decs.length > 0 ) {
            for ( int i = 0; i < decs.length; i++ ) {
                Declaration dec = decs[i];
                // check for duplications
                for ( int j = 0; j < this.requiredDeclarations.length; j++ ) {
                    if ( dec.equals( this.requiredDeclarations[j] ) ) {
                        dec = null;
                        break;
                    }
                }
                if ( dec != null ) {
                    Declaration[] tmp = this.requiredDeclarations;
                    this.requiredDeclarations = new Declaration[tmp.length + 1];
                    System.arraycopy( tmp,
                                      0,
                                      this.requiredDeclarations,
                                      0,
                                      tmp.length );
                    this.requiredDeclarations[this.requiredDeclarations.length - 1] = dec;
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public Declaration[] getRequiredDeclarations() {
        return this.requiredDeclarations;
    }

    /**
     * {@inheritDoc}
     */
    public ContextEntry getContextEntry() {
        return new MultiFieldConstraintContextEntry( this.betaConstraints );
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
        if ( object == null || object.getClass() != AbstractCompositeConstraint.class ) {
            return false;
        }
        final AbstractCompositeConstraint other = (AbstractCompositeConstraint) object;

        return Arrays.equals( this.alphaConstraints,
                              other.alphaConstraints ) && Arrays.equals( this.betaConstraints,
                                                                         other.betaConstraints ) && Arrays.equals( this.requiredDeclarations,
                                                                                                                   other.requiredDeclarations );
    }

    public AbstractCompositeConstraint() {
        super();
    }

    /**
     * A context entry for composite restrictions
     * 
     * @author etirelli
     */
    protected static class MultiFieldConstraintContextEntry
        implements
        ContextEntry {

        private static final long    serialVersionUID = -612826751146514955L;

        public final ContextEntry[] contexts;
        public ContextEntry          next;

        public MultiFieldConstraintContextEntry(BetaNodeFieldConstraint[] constraints) {
            contexts = new ContextEntry[constraints.length];
            for ( int i = 0; i < contexts.length; i++ ) {
                contexts[i] = constraints[i].getContextEntry();
            }
        }

        public ContextEntry getNext() {
            return this.next;
        }

        public void setNext(ContextEntry entry) {
            this.next = entry;
        }

        public void updateFromFactHandle(InternalWorkingMemory workingMemory,
                                         InternalFactHandle handle) {
            for ( int i = 0; i < contexts.length; i++ ) {
                contexts[i].updateFromFactHandle( workingMemory,
                                                  handle );
            }
        }

        public void updateFromTuple(InternalWorkingMemory workingMemory,
                                    ReteTuple tuple) {
            for ( int i = 0; i < contexts.length; i++ ) {
                contexts[i].updateFromTuple( workingMemory,
                                             tuple );
            }
        }

    }

}