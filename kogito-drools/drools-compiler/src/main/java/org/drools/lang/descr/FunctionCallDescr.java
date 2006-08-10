package org.drools.lang.descr;

import java.util.ArrayList;
import java.util.List;

/** This represents a function call - as in calling a Drools function. 
 * eg: functionName(argument list)
 */
public class FunctionCallDescr extends DeclarativeInvokerDescr {

	private String name;
	private List args = new ArrayList();

	public FunctionCallDescr(String name) {
		this.name = name;
	}
	
	public void addArgument(String arg) {
		args.add(arg);
	}

	public List getArgs() {
		return args;
	}

	public void setArgs(List args) {
		this.args = args;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
