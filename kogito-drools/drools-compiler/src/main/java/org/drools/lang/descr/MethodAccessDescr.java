package org.drools.lang.descr;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * This represents a method call.
 * As in:
 * 
 * variableName.methodName(argument list)
 *
 */
public class MethodAccessDescr extends DeclarativeInvokerDescr {

	private String methodName;
	private List arguments;
	private String variableName;
	
	public MethodAccessDescr(String variableName, String methodName) {
		this.methodName = methodName;
		this.variableName = variableName;
	}

	public List getArguments() {
		return arguments;
	}

	public void setArguments(List arguments) {
		this.arguments = arguments;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}
	
	
	
}
