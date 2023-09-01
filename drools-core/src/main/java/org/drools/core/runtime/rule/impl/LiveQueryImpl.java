package org.drools.core.runtime.rule.impl;

import org.kie.api.runtime.rule.FactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.kie.api.runtime.rule.LiveQuery;

public class LiveQueryImpl
    implements
    LiveQuery {
    InternalWorkingMemory wm;
    InternalFactHandle    factHandle;

    public LiveQueryImpl(InternalWorkingMemory wm,
                         FactHandle factHandle) {
        this.wm = wm;
        this.factHandle = (InternalFactHandle) factHandle;
    }

    public void close() {
        this.wm.closeLiveQuery(this.factHandle);
    }

}
