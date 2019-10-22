/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.index.event;

import java.net.URI;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.kie.kogito.index.model.ProcessInstance;

public class KogitoProcessCloudEvent extends KogitoCloudEvent<ProcessInstance> {

    @JsonProperty("kogitoProcessinstanceState")
    private Integer state;
    @JsonProperty("kogitoParentProcessinstanceId")
    private String parentProcessInstanceId;

    public static Builder builder() {
        return new Builder();
    }

    public String getParentProcessInstanceId() {
        return parentProcessInstanceId;
    }

    public void setParentProcessInstanceId(String id) {
        if (id != null && id.trim().isEmpty() == false) {
            this.parentProcessInstanceId = id;
        }
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    @Override
    public void setSource(URI source) {
        super.setSource(source);
        if (getData() != null && source != null) {
            getData().setEndpoint(source.toString());
        }
    }

    @Override
    public String toString() {
        return "KogitoProcessCloudEvent{" +
               "state=" + state +
               ", parentProcessInstanceId='" + parentProcessInstanceId + '\'' +
               "} " + super.toString();
    }

    public static final class Builder {

        private KogitoProcessCloudEvent event;

        private Builder() {
            event = new KogitoProcessCloudEvent();
        }

        public Builder type(String type) {
            event.setType(type);
            return this;
        }

        public Builder source(URI source) {
            event.setSource(source);
            return this;
        }

        public Builder id(String id) {
            event.setId(id);
            return this;
        }

        public Builder time(ZonedDateTime time) {
            event.setTime(time);
            return this;
        }

        public Builder schemaURL(URI schemaURL) {
            event.setSchemaURL(schemaURL);
            return this;
        }

        public Builder contentType(String contentType) {
            event.setContentType(contentType);
            return this;
        }

        public Builder data(ProcessInstance data) {
            event.setData(data);
            return this;
        }

        public Builder processInstanceId(String processInstanceId) {
            event.setProcessInstanceId(processInstanceId);
            return this;
        }

        public Builder processId(String processId) {
            event.setProcessId(processId);
            return this;
        }

        public Builder state(Integer state) {
            event.setState(state);
            return this;
        }

        public Builder rootProcessInstanceId(String rootProcessInstanceId) {
            event.setRootProcessInstanceId(rootProcessInstanceId);
            return this;
        }

        public Builder rootProcessId(String rootProcessId) {
            event.setRootProcessId(rootProcessId);
            return this;
        }

        public Builder parentProcessInstanceId(String parentProcessInstanceId) {
            event.setParentProcessInstanceId(parentProcessInstanceId);
            return this;
        }

        public Builder kogitoReferenceId(String kogitoReferenceId) {
            event.setKogitoReferenceId(kogitoReferenceId);
            return this;
        }

        public KogitoProcessCloudEvent build() {
            return event;
        }
    }
}
