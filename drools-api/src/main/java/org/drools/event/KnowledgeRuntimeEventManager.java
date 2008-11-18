package org.drools.event;

import org.drools.event.process.ProcessEventManager;
import org.drools.event.rule.WorkingMemoryEventManager;

public interface KnowledgeRuntimeEventManager extends WorkingMemoryEventManager, ProcessEventManager {

}
