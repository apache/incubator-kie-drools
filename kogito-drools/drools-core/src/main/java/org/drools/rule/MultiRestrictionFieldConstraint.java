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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.LeftTuple;
import org.drools.spi.AcceptsReadAccessor;
import org.drools.spi.InternalReadAccessor;
import org.drools.spi.ReadAccessor;
import org.drools.spi.Restriction;

public class MultiRestrictionFieldConstraint extends MutableTypeConstraint
    implements
    AcceptsReadAccessor {

    private static final long    serialVersionUID = 510l;

    private InternalReadAccessor readAccessor;

    private Restriction          restrictions;

    public MultiRestrictionFieldConstraint() {

    }

    public MultiRestrictionFieldConstraint(final InternalReadAccessor extractor,
                                           final Restriction restrictions) {
        this.readAccessor = extractor;
        this.restrictions = restrictions;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        readAccessor = (InternalReadAccessor) in.readObject();
        restrictions = (Restriction) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( readAccessor );
        out.writeObject( restrictions );
    }
    
    public void setReadAccessor(InternalReadAccessor readAccessor) {
        this.readAccessor = readAccessor;
    }        

    public ReadAccessor getFieldExtractor() {
        return this.readAccessor;
    }

    public Declaration[] getRequiredDeclarations() {
        return this.restrictions.getRequiredDeclarations();
    }

    public void replaceDeclaration(Declaration oldDecl,
                                   Declaration newDecl) {
        this.restrictions.replaceDeclaration( oldDecl,
                                              newDecl );
    }
    
    public boolean isTemporal() {
        return restrictions.isTemporal();
    }

    public String toString() {
        return "[MultiRestrictionConstraint fieldExtractor=" + this.readAccessor + " restrictions =" + this.restrictions + "]";
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * this.readAccessor.hashCode();
        result = PRIME * this.restrictions.hashCode();
        return result;
    }

    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }
        if ( object == null || object.getClass() != MultiRestrictionFieldConstraint.class ) {
            return false;
        }
        final MultiRestrictionFieldConstraint other = (MultiRestrictionFieldConstraint) object;

        return this.readAccessor.equals( other.readAccessor ) && this.restrictions.equals( other.restrictions );
    }

    public boolean isAllowed(final InternalFactHandle handle,
                             final InternalWorkingMemory workingMemory,
                             final ContextEntry context) {
        return this.restrictions.isAllowed( this.readAccessor,
                                            handle,
                                            workingMemory,
                                            context );
    }

    public ContextEntry createContextEntry() {
        return this.restrictions.createContextEntry();
    }

    public boolean isAllowedCachedLeft(final ContextEntry context,
                                       final InternalFactHandle handle) {
        return this.restrictions.isAllowedCachedLeft( context,
                                                      handle );
    }

    public boolean isAllowedCachedRight(final LeftTuple tuple,
                                        final ContextEntry context) {
        return this.restrictions.isAllowedCachedRight( tuple,
                                                       context );
    }

    public Object clone() {
        return new MultiRestrictionFieldConstraint( this.readAccessor,
                                                    (Restriction) this.restrictions.clone() );
    }

}
