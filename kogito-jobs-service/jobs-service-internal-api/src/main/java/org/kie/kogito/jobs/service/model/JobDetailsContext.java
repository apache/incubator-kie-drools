/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.jobs.service.model;

import org.kie.kogito.timer.JobContext;
import org.kie.kogito.timer.JobHandle;

public class JobDetailsContext implements JobContext {

    private JobDetails jobDetails;
    private JobHandle jobHandle;

    public JobDetailsContext(JobDetails jobDetails) {
        this.jobDetails = jobDetails;
        if (jobHandle == null) {
            jobHandle = new ManageableJobHandle(jobDetails.getScheduledId());
        }
    }

    @Override
    public void setJobHandle(JobHandle jobHandle) {
        this.jobHandle = jobHandle;
    }

    @Override
    public JobHandle getJobHandle() {
        return jobHandle;
    }

    public JobDetails getJobDetails() {
        return jobDetails;
    }
}
