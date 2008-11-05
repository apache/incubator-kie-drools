package org.drools.knowledge.definitions.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.definition.KnowledgePackage;
import org.drools.definition.process.Process;
import org.drools.definition.rule.Rule;
import org.drools.knowledge.definitions.rule.impl.RuleImpl;
import org.drools.rule.Package;

public class KnowledgePackageImp implements KnowledgePackage {
	public Package pkg;
	
	public KnowledgePackageImp(Package pkg) {
		this.pkg = pkg;
	}
	
	public String getName() {
		return this.pkg.getName();
	}
	
    public Collection<Rule> getRules() {
    	org.drools.rule.Rule[] rules = pkg.getRules();
    	List<Rule> list = new ArrayList<Rule>( rules.length );
    	for( org.drools.rule.Rule rule : rules ) {
    		list.add( new RuleImpl( rule ) );
    	}
    	return list;
    }
    
    public Collection<Process> getProcesses() {
    	Collection<org.drools.process.core.Process> processes = ( Collection<org.drools.process.core.Process> ) pkg.getRuleFlows().values();
    	List<Process> list = new ArrayList<Process>( processes.size() );
    	for( org.drools.process.core.Process process : processes ) {
    		list.add( process );
    	}
    	return list;
    }
}
