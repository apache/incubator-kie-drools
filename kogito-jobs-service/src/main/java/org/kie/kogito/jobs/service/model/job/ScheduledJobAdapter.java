/*
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.kie.kogito.jobs.service.model.job;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.kie.kogito.jobs.api.JobBuilder;
import org.kie.kogito.jobs.service.model.ScheduledJob;
import org.kie.kogito.jobs.service.utils.DateUtil;
import org.kie.kogito.timer.Trigger;
import org.kie.kogito.timer.impl.IntervalTrigger;
import org.kie.kogito.timer.impl.PointInTimeTrigger;

import static org.kie.kogito.jobs.service.utils.DateUtil.toDate;

public class ScheduledJobAdapter {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private ScheduledJobAdapter() {
    }

    public static ScheduledJob of(JobDetails jobDetails) {
        final ProcessPayload payload = payloadDeserialize(String.valueOf(jobDetails.getPayload()));
        return ScheduledJob.builder()
                .job(new JobBuilder()
                             .id(jobDetails.getId())
                             .priority(jobDetails.getPriority())
                             .expirationTime(Optional.ofNullable(jobDetails.getTrigger())
                                                     .map(Trigger::hasNextFireTime)
                                                     .map(DateUtil::fromDate)
                                                     .orElse(null))
                             .callbackEndpoint(Optional.ofNullable(jobDetails.getRecipient())
                                                       .map(Recipient.HTTPRecipient.class::cast)
                                                       .map(Recipient.HTTPRecipient::getEndpoint)
                                                       .orElse(null))
                             .repeatLimit(Optional.ofNullable(jobDetails.getTrigger())
                                                  .filter(IntervalTrigger.class::isInstance)
                                                  .map(IntervalTrigger.class::cast)
                                                  .map(IntervalTrigger::getRepeatLimit)
                                                  .orElse(null))
                             .repeatInterval(Optional.ofNullable(jobDetails.getTrigger())
                                                     .filter(IntervalTrigger.class::isInstance)
                                                     .map(IntervalTrigger.class::cast)
                                                     .map(IntervalTrigger::getPeriod)
                                                     .orElse(null))
                             .rootProcessId(payload.getRootProcessId())
                             .rootProcessInstanceId(payload.getRootProcessInstanceId())
                             .processId(payload.getProcessId())
                             .processInstanceId(payload.getProcessInstanceId())
                             .build())
                .scheduledId(jobDetails.getScheduledId())
                .status(jobDetails.getStatus())
                .executionCounter(jobDetails.getExecutionCounter())
                .retries(jobDetails.getRetries())
                .lastUpdate(jobDetails.getLastUpdate())
                .build();
    }

    public static JobDetails to(ScheduledJob scheduledJob) {
        return new JobDetailsBuilder()
                .id(scheduledJob.getId())
                .correlationId(scheduledJob.getId())
                .executionCounter(scheduledJob.getExecutionCounter())
                .lastUpdate(scheduledJob.getLastUpdate())
                .payload("process")
                .recipient(new Recipient.HTTPRecipient(scheduledJob.getCallbackEndpoint()))
                .retries(scheduledJob.getRetries())
                .scheduledId(scheduledJob.getScheduledId())
                .status(scheduledJob.getStatus())
                .type(JobDetails.Type.HTTP)
                .trigger(triggerAdapter(scheduledJob))
                .priority(scheduledJob.getPriority())
                .payload(payloadSerialize(scheduledJob))
                .build();
    }

    public static Trigger triggerAdapter(ScheduledJob scheduledJob) {
        return Optional.ofNullable(scheduledJob)
                .filter(job -> Objects.nonNull(job.getExpirationTime()))
                .map(job -> job.hasInterval()
                        .<Trigger>map(interval -> new IntervalTrigger(0l,
                                                                      toDate(scheduledJob.getExpirationTime()),
                                                                      null,
                                                                      scheduledJob.getRepeatLimit(),
                                                                      0,
                                                                      interval,
                                                                      null,
                                                                      null))
                        .orElse(new PointInTimeTrigger(scheduledJob.getExpirationTime().toInstant().toEpochMilli(),
                                                       null, null)))
                .orElse(null);
    }

    public static IntervalTrigger intervalTrigger(ZonedDateTime start, int repeatLimit, int intervalMillis) {
        return new IntervalTrigger(0, toDate(start), null, repeatLimit, 0, intervalMillis, null, null);
    }

    public static String payloadSerialize(ScheduledJob scheduledJob) {
        try {
            return OBJECT_MAPPER.writeValueAsString(new ProcessPayload(scheduledJob.getProcessInstanceId(),
                                                                       scheduledJob.getRootProcessInstanceId(),
                                                                       scheduledJob.getProcessId(),
                                                                       scheduledJob.getRootProcessId()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static ProcessPayload payloadDeserialize(String payload) {
        try {
            return OBJECT_MAPPER.readValue(payload, ProcessPayload.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    final static class ProcessPayload {

        private String processInstanceId;
        private String rootProcessInstanceId;
        private String processId;
        private String rootProcessId;

        private ProcessPayload() {
            //needed by jackson
        }

        public ProcessPayload(String processInstanceId, String rootProcessInstanceId, String processId, String rootProcessId) {
            this.processInstanceId = processInstanceId;
            this.rootProcessInstanceId = rootProcessInstanceId;
            this.processId = processId;
            this.rootProcessId = rootProcessId;
        }

        public String getProcessInstanceId() {
            return processInstanceId;
        }

        public String getRootProcessInstanceId() {
            return rootProcessInstanceId;
        }

        public String getProcessId() {
            return processId;
        }

        public String getRootProcessId() {
            return rootProcessId;
        }
    }
}
