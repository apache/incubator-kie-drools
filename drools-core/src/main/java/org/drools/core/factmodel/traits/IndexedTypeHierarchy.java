/*
 * Copyright 2015 JBoss Inc
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

package org.drools.core.factmodel.traits;

import org.drools.core.util.AbstractCodedHierarchyImpl;
import org.drools.core.util.HierNode;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;


/**
 * This class should be for testing purposes only
 * Use @see{TypeHierarchy} instead.
 * @param <T>
 */
public class IndexedTypeHierarchy<T> extends AbstractCodedHierarchyImpl<T> implements Externalizable {

    protected transient Map<T, HierNode<T>> cache;

    private BitSet bottom;
    private BitSet top;


    public IndexedTypeHierarchy() {
        super();
        top = new BitSet();
        cache = new HashMap<T, HierNode<T>>();
    }

    public IndexedTypeHierarchy( T topElement, BitSet topKey, T bottomElement, BitSet bottomKey ) {
        setTopCode( topKey );
        setBottomCode( bottomKey );
        cache = new HashMap<T, HierNode<T>>();
        addMember( topElement, topKey );
        addMember( bottomElement, bottomKey );

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

    protected HierNode<T> getNode( T name ) {
        return cache.get( name );
    }

    protected void add( HierNode<T> node ) {
        super.add( node );
        cache.put( node.getValue(), node );
    }


    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("*****************************************\n");

        int len = 0;
        for ( HierNode<T> node : getNodes() ) {
            len = Math.max( len, numBit( node.getBitMask() ) );
        }

        for ( HierNode<T> node : getNodes() ) {
            builder.append( node.toString(len) ).append("\n");
        }
        builder.append( "*****************************************\n" );
        builder.append( getSortedMap() ).append("\n");
        builder.append("*****************************************\n");
        return builder.toString();
    }

    protected HierNode<T> getNode( LatticeElement<T> name ) {
        return null;
    }

    @Override
    public void writeExternal( ObjectOutput objectOutput ) throws IOException {
        super.writeExternal( objectOutput );
    }

    @Override
    public void readExternal( ObjectInput objectInput ) throws IOException, ClassNotFoundException {
        super.readExternal( objectInput );
    }

}
