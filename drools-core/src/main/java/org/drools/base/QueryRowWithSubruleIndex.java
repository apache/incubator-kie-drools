package org.drools.base;

import org.drools.common.InternalFactHandle;


public class QueryRowWithSubruleIndex {
    private InternalFactHandle[] handles;
    private int subruleIndex;
    
    public QueryRowWithSubruleIndex(InternalFactHandle[] handles,
                                    int subruleIndex) {
        this.handles = handles;
        this.subruleIndex = subruleIndex;
    }

    public InternalFactHandle[] getHandles() {
        return handles;
    }

    public int getSubruleIndex() {
        return subruleIndex;
    }                
}
