package org.drools.common;

import org.drools.WorkingMemoryEntryPoint;

public interface InternalWorkingMemoryEntryPoint extends WorkingMemoryEntryPoint {
    ObjectTypeConfigurationRegistry getObjectTypeConfigurationRegistry();
}
