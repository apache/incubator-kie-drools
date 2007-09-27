package org.drools.analytics.components;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Toni Rikkola
 * 
 */
public class AnalyticsClass implements Serializable {
	private static final long serialVersionUID = -783733402566313623L;

	private static int index = 0;

	private int id = index++;
	private String name;

	private Set<Field> fields = new HashSet<Field>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Set<Field> getFields() {
		return fields;
	}

	public void setFields(Set<Field> fields) {
		this.fields = fields;
	}
}
