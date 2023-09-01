package org.drools.core.common;

import org.drools.core.phreak.PropagationEntry;

public interface WorkingMemoryAction extends PropagationEntry {
    short WorkingMemoryReteAssertAction  = 1;
    short DeactivateCallback             = 2;
    short PropagateAction                = 3;
    short LogicalRetractCallback         = 4;
    short WorkingMemoryReteExpireAction  = 5;
    short SignalProcessInstanceAction    = 6;
    short SignalAction                   = 7;
    short WorkingMemoryBehahviourRetract = 8;
}
