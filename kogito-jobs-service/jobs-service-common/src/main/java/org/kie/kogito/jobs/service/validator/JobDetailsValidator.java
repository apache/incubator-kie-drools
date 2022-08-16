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
package org.kie.kogito.jobs.service.validator;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.kie.kogito.jobs.service.model.job.JobDetails;
import org.kie.kogito.jobs.service.model.job.Recipient;

public class JobDetailsValidator {

    public static JobDetails validateToCreate(JobDetails job) {
        if (Objects.isNull(job.getPayload())
                || StringUtils.isEmpty(job.getId())
                || StringUtils.isEmpty(job.getCorrelationId())
                || Objects.isNull(job.getTrigger())
                || Objects.isNull(job.getRecipient())
                || (job.getRecipient() instanceof Recipient.HTTPRecipient && StringUtils.isBlank(((Recipient.HTTPRecipient) job.getRecipient()).getEndpoint()))) {
            throw new IllegalArgumentException("Invalid Job Attributes. " + job);
        }
        return job;
    }

    public static JobDetails validateToMerge(JobDetails job) {
        if (Objects.nonNull(job.getPayload())
                || StringUtils.isNotEmpty(job.getId())
                || StringUtils.isNotEmpty(job.getScheduledId())
                || StringUtils.isNotEmpty(job.getCorrelationId())
                || (Objects.nonNull(job.getExecutionCounter()) && job.getExecutionCounter() > 0)
                || Objects.nonNull(job.getPriority())
                || (Objects.nonNull(job.getRetries()) && job.getRetries() > 0)
                || Objects.nonNull(job.getRecipient())
                || Objects.nonNull(job.getStatus())) {
            throw new IllegalArgumentException("Merge can only be applied to the Job scheduling trigger attributes for Job. " + job);
        }
        return job;
    }
}