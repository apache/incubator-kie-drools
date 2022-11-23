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
package org.kie.kogito.jobs.service.model.job;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;

import org.kie.kogito.jobs.api.JobBuilder;
import org.kie.kogito.jobs.service.model.ScheduledJob;
import org.kie.kogito.jobs.service.utils.DateUtil;
import org.kie.kogito.timer.Trigger;
import org.kie.kogito.timer.impl.IntervalTrigger;
import org.kie.kogito.timer.impl.PointInTimeTrigger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import static org.kie.kogito.jobs.service.utils.DateUtil.toDate;

public class ScheduledJobAdapter {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        OBJECT_MAPPER.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    private ScheduledJobAdapter() {
    }

    public static ScheduledJob of(JobDetails jobDetails) {
        final ProcessPayload payload = payloadDeserialize(jobDetails.getPayload());
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
                                .map(i -> i + 1)
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
                        .nodeInstanceId(payload.getNodeInstanceId())
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
                .recipient(Optional.ofNullable(scheduledJob.getCallbackEndpoint())
                        .map(Recipient.HTTPRecipient::new)
                        .orElse(null))
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
                        .<Trigger> map(interval -> new IntervalTrigger(0l,
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
            if (Objects.isNull(scheduledJob.getProcessInstanceId())
                    && Objects.isNull(scheduledJob.getRootProcessInstanceId())
                    && Objects.isNull(scheduledJob.getProcessId())
                    && Objects.isNull(scheduledJob.getRootProcessId())) {
                return null;
            }
            return OBJECT_MAPPER.writeValueAsString(new ProcessPayload(scheduledJob.getProcessInstanceId(),
                    scheduledJob.getRootProcessInstanceId(),
                    scheduledJob.getProcessId(),
                    scheduledJob.getRootProcessId(),
                    scheduledJob.getNodeInstanceId()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static ProcessPayload payloadDeserialize(Object payload) {
        return Optional.ofNullable(payload)
                .map(String::valueOf)
                .map(p -> {
                    try {
                        return OBJECT_MAPPER.readValue(p, ProcessPayload.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .orElse(new ProcessPayload());
    }

    final static class ProcessPayload {

        private String processInstanceId;
        private String rootProcessInstanceId;
        private String processId;
        private String rootProcessId;
        private String nodeInstanceId;

        private ProcessPayload() {
            //needed by jackson
        }

        public ProcessPayload(String processInstanceId, String rootProcessInstanceId, String processId,
                String rootProcessId, String nodeInstanceId) {
            this.processInstanceId = processInstanceId;
            this.rootProcessInstanceId = rootProcessInstanceId;
            this.processId = processId;
            this.rootProcessId = rootProcessId;
            this.nodeInstanceId = nodeInstanceId;
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

        public String getNodeInstanceId() {
            return nodeInstanceId;
        }
    }
}
