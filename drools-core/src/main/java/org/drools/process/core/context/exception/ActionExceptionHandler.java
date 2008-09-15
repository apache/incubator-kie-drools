package org.drools.process.core.context.exception;

import org.drools.workflow.core.DroolsAction;

public class ActionExceptionHandler implements ExceptionHandler {
	
	private static final long serialVersionUID = 400L;
	
	private String faultVariable;
	private DroolsAction action;

	public String getFaultVariable() {
		return faultVariable;
	}

	public void setFaultVariable(String faultVariable) {
		this.faultVariable = faultVariable;
	}

	public DroolsAction getAction() {
		return action;
	}

	public void setAction(DroolsAction action) {
		this.action = action;
	}
	
	public String toString() {
		return action == null ? "" : action.toString();
	}

}
