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

package org.kie.kogito.app.audit.jpa.model;

import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "Task_Instance_Assignment_Log")
@SequenceGenerator(name = "taskInstanceAssignmentLogIdSeq", sequenceName = "TASK_INSTANCE_ASSIGNMENT_LOG_ID_SEQ")
public class UserTaskInstanceAssignmentLog extends AbstractUserTaskInstanceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "taskInstanceAssignmentLogIdSeq")
    private Long id;

    @Column(name = "task_definition_id")
    private String userTaskDefinitionId;

    @Column(name = "task_instance_id")
    private String userTaskInstanceId;

    @Column(name = "task_name")
    private String userTaskName;

    @Column(name = "assignment_type")
    private String assignmentType; // POT OWNERS, ADMIN...

    @ElementCollection
    @CollectionTable(name = "Task_Instance_Assignment_Users_Log", joinColumns = @JoinColumn(name = "task_instance_assignment_log_id"),
            foreignKey = @ForeignKey(name = "fk_task_instance_assignment_log_tid"))
    @Column(name = "user_id")
    private List<String> users;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserTaskDefinitionId() {
        return userTaskDefinitionId;
    }

    public void setUserTaskDefinitionId(String userTaskDefinitionId) {
        this.userTaskDefinitionId = userTaskDefinitionId;
    }

    public String getUserTaskInstanceId() {
        return userTaskInstanceId;
    }

    public void setUserTaskInstanceId(String userTaskInstanceId) {
        this.userTaskInstanceId = userTaskInstanceId;
    }

    public String getUserTaskName() {
        return userTaskName;
    }

    public void setUserTaskName(String userTaskName) {
        this.userTaskName = userTaskName;
    }

    public String getAssignmentType() {
        return assignmentType;
    }

    public void setAssignmentType(String assignmentType) {
        this.assignmentType = assignmentType;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

}