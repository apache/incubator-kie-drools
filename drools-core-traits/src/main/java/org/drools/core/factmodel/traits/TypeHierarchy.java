/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.factmodel.traits;

import org.drools.core.util.AbstractBitwiseHierarchyImpl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.BitSet;
import java.util.Collection;

public abstract class TypeHierarchy<T,J extends LatticeElement<T>> extends AbstractBitwiseHierarchyImpl<T,J>
        implements TypeLattice<T>, Externalizable {

    private BitSet bottom;
    private BitSet top;

    public TypeHierarchy() {
        top = new BitSet();
    }

    @Override
    protected J getNode( T name ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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
/*        List<T> sorted = getSortedMembers();
        for ( T member : sorted ) {
            HierNode<T> node = null;
            // no index by value is preserved for performance reasons, so I need to look it up
            for ( HierNode<T> n : line.values() ) {
                if ( member.equals( n.getValue() ) ) {
                    node = n;
                    break;
                }
            }
            if ( node == null ) { throw new IllegalStateException( "Serious corruption: member node is no longer in the lattice" ); }
            sb.append( member ).append( " >>> " ).append( node.getBitMask() ).append( "\n" );
            sb.append("\t parents ").append( node.getParents() ).append( "\n ");
            sb.append( "\t children " ).append( node.getChildren() ).append( "\n ");
        }
*/        sb.append( "***************************************** \n" );
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

    public void addMember(T val, BitSet key) {
        add(  wrap( val, key ) );
    }

    protected abstract J wrap( T val, BitSet key );

    protected Collection<T> parentValues( J node ) {
        return null;
    }

    public Collection<T> children(T y) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Collection<T> descendants(T y) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Collection<T> children(BitSet key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Collection<T> descendants(BitSet key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Collection<T> ancestors( T x ) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Collection<T> ancestors(BitSet key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

/*
    public void removeMember(T val) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void removeMember(BitSet key) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Map<T, BitSet> getSortedMap() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean hasKey(BitSet key) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Collection<T> children(BitSet key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Collection<T> lowerDescendants(BitSet key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Collection<T> parents(T x) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Collection<T> parents(BitSet x) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Collection<T> upperBorder(BitSet key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Collection<T> immediateParents(BitSet key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isEmpty() {
        return false;
    }

    protected void add( HierNode<T> node ) {
    }

    protected Collection<HierNode<T>> getNodes() {
        return null;
    }

    public T getMember( BitSet key ) {
        return null;
    }
*/

}
