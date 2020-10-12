/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.jobs.api;

import java.time.ZonedDateTime;

public class JobBuilder {

    private String id;
    private ZonedDateTime expirationTime;
    private Integer priority;
    private String callbackEndpoint;
    private String processInstanceId;
    private String rootProcessInstanceId;
    private String processId;
    private String rootProcessId;
    private Long repeatInterval;
    private Integer repeatLimit;
    private String nodeInstanceId;

    public JobBuilder id(String id) {
        this.id = id;
        return this;
    }

    public JobBuilder expirationTime(ZonedDateTime expirationTime) {
        this.expirationTime = expirationTime;
        return this;
    }

    public JobBuilder priority(Integer priority) {
        this.priority = priority;
        return this;
    }

    public JobBuilder callbackEndpoint(String callbackEndpoint) {
        this.callbackEndpoint = callbackEndpoint;
        return this;
    }

    public JobBuilder processInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
        return this;
    }

    public JobBuilder rootProcessInstanceId(String rootProcessInstanceId) {
        this.rootProcessInstanceId = rootProcessInstanceId;
        return this;
    }

    public JobBuilder processId(String processId) {
        this.processId = processId;
        return this;
    }

    public JobBuilder rootProcessId(String rootProcessId) {
        this.rootProcessId = rootProcessId;
        return this;
    }

    public JobBuilder repeatInterval(Long repeatInterval) {
        this.repeatInterval = repeatInterval;
        return this;
    }

    public JobBuilder repeatLimit(Integer repeatLimit) {
        this.repeatLimit = repeatLimit;
        return this;
    }

    public JobBuilder nodeInstanceId(String nodeInstanceId) {
        this.nodeInstanceId = nodeInstanceId;
        return this;
    }

    public Job build() {
        return new Job(id, expirationTime, priority, callbackEndpoint, processInstanceId, rootProcessInstanceId,
                       processId, rootProcessId, repeatInterval, repeatLimit, nodeInstanceId);
    }

    public static JobBuilder builder(){
        return new JobBuilder();
    }
}