package org.drools.runtime.rule.impl;

import org.drools.FactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.rule.LiveQuery;

public class LiveQueryImpl
    implements
    LiveQuery {
    ReteooWorkingMemory wm;
    InternalFactHandle  factHandle;

    public LiveQueryImpl(ReteooWorkingMemory wm,
                         FactHandle factHandle) {
        this.wm = wm;
        this.factHandle = (InternalFactHandle) factHandle;
    }

    public void close() {
        this.wm.closeLiveQuery( this.factHandle );
    }

}
