package org.jbpm.bpmn2.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class IntermediateLink implements Serializable {

	private static final long serialVersionUID = 201105091147L;

	private static final String THROW = "throw";

	private String uniqueId;

	private String target;

	private String name;

	private String type = null;

	private List<String> sources;

	public IntermediateLink() {
		this.sources = new ArrayList<String>();
	}

	public void setUniqueId(String id) {
		this.uniqueId = id;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getTarget() {
		return target;
	}

	public void addSource(String sources) {
		this.sources.add(sources);
	}

	public List<String> getSources() {
		return sources;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public boolean isThrowLink() {
		return THROW.equals(type);
	}

	/**
	 * Turn this link into a throw link.
	 */
	public void configureThrow() {
		this.type = THROW;
	}
}
