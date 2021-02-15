/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.workflow.core.impl;

import java.io.Serializable;

public class ConnectionRef implements Serializable {
    
    private static final long serialVersionUID = 510l;
	
    private String connectionId;
	private String toType;
    private long nodeId;
    
    public ConnectionRef(long nodeId, String toType) {
        this.nodeId = nodeId;
        this.toType = toType;
    }
    
    public ConnectionRef(String connectionId, long nodeId, String toType) {
        this.connectionId = connectionId;
        this.nodeId = nodeId;
        this.toType = toType;
    }
    
    public String getToType() {
        return toType;
    }
    
    public long getNodeId() {
        return nodeId;
    }    
    
    public String getConnectionId() {
        return connectionId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((connectionId == null) ? 0 : connectionId.hashCode());
        result = prime * result + (int) (nodeId ^ (nodeId >>> 32));
        result = prime * result + ((toType == null) ? 0 : toType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ConnectionRef other = (ConnectionRef) obj;
        if (connectionId == null) {
            if (other.connectionId != null)
                return false;
        } else if (!connectionId.equals(other.connectionId))
            return false;
        if (nodeId != other.nodeId)
            return false;
        if (toType == null) {
            if (other.toType != null)
                return false;
        } else if (!toType.equals(other.toType))
            return false;
        return true;
    }
    
}
