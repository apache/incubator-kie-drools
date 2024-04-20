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
package org.kie.kogito.jobs.service.model;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.StringJoiner;

import org.kie.kogito.timer.Trigger;

/**
 * Represents a Job Instance on the Job Service. This instance may be persisted and loaded at any point in time.
 */
public class JobDetails {

    private String id;//the unique id internally on the job service
    private String correlationId; //the job id on the runtimes, for instance
    private JobStatus status;
    private ZonedDateTime lastUpdate;
    private Integer retries;
    private Integer priority;
    private Integer executionCounter;//number of times the job was executed
    private String scheduledId;//the execution control on the scheduler (id on vertx.setTimer, quartzId...)
    private Recipient recipient;//http callback, event topic
    private Trigger trigger;//when/how it should be executed
    private Long executionTimeout;
    private ChronoUnit executionTimeoutUnit;
    private ZonedDateTime created;

    @SuppressWarnings("java:S107")
    protected JobDetails(String id, String correlationId, JobStatus status, ZonedDateTime lastUpdate, Integer retries,
            Integer executionCounter, String scheduledId, Recipient recipient, Trigger trigger, Integer priority,
            Long executionTimeout, ChronoUnit executionTimeoutUnit, ZonedDateTime created) {
        this.id = id;
        this.correlationId = correlationId;
        this.status = status;
        this.lastUpdate = lastUpdate;
        this.retries = retries;
        this.executionCounter = executionCounter;
        this.scheduledId = scheduledId;
        this.recipient = recipient;
        this.trigger = trigger;
        this.priority = priority;
        this.executionTimeout = executionTimeout;
        this.executionTimeoutUnit = executionTimeoutUnit;
        this.created = created;
    }

    public String getId() {
        return id;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public JobStatus getStatus() {
        return status;
    }

    public ZonedDateTime getLastUpdate() {
        return lastUpdate;
    }

    public Integer getRetries() {
        return retries;
    }

    public Integer getExecutionCounter() {
        return executionCounter;
    }

    public String getScheduledId() {
        return scheduledId;
    }

    public Recipient getRecipient() {
        return recipient;
    }

    public Trigger getTrigger() {
        return trigger;
    }

    public Integer getPriority() {
        return priority;
    }

    public Long getExecutionTimeout() {
        return executionTimeout;
    }

    public ChronoUnit getExecutionTimeoutUnit() {
        return executionTimeoutUnit;
    }

    public ZonedDateTime getCreated() {
        return created;
    }

    public static JobDetailsBuilder builder() {
        return new JobDetailsBuilder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JobDetails)) {
            return false;
        }
        JobDetails that = (JobDetails) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getCorrelationId(), that.getCorrelationId()) &&
                Objects.equals(getStatus(), that.getStatus()) &&
                (Objects.equals(getLastUpdate(), that.getLastUpdate()) || Objects.isNull(getLastUpdate()) || Objects.isNull(that.getLastUpdate())) &&
                Objects.equals(getRetries(), that.getRetries()) &&
                Objects.equals(getExecutionCounter(), that.getExecutionCounter()) &&
                Objects.equals(getScheduledId(), that.getScheduledId()) &&
                Objects.equals(getRecipient(), that.getRecipient()) &&
                Objects.equals(getTrigger().hasNextFireTime(), that.getTrigger().hasNextFireTime()) &&
                Objects.equals(getExecutionTimeout(), that.getExecutionTimeout()) &&
                Objects.equals(getExecutionTimeoutUnit(), that.getExecutionTimeoutUnit()) &&
                Objects.equals(getCreated(), that.getCreated());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getCorrelationId(), getStatus(), getRetries(), getExecutionCounter(),
                getScheduledId(), getRecipient(), getTrigger(), getExecutionTimeout(), getExecutionTimeoutUnit(), getCreated());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", JobDetails.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("correlationId='" + correlationId + "'")
                .add("status=" + status)
                .add("lastUpdate=" + lastUpdate)
                .add("retries=" + retries)
                .add("executionCounter=" + executionCounter)
                .add("scheduledId='" + scheduledId + "'")
                .add("recipient=" + recipient)
                .add("trigger=" + trigger)
                .add("executionTimeout=" + executionTimeout)
                .add("executionTimeoutUnit=" + executionTimeoutUnit)
                .add("created=" + created)
                .toString();
    }
}
