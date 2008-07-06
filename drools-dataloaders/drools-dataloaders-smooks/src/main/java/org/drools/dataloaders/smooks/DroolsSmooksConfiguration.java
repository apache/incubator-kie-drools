package org.drools.dataloaders.smooks;

public class DroolsSmooksConfiguration {
	private String rootId;
	
	private String iterableGetter;
	//private String 		
	
	public DroolsSmooksConfiguration() {
		this.rootId = "root";
		this.iterableGetter = null;
	}		
	
	public DroolsSmooksConfiguration(String rootId, String iterableGetter) {
		this.iterableGetter = iterableGetter;
		this.rootId = rootId;
	}

	public void setRootId(String roodId) {
		this.rootId = rootId;
	}
	
	public String getRoodId() {
		return this.rootId;
	}

	public String getIterableGetter() {
		return iterableGetter;
	}

	public void setIterableGetter(String iterableGetter) {
		this.iterableGetter = iterableGetter;
	}

	public String getRootId() {
		return rootId;
	}
	
	
}
