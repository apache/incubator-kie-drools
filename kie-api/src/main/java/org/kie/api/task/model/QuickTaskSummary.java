/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
