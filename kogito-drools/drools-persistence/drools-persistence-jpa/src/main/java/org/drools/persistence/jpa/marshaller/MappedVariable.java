/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.persistence.jpa.marshaller;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;

@Entity
@SequenceGenerator(name="mappedVarIdSeq", sequenceName="MAPPED_VAR_ID_SEQ")
public class MappedVariable implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator="mappedVarIdSeq")
    private Long   mappedVarId;

    @Version
    @Column(name = "OPTLOCK")
    private int    version;

    private Long variableId;

    private String variableType;

    private Long processInstanceId;
    private Long taskId;
    private Long workItemId;

    public MappedVariable() {

    }

    public MappedVariable(Long variableId, String variableType, Long processInstanceId) {
        this.variableId = variableId;
        this.variableType = variableType;
        this.processInstanceId = processInstanceId;
    }

    public MappedVariable(Long variableId, String variableType, Long processInstanceId, Long taskId, Long workItemId) {
        this.variableId = variableId;
        this.variableType = variableType;
        this.processInstanceId = processInstanceId;
        this.taskId = taskId;
        this.workItemId = workItemId;
    }

    public Long getMappedVarId() {
        return mappedVarId;
    }

    public void setMappedVarId(Long mappedVarId) {
        this.mappedVarId = mappedVarId;
    }

    public Long getVariableId() {
        return variableId;
    }

    public void setVariableId(Long variableId) {
        this.variableId = variableId;
    }

    public String getVariableType() {
        return variableType;
    }

    public void setVariableType(String variableType) {
        this.variableType = variableType;
    }

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getWorkItemId() {
        return workItemId;
    }

    public void setWorkItemId(Long workItemId) {
        this.workItemId = workItemId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MappedVariable that = (MappedVariable) o;

        if (processInstanceId != null ? !processInstanceId.equals(that.processInstanceId) : that.processInstanceId != null) {
            return false;
        }
        if (taskId != null ? !taskId.equals(that.taskId) : that.taskId != null) {
            return false;
        }
        if (variableId != null ? !variableId.equals(that.variableId) : that.variableId != null) {
            return false;
        }
        if (variableType != null ? !variableType.equals(that.variableType) : that.variableType != null) {
            return false;
        }
        if (workItemId != null ? !workItemId.equals(that.workItemId) : that.workItemId != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = variableId != null ? variableId.hashCode() : 0;
        result = 31 * result + (variableType != null ? variableType.hashCode() : 0);
        result = 31 * result + (processInstanceId != null ? processInstanceId.hashCode() : 0);
        result = 31 * result + (taskId != null ? taskId.hashCode() : 0);
        result = 31 * result + (workItemId != null ? workItemId.hashCode() : 0);
        return result;
    }
}
