package org.drools.traits.core.factmodel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

public class ExternalizableLinkedHashMap<K extends Comparable,T> extends LinkedHashMap<K,T> implements Externalizable {

    public ExternalizableLinkedHashMap() {

    }

    public void writeExternal( ObjectOutput out ) throws IOException {
        out.writeInt( this.size() );
        List<K> keys = new ArrayList<>( this.keySet() );
        Collections.sort( keys );
        for ( K k : keys ) {
            out.writeObject( k );
            out.writeObject( this.get( k ) );
        }

    }

    public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
        int n = in.readInt();
        for ( int j = 0; j < n; j++ ) {
            K k = (K) in.readObject();
            T t = (T) in.readObject();
            this.put( k, t );
        }
    }
}
