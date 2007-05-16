package org.drools.testing.core.model;

/**
 * 
 * @author Matt
 *
 * A fact represents any object used in conidtional clauses within
 * the set of rules being tested.
 * 
 * (c) Matt Shaw
 */
public class Fact {

	private Integer id;
	private String type;
	private Field[] fields;
	
	public Field[] getFields() {
		return fields;
	}
	public void setFields(Field[] fields) {
		this.fields = fields;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
