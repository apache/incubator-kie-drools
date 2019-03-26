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
package org.jbpm.kie.services.impl.model;

import java.io.Serializable;
import java.util.Date;

public class NodeInstanceDesc implements org.jbpm.services.api.model.NodeInstanceDesc, Serializable {

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

    private Long workItemId;
    
    private Long referenceId;    
    private String nodeContainerId;
    
    private Date slaDueDate;
    private Integer slaCompliance;

    public NodeInstanceDesc() {
        // default constructor
    }


    public NodeInstanceDesc(String id, String nodeId, String name, String nodeType,
                            String deploymentId, long processInstanceId, Date date, 
                            String connection, int type, Long workItemId,
                            Long referenceId, String nodeContainerId, Date slaDueDate, Integer slaCompliance) {
        this.id = Long.parseLong(id);
        this.name = name;
        this.nodeId = nodeId;
        this.nodeType = nodeType;
        this.deploymentId = deploymentId;
        this.processInstanceId = processInstanceId;
        this.dataTimeStamp = date;
        this.connection = connection;
        this.type = type;
        this.workItemId = workItemId;
        this.referenceId = referenceId;
        this.nodeContainerId = nodeContainerId;
        this.slaDueDate = slaDueDate;
        this.slaCompliance = slaCompliance;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public Long getProcessInstanceId() {
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

    public int getType() {
        return type;
    }

    public Long getWorkItemId() {
        return workItemId;
    }

    public void setWorkItemId(Long workItemId) {
        this.workItemId = workItemId;
    }

    public Long getReferenceId() {
        return referenceId;
    }
    
    public String getNodeContainerId() {
        return nodeContainerId;
    }
    
    public Date getSlaDueDate() {
        return slaDueDate;
    }
    
    public void setSlaDueDate(Date slaDueDate) {
        this.slaDueDate = slaDueDate;
    }
    
    public Integer getSlaCompliance() {
        return slaCompliance;
    }

    public void setSlaCompliance(Integer slaCompliance) {
        this.slaCompliance = slaCompliance;
    }

}
