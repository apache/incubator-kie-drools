/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workflow.core.node;

import java.util.ArrayList;
import java.util.List;

import org.drools.definition.process.Connection;
import org.drools.process.core.event.EventFilter;
import org.drools.process.core.event.EventTransformer;
import org.drools.process.core.event.EventTypeFilter;
import org.drools.workflow.core.impl.ExtendedNodeImpl;

public class EventNode extends ExtendedNodeImpl implements EventNodeInterface {

	private static final long serialVersionUID = 510l;
	
	private List<EventFilter> filters = new ArrayList<EventFilter>();
	private EventTransformer transformer;
	private String variableName;
	private String scope; 

	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

	public void addEventFilter(EventFilter eventFilter) {
		filters.add(eventFilter);
	}
	
	public void removeEventFilter(EventFilter eventFilter) {
		filters.remove(eventFilter);
	}
	
	public List<EventFilter> getEventFilters() {
		return filters;
	}
		
	public void setEventFilters(List<EventFilter> filters) {
		this.filters = filters;
	}
	
	public String getType() {
		for (EventFilter filter: filters) {
    		if (filter instanceof EventTypeFilter) {
    			return ((EventTypeFilter) filter).getType();
    		}
    	}
    	return null;
	}
		
	public boolean acceptsEvent(String type, Object event) {
    	for (EventFilter filter: filters) {
    		if (!filter.acceptsEvent(type, event)) {
    			return false;
    		}
    	}
    	return true;
    }
	
	public void setEventTransformer(EventTransformer transformer) {
		this.transformer = transformer;
	}
	
	public EventTransformer getEventTransformer() {
		return transformer;
	}
	
	
	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

    public void validateAddIncomingConnection(final String type, final Connection connection) {
        super.validateAddIncomingConnection(type, connection);
        if (!org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                "This type of node only accepts default incoming connection type!");
        }
        if (getFrom() != null) {
            throw new IllegalArgumentException(
                "This type of node cannot have more than one incoming connection!");
        }
    }

    public void validateAddOutgoingConnection(final String type, final Connection connection) {
        super.validateAddOutgoingConnection(type, connection);
        if (!org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                "This type of node only accepts default outgoing connection type!");
        }
        if (getTo() != null) {
            throw new IllegalArgumentException(
                "This type of node cannot have more than one outgoing connection!");
        }
    }
    
}
