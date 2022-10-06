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
package org.kie.kogito.event;

import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.kie.kogito.event.cloudevents.CloudEventExtensionConstants;
import org.kie.kogito.event.cloudevents.SpecVersionDeserializer;
import org.kie.kogito.event.cloudevents.SpecVersionSerializer;
import org.kie.kogito.event.cloudevents.utils.CloudEventUtils;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.cloudevents.CloudEvent;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.builder.CloudEventBuilder;

/**
 * This is an abstract implementation of the {@link DataEvent} that contains basic common attributes referring to
 * kogito processes metadata. This class is intended to be used only for applications consuming events published
 * by Kogito that wants to deserialize the cloud event in one shot.
 *
 * @param <T> the payload
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractDataEvent<T> implements DataEvent<T> {

    /**
     * String prefix for Kogito CloudEvents type fields.
     * Since this is a required field, the constructor will fill them with this default value.
     * Ideally, callers would use #TYPE_FORMAT to fill this field using the process name and the signal node name, e.g: process.travelagency.visaapproved
     */
    public static final String TYPE_PREFIX = "process";
    public static final String TYPE_FORMAT = TYPE_PREFIX + ".%s.%s";
    /**
     * String format for Kogito CloudEvents source fields.
     * Since this is a required field, the constructor will fill them with default value, e.g.: /process/travelagency
     * See more about the source format: https://github.com/cloudevents/spec/blob/v1.0/spec.md#source-1
     */
    public static final String SOURCE_FORMAT = "/process/%s";
    public static final String SPEC_VERSION = "1.0";
    public static final String DATA_CONTENT_TYPE = "application/json";

    @JsonDeserialize(using = SpecVersionDeserializer.class)
    @JsonSerialize(using = SpecVersionSerializer.class)
    @JsonProperty("specversion")
    private SpecVersion specVersion;

    private String id;

    private URI source;

    private String type;

    private OffsetDateTime time;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String subject;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("datacontenttype")
    private String dataContentType;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private URI dataSchema;

    private T data;

    @JsonProperty(CloudEventExtensionConstants.PROCESS_INSTANCE_ID)
    private String kogitoProcessInstanceId;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty(CloudEventExtensionConstants.PROCESS_ROOT_PROCESS_INSTANCE_ID)
    private String kogitoRootProcessInstanceId;

    @JsonProperty(CloudEventExtensionConstants.PROCESS_ID)
    private String kogitoProcessId;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty(CloudEventExtensionConstants.PROCESS_ROOT_PROCESS_ID)
    private String kogitoRootProcessId;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty(CloudEventExtensionConstants.ADDONS)
    private String kogitoAddons;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty(CloudEventExtensionConstants.PROCESS_INSTANCE_VERSION)
    protected String kogitoProcessInstanceVersion;

    @JsonProperty(CloudEventExtensionConstants.PROCESS_PARENT_PROCESS_INSTANCE_ID)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected String kogitoParentProcessInstanceId;

    @JsonProperty(CloudEventExtensionConstants.PROCESS_USER_TASK_INSTANCE_STATE)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected String kogitoProcessInstanceState;

    @JsonProperty(CloudEventExtensionConstants.PROCESS_REFERENCE_ID)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected String kogitoReferenceId;

    @JsonProperty(CloudEventExtensionConstants.PROCESS_START_FROM_NODE)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected String kogitoStartFromNode;

    @JsonProperty(CloudEventExtensionConstants.BUSINESS_KEY)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected String kogitoBusinessKey;

    @JsonProperty(CloudEventExtensionConstants.PROCESS_TYPE)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected String kogitoProcessType;

    private Map<String, Object> extensionAttributes = new HashMap<>();

    public AbstractDataEvent() {
    }

    protected AbstractDataEvent(String type,
            String source,
            T body,
            String kogitoProcessInstanceId,
            String kogitoRootProcessInstanceId,
            String kogitoProcessId,
            String kogitoRootProcessId,
            String kogitoAddons) {
        this(type, source, body, kogitoProcessInstanceId, kogitoRootProcessInstanceId, kogitoProcessId, kogitoRootProcessId, kogitoAddons, null, DATA_CONTENT_TYPE, null);
    }

    protected AbstractDataEvent(String type,
            String source,
            T body,
            String kogitoProcessInstanceId,
            String kogitoRootProcessInstanceId,
            String kogitoProcessId,
            String kogitoRootProcessId,
            String kogitoAddons,
            String subject,
            String dataContentType,
            String dataSchema) {
        this.specVersion = SpecVersion.parse(SPEC_VERSION);
        this.id = UUID.randomUUID().toString();
        this.source = Optional.ofNullable(source).map(URI::create).orElse(null);
        this.type = type;
        this.time = ZonedDateTime.now().toOffsetDateTime();
        this.data = body;
        this.kogitoProcessInstanceId = kogitoProcessInstanceId;
        this.kogitoRootProcessInstanceId = kogitoRootProcessInstanceId;
        this.kogitoProcessId = kogitoProcessId;
        this.kogitoRootProcessId = kogitoRootProcessId;
        this.kogitoAddons = kogitoAddons;
        this.subject = subject;
        this.dataContentType = dataContentType;
        this.dataSchema = dataSchema != null ? URI.create(dataSchema) : null;
        ensureRequiredFields();
    }

    protected void ensureRequiredFields() {
        if (this.type == null || this.type.isEmpty()) {
            this.type = TYPE_PREFIX;
        }
        if (this.source == null || this.source.toString().isEmpty()) {
            this.source = URI.create(String.format(SOURCE_FORMAT, kogitoProcessId));
        }
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

    @Override
    public T getData() {
        return data;
    }

    @Override
    public URI getDataSchema() {
        return dataSchema;
    }

    @Override
    public String getSubject() {
        return subject;
    }

    @Override
    public String getDataContentType() {
        return dataContentType;
    }

    @Override
    public String getKogitoProcessInstanceId() {
        return kogitoProcessInstanceId;
    }

    @Override
    public String getKogitoRootProcessInstanceId() {
        return kogitoRootProcessInstanceId;
    }

    @Override
    public String getKogitoProcessId() {
        return kogitoProcessId;
    }

    @Override
    public String getKogitoRootProcessId() {
        return kogitoRootProcessId;
    }

    @Override
    public String getKogitoAddons() {
        return kogitoAddons;
    }

    @Override
    public Object getAttribute(String name) throws IllegalArgumentException {
        return CloudEventUtils.getAttribute(name, this);
    }

    @Override
    public Object getExtension(String name) {
        return extensionAttributes.get(name);
    }

    @JsonIgnore
    @Override
    public Set<String> getAttributeNames() {
        return DataEvent.super.getAttributeNames();
    }

    @JsonIgnore
    @Override
    public Set<String> getExtensionNames() {
        return extensionAttributes.keySet();
    }

    @JsonAnyGetter
    @JsonIgnore
    public Map<String, Object> getExtensionAttributes() {
        return extensionAttributes;
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

    public void setKogitoProcessInstanceId(String kogitoProcessInstanceId) {
        this.kogitoProcessInstanceId = kogitoProcessInstanceId;
    }

    public void setKogitoRootProcessInstanceId(String kogitoRootProcessInstanceId) {
        this.kogitoRootProcessInstanceId = kogitoRootProcessInstanceId;
    }

    public void setKogitoProcessId(String kogitoProcessId) {
        this.kogitoProcessId = kogitoProcessId;
    }

    public void setKogitoRootProcessId(String kogitoRootProcessId) {
        this.kogitoRootProcessId = kogitoRootProcessId;
    }

    public void setKogitoAddons(String kogitoAddons) {
        this.kogitoAddons = kogitoAddons;
    }

    public void setExtensionAttributes(Map<String, Object> extensionAttributes) {
        this.extensionAttributes = extensionAttributes;
    }

    @Override
    public String getKogitoParentProcessInstanceId() {
        return kogitoParentProcessInstanceId;
    }

    @Override
    public String getKogitoProcessInstanceState() {
        return kogitoProcessInstanceState;
    }

    @Override
    public String getKogitoReferenceId() {
        return this.kogitoReferenceId;
    }

    @Override
    public String getKogitoBusinessKey() {
        return this.kogitoBusinessKey;
    }

    public void setKogitoStartFromNode(String kogitoStartFromNode) {
        this.kogitoStartFromNode = kogitoStartFromNode;
    }

    @Override
    public String getKogitoStartFromNode() {
        return this.kogitoStartFromNode;
    }

    @Override
    public String getKogitoProcessInstanceVersion() {
        return kogitoProcessInstanceVersion;
    }

    @Override
    public String getKogitoProcessType() {
        return kogitoProcessType;
    }

    public void setKogitoProcessInstanceVersion(String kogitoProcessInstanceVersion) {
        this.kogitoProcessInstanceVersion = kogitoProcessInstanceVersion;
    }

    public void setKogitoParentProcessInstanceId(String kogitoParentProcessInstanceId) {
        this.kogitoParentProcessInstanceId = kogitoParentProcessInstanceId;
    }

    public void setKogitoProcessInstanceState(String kogitoProcessInstanceState) {
        this.kogitoProcessInstanceState = kogitoProcessInstanceState;
    }

    public void setKogitoReferenceId(String kogitoReferenceId) {
        this.kogitoReferenceId = kogitoReferenceId;
    }

    public void setKogitoBusinessKey(String kogitoBusinessKey) {
        this.kogitoBusinessKey = kogitoBusinessKey;
    }

    public void setKogitoProcessType(String kogitoProcessType) {
        this.kogitoProcessType = kogitoProcessType;
    }

    @JsonAnySetter
    public void addExtensionAttribute(String name, Object value) {
        switch (name) {
            case CloudEventExtensionConstants.PROCESS_INSTANCE_ID:
                kogitoProcessInstanceId = (String) value;
                break;
            case CloudEventExtensionConstants.PROCESS_ROOT_PROCESS_ID:
                kogitoRootProcessId = (String) value;
                break;
            case CloudEventExtensionConstants.PROCESS_ROOT_PROCESS_INSTANCE_ID:
                kogitoRootProcessInstanceId = (String) value;
                break;
            case CloudEventExtensionConstants.ADDONS:
                kogitoAddons = (String) value;
                break;
            case CloudEventExtensionConstants.PROCESS_ID:
                kogitoProcessId = (String) value;
                break;
            case CloudEventExtensionConstants.PROCESS_REFERENCE_ID:
                this.kogitoReferenceId = (String) value;
                break;
            case CloudEventExtensionConstants.PROCESS_INSTANCE_VERSION:
                this.kogitoProcessInstanceVersion = (String) value;
                break;
            case CloudEventExtensionConstants.PROCESS_PARENT_PROCESS_INSTANCE_ID:
                this.kogitoParentProcessInstanceId = (String) value;
                break;
            case CloudEventExtensionConstants.PROCESS_USER_TASK_INSTANCE_ID:
                this.kogitoProcessInstanceState = (String) value;
                break;
            case CloudEventExtensionConstants.PROCESS_START_FROM_NODE:
                this.kogitoStartFromNode = (String) value;
                break;
            case CloudEventExtensionConstants.PROCESS_TYPE:
                this.kogitoProcessType = (String) value;
                break;
            case CloudEventExtensionConstants.BUSINESS_KEY:
                this.kogitoBusinessKey = (String) value;
                break;
        }
        extensionAttributes.put(name, value);
    }

    @Override
    public CloudEvent asCloudEvent() {
        CloudEventBuilder builder = CloudEventBuilder.fromSpecVersion(specVersion).withSource(source).withType(type).withId(id).withSubject(subject).withTime(time)
                .withDataContentType(dataContentType).withDataSchema(dataSchema);
        builder.withData(CloudEventUtils.fromObject(data, ObjectMapperFactory.get()::writeValueAsBytes));
        extensionAttributes.forEach((k, v) -> CloudEventUtils.withExtension(builder, k, v));
        return builder.build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        AbstractDataEvent<?> that = (AbstractDataEvent<?>) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AbstractDataEvent{" +
                "specVersion=" + specVersion +
                ", id='" + id + '\'' +
                ", source=" + source +
                ", type='" + type + '\'' +
                ", time=" + time +
                ", subject='" + subject + '\'' +
                ", dataContentType='" + dataContentType + '\'' +
                ", dataSchema=" + dataSchema +
                ", data=" + data +
                ", kogitoProcessInstanceId='" + kogitoProcessInstanceId + '\'' +
                ", kogitoRootProcessInstanceId='" + kogitoRootProcessInstanceId + '\'' +
                ", kogitoProcessId='" + kogitoProcessId + '\'' +
                ", kogitoRootProcessId='" + kogitoRootProcessId + '\'' +
                ", kogitoAddons='" + kogitoAddons + '\'' +
                ", extensionAttributes=" + extensionAttributes +
                '}';
    }
}
