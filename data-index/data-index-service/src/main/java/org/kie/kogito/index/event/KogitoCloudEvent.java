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
import java.util.Date;

import javax.json.bind.annotation.JsonbProperty;

import org.kie.kogito.index.model.ProcessInstance;

public class KogitoCloudEvent {

    //Cloud Event attributes
    @JsonbProperty("specversion")
    private String specVersion = "0.3";
    private String type;
    private URI source;
    private String id;
    private Date time;
    private URI schemaURL;
    private String contentType;
    private ProcessInstance data;

    //Extensions
    @JsonbProperty("kogitoProcessinstanceId")
    private String processInstanceId;
    @JsonbProperty("kogitoProcessId")
    private String processId;
    @JsonbProperty("kogitoProcessinstanceState")
    private Integer state;
    @JsonbProperty("kogitoRootProcessinstanceId")
    private String rootProcessInstanceId;
    @JsonbProperty("kogitoRootProcessId")
    private String rootProcessId;
    @JsonbProperty("kogitoParentProcessinstanceId")
    private String parentProcessInstanceId;

    public static Builder builder() {
        return new Builder();
    }

    public String getSpecVersion() {
        return specVersion;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public URI getSource() {
        return source;
    }

    public void setSource(URI source) {
        this.source = source;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public URI getSchemaURL() {
        return schemaURL;
    }

    public void setSchemaURL(URI schemaURL) {
        this.schemaURL = schemaURL;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public ProcessInstance getData() {
        return data;
    }

    public void setData(ProcessInstance data) {
        this.data = data;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getRootProcessInstanceId() {
        return rootProcessInstanceId;
    }

    public void setRootProcessInstanceId(String id) {
        if(id != null && id.trim().isEmpty() == false) {
            this.rootProcessInstanceId = id;
        }
    }

    public String getRootProcessId() {
        return rootProcessId;
    }

    public void setRootProcessId(String id) {
        if(id != null && id.trim().isEmpty() == false) {
            this.rootProcessId = id;
        }
    }

    public void setParentProcessInstanceId(String id) {
        if(id != null && id.trim().isEmpty() == false) {
            this.parentProcessInstanceId = id;
        }
    }

    public String getParentProcessInstanceId() {
        return parentProcessInstanceId;
    }

    @Override
    public String toString() {
        return "KogitoCloudEvent{" +
                "specVersion='" + specVersion + '\'' +
                ", type='" + type + '\'' +
                ", source=" + source +
                ", id='" + id + '\'' +
                ", time=" + time +
                ", schemaURL=" + schemaURL +
                ", contentType='" + contentType + '\'' +
                ", data=" + data +
                ", processInstanceId='" + processInstanceId + '\'' +
                ", processId='" + processId + '\'' +
                ", state=" + state +
                ", rootProcessInstanceId='" + rootProcessInstanceId + '\'' +
                ", rootProcessId='" + rootProcessId + '\'' +
                ", parentProcessInstanceId='" + parentProcessInstanceId + '\'' +
                '}';
    }

    public static final class Builder {

        private KogitoCloudEvent kogitoCloudEvent;

        private Builder() {
            kogitoCloudEvent = new KogitoCloudEvent();
        }

        public Builder type(String type) {
            kogitoCloudEvent.setType(type);
            return this;
        }

        public Builder source(URI source) {
            kogitoCloudEvent.setSource(source);
            return this;
        }

        public Builder id(String id) {
            kogitoCloudEvent.setId(id);
            return this;
        }

        public Builder time(Date time) {
            kogitoCloudEvent.setTime(time);
            return this;
        }

        public Builder schemaURL(URI schemaURL) {
            kogitoCloudEvent.setSchemaURL(schemaURL);
            return this;
        }

        public Builder contentType(String contentType) {
            kogitoCloudEvent.setContentType(contentType);
            return this;
        }

        public Builder data(ProcessInstance data) {
            kogitoCloudEvent.setData(data);
            return this;
        }

        public Builder processInstanceId(String processInstanceId) {
            kogitoCloudEvent.setProcessInstanceId(processInstanceId);
            return this;
        }

        public Builder processId(String processId) {
            kogitoCloudEvent.setProcessId(processId);
            return this;
        }

        public Builder state(Integer state) {
            kogitoCloudEvent.setState(state);
            return this;
        }

        public Builder rootProcessInstanceId(String rootProcessInstanceId) {
            kogitoCloudEvent.setRootProcessInstanceId(rootProcessInstanceId);
            return this;
        }

        public Builder rootProcessId(String rootProcessId) {
            kogitoCloudEvent.setRootProcessId(rootProcessId);
            return this;
        }

        public Builder parentProcessInstanceId(String parentProcessInstanceId) {
            kogitoCloudEvent.setParentProcessInstanceId(parentProcessInstanceId);
            return this;
        }

        public KogitoCloudEvent build() {
            return kogitoCloudEvent;
        }
    }
}
