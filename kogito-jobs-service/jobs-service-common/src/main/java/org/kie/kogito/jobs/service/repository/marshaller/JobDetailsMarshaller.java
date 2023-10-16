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
package org.kie.kogito.jobs.service.repository.marshaller;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.JobStatus;

import io.quarkus.arc.DefaultBean;
import io.vertx.core.json.JsonObject;

import static org.kie.kogito.jobs.service.utils.DateUtil.DEFAULT_ZONE;

@DefaultBean
@ApplicationScoped
public class JobDetailsMarshaller implements Marshaller<JobDetails, JsonObject> {

    RecipientMarshaller recipientMarshaller;

    TriggerMarshaller triggerMarshaller;

    public JobDetailsMarshaller() {
    }

    @Inject
    public JobDetailsMarshaller(TriggerMarshaller triggerMarshaller, RecipientMarshaller recipientMarshaller) {
        this.recipientMarshaller = recipientMarshaller;
        this.triggerMarshaller = triggerMarshaller;
    }

    @Override
    public JsonObject marshall(JobDetails jobDetails) {
        if (jobDetails != null) {
            return JsonObject.mapFrom(new JobDetailsAccessor(jobDetails, recipientMarshaller, triggerMarshaller));
        }
        return null;
    }

    @Override
    public JobDetails unmarshall(JsonObject jsonObject) {
        if (jsonObject != null) {
            return jsonObject.mapTo(JobDetailsAccessor.class).to(recipientMarshaller, triggerMarshaller);
        }
        return null;
    }

    private static class JobDetailsAccessor {

        private String id;
        private String correlationId;
        private String status;
        private Date lastUpdate;
        private Integer retries;
        private Integer priority;
        private Integer executionCounter;
        private String scheduledId;
        private Map<String, Object> recipient;
        private Map<String, Object> trigger;
        private Long executionTimeout;
        private String executionTimeoutUnit;

        public JobDetailsAccessor() {
        }

        public JobDetailsAccessor(JobDetails jobDetails, RecipientMarshaller recipientMarshaller, TriggerMarshaller triggerMarshaller) {
            this.id = jobDetails.getId();
            this.correlationId = jobDetails.getCorrelationId();
            this.status = Optional.ofNullable(jobDetails.getStatus()).map(Enum::name).orElse(null);
            this.lastUpdate = Optional.ofNullable(jobDetails.getLastUpdate()).map(u -> Date.from(u.toInstant())).orElse(null);
            this.retries = jobDetails.getRetries();
            this.priority = jobDetails.getPriority();
            this.executionCounter = jobDetails.getExecutionCounter();
            this.scheduledId = jobDetails.getScheduledId();
            this.recipient = Optional.ofNullable(jobDetails.getRecipient()).map(r -> recipientMarshaller.marshall(r).getMap()).orElse(null);
            this.trigger = Optional.ofNullable(jobDetails.getTrigger()).map(t -> triggerMarshaller.marshall(t).getMap()).orElse(null);
            this.executionTimeout = jobDetails.getExecutionTimeout();
            this.executionTimeoutUnit = Optional.ofNullable(jobDetails.getExecutionTimeoutUnit()).map(Enum::name).orElse(null);
        }

        public JobDetails to(RecipientMarshaller recipientMarshaller, TriggerMarshaller triggerMarshaller) {
            return JobDetails.builder()
                    .id(this.id)
                    .correlationId(this.correlationId)
                    .status(Optional.ofNullable(this.status).map(JobStatus::valueOf).orElse(null))
                    .lastUpdate(Optional.ofNullable(this.lastUpdate).map(t -> ZonedDateTime.ofInstant(t.toInstant(), DEFAULT_ZONE)).orElse(null))
                    .retries(this.retries)
                    .executionCounter(this.executionCounter)
                    .scheduledId(this.scheduledId)
                    .priority(this.priority)
                    .recipient(Optional.ofNullable(this.recipient).map(r -> recipientMarshaller.unmarshall(new JsonObject(r))).orElse(null))
                    .trigger(Optional.ofNullable(this.trigger).map(t -> triggerMarshaller.unmarshall(new JsonObject(t))).orElse(null))
                    .executionTimeout(this.executionTimeout)
                    .executionTimeoutUnit(Optional.ofNullable(this.executionTimeoutUnit).map(ChronoUnit::valueOf).orElse(null))
                    .build();
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCorrelationId() {
            return correlationId;
        }

        public void setCorrelationId(String correlationId) {
            this.correlationId = correlationId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Date getLastUpdate() {
            return lastUpdate;
        }

        public void setLastUpdate(Date lastUpdate) {
            this.lastUpdate = lastUpdate;
        }

        public Integer getRetries() {
            return retries;
        }

        public void setRetries(Integer retries) {
            this.retries = retries;
        }

        public Integer getPriority() {
            return priority;
        }

        public void setPriority(Integer priority) {
            this.priority = priority;
        }

        public Integer getExecutionCounter() {
            return executionCounter;
        }

        public void setExecutionCounter(Integer executionCounter) {
            this.executionCounter = executionCounter;
        }

        public String getScheduledId() {
            return scheduledId;
        }

        public void setScheduledId(String scheduledId) {
            this.scheduledId = scheduledId;
        }

        public Map<String, Object> getRecipient() {
            return recipient;
        }

        public void setRecipient(Map<String, Object> recipient) {
            this.recipient = recipient;
        }

        public Map<String, Object> getTrigger() {
            return trigger;
        }

        public void setTrigger(Map<String, Object> trigger) {
            this.trigger = trigger;
        }

        public Long getExecutionTimeout() {
            return executionTimeout;
        }

        public void setExecutionTimeout(Long executionTimeout) {
            this.executionTimeout = executionTimeout;
        }

        public String getExecutionTimeoutUnit() {
            return executionTimeoutUnit;
        }

        public void setExecutionTimeoutUnit(String executionTimeoutUnit) {
            this.executionTimeoutUnit = executionTimeoutUnit;
        }
    }
}
