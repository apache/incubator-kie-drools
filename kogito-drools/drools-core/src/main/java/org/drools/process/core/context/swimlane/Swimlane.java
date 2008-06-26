package org.drools.process.core.context.swimlane;

import java.io.Serializable;

public class Swimlane implements Serializable {

	private static final long serialVersionUID = 400L;
	
	private String name;
	private String actorId;
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
	    this.name = name;
	}
	
	public void setActorId(String actorId) {
		this.actorId = actorId;
	}
	
	public String getActorId() {
		return this.actorId;
	}
	
	public String toString() {
	    return name;
	}

}
