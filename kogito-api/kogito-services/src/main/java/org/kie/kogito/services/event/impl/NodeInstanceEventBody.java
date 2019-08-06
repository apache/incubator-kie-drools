/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.services.event.impl;

import java.util.Date;

public class NodeInstanceEventBody {

    private String id;
    private String nodeId;
    private String nodeDefinitionId;
    private String nodeName;
    private String nodeType;
    private Date triggerTime;
    private Date leaveTime;
    
    private NodeInstanceEventBody() {
    }

    public String getId() {
        return id;
    }
    
    public String getNodeId() {
        return nodeId;
    }    
    
    public String getNodeDefinitionId() {
        return nodeDefinitionId;
    }

    public String getNodeName() {
        return nodeName;
    }
    
    public String getNodeType() {
        return nodeType;
    }
    
    public Date getTriggerTime() {
        return triggerTime;
    }
    
    public Date getLeaveTime() {
        return leaveTime;
    }

    @Override
    public String toString() {
        return "NodeInstance [id=" + id + ", nodeId=" + nodeId + ", nodeName=" + nodeName + ", nodeType=" + nodeType + ", triggerTime=" + triggerTime + ", leaveTime=" + leaveTime + "]";
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        NodeInstanceEventBody other = (NodeInstanceEventBody) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    public Builder update() {
        return new Builder(this);
    }
    
    public static Builder create() {
        return new Builder(new NodeInstanceEventBody());
    }

    static class Builder {
        
        private NodeInstanceEventBody instance;
                
        private Builder(NodeInstanceEventBody instance) {
            this.instance = instance;
        }
        
        public Builder id(String id) {
            instance.id = id;
            return this;
        }
        
        public Builder nodeId(String nodeId) {
            instance.nodeId = nodeId;
            return this;
        }
        
        public Builder nodeDefinitionId(String nodeDefinitionId) {
            instance.nodeDefinitionId = nodeDefinitionId;
            return this;
        }
        
        public Builder nodeName(String nodeName) {
            instance.nodeName = nodeName;
            return this;
        }
        
        public Builder nodeType(String nodeType) {
            instance.nodeType = nodeType;
            return this;
        }
        
        public Builder triggerTime(Date triggerTime) {
            instance.triggerTime = triggerTime;
            return this;
        }

        public Builder leaveTime(Date leaveTime) {
            instance.leaveTime = leaveTime;
            return this;
        }
        
        public NodeInstanceEventBody build() {
            return instance;
        }
    }
}
