package org.drools.examples.cdss.data;

public class Diagnose {

	private String type;
	
	public Diagnose(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
	
	public String toString() {
		return "Diagnose: " + type;
	}
	
}
