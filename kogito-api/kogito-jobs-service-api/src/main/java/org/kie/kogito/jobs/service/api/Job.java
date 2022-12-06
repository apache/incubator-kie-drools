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

@Schema(description = "Defines a job that can be managed by the jobs service.")
public class Job {

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
    @Schema(description = "The job state, this value is set and managed by the jobs service.")
    private State state;
    @Schema(description = "This value represents the job triggering periodicity.", required = true)
    private Schedule schedule;
    @Schema(description = "This value establishes the retries configuration in cases where job execution fails.")
    private Retry retry;
    @Schema(description = "This value represents the entity that is called on the job execution, for example, an http resource, a kafka broker, or a knative sink, etc.", required = true)
    private Recipient<?> recipient;

    public Job() {
        // marshalling constructor.
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

    public Recipient<?> getRecipient() {
        return recipient;
    }

    public void setRecipient(Recipient<?> recipient) {
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
