/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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

import org.kie.kogito.jobs.api.Job;
import org.kie.kogito.jobs.api.JobBuilder;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.kie.kogito.jobs.service.model.ScheduledJob;

public class ScheduledJobMarshaller extends BaseMarshaller<ScheduledJob> {

    @Override
    public String getTypeName() {
        return getPackage() + ".ScheduledJob";
    }

    @Override
    public Class<? extends ScheduledJob> getJavaClass() {
        return ScheduledJob.class;
    }

    @Override
    public void writeTo(ProtoStreamWriter writer, ScheduledJob job) throws IOException {
        writer.writeString("id", job.getId());
        writer.writeString("callbackEndpoint", job.getCallbackEndpoint());
        writer.writeInstant("expirationTime", zonedDateTimeToInstant(job.getExpirationTime()));
        writer.writeInt("priority", job.getPriority());
        writer.writeString("processId", job.getProcessId());
        writer.writeString("processInstanceId", job.getProcessInstanceId());
        writer.writeString("rootProcessId", job.getRootProcessId());
        writer.writeString("rootProcessInstanceId", job.getRootProcessInstanceId());
        writer.writeLong("repeatInterval", job.getRepeatInterval());
        writer.writeInt("repeatLimit", job.getRepeatLimit());

        writer.writeString("scheduledId", job.getScheduledId());
        writer.writeInt("retries", job.getRetries());
        writer.writeString("status", job.getStatus().name());
        writer.writeInstant("lastUpdate", zonedDateTimeToInstant(job.getLastUpdate()));
        writer.writeInt("executionCounter", job.getExecutionCounter());
    }

    @Override
    public ScheduledJob readFrom(ProtoStreamReader reader) throws IOException {
        String id = reader.readString("id");
        String callbackEndpoint = reader.readString("callbackEndpoint");
        ZonedDateTime expirationTime = instantToZonedDateTime(reader.readInstant("expirationTime"));
        Integer priority = reader.readInt("priority");
        String processId = reader.readString("processId");
        String processInstanceId = reader.readString("processInstanceId");
        String rootProcessId = reader.readString("rootProcessId");
        String rootProcessInstanceId = reader.readString("rootProcessInstanceId");
        Long repeatInterval = reader.readLong("repeatInterval");
        Integer repeatLimit = reader.readInt("repeatLimit");
        Job job = JobBuilder.builder()
                .callbackEndpoint(callbackEndpoint)
                .id(id)
                .expirationTime(expirationTime)
                .priority(priority)
                .processId(processId)
                .processInstanceId(processInstanceId)
                .rootProcessId(rootProcessId)
                .rootProcessInstanceId(rootProcessInstanceId)
                .repeatInterval(repeatInterval)
                .repeatLimit(repeatLimit)
                .build();

        String scheduledId = reader.readString("scheduledId");
        Integer retries = reader.readInt("retries");
        JobStatus status = JobStatus.valueOf(reader.readString("status"));
        ZonedDateTime lastUpdate = instantToZonedDateTime(reader.readInstant("lastUpdate"));
        Integer executionCounter = reader.readInt("executionCounter");
        return ScheduledJob.builder()
                .job(job)
                .scheduledId(scheduledId)
                .retries(retries)
                .status(status)
                .job(job)
                .lastUpdate(lastUpdate)
                .executionCounter(executionCounter)
                .build();
    }
}