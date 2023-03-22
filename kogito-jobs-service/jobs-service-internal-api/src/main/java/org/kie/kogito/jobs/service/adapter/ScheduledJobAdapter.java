/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.jobs.service.adapter;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import org.kie.kogito.jobs.api.JobBuilder;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipient;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.JobDetailsBuilder;
import org.kie.kogito.jobs.service.model.Recipient;
import org.kie.kogito.jobs.service.model.RecipientInstance;
import org.kie.kogito.jobs.service.model.ScheduledJob;
import org.kie.kogito.jobs.service.utils.DateUtil;
import org.kie.kogito.timer.Trigger;
import org.kie.kogito.timer.impl.IntervalTrigger;
import org.kie.kogito.timer.impl.SimpleTimerTrigger;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

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
        //using headers (the new API approach)
        final ProcessPayload payload = Optional.ofNullable(jobDetails.getRecipient())
                .map(Recipient::getRecipient)
                .filter(HttpRecipient.class::isInstance)
                .map(HttpRecipient.class::cast)
                .map(httpRecipient -> {
                    String processInstanceId = httpRecipient.getHeader("processInstanceId");
                    String rootProcessInstanceId = httpRecipient.getHeader("rootProcessInstanceId");
                    String processId = httpRecipient.getHeader("processId");
                    String rootProcessId = httpRecipient.getHeader("rootProcessId");
                    String nodeInstanceId = httpRecipient.getHeader("nodeInstanceId");
                    return new ProcessPayload(processInstanceId, rootProcessInstanceId, processId, rootProcessId, nodeInstanceId);
                })
                .filter(processPayload -> Objects.nonNull(processPayload.processInstanceId))//just to guarantee headers were present
                .orElse(new ProcessPayload());

        return ScheduledJob.builder()
                .job(new JobBuilder()
                        .id(jobDetails.getId())
                        .priority(jobDetails.getPriority())
                        .expirationTime(Optional.ofNullable(jobDetails.getTrigger())
                                .map(Trigger::hasNextFireTime)
                                .map(DateUtil::fromDate)
                                .orElse(null))
                        .callbackEndpoint(Optional.ofNullable(jobDetails.getRecipient())
                                .map(Recipient::getRecipient)
                                .filter(HttpRecipient.class::isInstance)
                                .map(HttpRecipient.class::cast)
                                .map(HttpRecipient::getUrl)
                                .orElse(null))
                        .repeatLimit(extractRepeatLimit(jobDetails.getTrigger()))
                        .repeatInterval(extractRepeatInterval(jobDetails.getTrigger()))
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
                .recipient(recipientAdapter(scheduledJob))
                .retries(scheduledJob.getRetries())
                .scheduledId(scheduledJob.getScheduledId())
                .status(scheduledJob.getStatus())
                .trigger(triggerAdapter(scheduledJob))
                .priority(scheduledJob.getPriority())
                .build();
    }

    private static RecipientInstance recipientAdapter(ScheduledJob scheduledJob) {
        return Optional.ofNullable(scheduledJob.getCallbackEndpoint())
                .map(url -> new RecipientInstance(HttpRecipient.builder()
                        .forStringPayload()
                        .url(url)
                        .header("processId", scheduledJob.getProcessId())
                        .header("processInstanceId", scheduledJob.getProcessInstanceId())
                        .header("rootProcessInstanceId", scheduledJob.getRootProcessInstanceId())
                        .header("rootProcessId", scheduledJob.getRootProcessId())
                        .header("nodeInstanceId", scheduledJob.getNodeInstanceId())
                        .build()))
                .orElse(null);
    }

    public static Trigger triggerAdapter(ScheduledJob scheduledJob) {
        if (scheduledJob.getExpirationTime() == null) { //keep v1 criteria to check if the trigger can be created.
            return null;
        }
        Date startTime = DateUtil.toDate(scheduledJob.getExpirationTime().toOffsetDateTime());
        String zoneId = scheduledJob.getExpirationTime().toOffsetDateTime().getOffset().getId();
        long period = 0;
        ChronoUnit periodUnit = ChronoUnit.MILLIS;
        int repeatCount = 0;
        if (scheduledJob.hasInterval().isPresent()) { //keep v1 criteria to detect IntervalTrigger
            //shift the IntervalTrigger repetitions to the SimpleTimerRepeat repetitions
            if (scheduledJob.getRepeatLimit() != null && scheduledJob.getRepeatLimit() > 1) {
                repeatCount = scheduledJob.getRepeatLimit() - 1;
            }
            period = repeatCount != 0 ? scheduledJob.hasInterval().get() : 0;
        }
        return new SimpleTimerTrigger(startTime, period, periodUnit, repeatCount, zoneId);
    }

    private static Integer extractRepeatLimit(Trigger trigger) {
        if (trigger instanceof SimpleTimerTrigger) {
            return ((SimpleTimerTrigger) trigger).getRepeatCount();
        }
        if (trigger instanceof IntervalTrigger) {
            return ((IntervalTrigger) trigger).getRepeatLimit();
        }
        return null;
    }

    private static Long extractRepeatInterval(Trigger trigger) {
        if (trigger instanceof SimpleTimerTrigger) {
            SimpleTimerTrigger simpleTimerTrigger = (SimpleTimerTrigger) trigger;
            // external services right now expect intervals in milliseconds.
            return simpleTimerTrigger.getPeriodUnit()
                    .getDuration()
                    .multipliedBy(simpleTimerTrigger.getPeriod())
                    .toMillis();
        }
        if (trigger instanceof IntervalTrigger) {
            return ((IntervalTrigger) trigger).getPeriod();
        }
        return null;
    }

    public final static class ProcessPayload {
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

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ProcessPayload that = (ProcessPayload) o;
            return Objects.equals(processInstanceId, that.processInstanceId) && Objects.equals(rootProcessInstanceId, that.rootProcessInstanceId) && Objects.equals(processId,
                    that.processId) && Objects.equals(rootProcessId, that.rootProcessId) && Objects.equals(nodeInstanceId, that.nodeInstanceId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(processInstanceId, rootProcessInstanceId, processId, rootProcessId, nodeInstanceId);
        }
    }
}
