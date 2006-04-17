package org.drools.reteoo;

import org.drools.FactHandle;

public class InstrumentedReteTuple extends ReteTuple {
    public InstrumentedReteTuple(ReteTuple left,
                                 FactHandle handle) {
        super( left,
               (FactHandleImpl) handle );
    }

    public InstrumentedReteTuple(FactHandle handle) {
        super( (FactHandleImpl) handle );
    }
}
