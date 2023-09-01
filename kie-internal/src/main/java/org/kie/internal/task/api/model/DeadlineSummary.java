package org.kie.internal.task.api.model;

import java.util.Date;

public interface DeadlineSummary {

    long getTaskId();

    void setTaskId(long taskId);

    long getDeadlineId();

    void setDeadlineId(long deadlineId);

    Date getDate();

    void setDate(Date date);
}
