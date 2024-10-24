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

public class TaskDataEntityPK implements Serializable {

    private String name;
    private UserTaskInstanceEntity taskInstance;

    public TaskDataEntityPK() {
    }

    public TaskDataEntityPK(String inputName, UserTaskInstanceEntity taskInstance) {
        this.taskInstance = taskInstance;
        this.name = inputName;
    }

    public UserTaskInstanceEntity getTaskInstance() {
        return taskInstance;
    }

    public void setTaskInstance(UserTaskInstanceEntity taskInstance) {
        this.taskInstance = taskInstance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TaskDataEntityPK that = (TaskDataEntityPK) o;
        return Objects.equals(getName(), that.getName()) && Objects.equals(getTaskInstance(), that.getTaskInstance());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getTaskInstance());
    }

    @Override
    public String toString() {
        return "TaskInputEntityId{" +
                "taskInstance='" + taskInstance + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
