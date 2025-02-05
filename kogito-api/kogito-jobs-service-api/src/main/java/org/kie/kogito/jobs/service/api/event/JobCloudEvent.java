/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.jobs.service.api.event;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Set;

import org.kie.kogito.jobs.service.api.serialization.SpecVersionDeserializer;
import org.kie.kogito.jobs.service.api.serialization.SpecVersionSerializer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.cloudevents.CloudEventAttributes;
import io.cloudevents.SpecVersion;

import static io.cloudevents.core.v1.CloudEventV1.DATACONTENTTYPE;
import static io.cloudevents.core.v1.CloudEventV1.DATASCHEMA;
import static io.cloudevents.core.v1.CloudEventV1.ID;
import static io.cloudevents.core.v1.CloudEventV1.SOURCE;
import static io.cloudevents.core.v1.CloudEventV1.SPECVERSION;
import static io.cloudevents.core.v1.CloudEventV1.SUBJECT;
import static io.cloudevents.core.v1.CloudEventV1.TIME;
import static io.cloudevents.core.v1.CloudEventV1.TYPE;

@JsonPropertyOrder(value = { ID, SOURCE, TYPE, TIME, SUBJECT, SPECVERSION, DATACONTENTTYPE, DATASCHEMA })
public abstract class JobCloudEvent<T> implements CloudEventAttributes {

    public static final SpecVersion SPEC_VERSION = SpecVersion.V1;

    @JsonDeserialize(using = SpecVersionDeserializer.class)
    @JsonSerialize(using = SpecVersionSerializer.class)
    @JsonProperty(SPECVERSION)
    private SpecVersion specVersion = SPEC_VERSION;
    private String id;
    private URI source;
    private String type;
    private OffsetDateTime time;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String subject;
    @JsonProperty(DATACONTENTTYPE)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String dataContentType;
    @JsonProperty(DATASCHEMA)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private URI dataSchema;

    private T data;

    protected JobCloudEvent() {
        // Marshalling constructor.
    }

    @Override
    public URI getSource() {
        return source;
    }

    @Override
    public SpecVersion getSpecVersion() {
        return specVersion;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public OffsetDateTime getTime() {
        return time;
    }

    public T getData() {
        return data;
    }

    @Override
    public String getDataContentType() {
        return dataContentType;
    }

    @Override
    public URI getDataSchema() {
        return dataSchema;
    }

    @Override
    public String getSubject() {
        return subject;
    }

    @JsonIgnore
    @Override
    public Object getAttribute(String attributeName) throws IllegalArgumentException {
        switch (attributeName) {
            case SPECVERSION:
                return this.specVersion;
            case ID:
                return this.id;
            case SOURCE:
                return this.source;
            case TYPE:
                return this.type;
            case DATACONTENTTYPE:
                return this.dataContentType;
            case DATASCHEMA:
                return this.dataSchema;
            case SUBJECT:
                return this.subject;
            case TIME:
                return this.time;
            default:
                throw new IllegalArgumentException("Spec version v1 doesn't have attribute named " + attributeName);
        }
    }

    @JsonIgnore
    @Override
    public Set<String> getAttributeNames() {
        return CloudEventAttributes.super.getAttributeNames();
    }

    public void setSpecVersion(SpecVersion specVersion) {
        this.specVersion = specVersion;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSource(URI source) {
        this.source = source;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTime(OffsetDateTime time) {
        this.time = time;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setDataContentType(String dataContentType) {
        this.dataContentType = dataContentType;
    }

    public void setDataSchema(URI dataSchema) {
        this.dataSchema = dataSchema;
    }

    public void setData(T data) {
        this.data = data;
    }

    protected void assertExpectedType(String currentType, String expectedType) {
        if (!Objects.equals(currentType, expectedType)) {
            throw new IllegalArgumentException(getClass().getName() + " don't support other event type than: "
                    + expectedType + ", please don't use this value: " + currentType);
        }
    }

    @Override
    public String toString() {
        return "JobCloudEvent{" +
                "specVersion=" + specVersion +
                ", id='" + id + '\'' +
                ", source=" + source +
                ", type='" + type + '\'' +
                ", time=" + time +
                ", subject='" + subject + '\'' +
                ", dataContentType='" + dataContentType + '\'' +
                ", dataSchema=" + dataSchema +
                ", data=" + data +
                '}';
    }
}
