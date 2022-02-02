package org.drools.core.impl;

import java.util.Collection;

import org.drools.core.WorkingMemoryEntryPoint;

public interface EntryPointsManager {
    WorkingMemoryEntryPoint getDefaultEntryPoint();

    Collection<WorkingMemoryEntryPoint> getEntryPoints();

    WorkingMemoryEntryPoint getEntryPoint(String name);
}
