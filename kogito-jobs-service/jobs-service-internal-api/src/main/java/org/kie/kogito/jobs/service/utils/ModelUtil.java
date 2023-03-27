/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.jobs.service.utils;

import java.time.temporal.ChronoUnit;
import java.util.Objects;

import org.kie.kogito.jobs.service.adapter.JobDetailsAdapter;
import org.kie.kogito.jobs.service.api.Job;

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
}
