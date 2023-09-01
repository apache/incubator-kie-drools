package org.drools.core.reteoo;

import org.drools.core.common.BaseNode;

import java.io.Externalizable;

public interface LeftTupleSinkPropagator
    extends
    Externalizable {
    
    BaseNode getMatchingNode(BaseNode candidate);

    LeftTupleSinkNode getFirstLeftTupleSink();

    LeftTupleSinkNode getLastLeftTupleSink();
    
    LeftTupleSink[] getSinks();
    
    int size();
}
