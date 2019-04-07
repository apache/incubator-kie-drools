/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.drools.core.ClassObjectFilter;
import org.jbpm.persistence.api.integration.InstanceView;
import org.jbpm.workflow.instance.NodeInstance;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.jbpm.workflow.instance.node.DynamicNodeInstance;
import org.jbpm.workflow.instance.node.MilestoneNodeInstance;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.process.CaseAssignment;
import org.kie.api.runtime.process.CaseData;
import org.kie.api.runtime.process.ProcessInstance;

/**
 * InstanceView dedicated for <code>org.kie.api.runtime.process.ProcessInstance</code> that is
 * associated with dynamic process which is considered as case. 
 * copeFromSource is performed just before the delivery
 */
public class CaseInstanceView implements InstanceView<ProcessInstance> {
    
    private static final long serialVersionUID = -6518981747861727235L;
    
    private String compositeId;
    private Long id;
    private String caseDefinitionId;    
    private String caseDefinitionName;    
    private Integer caseStatus;
    private String containerId;
    private String owner;
    private Date date;
    private String caseDescription;
    private String caseId;
    private Long parentId;
    private Map<String, Object> variables;
    private Map<String, Object> caseVariables;
    
    private List<String> milestones;
    private List<String> stages;
    
    private Set<String> participants;
   
    
    private transient ProcessInstance source;

    public CaseInstanceView() {        
    }
    
    public CaseInstanceView(ProcessInstance source) {
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

    public String getCaseDefinitionId() {
        return caseDefinitionId;
    }

    public void setProcessId(String processId) {
        this.caseDefinitionId = processId;
    }

    public String getCaseDefinitionName() {
        return caseDefinitionName;
    }

    public void setProcessName(String processName) {
        this.caseDefinitionName = processName;
    }

    public Integer getCaseStatus() {
        return caseStatus;
    }

    public void setState(Integer state) {
        this.caseStatus = state;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public String getOwner() {
        return owner;
    }

    public void setInitiator(String initiator) {
        this.owner = initiator;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCaseDescription() {
        return caseDescription;
    }

    public void setProcessInstanceDescription(String processInstanceDescription) {
        this.caseDescription = processInstanceDescription;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCorrelationKey(String correlationKey) {
        this.caseId = correlationKey;
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

    public Map<String, Object> getCaseVariables() {
        return caseVariables;
    }   
    
    public List<String> getMilestones() {
        return milestones;
    }
    
    public List<String> getStages() {
        return stages;
    }

    public Set<String> getParticipants() {
        return participants;
    }

    @Override
    public String toString() {
        return "CaseInstance{" +
                "id=" + id +
                ", caseDefinitionId='" + caseDefinitionId + '\'' +
                ", caseDefinitionName='" + caseDefinitionName + '\'' +
                ", caseStatus=" + caseStatus +
                ", containerId='" + containerId + '\'' +
                ", caseId='" + caseId + '\'' +
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
        this.caseId = ((WorkflowProcessInstanceImpl)source).getCorrelationKey();
        this.date = new Date();
        this.id = source.getId();        
        this.parentId = source.getParentProcessInstanceId();
        this.caseDefinitionId = source.getProcessId();
        this.caseDescription = ((WorkflowProcessInstanceImpl)source).getDescription();
        this.caseDefinitionName = source.getProcessName();
        this.caseStatus = source.getState();
        this.variables = ((WorkflowProcessInstanceImpl)source).getVariables();        
        this.milestones = new ArrayList<>();
        this.stages = new ArrayList<>();
        this.participants = new LinkedHashSet<>();
        
        Collection<NodeInstance> instances = ((WorkflowProcessInstanceImpl)source).getNodeInstances(true);
        for (NodeInstance instance : instances) {
            if (instance instanceof MilestoneNodeInstance) {
                this.milestones.add(instance.getNodeName());
            } else if (instance instanceof DynamicNodeInstance) {
                this.stages.add(instance.getNodeName());
            }
        }
        // case file specific data
        CaseData caseFile = internalGetCaseFile(((WorkflowProcessInstanceImpl)source).getKnowledgeRuntime());
        if (caseFile != null) {
            // variables
            this.caseVariables = caseFile.getData();
            // roles
            Collection<String> roles = ((CaseAssignment) caseFile).getRoles();
            // if owner defined set it explicitly
            if (roles.contains("owner")) {
                this.owner = ((CaseAssignment) caseFile).getAssignments("owner").iterator().next().getId();
            }
            // add all assigned users and groups as participants
            for (String role : roles) {            
                List<String> assignees = ((CaseAssignment) caseFile).getAssignments(role).stream().map(oe -> oe.getId()).collect(Collectors.toList());
                participants.addAll(assignees);
            }
        }
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
        CaseInstanceView other = (CaseInstanceView) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    
    public CaseData internalGetCaseFile(KieRuntime kruntime) {
        Collection<? extends Object> caseFiles = kruntime.getObjects(new ClassObjectFilter(CaseData.class));
        if (caseFiles.size() != 1) {
            return null;
        }
        CaseData caseFile = (CaseData) caseFiles.iterator().next();
        
        return caseFile;
    }


}
