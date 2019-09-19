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

import javax.json.bind.annotation.JsonbProperty;

import org.kie.kogito.index.model.UserTaskInstance;

public class KogitoUserTaskCloudEvent extends KogitoCloudEvent<UserTaskInstance> {

    @JsonbProperty("kogitoUserTaskinstanceId")
    private String userTaskInstanceId;

    @JsonbProperty("kogitoUserTaskinstanceState")
    private String state;

    public String getUserTaskInstanceId() {
        return userTaskInstanceId;
    }

    public void setUserTaskInstanceId(String userTaskInstanceId) {
        this.userTaskInstanceId = userTaskInstanceId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private KogitoUserTaskCloudEvent event;

        private Builder() {
            event = new KogitoUserTaskCloudEvent();
        }

        public static Builder aKogitoUserTaskCloudEvent() {
            return new Builder();
        }

        public Builder type(String type) {
            event.setType(type);
            return this;
        }

        public Builder source(URI source) {
            event.setSource(source);
            return this;
        }

        public Builder userTaskInstanceId(String userTaskInstanceId) {
            event.setUserTaskInstanceId(userTaskInstanceId);
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

        public Builder state(String state) {
            event.setState(state);
            return this;
        }

        public Builder data(UserTaskInstance data) {
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

        public Builder rootProcessInstanceId(String rootProcessInstanceId) {
            event.setRootProcessInstanceId(rootProcessInstanceId);
            return this;
        }

        public Builder rootProcessId(String rootProcessId) {
            event.setRootProcessId(rootProcessId);
            return this;
        }

        public KogitoUserTaskCloudEvent build() {
            return event;
        }
    }
}
