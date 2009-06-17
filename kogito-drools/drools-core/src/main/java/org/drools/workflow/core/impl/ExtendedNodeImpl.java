package org.drools.workflow.core.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.workflow.core.DroolsAction;

public class ExtendedNodeImpl extends NodeImpl {
	
	public static final String EVENT_NODE_ENTER = "onEntry";
	public static final String EVENT_NODE_EXIT = "onExit";
	
	private static final String[] EVENT_TYPES =
		new String[] { EVENT_NODE_ENTER, EVENT_NODE_EXIT };
	
	private static final long serialVersionUID = 4L;
	
	private Map<String, List<DroolsAction>> actions =
		new HashMap<String, List<DroolsAction>>();
	
	public void setActions(String type, List<DroolsAction> actions) {
		this.actions.put(type, actions);
	}
	
	public List<DroolsAction> getActions(String type) {
		return this.actions.get(type);
	}
	
	public boolean containsActions() {
		for (List<DroolsAction> l: actions.values()) {
			if (!l.isEmpty()) {
				return true;
			}
		}
		return false;
	}
	
	public String[] getActionTypes() {
		return EVENT_TYPES;
	}
	
}
