package org.jbpm.process.workitem;

public class WorkDefinitionImpl extends org.drools.process.core.impl.WorkDefinitionExtensionImpl {

	private static final long serialVersionUID = 5L;
	
	private String[] dependencies;
	private String defaultHandler;
	
	public String[] getDependencies() {
		return dependencies;
	}
	
	public void setDependencies(String[] dependencies) {
		this.dependencies = dependencies;
	}
	
	public String getDefaultHandler() {
		return defaultHandler;
	}
	
	public void setDefaultHandler(String defaultHandler) {
		this.defaultHandler = defaultHandler;
	}
}
