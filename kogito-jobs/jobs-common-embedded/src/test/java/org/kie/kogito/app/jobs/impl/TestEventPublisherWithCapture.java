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
package org.kie.kogito.app.jobs.impl;

import java.util.ArrayList;
import java.util.List;

import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.job.JobInstanceDataEvent;
import org.kie.kogito.jobs.service.model.ScheduledJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Test event publisher that captures published events for validation.
 * Extends TestEventPublisher to maintain compatibility with existing tests.
 */
public class TestEventPublisherWithCapture extends TestEventPublisher {

    private static final Logger LOG = LoggerFactory.getLogger(TestEventPublisherWithCapture.class);

    private final List<ScheduledJob> capturedJobs = new ArrayList<>();
    private final ObjectMapper objectMapper;

    public TestEventPublisherWithCapture() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void publish(DataEvent<?> event) {
        super.publish(event);

        if (event instanceof JobInstanceDataEvent) {
            JobInstanceDataEvent jobEvent = (JobInstanceDataEvent) event;
            try {
                ScheduledJob scheduledJob = objectMapper.readValue(jobEvent.getData(), ScheduledJob.class);
                capturedJobs.add(scheduledJob);
                LOG.info("Captured job event: id={}, status={}, exceptionMessage={}",
                        scheduledJob.getId(),
                        scheduledJob.getStatus(),
                        scheduledJob.getExceptionMessage() != null ? scheduledJob.getExceptionMessage() : "null");
            } catch (Exception e) {
                LOG.error("Failed to deserialize job event", e);
            }
        }
    }

    /**
     * Returns all captured ScheduledJob objects from published events.
     */
    public List<ScheduledJob> getCapturedJobs() {
        return new ArrayList<>(capturedJobs);
    }

    /**
     * Returns the most recently captured ScheduledJob, or null if none captured.
     */
    public ScheduledJob getLastCapturedJob() {
        return capturedJobs.isEmpty() ? null : capturedJobs.get(capturedJobs.size() - 1);
    }

    /**
     * Clears all captured jobs.
     */
    public void clearCapturedJobs() {
        capturedJobs.clear();
    }
}
