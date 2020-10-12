/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.jobs.api;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Job describes the actual entity that should be scheduled and executed
 * upon given expiration time. The job requires following information
 * <ul>
 *  <li>id - unique UUID based identifier</li>
 *  <li>expirationTime - the time when this job should be executed</li>
 *  <li>callbackEndpoint - the callback endpoint (http/https) that will be invoked upon expiration</li>
 * </ul>
 *
 * On top of that there are additional meta data that points the job to the owner - such as process instance.
 * <ul>
 *  <li>processInstanceId - process instance that owns the job</li>
 *  <li>rootProcessInstanceId - root process instance that the job is part of - is owned as one of the subprocesses of the root process instance</li>
 *  <li>processId - process id of the process instance owning the job</li>
 *  <li>rootProcessId - root process id of the process instance that owns the job</li>
 * </ul>
 */
public class Job {

    private String id;
    private ZonedDateTime expirationTime;
    private Integer priority;
    private String callbackEndpoint;
    private String processInstanceId;
    private String rootProcessInstanceId;
    private String processId;
    private String rootProcessId;
    private String nodeInstanceId;
    private Long repeatInterval;
    private Integer repeatLimit;

    public Job() {
    }

    @SuppressWarnings("squid:S00107")
    public Job(String id, ZonedDateTime expirationTime, Integer priority, String callbackEndpoint,
               String processInstanceId, String rootProcessInstanceId, String processId, String rootProcessId,
               Long repeatInterval, Integer repeatLimit, String nodeInstanceId) {
        this.id = id;
        this.expirationTime = expirationTime;
        this.priority = priority;
        this.callbackEndpoint = callbackEndpoint;
        this.processInstanceId = processInstanceId;
        this.rootProcessInstanceId = rootProcessInstanceId;
        this.processId = processId;
        this.rootProcessId = rootProcessId;
        this.repeatInterval = repeatInterval;
        this.repeatLimit = repeatLimit;
        this.nodeInstanceId = nodeInstanceId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getNodeInstanceId() {
        return nodeInstanceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Job)) {
            return false;
        }
        Job job = (Job) o;
        return Objects.equals(getId(), job.getId()) &&
                Objects.equals(getExpirationTime(), job.getExpirationTime()) &&
                Objects.equals(getPriority(), job.getPriority()) &&
                Objects.equals(getCallbackEndpoint(), job.getCallbackEndpoint()) &&
                Objects.equals(getProcessInstanceId(), job.getProcessInstanceId()) &&
                Objects.equals(getRootProcessInstanceId(), job.getRootProcessInstanceId()) &&
                Objects.equals(getProcessId(), job.getProcessId()) &&
                Objects.equals(getRootProcessId(), job.getRootProcessId()) &&
                Objects.equals(getRepeatLimit(), job.getRepeatLimit()) &&
                Objects.equals(getRepeatInterval(), job.getRepeatInterval()) &&
                Objects.equals(getNodeInstanceId(), job.getNodeInstanceId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getExpirationTime(), getPriority(), getCallbackEndpoint(), getProcessInstanceId(),
                            getRootProcessInstanceId(), getProcessId(), getRootProcessId(), getRepeatLimit(),
                            getRepeatInterval(), getNodeInstanceId());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Job.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("expirationTime=" + expirationTime)
                .add("priority=" + priority)
                .add("callbackEndpoint='" + callbackEndpoint + "'")
                .add("processInstanceId='" + processInstanceId + "'")
                .add("rootProcessInstanceId='" + rootProcessInstanceId + "'")
                .add("processId='" + processId + "'")
                .add("rootProcessId='" + rootProcessId + "'")
                .add("repeatInterval=" + repeatInterval)
                .add("repeatLimit=" + repeatLimit)
                .add("nodeInstanceId=" + nodeInstanceId)
                .toString();
    }
}
