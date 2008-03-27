package org.drools.reteoo;

import java.util.ArrayList;
import java.util.List;

import org.drools.common.InternalWorkingMemory;
import org.drools.spi.PropagationContext;

public class MockRightTupleSink
    implements
    RightTupleSink {
    
    private final List        retracted        = new ArrayList();

    public void retractRightTuple(RightTuple rightTuple,
                                  PropagationContext context,
                                  InternalWorkingMemory workingMemory) {
        this.retracted.add( new Object[]{rightTuple, context, workingMemory} );

    }
    
    public List getRetracted() {
        return this.retracted;
    }    

}
