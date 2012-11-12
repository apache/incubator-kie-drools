package org.jbpm.process.workitem.handler;

import java.util.Map;

import org.kie.runtime.process.ProcessContext;

public interface JavaHandler {
	
	Map<String, Object> execute(ProcessContext kcontext);

}
