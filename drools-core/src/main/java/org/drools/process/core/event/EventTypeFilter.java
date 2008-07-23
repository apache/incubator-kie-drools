package org.drools.process.core.event;

public class EventTypeFilter implements EventFilter {

	private String type;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean acceptsEvent(String type, Object event) {
		if (this.type != null && this.type.equals(type)) {
			return true;
		}
		return false;
	}

}
