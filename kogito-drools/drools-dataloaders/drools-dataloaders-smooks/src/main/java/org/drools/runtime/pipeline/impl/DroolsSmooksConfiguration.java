package org.drools.runtime.pipeline.impl;

public class DroolsSmooksConfiguration {
	private String rootId;
	
	public DroolsSmooksConfiguration() {
		this.rootId = "root";
	}		
	
	public DroolsSmooksConfiguration(String rootId) {
		this.rootId = rootId;
	}

	public void setRootId(String rootId) {
		this.rootId = rootId;
	}

	public String getRootId() {
		return rootId;
	}
	
	
}
