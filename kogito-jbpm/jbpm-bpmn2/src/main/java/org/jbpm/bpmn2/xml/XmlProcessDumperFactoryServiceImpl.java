package org.jbpm.bpmn2.xml;

import org.jbpm.process.core.impl.XmlProcessDumper;
import org.jbpm.process.core.impl.XmlProcessDumperFactoryService;

public class XmlProcessDumperFactoryServiceImpl implements XmlProcessDumperFactoryService {

	public XmlProcessDumper newXmlProcessDumper() {
		return XmlBPMNProcessDumper.INSTANCE;
	}
	
}
