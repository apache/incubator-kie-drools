package org.jbpm.process.workitem.handler;

import java.util.Map;

import org.kie.runtime.process.ProcessContext;

public class RecordHandler implements JavaHandler {

	public Map<String, Object> execute(ProcessContext kcontext) {
		String employeeId = (String) kcontext.getVariable("employeeId");
		// look up employee in for example db
		// we will just create one here for demo purposes
		Employee employee = new Employee(employeeId, "krisv");
		kcontext.setVariable("employee", employee);
		return null;
	}

}
