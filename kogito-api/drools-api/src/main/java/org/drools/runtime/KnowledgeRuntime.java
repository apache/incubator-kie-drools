package org.drools.runtime;

import org.drools.event.KnowledgeRuntimeEventManager;
import org.drools.runtime.process.ProcessRuntime;
import org.drools.runtime.rule.WorkingMemory;

public interface KnowledgeRuntime extends WorkingMemory, ProcessRuntime, KnowledgeRuntimeEventManager {

}
