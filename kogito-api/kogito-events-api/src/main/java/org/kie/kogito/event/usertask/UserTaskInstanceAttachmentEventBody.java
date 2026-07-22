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

import java.net.URI;
import java.util.Date;
import java.util.Objects;

public class UserTaskInstanceAttachmentEventBody {
    public static final int EVENT_TYPE_ADDED = 1;
    public static final int EVENT_TYPE_CHANGE = 2;
    public static final int EVENT_TYPE_DELETED = 3;

    // common fields for events
    private Date eventDate;
    private String eventUser;

    private String userTaskDefinitionId;
    private String userTaskInstanceId;
    private String userTaskName;

    // custome data fields
    private String attachmentId;
    private String attachmentName;
    private URI attachmentURI;

    private int eventType;

    public static Builder create() {
        return new Builder(new UserTaskInstanceAttachmentEventBody());
    }

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

    public String getAttachmentId() {
        return attachmentId;
    }

    public String getAttachmentName() {
        return attachmentName;
    }

    public URI getAttachmentURI() {
        return attachmentURI;
    }

    public int getEventType() {
        return eventType;
    }

    @Override
    public String toString() {
        return "UserTaskInstanceAttachmentEventBody [eventDate=" + eventDate + ", eventUser=" + eventUser + ", userTaskDefinitionId=" + userTaskDefinitionId + ", userTaskInstanceId="
                + userTaskInstanceId + ", userTaskName=" + userTaskName + ", attachmentId=" + attachmentId + ", attachmentName=" + attachmentName + ", attachmentURI=" + attachmentURI
                + ", eventType=" + eventType + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(attachmentId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserTaskInstanceAttachmentEventBody other = (UserTaskInstanceAttachmentEventBody) obj;
        return Objects.equals(attachmentId, other.attachmentId);
    }

    public static class Builder {

        private UserTaskInstanceAttachmentEventBody instance;

        private Builder(UserTaskInstanceAttachmentEventBody instance) {
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

        public Builder attachmentId(String attachmentId) {
            this.instance.attachmentId = attachmentId;
            return this;
        }

        public Builder attachmentName(String attachmentName) {
            this.instance.attachmentName = attachmentName;
            return this;
        }

        public Builder attachmentURI(URI attachmentURI) {
            this.instance.attachmentURI = attachmentURI;
            return this;
        }

        public Builder eventType(int eventType) {
            this.instance.eventType = eventType;
            return this;
        }

        public UserTaskInstanceAttachmentEventBody build() {
            return this.instance;
        }
    }

}
