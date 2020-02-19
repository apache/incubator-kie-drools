/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.jobs.service.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import org.kie.kogito.event.AbstractDataEvent;
import org.kie.kogito.jobs.service.model.ScheduledJob;

/**
 * <a href="https://cloudevents.io">CloudEvent</a> to propagate job status information from Job Service.
 */
public class JobDataEvent extends AbstractDataEvent<ScheduledJob> {

    public static final String JOB_EVENT_TYPE = "JobEvent";

    public JobDataEvent(String source, ScheduledJob data) {
        super(JOB_EVENT_TYPE,
              source,
              data,
              data.getProcessInstanceId(),
              data.getRootProcessInstanceId(),
              data.getProcessId(),
              data.getRootProcessId(),
              null);
    }

    @JsonIgnore
    public static JobDataEventBuilder builder() {
        return new JobDataEventBuilder();
    }

    @JsonIgnoreType
    public static class JobDataEventBuilder {

        private String source;
        private ScheduledJob data;

        public JobDataEventBuilder source(String source) {
            this.source = source;
            return this;
        }

        public JobDataEventBuilder data(ScheduledJob data) {
            this.data = data;
            return this;
        }

        public JobDataEvent build() {
            return new JobDataEvent(source, data);
        }
    }
}
