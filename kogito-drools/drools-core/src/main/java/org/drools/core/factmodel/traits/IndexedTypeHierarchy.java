package org.drools.core.factmodel.traits;

import org.drools.core.factmodel.traits.TypeHierarchy;

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
public class IndexedTypeHierarchy<T> extends TypeHierarchy<T> implements Externalizable {

    protected transient Map<T, HierNode<T>> cache;

    public IndexedTypeHierarchy() {
        super();
        cache = new HashMap<T, HierNode<T>>();
    }

    public IndexedTypeHierarchy( T topElement, BitSet topKey, T bottomElement, BitSet bottomKey ) {
        setTopCode( topKey );
        setBottomCode( bottomKey );
        cache = new HashMap<T, HierNode<T>>();
        addMember( topElement, topKey );
        addMember( bottomElement, bottomKey );

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
            builder.append( node.toString( len ) ).append("\n");
        }
        builder.append( "*****************************************\n" );
        builder.append( getSortedMap() ).append("\n");
        builder.append("*****************************************\n");
        return builder.toString();
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
