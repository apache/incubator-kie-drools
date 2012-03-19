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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.LeftTuple;
import org.drools.spi.Restriction;

public abstract class AbstractCompositeRestriction
    implements
    Restriction {

    private static final long             serialVersionUID = 510l;

    protected Restriction[]         restrictions;

    public AbstractCompositeRestriction() {
    }

    public AbstractCompositeRestriction(final Restriction[] restriction) {
        this.restrictions = restriction;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        restrictions    = (Restriction[])in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(restrictions);
    }

    public Declaration[] getRequiredDeclarations() {
        // Iterate all restrictions building up a unique list of declarations
        // No need to cache, as this should only be called once at build time
        final Set<Declaration> set = new HashSet<Declaration>();
        for ( int i = 0, ilength = this.restrictions.length; i < ilength; i++ ) {
            final Declaration[] declarations = this.restrictions[i].getRequiredDeclarations();
            for ( int j = 0, jlength = declarations.length; j < jlength; j++ ) {
                if( declarations[j].getExtractor() != null ){
                    set.add( declarations[j] );
                }
            }
        }

        return set.toArray( new Declaration[set.size()] );
    }

    public void replaceDeclaration(Declaration oldDecl,
                                   Declaration newDecl) {
        for (Restriction restriction : this.restrictions) {
            restriction.replaceDeclaration(oldDecl, newDecl);
        }
    }
    
    public boolean isTemporal() {
        boolean temporal = false;
        for( Restriction r : restrictions ) {
            if( r.isTemporal() ) {
                temporal = true;
                break;
            }
        }
        return temporal;
    }

    private static int hashCode(final Object[] array) {
        final int PRIME = 31;
        if ( array == null ) {
            return 0;
        }
        int result = 1;
        for (Object anArray : array) {
            result = PRIME * result + (anArray == null ? 0 : anArray.hashCode());
        }
        return result;
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + AbstractCompositeRestriction.hashCode( this.restrictions );
        return result;
    }

    public boolean equals(final Object obj) {
        if ( this == obj ) {
            return true;
        }

        if ( obj == null || obj instanceof AbstractCompositeRestriction ) {
            return false;
        }

        final AbstractCompositeRestriction other = (AbstractCompositeRestriction) obj;
        return Arrays.equals(this.restrictions, other.restrictions);
    }

    public ContextEntry createContextEntry() {
        return new CompositeContextEntry( this.restrictions );
    }

    public abstract AbstractCompositeRestriction clone();

    public static class CompositeContextEntry
        implements
        ContextEntry {

        private static final long serialVersionUID = 510l;

        public ContextEntry[]     contextEntries;

        private ContextEntry      entry;

        public CompositeContextEntry() {
        }

        public CompositeContextEntry(final Restriction[] restrictions) {
            contextEntries = new ContextEntry[restrictions.length];
            for ( int i = 0; i < restrictions.length; i++ ) {
                contextEntries[i] = restrictions[i].createContextEntry();
            }
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            contextEntries  = (ContextEntry[])in.readObject();
            entry  = (ContextEntry)in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(contextEntries);
            out.writeObject(entry);
        }
        public ContextEntry getNext() {
            return this.entry;
        }

        public void setNext(final ContextEntry entry) {
            this.entry = entry;
        }

        public void updateFromFactHandle(final InternalWorkingMemory workingMemory,
                                         final InternalFactHandle handle) {
            for ( int i = 0, length = this.contextEntries.length; i < length; i++ ) {
                this.contextEntries[i].updateFromFactHandle( workingMemory,
                                                             handle );
            }
        }

        public void updateFromTuple(final InternalWorkingMemory workingMemory,
                                    final LeftTuple tuple) {
            for ( int i = 0, length = this.contextEntries.length; i < length; i++ ) {
                this.contextEntries[i].updateFromTuple( workingMemory,
                                                        tuple );
            }
        }

        public void resetTuple() {
            for ( int i = 0, length = this.contextEntries.length; i < length; i++ ) {
                this.contextEntries[i].resetTuple();
            }
        }

        public void resetFactHandle() {
            for ( int i = 0, length = this.contextEntries.length; i < length; i++ ) {
                this.contextEntries[i].resetFactHandle();
            }
        }

    }

}
