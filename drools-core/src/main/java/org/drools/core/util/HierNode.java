/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.util;

import org.drools.core.factmodel.traits.LatticeElement;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;


public class HierNode<T> implements LatticeElement<T>,Comparable<HierNode<T>>, Externalizable {

    public T value;
    public BitSet bitMask = null;
    public List<HierNode<T>> parents = new ArrayList<HierNode<T>>();
    public List<HierNode<T>> children = new ArrayList<HierNode<T>>();


    public HierNode() {

    }

    public HierNode( T value ) {
        this.value = value;
    }

    protected HierNode( HierNode<T> xt ) {
        value = xt.getValue();
        bitMask = xt.getBitMask();
        parents.addAll( xt.getParents() );
        children.addAll( xt.getChildren() );
    }

    public HierNode( BitSet key ) {
        bitMask = key;
    }

    public HierNode( T val, BitSet key ) {
        this.value = val;
        this.bitMask = key;
    }

    public T getValue() {
        return value;
    }

    public BitSet getBitMask() {
        return bitMask;
    }

    public void setBitMask( BitSet bitMask ) {
        if ( this.bitMask == null ) {
            this.bitMask = bitMask;
        } else {
            this.bitMask.clear();
            this.bitMask.or( bitMask );
        }
    }

    public Collection<HierNode<T>> getParents() {
        return parents;
    }

    public Collection<HierNode<T>> getChildren() {
        return children;
    }

    public void addChild( HierNode<T> node ) {
        children.add( node );
    }

    public void addParent( HierNode<T> node ) {
        parents.add( node );
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HierNode<T> hierNode = (HierNode<T>) o;

        if (!bitMask.equals(hierNode.bitMask)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return bitMask.hashCode();
    }

    @Override
    public String toString() {
        return toString( bitMask != null ? bitMask.length() : 0 );
    }

    public String toString( int len ) {
        return value + "[ " +
               ( ( bitMask != null ) ? toBinaryString( bitMask, len ) : "n/a" )
               + "]";
    }

    protected String toBinaryString( BitSet mask, int len ) {
        StringBuilder sb = new StringBuilder();
        for ( int j = len - 1; j >= 0; j-- ) {
            sb.append( mask.get( j ) ? "1 " : "0 " );
        }
        return sb.toString();
    }

    public int compareTo( HierNode<T> hierNode ) {
        BitSet yset = hierNode.bitMask;
        int lx = bitMask.length();
        int ly = yset.length();
        int l = lx > ly ? lx : ly;

        for ( int j = l; j >= 0; j-- ) {
            boolean x = bitMask.get( j );
            boolean y = yset.get( j );
            if ( x && ! y ) {
                return 1;
            }
            if ( y && ! x ) {
                return -1;
            }
        }

        return 0;
    }

    public void setValue( T value ) {
        this.value = value;
    }

    public void writeExternal( ObjectOutput out ) throws IOException {
        out.writeObject( value );
        out.writeObject( bitMask );
        out.writeObject( parents );
        out.writeObject( children );
    }

    public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
        value = (T) in.readObject();
        bitMask = (BitSet) in.readObject();
        parents = (List<HierNode<T>>) in.readObject();
        children = (List<HierNode<T>>) in.readObject();
    }

}
