package org.jbpm.runtime.manager.impl.deploy.testobject;

public class SimpleCustomObject {

	private String name;

	public SimpleCustomObject() {
		this.name = "default";
	}
	
	public SimpleCustomObject(String name) {
		this.name = name;
	}
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "SimpleCustomObject [name=" + name + "]";
	}
	
}
