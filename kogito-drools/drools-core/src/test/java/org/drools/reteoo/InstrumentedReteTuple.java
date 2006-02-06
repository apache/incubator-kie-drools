package org.drools.reteoo;

import org.drools.FactHandle;
import org.drools.WorkingMemory;

public class InstrumentedReteTuple extends ReteTuple {
    public InstrumentedReteTuple(ReteTuple left,
                                 FactHandle handle) {
        super( left,
               (FactHandleImpl) handle );
    }

    public InstrumentedReteTuple(int column,
                                 FactHandle handle,
                                 WorkingMemory workingMemory) {
        super( column,
               (FactHandleImpl) handle,
               (WorkingMemoryImpl) workingMemory );
    }
}
