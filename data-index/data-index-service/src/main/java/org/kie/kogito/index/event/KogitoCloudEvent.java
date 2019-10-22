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

public abstract class KogitoCloudEvent<T> {

    //Cloud Event attributes
    @JsonProperty("specversion")
    private String specVersion = "0.3";
    private String type;
    private URI source;
    private String id;
    private ZonedDateTime time;
    private URI schemaURL;
    private String contentType;
    private T data;

    //Extensions
    @JsonProperty("kogitoProcessinstanceId")
    private String processInstanceId;
    @JsonProperty("kogitoProcessId")
    private String processId;
    @JsonProperty("kogitoRootProcessinstanceId")
    private String rootProcessInstanceId;
    @JsonProperty("kogitoRootProcessId")
    private String rootProcessId;    
    private String kogitoReferenceId;

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

    public ZonedDateTime getTime() {
        return time;
    }

    public void setTime(ZonedDateTime time) {
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String id) {
        if (id != null && !id.trim().isEmpty()) {
            this.processInstanceId = id;
        }
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getRootProcessInstanceId() {
        return rootProcessInstanceId;
    }

    public void setRootProcessInstanceId(String id) {
        if (id != null && !id.trim().isEmpty()) {
            this.rootProcessInstanceId = id;
        }
    }

    public String getRootProcessId() {
        return rootProcessId;
    }

    public void setRootProcessId(String id) {
        if (id != null && !id.trim().isEmpty()) {
            this.rootProcessId = id;
        }
    }
    
    public String getKogitoReferenceId() {
        return kogitoReferenceId;
    }
    
    public void setKogitoReferenceId(String kogitoReferenceId) {
        this.kogitoReferenceId = kogitoReferenceId;
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
                ", rootProcessInstanceId='" + rootProcessInstanceId + '\'' +
                ", rootProcessId='" + rootProcessId + '\'' +
                '}';
    }
}
