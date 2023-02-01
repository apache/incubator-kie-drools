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

import java.time.ZonedDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.kie.kogito.jobs.api.JobBuilder;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipient;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.JobDetailsBuilder;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.kie.kogito.jobs.service.model.RecipientInstance;
import org.kie.kogito.jobs.service.model.ScheduledJob;
import org.kie.kogito.jobs.service.utils.DateUtil;
import org.kie.kogito.timer.impl.IntervalTrigger;
import org.kie.kogito.timer.impl.PointInTimeTrigger;

import static org.assertj.core.api.Assertions.assertThat;

class ScheduledJobAdapterTest {

    public static final String ENDPOINT = "url";
    public static final String ID = UUID.randomUUID().toString();
    public static final String SCHEDULED_ID = "scheduledId";
    public static final int RETRIES = 10;
    public static final int COUNTER = 5;
    public static final ZonedDateTime LAST_UPDATE = DateUtil.now().minusMinutes(1);
    public static final int PRIORITY = 2;
    public static final JobStatus STATUS = JobStatus.SCHEDULED;
    public static final ZonedDateTime TIME = DateUtil.now().plusMinutes(5);
    public static final int REPEAT_LIMIT = 50;
    public static final long INTERVAL = 500;
    public static final String ROOT_PROCESS_INSTANCE_ID = "ID";
    public static final String ROOT_PROCESS_ID = "";
    public static final String PROCESS_ID = "ID";
    public static final String PROCESS_INSTANCE_ID = "ID";
    private static final String NODE_INSTANCE_ID = "nodeId";

    @Test
    void testOfJobDetailsPointInTime() {
        JobDetails jobDetails = getJobDetailsCommonBuilder()
                .trigger(new PointInTimeTrigger(TIME.toInstant().toEpochMilli(), null, null))
                .build();

        ScheduledJob scheduledJob = ScheduledJobAdapter.of(jobDetails);
        assertScheduledJob(scheduledJob);
        assertThat(scheduledJob.getRepeatLimit()).isNull();
        assertThat(scheduledJob.getRepeatInterval()).isNull();
    }

    @Test
    void testOfJobDetailsInterval() {
        JobDetails jobDetails = getJobDetailsCommonBuilder()
                .trigger(new IntervalTrigger(0, DateUtil.toDate(TIME.toOffsetDateTime()), null, REPEAT_LIMIT, 0, INTERVAL, null, null))
                .build();

        ScheduledJob scheduledJob = ScheduledJobAdapter.of(jobDetails);
        assertScheduledJob(scheduledJob);
        assertThat(scheduledJob.getRepeatLimit()).isEqualTo(REPEAT_LIMIT + 1);
        assertThat(scheduledJob.getRepeatInterval()).isEqualTo(INTERVAL);
    }

    @Test
    void testToJobDetailsInterval() {
        JobBuilder jobBuilder = JobBuilder.builder().repeatLimit(REPEAT_LIMIT).repeatInterval(INTERVAL);
        ScheduledJob scheduledJob = getScheduledJobCommonBuilder(jobBuilder).build();

        JobDetails jobDetails = ScheduledJobAdapter.to(scheduledJob);
        assertJobDetails(jobDetails);
        assertThat(jobDetails.getTrigger()).isInstanceOf(IntervalTrigger.class);
        IntervalTrigger intervalTrigger = (IntervalTrigger) jobDetails.getTrigger();
        assertThat(intervalTrigger.getNextFireTime()).isEqualTo(DateUtil.toDate(TIME.toOffsetDateTime()));
        assertThat(intervalTrigger.getRepeatLimit()).isEqualTo(REPEAT_LIMIT);
        assertThat(intervalTrigger.getPeriod()).isEqualTo(INTERVAL);
    }

    @Test
    void testToJobDetailsPointInTIme() {
        ScheduledJob scheduledJob = getScheduledJobCommonBuilder(JobBuilder.builder()).build();

        JobDetails jobDetails = ScheduledJobAdapter.to(scheduledJob);
        assertJobDetails(jobDetails);
        assertThat(jobDetails.getTrigger()).isInstanceOf(PointInTimeTrigger.class);
        PointInTimeTrigger trigger = (PointInTimeTrigger) jobDetails.getTrigger();
        assertThat(trigger.nextFireTime()).isEqualTo(DateUtil.toDate(TIME.toOffsetDateTime()));
    }

    @Test
    void testToJobDetailsWithEmptyPayload() {
        ScheduledJob scheduledJob = ScheduledJob.builder()
                .job(JobBuilder.builder().id(UUID.randomUUID().toString()).build())
                .build();
        JobDetails jobDetails = ScheduledJobAdapter.to(scheduledJob);
        assertThat(jobDetails.getRecipient()).isNull();
    }

    @Test
    void testToScheduledJobWithEmptyPayload() {
        JobDetails jobDetails = JobDetails.builder().id(UUID.randomUUID().toString())
                .recipient(new RecipientInstance(HttpRecipient.builder().forStringPayload().payload(null).build()))
                .build();
        ScheduledJob scheduledJob = ScheduledJobAdapter.of(jobDetails);
        assertThat(scheduledJob.getProcessId()).isNull();
        assertThat(scheduledJob.getProcessInstanceId()).isNull();
        assertThat(scheduledJob.getRootProcessId()).isNull();
        assertThat(scheduledJob.getRootProcessInstanceId()).isNull();
    }

    private ScheduledJob.ScheduledJobBuilder getScheduledJobCommonBuilder(JobBuilder jobBuilder) {
        return ScheduledJob.builder()
                .job(jobBuilder
                        .id(ID)
                        .priority(PRIORITY)
                        .expirationTime(TIME)
                        .callbackEndpoint(ENDPOINT)
                        .rootProcessId(ROOT_PROCESS_ID)
                        .rootProcessInstanceId(ROOT_PROCESS_INSTANCE_ID)
                        .processId(PROCESS_ID)
                        .processInstanceId(PROCESS_INSTANCE_ID)
                        .nodeInstanceId(NODE_INSTANCE_ID)
                        .build())
                .executionCounter(COUNTER)
                .retries(RETRIES)
                .scheduledId(SCHEDULED_ID)
                .status(STATUS)
                .lastUpdate(LAST_UPDATE);
    }

    private JobDetailsBuilder getJobDetailsCommonBuilder() {
        return JobDetails.builder()
                .id(ID)
                .priority(PRIORITY)
                .recipient(new RecipientInstance(HttpRecipient.builder().forStringPayload().url(ENDPOINT)
                        .header("processId", PROCESS_ID)
                        .header("processInstanceId", PROCESS_INSTANCE_ID)
                        .header("rootProcessInstanceId", ROOT_PROCESS_INSTANCE_ID)
                        .header("rootProcessId", ROOT_PROCESS_ID)
                        .header("nodeInstanceId", NODE_INSTANCE_ID)
                        .build()))
                .scheduledId(SCHEDULED_ID)
                .status(STATUS)
                .correlationId(ID)
                .lastUpdate(LAST_UPDATE)
                .executionCounter(COUNTER)
                .priority(PRIORITY)
                .retries(RETRIES);
    }

    private void assertScheduledJob(ScheduledJob scheduledJob) {
        assertThat(scheduledJob.getId()).isEqualTo(ID);
        assertThat(scheduledJob.getScheduledId()).isEqualTo(SCHEDULED_ID);
        assertThat(scheduledJob.getRetries()).isEqualTo(RETRIES);
        assertThat(scheduledJob.getExecutionCounter()).isEqualTo(COUNTER);
        assertThat(scheduledJob.getLastUpdate()).isEqualTo(LAST_UPDATE);
        assertThat(scheduledJob.getPriority()).isEqualTo(PRIORITY);
        assertThat(scheduledJob.getStatus()).isEqualTo(STATUS);
        assertThat(scheduledJob.getExpirationTime()).isEqualTo(TIME);
        assertThat(scheduledJob.getRootProcessInstanceId()).isEqualTo(ROOT_PROCESS_INSTANCE_ID);
        assertThat(scheduledJob.getRootProcessId()).isEqualTo(ROOT_PROCESS_ID);
        assertThat(scheduledJob.getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(scheduledJob.getProcessInstanceId()).isEqualTo(PROCESS_INSTANCE_ID);
        assertThat(scheduledJob.getNodeInstanceId()).isEqualTo(NODE_INSTANCE_ID);
        assertThat(scheduledJob.getCallbackEndpoint()).isEqualTo(ENDPOINT);
    }

    private void assertJobDetails(JobDetails jobDetails) {
        assertThat(jobDetails.getId()).isEqualTo(ID);
        assertThat(jobDetails.getScheduledId()).isEqualTo(SCHEDULED_ID);
        assertThat(jobDetails.getExecutionCounter()).isEqualTo(COUNTER);
        assertThat(jobDetails.getRetries()).isEqualTo(RETRIES);
        assertThat(jobDetails.getCorrelationId()).isEqualTo(ID);
        assertThat(jobDetails.getLastUpdate()).isEqualTo(LAST_UPDATE);
        assertThat(jobDetails.getPriority()).isEqualTo(PRIORITY);
        assertThat(jobDetails.getStatus()).isEqualTo(STATUS);
        assertThat(jobDetails.getRecipient()).isInstanceOf(RecipientInstance.class);
        HttpRecipient recipient = (HttpRecipient) jobDetails.getRecipient().getRecipient();
        assertThat(recipient.getUrl()).isEqualTo(ENDPOINT);
        assertThat(recipient.getHeader("processId")).isEqualTo(PROCESS_ID);
        assertThat(recipient.getHeader("processInstanceId")).isEqualTo(PROCESS_INSTANCE_ID);
        assertThat(recipient.getHeader("rootProcessId")).isEqualTo(ROOT_PROCESS_ID);
        assertThat(recipient.getHeader("rootProcessInstanceId")).isEqualTo(ROOT_PROCESS_INSTANCE_ID);
        assertThat(recipient.getHeader("nodeInstanceId")).isEqualTo(NODE_INSTANCE_ID);
    }
}
