/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.jobs.api.event;

import org.kie.kogito.jobs.api.Job;

@Deprecated
public class CreateProcessInstanceJobRequestEvent extends ProcessInstanceContextJobCloudEvent<Job> {

    public static final String CREATE_PROCESS_INSTANCE_JOB_REQUEST = "CreateProcessInstanceJobRequest";

    public CreateProcessInstanceJobRequestEvent() {
        // marshalling constructor.
        setType(CREATE_PROCESS_INSTANCE_JOB_REQUEST);
    }

    @Override
    public void setType(String type) {
        assertExpectedType(type, CREATE_PROCESS_INSTANCE_JOB_REQUEST);
        super.setType(type);
    }

    public static Builder builder() {
        return new Builder(new CreateProcessInstanceJobRequestEvent());
    }

    @Override
    public String toString() {
        return "CreateProcessInstanceJobRequestEvent{} " + super.toString();
    }

    @Deprecated
    public static class Builder extends AbstractProcessInstanceContextJobCloudEventBuilder<Builder, Job, CreateProcessInstanceJobRequestEvent> {

        private Builder(CreateProcessInstanceJobRequestEvent current) {
            super(current);
        }

        public Builder job(Job job) {
            event.setData(job);
            return cast();
        }
    }

}
