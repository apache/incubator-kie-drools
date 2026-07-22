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
package org.kie.kogito.jobs.service.api.utils;

import org.junit.jupiter.api.Test;
import org.kie.kogito.jobs.service.api.Job;
import org.kie.kogito.jobs.service.api.JobLookupId;
import org.kie.kogito.jobs.service.api.Recipient;
import org.kie.kogito.jobs.service.api.Schedule;
import org.kie.kogito.jobs.service.api.event.CreateJobEvent;
import org.kie.kogito.jobs.service.api.event.DeleteJobEvent;
import org.kie.kogito.jobs.service.api.event.JobCloudEvent;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipient;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipientBinaryPayloadData;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipientJsonPayloadData;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipientStringPayloadData;
import org.kie.kogito.jobs.service.api.recipient.sink.SinkRecipient;
import org.kie.kogito.jobs.service.api.recipient.sink.SinkRecipientBinaryPayloadData;
import org.kie.kogito.jobs.service.api.recipient.sink.SinkRecipientJsonPayloadData;
import org.kie.kogito.jobs.service.api.recipient.sink.serialization.ContentModeDeserializer;
import org.kie.kogito.jobs.service.api.recipient.sink.serialization.ContentModeSerializer;
import org.kie.kogito.jobs.service.api.schedule.cron.CronSchedule;
import org.kie.kogito.jobs.service.api.schedule.timer.TimerSchedule;
import org.kie.kogito.jobs.service.api.serialization.SpecVersionDeserializer;
import org.kie.kogito.jobs.service.api.serialization.SpecVersionSerializer;

import static org.assertj.core.api.Assertions.assertThat;

class ReflectionUtilsTest {

    @Test
    void apiReflectiveClasses() {
        assertThat(ReflectionUtils.apiReflectiveClasses())
                .hasSize(20)
                .containsExactlyInAnyOrder(SpecVersionSerializer.class,
                        SpecVersionDeserializer.class,
                        Job.class,
                        JobLookupId.class,
                        Recipient.class,
                        HttpRecipient.class,
                        HttpRecipientStringPayloadData.class,
                        HttpRecipientBinaryPayloadData.class,
                        HttpRecipientJsonPayloadData.class,
                        ContentModeSerializer.class,
                        ContentModeDeserializer.class,
                        SinkRecipient.class,
                        SinkRecipientBinaryPayloadData.class,
                        SinkRecipientJsonPayloadData.class,
                        Schedule.class,
                        TimerSchedule.class,
                        CronSchedule.class,
                        JobCloudEvent.class,
                        CreateJobEvent.class,
                        DeleteJobEvent.class);
    }
}
