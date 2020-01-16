/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.index.model;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties("executionResponse")
@SuppressWarnings("Duplicates")
public class Job {

    private String id;
    private String processId;
    private String processInstanceId;
    private String rootProcessId;
    private String rootProcessInstanceId;

    private ZonedDateTime expirationTime;
    private Integer priority;
    private String callbackEndpoint;
    private Long repeatInterval;
    private Integer repeatLimit;

    //ScheduledJob
    private String scheduledId;
    private Integer retries;
    private String status;
    private ZonedDateTime lastUpdate;
    private Integer executionCounter;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getRootProcessInstanceId() {
        return rootProcessInstanceId;
    }

    public void setRootProcessInstanceId(String rootProcessInstanceId) {
        this.rootProcessInstanceId = rootProcessInstanceId;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getRootProcessId() {
        return rootProcessId;
    }

    public void setRootProcessId(String rootProcessId) {
        this.rootProcessId = rootProcessId;
    }

    public ZonedDateTime getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(ZonedDateTime expirationTime) {
        this.expirationTime = expirationTime;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getCallbackEndpoint() {
        return callbackEndpoint;
    }

    public void setCallbackEndpoint(String callbackEndpoint) {
        this.callbackEndpoint = callbackEndpoint;
    }

    public Long getRepeatInterval() {
        return repeatInterval;
    }

    public void setRepeatInterval(Long repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

    public Integer getRepeatLimit() {
        return repeatLimit;
    }

    public void setRepeatLimit(Integer repeatLimit) {
        this.repeatLimit = repeatLimit;
    }

    public String getScheduledId() {
        return scheduledId;
    }

    public void setScheduledId(String scheduledId) {
        this.scheduledId = scheduledId;
    }

    public Integer getRetries() {
        return retries;
    }

    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ZonedDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(ZonedDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Integer getExecutionCounter() {
        return executionCounter;
    }

    public void setExecutionCounter(Integer executionCounter) {
        this.executionCounter = executionCounter;
    }

    @Override
    public String toString() {
        return "Job{" +
                "id='" + id + '\'' +
                ", processId='" + processId + '\'' +
                ", processInstanceId='" + processInstanceId + '\'' +
                ", rootProcessId='" + rootProcessId + '\'' +
                ", rootProcessInstanceId='" + rootProcessInstanceId + '\'' +
                ", expirationTime=" + expirationTime +
                ", priority=" + priority +
                ", callbackEndpoint='" + callbackEndpoint + '\'' +
                ", repeatInterval=" + repeatInterval +
                ", repeatLimit=" + repeatLimit +
                ", scheduledId='" + scheduledId + '\'' +
                ", retries=" + retries +
                ", status=" + status +
                ", lastUpdate=" + lastUpdate +
                ", executionCounter=" + executionCounter +
                '}';
    }
}
