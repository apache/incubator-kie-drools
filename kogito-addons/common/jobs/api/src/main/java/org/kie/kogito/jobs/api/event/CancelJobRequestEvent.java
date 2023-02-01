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

@Deprecated
public class CancelJobRequestEvent extends ProcessInstanceContextJobCloudEvent<CancelJobRequestEvent.JobId> {

    public static final String CANCEL_JOB_REQUEST = "CancelJobRequest";

    public CancelJobRequestEvent() {
        // marshalling constructor.
        setType(CANCEL_JOB_REQUEST);
    }

    @Override
    public void setType(String type) {
        assertExpectedType(type, CANCEL_JOB_REQUEST);
        super.setType(type);
    }

    public static class JobId {

        private String id;

        public JobId() {
            // marshalling constructor.
        }

        public JobId(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    public static Builder builder() {
        return new Builder(new CancelJobRequestEvent());
    }

    @Override
    public String toString() {
        return "CancelJobRequestEvent{} " + super.toString();
    }

    @Deprecated
    public static class Builder extends AbstractProcessInstanceContextJobCloudEventBuilder<Builder, JobId, CancelJobRequestEvent> {

        private Builder(CancelJobRequestEvent event) {
            super(event);
        }

        public Builder jobId(String jobId) {
            event.setData(new CancelJobRequestEvent.JobId(jobId));
            return this;
        }
    }
}