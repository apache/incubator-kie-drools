/**
 * Copyright 2010 JBoss Inc
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

@Entity
@SequenceGenerator(name="nodeInstanceLogIdSeq", sequenceName="NODE_INST_LOG_ID_SEQ", allocationSize=1)
public class NodeInstanceLog implements Serializable {
    
	public static final int TYPE_ENTER = 0;
	public static final int TYPE_EXIT = 1;
	
	private static final long serialVersionUID = 510l;
	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator="nodeInstanceLogIdSeq")
	private long id;
    
    private int type;
    private long processInstanceId;
    private String processId;
    private String nodeInstanceId;
    private String nodeId;
    private String nodeName;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "log_date")
    private Date date;
    
    NodeInstanceLog() {
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
	
	public int getType() {
		return type;
	}
	
	void setType(int type) {
		this.type = type;
	}
    
    public long getId() {
    	return id;
    }
    
    void setId(long id) {
		this.id = id;
	}

    public long getProcessInstanceId() {
        return processInstanceId;
    }
    
	void setProcessInstanceId(long processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

    public String getProcessId() {
        return processId;
    }
    
	void setProcessId(String processId) {
		this.processId = processId;
	}

    public String getNodeInstanceId() {
		return nodeInstanceId;
	}

	void setNodeInstanceId(String nodeInstanceId) {
		this.nodeInstanceId = nodeInstanceId;
	}

	public String getNodeId() {
		return nodeId;
	}

	void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	
	public String getNodeName() {
		return nodeName;
	}
	
	void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public Date getDate() {
        return date;
    }
    
	void setDate(Date date) {
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
		return true;
	}
  	
}
