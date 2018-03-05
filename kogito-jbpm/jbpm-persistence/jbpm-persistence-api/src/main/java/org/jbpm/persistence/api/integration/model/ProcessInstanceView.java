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

package org.jbpm.persistence.api.integration.model;

import java.util.Date;
import java.util.Map;

import org.jbpm.persistence.api.integration.InstanceView;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.kie.api.runtime.process.ProcessInstance;

/**
 * InstanceView dedicated for <code>org.kie.api.runtime.process.ProcessInstance</code>
 * copeFromSource is performed just before the delivery
 */
public class ProcessInstanceView implements InstanceView<ProcessInstance> {

    private static final long serialVersionUID = 8232478719589705492L;

    private String compositeId;
    private Long id;
    private String processId;    
    private String processName;    
    private String processVersion;
    private Integer state;
    private String containerId;
    private String initiator;
    private Date date;
    private String processInstanceDescription;
    private String correlationKey;
    private Long parentId;
    private Map<String, Object> variables;
    
    private transient ProcessInstance source;

    public ProcessInstanceView() {        
    }
    
    public ProcessInstanceView(ProcessInstance source) {
        this.source = source;
    }
    
    public String getCompositeId() {
        return compositeId;
    }
    
    public void setCompositeId(String compositeId) {
        this.compositeId = compositeId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getProcessVersion() {
        return processVersion;
    }

    public void setProcessVersion(String processVersion) {
        this.processVersion = processVersion;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getProcessInstanceDescription() {
        return processInstanceDescription;
    }

    public void setProcessInstanceDescription(String processInstanceDescription) {
        this.processInstanceDescription = processInstanceDescription;
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

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    @Override
    public String toString() {
        return "ProcessInstance{" +
                "id=" + id +
                ", processId='" + processId + '\'' +
                ", processName='" + processName + '\'' +
                ", state=" + state +
                ", containerId='" + containerId + '\'' +
                ", correlationKey='" + correlationKey + '\'' +
                '}';
    }

    @Override
    public ProcessInstance getSource() {
        return source;
    }
    
    @Override
    public void copyFromSource() {
        if (this.id != null) {
            return;
        }
        this.compositeId = System.getProperty("org.kie.server.id", "") + "_" + source.getId();
        this.containerId = ((WorkflowProcessInstance)source).getDeploymentId();
        this.correlationKey = ((WorkflowProcessInstanceImpl)source).getCorrelationKey();
        this.date = new Date();
        this.id = source.getId();
        this.initiator = (String) ((WorkflowProcessInstance)source).getMetaData().get("Initiator");
        this.parentId = source.getParentProcessInstanceId();
        this.processId = source.getProcessId();
        this.processInstanceDescription = ((WorkflowProcessInstanceImpl)source).getDescription();
        this.processName = source.getProcessName();
        this.processVersion = source.getProcess().getVersion();
        this.state = source.getState();
        this.variables = ((WorkflowProcessInstanceImpl)source).getVariables();
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
        ProcessInstanceView other = (ProcessInstanceView) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}
