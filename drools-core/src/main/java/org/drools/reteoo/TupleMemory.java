package org.drools.reteoo;

import org.drools.common.InternalFactHandle;
import org.drools.util.Entry;
import org.drools.util.Iterator;

public interface TupleMemory {
    public Iterator iterator();

    public Iterator iterator(InternalFactHandle handle);

    public void add(ReteTuple tuple);

    public ReteTuple remove(ReteTuple tuple);

    public boolean contains(ReteTuple tuple);

    public boolean isIndexed();

    public int size();

    public Entry[] getTable();
    
    public Entry[] toArray();

}
