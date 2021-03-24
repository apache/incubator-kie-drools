/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import org.kie.kogito.index.model.UserTaskInstance;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.net.UrlEscapers;

import static java.lang.String.format;

public class KogitoUserTaskCloudEvent extends KogitoCloudEvent<UserTaskInstance> {

    @JsonProperty("kogitoUserTaskinstanceId")
    private String userTaskInstanceId;

    @JsonProperty("kogitoUserTaskinstanceState")
    private String state;

    public static Builder builder() {
        return new Builder();
    }

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

    @Override
    public void setTime(ZonedDateTime time) {
        super.setTime(time);
        if (getData() != null && time != null) {
            getData().setLastUpdate(time);
        }
    }

    @Override
    public void setData(UserTaskInstance data) {
        super.setData(data);
        setTime(getTime());
        setSource(getSource());
    }

    @Override
    public void setSource(URI source) {
        super.setSource(source);
        if (getData() != null && source != null) {
            getData().setEndpoint(getEndpoint(source, getData().getProcessInstanceId(), getData().getName(), getData().getId()));
        }
    }

    protected String getEndpoint(URI source, String pId, String taskName, String taskId) {
        String name = UrlEscapers.urlPathSegmentEscaper().escape(taskName);
        return source.toString() + format("/%s/%s/%s", pId, name, taskId);
    }

    @Override
    public String toString() {
        return "KogitoUserTaskCloudEvent{" +
                "userTaskInstanceId='" + userTaskInstanceId + '\'' +
                ", state='" + state + '\'' +
                "} " + super.toString();
    }

    public static final class Builder extends AbstractBuilder<Builder, UserTaskInstance, KogitoUserTaskCloudEvent> {

        private Builder() {
            super(new KogitoUserTaskCloudEvent());
        }

        public Builder userTaskInstanceId(String userTaskInstanceId) {
            event.setUserTaskInstanceId(userTaskInstanceId);
            return this;
        }

        public Builder state(String state) {
            event.setState(state);
            return this;
        }
    }
}
