package org.kie.internal.task.api;

import java.util.Date;

public interface AuditTask {
    long getTaskId();

    void setTaskId(long taskId);

    String getStatus();

    void setStatus(String status);

    Date getActivationTime();

    void setActivationTime(Date activationTime);

    String getName();

    void setName(String name);

    String getDescription();

    void setDescription(String description);

    int getPriority();

    void setPriority(int priority);

    String getCreatedBy();

    void setCreatedBy(String createdBy);

    Date getCreatedOn();

    void setCreatedOn(Date createdOn);

    Date getDueDate();

    void setDueDate(Date dueDate);

    long getProcessInstanceId();

    void setProcessInstanceId(String processInstanceId);

    String getProcessId();

    void setProcessId(String processId);

    long getProcessSessionId();

    void setProcessSessionId(long processSessionId);

    long getParentId();

    void setParentId(long parentId);

    String getActualOwner();

    void setActualOwner(String actualOwner);

    String getDeploymentId();

    void setDeploymentId(String deploymentId);

    long getWorkItemId();

    void setWorkItemId(long workItemId);


}
