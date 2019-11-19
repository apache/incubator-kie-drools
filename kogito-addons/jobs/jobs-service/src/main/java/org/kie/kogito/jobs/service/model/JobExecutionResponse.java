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

package org.kie.kogito.jobs.service.model;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.StringJoiner;

public class JobExecutionResponse {

    private String message;
    private String code;
    private ZonedDateTime timestamp;
    private String jobId;

    public JobExecutionResponse() {
    }

    public JobExecutionResponse(String message, String code, ZonedDateTime timestamp, String jobId) {
        this.message = message;
        this.code = code;
        this.timestamp = timestamp;
        this.jobId = jobId;
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public String getJobId() {
        return jobId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JobExecutionResponse)) {
            return false;
        }
        JobExecutionResponse that = (JobExecutionResponse) o;
        return Objects.equals(getMessage(), that.getMessage()) &&
                Objects.equals(getCode(), that.getCode()) &&
                Objects.equals(getTimestamp(), that.getTimestamp()) &&
                Objects.equals(getJobId(), that.getJobId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMessage(), getCode(), getTimestamp(), getJobId());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", JobExecutionResponse.class.getSimpleName() + "[", "]")
                .add("message='" + message + "'")
                .add("code='" + code + "'")
                .add("timestamp=" + timestamp)
                .add("jobId='" + jobId + "'")
                .toString();
    }

    public static JobExecutionResponseBuilder builder(){
        return new JobExecutionResponseBuilder();
    }

    public static class JobExecutionResponseBuilder {

        private String message;
        private String code;
        private ZonedDateTime timestamp;
        private String jobId;

        public JobExecutionResponseBuilder message(String message) {
            this.message = message;
            return this;
        }

        public JobExecutionResponseBuilder code(String code) {
            this.code = code;
            return this;
        }

        public JobExecutionResponseBuilder timestamp(ZonedDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public JobExecutionResponseBuilder now() {
            this.timestamp = ZonedDateTime.now();
            return this;
        }

        public JobExecutionResponseBuilder jobId(String jobId) {
            this.jobId = jobId;
            return this;
        }

        public JobExecutionResponse build() {
            return new JobExecutionResponse(message, code, timestamp, jobId);
        }
    }
}
