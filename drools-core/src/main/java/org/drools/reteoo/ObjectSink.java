package org.drools.reteoo;

import org.drools.FactException;
import org.drools.spi.PropagationContext;

public interface ObjectSink
{
    void assertObject(Object object,
                      FactHandleImpl handle,
                      PropagationContext context,
                      WorkingMemoryImpl workingMemory) throws FactException;

    void retractObject(FactHandleImpl handle,
                       PropagationContext context,
                       WorkingMemoryImpl workingMemory) throws FactException;    
}
