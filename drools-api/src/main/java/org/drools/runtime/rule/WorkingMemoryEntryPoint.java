package org.drools.runtime.rule;

public interface WorkingMemoryEntryPoint {

    FactHandle insert(Object object);

    void retract(FactHandle handle);

    void update(FactHandle handle,
                Object object);
    
    WorkingMemoryEntryPoint getWorkingMemoryEntryPoint(String name);
}
