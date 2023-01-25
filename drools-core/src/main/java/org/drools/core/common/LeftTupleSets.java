package org.drools.core.common;

import org.drools.core.reteoo.LeftTuple;

public interface LeftTupleSets extends TupleSets<LeftTuple> {
    @Override
    LeftTuple getInsertFirst();
    @Override
    LeftTuple getDeleteFirst();
    @Override
    LeftTuple getUpdateFirst();

    boolean addInsert(LeftTuple leftTuple);
    boolean addDelete(LeftTuple leftTuple);
    boolean addUpdate(LeftTuple leftTuple);

    void removeInsert(LeftTuple leftTuple);
    void removeDelete(LeftTuple leftTuple);
    void removeUpdate(LeftTuple leftTuple);

    void addAll(TupleSets<LeftTuple> source);

    void addTo(TupleSets<LeftTuple>target);

    LeftTupleSets takeAll();

    LeftTuple getNormalizedDeleteFirst();
}
