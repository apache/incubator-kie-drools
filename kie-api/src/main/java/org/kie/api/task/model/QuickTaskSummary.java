package org.kie.api.task.model;

import java.io.Externalizable;
import java.util.Date;

public interface QuickTaskSummary extends Externalizable {

    Long getId();

    String getName();

    String getStatusId();

    Integer getPriority();

    String getActualOwnerId();

    String getCreatedById();

    Date getCreatedOn();

    Date getActivationTime();

    Date getExpirationTime();

    String getProcessId();

    Long getProcessInstanceId();

    String getDeploymentId();

    Long getParentId();

}
