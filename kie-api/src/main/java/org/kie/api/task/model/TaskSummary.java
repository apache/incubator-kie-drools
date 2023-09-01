package org.kie.api.task.model;

import java.util.List;

public interface TaskSummary extends QuickTaskSummary {

    String getSubject();

    String getDescription();

    Status getStatus();

    Boolean isSkipable();

    User getActualOwner();

    User getCreatedBy();

    Long getProcessSessionId();

    @Deprecated // remove in 7.0 since this field is never filled
    List<String> getPotentialOwners();

    Boolean isQuickTaskSummary();

    String getCorrelationKey();

    Integer getProcessType();

}
