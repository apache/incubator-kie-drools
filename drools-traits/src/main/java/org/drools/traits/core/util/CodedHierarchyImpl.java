package org.drools.traits.core.util;

import java.io.Externalizable;
import java.util.HashMap;
import java.util.Map;

public class CodedHierarchyImpl<T> extends AbstractCodedHierarchyImpl<T> implements Externalizable {

    protected transient Map<T, HierNode<T>> cache = new HashMap<>();

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
            builder.append( node.toString( len ) ).append("\n");
        }
        builder.append( "*****************************************\n" );
        builder.append( getSortedMap() ).append("\n");
        builder.append("*****************************************\n");
        return builder.toString();
    }


}
