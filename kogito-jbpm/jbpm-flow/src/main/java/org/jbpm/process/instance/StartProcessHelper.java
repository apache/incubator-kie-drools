package org.jbpm.process.instance;

import java.util.Map;

import org.drools.definition.process.Process;
import org.drools.runtime.KnowledgeRuntime;
import org.drools.runtime.process.ProcessInstance;

public final class StartProcessHelper {
	
	public static ProcessInstance startProcessByName(KnowledgeRuntime kruntime, String name, Map<String, Object> parameters) {
		if (name == null) {
			throw new IllegalArgumentException("Name cannot be null");
		}
		String processId = null;
		double highestVersion = -1;
		for (Process process: kruntime.getKnowledgeBase().getProcesses()) {
			if (name.equals(process.getName())) {
				double version = 0;
				if (process.getVersion() != null) {
					try {
						version = Double.valueOf(process.getVersion());
					} catch (NumberFormatException e) {
						throw new IllegalArgumentException("Could not parse version: " + process.getVersion());
					}
				}
				if (version > highestVersion) {
					processId = process.getId();
				}
			}
		}
		if (processId == null) {
			throw new IllegalArgumentException("Could not find process with name " + name);
		}
		return kruntime.startProcess(processId, parameters);
	}

}
