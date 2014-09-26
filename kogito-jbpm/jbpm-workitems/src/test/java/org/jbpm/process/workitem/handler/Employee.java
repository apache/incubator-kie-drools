package org.jbpm.process.workitem.handler;

public class Employee {
	
	private String id;
	private String name;
	
	public Employee() {
		
	}
	
	public Employee(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

}
