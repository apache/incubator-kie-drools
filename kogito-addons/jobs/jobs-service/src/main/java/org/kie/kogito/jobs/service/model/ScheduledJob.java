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

import java.util.Optional;
import java.util.StringJoiner;

import org.kie.kogito.jobs.api.Job;

public class ScheduledJob {

    private Job job;
    private String scheduledId;
    private Integer retries;

    public ScheduledJob(Job job, String scheduledId) {
        this.job = job;
        this.scheduledId = scheduledId;
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

    @Override
    public String toString() {
        return new StringJoiner(", ", ScheduledJob.class.getSimpleName() + "[", "]")
                .add("job=" + Optional.ofNullable(job).map(Job::getId).orElse(null))
                .add("scheduledId='" + scheduledId + "'")
                .add("retries=" + retries)
                .toString();
    }
}
