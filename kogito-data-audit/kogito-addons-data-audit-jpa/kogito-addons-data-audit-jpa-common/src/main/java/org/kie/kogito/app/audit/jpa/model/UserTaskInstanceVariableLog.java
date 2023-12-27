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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "Task_Instance_Variable_Log")
@SequenceGenerator(name = "taskInstanceVariableLogIdSeq", sequenceName = "TASK_INSTANCE_VARIABLE_LOG_ID_SEQ")
public class UserTaskInstanceVariableLog extends AbstractUserTaskInstanceLog {

    public enum VariableType {
        INPUT,
        OUTPUT;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "taskInstanceVariableLogIdSeq")
    private Long id;

    @Column(name = "variable_id")
    private String variableId;

    @Column(name = "variable_name")
    private String variableName;

    @Column(name = "variable_value")
    private String variableValue;

    @Column(name = "variable_type")
    @Enumerated(EnumType.STRING)
    private VariableType variableType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVariableId() {
        return variableId;
    }

    public void setVariableId(String variableId) {
        this.variableId = variableId;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public String getVariableValue() {
        return variableValue;
    }

    public void setVariableValue(String variableValue) {
        this.variableValue = variableValue;
    }

    public VariableType getVariableType() {
        return variableType;
    }

    public void setVariableType(VariableType type) {
        this.variableType = type;
    }

}