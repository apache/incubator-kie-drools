package org.jbpm.process;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.definition.KnowledgePackage;
import org.drools.definition.process.Process;
import org.drools.definition.rule.Rule;

public class ProcessPackage implements KnowledgePackage {

	private String name;
	private List<Process> processes = new ArrayList<Process>();
	
	public ProcessPackage(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public Collection<Rule> getRules() {
		return null;
	}

	public void addProcess(Process process) {
		processes.add(process);
	}
	
	public Collection<Process> getProcesses() {
		return processes;
	}

}
