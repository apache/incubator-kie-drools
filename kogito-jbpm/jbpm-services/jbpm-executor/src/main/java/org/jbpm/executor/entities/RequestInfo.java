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

package org.jbpm.executor.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.kie.api.executor.STATUS;


@Entity
@SequenceGenerator(name="requestInfoIdSeq", sequenceName="REQUEST_INFO_ID_SEQ")
public class RequestInfo implements org.kie.internal.executor.api.RequestInfo, Serializable {

    private static final long serialVersionUID = 5823083735663566537L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator="requestInfoIdSeq")
    private Long id;
    @Column(name="timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date time;
    @Enumerated(EnumType.STRING)
    private STATUS status;
    private String commandName;
    private String message;
    //Business Key for callback
    @Column(name="businessKey")
    private String key;
    //Number of times that this request must be retried
    private int retries = 0;
    //Number of times that this request has been executed
    private int executions = 0;
    private String deploymentId;
    // owning component of this request, meaning when set only same component can execute it 
    private String owner;
    private int priority = 0;
    private Long processInstanceId;
    
    @Lob
    @Column(length=2147483647)
    private byte[] requestData;
    @Lob
    @Column(length=2147483647)
    private byte[] responseData;
    @OneToMany(cascade= CascadeType.ALL, mappedBy="requestInfo", fetch=FetchType.EAGER)
    private List<ErrorInfo> errorInfo = new ArrayList<ErrorInfo>();

    public RequestInfo() {
    }
    
    

    public RequestInfo(Long id, int priority, Date time) {
        super();
        this.id = id;        
        this.priority = priority;
        this.time = time;
    }



    public List<? extends ErrorInfo> getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(List<ErrorInfo> errorInfo) {
        this.errorInfo = errorInfo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public int getExecutions() {
        return executions;
    }

    public void setExecutions(int executions) {
        this.executions = executions;
    }

    
    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }    

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public byte[] getRequestData() {
        return requestData;
    }

    public void setRequestData(byte[] requestData) {
        this.requestData = requestData;
    }

    public byte[] getResponseData() {
        return responseData;
    }

    public void setResponseData(byte[] responseData) {
        this.responseData = responseData;
    }

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
	
    public int getPriority() {
        return priority;
    }
    
    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    
    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    @Override
    public String toString() {
        return "RequestInfo{" + "id=" + id + ", time=" + time 
        		+ ", status=" + status + ", commandName=" + commandName + ", message=" + message + ", owner=" + owner
        		+ ", key=" + key + ", requestData=" + requestData + ", responseData=" + responseData 
        		+ ", error=" + errorInfo + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RequestInfo other = (RequestInfo) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if (this.time != other.time && (this.time == null || !this.time.equals(other.time))) {
            return false;
        }
        if (this.status != other.status) {
            return false;
        }
        if ((this.commandName == null) ? (other.commandName != null) : !this.commandName.equals(other.commandName)) {
            return false;
        }
        if ((this.message == null) ? (other.message != null) : !this.message.equals(other.message)) {
            return false;
        }
        if ((this.key == null) ? (other.key != null) : !this.key.equals(other.key)) {
            return false;
        }
        if ((this.owner == null) ? (other.owner != null) : !this.owner.equals(other.owner)) {
            return false;
        }
        if ((this.deploymentId == null) ? (other.deploymentId != null) : !this.deploymentId.equals(other.deploymentId)) {
            return false;
        }
        if ((this.processInstanceId == null) ? (other.processInstanceId != null) : !this.processInstanceId.equals(other.processInstanceId)) {
            return false;
        }
        if (!Arrays.equals(this.requestData, other.requestData)) {
            return false;
        }
        if (!Arrays.equals(this.responseData, other.responseData)) {
            return false;
        }
        if (this.errorInfo != other.errorInfo && (this.errorInfo == null || !this.errorInfo.equals(other.errorInfo))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 79 * hash + (this.time != null ? this.time.hashCode() : 0);
        hash = 79 * hash + (this.status != null ? this.status.hashCode() : 0);
        hash = 79 * hash + (this.commandName != null ? this.commandName.hashCode() : 0);
        hash = 79 * hash + (this.message != null ? this.message.hashCode() : 0);
        hash = 79 * hash + (this.key != null ? this.key.hashCode() : 0);
        hash = 79 * hash + (this.owner != null ? this.owner.hashCode() : 0);
        hash = 79 * hash + (this.deploymentId != null ? this.deploymentId.hashCode() : 0);
        hash = 79 * hash + (this.processInstanceId != null ? this.processInstanceId.hashCode() : 0);
        hash = 79 * hash + Arrays.hashCode(this.requestData);
        hash = 79 * hash + Arrays.hashCode(this.responseData);
        hash = 79 * hash + (this.errorInfo != null ? this.errorInfo.hashCode() : 0);
        return hash;
    }    
}
