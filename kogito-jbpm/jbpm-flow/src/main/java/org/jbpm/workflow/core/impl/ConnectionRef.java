/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jbpm.workflow.core.impl;

import java.io.Serializable;
import java.util.Objects;

import org.kie.api.definition.process.WorkflowElementIdentifier;

public class ConnectionRef implements Serializable {

    private static final long serialVersionUID = 510l;

    private String connectionId;
    private String toType;
    private WorkflowElementIdentifier nodeId;

    public ConnectionRef(WorkflowElementIdentifier nodeId, String toType) {
        this.nodeId = nodeId;
        this.toType = toType;
    }

    public ConnectionRef(String connectionId, WorkflowElementIdentifier nodeId, String toType) {
        this.connectionId = connectionId;
        this.nodeId = nodeId;
        this.toType = toType;
    }

    public String getToType() {
        return toType;
    }

    public WorkflowElementIdentifier getNodeId() {
        return nodeId;
    }

    public String getConnectionId() {
        return connectionId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(connectionId, nodeId, toType);
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
        return Objects.equals(connectionId, other.connectionId) && Objects.equals(nodeId, other.nodeId) && Objects.equals(toType, other.toType);
    }

}
