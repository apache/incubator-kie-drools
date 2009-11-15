package org.drools.reteoo;

import org.drools.util.Entry;
import org.drools.util.Iterator;

public interface RightTupleMemory {
    public RightTuple getFirst(LeftTuple leftTuple);

    public RightTuple getLast(LeftTuple leftTuple);

    public void add(RightTuple rightTuple);

    public void remove(RightTuple rightTuple);

    public boolean contains(RightTuple rightTuple);
    
    public Iterator iterator();

    public boolean isIndexed();
    
    public Entry[] toArray();    

    public int size();
}
