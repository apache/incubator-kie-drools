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

package org.jbpm.process.audit;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.jbpm.process.audit.event.AuditEvent;

@Entity
@SequenceGenerator(name="nodeInstanceLogIdSeq", sequenceName="NODE_INST_LOG_ID_SEQ", allocationSize=1)
public class NodeInstanceLog implements Serializable, AuditEvent, org.kie.api.runtime.manager.audit.NodeInstanceLog {
   
	
	private static final long serialVersionUID = 510l;
	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator="nodeInstanceLogIdSeq")
	private long id;
    
    private long processInstanceId;
    private String processId;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "log_date")
    private Date date;
    
    private int type;
    private String nodeInstanceId;
    private String nodeId;
    private String nodeName;
    private String nodeType;
    private Long workItemId;    
    private String connection;
    
    private String externalId;
    
    private Long referenceId;    
    private String nodeContainerId;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "sla_due_date")
    private Date slaDueDate;
    
    @Column(nullable=true)
    private Integer slaCompliance;
    
    public NodeInstanceLog() {
    }
    
	public NodeInstanceLog(int type, long processInstanceId, String processId,
			               String nodeInstanceId, String nodeId, String nodeName) {
		this.type = type;
        this.processInstanceId = processInstanceId;
        this.processId = processId;
		this.nodeInstanceId = nodeInstanceId;
		this.nodeId = nodeId;
		this.nodeName = nodeName;
        this.date = new Date();
    }
	
	public Integer getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
    
    public long getId() {
    	return id;
    }
    
    void setId(long id) {
		this.id = id;
	}

    public Long getProcessInstanceId() {
        return processInstanceId;
    }
    
	public void setProcessInstanceId(long processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

    public String getProcessId() {
        return processId;
    }
    
	public void setProcessId(String processId) {
		this.processId = processId;
	}

    public String getNodeInstanceId() {
		return nodeInstanceId;
	}

	public void setNodeInstanceId(String nodeInstanceId) {
		this.nodeInstanceId = nodeInstanceId;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	
	public String getNodeName() {
		return nodeName;
	}
	
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public Date getDate() {
        return date;
    }
    
	public void setDate(Date date) {
		this.date = date;
	}

    public String toString() {
        return (type == 0 ? "Triggered " : "Left ") + "Node Instance '" + 
        	processId + "#" + nodeId + "' (" + nodeName + ") [" + processInstanceId + "#" + nodeInstanceId + "]";
    }
    
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + (int) id;
		result = prime * result + ((nodeId == null) ? 0 : nodeId.hashCode());
		result = prime * result
				+ ((nodeInstanceId == null) ? 0 : nodeInstanceId.hashCode());
		result = prime * result
				+ ((processId == null) ? 0 : processId.hashCode());
		result = prime * result	+ (int) processInstanceId;
		result = prime * result + type;
		result = prime * result
                + ((nodeType == null) ? 0 : nodeType.hashCode());
		result = prime * result
                + ((workItemId == null) ? 0 : workItemId.hashCode());
		result = prime * result
                + ((connection == null) ? 0 : connection.hashCode());
		result = prime * result
                + ((externalId == null) ? 0 : externalId.hashCode());
		result = prime * result
                + ((referenceId == null) ? 0 : referenceId.hashCode());
        result = prime * result
                + ((nodeContainerId == null) ? 0 : nodeContainerId.hashCode());
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
		NodeInstanceLog other = (NodeInstanceLog) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (id != other.id)
			return false;
		if (nodeId == null) {
			if (other.nodeId != null)
				return false;
		} else if (!nodeId.equals(other.nodeId))
			return false;
		if (nodeInstanceId == null) {
			if (other.nodeInstanceId != null)
				return false;
		} else if (!nodeInstanceId.equals(other.nodeInstanceId))
			return false;
		if (processId == null) {
			if (other.processId != null)
				return false;
		} else if (!processId.equals(other.processId))
			return false;
		if (processInstanceId != other.processInstanceId)
			return false;
		if (type != other.type)
			return false;
		
		if (nodeType == null) {
            if (other.nodeType != null)
                return false;
        } else if (!nodeType.equals(other.nodeType))
            return false;
		
		if (workItemId == null) {
            if (other.workItemId != null)
                return false;
        } else if (!workItemId.equals(other.workItemId))
            return false;
		
		if (connection == null) {
            if (other.connection != null)
                return false;
        } else if (!connection.equals(other.connection))
            return false;
		
		if (externalId == null) {
            if (other.externalId != null)
                return false;
        } else if (!externalId.equals(other.externalId))
            return false;
		
		if (referenceId == null) {
            if (other.referenceId != null)
                return false;
        } else if (!referenceId.equals(other.referenceId))
            return false;
        
        if (nodeContainerId == null) {
            if (other.nodeContainerId != null)
                return false;
        } else if (!nodeContainerId.equals(other.nodeContainerId))
            return false;
		
		return true;
	}

    public Long getWorkItemId() {
        return workItemId;
    }

    public void setWorkItemId(Long workItemId) {
        this.workItemId = workItemId;
    }

    public String getConnection() {
        return connection;
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String domainId) {
        this.externalId = domainId;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }
    
    public Long getReferenceId() {
        return referenceId;
    }
    
    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }
    
    public String getNodeContainerId() {
        return nodeContainerId;
    }
    
    public void setNodeContainerId(String nodeContainerId) {
        this.nodeContainerId = nodeContainerId;
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
