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

package org.jbpm.kie.services.impl.admin;

import org.jbpm.services.api.admin.ProcessNode;

public class ProcessNodeImpl implements ProcessNode {

	private static final long serialVersionUID = 8843363575668976484L;

	private String nodeName;
	private long nodeId;
	private String nodeType;
	private String processId;
    
	public ProcessNodeImpl(String nodeName, long nodeId, String nodeType, String processId) {
        super();
        this.nodeName = nodeName;
        this.nodeId = nodeId;
        this.nodeType = nodeType;
        this.processId = processId;
    }

    @Override
    public String getNodeName() {
        return nodeName;
    }
    
    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }
    
    @Override
    public long getNodeId() {
        return nodeId;
    }
    
    public void setNodeId(long nodeId) {
        this.nodeId = nodeId;
    }
    
    @Override
    public String getNodeType() {
        return nodeType;
    }
    
    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }
    
    @Override
    public String getProcessId() {
        return processId;
    }
    
    public void setProcessId(String processId) {
        this.processId = processId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (nodeId ^ (nodeId >>> 32));
        result = prime * result + ((nodeName == null) ? 0 : nodeName.hashCode());
        result = prime * result + ((nodeType == null) ? 0 : nodeType.hashCode());
        result = prime * result + ((processId == null) ? 0 : processId.hashCode());
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
        ProcessNodeImpl other = (ProcessNodeImpl) obj;
        if (nodeId != other.nodeId)
            return false;
        if (nodeName == null) {
            if (other.nodeName != null)
                return false;
        } else if (!nodeName.equals(other.nodeName))
            return false;
        if (nodeType == null) {
            if (other.nodeType != null)
                return false;
        } else if (!nodeType.equals(other.nodeType))
            return false;
        if (processId == null) {
            if (other.processId != null)
                return false;
        } else if (!processId.equals(other.processId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ProcessNodeImpl [nodeName=" + nodeName + ", nodeId=" + nodeId + ", nodeType=" + nodeType + ", processId=" + processId + "]";
    }	
	
}
