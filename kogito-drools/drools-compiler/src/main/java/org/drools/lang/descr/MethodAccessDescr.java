package org.drools.lang.descr;


/**
 * 
 * This represents a method call.
 * As in:
 * 
 * variableName.methodName(argument list)
 *
 */
public class MethodAccessDescr extends DeclarativeInvokerDescr {

    private static final long serialVersionUID = -1855405201484757499L;
    
    private String methodName;
	private String arguments;
	
    public MethodAccessDescr( String methodName ) {
        this.methodName = methodName;
    }

    public MethodAccessDescr( String methodName, String arguments ) {
        this.methodName = methodName;
        this.arguments = arguments;
    }

	public String getArguments() {
		return arguments;
	}

	public void setArguments(String arguments) {
		this.arguments = arguments;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
    
    public String toString() {
        return this.methodName + this.arguments;
    }

	
	
	
}
