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

package org.jbpm.usertask.jpa.model;

import java.util.Objects;

import jakarta.persistence.AssociationOverride;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "jbpm_user_tasks_reassignment_timer")
@AttributeOverrides({
        @AttributeOverride(name = "jobId", column = @Column(name = "reassignment_job_id")),
        @AttributeOverride(name = "value", column = @Column(name = "reassignment_value"))
})
@AssociationOverride(name = "taskInstance",
        joinColumns = @JoinColumn(name = "task_id", foreignKey = @ForeignKey(name = "fk_jbpm_user_tasks_reassignment_timer_tid")))
public class TaskReassignmentTimerEntity extends TaskTimerDataEntity<byte[]> {

    @Column(name = "reassignment_type")
    @Enumerated(EnumType.STRING)
    private TaskReassignmentType type;

    public TaskReassignmentType getType() {
        return type;
    }

    public void setType(TaskReassignmentType type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getJobId(), getValue(), getJavaType(), type);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        TaskReassignmentTimerEntity other = (TaskReassignmentTimerEntity) obj;
        return super.equals(obj) && type == other.type;
    }
}
