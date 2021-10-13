/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
