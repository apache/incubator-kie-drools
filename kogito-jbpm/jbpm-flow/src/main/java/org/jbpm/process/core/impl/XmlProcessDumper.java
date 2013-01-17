package org.jbpm.process.core.impl;

import org.kie.definition.process.Process;

public interface XmlProcessDumper {
	
	String dumpProcess(org.kie.definition.process.Process process);
	
	Process readProcess(String processXml);

}
