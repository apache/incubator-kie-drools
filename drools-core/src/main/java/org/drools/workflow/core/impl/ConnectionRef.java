package org.drools.workflow.core.impl;

import java.io.Serializable;

public class ConnectionRef implements Serializable {
    
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