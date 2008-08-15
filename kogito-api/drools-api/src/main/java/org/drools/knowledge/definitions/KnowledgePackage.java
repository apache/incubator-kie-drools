package org.drools.knowledge.definitions;

import java.util.Collection;

import org.drools.knowledge.definitions.process.Process;
import org.drools.knowledge.definitions.rule.Rule;

public interface KnowledgePackage {
    Collection<Rule> getRules();
    Collection<Process> getProcesses();
}
