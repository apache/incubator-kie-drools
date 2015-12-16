/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.api.task.model;


import java.io.Externalizable;
import java.util.Date;
import java.util.List;

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

}
