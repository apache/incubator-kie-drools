package org.drools.common;

import org.drools.RuleBase;
import org.drools.WorkingMemoryEntryPoint;

public interface InternalWorkingMemoryEntryPoint extends WorkingMemoryEntryPoint {
    ObjectTypeConfigurationRegistry getObjectTypeConfigurationRegistry();
    RuleBase getRuleBase();
}
