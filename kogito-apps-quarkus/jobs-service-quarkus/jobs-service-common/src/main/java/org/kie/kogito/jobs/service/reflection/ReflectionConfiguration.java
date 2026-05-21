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
package org.kie.kogito.jobs.service.reflection;

import org.kie.kogito.event.AbstractDataEvent;
import org.kie.kogito.event.cloudevents.SpecVersionDeserializer;
import org.kie.kogito.event.cloudevents.SpecVersionSerializer;
import org.kie.kogito.jobs.api.event.CancelJobRequestEvent;
import org.kie.kogito.jobs.service.adapter.ScheduledJobAdapter;
import org.kie.kogito.jobs.service.api.Job;
import org.kie.kogito.jobs.service.api.JobLookupId;
import org.kie.kogito.jobs.service.api.Recipient;
import org.kie.kogito.jobs.service.api.Schedule;
import org.kie.kogito.jobs.service.api.event.CreateJobEvent;
import org.kie.kogito.jobs.service.api.event.DeleteJobEvent;
import org.kie.kogito.jobs.service.api.event.JobCloudEvent;
import org.kie.kogito.jobs.service.api.event.serialization.JobCloudEventDeserializer;
import org.kie.kogito.jobs.service.api.event.serialization.JobCloudEventSerializer;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipient;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipientBinaryPayloadData;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipientJsonPayloadData;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipientStringPayloadData;
import org.kie.kogito.jobs.service.api.recipient.sink.SinkRecipient;
import org.kie.kogito.jobs.service.api.recipient.sink.SinkRecipientBinaryPayloadData;
import org.kie.kogito.jobs.service.api.recipient.sink.SinkRecipientJsonPayloadData;
import org.kie.kogito.jobs.service.api.schedule.cron.CronSchedule;
import org.kie.kogito.jobs.service.api.schedule.timer.TimerSchedule;
import org.kie.kogito.jobs.service.events.JobDataEvent;
import org.kie.kogito.jobs.service.repository.marshaller.TriggerMarshaller;
import org.kie.kogito.jobs.service.resource.error.ErrorResponse;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Placeholder for registering classes for reflection instead of using reflection-config.json approach or tagging
 * them individually.
 */
@RegisterForReflection(
        targets = {
                SpecVersionSerializer.class,
                SpecVersionDeserializer.class,
                AbstractDataEvent.class,
                JobDataEvent.class,
                ScheduledJobAdapter.ProcessPayload.class,
                TriggerMarshaller.PointInTimeTriggerAccessor.class,
                TriggerMarshaller.IntervalTriggerAccessor.class,
                TriggerMarshaller.SimpleTimerTriggerAccessor.class,
                CancelJobRequestEvent.JobId.class,
                org.kie.kogito.jobs.service.api.serialization.SpecVersionSerializer.class,
                org.kie.kogito.jobs.service.api.serialization.SpecVersionDeserializer.class,
                Job.class,
                JobLookupId.class,
                Recipient.class,
                HttpRecipient.class,
                HttpRecipientStringPayloadData.class,
                HttpRecipientBinaryPayloadData.class,
                HttpRecipientJsonPayloadData.class,
                SinkRecipient.class,
                SinkRecipientBinaryPayloadData.class,
                SinkRecipientJsonPayloadData.class,
                Schedule.class,
                TimerSchedule.class,
                CronSchedule.class,
                JobCloudEvent.class,
                CreateJobEvent.class,
                DeleteJobEvent.class,
                JobCloudEventSerializer.class,
                JobCloudEventDeserializer.class,
                ErrorResponse.class
        })
public class ReflectionConfiguration {
}
