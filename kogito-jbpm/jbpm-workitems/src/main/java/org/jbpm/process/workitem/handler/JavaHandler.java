package org.jbpm.process.workitem.handler;

import java.util.Map;

import org.drools.runtime.process.ProcessContext;

public interface JavaHandler {
	
	Map<String, Object> execute(ProcessContext kcontext);

}
