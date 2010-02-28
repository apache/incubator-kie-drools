package org.drools.reteoo;

import org.drools.common.InternalWorkingMemory;
import org.drools.spi.PropagationContext;

public interface RightTupleSink extends Sink {

    public short getType();

    public void retractRightTuple(final RightTuple rightTuple,
                                  final PropagationContext context,
                                  final InternalWorkingMemory workingMemory);
    
    public void modifyRightTuple(final RightTuple rightTuple,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory);    
}
