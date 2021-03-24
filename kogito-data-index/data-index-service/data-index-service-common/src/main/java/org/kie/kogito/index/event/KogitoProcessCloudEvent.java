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
import java.util.HashSet;

import org.kie.kogito.index.model.ProcessInstance;

import com.fasterxml.jackson.annotation.JsonProperty;

import static java.util.Arrays.asList;

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
    public void setKogitoAddons(String kogitoAddons) {
        super.setKogitoAddons(kogitoAddons);
        if (getData() != null && kogitoAddons != null) {
            getData().setAddons(new HashSet<>(asList(kogitoAddons.split(","))));
        }
    }

    @Override
    public void setTime(ZonedDateTime time) {
        super.setTime(time);
        if (getData() != null && time != null) {
            getData().setLastUpdate(time);
        }
    }

    @Override
    public void setData(ProcessInstance data) {
        super.setData(data);
        setTime(getTime());
        setKogitoAddons(getKogitoAddons());
        setSource(getSource());
    }

    @Override
    public String toString() {
        return "KogitoProcessCloudEvent{" +
                "state=" + state +
                ", parentProcessInstanceId='" + parentProcessInstanceId + '\'' +
                "} " + super.toString();
    }

    public static final class Builder extends AbstractBuilder<Builder, ProcessInstance, KogitoProcessCloudEvent> {

        private Builder() {
            super(new KogitoProcessCloudEvent());
        }

        public Builder state(Integer state) {
            event.setState(state);
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

        public Builder kogitoAddons(String kogitoAddons) {
            event.setKogitoAddons(kogitoAddons);
            return this;
        }
    }
}
