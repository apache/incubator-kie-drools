/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.index.jpa.model;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.kie.kogito.persistence.postgresql.hibernate.JsonBinaryConverter;

import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity(name = "processes")
@Table(name = "processes")
public class ProcessInstanceEntity extends AbstractEntity {

    @Id
    private String id;
    private String processId;
    private String version;
    private String processName;
    private Integer state;
    private String businessKey;
    private String endpoint;
    @ElementCollection
    @JoinColumn(name = "process_id")
    @CollectionTable(name = "processes_roles", joinColumns = @JoinColumn(name = "process_id", foreignKey = @ForeignKey(name = "fk_processes_roles_processes")))
    @Column(name = "role", nullable = false)
    private Set<String> roles;
    @Column(name = "startTime")
    private ZonedDateTime start;
    @Column(name = "endTime")
    private ZonedDateTime end;
    private String rootProcessInstanceId;
    private String rootProcessId;
    private String parentProcessInstanceId;
    @Column(name = "lastUpdateTime")
    private ZonedDateTime lastUpdate;
    private String createdBy;

    private String updatedBy;
    @Convert(converter = JsonBinaryConverter.class)
    @Column(columnDefinition = "jsonb")
    private ObjectNode variables;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "processInstance")
    private List<NodeInstanceEntity> nodes;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "processInstance")
    private List<MilestoneEntity> milestones;
    @ElementCollection
    @JoinColumn(name = "process_id")
    @CollectionTable(name = "processes_addons", joinColumns = @JoinColumn(name = "process_id", foreignKey = @ForeignKey(name = "fk_processes_addons_processes")))
    @Column(name = "addon", nullable = false)
    private Set<String> addons;
    @Embedded
    private ProcessInstanceErrorEntity error;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public ZonedDateTime getStart() {
        return start;
    }

    public void setStart(ZonedDateTime start) {
        this.start = start;
    }

    public ZonedDateTime getEnd() {
        return end;
    }

    public void setEnd(ZonedDateTime end) {
        this.end = end;
    }

    public String getRootProcessInstanceId() {
        return rootProcessInstanceId;
    }

    public void setRootProcessInstanceId(String rootProcessInstanceId) {
        this.rootProcessInstanceId = rootProcessInstanceId;
    }

    public String getRootProcessId() {
        return rootProcessId;
    }

    public void setRootProcessId(String rootProcessId) {
        this.rootProcessId = rootProcessId;
    }

    public String getParentProcessInstanceId() {
        return parentProcessInstanceId;
    }

    public void setParentProcessInstanceId(String parentProcessInstanceId) {
        this.parentProcessInstanceId = parentProcessInstanceId;
    }

    public ZonedDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(ZonedDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String identity) {
        this.createdBy = identity;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public ObjectNode getVariables() {
        return variables;
    }

    public void setVariables(ObjectNode variables) {
        this.variables = variables;
    }

    public List<NodeInstanceEntity> getNodes() {
        return nodes;
    }

    public void setNodes(List<NodeInstanceEntity> nodes) {
        this.nodes = nodes;
    }

    public List<MilestoneEntity> getMilestones() {
        return milestones;
    }

    public void setMilestones(List<MilestoneEntity> milestones) {
        this.milestones = milestones;
    }

    public Set<String> getAddons() {
        return addons;
    }

    public void setAddons(Set<String> addons) {
        this.addons = addons;
    }

    public ProcessInstanceErrorEntity getError() {
        return error;
    }

    public void setError(ProcessInstanceErrorEntity error) {
        this.error = error;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProcessInstanceEntity that = (ProcessInstanceEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ProcessInstanceEntity{" +
                "id='" + id + '\'' +
                ", processId='" + processId + '\'' +
                ", processName='" + processName + '\'' +
                ", state=" + state +
                ", businessKey='" + businessKey + '\'' +
                ", endpoint='" + endpoint + '\'' +
                ", roles=" + roles +
                ", start=" + start +
                ", end=" + end +
                ", rootProcessInstanceId='" + rootProcessInstanceId + '\'' +
                ", rootProcessId='" + rootProcessId + '\'' +
                ", parentProcessInstanceId='" + parentProcessInstanceId + '\'' +
                ", lastUpdate=" + lastUpdate +
                ", createdBy=" + createdBy +
                ", updatedBy=" + updatedBy +
                ", variables=" + variables +
                ", nodes=" + nodes +
                ", milestones=" + milestones +
                ", addons=" + addons +
                ", error=" + error +
                ", version=" + version +
                '}';
    }
}
