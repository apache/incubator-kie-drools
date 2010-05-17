/**
 *
 */
package org.drools.common;

import java.io.Externalizable;
import java.io.IOException;

import org.drools.marshalling.impl.MarshallerWriteContext;

public interface WorkingMemoryAction extends Externalizable {
    public static final int WorkingMemoryReteAssertAction = 1;
    public static final int DeactivateCallback = 2;
    public static final int PropagateAction = 3;
    public static final int LogicalRetractCallback = 4;
    public static final int WorkingMemoryReteExpireAction = 5;
    public static final int SignalProcessInstanceAction = 6;
    public static final int SignalAction = 7;
    
    
    public void execute(InternalWorkingMemory workingMemory);
    
    public void write(MarshallerWriteContext context) throws IOException;
}