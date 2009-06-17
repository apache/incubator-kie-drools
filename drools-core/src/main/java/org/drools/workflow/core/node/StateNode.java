package org.drools.workflow.core.node;

import java.util.HashMap;
import java.util.Map;

import org.drools.definition.process.Connection;
import org.drools.workflow.core.Constraint;
import org.drools.workflow.core.impl.ConnectionRef;

public class StateNode extends CompositeContextNode implements Constrainable {

	private static final long serialVersionUID = 4L;
	
    private Map<ConnectionRef, Constraint> constraints = new HashMap<ConnectionRef, Constraint>();
   
    public void setConstraints(Map<ConnectionRef, Constraint> constraints) {
        this.constraints = constraints;
    }

    public void setConstraint(final Connection connection, final Constraint constraint) {
		if (connection == null) {
			throw new IllegalArgumentException("connection is null");
		}
		if (!getDefaultOutgoingConnections().contains(connection)) {
			throw new IllegalArgumentException("connection is unknown:"	+ connection);
		}
		addConstraint(new ConnectionRef(
			connection.getTo().getId(), connection.getToType()), constraint);
	}

    public void addConstraint(ConnectionRef connectionRef, Constraint constraint) {
    	if (connectionRef == null) {
    		throw new IllegalArgumentException(
				"A state node only accepts constraints linked to a connection");
    	}
        constraints.put(connectionRef, constraint);
    }
    
    public Constraint getConstraint(String name){
        return constraints.get(name);
    }
    
    public Map<ConnectionRef, Constraint> getConstraints(){
        return constraints;
    }

    public Constraint getConstraint(final Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("connection is null");
        }
        ConnectionRef ref = new ConnectionRef(connection.getTo().getId(), connection.getToType());
        return this.constraints.get(ref);
    }

}
