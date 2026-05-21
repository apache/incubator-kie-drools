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
package org.kie.kogito.jobs.service.messaging.v2;

import java.util.Objects;

import org.kie.kogito.jobs.service.adapter.JobDetailsAdapter;
import org.kie.kogito.jobs.service.api.event.CreateJobEvent;
import org.kie.kogito.jobs.service.api.event.DeleteJobEvent;
import org.kie.kogito.jobs.service.api.event.serialization.JobCloudEventDeserializer;
import org.kie.kogito.jobs.service.messaging.ReactiveMessagingEventConsumer;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.repository.ReactiveJobRepository;
import org.kie.kogito.jobs.service.scheduler.impl.TimerDelegateJobScheduler;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEvent;

public class MessagingConsumer extends ReactiveMessagingEventConsumer {

    protected JobCloudEventDeserializer deserializer;

    public MessagingConsumer() {
    }

    public MessagingConsumer(TimerDelegateJobScheduler scheduler, ReactiveJobRepository jobRepository, ObjectMapper objectMapper) {
        super(scheduler, jobRepository, CreateJobEvent.TYPE, DeleteJobEvent.TYPE);
        this.deserializer = new JobCloudEventDeserializer(objectMapper);
    }

    @Override
    public JobDetails getJobDetails(CloudEvent createEvent) {
        if (!Objects.equals(getCreateJobEventType(), createEvent.getType())) {
            throw new IllegalArgumentException("Only " + getCreateJobEventType() + "is supported to get JobDetails " + createEvent);
        }
        final CreateJobEvent jobCloudEvent = (CreateJobEvent) deserializer.deserialize(createEvent);
        return JobDetailsAdapter.from(jobCloudEvent.getData());
    }

    @Override
    public String getJobId(CloudEvent createEvent) {
        if (!Objects.equals(getCancelJobEventType(), createEvent.getType())) {
            throw new IllegalArgumentException("Only " + getCreateJobEventType() + "is supported to get Job Id " + createEvent);
        }
        final DeleteJobEvent jobCloudEvent = (DeleteJobEvent) deserializer.deserialize(createEvent);
        return jobCloudEvent.getData().getId();
    }
}
