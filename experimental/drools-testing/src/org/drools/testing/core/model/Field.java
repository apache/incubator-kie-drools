package org.drools.testing.core.model;

/**
 * 
 * @author Matt
 *
 * (c) Matt Shaw
 */
public class Field {

	private String name;
	private String type;
	private String value;
	
	public Field () {}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
