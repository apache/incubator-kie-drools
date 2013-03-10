/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.factmodel.traits;

import org.drools.util.CodedHierarchyImpl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.BitSet;

public class TypeHierarchy<T> extends CodedHierarchyImpl<T> implements TypeLattice<T>, Externalizable {

    private BitSet bottom;
    private BitSet top;

    public TypeHierarchy() {
        top = new BitSet();
    }

    public TypeHierarchy( T topElement, BitSet topKey, T bottomElement, BitSet bottomKey ) {
        this.top = topKey;
        this.bottom = bottomKey;
        addMember( topElement, topKey );
        addMember( bottomElement, bottomKey );
    }

    @Override
    public void writeExternal( ObjectOutput objectOutput ) throws IOException {
        super.writeExternal( objectOutput );
        objectOutput.writeObject( bottom );
    }

    @Override
    public void readExternal( ObjectInput objectInput ) throws IOException, ClassNotFoundException {
        super.readExternal( objectInput );
        bottom = (BitSet) objectInput.readObject();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( super.toString() );
        sb.append("***************************************** \n");
        for ( T member : getSortedMembers() ) {
            sb.append( member ).append( " >>> " ).append( getCode( member ) ).append( "\n" );
            sb.append("\t parents ").append( getNode( member ).getParents() ).append( "\n ");
            sb.append( "\t children " ).append( getNode( member ).getChildren() ).append( "\n ");
        }
        sb.append( "***************************************** \n" );
        return sb.toString();
    }

    public BitSet getTopCode() {
        return top;
    }

    public BitSet getBottomCode() {
        return bottom;
    }

    public void setBottomCode( BitSet bottom ) {
        this.bottom = bottom;
    }

    public void setTopCode( BitSet top ) {
        this.top = top;
    }
}
