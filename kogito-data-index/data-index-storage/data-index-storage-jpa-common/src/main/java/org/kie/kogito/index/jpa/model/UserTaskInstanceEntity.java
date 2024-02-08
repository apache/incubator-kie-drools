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
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity(name = "tasks")
@Table(name = "tasks")
public class UserTaskInstanceEntity extends AbstractEntity {

    @Id
    private String id;
    private String description;
    private String name;
    private String priority;
    private String processInstanceId;
    private String state;
    private String actualOwner;
    @ElementCollection
    @JoinColumn(name = "task_id")
    @CollectionTable(name = "tasks_admin_groups", joinColumns = @JoinColumn(name = "task_id", foreignKey = @ForeignKey(name = "fk_tasks_admin_groups_tasks")))
    @Column(name = "group_id", nullable = false)
    private Set<String> adminGroups;
    @ElementCollection
    @JoinColumn(name = "task_id")
    @CollectionTable(name = "tasks_admin_users", joinColumns = @JoinColumn(name = "task_id", foreignKey = @ForeignKey(name = "fk_tasks_admin_users_tasks")))
    @Column(name = "user_id", nullable = false)
    private Set<String> adminUsers;
    private ZonedDateTime completed;
    private ZonedDateTime started;
    @ElementCollection
    @JoinColumn(name = "task_id")
    @CollectionTable(name = "tasks_excluded_users", joinColumns = @JoinColumn(name = "task_id", foreignKey = @ForeignKey(name = "fk_tasks_excluded_users_tasks")))
    @Column(name = "user_id", nullable = false)
    private Set<String> excludedUsers;
    @ElementCollection
    @JoinColumn(name = "task_id")
    @CollectionTable(name = "tasks_potential_groups", joinColumns = @JoinColumn(name = "task_id", foreignKey = @ForeignKey(name = "fk_tasks_potential_groups_tasks")))
    @Column(name = "group_id", nullable = false)
    private Set<String> potentialGroups;
    @ElementCollection
    @JoinColumn(name = "task_id")
    @CollectionTable(name = "tasks_potential_users", joinColumns = @JoinColumn(name = "task_id", foreignKey = @ForeignKey(name = "fk_tasks_potential_users_tasks")))
    @Column(name = "user_id", nullable = false)
    private Set<String> potentialUsers;
    private String referenceName;
    private ZonedDateTime lastUpdate;
    private String processId;
    private String rootProcessId;
    private String rootProcessInstanceId;
    @Convert(converter = JsonBinaryConverter.class)
    @Column(columnDefinition = "jsonb")
    private ObjectNode inputs;
    @Convert(converter = JsonBinaryConverter.class)
    @Column(columnDefinition = "jsonb")
    private ObjectNode outputs;
    private String endpoint;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userTask", orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CommentEntity> comments;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userTask", orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AttachmentEntity> attachments;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getActualOwner() {
        return actualOwner;
    }

    public void setActualOwner(String actualOwner) {
        this.actualOwner = actualOwner;
    }

    public Set<String> getAdminGroups() {
        return adminGroups;
    }

    public void setAdminGroups(Set<String> adminGroups) {
        this.adminGroups = adminGroups;
    }

    public Set<String> getAdminUsers() {
        return adminUsers;
    }

    public void setAdminUsers(Set<String> adminUsers) {
        this.adminUsers = adminUsers;
    }

    public ZonedDateTime getCompleted() {
        return completed;
    }

    public void setCompleted(ZonedDateTime completed) {
        this.completed = completed;
    }

    public ZonedDateTime getStarted() {
        return started;
    }

    public void setStarted(ZonedDateTime started) {
        this.started = started;
    }

    public Set<String> getExcludedUsers() {
        return excludedUsers;
    }

    public void setExcludedUsers(Set<String> excludedUsers) {
        this.excludedUsers = excludedUsers;
    }

    public Set<String> getPotentialGroups() {
        return potentialGroups;
    }

    public void setPotentialGroups(Set<String> potentialGroups) {
        this.potentialGroups = potentialGroups;
    }

    public Set<String> getPotentialUsers() {
        return potentialUsers;
    }

    public void setPotentialUsers(Set<String> potentialUsers) {
        this.potentialUsers = potentialUsers;
    }

    public String getReferenceName() {
        return referenceName;
    }

    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    }

    public ZonedDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(ZonedDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getRootProcessId() {
        return rootProcessId;
    }

    public void setRootProcessId(String rootProcessId) {
        this.rootProcessId = rootProcessId;
    }

    public String getRootProcessInstanceId() {
        return rootProcessInstanceId;
    }

    public void setRootProcessInstanceId(String rootProcessInstanceId) {
        this.rootProcessInstanceId = rootProcessInstanceId;
    }

    public ObjectNode getInputs() {
        return inputs;
    }

    public void setInputs(ObjectNode inputs) {
        this.inputs = inputs;
    }

    public ObjectNode getOutputs() {
        return outputs;
    }

    public void setOutputs(ObjectNode outputs) {
        this.outputs = outputs;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public List<CommentEntity> getComments() {
        return comments;
    }

    public void setComments(List<CommentEntity> comments) {
        this.comments = comments;
    }

    public List<AttachmentEntity> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentEntity> attachments) {
        this.attachments = attachments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserTaskInstanceEntity that = (UserTaskInstanceEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "UserTaskInstanceEntity{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", name='" + name + '\'' +
                ", priority='" + priority + '\'' +
                ", processInstanceId='" + processInstanceId + '\'' +
                ", state='" + state + '\'' +
                ", actualOwner='" + actualOwner + '\'' +
                ", adminGroups=" + adminGroups +
                ", adminUsers=" + adminUsers +
                ", completed=" + completed +
                ", started=" + started +
                ", excludedUsers=" + excludedUsers +
                ", potentialGroups=" + potentialGroups +
                ", potentialUsers=" + potentialUsers +
                ", referenceName='" + referenceName + '\'' +
                ", lastUpdate=" + lastUpdate +
                ", processId='" + processId + '\'' +
                ", rootProcessId='" + rootProcessId + '\'' +
                ", rootProcessInstanceId='" + rootProcessInstanceId + '\'' +
                ", inputs=" + inputs +
                ", outputs=" + outputs +
                ", endpoint='" + endpoint + '\'' +
                ", comments='" + comments + '\'' +
                ", attachments='" + attachments + '\'' +
                '}';
    }
}
