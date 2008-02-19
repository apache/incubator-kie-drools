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

import org.drools.RuntimeDroolsException;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.ReteTuple;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.BetaNodeFieldConstraint;
import org.drools.spi.Constraint;
import org.drools.spi.Constraint.ConstraintType;
import org.drools.util.ArrayUtils;

/**
 * A superclass for all composite constraints, like "OR" and "AND"
 * 
 * @author etirelli
 */
public abstract class AbstractCompositeConstraint extends MutableTypeConstraint {

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
        if ( constraint.getType() == ConstraintType.ALPHA ) {
            this.addAlphaConstraint( (AlphaNodeFieldConstraint) constraint );
        } else if ( constraint.getType() == ConstraintType.BETA ) {
            this.addBetaConstraint( (BetaNodeFieldConstraint) constraint );
        } else {
            throw new RuntimeDroolsException( "Constraint type MUST be known in advance.");
        }
    }

    /**
     * Updades the cached required declaration array
     * 
     * @param constraint
     */
    protected void updateRequiredDeclarations(Constraint constraint) {
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
    public void replaceDeclaration(Declaration oldDecl,
                                   Declaration newDecl) {
        for ( int i = 0; i < this.alphaConstraints.length; i++ ) {
            this.alphaConstraints[i].replaceDeclaration( oldDecl,
                                                         newDecl );
        }
        for ( int i = 0; i < this.betaConstraints.length; i++ ) {
            this.betaConstraints[i].replaceDeclaration( oldDecl,
                                                        newDecl );
        }
        for ( int i = 0; i < this.requiredDeclarations.length; i++ ) {
            if ( this.requiredDeclarations[i] == oldDecl ) {
                this.requiredDeclarations[i] = newDecl;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public ContextEntry createContextEntry() {
        return new MultiFieldConstraintContextEntry( this.alphaConstraints,
                                                     this.betaConstraints );
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

    public abstract Object clone();
    
    public void setType(ConstraintType type) {
        super.setType( type );
        for( int i = 0; i < alphaConstraints.length; i++ ) {
            if( this.alphaConstraints[i] instanceof MutableTypeConstraint ) {
                ((MutableTypeConstraint)this.alphaConstraints[i]).setType( type );
            }
        }
        for( int i = 0; i < betaConstraints.length; i++ ) {
            if( this.betaConstraints[i] instanceof MutableTypeConstraint ) {
                ((MutableTypeConstraint)this.betaConstraints[i]).setType( type );
            }
        }
    }

    /**
     * A context entry for composite restrictions
     * 
     * @author etirelli
     */
    protected static class MultiFieldConstraintContextEntry
        implements
        ContextEntry {

        private static final long    serialVersionUID = 400L;

        public final ContextEntry[]  alphas;
        public final ContextEntry[]  betas;
        public ContextEntry          next;
        public InternalWorkingMemory workingMemory;
        public InternalFactHandle    handle;

        public MultiFieldConstraintContextEntry(final AlphaNodeFieldConstraint[] alphas,
                                                final BetaNodeFieldConstraint[] betas) {
            this.alphas = new ContextEntry[alphas.length];
            for ( int i = 0; i < alphas.length; i++ ) {
                this.alphas[i] = alphas[i].createContextEntry();
            }
            this.betas = new ContextEntry[betas.length];
            for ( int i = 0; i < betas.length; i++ ) {
                this.betas[i] = betas[i].createContextEntry();
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
            this.workingMemory = workingMemory;
            this.handle = handle;
            for ( int i = 0; i < alphas.length; i++ ) {
                if ( alphas[i] != null ) {
                    alphas[i].updateFromFactHandle( workingMemory,
                                                    handle );
                }
            }
            for ( int i = 0; i < betas.length; i++ ) {
                betas[i].updateFromFactHandle( workingMemory,
                                               handle );
            }
        }

        public void updateFromTuple(InternalWorkingMemory workingMemory,
                                    ReteTuple tuple) {
            this.workingMemory = workingMemory;
            for ( int i = 0; i < alphas.length; i++ ) {
                if ( alphas[i] != null ) {
                    alphas[i].updateFromTuple( workingMemory,
                                               tuple );
                }
            }
            for ( int i = 0; i < betas.length; i++ ) {
                betas[i].updateFromTuple( workingMemory,
                                          tuple );
            }
        }

        public void resetTuple() {
            this.workingMemory = null;
            for ( int i = 0, length = this.alphas.length; i < length; i++ ) {
                if ( alphas[i] != null ) {
                    this.alphas[i].resetTuple();
                }
            }
            for ( int i = 0, length = this.betas.length; i < length; i++ ) {
                this.betas[i].resetTuple();
            }
        }

        public void resetFactHandle() {
            this.workingMemory = null;
            this.handle = null;
            for ( int i = 0, length = this.alphas.length; i < length; i++ ) {
                if ( alphas[i] != null ) {
                    this.alphas[i].resetFactHandle();
                }
            }
            for ( int i = 0, length = this.betas.length; i < length; i++ ) {
                this.betas[i].resetFactHandle();
            }
        }

    }

}