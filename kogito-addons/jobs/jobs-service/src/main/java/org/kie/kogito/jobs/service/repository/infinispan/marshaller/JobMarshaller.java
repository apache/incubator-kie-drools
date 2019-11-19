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

public class JobMarshaller extends BaseMarshaller<Job> {

    @Override
    public String getTypeName() {
        return getPackage() + ".Job";
    }

    @Override
    public Class<? extends Job> getJavaClass() {
        return Job.class;
    }

    @Override
    public void writeTo(ProtoStreamWriter writer, Job job) throws IOException {
        writer.writeString("id", job.getId());
        writer.writeString("callbackEndpoint", job.getCallbackEndpoint());
        writer.writeInstant("expirationTime", zonedDateTimeToInstant(job.getExpirationTime()));
        writer.writeInt("priority", job.getPriority());
        writer.writeString("processId", job.getProcessId());
        writer.writeString("processInstanceId", job.getProcessInstanceId());
        writer.writeString("rootProcessId", job.getRootProcessId());
        writer.writeString("rootProcessInstanceId", job.getRootProcessInstanceId());
    }

    @Override
    public Job readFrom(ProtoStreamReader reader) throws IOException {
        String id = reader.readString("id");
        String callbackEndpoint = reader.readString("callbackEndpoint");
        ZonedDateTime expirationTime = instantToZonedDateTime(reader.readInstant("expirationTime"));
        Integer priority = reader.readInt("priority");
        String processId = reader.readString("processId");
        String processInstanceId = reader.readString("processInstanceId");
        String rootProcessId = reader.readString("rootProcessId");
        String rootProcessInstanceId = reader.readString("rootProcessInstanceId");
        return JobBuilder.builder()
                .callbackEndpoint(callbackEndpoint)
                .id(id)
                .expirationTime(expirationTime)
                .priority(priority)
                .processId(processId)
                .processInstanceId(processInstanceId)
                .rootProcessId(rootProcessId)
                .rootProcessInstanceId(rootProcessInstanceId)
                .build();
    }
}
