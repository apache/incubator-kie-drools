package org.drools.lang.descr;

import java.util.ArrayList;
import java.util.List;

/** This represents a function call - as in calling a Drools function. 
 * eg: functionName(argument list)
 */
public class FunctionCallDescr extends DeclarativeInvokerDescr {

	private String name;
	private String arguments;

	public FunctionCallDescr(String name) {
		this.name = name;
	}
	
	public String getArguments() {
		return arguments;
	}

	public void setArguments(String arguments) {
		this.arguments = arguments;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
    public String toString() {
        return this.name+this.arguments;
    }
	
	
}
