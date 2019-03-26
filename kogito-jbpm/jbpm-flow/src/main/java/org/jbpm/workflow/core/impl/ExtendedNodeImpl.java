/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.workflow.core.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.workflow.core.DroolsAction;

public class ExtendedNodeImpl extends NodeImpl {
	
	public static final String EVENT_NODE_ENTER = "onEntry";
	public static final String EVENT_NODE_EXIT = "onExit";
	
	private static final String[] EVENT_TYPES =
		new String[] { EVENT_NODE_ENTER, EVENT_NODE_EXIT };
	
	private static final long serialVersionUID = 510l;
	
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
