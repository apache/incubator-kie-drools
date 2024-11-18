/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.jobs.descriptors;

import java.util.UUID;

import org.kie.kogito.jobs.ExpirationTime;

public class ProcessInstanceJobDescriptionBuilder {

    private String id;
    private String timerId;
    private ExpirationTime expirationTime;
    private Integer priority = ProcessInstanceJobDescription.DEFAULT_PRIORITY;
    private String processInstanceId;
    private String rootProcessInstanceId;
    private String processId;
    private String rootProcessId;
    private String nodeInstanceId;

    public ProcessInstanceJobDescriptionBuilder id(String id) {
        this.id = id;
        return this;
    }

    public ProcessInstanceJobDescriptionBuilder generateId() {
        return id(UUID.randomUUID().toString());
    }

    public ProcessInstanceJobDescriptionBuilder timerId(String timerId) {
        this.timerId = timerId;
        return this;
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
        return new ProcessInstanceJobDescription(id, timerId, expirationTime, priority, processInstanceId, rootProcessInstanceId, processId, rootProcessId, nodeInstanceId);
    }
}