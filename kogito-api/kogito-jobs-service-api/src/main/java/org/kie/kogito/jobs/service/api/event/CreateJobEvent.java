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

package org.kie.kogito.jobs.service.api.event;

import org.kie.kogito.jobs.service.api.Job;

public class CreateJobEvent extends JobCloudEvent<Job> {

    public static final String TYPE = "job.create";

    public CreateJobEvent() {
        // Marshalling constructor.
        setType(TYPE);
    }

    @Override
    public void setType(String type) {
        assertExpectedType(type, TYPE);
        super.setType(type);
    }

    @Override
    public String toString() {
        return "CreateJobEvent{} " + super.toString();
    }

    public static Builder builder() {
        return new Builder(new CreateJobEvent());
    }

    public static class Builder extends AbstractJobCloudEventBuilder<Builder, Job, CreateJobEvent> {

        private Builder(CreateJobEvent current) {
            super(current);
        }

        public Builder job(Job job) {
            event.setData(job);
            return cast();
        }
    }
}
