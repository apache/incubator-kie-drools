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
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

import org.kie.kogito.jobs.api.Job;
import org.kie.kogito.jobs.api.JobBuilder;
import org.kie.kogito.jobs.service.utils.DateUtil;

public class ScheduledJob extends Job {

    private String scheduledId;
    private Integer retries;
    private JobStatus status;
    private ZonedDateTime lastUpdate;
    private Integer executionCounter;
    private JobExecutionResponse executionResponse;

    public ScheduledJob() {
    }

    private ScheduledJob(Optional<Job> job,
            Optional<ZonedDateTime> expirationTime) {
        super(job.map(Job::getId).orElse(null),
                expirationTime.orElse(job.map(Job::getExpirationTime).orElse(null)),
                job.map(Job::getPriority).orElse(null),
                job.map(Job::getCallbackEndpoint).orElse(null),
                job.map(Job::getProcessInstanceId).orElse(null),
                job.map(Job::getRootProcessInstanceId).orElse(null),
                job.map(Job::getProcessId).orElse(null),
                job.map(Job::getRootProcessId).orElse(null),
                job.map(Job::getRepeatInterval).orElse(null),
                job.map(Job::getRepeatLimit).orElse(null),
                job.map(Job::getNodeInstanceId).orElse(null));
    }

    public String getScheduledId() {
        return scheduledId;
    }

    public Integer getRetries() {
        return retries;
    }

    public JobStatus getStatus() {
        return status;
    }

    public ZonedDateTime getLastUpdate() {
        return lastUpdate;
    }

    public JobExecutionResponse getExecutionResponse() {
        return executionResponse;
    }

    public Integer getExecutionCounter() {
        return executionCounter;
    }

    public Optional<Long> hasInterval() {
        return Optional.ofNullable(getRepeatInterval())
                .filter(interval -> interval > 0);
    }

    public static ScheduledJobBuilder builder() {
        return new ScheduledJobBuilder();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ScheduledJob.class.getSimpleName() + "[", "]")
                .add("scheduledId='" + scheduledId + "'")
                .add("retries=" + retries)
                .add("status=" + status)
                .add("lastUpdate=" + lastUpdate)
                .add("executionResponse=" + executionResponse)
                .add("executionCounter=" + executionCounter)
                .add("job=" + super.toString())
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ScheduledJob)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        ScheduledJob that = (ScheduledJob) o;
        return Objects.equals(getScheduledId(), that.getScheduledId()) &&
                Objects.equals(getRetries(), that.getRetries()) &&
                getStatus() == that.getStatus() &&
                getLastUpdate().equals(that.getLastUpdate()) &&
                Objects.equals(getExecutionCounter(), that.getExecutionCounter()) &&
                Objects.equals(getExecutionResponse(), that.getExecutionResponse());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getScheduledId(), getRetries(), getStatus(), getLastUpdate(), getExecutionCounter(), getExecutionResponse());
    }

    public static class ScheduledJobBuilder {

        private Job job;
        private String scheduledId;
        private Integer retries = 0;
        private JobStatus status;
        private ZonedDateTime lastUpdate;
        private ZonedDateTime expirationTime;
        private JobExecutionResponse executionResponse;
        private Integer executionCounter = 0;

        public ScheduledJobBuilder job(Job job) {
            this.job = job;
            return this;
        }

        private ScheduledJobBuilder mergeJob(Job mergeJob) {
            final Optional<Job> j = Optional.ofNullable(mergeJob);
            return job(JobBuilder.builder()
                    .id(j.map(Job::getId).orElse(job.getId()))
                    .repeatInterval(j.map(Job::getRepeatInterval).orElse(job.getRepeatInterval()))
                    .repeatLimit(j.map(Job::getRepeatLimit).orElse(job.getRepeatLimit()))
                    .priority(j.map(Job::getPriority).orElse(job.getPriority()))
                    .callbackEndpoint(j.map(Job::getCallbackEndpoint).orElse(job.getCallbackEndpoint()))
                    .rootProcessId(j.map(Job::getRootProcessId).orElse(job.getRootProcessId()))
                    .processId(j.map(Job::getProcessId).orElse(job.getProcessId()))
                    .rootProcessInstanceId(j.map(Job::getRootProcessInstanceId).orElse(job.getRootProcessInstanceId()))
                    .processInstanceId(j.map(Job::getProcessInstanceId).orElse(job.getProcessInstanceId()))
                    .expirationTime(j.map(Job::getExpirationTime).orElse(job.getExpirationTime()))
                    .nodeInstanceId(j.map(Job::getNodeInstanceId).orElse(job.getNodeInstanceId()))
                    .build());
        }

        public ScheduledJobBuilder scheduledId(String scheduledId) {
            this.scheduledId = scheduledId;
            return this;
        }

        public ScheduledJobBuilder expirationTime(ZonedDateTime expirationTime) {
            this.expirationTime = expirationTime;
            return this;
        }

        public ScheduledJobBuilder retries(Integer retries) {
            this.retries = retries;
            return this;
        }

        public ScheduledJobBuilder incrementExecutionCounter() {
            this.executionCounter++;
            return this;
        }

        public ScheduledJobBuilder incrementRetries() {
            this.retries++;
            return this;
        }

        public ScheduledJobBuilder of(ScheduledJob scheduledJob) {
            return job(scheduledJob)
                    .scheduledId(scheduledJob.getScheduledId())
                    .retries(scheduledJob.getRetries())
                    .status(scheduledJob.getStatus())
                    .executionResponse(scheduledJob.getExecutionResponse())
                    .executionCounter(scheduledJob.getExecutionCounter());
        }

        public ScheduledJobBuilder merge(ScheduledJob scheduledJob) {
            final Optional<ScheduledJob> j = Optional.ofNullable(scheduledJob);
            return mergeJob(scheduledJob)
                    .scheduledId(j.map(ScheduledJob::getScheduledId).orElse(scheduledId))
                    .retries(j.map(ScheduledJob::getRetries).orElse(retries))
                    .status(j.map(ScheduledJob::getStatus).orElse(status))
                    .executionResponse(j.map(ScheduledJob::getExecutionResponse).orElse(executionResponse))
                    .executionCounter(j.map(ScheduledJob::getExecutionCounter).orElse(executionCounter));
        }

        public ScheduledJobBuilder status(JobStatus status) {
            this.status = status;
            return this;
        }

        public ScheduledJobBuilder lastUpdate(ZonedDateTime time) {
            this.lastUpdate = time;
            return this;
        }

        public ScheduledJobBuilder executionResponse(JobExecutionResponse executionResponse) {
            this.executionResponse = executionResponse;
            return this;
        }

        public ScheduledJobBuilder executionCounter(Integer executionCounter) {
            this.executionCounter = executionCounter;
            return this;
        }

        public static ScheduledJob from(Job job) {
            return builder().job(job).build();
        }

        public ScheduledJob build() {
            ScheduledJob instance = new ScheduledJob(Optional.ofNullable(job), Optional.ofNullable(expirationTime));
            instance.scheduledId = scheduledId;
            instance.retries = retries;
            instance.status = status;
            instance.lastUpdate = getLastUpdate();
            instance.executionCounter = executionCounter;
            instance.executionResponse = executionResponse;
            return instance;
        }

        private ZonedDateTime getLastUpdate() {
            return Optional.ofNullable(lastUpdate).orElseGet(DateUtil::now);
        }
    }
}
