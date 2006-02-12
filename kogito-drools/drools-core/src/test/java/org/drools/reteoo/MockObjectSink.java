package org.drools.reteoo;

import java.util.ArrayList;
import java.util.List;

import org.drools.spi.PropagationContext;

public class MockObjectSink
    implements
    ObjectSink {
    private List                asserted  = new ArrayList();
    private List                retracted = new ArrayList();

    public void assertObject(FactHandleImpl handle,
                             PropagationContext context,
                             WorkingMemoryImpl workingMemory) {
        this.asserted.add( new Object[]{handle, context, workingMemory} );
    }

    public void retractObject(FactHandleImpl handle,
                              PropagationContext context,
                              WorkingMemoryImpl workingMemory) {
        this.retracted.add( new Object[]{handle, context, workingMemory} );
    }

    public List getAsserted() {
        return this.asserted;
    }

    public List getRetracted() {
        return this.retracted;
    }

    public void modifyObject(FactHandleImpl handle,
                             PropagationContext context,
                             WorkingMemoryImpl workingMemory) {
        // TODO Auto-generated method stub
        
    }
}
