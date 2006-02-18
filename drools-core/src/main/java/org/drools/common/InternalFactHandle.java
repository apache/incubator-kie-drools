package org.drools.common;

import org.drools.FactHandle;

public interface InternalFactHandle extends FactHandle {
    public long getId();
    
    public long getRecency();
    
}
