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
import java.util.Objects;

public class UserTaskInstanceVariableEventBody {

    // common fields for events
    private Date eventDate;
    private String eventUser;

    private String userTaskDefinitionId;
    private String userTaskInstanceId;
    private String userTaskName;

    // custom data fields
    private String variableType; // input / output
    private String variableId; // id
    private String variableName; // name
    private Object variableValue;

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

    public String getVariableType() {
        return variableType;
    }

    public String getVariableId() {
        return variableId;
    }

    public String getVariableName() {
        return variableName;
    }

    public Object getVariableValue() {
        return variableValue;
    }

    @Override
    public String toString() {
        return "UserTaskInstanceVariableEventBody [eventDate=" + eventDate + ", eventUser=" + eventUser + ", userTaskDefinitionId=" + userTaskDefinitionId + ", userTaskInstanceId="
                + userTaskInstanceId + ", userTaskName=" + userTaskName + ", variableType=" + variableType + ", variableId=" + variableId + ", variableName=" + variableName + ", variableValue="
                + variableValue + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventDate, userTaskDefinitionId, userTaskInstanceId, variableId, variableValue);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserTaskInstanceVariableEventBody other = (UserTaskInstanceVariableEventBody) obj;
        return Objects.equals(eventDate, other.eventDate) && Objects.equals(userTaskDefinitionId, other.userTaskDefinitionId) && Objects.equals(userTaskInstanceId, other.userTaskInstanceId)
                && Objects.equals(variableId, other.variableId) && Objects.equals(variableValue, other.variableValue);
    }

    public static Builder create() {
        return new Builder(new UserTaskInstanceVariableEventBody());
    }

    public static class Builder {

        private UserTaskInstanceVariableEventBody instance;

        private Builder(UserTaskInstanceVariableEventBody instance) {
            this.instance = instance;
        }

        public Builder eventDate(Date eventDate) {
            this.instance.eventDate = eventDate;
            return this;
        }

        public Builder eventuser(String userId) {
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

        public Builder variableType(String variableType) {
            this.instance.variableType = variableType;
            return this;
        }

        public Builder variableId(String variableId) {
            this.instance.variableId = variableId;
            return this;
        }

        public Builder variableName(String variableName) {
            this.instance.variableName = variableName;
            return this;
        }

        public Builder variableValue(Object variableValue) {
            this.instance.variableValue = variableValue;
            return this;
        }

        public Builder eventUser(String eventUser) {
            this.instance.eventUser = eventUser;
            return this;
        }

        public UserTaskInstanceVariableEventBody build() {
            return this.instance;
        }
    }
}
