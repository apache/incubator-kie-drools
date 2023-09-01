package org.drools.core.reteoo;

import org.drools.base.common.NetworkNode;

public interface LeftTupleNode extends NetworkNode {
    int getPathIndex();

    LeftTupleSource getLeftTupleSource();

    LeftTupleSinkPropagator getSinkPropagator();

    int getObjectCount();

    void setObjectCount(int count);
}
