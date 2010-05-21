package org.drools.reteoo;

import org.drools.common.InternalFactHandle;
import org.drools.core.util.Entry;
import org.drools.core.util.Iterator;

public interface RightTupleMemory {
    public RightTuple getFirst(LeftTuple leftTuple, InternalFactHandle factHandle);
    
    public RightTuple getFirst(RightTuple rightTuple);

    public void add(RightTuple rightTuple);

    public void remove(RightTuple rightTuple);

    public boolean contains(RightTuple rightTuple);
    
    public Iterator iterator();

    public boolean isIndexed();

    public Entry[] toArray();

    public int size();
}
