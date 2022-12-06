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

import org.kie.kogito.jobs.service.api.JobLookupId;

public class DeleteJobEvent extends JobCloudEvent<JobLookupId> {

    public static final String TYPE = "job.delete";

    public DeleteJobEvent() {
        // marshalling constructor.
        setType(TYPE);
    }

    @Override
    public void setType(String type) {
        assertExpectedType(type, TYPE);
        super.setType(type);
    }

    public static Builder builder() {
        return new Builder(new DeleteJobEvent());
    }

    public static class Builder extends AbstractJobCloudEventBuilder<Builder, JobLookupId, DeleteJobEvent> {

        private Builder(DeleteJobEvent event) {
            super(event);
        }

        public Builder lookupId(JobLookupId lookupId) {
            event.setData(lookupId);
            return this;
        }
    }
}
