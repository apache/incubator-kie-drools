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

package org.jbpm.persistence.scripts.oldentities;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;

@Entity
@SequenceGenerator(name = "processInstanceInfoIdSeq", sequenceName = "PROCESS_INSTANCE_INFO_ID_SEQ")
public class ProcessInstanceInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "processInstanceInfoIdSeq")
    @Column(name = "InstanceId")
    private Long processInstanceId;

    @Version
    @Column(name = "OPTLOCK")
    private int version;

    private String processId;
    private Date startDate;
    private Date lastReadDate;
    private Date lastModificationDate;
    private int state;

    @Lob
    @Column(length = 2147483647)
    byte[] processInstanceByteArray;

    @ElementCollection
    @CollectionTable(name = "EventTypes", joinColumns = @JoinColumn(name = "InstanceId"))
    @Column(name = "element")
    private Set<String> eventTypes = new HashSet<String>();

    public ProcessInstanceInfo() {
    }

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public Long getId() {
        return processInstanceId;
    }

    public String getProcessId() {
        return processId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getLastModificationDate() {
        return lastModificationDate;
    }

    public Date getLastReadDate() {
        return lastReadDate;
    }

    public int getState() {
        return state;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ProcessInstanceInfo other = (ProcessInstanceInfo) obj;
        if (this.processInstanceId != other.processInstanceId && (this.processInstanceId == null || !this
                .processInstanceId.equals(other.processInstanceId))) {
            return false;
        }
        if (this.version != other.version) {
            return false;
        }
        if ((this.processId == null) ? (other.processId != null) : !this.processId.equals(other.processId)) {
            return false;
        }
        if (this.startDate != other.startDate && (this.startDate == null || !this.startDate.equals(other.startDate))) {
            return false;
        }
        if (this.lastReadDate != other.lastReadDate && (this.lastReadDate == null || !this.lastReadDate.equals(other
                .lastReadDate))) {
            return false;
        }
        if (this.lastModificationDate != other.lastModificationDate && (this.lastModificationDate == null || !this
                .lastModificationDate.equals(other.lastModificationDate))) {
            return false;
        }
        if (this.state != other.state) {
            return false;
        }
        if (!Arrays.equals(this.processInstanceByteArray,
                other.processInstanceByteArray)) {
            return false;
        }
        if (this.eventTypes != other.eventTypes && (this.eventTypes == null || !this.eventTypes.equals(other
                .eventTypes))) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + (this.processInstanceId != null ? this.processInstanceId.hashCode() : 0);
        hash = 61 * hash + this.version;
        hash = 61 * hash + (this.processId != null ? this.processId.hashCode() : 0);
        hash = 61 * hash + (this.startDate != null ? this.startDate.hashCode() : 0);
        hash = 61 * hash + (this.lastReadDate != null ? this.lastReadDate.hashCode() : 0);
        hash = 61 * hash + (this.lastModificationDate != null ? this.lastModificationDate.hashCode() : 0);
        hash = 61 * hash + this.state;
        hash = 61 * hash + Arrays.hashCode(this.processInstanceByteArray);
        hash = 61 * hash + (this.eventTypes != null ? this.eventTypes.hashCode() : 0);
        return hash;
    }

    public int getVersion() {
        return version;
    }

    public Set<String> getEventTypes() {
        return eventTypes;
    }

    public byte[] getProcessInstanceByteArray() {
        return processInstanceByteArray;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setLastReadDate(Date lastReadDate) {
        this.lastReadDate = lastReadDate;
    }

    public void setLastModificationDate(Date lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setProcessInstanceByteArray(byte[] processInstanceByteArray) {
        this.processInstanceByteArray = processInstanceByteArray;
    }

    public void setEventTypes(Set<String> eventTypes) {
        this.eventTypes = eventTypes;
    }
}