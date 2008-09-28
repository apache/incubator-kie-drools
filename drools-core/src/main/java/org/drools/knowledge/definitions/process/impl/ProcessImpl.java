package org.drools.knowledge.definitions.process.impl;

import org.drools.process.core.Process;

public class ProcessImpl implements org.drools.knowledge.definitions.process.Process {
	private Process process;
	
	public ProcessImpl(Process process) {
		this.process = process;
	}

	public String getId() {
		return this.process.getId();
	}

	public String getName() {
		return this.process.getName();
	}

	public String getPackageName() {
		return this.process.getPackageName();
	}

	public String getVersion() {
		return this.process.getVersion();
	}
}
