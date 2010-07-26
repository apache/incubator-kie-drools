/**
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

import org.drools.RuntimeDroolsException;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.LeftTuple;
import org.drools.rule.ReturnValueRestriction.ReturnValueContextEntry;
import org.drools.runtime.rule.Evaluator;
import org.drools.spi.AcceptsReadAccessor;
import org.drools.spi.InternalReadAccessor;
import org.drools.spi.ReturnValueExpression;

public class ReturnValueConstraint extends MutableTypeConstraint
    implements
    AcceptsReadAccessor,
    Externalizable {

    private static final long      serialVersionUID = 400L;

    private InternalReadAccessor   readAccessor;
    private ReturnValueRestriction restriction;

    public ReturnValueConstraint() {
        this( null,
              null );
    }

    public ReturnValueConstraint(final InternalReadAccessor fieldExtractor,
                                 final ReturnValueRestriction restriction) {
        this.readAccessor = fieldExtractor;
        this.restriction = restriction;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        readAccessor = (InternalReadAccessor) in.readObject();
        restriction = (ReturnValueRestriction) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( readAccessor );
        out.writeObject( restriction );
    }
    
    public void setReadAccessor(InternalReadAccessor readAccessor) {
        this.readAccessor = readAccessor;
    }    

    public Declaration[] getRequiredDeclarations() {
        return this.restriction.getRequiredDeclarations();
    }

    public void replaceDeclaration(Declaration oldDecl,
                                   Declaration newDecl) {
        this.restriction.replaceDeclaration( oldDecl,
                                             newDecl );
    }

    public void setReturnValueExpression(final ReturnValueExpression expression) {
        this.restriction.setReturnValueExpression( expression );
    }

    public ReturnValueExpression getExpression() {
        return this.restriction.getExpression();
    }

    public Evaluator getEvaluator() {
        return this.restriction.getEvaluator();
    }
    
    public boolean isTemporal() {
        return this.restriction.isTemporal();
    }

    public String toString() {
        return "[ReturnValueConstraint fieldExtractor=" + this.readAccessor + " evaluator=" + getEvaluator() + " declarations=" + getRequiredDeclarations() + "]";
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + this.readAccessor.hashCode();
        result = PRIME * result + this.restriction.hashCode();
        return result;
    }

    public boolean equals(final Object object) {
        if ( object == this ) {
            return true;
        }

        if ( object == null || object.getClass() != ReturnValueConstraint.class ) {
            return false;
        }

        final ReturnValueConstraint other = (ReturnValueConstraint) object;

        return this.readAccessor.equals( other.readAccessor ) && this.restriction.equals( other.restriction );
    }

    public ContextEntry createContextEntry() {
        return this.restriction.createContextEntry();
    }

    public boolean isAllowed(final InternalFactHandle handle,
                             final InternalWorkingMemory workingMemory,
                             final ContextEntry context) {
        try {
            return this.restriction.isAllowed( this.readAccessor,
                                               handle,
                                               null,
                                               workingMemory,
                                               context );
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( "Exception executing ReturnValue constraint " + this.restriction + " : " + e.getMessage(),
                                              e );
        }
    }

    public boolean isAllowedCachedLeft(final ContextEntry context,
                                       final InternalFactHandle handle) {
        try {
            final ReturnValueContextEntry ctx = (ReturnValueContextEntry) context;
            return this.restriction.isAllowed( this.readAccessor,
                                               handle,
                                               ctx.getTuple(),
                                               ctx.getWorkingMemory(),
                                               ctx );
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( "Exception executing ReturnValue constraint " + this.restriction + " : " + e.getMessage(),
                                              e );
        }
    }

    public boolean isAllowedCachedRight(final LeftTuple tuple,
                                        final ContextEntry context) {
        try {
            final ReturnValueContextEntry ctx = (ReturnValueContextEntry) context;
            return this.restriction.isAllowed( this.readAccessor,
                                               ctx.getHandle(),
                                               tuple,
                                               ctx.getWorkingMemory(),
                                               ctx );
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( "Exception executing ReturnValue constraint " + this.restriction + " : " + e.getMessage(),
                                              e );
        }
    }

    public Object clone() {
        return new ReturnValueConstraint( this.readAccessor,
                                          (ReturnValueRestriction) this.restriction.clone() );
    }

}
