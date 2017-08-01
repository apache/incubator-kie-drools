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

import java.io.Serializable;

public class ConnectionRef implements Serializable {
    
    private static final long serialVersionUID = 510l;
	
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
