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

package org.kie.kogito.app.jobs.impl;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

import org.kie.kogito.jobs.JobDescription;
import org.kie.kogito.jobs.descriptors.ProcessInstanceJobDescription;
import org.kie.kogito.jobs.descriptors.ProcessJobDescription;
import org.kie.kogito.jobs.descriptors.UserTaskInstanceJobDescription;
import org.kie.kogito.jobs.service.api.Recipient;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.kie.kogito.jobs.service.model.RecipientInstance;
import org.kie.kogito.jobs.service.utils.DateUtil;
import org.kie.kogito.timer.impl.SimpleTimerTrigger;

public final class JobDetailsHelper {

    private JobDetailsHelper() {

    }

    public static JobDescription extractJobDescription(JobDetails jobDetails) {
        Recipient<InVMPayloadData> recipient = jobDetails.getRecipient().getRecipient();
        JobDescription jobDescription = recipient.getPayload().getJobDescription();
        return jobDescription;
    }

    public static JobDetails newScheduledJobDetails(JobDescription jobDescription) {

        Long delay = Optional.ofNullable(jobDescription.expirationTime().repeatInterval()).orElse(0L);
        Integer repeat = Optional.ofNullable(jobDescription.expirationTime().repeatLimit()).orElse(0);
        repeat = repeat >= 1 ? repeat - 1 : repeat;
        ZonedDateTime time = jobDescription.expirationTime().get();

        Date date = DateUtil.toDate(time.toOffsetDateTime().truncatedTo(ChronoUnit.MILLIS));

        SimpleTimerTrigger trigger = new SimpleTimerTrigger(date, delay, ChronoUnit.MILLIS, repeat, time.getOffset().getId());

        String correlationId = null;
        if (jobDescription instanceof ProcessJobDescription processJobDescription) {
            correlationId = processJobDescription.processId();
        } else if (jobDescription instanceof ProcessInstanceJobDescription processInstanceJobDescription) {
            correlationId = processInstanceJobDescription.processInstanceId();
        } else if (jobDescription instanceof UserTaskInstanceJobDescription userTaskInstanceJobDescription) {
            correlationId = userTaskInstanceJobDescription.id();
        }

        return JobDetails.builder().id(jobDescription.id())
                .correlationId(correlationId)
                .status(JobStatus.SCHEDULED).trigger(trigger)
                .recipient(new RecipientInstance(new InVMRecipient(new InVMPayloadData(jobDescription))))
                .executionTimeout(trigger.hasNextFireTime().getTime())
                .executionTimeoutUnit(ChronoUnit.MILLIS)
                .retries(0)
                .executionCounter(0)
                .build();

    }
}
