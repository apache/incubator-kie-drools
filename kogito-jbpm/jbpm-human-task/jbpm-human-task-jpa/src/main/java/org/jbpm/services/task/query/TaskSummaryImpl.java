/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.services.task.query;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;
import java.util.List;

import org.kie.api.task.model.Status;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.TaskModelFactory;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.model.InternalTaskSummary;
import org.kie.internal.task.api.model.SubTasksStrategy;

public class TaskSummaryImpl implements InternalTaskSummary {

    private long id;
    private String name = "";
    private String subject = "";
    private String description = "";
    private Status status;
    private String statusId;
    private int priority;
    private boolean skipable;
    private User actualOwner;
    private String actualOwnerId;
    private User createdBy;
    private String createdById;
    private Date createdOn;
    private Date activationTime;
    private Date expirationTime;
    private long processInstanceId;
    private String processId;
    private long processSessionId;
    private String deploymentId;
    private SubTasksStrategy subTaskStrategy;
    private long parentId;
    private boolean quickTaskSummary;

    // JPQL does not accept collections in constructor arguments
    // In short, this means that this field will never be filled
    @Deprecated // remove in 7.0
    private List<String> potentialOwners;

    public TaskSummaryImpl(long id,
            String name,
            String subject,
            String description,
            Status status,
            int priority,
            boolean skipable,
            User actualOwner,
            User createdBy,
            Date createdOn,
            Date activationTime,
            Date expirationTime,
            String processId,
            long processSessionId,
            long processInstanceId,
            String deploymentId,
            SubTasksStrategy subTaskStrategy,
            long parentId) {
        super();
        this.id = id;
        this.name = name;
        this.subject = subject;
        this.description = description;
        this.status = status;
        if (status != null) {
            this.statusId = status.name();
        }
        this.priority = priority;
        this.skipable = skipable;
        this.actualOwner = actualOwner;
        if (actualOwner != null) {
            this.actualOwnerId = actualOwner.getId();
        }
        this.createdBy = createdBy;
        if (createdBy != null) {
            this.createdById = createdBy.getId();
        }
        this.createdOn = createdOn;
        this.activationTime = activationTime;
        this.expirationTime = expirationTime;
        this.processInstanceId = processInstanceId;
        this.processId = processId;
        this.processSessionId = processSessionId;
        this.deploymentId = deploymentId;
        this.subTaskStrategy = subTaskStrategy;
        this.parentId = parentId;

        this.quickTaskSummary = false;
    }

    // Query API constructor
    public TaskSummaryImpl(long id,
            String name, String subject, String description,
            Status status, int priority, boolean skipable,
            String actualOwnerId, String createdById,
            Date createdOn, Date activationTime, Date expirationTime,
            String processId, long processSessionId, long processInstanceId, String deploymentId,
            SubTasksStrategy subTaskStrategy,
            long parentId) {
        super();
        this.id = id;
        this.name = name;
        this.subject = subject;
        this.description = description;
        this.status = status;
        if (status != null) {
            this.statusId = status.name();
        }
        this.priority = priority;
        this.skipable = skipable;

        this.actualOwnerId = actualOwnerId;
        if( this.actualOwnerId != null && ! this.actualOwnerId.isEmpty() ) {
            this.actualOwner = TaskModelProvider.getFactory().newUser(this.actualOwnerId);
        }
        this.createdById = createdById;
        if( this.createdById != null && ! this.createdById.isEmpty() ) {
            this.createdBy = TaskModelProvider.getFactory().newUser(this.createdById);
        }

        this.createdOn = createdOn;
        this.activationTime = activationTime;
        this.expirationTime = expirationTime;
        this.processInstanceId = processInstanceId;
        this.processId = processId;
        this.processSessionId = processSessionId;
        this.deploymentId = deploymentId;
        this.subTaskStrategy = subTaskStrategy;
        this.parentId = parentId;

        this.quickTaskSummary = false;
    }

    /*
     * Construct a QuickTaskSummary
     */
    public TaskSummaryImpl(long id,
            String name,
            String subject, 
            String description,
            Status status,
            int priority,
            String actualOwner,
            String createdBy,
            Date createdOn,
            Date activationTime,
            Date expirationTime,
            String processId,
            long processInstanceId,
            long parentId,
            String deploymentId,
            boolean skipable) {
        this.id = id;
        this.processInstanceId = processInstanceId;
        this.name = name;
        this.subject = subject;
        this.description = description;
        this.status = status;
        if (status != null) {
            this.statusId = status.name();
        }
        this.priority = priority;
        this.actualOwnerId = actualOwner;
        this.createdById = createdBy;
        this.createdOn = createdOn;
        this.activationTime = activationTime;
        this.expirationTime = expirationTime;
        this.processId = processId;
        this.parentId = parentId;
        this.deploymentId = deploymentId;
        this.skipable = skipable;
        this.quickTaskSummary = true;
    }

    public TaskSummaryImpl() {
        // JAXB constructor
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(id);

        out.writeLong(processInstanceId);

        if (name != null) {
            out.writeBoolean(true);
            out.writeUTF(name);
        } else {
            out.writeBoolean(false);
        }

        if (subject != null) {
            out.writeBoolean(true);
            out.writeUTF(subject);
        } else {
            out.writeBoolean(false);
        }

        if (description != null) {
            out.writeBoolean(true);
            out.writeUTF(description);
        } else {
            out.writeBoolean(false);
        }

        if (status != null) {
            out.writeBoolean(true);
            out.writeUTF(status.toString());
        } else {
            out.writeBoolean(false);
        }

        out.writeInt(priority);
        out.writeLong(parentId);
        out.writeBoolean(skipable);

        if (actualOwner != null) {
            out.writeBoolean(true);
            actualOwner.writeExternal(out);
        } else {
            out.writeBoolean(false);
        }

        if (createdBy != null) {
            out.writeBoolean(true);
            createdBy.writeExternal(out);
        } else {
            out.writeBoolean(false);
        }
        if (createdOn != null) {
            out.writeBoolean(true);
            out.writeLong(createdOn.getTime());
        } else {
            out.writeBoolean(false);
        }

        if (activationTime != null) {
            out.writeBoolean(true);
            out.writeLong(activationTime.getTime());
        } else {
            out.writeBoolean(false);
        }

        if (expirationTime != null) {
            out.writeBoolean(true);
            out.writeLong(expirationTime.getTime());
        } else {
            out.writeBoolean(false);
        }

        if (processId != null) {
            out.writeBoolean(true);
            out.writeUTF(processId);
        } else {
            out.writeBoolean(false);
        }

        out.writeLong(processSessionId);

        if (subTaskStrategy != null) {
            out.writeBoolean(true);
            out.writeUTF(subTaskStrategy.toString());
        } else {
            out.writeBoolean(false);
        }

        if (actualOwnerId != null) {
            out.writeBoolean(true);
            out.writeUTF(actualOwnerId);
        } else {
            out.writeBoolean(false);
        }

        if (createdById != null) {
            out.writeBoolean(true);
            out.writeUTF(createdById);
        } else {
            out.writeBoolean(false);
        }

        if (statusId != null) {
            out.writeBoolean(true);
            out.writeUTF(statusId);
        } else {
            out.writeBoolean(false);
        }

        out.writeBoolean(quickTaskSummary);
    }

    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        id = in.readLong();

        processInstanceId = in.readLong();

        if (in.readBoolean()) {
            name = in.readUTF();
        }

        if (in.readBoolean()) {
            subject = in.readUTF();
        }

        if (in.readBoolean()) {
            description = in.readUTF();
        }

        if (in.readBoolean()) {
            status = Status.valueOf(in.readUTF());
        }

        priority = in.readInt();
        parentId = in.readLong();
        skipable = in.readBoolean();

        if (in.readBoolean()) {
            actualOwner = TaskModelProvider.getFactory().newUser();
            actualOwner.readExternal(in);
        }

        if (in.readBoolean()) {
            createdBy = TaskModelProvider.getFactory().newUser();
            createdBy.readExternal(in);
        }

        if (in.readBoolean()) {
            createdOn = new Date(in.readLong());
        }

        if (in.readBoolean()) {
            activationTime = new Date(in.readLong());
        }

        if (in.readBoolean()) {
            expirationTime = new Date(in.readLong());
        }

        if (in.readBoolean()) {
            processId = in.readUTF();
        }

        processSessionId = in.readLong();

        if (in.readBoolean()) {
            subTaskStrategy = SubTasksStrategy.valueOf(in.readUTF());
        }

        if (in.readBoolean()) {
            actualOwnerId = in.readUTF();
        }

        if (in.readBoolean()) {
            createdById = in.readUTF();
        }

        if (in.readBoolean()) {
            statusId = in.readUTF();
        }

        quickTaskSummary = in.readBoolean();
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Boolean isSkipable() {
        return skipable;
    }

    public void setSkipable(boolean skipable) {
        this.skipable = skipable;
    }

    public User getActualOwner() {
        if(quickTaskSummary && actualOwnerId != null && !actualOwnerId.equals("")){
            actualOwner = TaskModelProvider.getFactory().newUser(actualOwnerId);
        }
        return actualOwner;
    }

    public void setActualOwner(User actualOwner) {
        this.actualOwner = actualOwner;
    }

    public User getCreatedBy() {
        if(quickTaskSummary && createdById != null && !createdById.equals("")){
            createdBy = TaskModelProvider.getFactory().newUser(createdById);
        }
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getActivationTime() {
        return activationTime;
    }

    public void setActivationTime(Date activationTime) {
        this.activationTime = activationTime;
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public Long getProcessSessionId() {
        return processSessionId;
    }

    public void setProcessSessionId(long processSessionId) {
        this.processSessionId = processSessionId;
    }

    public SubTasksStrategy getSubTaskStrategy() {
        return subTaskStrategy;
    }

    public void setSubTaskStrategy(SubTasksStrategy subTaskStrategy) {
        this.subTaskStrategy = subTaskStrategy;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    @Deprecated // remove in 7.0
    public List<String> getPotentialOwners() {
        return potentialOwners;
    }

    @Deprecated // remove in 7.0
    public void setPotentialOwners(List<String> potentialOwners) {
        this.potentialOwners = potentialOwners;
    }

    public Boolean isQuickTaskSummary() {
        return quickTaskSummary;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((activationTime == null) ? 0 : activationTime.hashCode());
        result = prime * result + ((actualOwner == null) ? 0 : actualOwner.hashCode());
        result = prime * result + ((createdBy == null) ? 0 : createdBy.hashCode());
        result = prime * result + ((createdOn == null) ? 0 : createdOn.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((expirationTime == null) ? 0 : expirationTime.hashCode());
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + (int) (processInstanceId ^ (processInstanceId >>> 32));
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((subTaskStrategy == null) ? 0 : subTaskStrategy.hashCode());
        result = prime * result + priority;
        result = prime * result + (int) (parentId ^ (parentId >>> 32));
        result = prime * result + (skipable ? 1231 : 1237);
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((subject == null) ? 0 : subject.hashCode());
        result = prime * result + ((processId == null) ? 0 : processId.hashCode());
        result = prime * result + (int) (processSessionId ^ (processSessionId >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof TaskSummaryImpl)) {
            return false;
        }
        TaskSummaryImpl other = (TaskSummaryImpl) obj;
        if (processInstanceId != other.processInstanceId) {
            return false;
        }
        if (activationTime == null) {
            if (other.activationTime != null) {
                return false;
            }
        } else if (activationTime.getTime() != other.activationTime.getTime()) {
            return false;
        }
        if (actualOwner == null) {
            if (other.actualOwner != null) {
                return false;
            }
        } else if (!actualOwner.equals(other.actualOwner)) {
            return false;
        }
        if (createdBy == null) {
            if (other.createdBy != null) {
                return false;
            }
        } else if (!createdBy.equals(other.createdBy)) {
            return false;
        }
        if (createdOn == null) {
            if (other.createdOn != null) {
                return false;
            }
        } else if (createdOn.getTime() != other.createdOn.getTime()) {
            return false;
        }
        if (description == null) {
            if (other.description != null) {
                return false;
            }
        } else if (!description.equals(other.description)) {
            return false;
        }
        if (expirationTime == null) {
            if (other.expirationTime != null) {
                return false;
            }
        } else if (expirationTime.getTime() != other.expirationTime.getTime()) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (subTaskStrategy == null) {
            if (other.subTaskStrategy != null) {
                return false;
            }
        } else if (!subTaskStrategy.equals(other.subTaskStrategy)) {
            return false;
        }
        if (priority != other.priority) {
            return false;
        }
        if (parentId != other.parentId) {
            return false;
        }
        if (skipable != other.skipable) {
            return false;
        }
        if (status == null) {
            if (other.status != null) {
                return false;
            }
        } else if (!status.equals(other.status)) {
            return false;
        }
        if (subject == null) {
            if (other.subject != null) {
                return false;
            }
        } else if (!subject.equals(other.subject)) {
            return false;
        }
        if (processId == null) {
            if (other.processId != null) {
                return false;
            }
        } else if (!processId.equals(other.processId)) {
            return false;
        }
        if (processSessionId != other.processSessionId) {
            return false;
        }
        return true;
    }

    @Override
    public String getStatusId() {
        return statusId;
    }

    @Override
    public String getActualOwnerId() {
        return actualOwnerId;
    }

    @Override
    public String getCreatedById() {
        return createdById;
    }

    @Override
    public String getDeploymentId() {
        return deploymentId;
    }

    @Override
    public String toString() {
        return "TaskSummaryImpl{" + "id=" + id + ", name=" + name + ", subject=" + subject + ", description=" + description + ", statusId=" + statusId + ", priority=" + priority + ", skipable=" + skipable + ", actualOwnerId=" + actualOwnerId + ", createdById=" + createdById + ", createdOn=" + createdOn + ", activationTime=" + activationTime + ", expirationTime=" + expirationTime + ", processInstanceId=" + processInstanceId + ", processId=" + processId + ", processSessionId=" + processSessionId + ", deploymentId=" + deploymentId + ", parentId=" + parentId + ", potentialOwners=" + potentialOwners + ", quickTaskSummary=" + quickTaskSummary + '}';
    }



}
