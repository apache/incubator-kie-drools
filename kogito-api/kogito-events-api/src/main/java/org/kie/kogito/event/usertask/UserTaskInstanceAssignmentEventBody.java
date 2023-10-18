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
import java.util.List;
import java.util.Objects;

public class UserTaskInstanceAssignmentEventBody {

    // common fields for events
    private Date eventDate;
    private String eventUser;

    private String userTaskDefinitionId;
    private String userTaskInstanceId;
    private String userTaskName;

    // custom data 
    private String assignmentType; // POT OWNERS, ADMIN...

    private List<String> users;

    private String eventType; //ADDED REMOVED

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

    public String getAssignmentType() {
        return assignmentType;
    }

    public List<String> getUsers() {
        return users;
    }

    public String getEventType() {
        return eventType;
    }

    @Override
    public String toString() {
        return "UserTaskInstanceAssignmentEventBody [eventDate=" + eventDate + ", eventUser=" + eventUser + ", userTaskDefinitionId=" + userTaskDefinitionId + ", userTaskInstanceId="
                + userTaskInstanceId + ", userTaskName=" + userTaskName + ", assignmentType=" + assignmentType + ", users=" + users + ", eventType=" + eventType + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(assignmentType, userTaskInstanceId, users);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserTaskInstanceAssignmentEventBody other = (UserTaskInstanceAssignmentEventBody) obj;
        return Objects.equals(assignmentType, other.assignmentType) && Objects.equals(userTaskInstanceId, other.userTaskInstanceId) && Objects.equals(users, other.users);
    }

    public static Builder create() {
        return new Builder(new UserTaskInstanceAssignmentEventBody());
    }

    public static class Builder {

        private UserTaskInstanceAssignmentEventBody instance;

        private Builder(UserTaskInstanceAssignmentEventBody instance) {
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

        public Builder assignmentType(String assignmentType) {
            this.instance.assignmentType = assignmentType;
            return this;
        }

        public Builder users(String... usersId) {
            this.instance.users = List.of(usersId);
            return this;
        }

        public Builder eventType(String eventType) {
            this.instance.eventType = eventType;
            return this;
        }

        public UserTaskInstanceAssignmentEventBody build() {
            return this.instance;
        }
    }

}
