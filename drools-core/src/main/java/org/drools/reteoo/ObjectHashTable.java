package org.drools.reteoo;

import org.drools.common.InternalFactHandle;
import org.drools.spi.Tuple;
import org.drools.util.Iterator;

public interface ObjectHashTable {
    public Iterator iterator();
    public Iterator iterator(int hashCode);
    public Iterator iterator(ReteTuple tuple);
    
    public boolean add(InternalFactHandle handle, boolean checkExists);
    public boolean add(InternalFactHandle handle);
    public boolean remove(InternalFactHandle handle);
    public boolean contains(InternalFactHandle handle);
    
    public int size();
    
}
