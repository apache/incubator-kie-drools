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

package org.kie.kogito.jobs.service.model;

import java.time.ZonedDateTime;
import java.util.Optional;

import org.kie.kogito.jobs.api.Job;
import org.kie.kogito.jobs.service.utils.DateUtil;

public class ScheduledJob {

    private Job job;
    private String scheduledId;
    private Integer retries;
    private JobStatus status;
    private ZonedDateTime lastUpdate;
    private JobExecutionResponse executionResponse;

    public ScheduledJob() {
    }

    public ScheduledJob(Job job, String scheduledId, Integer retries, JobStatus status, ZonedDateTime lastUpdate,
                        JobExecutionResponse executionResponse) {
        this.job = job;
        this.scheduledId = scheduledId;
        this.retries = retries;
        this.status = status;
        this.lastUpdate = lastUpdate;
        this.executionResponse = executionResponse;
    }

    public Job getJob() {
        return job;
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

    public static ScheduledJobBuilder builder() {
        return new ScheduledJobBuilder();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ScheduledJob{");
        sb.append("job=").append(job);
        sb.append(", scheduledId='").append(scheduledId).append('\'');
        sb.append(", retries=").append(retries);
        sb.append(", status=").append(status);
        sb.append(", lastUpdate=").append(lastUpdate);
        sb.append('}');
        return sb.toString();
    }

    public static class ScheduledJobBuilder {

        private Job job;
        private String scheduledId;
        private Integer retries = 0;
        private JobStatus status;
        private ZonedDateTime lastUpdate;
        private JobExecutionResponse executionResponse;

        public ScheduledJobBuilder job(Job job) {
            this.job = job;
            return this;
        }

        public ScheduledJobBuilder scheduledId(String scheduledId) {
            this.scheduledId = scheduledId;
            return this;
        }

        public ScheduledJobBuilder retries(Integer retries) {
            this.retries = retries;
            return this;
        }

        public ScheduledJobBuilder incrementRetries() {
            this.retries++;
            return this;
        }

        public ScheduledJobBuilder of(ScheduledJob scheduledJob) {
            return job(scheduledJob.getJob())
                    .scheduledId(scheduledJob.getScheduledId())
                    .retries(scheduledJob.getRetries())
                    .status(scheduledJob.getStatus());
        }

        public ScheduledJobBuilder status(JobStatus status) {
            this.status = status;
            return this;
        }

        public ScheduledJobBuilder lastUpdate(ZonedDateTime time) {
            this.lastUpdate = time;
            return this;
        }

        public ScheduledJobBuilder lastUpdate(JobExecutionResponse executionResponse) {
            this.executionResponse = executionResponse;
            return this;
        }

        public ScheduledJob build() {
            return new ScheduledJob(job, scheduledId, retries, status, getLastUpdate(), executionResponse);
        }

        private ZonedDateTime getLastUpdate() {
            return Optional.ofNullable(lastUpdate).orElseGet(() -> DateUtil.now());
        }
    }
}
