package org.drools.runtime.dataloader;

import java.util.Map;

import org.drools.runtime.rule.FactHandle;

public interface StatefulKnowledgeSessionDataLoader {
    Map<FactHandle, Object> insert(Object object);
}