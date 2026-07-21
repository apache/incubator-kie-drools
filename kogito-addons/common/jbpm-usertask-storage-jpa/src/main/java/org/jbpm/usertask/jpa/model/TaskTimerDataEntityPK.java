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

import java.io.Serializable;
import java.util.Objects;

public class TaskTimerDataEntityPK implements Serializable {

    private static final long serialVersionUID = 5506586793841760884L;

    private String jobId;
    private UserTaskInstanceEntity taskInstance;

    public TaskTimerDataEntityPK() {
    }

    public TaskTimerDataEntityPK(String jobId, UserTaskInstanceEntity taskInstance) {
        this.taskInstance = taskInstance;
        this.jobId = jobId;
    }

    public UserTaskInstanceEntity getTaskInstance() {
        return taskInstance;
    }

    public void setTaskInstance(UserTaskInstanceEntity taskInstance) {
        this.taskInstance = taskInstance;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TaskTimerDataEntityPK that = (TaskTimerDataEntityPK) o;
        return Objects.equals(getJobId(), that.getJobId()) && Objects.equals(getTaskInstance().getId(), that.getTaskInstance().getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getJobId(), getTaskInstance());
    }

    @Override
    public String toString() {
        return "TaskTimerDataEntityId {" +
                "taskInstance='" + taskInstance.getId() + '\'' +
                ", name='" + jobId + '\'' +
                '}';
    }
}
