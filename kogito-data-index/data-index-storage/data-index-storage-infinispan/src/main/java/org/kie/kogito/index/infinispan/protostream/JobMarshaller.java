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
package org.kie.kogito.index.infinispan.protostream;

import java.io.IOException;

import org.infinispan.protostream.MessageMarshaller;
import org.kie.kogito.index.model.Job;
import org.kie.kogito.persistence.infinispan.protostream.AbstractMarshaller;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JobMarshaller extends AbstractMarshaller implements MessageMarshaller<Job> {

    public JobMarshaller(ObjectMapper mapper) {
        super(mapper);
    }

    @Override
    public Job readFrom(ProtoStreamReader reader) throws IOException {
        Job job = new Job();
        job.setId(reader.readString("id"));
        job.setProcessId(reader.readString("processId"));
        job.setProcessInstanceId(reader.readString("processInstanceId"));
        job.setRootProcessId(reader.readString("rootProcessId"));
        job.setRootProcessInstanceId(reader.readString("rootProcessInstanceId"));
        job.setExpirationTime(dateToZonedDateTime(reader.readDate("expirationTime")));
        job.setPriority(reader.readInt("priority"));
        job.setCallbackEndpoint(reader.readString("callbackEndpoint"));
        job.setRepeatInterval(reader.readLong("repeatInterval"));
        job.setRepeatLimit(reader.readInt("repeatLimit"));
        job.setScheduledId(reader.readString("scheduledId"));
        job.setRetries(reader.readInt("retries"));
        job.setStatus(reader.readString("status"));
        job.setLastUpdate(dateToZonedDateTime(reader.readDate("lastUpdate")));
        job.setExecutionCounter(reader.readInt("executionCounter"));
        job.setEndpoint(reader.readString("endpoint"));
        job.setNodeInstanceId(reader.readString("nodeInstanceId"));
        return job;
    }

    @Override
    public void writeTo(ProtoStreamWriter writer, Job job) throws IOException {
        writer.writeString("id", job.getId());
        writer.writeString("processId", job.getProcessId());
        writer.writeString("processInstanceId", job.getProcessInstanceId());
        writer.writeString("rootProcessId", job.getRootProcessId());
        writer.writeString("rootProcessInstanceId", job.getRootProcessInstanceId());
        writer.writeDate("expirationTime", zonedDateTimeToDate(job.getExpirationTime()));
        writer.writeInt("priority", job.getPriority());
        writer.writeString("callbackEndpoint", job.getCallbackEndpoint());
        writer.writeLong("repeatInterval", job.getRepeatInterval());
        writer.writeInt("repeatLimit", job.getRepeatLimit());
        writer.writeString("scheduledId", job.getScheduledId());
        writer.writeInt("retries", job.getRetries());
        writer.writeString("status", job.getStatus());
        writer.writeDate("lastUpdate", zonedDateTimeToDate(job.getLastUpdate()));
        writer.writeInt("executionCounter", job.getExecutionCounter());
        writer.writeString("endpoint", job.getEndpoint());
        writer.writeString("nodeInstanceId", job.getNodeInstanceId());
    }

    @Override
    public Class<? extends Job> getJavaClass() {
        return Job.class;
    }

    @Override
    public String getTypeName() {
        return getJavaClass().getName();
    }
}
