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
@SequenceGenerator(name="variableInstanceLogIdSeq", sequenceName="VAR_INST_LOG_ID_SEQ", allocationSize=1)
public class VariableInstanceLog implements Serializable {
    
	private static final long serialVersionUID = 510l;
	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator="variableInstanceLogIdSeq")
	private long id;
    
    private long processInstanceId;
    private String processId;
    private String variableInstanceId;
    private String variableId;
    private String value;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "log_date")
    private Date date;
    
    VariableInstanceLog() {
    }
    
	public VariableInstanceLog(long processInstanceId, String processId,
			               	   String variableInstanceId, String variableId, String value) {
        this.processInstanceId = processInstanceId;
        this.processId = processId;
		this.variableInstanceId = variableInstanceId;
		this.variableId = variableId;
		setValue(value);
        this.date = new Date();
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

	public String getVariableInstanceId() {
		return variableInstanceId;
	}

	public void setVariableInstanceId(String variableInstanceId) {
		this.variableInstanceId = variableInstanceId;
	}

	public String getVariableId() {
		return variableId;
	}

	public void setVariableId(String variableId) {
		this.variableId = variableId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		if (value != null && value.length() > 255) {
			value = value.substring(0, 255);
		}
		this.value = value;
	}

	public Date getDate() {
        return date;
    }
    
	void setDate(Date date) {
		this.date = date;
	}

    public String toString() {
        return "Change variable '" + 
        	processId + "#" + variableId + "' to '" + value + "' [" + processInstanceId + "#" + variableInstanceId + "]";
    }
    
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + (int) id;
		result = prime * result
				+ ((processId == null) ? 0 : processId.hashCode());
		result = prime * result + (int) processInstanceId;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		result = prime * result
				+ ((variableId == null) ? 0 : variableId.hashCode());
		result = prime
				* result
				+ ((variableInstanceId == null) ? 0 : variableInstanceId
						.hashCode());
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
		VariableInstanceLog other = (VariableInstanceLog) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (id != other.id)
			return false;
		if (processId == null) {
			if (other.processId != null)
				return false;
		} else if (!processId.equals(other.processId))
			return false;
		if (processInstanceId != other.processInstanceId)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		if (variableId == null) {
			if (other.variableId != null)
				return false;
		} else if (!variableId.equals(other.variableId))
			return false;
		if (variableInstanceId == null) {
			if (other.variableInstanceId != null)
				return false;
		} else if (!variableInstanceId.equals(other.variableInstanceId))
			return false;
		return true;
	}
}
