package org.drools.dataloaders.jaxb;

public class DroolsJaxbConfiguration {
	private String iterableGetter; 		
	
	public DroolsJaxbConfiguration() {
		this.iterableGetter = null;
	}		
	
	public DroolsJaxbConfiguration(String iterableGetter) {
		this.iterableGetter = iterableGetter;
	}

	public String getIterableGetter() {
		return iterableGetter;
	}

	public void setIterableGetter(String iterableGetter) {
		this.iterableGetter = iterableGetter;
	}
	
}
