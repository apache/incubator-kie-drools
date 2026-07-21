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

public class UserTaskInstanceCommentEventBody {

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
    private String commentId;
    private String commentContent;

    private int eventType;

    private UserTaskInstanceCommentEventBody() {
    }

    public static Builder create() {
        return new Builder(new UserTaskInstanceCommentEventBody());
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

    public String getCommentId() {
        return commentId;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public int getEventType() {
        return eventType;
    }

    @Override
    public String toString() {
        return "UserTaskInstanceCommentEventBody [eventDate=" + eventDate + ", eventUser=" + eventUser + ", userTaskDefinitionId=" + userTaskDefinitionId + ", userTaskInstanceId=" + userTaskInstanceId
                + ", userTaskName=" + userTaskName + ", commentId=" + commentId + ", commentContent=" + commentContent + ", eventType=" + eventType + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserTaskInstanceCommentEventBody other = (UserTaskInstanceCommentEventBody) obj;
        return Objects.equals(commentContent, other.commentContent) && Objects.equals(commentId, other.commentId) && Objects.equals(eventDate, other.eventDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commentContent, commentId, eventDate);
    }

    public Builder update() {
        return new Builder(this);
    }

    public static class Builder {

        private UserTaskInstanceCommentEventBody instance;

        private Builder(UserTaskInstanceCommentEventBody instance) {
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

        public Builder commentId(String commentId) {
            this.instance.commentId = commentId;
            return this;
        }

        public Builder commentContent(String commentContent) {
            this.instance.commentContent = commentContent;
            return this;
        }

        public Builder eventType(int eventType) {
            this.instance.eventType = eventType;
            return this;
        }

        public UserTaskInstanceCommentEventBody build() {
            return instance;
        }
    }
}
