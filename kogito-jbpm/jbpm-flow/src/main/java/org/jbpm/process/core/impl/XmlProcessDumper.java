package org.jbpm.process.core.impl;

import org.kie.api.definition.process.Process;

public interface XmlProcessDumper {
	
	String dumpProcess(org.kie.api.definition.process.Process process);
	
	Process readProcess(String processXml);

}
