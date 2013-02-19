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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;

/**
 *
 * @author salaboy
 */
@Entity
public class NodeInstanceDesc implements Serializable{
    @Id
    @GeneratedValue()
    private long pk;
    
    private long id;
    private long nodeId;
    private String nodeUniqueId;
    private String name;
    private String domainName;
    private int sessionId;
    private long processInstanceId;
    private String type;
    private boolean completed;
    private String connection;
    
    
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataTimeStamp;

    public NodeInstanceDesc() {
        this.dataTimeStamp = new Date();
    }

    public NodeInstanceDesc(long id, long nodeId, String name, String uniqueId, String type, 
                            String domainName, int sessionId, long processInstanceId, String connection){
        this(id, nodeId, name, uniqueId,  type, domainName, sessionId, processInstanceId, connection, false);
        
    } 
    public NodeInstanceDesc(long id, long nodeId, String name, String uniqueId, String type,
                            String domainName, int sessionId, long processInstanceId, String connection, 
                            boolean completed) {
        this.id = id;
        this.nodeId = nodeId;
        this.name = name;
        this.nodeUniqueId = uniqueId;
        this.type = type;
        this.sessionId = sessionId;
        this.processInstanceId = processInstanceId;
        this.domainName = domainName;
        this.completed = completed;
        this.dataTimeStamp = new Date();
        this.connection = connection;
    }

    public long getId() {
        return id;
    }

    public long getNodeId() {
        return nodeId;
    }

    public String getName() {
        return name;
    }

    public int getSessionId() {
        return sessionId;
    }

    public long getProcessInstanceId() {
        return processInstanceId;
    }

    public Date getDataTimeStamp() {
        return dataTimeStamp;
    }

    public String getDomainName() {
        return domainName;
    }

    public String getType() {
        return type;
    }

    public String getNodeUniqueId() {
        return nodeUniqueId;
    }

    public void setNodeUniqueId(String nodeUniqueId) {
        this.nodeUniqueId = nodeUniqueId;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
    
    @Override
    public String toString() {
        return "NodeInstanceDesc{" + "pk=" + pk + ", id=" + id + ", nodeId=" + nodeId + ", nodeUniqueId=" + nodeUniqueId + ", name=" + name + ", domainName=" + domainName + ", sessionId=" + sessionId + ", processInstanceId=" + processInstanceId + ", type=" + type + ", completed=" + completed + ", dataTimeStamp=" + dataTimeStamp + '}';
    }

    public String getConnection() {
        return connection;
    }

    public void setConnection(String incomingConnection) {
        this.connection = incomingConnection;
    }
    
}
