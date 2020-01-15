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

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.jobs.service.model.ScheduledJob;

/**
 * <a href="https://cloudevents.io">CloudEvent</a> to propagate job status information from Job Service.
 */
public class JobDataEvent implements DataEvent<ScheduledJob> {

    public static final String JOB_EVENT_TYPE = "JobEvent";
    public static final String DEFAULT_SPEC_VERSION = "0.3";
    private String specversion;
    private String id;
    private String source;
    private ZonedDateTime time;
    private ScheduledJob data;

    public JobDataEvent() {

    }

    public JobDataEvent(String specversion, String id, String source, ZonedDateTime time, ScheduledJob data) {
        this.specversion = Optional.ofNullable(specversion).orElse(DEFAULT_SPEC_VERSION);
        this.id = id;
        this.source = source;
        this.time = time;
        this.data = data;
    }

    @Override
    public String getSpecversion() {
        return specversion;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getType() {
        return JOB_EVENT_TYPE;
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public String getTime() {
        return time.format(DateTimeFormatter.ISO_INSTANT);
    }

    @Override
    public ScheduledJob getData() {
        return data;
    }

    @JsonIgnore
    public static JobDataEventBuilder builder() {
        return new JobDataEventBuilder();
    }

    @JsonIgnoreType
    public static class JobDataEventBuilder {

        private String specversion;
        private String id;
        private String source;
        private ZonedDateTime time;
        private ScheduledJob data;

        public JobDataEventBuilder specversion(String specversion) {
            this.specversion = specversion;
            return this;
        }

        public JobDataEventBuilder id(String id) {
            this.id = id;
            return this;
        }

        public JobDataEventBuilder source(String source) {
            this.source = source;
            return this;
        }

        public JobDataEventBuilder time(ZonedDateTime time) {
            this.time = time;
            return this;
        }

        public JobDataEventBuilder data(ScheduledJob data) {
            this.data = data;
            return this;
        }

        public JobDataEvent build() {
            return new JobDataEvent(specversion, id, source, time, data);
        }
    }
}
