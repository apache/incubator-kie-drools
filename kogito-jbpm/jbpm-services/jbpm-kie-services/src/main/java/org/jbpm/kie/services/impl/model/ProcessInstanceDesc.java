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
import java.util.List;


public class ProcessInstanceDesc implements org.jbpm.services.api.model.ProcessInstanceDesc, Serializable{

    private static final long serialVersionUID = 7310019271033570922L;

    private long id;
    private String processId;
    private String processName;
    private String processVersion;
    private int state;
    private String deploymentId;    
    private String initiator;
    
    private Date dataTimeStamp;

    private String processInstanceDescription;
    
    private String correlationKey;

    private Long parentId;
    
    private Date slaDueDate;
    private Integer slaCompliance;
    
    private List<org.jbpm.services.api.model.UserTaskInstanceDesc> activeTasks;

    public ProcessInstanceDesc() {
        
    }

    public ProcessInstanceDesc(long id, String processId, String processName, String processVersion,
                               int state, String deploymentId, Date dataTimeStamp, String initiator,
                               String processInstanceDescription, String correlationKey, Long parentId,
                               Date slaDueDate, Integer slaCompliance) {
        this(id,processId,processName, processVersion,state, deploymentId, dataTimeStamp, initiator,
                processInstanceDescription, correlationKey);
        this.parentId = parentId;
        this.slaDueDate = slaDueDate;
        this.slaCompliance = slaCompliance;

    }
    
    public ProcessInstanceDesc(long id, String processId, String processName, String processVersion, 
                                int state, String deploymentId, Date dataTimeStamp, String initiator,
                                String processInstanceDescription, String correlationKey) {
        this(id,processId,processName, processVersion,state, deploymentId, dataTimeStamp, initiator, correlationKey);
        this.processInstanceDescription = processInstanceDescription;

    }
    
    public ProcessInstanceDesc(long id, String processId, String processName, String processVersion, 
                               int state, String deploymentId, Date dataTimeStamp, String initiator, String correlationKey) {
       this.id = id;
       this.processId = processId;
       this.processName = processName;
       this.processVersion = processVersion==null?"":processVersion;
       this.state = state;
       this.deploymentId = deploymentId;
       this.dataTimeStamp = dataTimeStamp;
       this.initiator = initiator;
       this.correlationKey = correlationKey==null?"":correlationKey;
    }
    
    public ProcessInstanceDesc(long id, String processId, String processName, String processVersion,
                               int state, String deploymentId, Date dataTimeStamp, String initiator,
                               String processInstanceDescription, String correlationKey, Long parentId) {
        this(id,processId,processName, processVersion,state, deploymentId, dataTimeStamp, initiator,
                processInstanceDescription, correlationKey);
        this.parentId = parentId;

    }
    
    public String getProcessId() {
        return processId;
    }

    public Long getId() {
        return id;
    }

    public String getProcessName() {
        return processName;
    }

    public Integer getState() {
        return state;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public Date getDataTimeStamp() {
        return dataTimeStamp;
    }

    public String getProcessVersion() {
        return processVersion;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    public String getProcessInstanceDescription() {
        return processInstanceDescription;
    }
    
    public void setProcessInstanceDescription(String processInstanceDescription) {
        this.processInstanceDescription = processInstanceDescription;
    }

	public List<org.jbpm.services.api.model.UserTaskInstanceDesc> getActiveTasks() {
		return activeTasks;
	}

	public void setActiveTasks(List<org.jbpm.services.api.model.UserTaskInstanceDesc> activeTasks) {
		this.activeTasks = activeTasks;
	}

	public String getCorrelationKey() {
		return correlationKey;
	}

	public void setCorrelationKey(String correlationKey) {
		this.correlationKey = correlationKey;
	}

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
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

    @Override
    public String toString() {
        return "ProcessInstanceDesc{" + "id=" + id + ", processId=" + processId + ", processName=" + processName + ", processVersion=" + processVersion + ", state=" + state + ", deploymentId=" + deploymentId + ", initiator=" + initiator + ", dataTimeStamp=" + dataTimeStamp + ", processInstanceDescription=" + processInstanceDescription + ", correlationKey=" + correlationKey + ", parentId=" + parentId + ", activeTasks=" + activeTasks + '}';
    }
    
    
}
