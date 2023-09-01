package org.drools.core.base;

import org.drools.core.common.InternalFactHandle;
import org.kie.api.runtime.rule.FactHandle;


public class QueryRowWithSubruleIndex {
    private FactHandle[] handles;
    private int subruleIndex;
    
    public QueryRowWithSubruleIndex(FactHandle[] handles,
                                    int subruleIndex) {
        this.handles = handles;
        this.subruleIndex = subruleIndex;
    }

    public FactHandle[] getHandles() {
        return handles;
    }

    public int getSubruleIndex() {
        return subruleIndex;
    }                
}
