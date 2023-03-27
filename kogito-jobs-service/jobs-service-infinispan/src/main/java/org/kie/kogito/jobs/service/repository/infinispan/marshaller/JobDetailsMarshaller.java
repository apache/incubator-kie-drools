/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.jobs.service.repository.infinispan.marshaller;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.kie.kogito.jobs.service.model.Recipient;
import org.kie.kogito.jobs.service.repository.marshaller.RecipientMarshaller;
import org.kie.kogito.timer.Trigger;

import io.vertx.core.json.JsonObject;

import static org.kie.kogito.jobs.service.utils.DateUtil.instantToZonedDateTime;
import static org.kie.kogito.jobs.service.utils.DateUtil.zonedDateTimeToInstant;

public class JobDetailsMarshaller extends BaseMarshaller<JobDetails> {

    private RecipientMarshaller recipientMarshaller;

    public JobDetailsMarshaller(RecipientMarshaller recipientMarshaller) {
        this.recipientMarshaller = recipientMarshaller;
    }

    @Override
    public String getTypeName() {
        return getPackage() + ".JobDetails";
    }

    @Override
    public Class<? extends JobDetails> getJavaClass() {
        return JobDetails.class;
    }

    @Override
    public void writeTo(ProtoStreamWriter writer, JobDetails job) throws IOException {
        writer.writeString("id", job.getId());
        writer.writeString("correlationId", job.getCorrelationId());
        writer.writeString("status", mapEnum(job.getStatus()));
        writer.writeInstant("lastUpdate", zonedDateTimeToInstant(job.getLastUpdate()));
        writer.writeInt("retries", job.getRetries());
        writer.writeInt("priority", job.getPriority());
        writer.writeInt("executionCounter", job.getExecutionCounter());
        writer.writeString("scheduledId", job.getScheduledId());
        writer.writeString("recipient", Optional.ofNullable(job.getRecipient()).map(r -> recipientMarshaller.marshall(r).encode()).orElse(null));
        writer.writeObject("trigger", job.getTrigger(), getInterface(job.getTrigger()));
        writer.writeLong("executionTimeout", job.getExecutionTimeout());
        writer.writeString("executionTimeoutUnit", job.getExecutionTimeoutUnit() != null ? job.getExecutionTimeoutUnit().name() : null);
    }

    public Class<?> getInterface(Object object) {
        return Optional.ofNullable(object)
                .map(Object::getClass)
                .map(Class::getInterfaces)
                .map(i -> i[0])
                .orElse(null);
    }

    @Override
    public JobDetails readFrom(ProtoStreamReader reader) throws IOException {
        String id = reader.readString("id");
        String correlationId = reader.readString("correlationId");
        JobStatus status = mapString(reader.readString("status"), JobStatus.class);
        ZonedDateTime lastUpdate = instantToZonedDateTime(reader.readInstant("lastUpdate"));
        Integer retries = reader.readInt("retries");
        Integer priority = reader.readInt("priority");
        Integer executionCounter = reader.readInt("executionCounter");
        String scheduledId = reader.readString("scheduledId");
        Recipient recipient = Optional.ofNullable(reader.readString("recipient")).map(r -> recipientMarshaller.unmarshall(new JsonObject(r))).orElse(null);
        Trigger trigger = reader.readObject("trigger", Trigger.class);
        Long executionTimeout = reader.readLong("executionTimeout");
        String executionTimeoutUnit = reader.readString("executionTimeoutUnit");

        return JobDetails.builder()
                .id(id)
                .correlationId(correlationId)
                .status(status)
                .lastUpdate(lastUpdate)
                .retries(retries)
                .priority(priority)
                .executionCounter(executionCounter)
                .scheduledId(scheduledId)
                .recipient(recipient)
                .trigger(trigger)
                .executionTimeout(executionTimeout)
                .executionTimeoutUnit(executionTimeoutUnit != null ? ChronoUnit.valueOf(executionTimeoutUnit) : null)
                .build();
    }
}
