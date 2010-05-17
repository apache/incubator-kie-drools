package org.drools.marshalling.impl;

import java.io.IOException;

import org.drools.common.WorkingMemoryAction;
import org.drools.common.RuleFlowGroupImpl.DeactivateCallback;
import org.drools.common.TruthMaintenanceSystem.LogicalRetractCallback;
import org.drools.process.instance.event.DefaultSignalManager.SignalAction;
import org.drools.process.instance.event.DefaultSignalManager.SignalProcessInstanceAction;
import org.drools.reteoo.PropagationQueuingNode.PropagateAction;
import org.drools.reteoo.ReteooWorkingMemory.WorkingMemoryReteAssertAction;
import org.drools.reteoo.ReteooWorkingMemory.WorkingMemoryReteExpireAction;

public class PersisterHelper {
    public static WorkingMemoryAction readWorkingMemoryAction(MarshallerReaderContext context) throws IOException, ClassNotFoundException {
        int type = context.readInt();
        switch(type) {
            case WorkingMemoryAction.WorkingMemoryReteAssertAction : {
                return new WorkingMemoryReteAssertAction(context);
            }
            case WorkingMemoryAction.DeactivateCallback : {
                return new DeactivateCallback(context);
            }
            case WorkingMemoryAction.PropagateAction : {
                return new PropagateAction(context);
            }
            case WorkingMemoryAction.LogicalRetractCallback : {
                return new LogicalRetractCallback(context);
            }
            case WorkingMemoryAction.WorkingMemoryReteExpireAction : {
                return new WorkingMemoryReteExpireAction(context);
            }
            case WorkingMemoryAction.SignalProcessInstanceAction : {
                return new SignalProcessInstanceAction(context);
            }
            case WorkingMemoryAction.SignalAction : {
                return new SignalAction(context);
            }
        }    
        return null;
    }
    
    public void write(MarshallerWriteContext context) throws IOException {
        
    }
}
