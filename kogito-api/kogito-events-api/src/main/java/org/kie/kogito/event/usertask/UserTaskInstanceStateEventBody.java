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
package org.kie.kogito.event.usertask;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.kie.kogito.event.process.ProcessInstanceEventMetadata;

public class UserTaskInstanceStateEventBody {

    // common fields for events
    private Date eventDate;
    private String eventUser;

    private String userTaskDefinitionId;
    private String userTaskInstanceId;
    private String userTaskName;

    private String userTaskDescription;
    private String userTaskPriority;
    private String userTaskReferenceName;

    private String state;

    private String actualOwner;

    private String processInstanceId;

    private String eventType;

    private String externalReferenceId;

    private Date slaDueDate;

    public Date getEventDate() {
        return eventDate;
    }

    public String getEventUser() {
        return eventUser;
    }

    public String getUserTaskDefinitionId() {
        return userTaskDefinitionId;
    }

    public String getUserTaskInstanceId() {
        return userTaskInstanceId;
    }

    public String getUserTaskName() {
        return userTaskName;
    }

    public String getUserTaskDescription() {
        return userTaskDescription;
    }

    public String getUserTaskPriority() {
        return userTaskPriority;
    }

    public String getUserTaskReferenceName() {
        return userTaskReferenceName;
    }

    public String getState() {
        return state;
    }

    public String getActualOwner() {
        return actualOwner;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getExternalReferenceId() {
        return externalReferenceId;
    }

    public Date getSlaDueDate() {
        return slaDueDate;
    }

    public Map<String, Object> metaData() {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put(UserTaskInstanceEventMetadata.USER_TASK_INSTANCE_ID_META_DATA, userTaskInstanceId);
        metadata.put(ProcessInstanceEventMetadata.PROCESS_INSTANCE_ID_META_DATA, processInstanceId);
        metadata.put(UserTaskInstanceEventMetadata.USER_TASK_INSTANCE_STATE_META_DATA, state);
        return metadata;
    }

    @Override
    public String toString() {
        return "UserTaskInstanceStateEventBody [eventDate=" + eventDate + ", eventUser=" + eventUser + ", userTaskDefinitionId=" + userTaskDefinitionId + ", userTaskInstanceId=" + userTaskInstanceId
                + ", userTaskName=" + userTaskName + ", userTaskDescription=" + userTaskDescription + ", userTaskPriority=" + userTaskPriority + ", userTaskReferenceName="
                + userTaskReferenceName
                + ", state=" + state + ", actualOwner=" + actualOwner + ", processInstanceId=" + processInstanceId + ", slaDueDate=" + slaDueDate + "]";
    }

    public Builder update() {
        return new Builder(this);
    }

    public static Builder create() {
        return new Builder(new UserTaskInstanceStateEventBody());
    }

    @Override
    public int hashCode() {
        return Objects.hash(userTaskInstanceId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserTaskInstanceStateEventBody other = (UserTaskInstanceStateEventBody) obj;
        return Objects.equals(userTaskInstanceId, other.userTaskInstanceId);
    }

    public static class Builder {

        private UserTaskInstanceStateEventBody instance;

        private Builder(UserTaskInstanceStateEventBody instance) {
            this.instance = instance;
        }

        public Builder eventDate(Date eventDate) {
            this.instance.eventDate = eventDate;
            return this;
        }

        public Builder eventUser(String userId) {
            this.instance.eventUser = userId;
            return this;
        }

        public Builder userTaskDefinitionId(String userTaskDefinitionId) {
            this.instance.userTaskDefinitionId = userTaskDefinitionId;
            return this;
        }

        public Builder userTaskInstanceId(String userTaskInstanceId) {
            this.instance.userTaskInstanceId = userTaskInstanceId;
            return this;
        }

        public Builder userTaskName(String userTaskName) {
            this.instance.userTaskName = userTaskName;
            return this;
        }

        public Builder userTaskDescription(String userTaskDescription) {
            this.instance.userTaskDescription = userTaskDescription;
            return this;
        }

        public Builder userTaskPriority(String userTaskPriority) {
            this.instance.userTaskPriority = userTaskPriority;
            return this;
        }

        public Builder userTaskReferenceName(String userTaskReferenceName) {
            this.instance.userTaskReferenceName = userTaskReferenceName;
            return this;
        }

        public Builder state(String state) {
            this.instance.state = state;
            return this;
        }

        public Builder actualOwner(String userId) {
            this.instance.actualOwner = userId;
            return this;
        }

        public Builder externalReferenceId(String externalReferenceId) {
            this.instance.externalReferenceId = externalReferenceId;
            return this;
        }

        public Builder eventType(String eventType) {
            this.instance.eventType = eventType;
            return this;
        }

        public Builder processInstanceId(String processInstanceId) {
            this.instance.processInstanceId = processInstanceId;
            return this;
        }

        public Builder slaDueDate(Date slaDueDate) {
            this.instance.slaDueDate = slaDueDate;
            return this;
        }

        public UserTaskInstanceStateEventBody build() {
            return this.instance;
        }
    }

}
