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
import java.util.Map;

public class UserTaskInstanceDeadlineEventBody {

    // common fields for events
    private Date eventDate;
    private String eventUser;

    private String userTaskDefinitionId;
    private String userTaskInstanceId;
    private String userTaskName;

    // custom data fields

    private Map<String, Object> notification;

    private Map<String, Object> inputs;

    private String eventType;

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

    public Map<String, Object> getNotification() {
        return notification;
    }

    public Map<String, Object> getInputs() {
        return inputs;
    }

    public String getEventType() {
        return eventType;
    }

    @Override
    public String toString() {
        return "UserTaskInstanceDeadlineEventBody [eventDate=" + eventDate + ", eventUser=" + eventUser + ", userTaskDefinitionId=" + userTaskDefinitionId + ", userTaskInstanceId="
                + userTaskInstanceId + ", userTaskName=" + userTaskName + ", notification=" + notification + ", inputs=" + inputs + ", eventType=" + eventType + "]";
    }

    public static Builder create() {
        return new Builder(new UserTaskInstanceDeadlineEventBody());
    }

    public static class Builder {

        private UserTaskInstanceDeadlineEventBody instance;

        protected Builder(UserTaskInstanceDeadlineEventBody instance) {
            this.instance = instance;
        }

        public Builder eventDate(Date eventDate) {
            this.instance.eventDate = eventDate;
            return this;
        }

        public Builder eventUser(String eventUser) {
            this.instance.eventUser = eventUser;
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

        public Builder notification(Map<String, Object> notification) {
            this.instance.notification = notification;
            return this;
        }

        public Builder inputs(Map<String, Object> inputs) {
            this.instance.inputs = inputs;
            return this;
        }

        public Builder eventType(String eventType) {
            this.instance.eventType = eventType;
            return this;
        }

        public UserTaskInstanceDeadlineEventBody build() {
            return instance;
        }
    }

}
