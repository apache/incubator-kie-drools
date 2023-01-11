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

package org.kie.kogito.jobs.service.api;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import static org.kie.kogito.jobs.service.api.Job.CORRELATION_ID_PROPERTY;
import static org.kie.kogito.jobs.service.api.Job.ID_PROPERTY;
import static org.kie.kogito.jobs.service.api.Job.RECIPIENT_PROPERTY;
import static org.kie.kogito.jobs.service.api.Job.RETRY_PROPERTY;
import static org.kie.kogito.jobs.service.api.Job.SCHEDULE_PROPERTY;
import static org.kie.kogito.jobs.service.api.Job.STATE_PROPERTY;

@Schema(description = "Defines a job that can be managed by the jobs service.",
        requiredProperties = { SCHEDULE_PROPERTY, RETRY_PROPERTY, RECIPIENT_PROPERTY })
@JsonPropertyOrder({ ID_PROPERTY, CORRELATION_ID_PROPERTY, STATE_PROPERTY, SCHEDULE_PROPERTY, RETRY_PROPERTY, RECIPIENT_PROPERTY })
public class Job {

    static final String ID_PROPERTY = "id";
    static final String CORRELATION_ID_PROPERTY = "correlationId";
    static final String STATE_PROPERTY = "state";
    static final String SCHEDULE_PROPERTY = "schedule";
    static final String RETRY_PROPERTY = "retry";
    static final String RECIPIENT_PROPERTY = "recipient";

    @Schema(description = "Available states for a Job.")
    public enum State {
        TBD1,
        TBD2,
        TBD3
    }

    @Schema(description = "The unique identifier of the job in the system, this value is set by the jobs service.")
    private String id;
    @Schema(description = "Logical user provided identifier of the job in the system.")
    private String correlationId;
    private State state;
    private Schedule schedule;
    private Retry retry;
    private Recipient<?> recipient;

    public Job() {
        // Marshalling constructor.
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

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public Retry getRetry() {
        return retry;
    }

    public void setRetry(Retry retry) {
        this.retry = retry;
    }

    public Recipient getRecipient() {
        return recipient;
    }

    public void setRecipient(Recipient recipient) {
        this.recipient = recipient;
    }

    @Override
    public String toString() {
        return "Job{" +
                "id='" + id + '\'' +
                ", correlationId='" + correlationId + '\'' +
                ", state=" + state +
                ", schedule=" + schedule +
                ", retry=" + retry +
                ", recipient=" + recipient +
                '}';
    }

    public static Builder builder() {
        return new Builder(new Job());
    }

    public static class Builder {

        private final Job job;

        private Builder(Job job) {
            this.job = job;
        }

        public Builder id(String id) {
            job.setId(id);
            return this;
        }

        public Builder correlationId(String correlationId) {
            job.setCorrelationId(correlationId);
            return this;
        }

        public Builder state(State state) {
            job.setState(state);
            return this;
        }

        public Builder schedule(Schedule schedule) {
            job.setSchedule(schedule);
            return this;
        }

        public Builder retry(Retry retry) {
            job.setRetry(retry);
            return this;
        }

        public Builder recipient(Recipient<?> recipient) {
            job.setRecipient(recipient);
            return this;
        }

        public Job build() {
            return job;
        }
    }
}
