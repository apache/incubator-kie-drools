package org.drools.core.reteoo;

import org.drools.core.common.Memory;

public interface SegmentNodeMemory extends Memory {

    long getNodePosMaskBit();
    void setNodePosMaskBit(long segmentPos);

    void setNodeDirtyWithoutNotify();
    void setNodeCleanWithoutNotify();
}
