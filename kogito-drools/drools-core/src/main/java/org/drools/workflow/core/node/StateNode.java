package org.drools.workflow.core.node;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.drools.definition.process.Connection;
import org.drools.workflow.core.Constraint;

public class StateNode extends CompositeContextNode {

	private static final long serialVersionUID = 4L;
	
    private Map<ConnectionRef, Constraint> constraints = new HashMap<ConnectionRef, Constraint>();
    // TODO timers, on-entry / on-exit actions ?
   
    public void setConstraints(Map<ConnectionRef, Constraint> constraints) {
        this.constraints = constraints;
    }

    public void setConstraint(final Connection connection,
			final Constraint constraint) {
		if (connection == null) {
			throw new IllegalArgumentException("connection is null");
		}
		if (getOutgoingConnections(org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE) != null
				&& !getOutgoingConnections(org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE).contains(connection)) {
			throw new IllegalArgumentException("connection is unknown:"	+ connection);
		}
		internalSetConstraint(
			new ConnectionRef(connection.getTo().getId(), connection.getToType()), constraint);
	}

    public void internalSetConstraint(ConnectionRef connectionRef, Constraint constraint) {
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

    public static class ConnectionRef implements Serializable {
        
        private static final long serialVersionUID = 4L;
		
		private String toType;
        private long nodeId;
        
        public ConnectionRef(long nodeId, String toType) {
            this.nodeId = nodeId;
            this.toType = toType;
        }
        
        public String getToType() {
            return toType;
        }
        
        public long getNodeId() {
            return nodeId;
        }
        
        public boolean equals(Object o) {
            if (o instanceof ConnectionRef) {
                ConnectionRef c = (ConnectionRef) o;
                return toType.equals(c.toType) && nodeId == c.nodeId;
            }
            return false;
        }
        
        public int hashCode() {
            return 7*toType.hashCode() + (int) nodeId;
        }
    }

}
