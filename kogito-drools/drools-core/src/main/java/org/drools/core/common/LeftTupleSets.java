package org.drools.core.common;

import org.drools.core.reteoo.LeftTuple;

public interface LeftTupleSets {
    LeftTuple getInsertFirst();

    LeftTuple getDeleteFirst();

    LeftTuple getUpdateFirst();

//    void resetInsert();
//
//    void resetDelete();
//
//    void resetUpdate();

    void resetAll();

    int insertSize();

    int deleteSize();

    int updateSize();

    boolean addInsert(LeftTuple leftTuple);

    boolean addDelete(LeftTuple leftTuple);

    boolean addUpdate(LeftTuple leftTuple);

    void removeInsert(LeftTuple leftTuple);

    void removeDelete(LeftTuple leftTuple);

    void removeUpdate(LeftTuple leftTuple);

    void addAllInserts(LeftTupleSets tupleSets);

    void addAllDeletes(LeftTupleSets tupleSets);

    void addAllUpdates(LeftTupleSets tupleSets);

    void addAll(LeftTupleSets source);

    LeftTupleSets takeAll();

//    void clear();

    boolean isEmpty();

    String toStringSizes();
}
