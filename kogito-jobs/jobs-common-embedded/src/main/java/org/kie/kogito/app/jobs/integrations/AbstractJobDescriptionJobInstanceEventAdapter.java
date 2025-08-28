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
package org.kie.kogito.app.jobs.integrations;

import java.util.Optional;

import org.kie.kogito.app.jobs.api.JobDetailsEventAdapter;
import org.kie.kogito.app.jobs.impl.InVMPayloadData;
import org.kie.kogito.event.job.JobInstanceDataEvent;
import org.kie.kogito.jobs.JobDescription;
import org.kie.kogito.jobs.api.Job;
import org.kie.kogito.jobs.api.JobBuilder;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.ScheduledJob;
import org.kie.kogito.jobs.service.utils.DateUtil;
import org.kie.kogito.timer.Trigger;
import org.kie.kogito.timer.impl.IntervalTrigger;
import org.kie.kogito.timer.impl.SimpleTimerTrigger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public abstract class AbstractJobDescriptionJobInstanceEventAdapter implements JobDetailsEventAdapter {
    public static final String JOB_EVENT_TYPE = "JobEvent";

    private ObjectMapper objectMapper;

    private String serviceURL;

    public AbstractJobDescriptionJobInstanceEventAdapter(String serviceURL) {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.serviceURL = serviceURL;
    }

    @Override
    public JobInstanceDataEvent adapt(JobDetails jobDetails) {
        try {
            ScheduledJob scheduledJob = toScheduleJob(jobDetails);
            byte[] jsonContent = objectMapper.writeValueAsBytes(scheduledJob);
            JobInstanceDataEvent jobInstanceEvent = new JobInstanceDataEvent(
                    JOB_EVENT_TYPE,
                    serviceURL,
                    jsonContent,
                    scheduledJob.getProcessInstanceId(), scheduledJob.getRootProcessInstanceId(),
                    scheduledJob.getProcessId(), scheduledJob.getRootProcessId(), null);
            return jobInstanceEvent;
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    protected abstract void doAdaptPayload(JobBuilder jobBuilder, JobDescription jobDescription);

    protected ScheduledJob toScheduleJob(JobDetails jobDetails) {
        JobBuilder jobBuilder = new JobBuilder()
                .id(jobDetails.getId())
                .priority(jobDetails.getPriority())
                .expirationTime(Optional.ofNullable(jobDetails.getTrigger()).map(Trigger::hasNextFireTime).map(DateUtil::fromDate).orElse(null))
                .callbackEndpoint(null)
                .repeatLimit(extractRepeatLimit(jobDetails.getTrigger()))
                .repeatInterval(extractRepeatInterval(jobDetails.getTrigger()));

        doAdaptPayload(jobBuilder, extractJobDescription(jobDetails));

        Job job = jobBuilder.build();

        ScheduledJob scheduledJob = ScheduledJob.builder()
                .job(job)
                .scheduledId(jobDetails.getScheduledId())
                .status(jobDetails.getStatus())
                .executionCounter(jobDetails.getExecutionCounter())
                .retries(jobDetails.getRetries())
                .lastUpdate(jobDetails.getLastUpdate())
                .build();

        return scheduledJob;
    }

    protected JobDescription extractJobDescription(JobDetails jobDetails) {
        return jobDetails.getRecipient().<InVMPayloadData> getRecipient().getPayload().getJobDescription();
    }

    private Integer extractRepeatLimit(Trigger trigger) {
        if (trigger instanceof SimpleTimerTrigger) {
            return ((SimpleTimerTrigger) trigger).getRepeatCount();
        }
        if (trigger instanceof IntervalTrigger) {
            return ((IntervalTrigger) trigger).getRepeatLimit();
        }
        return null;
    }

    private Long extractRepeatInterval(Trigger trigger) {
        if (trigger instanceof SimpleTimerTrigger) {
            SimpleTimerTrigger simpleTimerTrigger = (SimpleTimerTrigger) trigger;
            // external services right now expect intervals in milliseconds.
            return simpleTimerTrigger.getPeriodUnit().getDuration().multipliedBy(simpleTimerTrigger.getPeriod())
                    .toMillis();
        }
        if (trigger instanceof IntervalTrigger) {
            return ((IntervalTrigger) trigger).getPeriod();
        }
        return null;
    }

}
