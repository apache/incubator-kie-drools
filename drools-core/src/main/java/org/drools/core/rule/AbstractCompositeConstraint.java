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

package org.drools.core.rule;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.BetaNodeFieldConstraint;
import org.drools.core.spi.Constraint;

/**
 * A superclass for all composite constraints, like "OR" and "AND"
 */
public abstract class AbstractCompositeConstraint extends MutableTypeConstraint {

    protected AlphaNodeFieldConstraint[] alphaConstraints     = new AlphaNodeFieldConstraint[0];
    protected BetaNodeFieldConstraint[]  betaConstraints      = new BetaNodeFieldConstraint[0];
    protected Declaration[]              requiredDeclarations = new Declaration[0];

    public AbstractCompositeConstraint() {
        super();
        this.setType( Constraint.ConstraintType.ALPHA );
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        alphaConstraints        = (AlphaNodeFieldConstraint[])in.readObject();
        betaConstraints         = (BetaNodeFieldConstraint[])in.readObject();
        requiredDeclarations    = (Declaration[])in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(alphaConstraints);
        out.writeObject(betaConstraints);
        out.writeObject(requiredDeclarations);
    }

    public AlphaNodeFieldConstraint[] getAlphaConstraints() {
        return alphaConstraints;
    }

    public BetaNodeFieldConstraint[] getBetaConstraints() {
        return betaConstraints;
    }
    
    public boolean isTemporal() {
        for( AlphaNodeFieldConstraint c : this.alphaConstraints ) {
            if( c.isTemporal() ) {
                return true;
            }
        }
        for( BetaNodeFieldConstraint c : this.betaConstraints ) {
            if( c.isTemporal() ) {
                return true;
            }
        }
        return false;
    }

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
            this.setType( Constraint.ConstraintType.BETA );
        }
    }

    /**
     * Adds a constraint too all lists it belongs to by checking for its type
     * @param constraint
     */
    public void addConstraint(Constraint constraint) {
        if ( ConstraintType.ALPHA.equals(constraint.getType())) {
            this.addAlphaConstraint( (AlphaNodeFieldConstraint) constraint );
        } else if ( ConstraintType.BETA.equals(constraint.getType())) {
            this.addBetaConstraint( (BetaNodeFieldConstraint) constraint );
        } else {
            throw new RuntimeException( "Constraint type MUST be known in advance.");
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
            for (Declaration dec1 : decs) {
                Declaration dec = dec1;
                // check for duplications
                for (Declaration requiredDeclaration : this.requiredDeclarations) {
                    if (dec.equals(requiredDeclaration)) {
                        dec = null;
                        break;
                    }
                }
                if (dec != null) {
                    Declaration[] tmp = this.requiredDeclarations;
                    this.requiredDeclarations = new Declaration[tmp.length + 1];
                    System.arraycopy(tmp,
                            0,
                            this.requiredDeclarations,
                            0,
                            tmp.length);
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
        for (AlphaNodeFieldConstraint alphaConstraint : this.alphaConstraints) {
            alphaConstraint.replaceDeclaration(oldDecl, newDecl);
        }
        for (BetaNodeFieldConstraint betaConstraint : this.betaConstraints) {
            betaConstraint.replaceDeclaration(oldDecl, newDecl);
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
        result = PRIME * result + Arrays.hashCode( this.alphaConstraints );
        result = PRIME * result + Arrays.hashCode( this.betaConstraints );
        result = PRIME * result + Arrays.hashCode( this.requiredDeclarations );
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

    public abstract AbstractCompositeConstraint clone();

    /**
     * A context entry for composite restrictions
     */
    protected static class MultiFieldConstraintContextEntry
        implements
        ContextEntry {

        private static final long    serialVersionUID = 510l;

        public ContextEntry[]        alphas;
        public ContextEntry[]        betas;
        public ContextEntry          next;
        public InternalWorkingMemory workingMemory;
        public InternalFactHandle    handle;

        public MultiFieldConstraintContextEntry() {
        }

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

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            alphas  = (ContextEntry[])in.readObject();
            betas   = (ContextEntry[])in.readObject();
            next  = (ContextEntry)in.readObject();
            workingMemory  = (InternalWorkingMemory)in.readObject();
            handle  = (InternalFactHandle)in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(alphas);
            out.writeObject(betas);
            out.writeObject(next);
            out.writeObject(workingMemory);
            out.writeObject(handle);
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
            for (ContextEntry alpha : alphas) {
                if (alpha != null) {
                    alpha.updateFromFactHandle(workingMemory, handle);
                }
            }
            for (ContextEntry beta : betas) {
                beta.updateFromFactHandle(workingMemory, handle);
            }
        }

        public void updateFromTuple(InternalWorkingMemory workingMemory,
                                    LeftTuple tuple) {
            this.workingMemory = workingMemory;
            for (ContextEntry alpha : alphas) {
                if (alpha != null) {
                    alpha.updateFromTuple(workingMemory, tuple);
                }
            }
            for (ContextEntry beta : betas) {
                beta.updateFromTuple(workingMemory, tuple);
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
