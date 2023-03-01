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
package org.kie.kogito.jobs.service.adapter;

import java.util.Objects;

import org.apache.commons.lang3.NotImplementedException;
import org.kie.kogito.jobs.service.api.Job;
import org.kie.kogito.jobs.service.api.Recipient;
import org.kie.kogito.jobs.service.api.Retry;
import org.kie.kogito.jobs.service.api.Schedule;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipient;
import org.kie.kogito.jobs.service.api.recipient.sink.SinkRecipient;
import org.kie.kogito.jobs.service.api.schedule.timer.TimerSchedule;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.kie.kogito.jobs.service.model.RecipientInstance;
import org.kie.kogito.jobs.service.utils.DateUtil;
import org.kie.kogito.timer.Trigger;
import org.kie.kogito.timer.impl.IntervalTrigger;
import org.kie.kogito.timer.impl.PointInTimeTrigger;

public class JobDetailsAdapter {

    private JobDetailsAdapter() {
    }

    public static class StatusAdapter {

        private StatusAdapter() {
        }

        public static Job.State toState(JobStatus status) {
            if (Objects.isNull(status)) {
                return Job.State.SCHEDULED;
            }
            switch (status) {
                case ERROR:
                    return Job.State.ERROR;
                case EXECUTED:
                    return Job.State.EXECUTED;
                case SCHEDULED:
                    return Job.State.SCHEDULED;
                case RETRY:
                    return Job.State.RETRY;
                case CANCELED:
                    return Job.State.CANCELED;
                default:
                    throw new IllegalArgumentException("JobStatus: " + status + " can not be converted to a Job.State");
            }
        }

        public static JobStatus from(Job.State state) {
            if (Objects.isNull(state)) {
                return JobStatus.SCHEDULED;
            }
            switch (state) {
                case ERROR:
                    return JobStatus.ERROR;
                case EXECUTED:
                    return JobStatus.EXECUTED;
                case SCHEDULED:
                    return JobStatus.SCHEDULED;
                case RETRY:
                    return JobStatus.RETRY;
                case CANCELED:
                    return JobStatus.CANCELED;
                default:
                    throw new IllegalArgumentException("Job.State: " + state + " can not be converted to a JobStatus");
            }
        }
    }

    public static class ScheduleAdapter {

        private ScheduleAdapter() {
        }

        public static Schedule toSchedule(Trigger trigger) {

            if (trigger instanceof IntervalTrigger) {
                IntervalTrigger intervalTrigger = (IntervalTrigger) trigger;
                return TimerSchedule.builder()
                        .startTime(DateUtil.dateToOffsetDateTime(intervalTrigger.hasNextFireTime()))
                        .repeatCount(intervalTrigger.getRepeatLimit())
                        .delay(intervalTrigger.getPeriod())
                        .build();
            }
            if (trigger instanceof PointInTimeTrigger) {
                return TimerSchedule.builder()
                        .startTime(DateUtil.dateToOffsetDateTime(trigger.hasNextFireTime()))
                        .build();
            }

            throw new NotImplementedException("Only IntervalTrigger and PointInTimeTrigger are supported");
        }

        public static Trigger from(Schedule schedule) {
            if (schedule instanceof TimerSchedule) {
                TimerSchedule timerSchedule = (TimerSchedule) schedule;
                if (timerSchedule.getRepeatCount() != null && timerSchedule.getRepeatCount() > 0) {
                    return new IntervalTrigger(0, DateUtil.toDate(timerSchedule.getStartTime()), null, timerSchedule.getRepeatCount(), 0, timerSchedule.getDelay(), null, null);
                }
                return new PointInTimeTrigger(timerSchedule.getStartTime().toInstant().toEpochMilli(), null, null);
            }
            throw new NotImplementedException("Only TimeSchedule is supported");
        }
    }

    public static class RecipientAdapter {

        private RecipientAdapter() {
        }

        public static Recipient<?> toRecipient(JobDetails jobDetails) {
            checkIsSupported(jobDetails.getRecipient().getRecipient());
            return jobDetails.getRecipient().getRecipient();
        }

        public static <T> T payload(Recipient<?> recipient) {
            return (T) recipient.getPayload();
        }

        public static org.kie.kogito.jobs.service.model.Recipient from(Recipient<?> recipient) {
            checkIsSupported(recipient);
            return new RecipientInstance(recipient);
        }

        static void checkIsSupported(Recipient<?> recipient) {
            if (!(recipient instanceof HttpRecipient) && !(recipient instanceof SinkRecipient)) {
                throw new NotImplementedException("Only HttpRecipient and SinkRecipient are supported");
            }
        }
    }

    public static class RetryAdapter {

        private RetryAdapter() {
        }

        public static Retry toRetry(JobDetails jobDetails) {
            return null;
        }
    }

    public static JobDetails from(Job job) {
        return JobDetails.builder()
                .id(job.getId())
                .correlationId(job.getCorrelationId())
                .status(StatusAdapter.from(job.getState()))
                .trigger(ScheduleAdapter.from(job.getSchedule()))
                .recipient(RecipientAdapter.from(job.getRecipient()))
                .build();
    }

    public static Job toJob(JobDetails jobDetails) {
        return Job.builder()
                .id(jobDetails.getId())
                .correlationId(jobDetails.getCorrelationId())
                .state(StatusAdapter.toState(jobDetails.getStatus()))
                .schedule(ScheduleAdapter.toSchedule(jobDetails.getTrigger()))
                .recipient(RecipientAdapter.toRecipient(jobDetails))
                .retry(RetryAdapter.toRetry(jobDetails))
                .build();
    }
}
