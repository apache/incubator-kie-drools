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
package org.kie.kogito.index.jpa.mapper;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.index.jpa.model.JobEntity;
import org.kie.kogito.index.model.Job;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractJobEntityMapperIT {

    @Inject
    JobEntityMapper mapper;

    Job job = new Job();

    JobEntity jobEntity = new JobEntity();

    @BeforeEach
    void setup() {
        String testId = "testId";
        ZonedDateTime time = ZonedDateTime.now();
        String status = "ACTIVE";
        String processId = "testProcessId";
        String processInstanceId = "testProcessInstanceId";
        String rootProcessId = "testRootProcessId";
        String rootProcessInstanceId = "testRootProcessInstanceId";
        Integer priority = 79;
        String callbackEndpoint = "testCallbackEndpoint";
        Long repeatInterval = 70L;
        Integer repeatLimit = 89;
        String scheduledId = "testScheduleId";
        Integer retries = 25;
        Integer executionCounter = 17;

        job.setId(testId);
        job.setStatus(status);
        job.setLastUpdate(time);
        job.setProcessId(processId);
        job.setProcessInstanceId(processInstanceId);
        job.setRootProcessId(rootProcessId);
        job.setRootProcessInstanceId(rootProcessInstanceId);
        job.setExpirationTime(time);
        job.setPriority(priority);
        job.setCallbackEndpoint(callbackEndpoint);
        job.setRepeatInterval(repeatInterval);
        job.setRepeatLimit(repeatLimit);
        job.setScheduledId(scheduledId);
        job.setRetries(retries);
        job.setExecutionCounter(executionCounter);

        jobEntity.setId(testId);
        jobEntity.setStatus(status);
        jobEntity.setLastUpdate(time);
        jobEntity.setProcessId(processId);
        jobEntity.setProcessInstanceId(processInstanceId);
        jobEntity.setRootProcessId(rootProcessId);
        jobEntity.setRootProcessInstanceId(rootProcessInstanceId);
        jobEntity.setExpirationTime(time);
        jobEntity.setPriority(priority);
        jobEntity.setCallbackEndpoint(callbackEndpoint);
        jobEntity.setRepeatInterval(repeatInterval);
        jobEntity.setRepeatLimit(repeatLimit);
        jobEntity.setScheduledId(scheduledId);
        jobEntity.setRetries(retries);
        jobEntity.setExecutionCounter(executionCounter);
    }

    @Test
    void testMapToEntity() {
        JobEntity result = mapper.mapToEntity(job);
        assertThat(result).isEqualToIgnoringGivenFields(jobEntity, "$$_hibernate_tracker");
    }

    @Test
    void testMapToModel() {
        Job result = mapper.mapToModel(jobEntity);
        assertThat(result).isEqualToComparingFieldByField(job);
    }
}
