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
package org.kie.kogito.app.jobs.springboot;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.kie.kogito.app.jobs.impl.InVMRecipient;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.JobExecutionExceptionDetails;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.kie.kogito.jobs.service.model.RecipientInstance;
import org.kie.kogito.timer.impl.SimpleTimerTrigger;

/**
 * Factory class for creating test JobDetails instances.
 */
public class TestJobDetailsFactory {

    private TestJobDetailsFactory() {
        // Utility class
    }

    public static JobDetails createJobDetailsWithException(String jobId) {
        JobExecutionExceptionDetails exceptionDetails = new JobExecutionExceptionDetails(
                "java.lang.RuntimeException",
                "Test exception details");

        return JobDetails.builder()
                .id(jobId)
                .status(JobStatus.RETRY)
                .retries(1)
                .lastUpdate(OffsetDateTime.now().toZonedDateTime())
                .recipient(new RecipientInstance(new InVMRecipient()))
                .trigger(new SimpleTimerTrigger(new Date(), 0L, ChronoUnit.MILLIS, 0, "UTC"))
                .exceptionDetails(exceptionDetails)
                .build();
    }

    public static JobDetails createJobDetailsWithoutException(String jobId) {
        return JobDetails.builder()
                .id(jobId)
                .status(JobStatus.SCHEDULED)
                .retries(0)
                .lastUpdate(OffsetDateTime.now().toZonedDateTime())
                .recipient(new RecipientInstance(new InVMRecipient()))
                .trigger(new SimpleTimerTrigger(new Date(), 0L, ChronoUnit.MILLIS, 0, "UTC"))
                .exceptionDetails(null)
                .build();
    }
}
