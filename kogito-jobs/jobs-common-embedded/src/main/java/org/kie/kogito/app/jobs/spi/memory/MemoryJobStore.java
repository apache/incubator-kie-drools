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
package org.kie.kogito.app.jobs.spi.memory;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.kogito.app.jobs.spi.JobContext;
import org.kie.kogito.app.jobs.spi.JobStore;
import org.kie.kogito.jobs.DurationExpirationTime;
import org.kie.kogito.jobs.ExactExpirationTime;
import org.kie.kogito.jobs.JobDescription;
import org.kie.kogito.jobs.service.json.DurationExpirationTimeDeserializer;
import org.kie.kogito.jobs.service.json.DurationExpirationTimeSerializer;
import org.kie.kogito.jobs.service.json.ExactExpirationTimeDeserializer;
import org.kie.kogito.jobs.service.json.ExactExpirationTimeSerializer;
import org.kie.kogito.jobs.service.json.JobDescriptionDeserializer;
import org.kie.kogito.jobs.service.json.JobDescriptionSerializer;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.kie.kogito.jobs.service.utils.DateUtil;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.cloudevents.jackson.JsonFormat;

public class MemoryJobStore implements JobStore {

    private Map<String, JobDetails> jobs;
    private ObjectMapper objectMapper;

    public MemoryJobStore() {
        this.jobs = new HashMap<>();
        this.objectMapper = new ObjectMapper();

        SimpleModule kogitoCustomModule = new SimpleModule();
        kogitoCustomModule.addSerializer(JobDescription.class, new JobDescriptionSerializer());
        kogitoCustomModule.addDeserializer(JobDescription.class, new JobDescriptionDeserializer());
        kogitoCustomModule.addSerializer(DurationExpirationTime.class, new DurationExpirationTimeSerializer());
        kogitoCustomModule.addDeserializer(DurationExpirationTime.class, new DurationExpirationTimeDeserializer());
        kogitoCustomModule.addSerializer(ExactExpirationTime.class, new ExactExpirationTimeSerializer());
        kogitoCustomModule.addDeserializer(ExactExpirationTime.class, new ExactExpirationTimeDeserializer());
        objectMapper.registerModule(new JavaTimeModule()).registerModule(kogitoCustomModule)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .registerModule(JsonFormat.getCloudEventJacksonModule());
    }

    @Override
    public synchronized void persist(JobContext context, JobDetails jobDetails) {
        this.jobs.put(jobDetails.getId(), JobDetails.builder().of(jobDetails).build());
    }

    @Override
    public synchronized JobDetails find(JobContext context, String jobId) {
        JobDetails jobDetails = jobs.get(jobId);
        if (jobDetails == null) {
            return null;
        }
        return JobDetails.builder().of(jobDetails).build();
    }

    @Override
    public synchronized void update(JobContext context, JobDetails jobDetails) {
        JobDetails persisted = JobDetails.builder().of(jobDetails).build();
        this.jobs.put(jobDetails.getId(), persisted);
    }

    @Override
    public synchronized void remove(JobContext context, String jobId) {
        this.jobs.remove(jobId);
    }

    @Override
    public synchronized boolean shouldRun(JobContext jobContext, String jobId) {
        JobDetails jobDetails = find(jobContext, jobId);
        if (jobDetails == null) {
            return false;
        }
        if (EnumSet.of(JobStatus.RETRY, JobStatus.SCHEDULED).contains(jobDetails.getStatus())) {
            update(jobContext, JobDetails.builder().of(jobDetails).status(JobStatus.RUNNING).build());
            return true;
        }
        return false;
    }

    @Override
    public synchronized List<JobDetails> loadActiveJobs(JobContext jobContext, OffsetDateTime maxWindowsLoad) {
        List<JobDetails> copyJobDetails = new ArrayList<>();
        for (JobDetails jobDetails : jobs.values().stream().filter(j -> DateUtil.dateToOffsetDateTime(j.getTrigger().hasNextFireTime()).isBefore(maxWindowsLoad)).toList()) {
            copyJobDetails.add(JobDetails.builder().of(jobDetails).build());
        }
        return copyJobDetails;
    }

}
