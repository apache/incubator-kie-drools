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

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
@IdClass(TaskTimerDataEntityPK.class)
public abstract class TaskTimerDataEntity<T> extends AbstractTaskEntity<T> {

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "task_id")
    protected UserTaskInstanceEntity taskInstance;

    @Id
    @Column(name = "jobId")
    protected String jobId;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public UserTaskInstanceEntity getTaskInstance() {
        return taskInstance;
    }

    public void setTaskInstance(UserTaskInstanceEntity taskInstance) {
        this.taskInstance = taskInstance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TaskTimerDataEntity<?> that = (TaskTimerDataEntity<?>) o;
        return Objects.equals(getJobId(), that.getJobId()) && super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getJobId(), getValue(), getJavaType());
    }
}
