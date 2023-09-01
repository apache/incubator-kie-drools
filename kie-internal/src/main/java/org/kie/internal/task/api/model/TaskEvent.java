package org.kie.internal.task.api.model;

import java.util.Date;

public interface TaskEvent {

    public enum TaskEventType{ADDED, UPDATED, STARTED, ACTIVATED, COMPLETED,
                                STOPPED, EXITED, FAILED,
                                CLAIMED, SKIPPED, SUSPENDED, CREATED,
                                FORWARDED, RELEASED, RESUMED, DELEGATED, NOMINATED};

    long getId();

    long getTaskId();

    TaskEventType getType();

    String getUserId();

    Date getLogTime();

    Long getProcessInstanceId();

    Long getWorkItemId();

    String getMessage();

    String getCorrelationKey();

    Integer getProcessType();

    void setCorrelationKey(String correlationKey);

    void setProcessType(Integer processType);
}
