package org.drools.runtime.dataloader;

import java.util.Map;

import org.drools.runtime.pipeline.Feeder;
import org.drools.runtime.rule.FactHandle;

public interface WorkingMemoryDataLoader {
    Map<FactHandle, Object> insert(Object object);
}