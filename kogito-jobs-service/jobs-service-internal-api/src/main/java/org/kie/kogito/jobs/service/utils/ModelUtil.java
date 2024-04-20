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
package org.kie.kogito.jobs.service.utils;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import org.kie.kogito.jobs.service.adapter.JobDetailsAdapter;
import org.kie.kogito.jobs.service.api.Job;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.kie.kogito.jobs.service.model.ManageableJobHandle;

public class ModelUtil {

    private ModelUtil() {
    }

    public static Long getExecutionTimeoutInMillis(Job job) {
        Objects.requireNonNull(job, "A Job is required to calculate the execution timeout in milliseconds.");
        if (job.getExecutionTimeout() == null) {
            return null;
        }
        ChronoUnit chronoUnit = job.getExecutionTimeoutUnit() != null ? JobDetailsAdapter.TemporalUnitAdapter.toChronoUnit(job.getExecutionTimeoutUnit()) : ChronoUnit.MILLIS;
        return chronoUnit.getDuration().multipliedBy(job.getExecutionTimeout()).toMillis();
    }

    public static JobDetails jobWithStatus(JobDetails job, JobStatus status) {
        return JobDetails.builder().of(job).status(status).build();
    }

    public static JobDetails jobWithStatusAndHandle(JobDetails job, JobStatus status, ManageableJobHandle handle) {
        return JobDetails.builder().of(job).status(status).scheduledId(String.valueOf(handle.getId())).build();
    }

    public static JobDetails jobWithCreatedAndLastUpdate(boolean isNew, JobDetails job) {
        ZonedDateTime now = DateUtil.now();
        return isNew ? jobWithCreated(job, now, now) : jobWithLastUpdate(job, now);
    }

    public static JobDetails jobWithCreated(JobDetails job, ZonedDateTime created, ZonedDateTime lastUpdate) {
        return JobDetails.builder().of(job).created(created).lastUpdate(lastUpdate).build();
    }

    public static JobDetails jobWithLastUpdate(JobDetails job, ZonedDateTime lastUpdate) {
        return JobDetails.builder().of(job).lastUpdate(lastUpdate).build();
    }
}
