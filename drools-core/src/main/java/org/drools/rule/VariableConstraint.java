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

package org.drools.rule;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.LeftTuple;
import org.drools.spi.AcceptsReadAccessor;
import org.drools.spi.Evaluator;
import org.drools.spi.InternalReadAccessor;
import org.drools.spi.Restriction;
import org.drools.time.Interval;

public class VariableConstraint extends MutableTypeConstraint
    implements
    AcceptsReadAccessor,    
    Externalizable {

    private static final long    serialVersionUID = 510l;

    private InternalReadAccessor fieldExtractor;
    private Restriction  restriction;

    public VariableConstraint() {
    }

    public VariableConstraint(final InternalReadAccessor fieldExtractor,
                              final Declaration declaration,
                              final Evaluator evaluator) {
        this.fieldExtractor = fieldExtractor;
        this.restriction = new VariableRestriction( fieldExtractor,
                                                    declaration,
                                                    evaluator );
    }

    public VariableConstraint(final InternalReadAccessor fieldExtractor,
                              final Restriction restriction) {
        this.fieldExtractor = fieldExtractor;
        this.restriction = restriction;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        fieldExtractor = (InternalReadAccessor) in.readObject();
        restriction = (Restriction) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( fieldExtractor );
        out.writeObject( restriction );
    }
    
    public void setReadAccessor(InternalReadAccessor readAccessor) {
        this.fieldExtractor = readAccessor;
    }     

    public Declaration[] getRequiredDeclarations() {
        return this.restriction.getRequiredDeclarations();
    }

    public void replaceDeclaration(Declaration oldDecl,
                                   Declaration newDecl) {
        this.restriction.replaceDeclaration( oldDecl,
                                             newDecl );
    }

    public InternalReadAccessor getFieldExtractor() {
        return this.fieldExtractor;
    }

    public Evaluator getEvaluator() {
        if ( this.restriction instanceof UnificationRestriction ) {
            return ((UnificationRestriction) this.restriction).getEvaluator();
        } else {
            return ((VariableRestriction) this.restriction).getEvaluator();
        }
    }
    
    public Restriction getRestriction() {
        return this.restriction;
    }

    public boolean isAllowed(final InternalFactHandle handle,
                             final InternalWorkingMemory workingMemory,
                             final ContextEntry context) {
        return this.restriction.isAllowed( this.fieldExtractor,
                                           handle,
                                           workingMemory,
                                           context );
    }

    public boolean isAllowedCachedLeft(final ContextEntry context,
                                       final InternalFactHandle handle) {
        return this.restriction.isAllowedCachedLeft( context,
                                                     handle );
    }

    public boolean isAllowedCachedRight(final LeftTuple tuple,
                                        final ContextEntry context) {
        return this.restriction.isAllowedCachedRight( tuple,
                                                      context );
    }
    
    public boolean isTemporal() {
        return this.restriction.isTemporal();
    }
    
    public Interval getInterval() {
        if ( this.restriction instanceof UnificationRestriction ) {
            return ((UnificationRestriction) this.restriction).getInterval();
        } else {
            return ((VariableRestriction) this.restriction).getInterval();
        }
    }

    public String toString() {
        return this.fieldExtractor + " " + this.restriction;
    } 

    public ContextEntry createContextEntry() {
        return this.restriction.createContextEntry();
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + this.fieldExtractor.hashCode();
        result = PRIME * result + this.restriction.hashCode();
        return result;
    }

    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || getClass() != object.getClass() ) {
            return false;
        }

        final VariableConstraint other = (VariableConstraint) object;

        return this.fieldExtractor.equals( other.fieldExtractor ) && this.restriction.equals( other.restriction );
    }

    public Object clone() {
        return new VariableConstraint( this.fieldExtractor,
                                       (VariableRestriction) this.restriction.clone() );
    }

}
