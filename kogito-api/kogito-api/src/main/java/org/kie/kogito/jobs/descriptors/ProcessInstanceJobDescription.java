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

import org.kie.kogito.jobs.ExpirationTime;
import org.kie.kogito.jobs.JobDescription;

import static java.util.Objects.requireNonNull;

public class ProcessInstanceJobDescription implements JobDescription {

    public static final Integer DEFAULT_PRIORITY = 5;

    private final String id;
    private final String timerId;
    private final ExpirationTime expirationTime;
    private final Integer priority;
    private final String processInstanceId;
    private final String rootProcessInstanceId;
    private final String processId;
    private final String rootProcessId;
    private final String nodeInstanceId;

    public ProcessInstanceJobDescription(String id,
            String timerId,
            ExpirationTime expirationTime,
            Integer priority,
            String processInstanceId,
            String rootProcessInstanceId,
            String processId,
            String rootProcessId,
            String nodeInstanceId) {
        this.id = requireNonNull(id);
        this.timerId = requireNonNull(timerId);
        this.expirationTime = requireNonNull(expirationTime);
        this.priority = requireNonNull(priority);
        this.processInstanceId = requireNonNull(processInstanceId);
        this.rootProcessInstanceId = rootProcessInstanceId;
        this.processId = processId;
        this.rootProcessId = rootProcessId;
        this.nodeInstanceId = nodeInstanceId;
    }

    @Override
    public String id() {
        return id;
    }

    public String timerId() {
        return timerId;
    }

    @Override
    public ExpirationTime expirationTime() {
        return expirationTime;
    }

    @Override
    public Integer priority() {
        return priority;
    }

    public String processInstanceId() {
        return processInstanceId;
    }

    public String rootProcessInstanceId() {
        return rootProcessInstanceId;
    }

    public String processId() {
        return processId;
    }

    public String rootProcessId() {
        return rootProcessId;
    }

    public String nodeInstanceId() {
        return nodeInstanceId;
    }

    public static ProcessInstanceJobDescriptionBuilder newProcessInstanceJobDescriptionBuilder() {
        return new ProcessInstanceJobDescriptionBuilder();
    }

    @Override
    public String path() {
        return JOBS_CALLBACK_URI + "/"
                + processId()
                + "/instances/"
                + processInstanceId()
                + "/timers/"
                + timerId();

    }

    @Override
    public String toString() {
        return "ProcessInstanceJobDescription{" +
                "id='" + id + '\'' +
                ", timerId=" + timerId + '\'' +
                ", expirationTime=" + expirationTime +
                ", priority=" + priority +
                ", processInstanceId='" + processInstanceId + '\'' +
                ", rootProcessInstanceId='" + rootProcessInstanceId + '\'' +
                ", processId='" + processId + '\'' +
                ", rootProcessId='" + rootProcessId + '\'' +
                ", nodeInstanceId='" + nodeInstanceId + '\'' +
                '}';
    }
}
