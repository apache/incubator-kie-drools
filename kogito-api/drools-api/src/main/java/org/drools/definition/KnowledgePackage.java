package org.drools.definition;

import java.util.Collection;

import org.drools.definition.process.Process;
import org.drools.definition.rule.Rule;

public interface KnowledgePackage {
	String getName();
	
    Collection<Rule> getRules();
    
    Collection<Process> getProcesses();
    
}
