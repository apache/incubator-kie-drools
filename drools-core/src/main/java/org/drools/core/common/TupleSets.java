package org.drools.core.common;

import org.drools.core.reteoo.Tuple;

public interface TupleSets<T extends Tuple> {
    T getInsertFirst();
    T getDeleteFirst();
    T getUpdateFirst();

    int getInsertSize();

    void resetAll();

    /**
     * clear also ensures all contained LeftTuples are cleared
     * reset does not touch any contained tuples
     */
    void clear();

    boolean addInsert(T leftTuple);
    boolean addDelete(T leftTuple);
    boolean addUpdate(T leftTuple);

    void removeInsert(T leftTuple);
    void removeDelete(T leftTuple);
    void removeUpdate(T leftTuple);

    void addAll(TupleSets<T> source);

    void addTo(TupleSets<T> target);

    TupleSets<T> takeAll();

    boolean isEmpty();

    String toStringSizes();

    T getNormalizedDeleteFirst();
    boolean addNormalizedDelete(T leftTuple);
}
