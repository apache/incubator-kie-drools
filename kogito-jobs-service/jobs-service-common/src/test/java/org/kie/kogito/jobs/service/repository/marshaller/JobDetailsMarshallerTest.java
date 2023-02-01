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
package org.kie.kogito.jobs.service.repository.marshaller;

import java.time.ZonedDateTime;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipient;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipientStringPayloadData;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.kie.kogito.jobs.service.model.Recipient;
import org.kie.kogito.jobs.service.model.RecipientInstance;
import org.kie.kogito.timer.Trigger;
import org.kie.kogito.timer.impl.PointInTimeTrigger;

import io.vertx.core.json.JsonObject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.kie.kogito.jobs.service.utils.DateUtil.DEFAULT_ZONE;

class JobDetailsMarshallerTest {

    JobDetailsMarshaller jobDetailsMarshaller;

    JobDetails jobDetails;

    JsonObject jsonObject;

    @BeforeEach
    void setUp() {
        jobDetailsMarshaller = new JobDetailsMarshaller(new TriggerMarshaller(), new RecipientMarshaller());

        String id = "testId";
        String correlationId = "testCorrelationId";
        JobStatus status = JobStatus.SCHEDULED;
        Date date = new Date();
        ZonedDateTime lastUpdate = ZonedDateTime.ofInstant(date.toInstant(), DEFAULT_ZONE);
        Integer retries = 2;
        Integer priority = 3;
        Integer executionCounter = 4;
        String scheduledId = "testScheduledId";
        String payload = "test";
        Recipient recipient = new RecipientInstance(HttpRecipient.builder().forStringPayload().url("url").payload(HttpRecipientStringPayloadData.from(payload)).build());
        Trigger trigger = new PointInTimeTrigger(new Date().toInstant().toEpochMilli(), null, null);

        jobDetails = JobDetails.builder()
                .id(id)
                .correlationId(correlationId)
                .status(status)
                .lastUpdate(lastUpdate)
                .retries(retries)
                .executionCounter(executionCounter)
                .scheduledId(scheduledId)
                .priority(priority)
                .recipient(recipient)
                .trigger(trigger)
                .build();

        jsonObject = new JsonObject()
                .put("id", id)
                .put("correlationId", correlationId)
                .put("status", status.name())
                .put("lastUpdate", date.getTime())
                .put("retries", retries)
                .put("executionCounter", executionCounter)
                .put("scheduledId", scheduledId)
                .put("priority", priority)
                .put("recipient", JsonObject
                        .mapFrom(HttpRecipient.builder().forStringPayload().url("url").payload(HttpRecipientStringPayloadData.from(payload)).build())
                        .put("classType", HttpRecipient.class.getName()))
                .put("trigger", new JsonObject()
                        .put("nextFireTime", trigger.hasNextFireTime().getTime())
                        .put("classType", PointInTimeTrigger.class.getName()));
    }

    @Test
    void marshall() {
        assertEquals(jsonObject, jobDetailsMarshaller.marshall(jobDetails));
    }

    @Test
    void marshallNull() {
        assertNull(jobDetailsMarshaller.marshall(null));
    }

    @Test
    void unmarshall() {
        assertEquals(jobDetails, jobDetailsMarshaller.unmarshall(jsonObject));
    }

    @Test
    void unmarshallNull() {
        assertNull(jobDetailsMarshaller.unmarshall(null));
    }
}
