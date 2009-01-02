package org.drools.common;

import org.drools.RuleBase;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;

public interface InternalWorkingMemoryEntryPoint extends WorkingMemoryEntryPoint {
    ObjectTypeConfigurationRegistry getObjectTypeConfigurationRegistry();
    RuleBase getRuleBase();
}
