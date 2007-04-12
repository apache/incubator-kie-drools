package org.drools.spi;

import org.drools.WorkingMemory;
import org.drools.base.JavaFactRegistryEntry;

public interface JavaFact {
    public JavaFactRegistryEntry[] listWorkingMemories();

    public boolean register(final WorkingMemory workingMemory);

    public void unregisterAll();

    public boolean unregister(final WorkingMemory workingMemory);

    public boolean isRegistered(final WorkingMemory workingMemory);

    public int[] getChanges();
}
