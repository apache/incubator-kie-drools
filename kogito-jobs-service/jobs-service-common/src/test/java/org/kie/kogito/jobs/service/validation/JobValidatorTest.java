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

package org.kie.kogito.jobs.service.validation;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.jobs.service.api.Job;
import org.kie.kogito.jobs.service.api.Recipient;
import org.kie.kogito.jobs.service.api.Schedule;
import org.kie.kogito.jobs.service.api.TemporalUnit;
import org.kie.kogito.jobs.service.api.schedule.timer.TimerSchedule;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;

class JobValidatorTest {

    private static final String JOB_ID = "JOB_ID";
    private static final String CORRELATION_ID = "CORRELATION_IDB_ID";
    private static final OffsetDateTime START_TIME = OffsetDateTime.parse("2023-03-23T15:20:25.001+01:00");
    private static final int REPEAT_COUNT = 2;
    private static final long DELAY = 4;
    private static final TemporalUnit DELAY_UNIT = TemporalUnit.MINUTES;
    private static final long EXECUTION_TIMEOUT = 5;
    private static final TemporalUnit EXECUTION_TIMEOUT_UNIT = TemporalUnit.SECONDS;
    private static final String RECIPIENT_VALIDATION_ERROR = "The recipient validation has failed!";

    private Job job;
    private Recipient recipient;
    private JobValidator validator;
    private RecipientValidatorProvider recipientValidatorProvider;

    @BeforeEach
    void setUp() {
        recipientValidatorProvider = mock(RecipientValidatorProvider.class);
        doReturn(Optional.empty()).when(recipientValidatorProvider).getValidator(any());
        validator = new JobValidator(recipientValidatorProvider);
        recipient = mock(Recipient.class);
        job = Job.builder()
                .id(JOB_ID)
                .correlationId(CORRELATION_ID)
                .schedule(TimerSchedule.builder()
                        .startTime(START_TIME)
                        .repeatCount(REPEAT_COUNT)
                        .delay(DELAY)
                        .delayUnit(DELAY_UNIT)
                        .build())
                .recipient(recipient)
                .executionTimeout(EXECUTION_TIMEOUT)
                .executionTimeoutUnit(EXECUTION_TIMEOUT_UNIT)
                .build();
    }

    @Test
    void validateToCreateSuccessful() {
        validator.validateToCreate(job);
    }

    @Test
    void validateToCreateWithNullId() {
        job.setId(null);
        validateToCreateWithError(job, "A non empty id");
    }

    @Test
    void validateToCreateWithNullCorrelationId() {
        job.setCorrelationId(null);
        validateToCreateWithError(job, "A non empty correlationId");
    }

    @Test
    void validateToCreateWithNullSchedule() {
        job.setSchedule(null);
        validateToCreateWithError(job, "A non null Schedule");
    }

    @Test
    void validateToCreateWithNoTimerSchedule() {
        job.setSchedule(mock(Schedule.class));
        validateToCreateWithError(job, "Only the TimerSchedule");
    }

    @Test
    void validateToCreateWithTimerScheduleStartTimeNull() {
        ((TimerSchedule) job.getSchedule()).setStartTime(null);
        validateToCreateWithError(job, "A non null startTime must");
    }

    @Test
    void validateToCreateWithTimerScheduleNegativeRepeatCount() {
        ((TimerSchedule) job.getSchedule()).setRepeatCount(-1);
        validateToCreateWithError(job, "A negative repeatCount");
    }

    @Test
    void validateToCreateWithTimerScheduleNegativeDelay() {
        ((TimerSchedule) job.getSchedule()).setDelay(-1L);
        validateToCreateWithError(job, "A negative delay");
    }

    @Test
    void validateToCreateWithNullRecipient() {
        job.setRecipient(null);
        validateToCreateWithError(job, "A non null Recipient");
    }

    @Test
    void validateToCreateWithRecipientError() {
        RecipientValidator recipientValidator = mock(RecipientValidator.class);
        doReturn(Optional.of(recipientValidator)).when(recipientValidatorProvider).getValidator(recipient);
        doThrow(new RuntimeException(RECIPIENT_VALIDATION_ERROR)).when(recipientValidator).validate(eq(recipient), any());
        validateToCreateWithError(job, RECIPIENT_VALIDATION_ERROR);
    }

    @Test
    void validateToCreateWithNegativeExecutionTimeout() {
        job.setExecutionTimeout(-1L);
        validateToCreateWithError(job, "Job executionTimeout can not be negative");
    }

    private void validateToCreateWithError(Job job, String expectedMessage) {
        assertThatThrownBy(() -> validator.validateToCreate(job)).hasMessageStartingWith(expectedMessage);
    }
}
