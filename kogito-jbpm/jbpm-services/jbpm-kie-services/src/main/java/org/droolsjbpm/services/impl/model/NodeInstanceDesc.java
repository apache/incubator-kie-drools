/*
 * Copyright 2012 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.droolsjbpm.services.impl.model;

import java.io.Serializable;
import java.util.Date;

public class NodeInstanceDesc implements Serializable{
    
    private static final long serialVersionUID = -5724814793988493958L;
    
    private long id;
    private String nodeId;
    private String name;
    private String deploymentId;
    private long processInstanceId;
    private String nodeType;
    private String connection;
    private int type;
    
    private Date dataTimeStamp;

    public NodeInstanceDesc() {
    }


    public NodeInstanceDesc(String id, String nodeId, String name, String nodeType,
                            String deploymentId, long processInstanceId, Date date, 
                            String connection, int type) {
        this.id = Long.parseLong(id);
        this.name = name;
        this.nodeId = nodeId;
        this.nodeType = nodeType;
        this.deploymentId = deploymentId;
        this.processInstanceId = processInstanceId;
        this.dataTimeStamp = date;
        this.connection = connection;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public long getProcessInstanceId() {
        return processInstanceId;
    }

    public Date getDataTimeStamp() {
        return dataTimeStamp;
    }

    public String getNodeType() {
        return nodeType;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeUniqueId) {
        this.nodeId = nodeUniqueId;
    }

    public boolean isCompleted() {
        return (this.type==1);
    }
    
    @Override
    public String toString() {
        return "NodeInstanceDesc{" + "id=" + id + ", nodeId=" + nodeId + ", nodeUniqueId=" + nodeId + ", name=" + name + ", deploymentId=" + deploymentId + ", processInstanceId="
                + processInstanceId + ", type=" + nodeType + ", completed=" + isCompleted() + ", dataTimeStamp=" + dataTimeStamp + '}';
    }

    public String getConnection() {
        return connection;
    }

    public void setConnection(String incomingConnection) {
        this.connection = incomingConnection;
    }
    
}
