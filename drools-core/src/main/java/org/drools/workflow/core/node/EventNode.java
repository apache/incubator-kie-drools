package org.drools.workflow.core.node;

import java.util.ArrayList;
import java.util.List;

import org.drools.definition.process.Connection;
import org.drools.process.core.event.EventFilter;
import org.drools.process.core.event.EventTransformer;
import org.drools.process.core.event.EventTypeFilter;
import org.drools.workflow.core.impl.ExtendedNodeImpl;

public class EventNode extends ExtendedNodeImpl implements EventNodeInterface {

	private static final long serialVersionUID = 4L;
	
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
