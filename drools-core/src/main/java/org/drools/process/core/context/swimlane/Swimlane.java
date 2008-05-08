package org.drools.process.core.context.swimlane;

import java.io.Serializable;

public class Swimlane implements Serializable {

	private static final long serialVersionUID = 400L;
	
	private String name;
	private String actorId;
	
	public Swimlane(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setActorId(String actorId) {
		this.actorId = actorId;
	}
	
	public String getActorId() {
		return this.actorId;
	}

}
