package org.drools.core.common;

import org.drools.core.RuleBaseConfiguration;

public interface MemoryFactory<T extends Memory> {
    int getMemoryId();
    
    T createMemory(RuleBaseConfiguration config, ReteEvaluator reteEvaluator);
}
