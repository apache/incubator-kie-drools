package org.drools.reteoo;

import java.util.ArrayList;
import java.util.List;

import org.drools.common.InternalFactHandle;
import org.drools.rule.Declaration;
import org.drools.spi.Activation;
import org.drools.spi.Tuple;
import org.drools.util.BaseEntry;
import org.drools.util.LinkedList;

public class ReteTuple extends BaseEntry
    implements
    Tuple {
    private static final long serialVersionUID = 320L;

    private int                      index;

    private final InternalFactHandle handle;

    private ReteTuple                parent;

    private long                     recency;    
    
    private int                      hashCode;    
    
    private boolean                  fieldIndexed;
    
    private int                      matches;    

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    public ReteTuple(final InternalFactHandle handle) {
        this.index = 0;
        this.parent = null;
        this.recency = handle.getRecency();
        this.handle = handle;
        this.hashCode = handle.hashCode();
    }

    public ReteTuple(final ReteTuple tuple) {
        this.index = tuple.index;
        this.parent = tuple.parent;
        this.recency = tuple.recency;
        this.handle = tuple.handle;
        this.hashCode = tuple.hashCode();
    }

    public ReteTuple(final ReteTuple parentTuple,
                     final InternalFactHandle handle) {
        this.index = parentTuple.index + 1;
        this.parent = parentTuple;
        this.recency = parentTuple.recency + handle.getRecency();
        this.handle = handle;
        this.hashCode = parentTuple.hashCode ^( handle.hashCode() * 31 );
    }

    public InternalFactHandle get(int index) {
        ReteTuple entry = this;
        while ( entry.index != index ) {
            entry = entry.parent;
        }
        return entry.handle;
    }        
    
    public boolean isFieldIndexed() {
        return fieldIndexed;
    }

    public void setIsFieldIndexHashCode(boolean fieldIndexed) {
        this.fieldIndexed = fieldIndexed;
    }        

    public int getMatches() {
        return matches;
    }

    public void setMatches(int matches) {
        this.matches = matches;
    }

    public InternalFactHandle getLastHandle() {
        return this.handle;
    }

    public InternalFactHandle get(Declaration declaration) {
        return get( declaration.getColumn().getIndex() );
    }

    public InternalFactHandle[] getFactHandles() {
        List list = new ArrayList();
        ReteTuple entry = this;
        while ( entry != null ) {
            list.add( entry.handle );
            entry = entry.parent;
        }

        return (InternalFactHandle[]) list.toArray( new InternalFactHandle[list.size()] );
    }

    public long getRecency() {
        return this.recency;
    }   
    
    public int hashCode() {
        return this.handle.hashCode();
    }
    
    /**
     * We use this equals method to avoid the cast
     * @param tuple
     * @return
     */
    public boolean equals(ReteTuple other) {
        // we know the object is never null and always of the  type ReteTuple
        if ( other == this ) {
            return true;
        }
        
        // A ReteTuple is  only the same if it has the same hashCode, factId and parent
        if ( this.hashCode != other.hashCode ) {
            return false;
        }
        
        if ( this.handle.getId() !=  other.handle.getId() ) {
            return false;
        }
        
        if( this.parent == null ) {
            return ( other.parent == null );
        } else {
            return this.parent.equals( other.parent );   
        }        
    }
    
    public boolean equals(Object object) {
        // we know the object is never null and always of the  type ReteTuple    
        return equals((ReteTuple)object);
    }
}
