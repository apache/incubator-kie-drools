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
package org.kie.kogito.jobs.service.validation;

import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.kie.kogito.jobs.service.exception.JobValidationException;
import org.kie.kogito.jobs.service.model.JobDetails;

@ApplicationScoped
public class JobDetailsValidator {

    private final RecipientInstanceValidator recipientValidator;

    @Inject
    public JobDetailsValidator(RecipientInstanceValidator recipientValidator) {
        this.recipientValidator = recipientValidator;
    }

    public JobDetails validateToCreate(JobDetails job) {
        if (StringUtils.isEmpty(job.getId())) {
            throw new JobValidationException("A non empty id must be provided to create a Job.");
        }
        if (StringUtils.isEmpty(job.getCorrelationId())) {
            throw new JobValidationException("A non empty correlationId id must be provided to create a Job.");
        }
        if (job.getTrigger() == null) {
            throw new JobValidationException("A non null trigger must be provided to create a Job, please verify that" +
                    " the Schedule configuration was provided.");
        }
        if (job.getRecipient() == null || job.getRecipient().getRecipient() == null) {
            throw new JobValidationException("A non null Recipient configuration must be provided to create a Job.");
        }
        recipientValidator.validate(job.getRecipient());
        return job;
    }

    public JobDetails validateToMerge(JobDetails job) {
        if (StringUtils.isNotEmpty(job.getId())
                || StringUtils.isNotEmpty(job.getScheduledId())
                || StringUtils.isNotEmpty(job.getCorrelationId())
                || (Objects.nonNull(job.getExecutionCounter()) && job.getExecutionCounter() > 0)
                || Objects.nonNull(job.getPriority())
                || (Objects.nonNull(job.getRetries()) && job.getRetries() > 0)
                || Objects.nonNull(job.getRecipient())
                || Objects.nonNull(job.getStatus())) {
            throw new JobValidationException("Merge can only be applied to the Job scheduling trigger attributes for Job. " + job);
        }
        return job;
    }
}
