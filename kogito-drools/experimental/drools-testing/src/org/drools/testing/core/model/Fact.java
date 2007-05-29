package org.drools.testing.core.model;

import java.util.ArrayList;
import java.util.Collection;

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
	private Collection fields = new ArrayList();
	
	public Fact () {}
	
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

	public Collection getFields() {
		return fields;
	}

	public void setFields(Collection fields) {
		this.fields = fields;
	}
}
