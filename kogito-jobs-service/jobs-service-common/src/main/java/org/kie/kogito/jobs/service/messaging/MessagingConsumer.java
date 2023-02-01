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
package org.kie.kogito.jobs.service.messaging;

import java.util.Objects;

import org.kie.kogito.jobs.api.event.CancelJobRequestEvent;
import org.kie.kogito.jobs.api.event.CreateProcessInstanceJobRequestEvent;
import org.kie.kogito.jobs.api.event.serialization.JobCloudEventDeserializer;
import org.kie.kogito.jobs.service.adapter.ScheduledJobAdapter;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.ScheduledJob;
import org.kie.kogito.jobs.service.repository.ReactiveJobRepository;
import org.kie.kogito.jobs.service.scheduler.impl.TimerDelegateJobScheduler;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEvent;

public class MessagingConsumer extends ReactiveMessagingEventConsumer {

    protected JobCloudEventDeserializer deserializer;

    public MessagingConsumer() {
    }

    public MessagingConsumer(TimerDelegateJobScheduler scheduler, ReactiveJobRepository jobRepository, ObjectMapper objectMapper) {
        super(scheduler, jobRepository, CreateProcessInstanceJobRequestEvent.CREATE_PROCESS_INSTANCE_JOB_REQUEST, CancelJobRequestEvent.CANCEL_JOB_REQUEST);
        this.deserializer = new JobCloudEventDeserializer(objectMapper);
    }

    @Override
    public JobDetails getJobDetails(CloudEvent createEvent) {
        if (!Objects.equals(getCreateJobEventType(), createEvent.getType())) {
            throw new IllegalArgumentException("Only " + getCreateJobEventType() + "is supported to get JobDetails " + createEvent);
        }
        final CreateProcessInstanceJobRequestEvent jobCloudEvent = (CreateProcessInstanceJobRequestEvent) deserializer.deserialize(createEvent);
        return ScheduledJobAdapter.to(ScheduledJob.builder().job(jobCloudEvent.getData()).build());
    }

    @Override
    public String getJobId(CloudEvent createEvent) {
        if (!Objects.equals(getCancelJobEventType(), createEvent.getType())) {
            throw new IllegalArgumentException("Only " + getCreateJobEventType() + "is supported to get Job Id " + createEvent);
        }
        final CancelJobRequestEvent jobCloudEvent = (CancelJobRequestEvent) deserializer.deserialize(createEvent);
        return jobCloudEvent.getData().getId();
    }
}
