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
    public void writeTo(ProtoStreamWriter writer, ScheduledJob scheduledJob) throws IOException {
        writer.writeString("scheduledId", scheduledJob.getScheduledId());
        writer.writeObject("job", scheduledJob.getJob(), Job.class);
        writer.writeInt("retries", scheduledJob.getRetries());
        writer.writeString("status", scheduledJob.getStatus().name());
        writer.writeInstant("lastUpdate", zonedDateTimeToInstant(scheduledJob.getLastUpdate()));

    }

    @Override
    public ScheduledJob readFrom(ProtoStreamReader reader) throws IOException {
        String scheduledId = reader.readString("scheduledId");
        Job job = reader.readObject("job", Job.class);
        Integer retries = reader.readInt("retries");
        JobStatus status = JobStatus.valueOf(reader.readString("status"));
        ZonedDateTime lastUpdate = instantToZonedDateTime(reader.readInstant("lastUpdate"));
        return ScheduledJob.builder()
                .scheduledId(scheduledId)
                .retries(retries)
                .status(status)
                .job(job)
                .lastUpdate(lastUpdate)
                .build();
    }
}