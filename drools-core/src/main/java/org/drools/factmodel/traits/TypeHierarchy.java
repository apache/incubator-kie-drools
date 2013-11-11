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

import org.drools.core.util.HierNode;
import org.drools.util.AbstractBitwiseHierarchyImpl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;

//public class TypeHierarchy<T> extends AbstractCodedHierarchyImpl<T> implements TypeLattice<T>, Externalizable {
public class TypeHierarchy<T,J extends LatticeElement<T>> extends AbstractBitwiseHierarchyImpl<T,J>
        implements TypeLattice<LatticeElement<T>>, Externalizable {

    private BitSet bottom;
    private BitSet top;
    Collection<LatticeElement<T>> mostSpecificTraits = new LinkedList<LatticeElement<T>>();


    public TypeHierarchy() {
        top = new BitSet();
    }

//    public TypeHierarchy( T topElement, BitSet topKey, T bottomElement, BitSet bottomKey ) {
//        this.top = topKey;
//        this.bottom = bottomKey;
//        addMember( topElement, topKey );
//        addMember( bottomElement, bottomKey );
//    }

    @Override
    protected J getNode(LatticeElement<T> name) {
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

//    @Override
//    protected HierNode<T> getNode( T name ) {
//        throw new UnsupportedOperationException( "Concrete Type lattices should be indexed by key (BitSet), not by value" );
//    }

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

    public void updateMostSpecificTrait(LatticeElement<T> val)
    {
        BitSet tmp = (BitSet)val.getBitMask().clone();
        int size = mostSpecificTraits.size();
        boolean addIt = true;
        Collection<LatticeElement<T>> tmpMost = new LinkedList<LatticeElement<T>>(mostSpecificTraits);
        for(LatticeElement<T> node : tmpMost)
        {
            if( superset( tmp, node.getBitMask() ) > 0 )
            {
                mostSpecificTraits.remove( node );
            }
            else if(superset( node.getBitMask(), tmp ) > 0)
            {
                addIt = false;
                break;
            }
        }
        if( size > mostSpecificTraits.size() || addIt )
            mostSpecificTraits.add( val );
    }

    public void addMember(LatticeElement<T> val, BitSet key) {
        add( (J) val );
    }

    public Collection<LatticeElement<T>> children(LatticeElement<T> y) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Collection<LatticeElement<T>> descendants(LatticeElement<T> y) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Collection<LatticeElement<T>> children(BitSet key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Collection<LatticeElement<T>> descendants(BitSet key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Collection<LatticeElement<T>> ancestors(LatticeElement<T> x) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Collection<LatticeElement<T>> ancestors(BitSet key) {
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
