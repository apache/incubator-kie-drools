/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.jobs;

import java.util.UUID;

public class ProcessInstanceJobDescriptionBuilder {
    private String timerId;
    private ExpirationTime expirationTime;
    private Integer priority = ProcessInstanceJobDescription.DEFAULT_PRIORITY;
    private String processInstanceId;
    private String rootProcessInstanceId;
    private String processId;
    private String rootProcessId;
    private String nodeInstanceId;

    public ProcessInstanceJobDescriptionBuilder timerId(String timerId) {
        this.timerId = timerId;
        return this;
    }

    public ProcessInstanceJobDescriptionBuilder generateTimerId() {
        return timerId(UUID.randomUUID().toString());
    }

    public ProcessInstanceJobDescriptionBuilder expirationTime(ExpirationTime expirationTime) {
        this.expirationTime = expirationTime;
        return this;
    }

    public ProcessInstanceJobDescriptionBuilder priority(Integer priority) {
        this.priority = priority;
        return this;
    }

    public ProcessInstanceJobDescriptionBuilder processInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
        return this;
    }

    public ProcessInstanceJobDescriptionBuilder rootProcessInstanceId(String rootProcessInstanceId) {
        this.rootProcessInstanceId = rootProcessInstanceId;
        return this;
    }

    public ProcessInstanceJobDescriptionBuilder processId(String processId) {
        this.processId = processId;
        return this;
    }

    public ProcessInstanceJobDescriptionBuilder rootProcessId(String rootProcessId) {
        this.rootProcessId = rootProcessId;
        return this;
    }

    public ProcessInstanceJobDescriptionBuilder nodeInstanceId(String nodeInstanceId) {
        this.nodeInstanceId = nodeInstanceId;
        return this;
    }

    public ProcessInstanceJobDescription build() {
        return new ProcessInstanceJobDescription(timerId, expirationTime, priority, processInstanceId, rootProcessInstanceId, processId, rootProcessId, nodeInstanceId);
    }
}