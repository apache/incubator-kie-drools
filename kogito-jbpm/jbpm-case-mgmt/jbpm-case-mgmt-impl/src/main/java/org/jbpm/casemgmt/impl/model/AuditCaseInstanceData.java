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

package org.jbpm.casemgmt.impl.model;

import java.io.Serializable;
import java.util.List;

import org.jbpm.casemgmt.impl.audit.CaseFileDataLog;
import org.jbpm.casemgmt.impl.audit.CaseRoleAssignmentLog;

public class AuditCaseInstanceData implements Serializable {

    private static final long serialVersionUID = -5706018774172157108L;

    private String caseId;

    private Long processInstanceId;

    private List<CaseFileDataLog> caseFileData;

    private List<CaseRoleAssignmentLog> caseRoleAssignments;

    public AuditCaseInstanceData(String caseId, List<CaseFileDataLog> caseFileData, List<CaseRoleAssignmentLog> caseRoleAssignments) {
        this.caseId = caseId;
        this.caseFileData = caseFileData;
        this.caseRoleAssignments = caseRoleAssignments;
    }

    public AuditCaseInstanceData(Long processInstanceId, String caseId, List<CaseFileDataLog> caseFileData, List<CaseRoleAssignmentLog> caseRoleAssignments) {
        this.processInstanceId = processInstanceId;
        this.caseId = caseId;
        this.caseFileData = caseFileData;
        this.caseRoleAssignments = caseRoleAssignments;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public List<CaseFileDataLog> getCaseFileData() {
        return caseFileData;
    }

    public void setCaseFileData(List<CaseFileDataLog> caseFileData) {
        this.caseFileData = caseFileData;
    }

    public List<CaseRoleAssignmentLog> getCaseRoleAssignments() {
        return caseRoleAssignments;
    }

    public void setCaseRoleAssignments(List<CaseRoleAssignmentLog> caseRoleAssignments) {
        this.caseRoleAssignments = caseRoleAssignments;
    }

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    @Override
    public String toString() {
        return "AuditCaseInstanceData [caseId=" + caseId + ", processInstanceId=" + processInstanceId + ", caseFileData=" + caseFileData + ", caseRoleAssignments=" + caseRoleAssignments + "]";
    }

}
