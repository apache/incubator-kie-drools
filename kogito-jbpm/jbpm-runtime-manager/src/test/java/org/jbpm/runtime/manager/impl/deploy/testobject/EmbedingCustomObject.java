package org.jbpm.runtime.manager.impl.deploy.testobject;

public class EmbedingCustomObject {

	private SimpleCustomObject customObject;
	private String description;
	
	public EmbedingCustomObject() {
		
	}
	
	public EmbedingCustomObject(SimpleCustomObject customObject, String description) {
		this.setCustomObject(customObject);
		this.setDescription(description);
	}


	public SimpleCustomObject getCustomObject() {
		return customObject;
	}


	public void setCustomObject(SimpleCustomObject customObject) {
		this.customObject = customObject;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
