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

package org.jbpm.kie.services.impl.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jbpm.services.api.admin.MigrationEntry;


public class MigrationReportImpl implements org.jbpm.services.api.admin.MigrationReport, Serializable {

    private static final long serialVersionUID = 549850629493262347L;

    private boolean successful;
    
    private Date startDate;
    private Date endDate;
    
    private Long processInstanceId;
    
    private List<MigrationEntry> entries = new ArrayList<MigrationEntry>();

    public MigrationReportImpl(Long processInstanceId, boolean successful, Date startDate, Date endDate, List<MigrationEntry> entries) {
        this.processInstanceId = processInstanceId;
        this.successful = successful;
        this.startDate = startDate;
        this.endDate = endDate;
        this.entries = entries;
    }

    public boolean isSuccessful() {
        return successful;
    }
    
    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }
    
    public Date getStartDate() {
        return startDate;
    }
    
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    
    public Date getEndDate() {
        return endDate;
    }
    
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }    
    
    public Long getProcessInstanceId() {
        return processInstanceId;
    }
    
    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public List<MigrationEntry> getEntries() {
        return entries;
    }
    
    public void setEntries(List<MigrationEntry> entries) {
        this.entries = entries;
    }
}
