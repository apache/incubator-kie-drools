/**
 * 
 */
package org.drools.marshalling.impl;

import org.drools.reteoo.Sink;

public class RightTupleKey {
    private final int  id;
    private final Sink sink;

    public RightTupleKey(int id,
                         Sink sink) {
        super();
        this.id = id;
        this.sink = sink;
    }

    public int getId() {
        return id;
    }

    public Sink getSink() {
        return sink;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + ((sink!=null) ? sink.getId() : 17 );
        return result;
    }

    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        
        final RightTupleKey other = (RightTupleKey) obj;
        if ( id != other.id ) return false;
        if ( sink == null ) {
            if ( other.sink != null ) return false;
        } else if ( sink.getId() != other.sink.getId() ) return false;
        return true;
    }
    
    @Override
    public String toString() {
        return "RightTupleKey( id="+id+" sink="+sink+" )";
    }

}