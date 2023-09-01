package org.drools.core.reteoo;

import org.drools.core.util.AbstractHashTable.Index;
import org.drools.core.util.FastIterator;
import org.drools.core.util.Iterator;

public interface TupleMemory {

    default Index getIndex() {
        return null;
    }

    enum IndexType {
        NONE, EQUAL, COMPARISON, RANGE;

        public boolean isComparison() {
            return this == COMPARISON || this == RANGE;
        }
    }

    /**
     * The FactHandle is always the context fact and is necessary when the object being modified is in the both left and right
     * node memories. This is because the memory on the opposite side would not have yet memory.removeAdd the fact, so it
     * could potentially be in the wrong bucket. So the bucket matches check always checks to ignore the first facthandle if it's
     * the same as the context fact.
     */
    Tuple getFirst( Tuple tuple );
    
    void removeAdd( Tuple tuple );

    void add( Tuple tuple );

    void remove( Tuple tuple );

    boolean isIndexed();

    int size();

    Iterator<Tuple> iterator();
    
    FastIterator<Tuple> fastIterator();
    
    /**
     * Iterates the entire data structure, regardless of whether TupleMemory is hashed or not.
     * @return
     */
    FastIterator<Tuple> fullFastIterator();
    
    /**
     * Iterator that resumes from the current RightTuple, regardless of whether the TupleMemory is hashed or not 
     * @param tuple
     * @return
     */
    FastIterator<Tuple> fullFastIterator( Tuple tuple );

    Tuple[] toArray();

    IndexType getIndexType();

    void clear();
}
