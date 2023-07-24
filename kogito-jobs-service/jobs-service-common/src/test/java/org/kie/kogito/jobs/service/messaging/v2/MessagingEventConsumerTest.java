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
package org.kie.kogito.jobs.service.messaging.v2;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.jobs.service.api.Job;
import org.kie.kogito.jobs.service.api.JobLookupId;
import org.kie.kogito.jobs.service.api.event.CreateJobEvent;
import org.kie.kogito.jobs.service.api.event.DeleteJobEvent;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipient;
import org.kie.kogito.jobs.service.api.schedule.timer.TimerSchedule;
import org.kie.kogito.jobs.service.messaging.ReactiveMessagingEventConsumer;
import org.kie.kogito.jobs.service.messaging.ReactiveMessagingEventConsumerTest;
import org.mockito.junit.jupiter.MockitoExtension;

import io.cloudevents.CloudEvent;

@ExtendWith(MockitoExtension.class)
public abstract class MessagingEventConsumerTest<T extends ReactiveMessagingEventConsumer> extends ReactiveMessagingEventConsumerTest<T> {

    @Override
    public CloudEvent newCreateProcessInstanceJobRequestCloudEvent() {
        CreateJobEvent createJobEvent = CreateJobEvent.builder()
                .id(JOB_ID)
                .source(EVENT_SOURCE)
                .job(Job.builder()
                        .id(JOB_ID)
                        .correlationId(JOB_ID)
                        .schedule(TimerSchedule.builder().startTime(OffsetDateTime.now()).build())
                        .recipient(HttpRecipient.builder().forStringPayload().url("url").build())
                        .build())
                .build();
        return objectMapper.convertValue(createJobEvent, CloudEvent.class);
    }

    @Override
    public CloudEvent newCancelJobRequestCloudEvent() {
        DeleteJobEvent deleteJobEvent = DeleteJobEvent.builder()
                .id(JOB_ID)
                .lookupId(JobLookupId.fromId(JOB_ID))
                .source(EVENT_SOURCE)
                .build();
        return objectMapper.convertValue(deleteJobEvent, CloudEvent.class);
    }
}
