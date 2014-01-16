package org.drools.command;

import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;

public interface WorkingMemoryEntryPointBuilder {

    WorkingMemoryEntryPoint getWorkingMemoryEntryPoint(String entryPoint);
}
