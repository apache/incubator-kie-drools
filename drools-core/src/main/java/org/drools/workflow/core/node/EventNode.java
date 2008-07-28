package org.drools.workflow.core.node;

import java.util.ArrayList;
import java.util.List;

import org.drools.process.core.event.EventFilter;
import org.drools.workflow.core.Connection;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.impl.NodeImpl;

public class EventNode extends NodeImpl implements EventNodeInterface {

	private static final long serialVersionUID = 4L;
	
	private List<EventFilter> filters = new ArrayList<EventFilter>();
	private String variableName;

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
		
	public boolean acceptsEvent(String type, Object event) {
    	for (EventFilter filter: filters) {
    		if (!filter.acceptsEvent(type, event)) {
    			return false;
    		}
    	}
    	return true;
    }
	
	public void validateAddIncomingConnection(final String type, final Connection connection) {
        throw new UnsupportedOperationException(
            "An event node does not have an incoming connection!");
    }

    public void validateRemoveIncomingConnection(final String type, final Connection connection) {
        throw new UnsupportedOperationException(
            "An event node does not have an incoming connection!");
    }

    public void validateAddOutgoingConnection(final String type, final Connection connection) {
        super.validateAddOutgoingConnection(type, connection);
        if (!Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                "This type of node only accepts default outgoing connection type!");
        }
        if (getOutgoingConnections(Node.CONNECTION_DEFAULT_TYPE) != null
                && !getOutgoingConnections(Node.CONNECTION_DEFAULT_TYPE).isEmpty()) {
            throw new IllegalArgumentException(
                "This type of node cannot have more than one outgoing connection!");
        }
    }

}
