package org.drools.analytics.components;

import java.io.Serializable;

/**
 * @author Toni Rikkola
 * 
 */
public class AnalyticsClass implements Serializable {
	private static final long serialVersionUID = -783733402566313623L;

	private static int index = 0;

	private int id = index++;
	private String name;

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
}
