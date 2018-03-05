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

package org.jbpm.casemgmt.impl.model.instance;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;
import org.jbpm.casemgmt.api.model.instance.CaseInstance;
import org.jbpm.casemgmt.api.model.instance.CaseMilestoneInstance;
import org.jbpm.casemgmt.api.model.instance.CaseRoleInstance;
import org.jbpm.casemgmt.api.model.instance.CaseStageInstance;


public class CaseInstanceImpl implements CaseInstance, Serializable {

    private static final long serialVersionUID = 832035193857983082L;

    private String caseId;
    private String caseDescription;
    private Collection<CaseStageInstance> caseStages;
    private Collection<CaseMilestoneInstance> caseMilestones;
    private Collection<CaseRoleInstance> caseRoles;
    private CaseFileInstance caseFile;   
    
    private String caseDefinitionId;
    private Integer status;
    private String deploymentId;
    private String owner;
    private Date startedAt;
    private Date completedAt;
    private Long processInstanceId;
    private String completionMessage;
    private Date slaDueDate;
    private Integer slaCompliance;
    
    public CaseInstanceImpl() {
        
    }
    
    public CaseInstanceImpl(String caseId, String caseDescription, Collection<CaseStageInstance> caseStages, Collection<CaseMilestoneInstance> caseMilestones, Collection<CaseRoleInstance> caseRoles, CaseFileInstance caseFile) {
        this.caseId = caseId;
        this.caseDescription = caseDescription;
        this.caseStages = caseStages;
        this.caseMilestones = caseMilestones;
        this.caseRoles = caseRoles;
        this.caseFile = caseFile;
    }
    
    /**
     * Constructor to be used mainly by persistence provider to create instances automatically
     * @param caseId
     * @param caseDescription
     */
    public CaseInstanceImpl(String caseId, String caseDescription, String caseDefinitionId, Integer status, String deploymentId, String owner, Date startedAt, Date completedAt, Long processInstanceId, String completionMessage, Date slaDueDate, Integer slaCompliance) {
        this.caseId = caseId;
        this.caseDescription = caseDescription;
        this.caseDefinitionId = caseDefinitionId;
        this.status = status;
        this.deploymentId = deploymentId;
        this.owner = owner;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.processInstanceId = processInstanceId;
        this.completionMessage = completionMessage;
        this.slaDueDate = slaDueDate;
        this.slaCompliance = slaCompliance;
    }

    @Override
    public String getCaseId() {
        return caseId;
    }

    @Override
    public String getCaseDescription() {
        return caseDescription;
    }
    
    @Override
    public Collection<CaseStageInstance> getCaseStages() {
        return caseStages;
    }

    @Override
    public Collection<CaseMilestoneInstance> getCaseMilestones() {
        return caseMilestones;
    }

    @Override
    public Collection<CaseRoleInstance> getCaseRoles() {
        return caseRoles;
    }

    @Override
    public CaseFileInstance getCaseFile() {
        return caseFile;
    }

    
    public String getCaseDefinitionId() {
        return caseDefinitionId;
    }

    
    public void setCaseDefinitionId(String caseDefinitionId) {
        this.caseDefinitionId = caseDefinitionId;
    }

    
    public Integer getStatus() {
        return status;
    }

    
    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String getDeploymentId() {
        return deploymentId;
    }

    
    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    
    public String getOwner() {
        return owner;
    }

    
    public void setOwner(String owner) {
        this.owner = owner;
    }

    
    public Date getStartedAt() {
        return startedAt;
    }

    
    public void setStartedAt(Date startedAt) {
        this.startedAt = startedAt;
    }

    
    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    
    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    
    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    
    public void setCaseDescription(String caseDescription) {
        this.caseDescription = caseDescription;
    }

    
    public void setCaseStages(Collection<CaseStageInstance> caseStages) {
        this.caseStages = caseStages;
    }

    
    public void setCaseMilestones(Collection<CaseMilestoneInstance> caseMilestones) {
        this.caseMilestones = caseMilestones;
    }

    
    public void setCaseRoles(Collection<CaseRoleInstance> caseRoles) {
        this.caseRoles = caseRoles;
    }

    
    public void setCaseFile(CaseFileInstance caseFile) {
        this.caseFile = caseFile;
    }
    
    public Date getCompletedAt() {
        return completedAt;
    }
    
    public void setCompletedAt(Date completedAt) {
        this.completedAt = completedAt;
    }
    
    public String getCompletionMessage() {
        return completionMessage;
    }
    
    public void setCompletionMessage(String completionMessage) {
        this.completionMessage = completionMessage;
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
        return "CaseInstanceImpl [caseId=" + caseId + ", status=" + status + ", deploymentId=" + deploymentId + ", owner=" + owner + ", processInstanceId=" + processInstanceId + "]";
    }


}
