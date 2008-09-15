package org.drools.process.core.context.exception;

public interface ExceptionHandler {
	
	String getFaultVariable();
	
	void setFaultVariable(String faultVariable);
    
}
