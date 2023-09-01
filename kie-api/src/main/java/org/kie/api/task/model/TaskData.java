package org.kie.api.task.model;

import java.io.Externalizable;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface TaskData extends Externalizable {

    Status getStatus();

    Status getPreviousStatus();

    User getActualOwner();

    User getCreatedBy();

    Date getCreatedOn();

    Date getActivationTime();

    Date getExpirationTime();

    boolean isSkipable();

    long getWorkItemId();

    long getProcessInstanceId();

    String getProcessId();

    String getDeploymentId();

    long getProcessSessionId();

    String getDocumentType();

    long getDocumentContentId();

    String getOutputType();

    Long getOutputContentId();

    String getFaultName();

    String getFaultType();

    long getFaultContentId();

    List<Comment> getComments();

    List<Attachment> getAttachments();

    long getParentId();

    Map<String, Object> getTaskInputVariables();

    Map<String, Object> getTaskOutputVariables();

}
