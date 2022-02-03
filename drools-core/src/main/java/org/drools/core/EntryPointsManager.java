package org.drools.core;

import java.util.Collection;

public interface EntryPointsManager {
    WorkingMemoryEntryPoint getDefaultEntryPoint();

    Collection<WorkingMemoryEntryPoint> getEntryPoints();

    WorkingMemoryEntryPoint getEntryPoint(String name);
}
