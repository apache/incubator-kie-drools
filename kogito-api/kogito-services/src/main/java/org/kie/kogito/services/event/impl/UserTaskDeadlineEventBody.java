/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.services.event.impl;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.kie.kogito.process.workitem.Attachment;
import org.kie.kogito.process.workitem.Comment;

public class UserTaskDeadlineEventBody {

    private Map<String, Object> notification;
    private String id;
    private String taskName;
    private String taskDescription;
    private String taskPriority;
    private String referenceName;
    private Date startDate;
    private String state;
    private String actualOwner;
    private Set<String> potentialUsers;
    private Set<String> potentialGroups;

    private Map<String, Object> inputs;
    private Map<String, Object> outputs;
    private String processInstanceId;
    private String rootProcessInstanceId;
    private String processId;
    private String rootProcessId;
    private Collection<Comment> comments;
    private Collection<Attachment> attachments;

    public UserTaskDeadlineEventBody() {
    }

    private UserTaskDeadlineEventBody(String id, Map<String, Object> notification) {
        this.id = id;
        this.notification = notification;
    }

    public String getId() {
        return id;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public String getTaskPriority() {
        return taskPriority;
    }

    public String getReferenceName() {
        return referenceName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public String getState() {
        return state;
    }

    public String getActualOwner() {
        return actualOwner;
    }

    public Map<String, Object> getInputs() {
        return inputs;
    }

    public Map<String, Object> getOutputs() {
        return outputs;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public String getRootProcessInstanceId() {
        return rootProcessInstanceId;
    }

    public String getProcessId() {
        return processId;
    }

    public String getRootProcessId() {
        return rootProcessId;
    }

    public static Builder create(String id, Map<String, Object> notification) {
        return new Builder(new UserTaskDeadlineEventBody(id, notification));
    }

    public Map<String, Object> getNotification() {
        return notification;
    }

    public Set<String> getPotentialUsers() {
        return potentialUsers;
    }

    public Set<String> getPotentialGroups() {
        return potentialGroups;
    }

    public Collection<Comment> getComments() {
        return comments;
    }

    public Collection<Attachment> getAttachments() {
        return attachments;
    }

    public static class Builder {

        private UserTaskDeadlineEventBody instance;

        protected Builder(UserTaskDeadlineEventBody instance) {
            this.instance = instance;
        }

        public Builder taskName(String taskName) {
            instance.taskName = taskName;
            return this;
        }

        public Builder taskDescription(String taskDescription) {
            instance.taskDescription = taskDescription;
            return this;
        }

        public Builder taskPriority(String taskPriority) {
            instance.taskPriority = taskPriority;
            return this;
        }

        public Builder referenceName(String referenceName) {
            instance.referenceName = referenceName;
            return this;
        }

        public Builder state(String state) {
            instance.state = state;
            return this;
        }

        public Builder actualOwner(String actualOwner) {
            instance.actualOwner = actualOwner;
            return this;
        }

        public Builder startDate(Date startDate) {
            instance.startDate = startDate;
            return this;
        }

        public Builder inputs(Map<String, Object> inputs) {
            instance.inputs = inputs;
            return this;
        }

        public Builder outputs(Map<String, Object> outputs) {
            instance.outputs = outputs;
            return this;
        }

        public Builder processInstanceId(String processInstanceId) {
            instance.processInstanceId = processInstanceId;
            return this;
        }

        public Builder rootProcessInstanceId(String rootProcessInstanceId) {
            instance.rootProcessInstanceId = rootProcessInstanceId;
            return this;
        }

        public Builder processId(String processId) {
            instance.processId = processId;
            return this;
        }

        public Builder rootProcessId(String rootProcessId) {
            instance.rootProcessId = rootProcessId;
            return this;
        }

        public Builder potentialUsers(Set<String> potentialUsers) {
            instance.potentialUsers = potentialUsers;
            return this;
        }

        public Builder potentialGroups(Set<String> potentialGroups) {
            instance.potentialGroups = potentialGroups;
            return this;
        }

        public Builder comments(Collection<Comment> comments) {
            instance.comments = comments;
            return this;
        }

        public Builder attachments(Collection<Attachment> attachments) {
            instance.attachments = attachments;
            return this;
        }

        public UserTaskDeadlineEventBody build() {
            return instance;
        }
    }

    @Override
    public String toString() {
        return "UserTaskDeadlineEventBody [notification=" + notification + ", id=" + id + ", taskName=" + taskName +
                ", taskDescription=" + taskDescription + ", taskPriority=" + taskPriority + ", referenceName=" +
                referenceName + ", startDate=" + startDate + ", state=" + state + ", actualOwner=" + actualOwner +
                ", potentialUsers=" + potentialUsers + ", potentialGroups=" + potentialGroups + ", inputs=" + inputs +
                ", outputs=" + outputs + ", processInstanceId=" + processInstanceId + ", rootProcessInstanceId=" +
                rootProcessInstanceId + ", processId=" + processId + ", rootProcessId=" + rootProcessId +
                ", comments=" + comments + ", attachments=" + attachments + "]";
    }
}
