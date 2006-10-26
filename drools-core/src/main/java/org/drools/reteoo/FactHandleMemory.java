package org.drools.reteoo;

import org.drools.common.InternalFactHandle;
import org.drools.util.Entry;
import org.drools.util.Iterator;

public interface FactHandleMemory {
    public Iterator iterator();

    public Iterator iterator(ReteTuple tuple);

    public boolean add(InternalFactHandle handle,
                       boolean checkExists);

    public boolean add(InternalFactHandle handle);

    public boolean remove(InternalFactHandle handle);

    public boolean contains(InternalFactHandle handle);

    public boolean isIndexed();

    public int size();

    public Entry[] getTable();

}
