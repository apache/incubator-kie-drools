package org.drools.reteoo;

import org.drools.util.Entry;
import org.drools.util.Iterator;

public interface LeftTupleMemory {
    public Iterator iterator();

    public LeftTuple getFirst(RightTuple rightTuple);

    public void add(LeftTuple tuple);

    public void remove(LeftTuple leftTuple);

    public boolean contains(LeftTuple leftTuple);

    public boolean isIndexed();

    public int size();

    //    public Entry[] getTable();

    public Entry[] toArray();

}
