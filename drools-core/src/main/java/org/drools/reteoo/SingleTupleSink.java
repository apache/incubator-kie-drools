package org.drools.reteoo;

import org.drools.common.InternalWorkingMemory;
import org.drools.spi.PropagationContext;

public class SingleTupleSink implements TupleSink {
    private TupleSink sink;
    public SingleTupleSink(TupleSink sink) {
        this.sink = sink;
    }
    
    public void assertTuple(ReteTuple tuple,
                            PropagationContext context,
                            InternalWorkingMemory workingMemory) {
        sink.assertTuple( tuple, context, workingMemory );
        
    }

    public void modifyTuple(ReteTuple tuple,
                            PropagationContext context,
                            InternalWorkingMemory workingMemory) {
        sink.modifyTuple( tuple, context, workingMemory );
        
    }

    public void retractTuple(ReteTuple tuple,
                             PropagationContext context,
                             InternalWorkingMemory workingMemory) {
        this.retractTuple( tuple, context, workingMemory );
        
    }


}
