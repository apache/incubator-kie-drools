/*
 * Copyright 2012 JBoss by Red Hat.
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
package org.droolsjbpm.services.impl.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;

/**
 *
 * @author salaboy
 */
@Entity
public class BAMProcessSummary implements Serializable {

    @Id
    @GeneratedValue()
    private Long pk;
    private long processInstanceId;
    private String processName;
    private String status;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date startDate;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date endDate;
    // Initiator
    private String userId;
    private String processVersion;
    private Long duration;

    public BAMProcessSummary() {
    }

    public BAMProcessSummary(long processInstanceId, String name, String status, Date startDate, String userId, String version) {
        this.processInstanceId = processInstanceId;
        this.processName = name;
        this.status = status;
        this.startDate = startDate;

        this.userId = userId;
        this.processVersion = version;
    }

    public long getProcessId() {
        return processInstanceId;
    }

    public void setProcessId(long processId) {
        this.processInstanceId = processId;
    }

    public String getName() {
        return processName;
    }

    public void setName(String name) {
        this.processName = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVersionId() {
        return processVersion;
    }

    public void setVersionId(String version) {
        this.processVersion = version;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "BAMProcessSummary{" + "processId=" + processInstanceId + ", name=" + processName + ", status=" + status + ", startDate=" + startDate + ", endDate=" + endDate + ", userId=" + userId + ", versionId=" + processVersion + ", duration=" + duration + '}';
    }
}
